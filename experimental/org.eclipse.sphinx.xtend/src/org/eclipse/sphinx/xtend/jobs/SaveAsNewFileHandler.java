/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.xtend.jobs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.resource.ModelResourceDescriptor;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.ReflectUtil;
import org.eclipse.sphinx.xtend.internal.Activator;

public class SaveAsNewFileHandler extends JobChangeAdapter {

	protected XtendJob getXtendJob(IJobChangeEvent event) {
		if (event != null) {
			Job job = event.getJob();
			if (job instanceof XtendJob) {
				return (XtendJob) job;
			}
		}
		return null;
	}

	@Override
	public void done(IJobChangeEvent event) {
		XtendJob xtendJob = getXtendJob(event);
		if (xtendJob != null) {
			handleResultObjects(xtendJob.getResultObjects());
		}
	}

	protected void handleResultObjects(Map<Object, Collection<?>> resultObjects) {
		// Create descriptors for resources in which to save result objects
		Set<ModelResourceDescriptor> allModelResourceDescriptors = new HashSet<ModelResourceDescriptor>();
		Set<IPath> allocatedResultPaths = new HashSet<IPath>();
		for (Object inputObject : resultObjects.keySet()) {
			for (Object resultObject : resultObjects.get(inputObject)) {
				if (resultObject instanceof EObject && shouldSave((EObject) resultObject)) {
					IPath resultPath = getUniqueResultPath(inputObject, ((EObject) resultObject), allocatedResultPaths);
					if (resultPath != null) {
						allocatedResultPaths.add(resultPath);

						String resultContentTypeId = getContentTypeIdFor(((EObject) resultObject));
						allModelResourceDescriptors.add(new ModelResourceDescriptor((EObject) resultObject, resultPath, resultContentTypeId));
					}
				}
			}
		}

		// Sort model resource descriptors according to editing domain which they should belong to
		Map<TransactionalEditingDomain, Collection<ModelResourceDescriptor>> modelResourceDescriptors = new HashMap<TransactionalEditingDomain, Collection<ModelResourceDescriptor>>();
		for (ModelResourceDescriptor modelResourceDescriptor : allModelResourceDescriptors) {
			IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(modelResourceDescriptor.getModelRoot());
			IFile modelFile = ResourcesPlugin.getWorkspace().getRoot().getFile(modelResourceDescriptor.getPath());
			TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(modelFile.getParent(), mmDescriptor);

			Collection<ModelResourceDescriptor> modelResourceDescriptorsForEditingDomain = modelResourceDescriptors.get(editingDomain);
			if (modelResourceDescriptorsForEditingDomain == null) {
				modelResourceDescriptorsForEditingDomain = new HashSet<ModelResourceDescriptor>();
				modelResourceDescriptors.put(editingDomain, modelResourceDescriptorsForEditingDomain);
			}
			modelResourceDescriptorsForEditingDomain.add(modelResourceDescriptor);
		}

		// Save model resources
		for (TransactionalEditingDomain editingDomain : modelResourceDescriptors.keySet()) {
			try {
				EcorePlatformUtil.saveNewModelResources(editingDomain, modelResourceDescriptors.get(editingDomain), true, null);
			} catch (Exception ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}
	}

	protected boolean shouldSave(EObject object) {
		Assert.isNotNull(object);

		// Return true if given object is not already contained in any resource
		return object.eResource() == null;
	}

	protected IPath getUniqueResultPath(Object inputObject, EObject resultObject, Set<IPath> allocatedResultPaths) {
		IPath candidateResultPath = getResultPath(inputObject, resultObject);
		if (candidateResultPath != null) {
			IPath resultContainerPath = candidateResultPath.removeLastSegments(1);
			String candidateResultFileName = candidateResultPath.lastSegment();

			// Make result path unique wrt existing files
			String resultFileName = ExtendedPlatform.createUniqueFileName(resultContainerPath, candidateResultFileName);

			// Make result path unique wrt other result paths that have already been allocated
			return ExtendedPlatform.createUniquePath(resultContainerPath.append(resultFileName), allocatedResultPaths);
		}
		return null;
	}

	protected IPath getResultPath(Object inputObject, EObject resultObject) {
		IFile inputFile = EcorePlatformUtil.getFile(inputObject);
		if (inputFile != null) {
			String resultFileExtension = getFileExtensionFor(resultObject);
			inputFile.getFullPath().removeFileExtension().addFileExtension(resultFileExtension);
		}
		return null;
	}

	protected String getFileExtensionFor(EObject object) {
		// Try to retrieve file extension supported by content type behind given EObject
		String contentTypeId = getContentTypeIdFor(object);
		if (contentTypeId != null) {
			Collection<String> possibleExtensions = ExtendedPlatform.getContentTypeFileExtensions(contentTypeId);
			if (!possibleExtensions.isEmpty()) {
				return possibleExtensions.iterator().next();
			}
		}

		// Return name of given EObject's EPackage as file extension
		return object.eClass().getEPackage().getName();
	}

	protected String getContentTypeIdFor(EObject object) {
		try {
			EPackage ePackage = object.eClass().getEPackage();
			Object contentTypeId = ReflectUtil.getFieldValue(ePackage, "eCONTENT_TYPE"); //$NON-NLS-1$;
			if (contentTypeId instanceof String) {
				return (String) contentTypeId;
			}
		} catch (Exception ex) {
			// Ignore exception
		}
		return null;
	}
}
