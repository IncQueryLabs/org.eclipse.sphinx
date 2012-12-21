/**
 */
package org.eclipse.sphinx.platform.perfs.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.sphinx.platform.perfs.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PerfsFactoryImpl extends EFactoryImpl implements PerfsFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static PerfsFactory init()
  {
    try
    {
      PerfsFactory thePerfsFactory = (PerfsFactory)EPackage.Registry.INSTANCE.getEFactory("org.eclipse.sphinx.platform.perfs"); 
      if (thePerfsFactory != null)
      {
        return thePerfsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new PerfsFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PerfsFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case PerfsPackage.PERFORMANCE_STATS: return createPerformanceStats();
      case PerfsPackage.MEASUREMENT: return createMeasurement();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PerformanceStats createPerformanceStats()
  {
    PerformanceStatsImpl performanceStats = new PerformanceStatsImpl();
    return performanceStats;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Measurement createMeasurement()
  {
    MeasurementImpl measurement = new MeasurementImpl();
    return measurement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PerfsPackage getPerfsPackage()
  {
    return (PerfsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static PerfsPackage getPackage()
  {
    return PerfsPackage.eINSTANCE;
  }

} //PerfsFactoryImpl
