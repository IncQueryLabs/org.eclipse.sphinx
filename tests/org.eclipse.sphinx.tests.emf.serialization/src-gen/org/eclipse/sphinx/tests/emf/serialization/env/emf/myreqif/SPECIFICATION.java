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
package org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>SPECIFICATION</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getALTERNATIVEID <em>ALTERNATIVEID</em>}
 * </li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getVALUES <em>VALUES</em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getCHILDREN <em>CHILDREN</em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getTYPE <em>TYPE</em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getDESC <em>DESC</em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getIDENTIFIER <em>IDENTIFIER</em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getLASTCHANGE <em>LASTCHANGE</em>}</li>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getLONGNAME <em>LONGNAME</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSPECIFICATION()
 * @model extendedMetaData="name='SPECIFICATION' kind='elementOnly'"
 * @generated
 */
public interface SPECIFICATION extends EObject {
	/**
	 * Returns the value of the '<em><b>ALTERNATIVEID</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>ALTERNATIVEID</em>' containment reference isn't clear, there really should be more of
	 * a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>ALTERNATIVEID</em>' containment reference.
	 * @see #setALTERNATIVEID(ALTERNATIVEIDType10)
	 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSPECIFICATION_ALTERNATIVEID()
	 * @model containment="true" extendedMetaData="kind='element' name='ALTERNATIVE-ID' namespace='##targetNamespace'"
	 * @generated
	 */
	ALTERNATIVEIDType10 getALTERNATIVEID();

	/**
	 * Sets the value of the '{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getALTERNATIVEID
	 * <em>ALTERNATIVEID</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>ALTERNATIVEID</em>' containment reference.
	 * @see #getALTERNATIVEID()
	 * @generated
	 */
	void setALTERNATIVEID(ALTERNATIVEIDType10 value);

	/**
	 * Returns the value of the '<em><b>VALUES</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>VALUES</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>VALUES</em>' containment reference.
	 * @see #setVALUES(VALUESType)
	 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSPECIFICATION_VALUES()
	 * @model containment="true" extendedMetaData="kind='element' name='VALUES' namespace='##targetNamespace'"
	 * @generated
	 */
	VALUESType getVALUES();

	/**
	 * Sets the value of the '{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getVALUES
	 * <em>VALUES</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>VALUES</em>' containment reference.
	 * @see #getVALUES()
	 * @generated
	 */
	void setVALUES(VALUESType value);

	/**
	 * Returns the value of the '<em><b>CHILDREN</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>CHILDREN</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>CHILDREN</em>' containment reference.
	 * @see #setCHILDREN(CHILDRENType1)
	 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSPECIFICATION_CHILDREN()
	 * @model containment="true" extendedMetaData="kind='element' name='CHILDREN' namespace='##targetNamespace'"
	 * @generated
	 */
	CHILDRENType1 getCHILDREN();

	/**
	 * Sets the value of the '{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getCHILDREN
	 * <em>CHILDREN</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>CHILDREN</em>' containment reference.
	 * @see #getCHILDREN()
	 * @generated
	 */
	void setCHILDREN(CHILDRENType1 value);

	/**
	 * Returns the value of the '<em><b>TYPE</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>TYPE</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>TYPE</em>' containment reference.
	 * @see #setTYPE(TYPEType1)
	 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSPECIFICATION_TYPE()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='TYPE' namespace='##targetNamespace'"
	 * @generated
	 */
	TYPEType1 getTYPE();

	/**
	 * Sets the value of the '{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getTYPE
	 * <em>TYPE</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>TYPE</em>' containment reference.
	 * @see #getTYPE()
	 * @generated
	 */
	void setTYPE(TYPEType1 value);

	/**
	 * Returns the value of the '<em><b>DESC</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>DESC</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>DESC</em>' attribute.
	 * @see #setDESC(String)
	 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSPECIFICATION_DESC()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" extendedMetaData="kind='attribute' name='DESC'"
	 * @generated
	 */
	String getDESC();

	/**
	 * Sets the value of the '{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getDESC
	 * <em>DESC</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>DESC</em>' attribute.
	 * @see #getDESC()
	 * @generated
	 */
	void setDESC(String value);

	/**
	 * Returns the value of the '<em><b>IDENTIFIER</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>IDENTIFIER</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>IDENTIFIER</em>' attribute.
	 * @see #setIDENTIFIER(String)
	 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSPECIFICATION_IDENTIFIER()
	 * @model id="true" dataType="org.eclipse.emf.ecore.xml.type.ID" required="true"
	 *        extendedMetaData="kind='attribute' name='IDENTIFIER'"
	 * @generated
	 */
	String getIDENTIFIER();

	/**
	 * Sets the value of the '{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getIDENTIFIER
	 * <em>IDENTIFIER</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>IDENTIFIER</em>' attribute.
	 * @see #getIDENTIFIER()
	 * @generated
	 */
	void setIDENTIFIER(String value);

	/**
	 * Returns the value of the '<em><b>LASTCHANGE</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>LASTCHANGE</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>LASTCHANGE</em>' attribute.
	 * @see #setLASTCHANGE(XMLGregorianCalendar)
	 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSPECIFICATION_LASTCHANGE()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.DateTime" required="true"
	 *        extendedMetaData="kind='attribute' name='LAST-CHANGE'"
	 * @generated
	 */
	XMLGregorianCalendar getLASTCHANGE();

	/**
	 * Sets the value of the '{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getLASTCHANGE
	 * <em>LASTCHANGE</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>LASTCHANGE</em>' attribute.
	 * @see #getLASTCHANGE()
	 * @generated
	 */
	void setLASTCHANGE(XMLGregorianCalendar value);

	/**
	 * Returns the value of the '<em><b>LONGNAME</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>LONGNAME</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>LONGNAME</em>' attribute.
	 * @see #setLONGNAME(String)
	 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSPECIFICATION_LONGNAME()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" extendedMetaData="kind='attribute' name='LONG-NAME'"
	 * @generated
	 */
	String getLONGNAME();

	/**
	 * Sets the value of the '{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SPECIFICATION#getLONGNAME
	 * <em>LONGNAME</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>LONGNAME</em>' attribute.
	 * @see #getLONGNAME()
	 * @generated
	 */
	void setLONGNAME(String value);

} // SPECIFICATION
