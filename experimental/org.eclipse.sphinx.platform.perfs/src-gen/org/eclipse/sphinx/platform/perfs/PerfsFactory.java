/**
 */
package org.eclipse.sphinx.platform.perfs;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.sphinx.platform.perfs.PerfsPackage
 * @generated
 */
public interface PerfsFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  PerfsFactory eINSTANCE = org.eclipse.sphinx.platform.perfs.impl.PerfsFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Performance Stats</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Performance Stats</em>'.
   * @generated
   */
  PerformanceStats createPerformanceStats();

  /**
   * Returns a new object of class '<em>Measurement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Measurement</em>'.
   * @generated
   */
  Measurement createMeasurement();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  PerfsPackage getPerfsPackage();

} //PerfsFactory
