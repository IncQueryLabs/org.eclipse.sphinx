package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ComponentTypesMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.InterfacesMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ParametersMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PlatformsMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PortsMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.ComponentTypesQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.InterfacesQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.ParametersQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.PlatformsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.PortsQuerySpecification;

/**
 * A pattern group formed of all patterns defined in typemodel.eiq.
 * 
 * <p>Use the static instance as any {@link org.eclipse.incquery.runtime.api.IPatternGroup}, to conveniently prepare
 * an EMF-IncQuery engine for matching all patterns originally defined in file typemodel.eiq,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel, the group contains the definition of the following patterns: <ul>
 * <li>platforms</li>
 * <li>componentTypes</li>
 * <li>ports</li>
 * <li>interfaces</li>
 * <li>parameters</li>
 * </ul>
 * 
 * @see IPatternGroup
 * 
 */
@SuppressWarnings("all")
public final class Typemodel extends BaseGeneratedPatternGroup {
  /**
   * Access the pattern group.
   * 
   * @return the singleton instance of the group
   * @throws IncQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static Typemodel instance() throws IncQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new Typemodel();
    }
    return INSTANCE;
    
  }
  
  private static Typemodel INSTANCE;
  
  private Typemodel() throws IncQueryException {
    querySpecifications.add(PlatformsQuerySpecification.instance());
    querySpecifications.add(ComponentTypesQuerySpecification.instance());
    querySpecifications.add(PortsQuerySpecification.instance());
    querySpecifications.add(InterfacesQuerySpecification.instance());
    querySpecifications.add(ParametersQuerySpecification.instance());
    
  }
  
  public PlatformsQuerySpecification getPlatforms() throws IncQueryException {
    return PlatformsQuerySpecification.instance();
  }
  
  public PlatformsMatcher getPlatforms(final IncQueryEngine engine) throws IncQueryException {
    return PlatformsMatcher.on(engine);
  }
  
  public ComponentTypesQuerySpecification getComponentTypes() throws IncQueryException {
    return ComponentTypesQuerySpecification.instance();
  }
  
  public ComponentTypesMatcher getComponentTypes(final IncQueryEngine engine) throws IncQueryException {
    return ComponentTypesMatcher.on(engine);
  }
  
  public PortsQuerySpecification getPorts() throws IncQueryException {
    return PortsQuerySpecification.instance();
  }
  
  public PortsMatcher getPorts(final IncQueryEngine engine) throws IncQueryException {
    return PortsMatcher.on(engine);
  }
  
  public InterfacesQuerySpecification getInterfaces() throws IncQueryException {
    return InterfacesQuerySpecification.instance();
  }
  
  public InterfacesMatcher getInterfaces(final IncQueryEngine engine) throws IncQueryException {
    return InterfacesMatcher.on(engine);
  }
  
  public ParametersQuerySpecification getParameters() throws IncQueryException {
    return ParametersQuerySpecification.instance();
  }
  
  public ParametersMatcher getParameters(final IncQueryEngine engine) throws IncQueryException {
    return ParametersMatcher.on(engine);
  }
}
