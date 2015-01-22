/**
 * <copyright>
 *
 * Copyright (c) 2008-2014 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - Enhancements and maintenance
 *
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird20.typemodel;

import org.eclipse.emf.common.util.EList;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Interface</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface#getProvidingComponentTypes <em>Providing Component Types</em>}</li>
 *   <li>{@link org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface#getRequiringPorts <em>Requiring Ports</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package#getInterface()
 * @model
 * @generated
 */
public interface Interface extends Identifiable {
	/**
	 * Returns the value of the '<em><b>Providing Component Types</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType#getProvidedInterfaces <em>Provided Interfaces</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Providing Component Types</em>' reference list isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Providing Component Types</em>' reference list.
	 * @see org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package#getInterface_ProvidingComponentTypes()
	 * @see org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType#getProvidedInterfaces
	 * @model opposite="providedInterfaces"
	 * @generated
	 */
	EList<ComponentType> getProvidingComponentTypes();

	/**
	 * Returns the value of the '<em><b>Requiring Ports</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.sphinx.examples.hummingbird20.typemodel.Port}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.sphinx.examples.hummingbird20.typemodel.Port#getRequiredInterface <em>Required Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Requiring Ports</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Requiring Ports</em>' reference list.
	 * @see org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package#getInterface_RequiringPorts()
	 * @see org.eclipse.sphinx.examples.hummingbird20.typemodel.Port#getRequiredInterface
	 * @model opposite="requiredInterface"
	 * @generated
	 */
	EList<Port> getRequiringPorts();

} // Interface
