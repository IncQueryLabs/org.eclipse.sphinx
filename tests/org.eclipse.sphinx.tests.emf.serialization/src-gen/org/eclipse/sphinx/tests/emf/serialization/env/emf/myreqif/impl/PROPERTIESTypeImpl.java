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
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.EMBEDDEDVALUE;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.PROPERTIESType;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>PROPERTIES Type</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.PROPERTIESTypeImpl#getEMBEDDEDVALUE <em>
 * EMBEDDEDVALUE</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PROPERTIESTypeImpl extends EObjectImpl implements PROPERTIESType {
	/**
	 * The cached value of the '{@link #getEMBEDDEDVALUE() <em>EMBEDDEDVALUE</em>}' containment reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getEMBEDDEDVALUE()
	 * @generated
	 * @ordered
	 */
	protected EMBEDDEDVALUE eMBEDDEDVALUE;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected PROPERTIESTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MyreqifPackage.Literals.PROPERTIES_TYPE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EMBEDDEDVALUE getEMBEDDEDVALUE() {
		return eMBEDDEDVALUE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetEMBEDDEDVALUE(EMBEDDEDVALUE newEMBEDDEDVALUE, NotificationChain msgs) {
		EMBEDDEDVALUE oldEMBEDDEDVALUE = eMBEDDEDVALUE;
		eMBEDDEDVALUE = newEMBEDDEDVALUE;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MyreqifPackage.PROPERTIES_TYPE__EMBEDDEDVALUE,
					oldEMBEDDEDVALUE, newEMBEDDEDVALUE);
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
	public void setEMBEDDEDVALUE(EMBEDDEDVALUE newEMBEDDEDVALUE) {
		if (newEMBEDDEDVALUE != eMBEDDEDVALUE) {
			NotificationChain msgs = null;
			if (eMBEDDEDVALUE != null) {
				msgs = ((InternalEObject) eMBEDDEDVALUE).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MyreqifPackage.PROPERTIES_TYPE__EMBEDDEDVALUE,
						null, msgs);
			}
			if (newEMBEDDEDVALUE != null) {
				msgs = ((InternalEObject) newEMBEDDEDVALUE).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MyreqifPackage.PROPERTIES_TYPE__EMBEDDEDVALUE,
						null, msgs);
			}
			msgs = basicSetEMBEDDEDVALUE(newEMBEDDEDVALUE, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, MyreqifPackage.PROPERTIES_TYPE__EMBEDDEDVALUE, newEMBEDDEDVALUE, newEMBEDDEDVALUE));
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
		case MyreqifPackage.PROPERTIES_TYPE__EMBEDDEDVALUE:
			return basicSetEMBEDDEDVALUE(null, msgs);
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
		case MyreqifPackage.PROPERTIES_TYPE__EMBEDDEDVALUE:
			return getEMBEDDEDVALUE();
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
		case MyreqifPackage.PROPERTIES_TYPE__EMBEDDEDVALUE:
			setEMBEDDEDVALUE((EMBEDDEDVALUE) newValue);
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
		case MyreqifPackage.PROPERTIES_TYPE__EMBEDDEDVALUE:
			setEMBEDDEDVALUE((EMBEDDEDVALUE) null);
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
		case MyreqifPackage.PROPERTIES_TYPE__EMBEDDEDVALUE:
			return eMBEDDEDVALUE != null;
		}
		return super.eIsSet(featureID);
	}

} // PROPERTIESTypeImpl
