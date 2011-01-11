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
package org.eclipse.sphinx.emf.ecore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;

public class DefaultEcoreTraversalHelper implements EcoreTraversalHelper {

	/**
	 * {@inheritDoc}
	 * <p>
	 * Applies resource filtering to include only reachable references that are in the same context as the given object.
	 */
	// TODO Change signature to Collection<EObject> getPossibleValuesForReference(EObject object, EReference
	// reference)
	public Collection<EObject> getReachableEObjects(EObject referenceSource, EReference reference) {
		LinkedList<EObject> itemQueue = new LinkedList<EObject>();
		Collection<EObject> visited = new HashSet<EObject>();
		Collection<EObject> result = new ArrayList<EObject>();
		Resource resource = referenceSource.eResource();
		if (resource != null) {
			Collection<Resource> contextResources = EcorePlatformUtil.getResourcesInModel(resource, true);
			if (!contextResources.isEmpty()) {
				for (Resource contextResource : contextResources) {
					for (EObject eObject : contextResource.getContents()) {
						collectReachableObjectsOfType(visited, itemQueue, result, eObject, reference.getEType());
					}
				}
			} else {
				for (EObject eObject : resource.getContents()) {
					collectReachableObjectsOfType(visited, itemQueue, result, eObject, reference.getEType());
				}
			}
		} else {
			collectReachableObjectsOfType(visited, itemQueue, result, EcoreUtil.getRootContainer(referenceSource), reference.getEType());
		}

		while (!itemQueue.isEmpty()) {
			EObject nextItem = itemQueue.removeFirst();
			collectReachableObjectsOfType(visited, itemQueue, result, nextItem, reference.getEType());
		}

		return result;
	}

	/**
	 * This will visit all reachable references from object except those in visited and add them to the queue. The queue
	 * is processed outside this recursive traversal to avoid stack overflows. It updates visited and adds to result any
	 * object with a meta object that indicates that it is a subtype of type.
	 */
	private void collectReachableObjectsOfType(Collection<EObject> visited, LinkedList<EObject> itemQueue, Collection<EObject> result,
			EObject object, EClassifier type) {

		// Try to perform efficient direct search for instances of frequently used types
		if (collectReachableObjectsOfTypeUnderObject(result, object, type)) {
			// Don't fill itemQueue in order to stop recursive traversal at current level
			return;
		}

		// Perform regular recursive traversal
		if (visited.add(object)) {
			if (type.isInstance(object)) {
				result.add(object);
			}
			for (EStructuralFeature feature : getFeaturesToTraverseFor(object, type)) {
				if (!feature.isDerived()) {
					if (feature instanceof EReference) {
						EReference eReference = (EReference) feature;
						if (eReference.isMany()) {
							@SuppressWarnings("unchecked")
							List<EObject> list = (List<EObject>) object.eGet(eReference);
							for (EObject eObject : list) {
								itemQueue.addLast(eObject);
							}
						} else {
							EObject eObject = (EObject) object.eGet(eReference);
							if (eObject != null) {
								itemQueue.addLast(eObject);
							}
						}
					} else if (FeatureMapUtil.isFeatureMap(feature)) {
						for (FeatureMap.Entry entry : (FeatureMap) object.eGet(feature)) {
							if (entry.getEStructuralFeature() instanceof EReference && entry.getValue() != null) {
								itemQueue.addLast((EObject) entry.getValue());
							}
						}
					}
				}
			}
		}
	}

	public boolean collectReachableObjectsOfTypeUnderObject(Collection<EObject> result, EObject object, EClassifier type) {
		return false;
	}

	public List<EStructuralFeature> getFeaturesToTraverseFor(EObject object, EClassifier type) {
		return getDefaultFeaturesToTraverseFor(object, type);
	}

	protected List<EStructuralFeature> getDefaultFeaturesToTraverseFor(EObject object, EClassifier type) {
		List<EStructuralFeature> features = new ArrayList<EStructuralFeature>();
		for (EReference reference : object.eClass().getEAllReferences()) {
			if (reference.isContainment()) {
				features.add(reference);
			}
		}
		if (type instanceof EDataType) {
			features.addAll(object.eClass().getEAllAttributes());
		}
		return features;
	}
}
