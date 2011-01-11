/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.tests.emf.metamodel.mocks;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

public class MockEPackage implements EPackage {

	private EFactory fEFactory;

	public EClassifier getEClassifier(String name) {
		return null;
	}

	public EList<EClassifier> getEClassifiers() {
		return null;
	}

	public EFactory getEFactoryInstance() {
		return fEFactory;
	}

	public EList<EPackage> getESubpackages() {
		return null;
	}

	public EPackage getESuperPackage() {
		return null;
	}

	public String getNsPrefix() {
		return null;
	}

	public String getNsURI() {
		return null;
	}

	public void setEFactoryInstance(EFactory eFactory) {
		fEFactory = eFactory;
	}

	public void setNsPrefix(String value) {
	}

	public void setNsURI(String value) {
	}

	public String getName() {
		return null;
	}

	public void setName(String value) {
	}

	public EAnnotation getEAnnotation(String source) {
		return null;
	}

	public EList<EAnnotation> getEAnnotations() {
		return null;
	}

	public TreeIterator<EObject> eAllContents() {
		return null;
	}

	public EClass eClass() {
		return null;
	}

	public EObject eContainer() {
		return null;
	}

	public EStructuralFeature eContainingFeature() {
		return null;
	}

	public EReference eContainmentFeature() {
		return null;
	}

	public EList<EObject> eContents() {
		return null;
	}

	public EList<EObject> eCrossReferences() {
		return null;
	}

	public Object eGet(EStructuralFeature feature) {
		return null;
	}

	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return null;
	}

	public boolean eIsProxy() {
		return false;
	}

	public boolean eIsSet(EStructuralFeature feature) {
		return false;
	}

	public Resource eResource() {
		return null;
	}

	public void eSet(EStructuralFeature feature, Object newValue) {
	}

	public void eUnset(EStructuralFeature feature) {
	}

	public EList<Adapter> eAdapters() {
		return null;
	}

	public boolean eDeliver() {
		return false;
	}

	public void eNotify(Notification notification) {
	}

	public void eSetDeliver(boolean deliver) {
	}

	public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException {
		return null;
	}
}
