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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ALTERNATIVEID;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ALTERNATIVEIDType4;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>ALTERNATIVEID Type4</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.ALTERNATIVEIDType4Impl#getALTERNATIVEID <em>
 * ALTERNATIVEID</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ALTERNATIVEIDType4Impl extends EObjectImpl implements ALTERNATIVEIDType4 {
	/**
	 * The cached value of the '{@link #getALTERNATIVEID() <em>ALTERNATIVEID</em>}' containment reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getALTERNATIVEID()
	 * @generated
	 * @ordered
	 */
	protected ALTERNATIVEID aLTERNATIVEID;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ALTERNATIVEIDType4Impl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MyreqifPackage.Literals.ALTERNATIVEID_TYPE4;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ALTERNATIVEID getALTERNATIVEID() {
		return aLTERNATIVEID;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetALTERNATIVEID(ALTERNATIVEID newALTERNATIVEID, NotificationChain msgs) {
		ALTERNATIVEID oldALTERNATIVEID = aLTERNATIVEID;
		aLTERNATIVEID = newALTERNATIVEID;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MyreqifPackage.ALTERNATIVEID_TYPE4__ALTERNATIVEID,
					oldALTERNATIVEID, newALTERNATIVEID);
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
	public void setALTERNATIVEID(ALTERNATIVEID newALTERNATIVEID) {
		if (newALTERNATIVEID != aLTERNATIVEID) {
			NotificationChain msgs = null;
			if (aLTERNATIVEID != null) {
				msgs = ((InternalEObject) aLTERNATIVEID).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- MyreqifPackage.ALTERNATIVEID_TYPE4__ALTERNATIVEID, null, msgs);
			}
			if (newALTERNATIVEID != null) {
				msgs = ((InternalEObject) newALTERNATIVEID).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- MyreqifPackage.ALTERNATIVEID_TYPE4__ALTERNATIVEID, null, msgs);
			}
			msgs = basicSetALTERNATIVEID(newALTERNATIVEID, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, MyreqifPackage.ALTERNATIVEID_TYPE4__ALTERNATIVEID, newALTERNATIVEID,
					newALTERNATIVEID));
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
		case MyreqifPackage.ALTERNATIVEID_TYPE4__ALTERNATIVEID:
			return basicSetALTERNATIVEID(null, msgs);
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
		case MyreqifPackage.ALTERNATIVEID_TYPE4__ALTERNATIVEID:
			return getALTERNATIVEID();
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
		case MyreqifPackage.ALTERNATIVEID_TYPE4__ALTERNATIVEID:
			setALTERNATIVEID((ALTERNATIVEID) newValue);
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
		case MyreqifPackage.ALTERNATIVEID_TYPE4__ALTERNATIVEID:
			setALTERNATIVEID((ALTERNATIVEID) null);
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
		case MyreqifPackage.ALTERNATIVEID_TYPE4__ALTERNATIVEID:
			return aLTERNATIVEID != null;
		}
		return super.eIsSet(featureID);
	}

} // ALTERNATIVEIDType4Impl
