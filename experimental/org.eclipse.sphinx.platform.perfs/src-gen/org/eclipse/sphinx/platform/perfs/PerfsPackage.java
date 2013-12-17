/**
 */
package org.eclipse.sphinx.platform.perfs;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each operation of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc --> <!-- begin-model-doc --> * <copyright> Copyright (c) 2012 itemis and others. All rights
 * reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public
 * License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: itemis - Initial API and implementation </copyright> <!-- end-model-doc -->
 * 
 * @see org.eclipse.sphinx.platform.perfs.PerfsFactory
 * @model kind="package" annotation="http://www.eclipse.org/emf/2002/GenModel basePackage='org.eclipse.sphinx.platform'"
 * @generated
 */
public interface PerfsPackage extends EPackage {
	/**
	 * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNAME = "perfs";

	/**
	 * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_URI = "org.eclipse.sphinx.platform.perfs";

	/**
	 * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_PREFIX = "perfs";

	/**
	 * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	PerfsPackage eINSTANCE = org.eclipse.sphinx.platform.perfs.impl.PerfsPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.sphinx.platform.perfs.impl.PerformanceStatsImpl
	 * <em>Performance Stats</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.sphinx.platform.perfs.impl.PerformanceStatsImpl
	 * @see org.eclipse.sphinx.platform.perfs.impl.PerfsPackageImpl#getPerformanceStats()
	 * @generated
	 */
	int PERFORMANCE_STATS = 0;

	/**
	 * The feature id for the '<em><b>Measurements</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int PERFORMANCE_STATS__MEASUREMENTS = 0;

	/**
	 * The number of structural features of the '<em>Performance Stats</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int PERFORMANCE_STATS_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Performance Stats</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int PERFORMANCE_STATS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl <em>Measurement</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl
	 * @see org.eclipse.sphinx.platform.perfs.impl.PerfsPackageImpl#getMeasurement()
	 * @generated
	 */
	int MEASUREMENT = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Count</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT__COUNT = 1;

	/**
	 * The feature id for the '<em><b>Total</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT__TOTAL = 2;

	/**
	 * The feature id for the '<em><b>Running</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT__RUNNING = 3;

	/**
	 * The feature id for the '<em><b>Start Time</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT__START_TIME = 4;

	/**
	 * The feature id for the '<em><b>Average Time</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT__AVERAGE_TIME = 5;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT__CHILDREN = 6;

	/**
	 * The number of structural features of the '<em>Measurement</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT_FEATURE_COUNT = 7;

	/**
	 * The operation id for the '<em>Start</em>' operation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT___START = 0;

	/**
	 * The operation id for the '<em>Stop</em>' operation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT___STOP = 1;

	/**
	 * The number of operations of the '<em>Measurement</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MEASUREMENT_OPERATION_COUNT = 2;

	/**
	 * Returns the meta object for class '{@link org.eclipse.sphinx.platform.perfs.PerformanceStats
	 * <em>Performance Stats</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Performance Stats</em>'.
	 * @see org.eclipse.sphinx.platform.perfs.PerformanceStats
	 * @generated
	 */
	EClass getPerformanceStats();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.sphinx.platform.perfs.PerformanceStats#getMeasurements <em>Measurements</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Measurements</em>'.
	 * @see org.eclipse.sphinx.platform.perfs.PerformanceStats#getMeasurements()
	 * @see #getPerformanceStats()
	 * @generated
	 */
	EReference getPerformanceStats_Measurements();

