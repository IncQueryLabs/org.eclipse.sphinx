/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.xmi.XMIException;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.internal.messages.Messages;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory;
import org.eclipse.sphinx.emf.resource.IXMLMarker;
import org.eclipse.sphinx.emf.resource.ProxyURIIntegrityException;
import org.eclipse.sphinx.emf.resource.XMLIntegrityException;
import org.eclipse.sphinx.emf.resource.XMLValidityException;
import org.eclipse.sphinx.emf.resource.XMLWellformednessException;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.messages.PlatformMessages;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.xml.sax.SAXParseException;

/**
 * Provides methods for analyzing {@link Resource#getErrors() errors} and {@link Resource#getWarnings() warnings} of
 * {@link Resource} resources and creating corresponding problem markers on underlying {@link IFile file}s.
 * 
 * @see IXMLMarker#XML_WELLFORMEDNESS_PROBLEM
 * @see IXMLMarker#XML_VALIDITY_PROBLEM
 * @see IXMLMarker#XML_INTEGRITY_PROBLEM
 * @see #PROXY_URI_INTEGRITY_PROBLEM
 * @see IMarker#PROBLEM
 */
public class ResourceProblemMarkerService {

	/**
	 * Proxy URI integrity problem marker type.
	 * 
	 * @see IMarker#getType()
	 */
	public static final String PROXY_URI_INTEGRITY_PROBLEM = Activator.getPlugin().getSymbolicName() + ".proxyuriintegrityproblemmarker"; //$NON-NLS-1$

	/**
	 * Singleton instance.
	 */
	public static ResourceProblemMarkerService INSTANCE = new ResourceProblemMarkerService();

	// Private default constructor for singleton pattern
	private ResourceProblemMarkerService() {
	}

