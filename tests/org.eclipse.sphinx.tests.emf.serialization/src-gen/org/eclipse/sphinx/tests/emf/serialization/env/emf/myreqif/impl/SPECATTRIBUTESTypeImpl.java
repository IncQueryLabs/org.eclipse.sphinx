/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ATTRIBUTEDEFINITIONBOOLEAN;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ATTRIBUTEDEFINITIONDATE;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ATTRIBUTEDEFINITIONENUMERATION;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ATTRIBUTEDEFINITIONINTEGER;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ATTRIBUTEDEFINITIONREAL;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ATTRIBUTEDEFINITIONSTRING;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ATTRIBUTEDEFINITIONXHTML;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECATTRIBUTESType;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>SPECATTRIBUTES Type</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECATTRIBUTESTypeImpl#getGroup <em>Group</em>}</li>
 * <li>
 * {@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECATTRIBUTESTypeImpl#getATTRIBUTEDEFINITIONBOOLEAN
 * <em>ATTRIBUTEDEFINITIONBOOLEAN</em>}</li>
 * <li>
 * {@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECATTRIBUTESTypeImpl#getATTRIBUTEDEFINITIONDATE
 * <em>ATTRIBUTEDEFINITIONDATE</em>}</li>
 * <li>
 * {@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECATTRIBUTESTypeImpl#getATTRIBUTEDEFINITIONENUMERATION
 * <em>ATTRIBUTEDEFINITIONENUMERATION</em>}</li>
 * <li>
 * {@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECATTRIBUTESTypeImpl#getATTRIBUTEDEFINITIONINTEGER
 * <em>ATTRIBUTEDEFINITIONINTEGER</em>}</li>
 * <li>
 * {@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECATTRIBUTESTypeImpl#getATTRIBUTEDEFINITIONREAL
 * <em>ATTRIBUTEDEFINITIONREAL</em>}</li>
 * <li>
 * {@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECATTRIBUTESTypeImpl#getATTRIBUTEDEFINITIONSTRING
 * <em>ATTRIBUTEDEFINITIONSTRING</em>}</li>
 * <li>
 * {@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECATTRIBUTESTypeImpl#getATTRIBUTEDEFINITIONXHTML
 * <em>ATTRIBUTEDEFINITIONXHTML</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SPECATTRIBUTESTypeImpl extends EObjectImpl implements SPECATTRIBUTESType {
	/**
	 * The cached value of the '{@link #getGroup() <em>Group</em>}' attribute list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getGroup()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap group;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected SPECATTRIBUTESTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MyreqifPackage.Literals.SPECATTRIBUTES_TYPE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public FeatureMap getGroup() {
		if (group == null) {
			group = new BasicFeatureMap(this, MyreqifPackage.SPECATTRIBUTES_TYPE__GROUP);
		}
		return group;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<ATTRIBUTEDEFINITIONBOOLEAN> getATTRIBUTEDEFINITIONBOOLEAN() {
		return getGroup().list(MyreqifPackage.Literals.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONBOOLEAN);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<ATTRIBUTEDEFINITIONDATE> getATTRIBUTEDEFINITIONDATE() {
		return getGroup().list(MyreqifPackage.Literals.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONDATE);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<ATTRIBUTEDEFINITIONENUMERATION> getATTRIBUTEDEFINITIONENUMERATION() {
		return getGroup().list(MyreqifPackage.Literals.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONENUMERATION);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<ATTRIBUTEDEFINITIONINTEGER> getATTRIBUTEDEFINITIONINTEGER() {
		return getGroup().list(MyreqifPackage.Literals.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONINTEGER);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<ATTRIBUTEDEFINITIONREAL> getATTRIBUTEDEFINITIONREAL() {
		return getGroup().list(MyreqifPackage.Literals.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONREAL);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<ATTRIBUTEDEFINITIONSTRING> getATTRIBUTEDEFINITIONSTRING() {
		return getGroup().list(MyreqifPackage.Literals.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONSTRING);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<ATTRIBUTEDEFINITIONXHTML> getATTRIBUTEDEFINITIONXHTML() {
		return getGroup().list(MyreqifPackage.Literals.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONXHTML);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case MyreqifPackage.SPECATTRIBUTES_TYPE__GROUP:
			return ((InternalEList<?>) getGroup()).basicRemove(otherEnd, msgs);
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONBOOLEAN:
			return ((InternalEList<?>) getATTRIBUTEDEFINITIONBOOLEAN()).basicRemove(otherEnd, msgs);
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONDATE:
			return ((InternalEList<?>) getATTRIBUTEDEFINITIONDATE()).basicRemove(otherEnd, msgs);
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONENUMERATION:
			return ((InternalEList<?>) getATTRIBUTEDEFINITIONENUMERATION()).basicRemove(otherEnd, msgs);
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONINTEGER:
			return ((InternalEList<?>) getATTRIBUTEDEFINITIONINTEGER()).basicRemove(otherEnd, msgs);
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONREAL:
			return ((InternalEList<?>) getATTRIBUTEDEFINITIONREAL()).basicRemove(otherEnd, msgs);
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONSTRING:
			return ((InternalEList<?>) getATTRIBUTEDEFINITIONSTRING()).basicRemove(otherEnd, msgs);
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONXHTML:
			return ((InternalEList<?>) getATTRIBUTEDEFINITIONXHTML()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case MyreqifPackage.SPECATTRIBUTES_TYPE__GROUP:
			if (coreType) {
				return getGroup();
			}
			return ((FeatureMap.Internal) getGroup()).getWrapper();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONBOOLEAN:
			return getATTRIBUTEDEFINITIONBOOLEAN();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONDATE:
			return getATTRIBUTEDEFINITIONDATE();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONENUMERATION:
			return getATTRIBUTEDEFINITIONENUMERATION();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONINTEGER:
			return getATTRIBUTEDEFINITIONINTEGER();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONREAL:
			return getATTRIBUTEDEFINITIONREAL();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONSTRING:
			return getATTRIBUTEDEFINITIONSTRING();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONXHTML:
			return getATTRIBUTEDEFINITIONXHTML();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case MyreqifPackage.SPECATTRIBUTES_TYPE__GROUP:
			((FeatureMap.Internal) getGroup()).set(newValue);
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONBOOLEAN:
			getATTRIBUTEDEFINITIONBOOLEAN().clear();
			getATTRIBUTEDEFINITIONBOOLEAN().addAll((Collection<? extends ATTRIBUTEDEFINITIONBOOLEAN>) newValue);
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONDATE:
			getATTRIBUTEDEFINITIONDATE().clear();
			getATTRIBUTEDEFINITIONDATE().addAll((Collection<? extends ATTRIBUTEDEFINITIONDATE>) newValue);
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONENUMERATION:
			getATTRIBUTEDEFINITIONENUMERATION().clear();
			getATTRIBUTEDEFINITIONENUMERATION().addAll((Collection<? extends ATTRIBUTEDEFINITIONENUMERATION>) newValue);
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONINTEGER:
			getATTRIBUTEDEFINITIONINTEGER().clear();
			getATTRIBUTEDEFINITIONINTEGER().addAll((Collection<? extends ATTRIBUTEDEFINITIONINTEGER>) newValue);
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONREAL:
			getATTRIBUTEDEFINITIONREAL().clear();
			getATTRIBUTEDEFINITIONREAL().addAll((Collection<? extends ATTRIBUTEDEFINITIONREAL>) newValue);
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONSTRING:
			getATTRIBUTEDEFINITIONSTRING().clear();
			getATTRIBUTEDEFINITIONSTRING().addAll((Collection<? extends ATTRIBUTEDEFINITIONSTRING>) newValue);
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONXHTML:
			getATTRIBUTEDEFINITIONXHTML().clear();
			getATTRIBUTEDEFINITIONXHTML().addAll((Collection<? extends ATTRIBUTEDEFINITIONXHTML>) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case MyreqifPackage.SPECATTRIBUTES_TYPE__GROUP:
			getGroup().clear();
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONBOOLEAN:
			getATTRIBUTEDEFINITIONBOOLEAN().clear();
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONDATE:
			getATTRIBUTEDEFINITIONDATE().clear();
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONENUMERATION:
			getATTRIBUTEDEFINITIONENUMERATION().clear();
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONINTEGER:
			getATTRIBUTEDEFINITIONINTEGER().clear();
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONREAL:
			getATTRIBUTEDEFINITIONREAL().clear();
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONSTRING:
			getATTRIBUTEDEFINITIONSTRING().clear();
			return;
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONXHTML:
			getATTRIBUTEDEFINITIONXHTML().clear();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case MyreqifPackage.SPECATTRIBUTES_TYPE__GROUP:
			return group != null && !group.isEmpty();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONBOOLEAN:
			return !getATTRIBUTEDEFINITIONBOOLEAN().isEmpty();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONDATE:
			return !getATTRIBUTEDEFINITIONDATE().isEmpty();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONENUMERATION:
			return !getATTRIBUTEDEFINITIONENUMERATION().isEmpty();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONINTEGER:
			return !getATTRIBUTEDEFINITIONINTEGER().isEmpty();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONREAL:
			return !getATTRIBUTEDEFINITIONREAL().isEmpty();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONSTRING:
			return !getATTRIBUTEDEFINITIONSTRING().isEmpty();
		case MyreqifPackage.SPECATTRIBUTES_TYPE__ATTRIBUTEDEFINITIONXHTML:
			return !getATTRIBUTEDEFINITIONXHTML().isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (group: ");
		result.append(group);
		result.append(')');
		return result.toString();
	}

} // SPECATTRIBUTESTypeImpl
