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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>SOURCESPECIFICATION Type</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SOURCESPECIFICATIONType#getSPECIFICATIONREF <em>
 * SPECIFICATIONREF</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSOURCESPECIFICATIONType()
 * @model extendedMetaData="name='SOURCE-SPECIFICATION_._type' kind='elementOnly'"
 * @generated
 */
public interface SOURCESPECIFICATIONType extends EObject {
	/**
	 * Returns the value of the '<em><b>SPECIFICATIONREF</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>SPECIFICATIONREF</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>SPECIFICATIONREF</em>' attribute.
	 * @see #setSPECIFICATIONREF(String)
	 * @see org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.MyreqifPackage#getSOURCESPECIFICATIONType_SPECIFICATIONREF()
	 * @model dataType="org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.GLOBALREF"
	 *        extendedMetaData="kind='element' name='SPECIFICATION-REF' namespace='##targetNamespace'"
	 * @generated
	 */
	String getSPECIFICATIONREF();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.sphinx.tests.emf.serialization.env.emf.myreqif.SOURCESPECIFICATIONType#getSPECIFICATIONREF
	 * <em>SPECIFICATIONREF</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>SPECIFICATIONREF</em>' attribute.
	 * @see #getSPECIFICATIONREF()
	 * @generated
	 */
	void setSPECIFICATIONREF(String value);

} // SOURCESPECIFICATIONType