	/**
	 * Creates problem markers for {@link IFile}s in given map based on the {@link Exception exception} which is
	 * associated with each of them.
	 * <p>
	 * The type of the problem marker being created depends on the type of exception associated with the {@link IFile
	 * file} and can be one of the following:
	 * </p>
	 * <table>
	 * <tr align="left">
	 * <th>{@link Exception Exception type}</th>
	 * <th>{@link Exception#getCause() Exception cause}</th>
	 * <th>Problem marker type</th>
	 * </tr>
	 * <tr>
	 * <td>{@link XMLIntegrityException}</td>
	 * <td>any</td>
	 * <td>{@link IXMLMarker#XML_INTEGRITY_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link ProxyURIIntegrityException}</td>
	 * <td>any</td>
	 * <td>{@link #PROXY_URI_INTEGRITY_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link XMIException}</td>
	 * <td>{@link XMLWellformednessException}</td>
	 * <td>{@link IXMLMarker#XML_WELLFORMEDNESS_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link XMIException}</td>
	 * <td>{@link XMLValidityException}</td>
	 * <td>{@link IXMLMarker#XML_VALIDITY_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link XMIException}</td>
	 * <td>any other</td>
	 * <td>{@link IMarker#PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>any other</td>
	 * <td>any</td>
	 * <td>{@link IMarker#PROBLEM}</td>
	 * </tr>
	 * </table>
	 * 
	 * @param filesWithErrors
	 *            Map of {@link IFile file}s to create problem markers for and {@link Exception exceptions} to be used
	 *            as basis for that.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 * @see IXMLMarker#XML_WELLFORMEDNESS_PROBLEM
	 * @see IXMLMarker#XML_VALIDITY_PROBLEM
	 * @see IXMLMarker#XML_INTEGRITY_PROBLEM
	 * @see #PROXY_URI_INTEGRITY_PROBLEM
	 * @see IMarker#PROBLEM
	 */
	public void updateProblemMarkers(final Map<IFile, Exception> filesWithErrors, boolean async, final IProgressMonitor monitor) {
		Assert.isNotNull(filesWithErrors);

		if (!filesWithErrors.isEmpty() && Platform.isRunning()) {
			if (async) {
				Job job = new WorkspaceJob(PlatformMessages.job_updatingProblemMarkers) {
					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor) {
						try {
							runUpdateProblemMarkers(filesWithErrors, monitor);
							return Status.OK_STATUS;
						} catch (OperationCanceledException ex) {
							return Status.CANCEL_STATUS;
						} catch (Exception ex) {
							return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
						}
					}

					@Override
					public boolean belongsTo(Object family) {
						return IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
					}
				};

				/*
				 * !! Important Note !! Updating problem marker jobs must be scheduled on workspace root to avoid that
				 * multiple of them run in parallel. Otherwise they would cause deadlocks even though the files which
				 * they are acting upon are different. The reason is that the IFile#createMarker() and
				 * IMarker#setAttributes() methods being called by the updating problem marker jobs try to start an
				 * exclusive workspace operation which is scheduled upon the marker rule of each underlying file. But
				 * for some reason which we don't understand marker rules of file are always null... (see
				 * org.eclipse.core.internal.resources.Rules#markerRule for details)
				 */
				job.setPriority(Job.BUILD);
				job.setRule(ResourcesPlugin.getWorkspace().getRoot());
				job.setSystem(true);
				job.schedule();
			} else {
				try {
					runUpdateProblemMarkers(filesWithErrors, monitor);
				} catch (OperationCanceledException ex) {
					// Ignore exception
				}
			}
		}
	}

	protected void runUpdateProblemMarkers(Map<IFile, Exception> filesWithErrors, IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(filesWithErrors);
		SubMonitor progress = SubMonitor.convert(monitor, filesWithErrors.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (IFile file : filesWithErrors.keySet()) {
			try {
				// Remove old problem makers
				if (file.isAccessible()) {
					file.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
					file.deleteMarkers(IXMLMarker.XML_WELLFORMEDNESS_PROBLEM, false, IResource.DEPTH_ZERO);
					file.deleteMarkers(IXMLMarker.XML_VALIDITY_PROBLEM, false, IResource.DEPTH_ZERO);
					file.deleteMarkers(IXMLMarker.XML_INTEGRITY_PROBLEM, false, IResource.DEPTH_ZERO);
				}

				Exception error = filesWithErrors.get(file);
				if (error instanceof XMIException) {
					createProblemMarkerForDiagnostic(file, null, (XMIException) error, IMarker.SEVERITY_ERROR);
				} else {
					createProblemMarkerForException(file, error, IMarker.SEVERITY_ERROR);
				}
			} catch (CoreException ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	/**
	 * Analyzes {@link Resource#getErrors() errors} and {@link Resource#getWarnings() warnings} of given
	 * {@link Resource resource} and creates corresponding problem markers on underlying {@link IFile file}.
	 * <p>
	 * The type of the problem marker being created depends on the type of {@link Resource#getErrors() error} or
	 * {@link Resource#getWarnings() warning} of given {@link Resource resource} and can be one of the following:
	 * </p>
	 * <table>
	 * <tr align="left">
	 * <th>{@link Diagnostic Diagnostic type}</th>
	 * <th>{@link Exception#getCause() Exception cause} (in case that {@link Diagnostic diagnostic} is an
	 * {@link Exception exception})</th>
	 * <th>Problem marker type</th>
	 * </tr>
	 * <tr>
	 * <td>{@link XMLIntegrityException}</td>
	 * <td>any</td>
	 * <td>{@link IXMLMarker#XML_INTEGRITY_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link ProxyURIIntegrityException}</td>
	 * <td>any</td>
	 * <td>{@link #PROXY_URI_INTEGRITY_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link XMIException}</td>
	 * <td>{@link XMLWellformednessException}</td>
	 * <td>{@link IXMLMarker#XML_WELLFORMEDNESS_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link XMIException}</td>
	 * <td>{@link XMLValidityException}</td>
	 * <td>{@link IXMLMarker#XML_VALIDITY_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link XMIException}</td>
	 * <td>any other</td>
	 * <td>{@link IMarker#PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>any other</td>
	 * <td>any</td>
	 * <td>{@link IMarker#PROBLEM}</td>
	 * </tr>
	 * </table>
	 * 
	 * @param resource
	 *            {@link Resource} whose {@link Resource#getErrors() errors} and {@link Resource#getWarnings() warnings}
	 *            are to be analyzed and converted into corresponding problem markers on underlying {@link IFile file}.
	 * @see IXMLMarker#XML_WELLFORMEDNESS_PROBLEM
	 * @see IXMLMarker#XML_VALIDITY_PROBLEM
	 * @see IXMLMarker#XML_INTEGRITY_PROBLEM
	 * @see #PROXY_URI_INTEGRITY_PROBLEM
	 * @see IMarker#PROBLEM
	 */
	public void updateProblemMarkers(Resource resource, boolean async, final IProgressMonitor monitor) {
		if (resource != null) {
			updateProblemMarkers(Collections.singleton(resource), async, monitor);
		}
	}

	/**
	 * Analyzes {@link Resource#getErrors() errors} and {@link Resource#getWarnings() warnings} of given collection of
	 * {@link Resource resource}s and creates corresponding problem markers on underlying {@link IFile file}s.
	 * <p>
	 * The type of the problem marker being created depends on the type of {@link Resource#getErrors() error} or
	 * {@link Resource#getWarnings() warning} of given {@link Resource resource} and can be one of the following:
	 * </p>
	 * <table>
	 * <tr align="left">
	 * <th>{@link Diagnostic Diagnostic type}</th>
	 * <th>{@link Exception#getCause() Exception cause} (in case that {@link Diagnostic diagnostic} is an
	 * {@link Exception exception})</th>
	 * <th>Problem marker type</th>
	 * </tr>
	 * <tr>
	 * <td>{@link XMLIntegrityException}</td>
	 * <td>any</td>
	 * <td>{@link IXMLMarker#XML_INTEGRITY_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link ProxyURIIntegrityException}</td>
	 * <td>any</td>
	 * <td>{@link #PROXY_URI_INTEGRITY_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link XMIException}</td>
	 * <td>{@link XMLWellformednessException}</td>
	 * <td>{@link IXMLMarker#XML_WELLFORMEDNESS_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link XMIException}</td>
	 * <td>{@link XMLValidityException}</td>
	 * <td>{@link IXMLMarker#XML_VALIDITY_PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link XMIException}</td>
	 * <td>any other</td>
	 * <td>{@link IMarker#PROBLEM}</td>
	 * </tr>
	 * <tr>
	 * <td>any other</td>
	 * <td>any</td>
	 * <td>{@link IMarker#PROBLEM}</td>
	 * </tr>
	 * </table>
	 * 
	 * @param resources
	 *            Collection of {@link Resource}s whose {@link Resource#getErrors() errors} and
	 *            {@link Resource#getWarnings() warnings} are to be analyzed and converted into corresponding problem
	 *            markers on underlying {@link IFile file}s.
	 * @param async
	 *            <code>true</code> if this operation is required to be run asynchronously, or <code>false</code> if
	 *            synchronous execution is desired.
	 * @param monitor
	 *            A {@link IProgressMonitor progress monitor}, or <code>null</code> if progress reporting is not
	 *            desired.
	 * @see IXMLMarker#XML_WELLFORMEDNESS_PROBLEM
	 * @see IXMLMarker#XML_VALIDITY_PROBLEM
	 * @see IXMLMarker#XML_INTEGRITY_PROBLEM
	 * @see #PROXY_URI_INTEGRITY_PROBLEM
	 * @see IMarker#PROBLEM
	 */
	public void updateProblemMarkers(final Collection<Resource> resources, boolean async, final IProgressMonitor monitor) {
		Assert.isNotNull(resources);

		if (!resources.isEmpty() && Platform.isRunning()) {
			if (async) {
				Job job = new WorkspaceJob(PlatformMessages.job_updatingProblemMarkers) {
					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor) {
						try {
							runUpdateProblemMarkers(resources, monitor);
							return Status.OK_STATUS;
						} catch (OperationCanceledException ex) {
							return Status.CANCEL_STATUS;
						} catch (Exception ex) {
							return StatusUtil.createErrorStatus(Activator.getPlugin(), ex);
						}
					}

					@Override
					public boolean belongsTo(Object family) {
						return IExtendedPlatformConstants.FAMILY_LONG_RUNNING.equals(family);
					}
				};

				/*
				 * !! Important Note !! Updating problem marker jobs must be scheduled on workspace root to avoid that
				 * multiple of them run in parallel. Otherwise they would cause deadlocks even though the files which
				 * they are acting upon are different. The reason is that the IFile#createMarker() and
				 * IMarker#setAttributes() methods being called by the updating problem marker jobs try to start an
				 * exclusive workspace operation which is scheduled upon the marker rule of each underlying file. But
				 * for some reason which we don't understand marker rules of file are always null... (see
				 * org.eclipse.core.internal.resources.Rules#markerRule for details)
				 */
				job.setPriority(Job.BUILD);
				job.setRule(ResourcesPlugin.getWorkspace().getRoot());
				job.setSystem(true);
				job.schedule();
			} else {
				try {
					runUpdateProblemMarkers(resources, monitor);
				} catch (OperationCanceledException ex) {
					// Ignore exception
				}
			}
		}
	}

	protected void runUpdateProblemMarkers(Collection<Resource> resources, IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(resources);

		// Collect resources to update problem markers for in sets of resources per editing domain; tolerate resources
		// that aren't in any editing domain
		final Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUpdate = new HashMap<TransactionalEditingDomain, Collection<Resource>>();
		for (Resource resource : resources) {
			TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resource);
			Collection<Resource> resourcesToUpdateInEditingDomain = resourcesToUpdate.get(editingDomain);
			if (resourcesToUpdateInEditingDomain == null) {
				resourcesToUpdateInEditingDomain = new HashSet<Resource>();
				resourcesToUpdate.put(editingDomain, resourcesToUpdateInEditingDomain);
			}
			resourcesToUpdateInEditingDomain.add(resource);
		}

		// Update problem markers of resources in each editing domain
		SubMonitor progress = SubMonitor.convert(monitor, resourcesToUpdate.keySet().size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (final TransactionalEditingDomain editingDomain : resourcesToUpdate.keySet()) {
			updateProblemMarkersInEditingDomain(editingDomain, resourcesToUpdate, progress.newChild(1));
		}
	}

	private void updateProblemMarkersInEditingDomain(final TransactionalEditingDomain editingDomain,
			final Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUpdate, final IProgressMonitor monitor) {
		Assert.isNotNull(resourcesToUpdate);

		Runnable runnable = new Runnable() {
			public void run() {
				Collection<Resource> resourcesToUpdateInEditingDomain = resourcesToUpdate.get(editingDomain);
				SubMonitor progress = SubMonitor.convert(monitor, resourcesToUpdateInEditingDomain.size());
				if (progress.isCanceled()) {
					throw new OperationCanceledException();
				}

				for (Resource resource : resourcesToUpdateInEditingDomain) {
					try {
						IFile file = EcorePlatformUtil.getFile(resource);
						if (file != null) {
							// Remove old problem makers related to resource loading and saving
							if (file.isAccessible()) {
								file.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
								file.deleteMarkers(IXMLMarker.XML_WELLFORMEDNESS_PROBLEM, false, IResource.DEPTH_ZERO);
								file.deleteMarkers(IXMLMarker.XML_VALIDITY_PROBLEM, false, IResource.DEPTH_ZERO);
								file.deleteMarkers(IXMLMarker.XML_INTEGRITY_PROBLEM, false, IResource.DEPTH_ZERO);
							}

							ExtendedResource extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(resource);
							int maxCount = extendedResource != null ? (Integer) extendedResource.getProblemHandlingOptions().get(
									ExtendedResource.OPTION_MAX_PROBLEM_MARKER_COUNT) : ExtendedResource.OPTION_MAX_PROBLEM_MARKER_COUNT_UNLIMITED;
							int count = 0;

							// Handle errors
							ArrayList<Diagnostic> safeErrors = new ArrayList<Diagnostic>(resource.getErrors());
							for (Iterator<Diagnostic> iter = safeErrors.iterator(); iter.hasNext() && count != maxCount; count++) {
								try {
									createProblemMarkerForDiagnostic(file, extendedResource, iter.next(), IMarker.SEVERITY_ERROR);
								} catch (Exception ex) {
									PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
								}
							}

							// Handle warnings
							ArrayList<Diagnostic> safeWarnings = new ArrayList<Diagnostic>(resource.getWarnings());
							for (Iterator<Diagnostic> iter = safeWarnings.iterator(); iter.hasNext() && count != maxCount; count++) {
								try {
									createProblemMarkerForDiagnostic(file, extendedResource, iter.next(), IMarker.SEVERITY_WARNING);
								} catch (Exception ex) {
									PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
								}
							}
						}
					} catch (CoreException ex) {
						PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
					}

					progress.worked(1);
					if (progress.isCanceled()) {
						throw new OperationCanceledException();
					}
				}
			}
		};

		if (editingDomain != null) {
			try {
				editingDomain.runExclusive(runnable);
			} catch (InterruptedException ex) {
				PlatformLogUtil.logAsError(Activator.getDefault(), ex);
			}
		} else {
			runnable.run();
		}
	}

	protected void createProblemMarkerForDiagnostic(IFile file, ExtendedResource extendedResource, Resource.Diagnostic diagnostic, int severity)
			throws CoreException {
		Assert.isNotNull(file);
		Assert.isNotNull(diagnostic);

		// Create marker attributes which are common for all problem types
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(IMarker.TRANSIENT, Boolean.TRUE);
		// Make sure that problem marker line is always 1 or greater because otherwise it would not be visible when
		// underlying resource is opened in a text editor
		int line = diagnostic.getLine();
		attributes.put(IMarker.LINE_NUMBER, line > 0 ? line : 1);
		attributes.put(IMarker.LOCATION, NLS.bind(Messages.attribute_line, line));
		attributes.put(IMarker.SEVERITY, severity);

		String type = IMarker.PROBLEM;

		// Handle XMI exceptions
		if (diagnostic instanceof XMIException) {
			XMIException xmiException = (XMIException) diagnostic;

			// Handle XML well-formedness exceptions
			if (xmiException.getCause() instanceof XMLWellformednessException) {
				String format = extendedResource != null ? (String) extendedResource.getProblemHandlingOptions().get(
						ExtendedResource.OPTION_XML_WELLFORMEDNESS_PROBLEM_FORMAT_STRING) : null;
				attributes.put(IMarker.MESSAGE, createProblemMarkerMessage(format, xmiException));

				type = IXMLMarker.XML_WELLFORMEDNESS_PROBLEM;
			}

			// Handle schema validation exceptions
			else if (xmiException.getCause() instanceof XMLValidityException || xmiException.getCause() instanceof SAXParseException
					&& xmiException.getMessage().contains("cvc-")) { //$NON-NLS-1$
				Integer resourceDefinedSeverity = extendedResource != null ? (Integer) extendedResource.getProblemHandlingOptions().get(
						ExtendedResource.OPTION_XML_VALIDITY_PROBLEM_SEVERITY) : null;
				if (resourceDefinedSeverity != null) {
					attributes.put(IMarker.SEVERITY, resourceDefinedSeverity);
				}

				String format = extendedResource != null ? (String) extendedResource.getProblemHandlingOptions().get(
						ExtendedResource.OPTION_XML_VALIDITY_PROBLEM_FORMAT_STRING) : null;
				attributes.put(IMarker.MESSAGE, createProblemMarkerMessage(format, xmiException));

				type = IXMLMarker.XML_VALIDITY_PROBLEM;
			}

			// Handle XML integrity exceptions
			else if (xmiException instanceof XMLIntegrityException) {
				attributes.put(IMarker.MESSAGE, createProblemMarkerMessage(xmiException));

				type = IXMLMarker.XML_INTEGRITY_PROBLEM;
			}

			// Handle proxy URI integrity exceptions
			else if (xmiException instanceof ProxyURIIntegrityException) {
				attributes.put(IMarker.MESSAGE, createProblemMarkerMessage(xmiException));

				type = PROXY_URI_INTEGRITY_PROBLEM;
			}

			// Handle other XMI exceptions
			else {
				attributes.put(IMarker.MESSAGE, createProblemMarkerMessage(xmiException));
			}
		} else {
			// Handle ordinary diagnostics
			attributes.put(IMarker.MESSAGE, diagnostic.getMessage());
		}

		// Does file exist?
		if (file.isAccessible()) {
			// Create new problem marker with previously calculated type and attributes
			IMarker marker = file.createMarker(type);
			marker.setAttributes(attributes);
		} else {
			// Use error log for errors of non-existing files or in-memory resources
			Integer effectiveSeverity = (Integer) attributes.get(IMarker.SEVERITY);
			String effectiveMessage = (String) attributes.get(IMarker.MESSAGE);
			if (effectiveSeverity == IMarker.SEVERITY_ERROR) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), effectiveMessage);
			} else if (effectiveSeverity == IMarker.SEVERITY_WARNING) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), effectiveMessage);
			} else if (effectiveSeverity == IMarker.SEVERITY_INFO) {
				PlatformLogUtil.logAsInfo(Activator.getPlugin(), effectiveMessage);
			}
		}
	}

	protected void createProblemMarkerForException(IFile file, Exception exception, int severity) throws CoreException {
		Assert.isNotNull(file);
		Assert.isNotNull(exception);

		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(IMarker.TRANSIENT, Boolean.TRUE);
		attributes.put(IMarker.LINE_NUMBER, 1);
		attributes.put(IMarker.LOCATION, NLS.bind(Messages.attribute_line, 1));
		attributes.put(IMarker.SEVERITY, severity);
		attributes.put(IMarker.MESSAGE, createProblemMarkerMessage(exception));

		String type = IMarker.PROBLEM;

		// Does file exist?
		if (file.isAccessible()) {
			// Create new problem marker with previously calculated type and attributes
			IMarker marker = file.createMarker(type);
			marker.setAttributes(attributes);
		} else {
			// Use error log for errors of non-existing files or in-memory resources
			Integer effectiveSeverity = (Integer) attributes.get(IMarker.SEVERITY);
			String effectiveMessage = (String) attributes.get(IMarker.MESSAGE);
			if (effectiveSeverity == IMarker.SEVERITY_ERROR) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), effectiveMessage);
			} else if (effectiveSeverity == IMarker.SEVERITY_WARNING) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), effectiveMessage);
			} else if (effectiveSeverity == IMarker.SEVERITY_INFO) {
				PlatformLogUtil.logAsInfo(Activator.getPlugin(), effectiveMessage);
			}
		}
	}

	protected String createProblemMarkerMessage(Exception exception) {
		Assert.isNotNull(exception);

		StringBuilder msg = new StringBuilder();
		msg.append(exception.getLocalizedMessage());
		Throwable cause = exception.getCause();
		if (cause != null) {
			String causeMsg = cause.getLocalizedMessage();
			if (causeMsg != null && causeMsg.length() > 0 && !msg.toString().contains(causeMsg)) {
				msg.append(": "); //$NON-NLS-1$
				msg.append(causeMsg);
			}
		}
		return msg.toString();
	}

	protected String createProblemMarkerMessage(String format, Exception exception) {
		Assert.isNotNull(exception);

		String msg = createProblemMarkerMessage(exception);
		if (format == null) {
			return msg;
		}

		if (format.contains("{0}")) { //$NON-NLS-1$
			return NLS.bind(format, msg);
		} else {
			if (!format.endsWith(" ")) { //$NON-NLS-1$
				format.concat(" "); //$NON-NLS-1$
			}
			return format.concat(msg);
		}
	}
}