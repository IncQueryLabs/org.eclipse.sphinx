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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;

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

	private List<IModelSplitDirective> modelSplitDirectives = new ArrayList<IModelSplitDirective>();

	private Map<EObject, Map<URI, EObject>> originalToSplitEObjectsMap = new HashMap<EObject, Map<URI, EObject>>();
	private Map<URI, List<EObject>> targetResourceURIToContentsMap = new HashMap<URI, List<EObject>>();

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

	public Map<URI, List<EObject>> getTargetResourceContents() {
		return Collections.unmodifiableMap(targetResourceURIToContentsMap);
	}

	public List<IModelSplitDirective> getModelSplitDirectives() {
		return modelSplitDirectives;
	}

	protected <T extends EObject> T copyAncestor(T ancestor, IModelSplitDirective directive) {
		AncestorCopier copier = new AncestorCopier(directive);
		EObject copiedAncestor = copier.copy(ancestor);
		copier.copyReferences();

		@SuppressWarnings("unchecked")
		T t = (T) copiedAncestor;
		return t;
	}

	public void run(IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor, modelSplitDirectives.size());
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (IModelSplitDirective directive : modelSplitDirectives) {
			if (directive.isValid()) {
				processSplitDirective(directive);
			}

			progress.worked(1);
			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}
	}

	protected void processSplitDirective(IModelSplitDirective directive) {
		Assert.isNotNull(directive);

		EObject eObject = directive.getEObject();
		URI targetResourceURI = directive.getTargetResourceURI();

		// Has given model object already been split?
		if (getSplitEObject(eObject, targetResourceURI) != null) {
			return;
		}

		// Retrieve ancestor object branch
		List<EObject> ancestors = new ArrayList<EObject>();
		InternalEObject internalEObject = (InternalEObject) eObject;
		for (InternalEObject container = internalEObject.eInternalContainer(); container != null; container = internalEObject.eInternalContainer()) {
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

	public void dispose() {
		originalToSplitEObjectsMap.clear();
		targetResourceURIToContentsMap.clear();
	}
}
