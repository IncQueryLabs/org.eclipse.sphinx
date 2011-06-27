/**
 * <copyright>
 *
 * Copyright (c) 2002-2007 IBM Corporation, See4sys and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *   See4sys - Copied from org.eclipse.emf.ecore.util.EcoreUtil.EqualityHelper
 *             to provide a set of equality assert methods for {@link EObject}s.
 * </copyright>
 */
package org.eclipse.sphinx.testutils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil.EqualityHelper;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

/**
 * This class is a copied and modified version of {@link EqualityHelper} and provides a set of equality assert methods
 * for {@link EObject}s. Messages are only displayed when an assert fails and indicate the {@link EObject}s which are
 * unequal and the reason for their inequality. Analyzes aside from that proxy states of given objects as considers them
 * as additional criterion in equality evaluation.
 * 
 * @see EqualityHelper
 */
@SuppressWarnings("nls")
public class EcoreEqualityAssert extends Assert {

	private static Map<EObject, EObject> mappedEObjects = new HashMap<EObject, EObject>();

	/**
	 * Asserts that <code>eObject1</code> and <code>eObject2</code> are equal.
	 * 
	 * @since 0.7.0
	 */
	public static void assertEquals(EObject eObject1, EObject eObject2) {
		try {
			internalAssertEquals(eObject1, eObject2);
		} finally {
			mappedEObjects.clear();
		}
	}

	protected static void internalAssertEquals(EObject eObject1, EObject eObject2) {
		// If the first object is null, the second object must be null.
		//
		if (eObject1 == null) {
			assertTrue("First object is null, but second isn't: " + eObject2 + ".", eObject2 == null);
			return;
		}

		// We know the first object isn't null, so if the second one is, it can't be equal.
		//
		assertFalse("First object isn't null: " + eObject1.toString() + ", but second is.", eObject2 == null);

		// Both eObject1 and eObject2 are not null.
		// If eObject1 has been compared already...
		//
		Object eObject1MappedValue = mappedEObjects.get(eObject1);
		if (eObject1MappedValue != null) {
			// Then eObject2 must be that previous match.
			//
			assertTrue(eObject1MappedValue == eObject2);
			return;
		}

		// If eObject2 has been compared already...
		//
		Object eObject2MappedValue = mappedEObjects.get(eObject2);
		if (eObject2MappedValue != null) {
			// Then eObject1 must be that match.
			//
			assertTrue(eObject2MappedValue == eObject1);
			return;
		}

		// If eObject1 and eObject2 are the same instance...
		//
		if (eObject1 == eObject2) {
			// Match them and return.
			//
			mappedEObjects.put(eObject1, eObject2);
			mappedEObjects.put(eObject2, eObject1);
			return;
		}

		// If the first object is a proxy, the second object must be a proxy.
		//
		if (eObject1.eIsProxy()) {
			assertTrue("First object is a proxy: " + eObject1.toString() + ", but second isn't: " + eObject2.toString() + ".", eObject2.eIsProxy());

			// Check the proxy URIs.
			//
			assertEquals(((InternalEObject) eObject1).eProxyURI(), ((InternalEObject) eObject2).eProxyURI());

			// Match them and return.
			//
			mappedEObjects.put(eObject1, eObject2);
			mappedEObjects.put(eObject2, eObject1);
			return;
		}

		// We know the first object isn't a proxy, so if the second one is, it can't be equal.
		//
		assertFalse("First object isn't a proxy: " + eObject1.toString() + ", but second is: " + eObject2.toString() + ".", eObject2.eIsProxy());

		// If they don't have the same class, they can't be equal.
		//
		EClass eClass = eObject1.eClass();
		assertTrue("Object " + eObject1.toString() + " and object " + eObject2.toString() + " don't have the same class: "
				+ eObject1.eClass().getName() + " <-> " + eObject2.eClass().getName() + ".", eClass == eObject2.eClass());

		// Assume from now on that they match.
		//
		mappedEObjects.put(eObject1, eObject2);
		mappedEObjects.put(eObject2, eObject1);

		// Check all the values.
		//
		for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i) {
			// Ignore derived features and XML serialization-specific features
			//
			EStructuralFeature feature = eClass.getEStructuralFeature(i);
			if (!feature.isDerived() && !isXMLSpecificFeature(feature)) {
				assertEqualFeature(eObject1, eObject2, feature);
			}
		}

