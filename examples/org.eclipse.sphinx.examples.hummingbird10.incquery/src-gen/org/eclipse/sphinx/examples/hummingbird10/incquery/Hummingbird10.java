package org.eclipse.sphinx.examples.hummingbird10.incquery;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ApplicationsMatcher;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ComponentsMatcher;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ConnectionsMatcher;
import org.eclipse.sphinx.examples.hummingbird10.incquery.InterfacesMatcher;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ParametersMatcher;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.ApplicationsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.ComponentsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.ConnectionsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.InterfacesQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.ParametersQuerySpecification;

/**
 * A pattern group formed of all patterns defined in hummingbird10.eiq.
 * 
 * <p>Use the static instance as any {@link org.eclipse.incquery.runtime.api.IPatternGroup}, to conveniently prepare
 * an EMF-IncQuery engine for matching all patterns originally defined in file hummingbird10.eiq,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package org.eclipse.sphinx.examples.hummingbird10.incquery, the group contains the definition of the following patterns: <ul>
 * <li>applications</li>
 * <li>components</li>
 * <li>interfaces</li>
 * <li>connections</li>
 * <li>parameters</li>
 * </ul>
 * 
 * @see IPatternGroup
 * 
 */
@SuppressWarnings("all")
public final class Hummingbird10 extends BaseGeneratedPatternGroup {
  /**
   * Access the pattern group.
   * 
   * @return the singleton instance of the group
   * @throws IncQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static Hummingbird10 instance() throws IncQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new Hummingbird10();
    }
    return INSTANCE;
    
  }
  
  private static Hummingbird10 INSTANCE;
  
  private Hummingbird10() throws IncQueryException {
    querySpecifications.add(ApplicationsQuerySpecification.instance());
    querySpecifications.add(ComponentsQuerySpecification.instance());
    querySpecifications.add(InterfacesQuerySpecification.instance());
    querySpecifications.add(ConnectionsQuerySpecification.instance());
    querySpecifications.add(ParametersQuerySpecification.instance());
    
  }
  
  public ApplicationsQuerySpecification getApplications() throws IncQueryException {
    return ApplicationsQuerySpecification.instance();
  }
  
  public ApplicationsMatcher getApplications(final IncQueryEngine engine) throws IncQueryException {
    return ApplicationsMatcher.on(engine);
  }
  
  public ComponentsQuerySpecification getComponents() throws IncQueryException {
    return ComponentsQuerySpecification.instance();
  }
  
  public ComponentsMatcher getComponents(final IncQueryEngine engine) throws IncQueryException {
    return ComponentsMatcher.on(engine);
  }
  
  public InterfacesQuerySpecification getInterfaces() throws IncQueryException {
    return InterfacesQuerySpecification.instance();
  }
  
  public InterfacesMatcher getInterfaces(final IncQueryEngine engine) throws IncQueryException {
    return InterfacesMatcher.on(engine);
  }
  
  public ConnectionsQuerySpecification getConnections() throws IncQueryException {
    return ConnectionsQuerySpecification.instance();
  }
  
  public ConnectionsMatcher getConnections(final IncQueryEngine engine) throws IncQueryException {
    return ConnectionsMatcher.on(engine);
  }
  
  public ParametersQuerySpecification getParameters() throws IncQueryException {
    return ParametersQuerySpecification.instance();
  }
  
  public ParametersMatcher getParameters(final IncQueryEngine engine) throws IncQueryException {
    return ParametersMatcher.on(engine);
  }
}
