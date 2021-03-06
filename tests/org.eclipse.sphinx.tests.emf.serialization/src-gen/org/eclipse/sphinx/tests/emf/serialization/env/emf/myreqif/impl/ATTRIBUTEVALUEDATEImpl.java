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

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ATTRIBUTEVALUEDATE;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.DEFINITIONType1;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>ATTRIBUTEVALUEDATE</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.ATTRIBUTEVALUEDATEImpl#getDEFINITION <em>
 * DEFINITION</em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.ATTRIBUTEVALUEDATEImpl#getTHEVALUE <em>THEVALUE
 * </em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ATTRIBUTEVALUEDATEImpl extends EObjectImpl implements ATTRIBUTEVALUEDATE {
	/**
	 * The cached value of the '{@link #getDEFINITION() <em>DEFINITION</em>}' containment reference. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see #getDEFINITION()
	 * @generated
	 * @ordered
	 */
	protected DEFINITIONType1 dEFINITION;

	/**
	 * The default value of the '{@link #getTHEVALUE() <em>THEVALUE</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTHEVALUE()
	 * @generated
	 * @ordered
	 */
	protected static final XMLGregorianCalendar THEVALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTHEVALUE() <em>THEVALUE</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTHEVALUE()
	 * @generated
	 * @ordered
	 */
	protected XMLGregorianCalendar tHEVALUE = THEVALUE_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ATTRIBUTEVALUEDATEImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MyreqifPackage.Literals.ATTRIBUTEVALUEDATE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public DEFINITIONType1 getDEFINITION() {
		return dEFINITION;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetDEFINITION(DEFINITIONType1 newDEFINITION, NotificationChain msgs) {
		DEFINITIONType1 oldDEFINITION = dEFINITION;
		dEFINITION = newDEFINITION;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MyreqifPackage.ATTRIBUTEVALUEDATE__DEFINITION,
					oldDEFINITION, newDEFINITION);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setDEFINITION(DEFINITIONType1 newDEFINITION) {
		if (newDEFINITION != dEFINITION) {
			NotificationChain msgs = null;
			if (dEFINITION != null) {
				msgs = ((InternalEObject) dEFINITION).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MyreqifPackage.ATTRIBUTEVALUEDATE__DEFINITION,
						null, msgs);
			}
			if (newDEFINITION != null) {
				msgs = ((InternalEObject) newDEFINITION).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MyreqifPackage.ATTRIBUTEVALUEDATE__DEFINITION,
						null, msgs);
			}
			msgs = basicSetDEFINITION(newDEFINITION, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, MyreqifPackage.ATTRIBUTEVALUEDATE__DEFINITION, newDEFINITION, newDEFINITION));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public XMLGregorianCalendar getTHEVALUE() {
		return tHEVALUE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setTHEVALUE(XMLGregorianCalendar newTHEVALUE) {
		XMLGregorianCalendar oldTHEVALUE = tHEVALUE;
		tHEVALUE = newTHEVALUE;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, MyreqifPackage.ATTRIBUTEVALUEDATE__THEVALUE, oldTHEVALUE, tHEVALUE));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case MyreqifPackage.ATTRIBUTEVALUEDATE__DEFINITION:
			return basicSetDEFINITION(null, msgs);
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
		case MyreqifPackage.ATTRIBUTEVALUEDATE__DEFINITION:
			return getDEFINITION();
		case MyreqifPackage.ATTRIBUTEVALUEDATE__THEVALUE:
			return getTHEVALUE();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case MyreqifPackage.ATTRIBUTEVALUEDATE__DEFINITION:
			setDEFINITION((DEFINITIONType1) newValue);
			return;
		case MyreqifPackage.ATTRIBUTEVALUEDATE__THEVALUE:
			setTHEVALUE((XMLGregorianCalendar) newValue);
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
		case MyreqifPackage.ATTRIBUTEVALUEDATE__DEFINITION:
			setDEFINITION((DEFINITIONType1) null);
			return;
		case MyreqifPackage.ATTRIBUTEVALUEDATE__THEVALUE:
			setTHEVALUE(THEVALUE_EDEFAULT);
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
		case MyreqifPackage.ATTRIBUTEVALUEDATE__DEFINITION:
			return dEFINITION != null;
		case MyreqifPackage.ATTRIBUTEVALUEDATE__THEVALUE:
			return THEVALUE_EDEFAULT == null ? tHEVALUE != null : !THEVALUE_EDEFAULT.equals(tHEVALUE);
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
		result.append(" (tHEVALUE: ");
		result.append(tHEVALUE);
		result.append(')');
		return result.toString();
	}

} // ATTRIBUTEVALUEDATEImpl
