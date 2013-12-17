/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird20.instancemodel;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Application</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application#getComponents <em>Components</em>}</li>
 * <li>{@link org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application#getMixed <em>Mixed</em>}</li>
 * <li>{@link org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application#getXSISchemaLocation <em>XSI Schema
 * Location</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package#getApplication()
 * @model features="mixedOuterContent" mixedOuterContentUnique="false"
 *        mixedOuterContentDataType="org.eclipse.emf.ecore.EFeatureMapEntry" mixedOuterContentMany="true"
 *        mixedOuterContentTransient="true" mixedOuterContentSuppressedGetVisibility="true"
 *        mixedOuterContentExtendedMetaData="kind='elementWildcard' wildcards='http://www.eclipse.org/emf/2003/XMLType'"
 *        extendedMetaData="kind='mixed'"
 * @generated
 */
public interface Application extends Identifiable {
	/**
	 * Returns the value of the '<em><b>Components</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Components</em>' containment reference list isn't clear, there really should be more
	 * of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Components</em>' containment reference list.
	 * @see org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package#getApplication_Components()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<Component> getComponents();

	/**
	 * Returns the value of the '<em><b>Mixed</b></em>' attribute list. The list contents are of type
	 * {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mixed</em>' attribute list isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Mixed</em>' attribute list.
	 * @see org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package#getApplication_Mixed()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="name=':mixed' kind='elementWildcard'"
	 * @generated
	 */
	FeatureMap getMixed();

	/**
	 * Returns the value of the '<em><b>XSI Schema Location</b></em>' map. The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String}, <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>XSI Schema Location</em>' map isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>XSI Schema Location</em>' map.
	 * @see org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package#getApplication_XSISchemaLocation()
	 * @model mapType=
	 *        "org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
	 *        transient="true" extendedMetaData="name='xsi:schemaLocation' kind='element'"
	 * @generated
	 */
	EMap<String, String> getXSISchemaLocation();

} // Application