		// There's no reason they aren't equal, so they are.
		//
		return;
	}

	/**
	 * Tests if given {@link EStructuralFeature feature} is dedicated to capturing XML serialization specific things and
	 * does not represent any information that would be relevant from a domain (meta-model) point of view.
	 * 
	 * @param feature
	 * @return <code>true</code> if given feature is XML serialization-specific, <code>false</code> otherwise.
	 * @since 0.7.0
	 */
	protected static boolean isXMLSpecificFeature(EStructuralFeature feature) {
		EClass eClass = feature.getEContainingClass();
		if (feature == ExtendedMetaData.INSTANCE.getXSISchemaLocationMapFeature(eClass)) {
			return true;
		}
		if (feature == ExtendedMetaData.INSTANCE.getXMLNSPrefixMapFeature(eClass)) {
			return true;
		}
		return false;
	}

	/**
	 * Asserts that <code>list1</code> and <code>list2</code> contain equal {@link EObject}s at the same index. It is
	 * assumed that list1 and list2 only contain EObjects.
	 * 
	 * @since 0.7.0
	 */
	public static void assertEquals(List<EObject> list1, List<EObject> list2) {
		try {
			internalAssertEquals(list1, list2);
		} finally {
			mappedEObjects.clear();
		}
	}

	protected static void internalAssertEquals(List<EObject> list1, List<EObject> list2) {
		int size = list1.size();
		assertTrue("List " + list2.toString() + " and list " + list2.toString() + " don't have same size: " + list1.size() + " <-> " + list2.size()
				+ ".", size == list2.size());

		for (int i = 0; i < size; i++) {
			EObject eObject1 = list1.get(i);
			EObject eObject2 = list2.get(i);
			// This is a debugging help for chasing down proxy resolution problems
			if (eObject2 != null && eObject2.eIsProxy()) {
				@SuppressWarnings("unused")
				EObject resolvedValue2 = ((InternalEObject) ((EcoreEList<?>) list2).getNotifier()).eResolveProxy((InternalEObject) eObject2);
			}
			internalAssertEquals(eObject1, eObject2);
		}

		return;
	}

	/**
	 * Asserts that the two objects have equal {@link EObject#eIsSet(EStructuralFeature) isSet} states and
	 * {@link EObject#eGet(EStructuralFeature) value}s for the feature.
	 * 
	 * @since 0.7.0
	 * @see #assertEquals(EObject, EObject)
	 * @see #assertEquals(List, List)
	 */
	protected static void assertEqualFeature(EObject eObject1, EObject eObject2, EStructuralFeature feature) {
		// If the set states are the same, and the values of the feature are the structurally equal, they are equal.
		//
		// assertTrue("Feature '" + feature.getName() + "' of object " + eObject1.toString() + " and object " +
		// eObject2.toString()
		// + " don't have same set state: " + eObject1.eIsSet(feature) + " <-> " + eObject2.eIsSet(feature) + ".",
		// eObject1.eIsSet(feature) == eObject2.eIsSet(feature));

		if (feature instanceof EReference) {
			assertEqualReference(eObject1, eObject2, (EReference) feature);
		} else {
			assertEqualAttribute(eObject1, eObject2, (EAttribute) feature);
		}
	}

	/**
	 * Asserts that the two objects have equal {@link EObject#eGet(EStructuralFeature) value}s for the reference.
	 * 
	 * @since 0.7.0
	 * @see #assertEquals(EObject, EObject)
	 * @see #assertEquals(List, List)
	 */
	@SuppressWarnings("unchecked")
	protected static void assertEqualReference(EObject eObject1, EObject eObject2, EReference reference) {
		Object value1 = eObject1.eGet(reference);
		Object value2 = eObject2.eGet(reference);

		if (reference.isMany()) {
			internalAssertEquals((List<EObject>) value1, (List<EObject>) value2);
		} else {
			// This is a debugging help for chasing down proxy resolution problems
			if (value2 != null && ((EObject) value2).eIsProxy()) {
				@SuppressWarnings("unused")
				EObject resolvedValue2 = ((InternalEObject) eObject2).eResolveProxy((InternalEObject) value2);
			}
			internalAssertEquals((EObject) value1, (EObject) value2);
		}
	}

	/**
	 * Asserts that the two objects have equal {@link EObject#eGet(EStructuralFeature) value}s for the attribute.
	 * 
	 * @since 0.7.0
	 * @see #equalFeatureMaps(FeatureMap, FeatureMap)
	 */
	protected static void assertEqualAttribute(EObject eObject1, EObject eObject2, EAttribute attribute) {
		Object value1 = eObject1.eGet(attribute);
		Object value2 = eObject2.eGet(attribute);

		// If the first value is null, the second value must be null.
		//
		if (value1 == null) {
			assertTrue("Value of attribute '" + attribute.getName() + "' on object " + eObject1.toString()
					+ " is null, but value of same attribute on object " + eObject2.toString() + " isn't: " + value2 + ".", value2 == null);
			return;
		}

		// Since the first value isn't null, if the second one is, they aren't equal.
		//
		assertFalse("Value of attribute '" + attribute.getName() + "' on object " + eObject1.toString() + " isn't null: " + value1.toString()
				+ ", but value of same attribute on object " + eObject2.toString() + " is.", value2 == null);

		// If this is a feature map...
		//
		if (FeatureMapUtil.isFeatureMap(attribute)) {
			// The feature maps must be equal.
			//
			FeatureMap featureMap1 = (FeatureMap) value1;
			FeatureMap featureMap2 = (FeatureMap) value2;
			assertEqualFeatureMaps(featureMap1, featureMap2);
		} else {
			// The values must be Java equal.
			//
			if (!"uuid".equals(attribute.getName())) {
				assertEquals("Value of attribute '" + attribute.getName() + "' on object " + eObject1.toString()
						+ " and that of same attribute on object " + eObject2.toString() + " are not equal, ", value1, value2);
			}
		}
	}

	/**
	 * Asserts that the two feature maps are equal.
	 * 
	 * @since 0.7.0
	 */
	protected static void assertEqualFeatureMaps(FeatureMap featureMap1, FeatureMap featureMap2) {
		// If they don't have the same size, the feature maps aren't equal.
		//
		int size = featureMap1.size();
		assertTrue(
				"Feature map " + featureMap1.toString() + " and feature map " + featureMap2.toString() + " don't have same size: "
						+ featureMap1.size() + " <-> " + featureMap2.size() + ".", size == featureMap2.size());

		// Compare entries in order.
		//
		for (int i = 0; i < size; i++) {
			// If entries don't have the same feature, the feature maps aren't equal.
			//
			EStructuralFeature feature = featureMap1.getEStructuralFeature(i);
			assertTrue("Feature at position " + i + " of feature map " + featureMap1.toString() + " and feature at same position of feature map "
					+ featureMap2.toString() + " are not the same: " + feature.getName() + " <-> " + featureMap2.getEStructuralFeature(i).getName()
					+ ".", feature == featureMap2.getEStructuralFeature(i));

			Object value1 = featureMap1.getValue(i);
			Object value2 = featureMap2.getValue(i);

			assertEqualFeatureMapValues(value1, value2, feature);
		}

		// There is no reason they aren't equals.
		//
		return;
	}

	/**
	 * Asserts that the two values of a feature map are equal.
	 * 
	 * @since 0.7.0
	 */
	protected static void assertEqualFeatureMapValues(Object value1, Object value2, EStructuralFeature feature) {
		if (feature instanceof EReference) {
			// If the referenced EObjects aren't equal, the feature maps aren't equal.
			//
			internalAssertEquals((EObject) value1, (EObject) value2);
		} else {
			// If the values aren't Java equal, the feature maps aren't equal.
			//
			assertTrue("Feature map values for attribute '" + feature.getName() + "' are unequal: " + value1.toString() + " <-> " + value2.toString()
					+ ".", value1 == null ? value2 == null : value1.equals(value2));
		}
	}
}