	/**
	 * Returns the meta object for class '{@link org.eclipse.sphinx.platform.perfs.Measurement <em>Measurement</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Measurement</em>'.
	 * @see org.eclipse.sphinx.platform.perfs.Measurement
	 * @generated
	 */
	EClass getMeasurement();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.sphinx.platform.perfs.Measurement#getName
	 * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.sphinx.platform.perfs.Measurement#getName()
	 * @see #getMeasurement()
	 * @generated
	 */
	EAttribute getMeasurement_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.sphinx.platform.perfs.Measurement#getCount
	 * <em>Count</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Count</em>'.
	 * @see org.eclipse.sphinx.platform.perfs.Measurement#getCount()
	 * @see #getMeasurement()
	 * @generated
	 */
	EAttribute getMeasurement_Count();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.sphinx.platform.perfs.Measurement#getTotal
	 * <em>Total</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Total</em>'.
	 * @see org.eclipse.sphinx.platform.perfs.Measurement#getTotal()
	 * @see #getMeasurement()
	 * @generated
	 */
	EAttribute getMeasurement_Total();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.sphinx.platform.perfs.Measurement#isRunning
	 * <em>Running</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Running</em>'.
	 * @see org.eclipse.sphinx.platform.perfs.Measurement#isRunning()
	 * @see #getMeasurement()
	 * @generated
	 */
	EAttribute getMeasurement_Running();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.sphinx.platform.perfs.Measurement#getStartTime
	 * <em>Start Time</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Start Time</em>'.
	 * @see org.eclipse.sphinx.platform.perfs.Measurement#getStartTime()
	 * @see #getMeasurement()
	 * @generated
	 */
	EAttribute getMeasurement_StartTime();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.sphinx.platform.perfs.Measurement#getAverageTime
	 * <em>Average Time</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Average Time</em>'.
	 * @see org.eclipse.sphinx.platform.perfs.Measurement#getAverageTime()
	 * @see #getMeasurement()
	 * @generated
	 */
	EAttribute getMeasurement_AverageTime();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.sphinx.platform.perfs.Measurement#getChildren <em>Children</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Children</em>'.
	 * @see org.eclipse.sphinx.platform.perfs.Measurement#getChildren()
	 * @see #getMeasurement()
	 * @generated
	 */
	EReference getMeasurement_Children();

	/**
	 * Returns the meta object for the '{@link org.eclipse.sphinx.platform.perfs.Measurement#start() <em>Start</em>}'
	 * operation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the '<em>Start</em>' operation.
	 * @see org.eclipse.sphinx.platform.perfs.Measurement#start()
	 * @generated
	 */
	EOperation getMeasurement__Start();

	/**
	 * Returns the meta object for the '{@link org.eclipse.sphinx.platform.perfs.Measurement#stop() <em>Stop</em>}'
	 * operation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the '<em>Stop</em>' operation.
	 * @see org.eclipse.sphinx.platform.perfs.Measurement#stop()
	 * @generated
	 */
	EOperation getMeasurement__Stop();

	/**
	 * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	PerfsFactory getPerfsFactory();

	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each operation of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.sphinx.platform.perfs.impl.PerformanceStatsImpl
		 * <em>Performance Stats</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.sphinx.platform.perfs.impl.PerformanceStatsImpl
		 * @see org.eclipse.sphinx.platform.perfs.impl.PerfsPackageImpl#getPerformanceStats()
		 * @generated
		 */
		EClass PERFORMANCE_STATS = eINSTANCE.getPerformanceStats();

		/**
		 * The meta object literal for the '<em><b>Measurements</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference PERFORMANCE_STATS__MEASUREMENTS = eINSTANCE.getPerformanceStats_Measurements();

		/**
		 * The meta object literal for the '{@link org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl
		 * <em>Measurement</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl
		 * @see org.eclipse.sphinx.platform.perfs.impl.PerfsPackageImpl#getMeasurement()
		 * @generated
		 */
		EClass MEASUREMENT = eINSTANCE.getMeasurement();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute MEASUREMENT__NAME = eINSTANCE.getMeasurement_Name();

		/**
		 * The meta object literal for the '<em><b>Count</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute MEASUREMENT__COUNT = eINSTANCE.getMeasurement_Count();

		/**
		 * The meta object literal for the '<em><b>Total</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute MEASUREMENT__TOTAL = eINSTANCE.getMeasurement_Total();

		/**
		 * The meta object literal for the '<em><b>Running</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute MEASUREMENT__RUNNING = eINSTANCE.getMeasurement_Running();

		/**
		 * The meta object literal for the '<em><b>Start Time</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute MEASUREMENT__START_TIME = eINSTANCE.getMeasurement_StartTime();

		/**
		 * The meta object literal for the '<em><b>Average Time</b></em>' attribute feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute MEASUREMENT__AVERAGE_TIME = eINSTANCE.getMeasurement_AverageTime();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference MEASUREMENT__CHILDREN = eINSTANCE.getMeasurement_Children();

		/**
		 * The meta object literal for the '<em><b>Start</b></em>' operation. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @generated
		 */
		EOperation MEASUREMENT___START = eINSTANCE.getMeasurement__Start();

		/**
		 * The meta object literal for the '<em><b>Stop</b></em>' operation. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @generated
		 */
		EOperation MEASUREMENT___STOP = eINSTANCE.getMeasurement__Stop();

	}

} // PerfsPackage
