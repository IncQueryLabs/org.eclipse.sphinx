/**
 */
package org.eclipse.sphinx.platform.perfs.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.sphinx.platform.perfs.Measurement;
import org.eclipse.sphinx.platform.perfs.PerfsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Measurement</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl#getCount <em>Count</em>}</li>
 * <li>{@link org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl#getTotal <em>Total</em>}</li>
 * <li>{@link org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl#isRunning <em>Running</em>}</li>
 * <li>{@link org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl#getStartTime <em>Start Time</em>}</li>
 * <li>{@link org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl#getAverageTime <em>Average Time</em>}</li>
 * <li>{@link org.eclipse.sphinx.platform.perfs.impl.MeasurementImpl#getChildren <em>Children</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class MeasurementImpl extends MinimalEObjectImpl.Container implements Measurement {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getCount() <em>Count</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getCount()
	 * @generated
	 * @ordered
	 */
	protected static final int COUNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getCount() <em>Count</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getCount()
	 * @generated
	 * @ordered
	 */
	protected int count = COUNT_EDEFAULT;

	/**
	 * The default value of the '{@link #getTotal() <em>Total</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTotal()
	 * @generated
	 * @ordered
	 */
	protected static final long TOTAL_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getTotal() <em>Total</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getTotal()
	 * @generated
	 * @ordered
	 */
	protected long total = TOTAL_EDEFAULT;

	/**
	 * The default value of the '{@link #isRunning() <em>Running</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #isRunning()
	 * @generated
	 * @ordered
	 */
	protected static final boolean RUNNING_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isRunning() <em>Running</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #isRunning()
	 * @generated
	 * @ordered
	 */
	protected boolean running = RUNNING_EDEFAULT;

	/**
	 * The default value of the '{@link #getStartTime() <em>Start Time</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getStartTime()
	 * @generated
	 * @ordered
	 */
	protected static final long START_TIME_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getStartTime() <em>Start Time</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getStartTime()
	 * @generated
	 * @ordered
	 */
	protected long startTime = START_TIME_EDEFAULT;

	/**
	 * The default value of the '{@link #getAverageTime() <em>Average Time</em>}' attribute. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getAverageTime()
	 * @generated
	 * @ordered
	 */
	protected static final long AVERAGE_TIME_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getChildren()
	 * @generated
	 * @ordered
	 */
	protected EList<Measurement> children;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected MeasurementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PerfsPackage.Literals.MEASUREMENT;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, PerfsPackage.MEASUREMENT__NAME, oldName, name));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public int getCount() {
		return count;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setCount(int newCount) {
		int oldCount = count;
		count = newCount;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, PerfsPackage.MEASUREMENT__COUNT, oldCount, count));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public long getTotal() {
		return total;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setTotal(long newTotal) {
		long oldTotal = total;
		total = newTotal;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, PerfsPackage.MEASUREMENT__TOTAL, oldTotal, total));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean isRunning() {
		return running;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setRunning(boolean newRunning) {
		boolean oldRunning = running;
		running = newRunning;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, PerfsPackage.MEASUREMENT__RUNNING, oldRunning, running));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public long getStartTime() {
		return startTime;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setStartTime(long newStartTime) {
		long oldStartTime = startTime;
		startTime = newStartTime;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, PerfsPackage.MEASUREMENT__START_TIME, oldStartTime, startTime));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public long getAverageTime() {
		int _count = getCount();
		boolean _greaterThan = _count > 0;
		if (_greaterThan) {
			long _total = getTotal();
			int _count_1 = getCount();
			return _total / _count_1;
		}
		return 0;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<Measurement> getChildren() {
		if (children == null) {
			children = new EObjectContainmentEList<Measurement>(Measurement.class, this, PerfsPackage.MEASUREMENT__CHILDREN);
		}
		return children;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void start() {
		Measurement _this = this;
		_this.setRunning(true);
		Measurement _this_1 = this;
		ThreadMXBean _threadMXBean = ManagementFactory.getThreadMXBean();
		long _currentThreadCpuTime = _threadMXBean.getCurrentThreadCpuTime();
		_this_1.setStartTime(_currentThreadCpuTime);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void stop() {
		Measurement _this = this;
		boolean _isRunning = _this.isRunning();
		if (_isRunning) {
			Measurement _this_1 = this;
			_this_1.setRunning(false);
			ThreadMXBean _threadMXBean = ManagementFactory.getThreadMXBean();
			long _currentThreadCpuTime = _threadMXBean.getCurrentThreadCpuTime();
			Measurement _this_2 = this;
			long _startTime = _this_2.getStartTime();
			final long interval = _currentThreadCpuTime - _startTime;
			Measurement _this_3 = this;
			Measurement _this_4 = this;
			int _count = _this_4.getCount();
			int _plus = _count + 1;
			_this_3.setCount(_plus);
			Measurement _this_5 = this;
			Measurement _this_6 = this;
			long _total = _this_6.getTotal();
			long _plus_1 = _total + interval;
			_this_5.setTotal(_plus_1);
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
		case PerfsPackage.MEASUREMENT__CHILDREN:
			return ((InternalEList<?>) getChildren()).basicRemove(otherEnd, msgs);
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
		case PerfsPackage.MEASUREMENT__NAME:
			return getName();
		case PerfsPackage.MEASUREMENT__COUNT:
			return getCount();
		case PerfsPackage.MEASUREMENT__TOTAL:
			return getTotal();
		case PerfsPackage.MEASUREMENT__RUNNING:
			return isRunning();
		case PerfsPackage.MEASUREMENT__START_TIME:
			return getStartTime();
		case PerfsPackage.MEASUREMENT__AVERAGE_TIME:
			return getAverageTime();
		case PerfsPackage.MEASUREMENT__CHILDREN:
			return getChildren();
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
		case PerfsPackage.MEASUREMENT__NAME:
			setName((String) newValue);
			return;
		case PerfsPackage.MEASUREMENT__COUNT:
			setCount((Integer) newValue);
			return;
		case PerfsPackage.MEASUREMENT__TOTAL:
			setTotal((Long) newValue);
			return;
		case PerfsPackage.MEASUREMENT__RUNNING:
			setRunning((Boolean) newValue);
			return;
		case PerfsPackage.MEASUREMENT__START_TIME:
			setStartTime((Long) newValue);
			return;
		case PerfsPackage.MEASUREMENT__CHILDREN:
			getChildren().clear();
			getChildren().addAll((Collection<? extends Measurement>) newValue);
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
		case PerfsPackage.MEASUREMENT__NAME:
			setName(NAME_EDEFAULT);
			return;
		case PerfsPackage.MEASUREMENT__COUNT:
			setCount(COUNT_EDEFAULT);
			return;
		case PerfsPackage.MEASUREMENT__TOTAL:
			setTotal(TOTAL_EDEFAULT);
			return;
		case PerfsPackage.MEASUREMENT__RUNNING:
			setRunning(RUNNING_EDEFAULT);
			return;
		case PerfsPackage.MEASUREMENT__START_TIME:
			setStartTime(START_TIME_EDEFAULT);
			return;
		case PerfsPackage.MEASUREMENT__CHILDREN:
			getChildren().clear();
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
		case PerfsPackage.MEASUREMENT__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case PerfsPackage.MEASUREMENT__COUNT:
			return count != COUNT_EDEFAULT;
		case PerfsPackage.MEASUREMENT__TOTAL:
			return total != TOTAL_EDEFAULT;
		case PerfsPackage.MEASUREMENT__RUNNING:
			return running != RUNNING_EDEFAULT;
		case PerfsPackage.MEASUREMENT__START_TIME:
			return startTime != START_TIME_EDEFAULT;
		case PerfsPackage.MEASUREMENT__AVERAGE_TIME:
			return getAverageTime() != AVERAGE_TIME_EDEFAULT;
		case PerfsPackage.MEASUREMENT__CHILDREN:
			return children != null && !children.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
		case PerfsPackage.MEASUREMENT___START:
			start();
			return null;
		case PerfsPackage.MEASUREMENT___STOP:
			stop();
			return null;
		}
		return super.eInvoke(operationID, arguments);
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
		result.append(" (name: ");
		result.append(name);
		result.append(", count: ");
		result.append(count);
		result.append(", total: ");
		result.append(total);
		result.append(", running: ");
		result.append(running);
		result.append(", startTime: ");
		result.append(startTime);
		result.append(')');
		return result.toString();
	}

} // MeasurementImpl
