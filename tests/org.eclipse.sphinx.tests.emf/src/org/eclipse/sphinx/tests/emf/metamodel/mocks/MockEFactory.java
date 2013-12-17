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
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

public class MockEFactory implements EFactory {

	@Override
	public String convertToString(EDataType dataType, Object instanceValue) {
		return null;
	}

	@Override
	public EObject create(EClass class1) {
		return null;
	}

	@Override
	public Object createFromString(EDataType dataType, String literalValue) {
		return null;
	}

	@Override
	public EPackage getEPackage() {
		return null;
	}

	@Override
	public void setEPackage(EPackage value) {
	}

	@Override
	public EAnnotation getEAnnotation(String source) {
		return null;
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		return null;
	}

	@Override
	public TreeIterator<EObject> eAllContents() {
		return null;
	}

	@Override
	public EClass eClass() {
		return null;
	}

	@Override
	public EObject eContainer() {
		return null;
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		return null;
	}

	@Override
	public EReference eContainmentFeature() {
		return null;
	}

	@Override
	public EList<EObject> eContents() {
		return null;
	}

	@Override
	public EList<EObject> eCrossReferences() {
		return null;
	}

	@Override
	public Object eGet(EStructuralFeature feature) {
		return null;
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return null;
	}

	@Override
	public boolean eIsProxy() {
		return false;
	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		return false;
	}

	@Override
	public Resource eResource() {
		return null;
	}

	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
	}

	@Override
	public void eUnset(EStructuralFeature feature) {
	}

	@Override
	public EList<Adapter> eAdapters() {
		return null;
	}

	@Override
	public boolean eDeliver() {
		return false;
	}

	@Override
	public void eNotify(Notification notification) {

	}

	@Override
	public void eSetDeliver(boolean deliver) {
	}

	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException {
		return null;
	}
}
