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
package org.eclipse.sphinx.platform.perfs;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Performance Stats</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.sphinx.platform.perfs.PerformanceStats#getMeasurements <em>Measurements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.sphinx.platform.perfs.PerfsPackage#getPerformanceStats()
 * @model
 * @generated
 */
public interface PerformanceStats extends EObject {
	/**
	 * Returns the value of the '<em><b>Measurements</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.sphinx.platform.perfs.Measurement}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Measurements</em>' containment reference list isn't clear, there really should be more
	 * of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Measurements</em>' containment reference list.
	 * @see org.eclipse.sphinx.platform.perfs.PerfsPackage#getPerformanceStats_Measurements()
	 * @model containment="true"
	 * @generated
	 */
	EList<Measurement> getMeasurements();

} // PerformanceStats
