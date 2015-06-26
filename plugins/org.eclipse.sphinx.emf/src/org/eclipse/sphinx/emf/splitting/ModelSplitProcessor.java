/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.splitting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.sphinx.emf.resource.ModelResourceDescriptor;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;

public class ModelSplitProcessor {

	protected static class AncestorCopier extends EcoreUtil.Copier {

		private static final long serialVersionUID = 1L;

		protected IModelSplitDirective modelSplitDirective;

		public AncestorCopier(IModelSplitDirective modelSplitDirective) {
			Assert.isNotNull(modelSplitDirective);
			this.modelSplitDirective = modelSplitDirective;
		}

		@Override
		public EObject copy(EObject ancestor) {
			if (ancestor == null) {
				return null;
			}

			EObject copiedAncestor = createCopy(ancestor);
			if (copiedAncestor != null) {
				put(ancestor, copiedAncestor);
				for (EStructuralFeature eStructuralFeature : ancestor.eClass().getEAllStructuralFeatures()) {
					if (eStructuralFeature.isChangeable() && !eStructuralFeature.isDerived()) {
						if (modelSplitDirective.shouldReplicateAncestorFeature(ancestor, eStructuralFeature)) {
							if (eStructuralFeature instanceof EAttribute) {
								EAttribute eAttribute = (EAttribute) eStructuralFeature;
								copyAttribute(eAttribute, ancestor, copiedAncestor);
							} else {
								EReference eReference = (EReference) eStructuralFeature;
								if (eReference.isContainment()) {
									copyContainment(eReference, ancestor, copiedAncestor);
								}
							}
						}
					}
				}

				copyProxyURI(ancestor, copiedAncestor);
			}
			return copiedAncestor;
		}

		@Override
		protected void copyFeatureMap(FeatureMap featureMap) {
			for (int i = 0, size = featureMap.size(); i < size; ++i) {
				EStructuralFeature feature = featureMap.getEStructuralFeature(i);
				if (feature instanceof EReference && ((EReference) feature).isContainment()) {
					if (modelSplitDirective.shouldReplicateAncestorFeature(((FeatureMap.Internal) featureMap).getEObject(), feature)) {
						Object value = featureMap.getValue(i);
						if (value != null) {
							copy((EObject) value);
						}
					}
				}
			}
		}
	}

	private IModelSplitPolicy modelSplitPolicy;

	private Map<EObject, Map<URI, EObject>> originalToSplitEObjectsMap = new HashMap<EObject, Map<URI, EObject>>();
	private Map<URI, List<EObject>> targetResourceURIToContentsMap = new HashMap<URI, List<EObject>>();

	public ModelSplitProcessor(IModelSplitPolicy modelSplitPolicy) {
		Assert.isNotNull(modelSplitPolicy);
		this.modelSplitPolicy = modelSplitPolicy;
	}

	protected EObject getSplitEObject(EObject originalEObject, URI targetResourceURI) {
		Map<URI, EObject> uriToSplitEObjectsMap = originalToSplitEObjectsMap.get(originalEObject);
		if (uriToSplitEObjectsMap != null) {
			return uriToSplitEObjectsMap.get(targetResourceURI);
		}
		return null;
	}

	protected void addSplitEObject(EObject originalEObject, EObject splitEObject, URI targetResourceURI) {
		Map<URI, EObject> splitEObjects = new HashMap<URI, EObject>();
		splitEObjects.put(targetResourceURI, splitEObject);
		originalToSplitEObjectsMap.put(originalEObject, splitEObjects);
	}

	protected List<EObject> getTargetResourceContents(URI targetResourceURI) {
		List<EObject> targetResourceContents = targetResourceURIToContentsMap.get(targetResourceURI);
		if (targetResourceContents == null) {
			targetResourceContents = new ArrayList<EObject>();
			targetResourceURIToContentsMap.put(targetResourceURI, targetResourceContents);
		}
		return targetResourceContents;
	}

