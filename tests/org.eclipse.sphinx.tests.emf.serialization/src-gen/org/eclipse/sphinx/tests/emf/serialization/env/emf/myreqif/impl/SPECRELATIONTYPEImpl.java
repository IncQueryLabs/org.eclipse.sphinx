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
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.ALTERNATIVEIDType14;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECATTRIBUTESType;
import org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECRELATIONTYPE;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>SPECRELATIONTYPE</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECRELATIONTYPEImpl#getALTERNATIVEID <em>
 * ALTERNATIVEID</em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECRELATIONTYPEImpl#getSPECATTRIBUTES <em>
 * SPECATTRIBUTES</em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECRELATIONTYPEImpl#getDESC <em>DESC</em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECRELATIONTYPEImpl#getIDENTIFIER <em>IDENTIFIER
 * </em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECRELATIONTYPEImpl#getLASTCHANGE <em>LASTCHANGE
 * </em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.impl.SPECRELATIONTYPEImpl#getLONGNAME <em>LONGNAME
 * </em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SPECRELATIONTYPEImpl extends EObjectImpl implements SPECRELATIONTYPE {
	/**
	 * The cached value of the '{@link #getALTERNATIVEID() <em>ALTERNATIVEID</em>}' containment reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getALTERNATIVEID()
	 * @generated
	 * @ordered
	 */
	protected ALTERNATIVEIDType14 aLTERNATIVEID;

	/**
	 * The cached value of the '{@link #getSPECATTRIBUTES() <em>SPECATTRIBUTES</em>}' containment reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSPECATTRIBUTES()
	 * @generated
	 * @ordered
	 */
	protected SPECATTRIBUTESType sPECATTRIBUTES;

	/**
	 * The default value of the '{@link #getDESC() <em>DESC</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getDESC()
	 * @generated
	 * @ordered
	 */
	protected static final String DESC_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDESC() <em>DESC</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getDESC()
	 * @generated
	 * @ordered
	 */
	protected String dESC = DESC_EDEFAULT;

	/**
	 * The default value of the '{@link #getIDENTIFIER() <em>IDENTIFIER</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getIDENTIFIER()
	 * @generated
	 * @ordered
	 */
	protected static final String IDENTIFIER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIDENTIFIER() <em>IDENTIFIER</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getIDENTIFIER()
	 * @generated
	 * @ordered
	 */
	protected String iDENTIFIER = IDENTIFIER_EDEFAULT;

	/**
	 * The default value of the '{@link #getLASTCHANGE() <em>LASTCHANGE</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getLASTCHANGE()
	 * @generated
	 * @ordered
	 */
	protected static final XMLGregorianCalendar LASTCHANGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLASTCHANGE() <em>LASTCHANGE</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getLASTCHANGE()
	 * @generated
	 * @ordered
	 */
	protected XMLGregorianCalendar lASTCHANGE = LASTCHANGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getLONGNAME() <em>LONGNAME</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getLONGNAME()
	 * @generated
	 * @ordered
	 */
	protected static final String LONGNAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLONGNAME() <em>LONGNAME</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getLONGNAME()
	 * @generated
	 * @ordered
	 */
	protected String lONGNAME = LONGNAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected SPECRELATIONTYPEImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MyreqifPackage.Literals.SPECRELATIONTYPE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ALTERNATIVEIDType14 getALTERNATIVEID() {
		return aLTERNATIVEID;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetALTERNATIVEID(ALTERNATIVEIDType14 newALTERNATIVEID, NotificationChain msgs) {
		ALTERNATIVEIDType14 oldALTERNATIVEID = aLTERNATIVEID;
		aLTERNATIVEID = newALTERNATIVEID;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MyreqifPackage.SPECRELATIONTYPE__ALTERNATIVEID,
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
	public void setALTERNATIVEID(ALTERNATIVEIDType14 newALTERNATIVEID) {
		if (newALTERNATIVEID != aLTERNATIVEID) {
			NotificationChain msgs = null;
			if (aLTERNATIVEID != null) {
				msgs = ((InternalEObject) aLTERNATIVEID).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - MyreqifPackage.SPECRELATIONTYPE__ALTERNATIVEID, null, msgs);
			}
			if (newALTERNATIVEID != null) {
				msgs = ((InternalEObject) newALTERNATIVEID).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - MyreqifPackage.SPECRELATIONTYPE__ALTERNATIVEID, null, msgs);
			}
			msgs = basicSetALTERNATIVEID(newALTERNATIVEID, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, MyreqifPackage.SPECRELATIONTYPE__ALTERNATIVEID, newALTERNATIVEID, newALTERNATIVEID));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public SPECATTRIBUTESType getSPECATTRIBUTES() {
		return sPECATTRIBUTES;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetSPECATTRIBUTES(SPECATTRIBUTESType newSPECATTRIBUTES, NotificationChain msgs) {
		SPECATTRIBUTESType oldSPECATTRIBUTES = sPECATTRIBUTES;
		sPECATTRIBUTES = newSPECATTRIBUTES;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MyreqifPackage.SPECRELATIONTYPE__SPECATTRIBUTES,
					oldSPECATTRIBUTES, newSPECATTRIBUTES);
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
	public void setSPECATTRIBUTES(SPECATTRIBUTESType newSPECATTRIBUTES) {
		if (newSPECATTRIBUTES != sPECATTRIBUTES) {
			NotificationChain msgs = null;
			if (sPECATTRIBUTES != null) {
				msgs = ((InternalEObject) sPECATTRIBUTES).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
						- MyreqifPackage.SPECRELATIONTYPE__SPECATTRIBUTES, null, msgs);
			}
			if (newSPECATTRIBUTES != null) {
				msgs = ((InternalEObject) newSPECATTRIBUTES).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- MyreqifPackage.SPECRELATIONTYPE__SPECATTRIBUTES, null, msgs);
			}
			msgs = basicSetSPECATTRIBUTES(newSPECATTRIBUTES, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, MyreqifPackage.SPECRELATIONTYPE__SPECATTRIBUTES, newSPECATTRIBUTES,
					newSPECATTRIBUTES));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getDESC() {
		return dESC;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setDESC(String newDESC) {
		String oldDESC = dESC;
		dESC = newDESC;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, MyreqifPackage.SPECRELATIONTYPE__DESC, oldDESC, dESC));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getIDENTIFIER() {
		return iDENTIFIER;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setIDENTIFIER(String newIDENTIFIER) {
		String oldIDENTIFIER = iDENTIFIER;
		iDENTIFIER = newIDENTIFIER;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, MyreqifPackage.SPECRELATIONTYPE__IDENTIFIER, oldIDENTIFIER, iDENTIFIER));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public XMLGregorianCalendar getLASTCHANGE() {
		return lASTCHANGE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setLASTCHANGE(XMLGregorianCalendar newLASTCHANGE) {
		XMLGregorianCalendar oldLASTCHANGE = lASTCHANGE;
		lASTCHANGE = newLASTCHANGE;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, MyreqifPackage.SPECRELATIONTYPE__LASTCHANGE, oldLASTCHANGE, lASTCHANGE));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getLONGNAME() {
		return lONGNAME;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setLONGNAME(String newLONGNAME) {
		String oldLONGNAME = lONGNAME;
		lONGNAME = newLONGNAME;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, MyreqifPackage.SPECRELATIONTYPE__LONGNAME, oldLONGNAME, lONGNAME));
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
		case MyreqifPackage.SPECRELATIONTYPE__ALTERNATIVEID:
			return basicSetALTERNATIVEID(null, msgs);
		case MyreqifPackage.SPECRELATIONTYPE__SPECATTRIBUTES:
			return basicSetSPECATTRIBUTES(null, msgs);
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
		case MyreqifPackage.SPECRELATIONTYPE__ALTERNATIVEID:
			return getALTERNATIVEID();
		case MyreqifPackage.SPECRELATIONTYPE__SPECATTRIBUTES:
			return getSPECATTRIBUTES();
		case MyreqifPackage.SPECRELATIONTYPE__DESC:
			return getDESC();
		case MyreqifPackage.SPECRELATIONTYPE__IDENTIFIER:
			return getIDENTIFIER();
		case MyreqifPackage.SPECRELATIONTYPE__LASTCHANGE:
			return getLASTCHANGE();
		case MyreqifPackage.SPECRELATIONTYPE__LONGNAME:
			return getLONGNAME();
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
		case MyreqifPackage.SPECRELATIONTYPE__ALTERNATIVEID:
			setALTERNATIVEID((ALTERNATIVEIDType14) newValue);
			return;
		case MyreqifPackage.SPECRELATIONTYPE__SPECATTRIBUTES:
			setSPECATTRIBUTES((SPECATTRIBUTESType) newValue);
			return;
		case MyreqifPackage.SPECRELATIONTYPE__DESC:
			setDESC((String) newValue);
			return;
		case MyreqifPackage.SPECRELATIONTYPE__IDENTIFIER:
			setIDENTIFIER((String) newValue);
			return;
		case MyreqifPackage.SPECRELATIONTYPE__LASTCHANGE:
			setLASTCHANGE((XMLGregorianCalendar) newValue);
			return;
		case MyreqifPackage.SPECRELATIONTYPE__LONGNAME:
			setLONGNAME((String) newValue);
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
		case MyreqifPackage.SPECRELATIONTYPE__ALTERNATIVEID:
			setALTERNATIVEID((ALTERNATIVEIDType14) null);
			return;
		case MyreqifPackage.SPECRELATIONTYPE__SPECATTRIBUTES:
			setSPECATTRIBUTES((SPECATTRIBUTESType) null);
			return;
		case MyreqifPackage.SPECRELATIONTYPE__DESC:
			setDESC(DESC_EDEFAULT);
			return;
		case MyreqifPackage.SPECRELATIONTYPE__IDENTIFIER:
			setIDENTIFIER(IDENTIFIER_EDEFAULT);
			return;
		case MyreqifPackage.SPECRELATIONTYPE__LASTCHANGE:
			setLASTCHANGE(LASTCHANGE_EDEFAULT);
			return;
		case MyreqifPackage.SPECRELATIONTYPE__LONGNAME:
			setLONGNAME(LONGNAME_EDEFAULT);
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
		case MyreqifPackage.SPECRELATIONTYPE__ALTERNATIVEID:
			return aLTERNATIVEID != null;
		case MyreqifPackage.SPECRELATIONTYPE__SPECATTRIBUTES:
			return sPECATTRIBUTES != null;
		case MyreqifPackage.SPECRELATIONTYPE__DESC:
			return DESC_EDEFAULT == null ? dESC != null : !DESC_EDEFAULT.equals(dESC);
		case MyreqifPackage.SPECRELATIONTYPE__IDENTIFIER:
			return IDENTIFIER_EDEFAULT == null ? iDENTIFIER != null : !IDENTIFIER_EDEFAULT.equals(iDENTIFIER);
		case MyreqifPackage.SPECRELATIONTYPE__LASTCHANGE:
			return LASTCHANGE_EDEFAULT == null ? lASTCHANGE != null : !LASTCHANGE_EDEFAULT.equals(lASTCHANGE);
		case MyreqifPackage.SPECRELATIONTYPE__LONGNAME:
			return LONGNAME_EDEFAULT == null ? lONGNAME != null : !LONGNAME_EDEFAULT.equals(lONGNAME);
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
		result.append(" (dESC: ");
		result.append(dESC);
		result.append(", iDENTIFIER: ");
		result.append(iDENTIFIER);
		result.append(", lASTCHANGE: ");
		result.append(lASTCHANGE);
		result.append(", lONGNAME: ");
		result.append(lONGNAME);
		result.append(')');
		return result.toString();
	}

} // SPECRELATIONTYPEImpl
