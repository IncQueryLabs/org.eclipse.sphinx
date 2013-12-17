/**
 */
package org.eclipse.sphinx.platform.perfs.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.sphinx.platform.perfs.Measurement;
import org.eclipse.sphinx.platform.perfs.PerformanceStats;
import org.eclipse.sphinx.platform.perfs.PerfsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Performance Stats</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.sphinx.platform.perfs.impl.PerformanceStatsImpl#getMeasurements <em>Measurements</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class PerformanceStatsImpl extends MinimalEObjectImpl.Container implements PerformanceStats {
	/**
	 * The cached value of the '{@link #getMeasurements() <em>Measurements</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getMeasurements()
	 * @generated
	 * @ordered
	 */
	protected EList<Measurement> measurements;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected PerformanceStatsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PerfsPackage.Literals.PERFORMANCE_STATS;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<Measurement> getMeasurements() {
		if (measurements == null) {
			measurements = new EObjectContainmentEList<Measurement>(Measurement.class, this, PerfsPackage.PERFORMANCE_STATS__MEASUREMENTS);
		}
		return measurements;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case PerfsPackage.PERFORMANCE_STATS__MEASUREMENTS:
			return ((InternalEList<?>) getMeasurements()).basicRemove(otherEnd, msgs);
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
		case PerfsPackage.PERFORMANCE_STATS__MEASUREMENTS:
			return getMeasurements();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case PerfsPackage.PERFORMANCE_STATS__MEASUREMENTS:
			getMeasurements().clear();
			getMeasurements().addAll((Collection<? extends Measurement>) newValue);
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
		case PerfsPackage.PERFORMANCE_STATS__MEASUREMENTS:
			getMeasurements().clear();
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
		case PerfsPackage.PERFORMANCE_STATS__MEASUREMENTS:
			return measurements != null && !measurements.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} // PerformanceStatsImpl