	public void splitResources(Collection<Resource> resources, IProgressMonitor monitor) {
		Assert.isNotNull(resources);

		SubMonitor progress = SubMonitor.convert(monitor, resources.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (Resource resource : resources) {
			splitEObjects(resource.getContents(), progress.newChild(1));

			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	public void splitEObjects(Collection<EObject> eObjects, IProgressMonitor monitor) {
		Assert.isNotNull(eObjects);

		SubMonitor progress = SubMonitor.convert(monitor, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Traverse the model objects' contents, present each model object to split policy and collect resulting split
		// directives
		List<IModelSplitDirective> directives = collectSplitDirectives(eObjects, progress.newChild(25));

		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		// Process split directives and build split model content
		processSplitDirectives(directives, progress.newChild(75));
	}

	public Map<URI, List<EObject>> getSplitModelContents() {
		return Collections.unmodifiableMap(targetResourceURIToContentsMap);
	}

	public Collection<ModelResourceDescriptor> getSplitResourceDescriptors() {
		List<ModelResourceDescriptor> descriptors = new ArrayList<ModelResourceDescriptor>(targetResourceURIToContentsMap.keySet().size());
		for (URI uri : targetResourceURIToContentsMap.keySet()) {
			List<EObject> contents = targetResourceURIToContentsMap.get(uri);
			if (contents != null && !contents.isEmpty()) {
				IPath path = EcorePlatformUtil.createPath(uri);
				String contentTypeId = modelSplitPolicy.getContentTypeId(contents);
				descriptors.add(new ModelResourceDescriptor(contents, path, contentTypeId));
			}
		}
		return Collections.unmodifiableList(descriptors);
	}

	protected List<IModelSplitDirective> collectSplitDirectives(Collection<EObject> eObjects, IProgressMonitor monitor) {
		Assert.isNotNull(eObjects);

		SubMonitor progress = SubMonitor.convert(monitor, eObjects.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		List<IModelSplitDirective> directives = new ArrayList<IModelSplitDirective>();
		for (EObject eObject : eObjects) {
			for (TreeIterator<EObject> iterator = eObject.eAllContents(); iterator.hasNext();) {
				IModelSplitDirective directive = modelSplitPolicy.getSplitDirective(iterator.next());
				if (directive != null) {
					directives.add(directive);
					iterator.prune();
				}
			}
			progress.worked(1);

			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
		return directives;
	}

	protected void processSplitDirectives(List<IModelSplitDirective> directives, IProgressMonitor monitor) {
		Assert.isNotNull(directives);

		SubMonitor progress = SubMonitor.convert(monitor, directives.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (IModelSplitDirective directive : directives) {
			if (directive.isValid()) {
				EObject eObject = directive.getEObject();
				URI targetResourceURI = directive.getTargetResourceURI();

				// Has given model object already been split?
				if (getSplitEObject(eObject, targetResourceURI) != null) {
					return;
				}

				// Retrieve ancestor object branch
				List<EObject> ancestors = new ArrayList<EObject>();
				InternalEObject internalEObject = (InternalEObject) eObject;
				for (InternalEObject container = internalEObject.eInternalContainer(); container != null; container = internalEObject
						.eInternalContainer()) {
					ancestors.add(container);
					internalEObject = container;
				}
				EObject rootContainer = internalEObject;

				// Split given model object by just moving (rather than copying) original model object
				addSplitEObject(eObject, eObject, targetResourceURI);

				// Split ancestor object branch
				EObject lastEObject = eObject;
				EObject lastSplitEObject = eObject;
				for (EObject ancestor : ancestors) {
					EObject splitAncestor = null;

					// Split current ancestor if not already done so
					splitAncestor = getSplitEObject(ancestor, targetResourceURI);
					if (splitAncestor == null) {
						splitAncestor = copyAncestor(ancestor, directive);
						addSplitEObject(ancestor, splitAncestor, targetResourceURI);
					}

					// Connect split ancestor to previously split ancestor and model objects
					EStructuralFeature containingFeature = lastEObject.eContainingFeature();
					if (containingFeature == null) {
						throw new RuntimeException("Containing feature of '" + lastEObject + "' not found"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					if (containingFeature.isMany()) {
						@SuppressWarnings("unchecked")
						List<Object> values = (List<Object>) splitAncestor.eGet(containingFeature);
						values.add(lastSplitEObject);
					} else {
						splitAncestor.eSet(containingFeature, lastSplitEObject);
					}

					lastEObject = ancestor;
					lastSplitEObject = splitAncestor;
				}

				// Add split model object branch to target resource contents
				EObject splitRootContainer = getSplitEObject(rootContainer, targetResourceURI);
				getTargetResourceContents(targetResourceURI).add(splitRootContainer);
			}
			progress.worked(1);

			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	protected <T extends EObject> T copyAncestor(T ancestor, IModelSplitDirective directive) {
		AncestorCopier copier = new AncestorCopier(directive);
		EObject copiedAncestor = copier.copy(ancestor);
		copier.copyReferences();

		@SuppressWarnings("unchecked")
		T t = (T) copiedAncestor;
		return t;
	}

	public void dispose() {
		originalToSplitEObjectsMap.clear();
		targetResourceURIToContentsMap.clear();
	}
}
