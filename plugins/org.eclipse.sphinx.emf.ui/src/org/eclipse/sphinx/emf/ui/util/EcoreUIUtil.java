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
package org.eclipse.sphinx.emf.ui.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.edit.provider.WrapperItemProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;

/**
 * 
 */
public class EcoreUIUtil {

	// Prevent from instantiation
	private EcoreUIUtil() {
	}

	/**
	 * Tests if given object represents an intermediate category node i.e. a non-modeled object.
	 */
	public static boolean isVirtualElement(Object object) {
		if (object instanceof WrapperItemProvider) {
			WrapperItemProvider provider = (WrapperItemProvider) object;
			if (provider.getOwner() instanceof WrapperItemProvider) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests if given {@link IStructuredSelection} selection contains at least one non-modeled object.
	 * 
	 * @param selection
	 * @return
	 */

	public static boolean hasVirtualElements(IStructuredSelection selection) {
		for (Object object : selection.toList()) {
			if (isVirtualElement(object)) {
				return true;
			}
		}
		return false;
	}

	public static void openWizardDialog(final IWizard wizard) throws OperationCanceledException, ExecutionException {
		openWizardDialog(wizard, null);
	}

	/**
	 * @param wizard
	 *            The wizard the dialog to open is supposed to work on.
	 * @param editingDomain
	 *            The transactional editing domain to use for the transaction.
	 * @throws OperationCanceledException
	 *             that is mandatory to force underlying operation to abort without commit if user clicks on the cancel
	 *             button.
	 * @throws ExecutionException
	 *             if an execution exception occurs.
	 */
	public static void openWizardDialog(final IWizard wizard, TransactionalEditingDomain editingDomain) throws OperationCanceledException,
			ExecutionException {
		Assert.isNotNull(wizard);

		final Runnable runnable = new Runnable() {
			public void run() {
				// Creates and opens the wizard dialog
				int result = new WizardDialog(ExtendedPlatformUI.getDisplay().getActiveShell(), wizard).open();

				if (result == Window.CANCEL) {
					// OperationCanceledException is mandatory to force underlying operation to abort without commit
					throw new OperationCanceledException(wizard.getWindowTitle());
				}
			}
		};

		if (editingDomain != null) {
			WorkspaceTransactionUtil.executeInWriteTransaction(editingDomain, runnable, wizard.getWindowTitle());
		} else {
			runnable.run();
		}
	}

	public static URIEditorInput createURIEditorInput(Object object) {
		URI uri = null;

		if (object instanceof URI) {
			uri = (URI) object;
		} else if (object instanceof Resource) {
			uri = ((Resource) object).getURI();
		} else if (object instanceof EObject) {
			if (!((EObject) object).eIsProxy()) {
				uri = EcoreUtil.getURI((EObject) object);
			}
		} else if (object instanceof IWrapperItemProvider) {
			Object unwrapped = AdapterFactoryEditingDomain.unwrap(object);
			return createURIEditorInput(unwrapped);
		} else if (object instanceof FeatureMap.Entry) {
			Object unwrapped = AdapterFactoryEditingDomain.unwrap(object);
			return createURIEditorInput(unwrapped);
		}

		if (uri != null) {
			return new URIEditorInput(uri);
		}
		return null;
	}

	public static IEditorDescriptor getDefaultEditor(Object object) {
		if (object instanceof EObject) {
			return getDefaultEditor(object.getClass());
		} else if (object instanceof IWrapperItemProvider) {
			Object unwrapped = AdapterFactoryEditingDomain.unwrap(object);
			return getDefaultEditor(unwrapped);
		} else if (object instanceof FeatureMap.Entry) {
			Object unwrapped = AdapterFactoryEditingDomain.unwrap(object);
			return getDefaultEditor(unwrapped);
		} else if (object instanceof Resource) {
			String fileName = ((Resource) object).getURI().lastSegment();
			return PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(fileName);
		}
		return null;
	}

	public static IEditorDescriptor getDefaultEditor(Class<?> type) {
		IEditorDescriptor descriptor = findDefaultEditorForType(type);
		if (descriptor == null) {
			descriptor = findDefaultEditorForSuperType(type);
		}
		return descriptor;
	}

	public static URI getURIFromEditorInput(IEditorInput editorInput) {
		if (editorInput instanceof URIEditorInput) {
			return ((URIEditorInput) editorInput).getURI();
		}
		if (editorInput != null) {
			IFile file = (IFile) editorInput.getAdapter(IFile.class);
			if (file != null) {
				return EcorePlatformUtil.createURI(file.getFullPath());
			}
		}
		return null;
	}

	/**
	 * Returns the file behind the editor input
	 * 
	 * @param editorInput
	 * @return as specified above
	 */
	public static IFile getFileFromEditorInput(IEditorInput editorInput) {
		if (editorInput instanceof URIEditorInput) {
			return EcorePlatformUtil.getFile(((URIEditorInput) editorInput).getURI());
		}
		if (editorInput != null) {
			return (IFile) editorInput.getAdapter(IFile.class);
		}
		return null;
	}

	private static IEditorDescriptor findDefaultEditorForSuperType(Class<?> objectType) {
		if (objectType != null) {
			// Try to find matching super class
			Set<Class<?>> superTypes = new HashSet<Class<?>>();
			if (!objectType.isInterface()) {
				Class<?> superClass = objectType.getSuperclass();
				if (superClass != null) {
					superTypes.add(superClass);
					IEditorDescriptor descriptor = findDefaultEditorForType(superClass);
					if (descriptor != null) {
						return descriptor;
					}
				}
			}

			// Try to find matching interface
			Class<?>[] interfaces = objectType.getInterfaces();
			for (Class<?> interfaze : interfaces) {
				superTypes.add(interfaze);
				IEditorDescriptor descriptor = findDefaultEditorForType(interfaze);
				if (descriptor != null) {
					return descriptor;
				}
			}

			// Try to find matching super type of super class and interfaces
			for (Class<?> superType : superTypes) {
				IEditorDescriptor descriptor = findDefaultEditorForSuperType(superType);
				if (descriptor != null) {
					return descriptor;
				}
			}
		}

		return null;
	}

	private static IEditorDescriptor findDefaultEditorForType(Class<?> objectType) {
		if (objectType != null) {
			// Try to find editor registered with qualified or simple object type name
			String dummyFileName = "*." + objectType.getName(); //$NON-NLS-1$
			IEditorDescriptor descriptor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(dummyFileName);
			if (descriptor != null) {
				if (!isInapplicableTextBasedEditor(objectType, descriptor)) {
					return descriptor;
				} else {
					// Try to find alternative editor
					for (IEditorDescriptor alternativeDescriptor : PlatformUI.getWorkbench().getEditorRegistry().getEditors(dummyFileName)) {
						if (alternativeDescriptor != descriptor && !isInapplicableTextBasedEditor(objectType, alternativeDescriptor)) {
							return alternativeDescriptor;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Avoid that model objects are opened in file-based editors. This may happen in case that file-based editors are
	 * registered upon file extensions which are equal to the simple class name of some model object (e.g. model object
	 * type = Library, file extension = *.library)
	 */
	// TODO This hard-coded kind of file editor exclusions could eventually be avoided by providing a separate extension
	// point for model editors or by handing in a list of inapplicable editor id patterns via the API
	private static boolean isInapplicableTextBasedEditor(Class<?> objectType, IEditorDescriptor editorDescriptor) {
		if (editorDescriptor.getId().startsWith("org.eclipse.ui") || editorDescriptor.getId().startsWith("org.eclipse.wst")) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		return false;
	}
}
