package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ApplicationsByNameMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ApplicationsMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ComponentsByNameMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ComponentsMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ConnectionsByNameMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ConnectionsMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ApplicationsByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ApplicationsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ComponentsByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ComponentsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ConnectionsByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ConnectionsQuerySpecification;

/**
 * A pattern group formed of all patterns defined in instancemodel.eiq.
 * 
 * <p>Use the static instance as any {@link org.eclipse.incquery.runtime.api.IPatternGroup}, to conveniently prepare
 * an EMF-IncQuery engine for matching all patterns originally defined in file instancemodel.eiq,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel, the group contains the definition of the following patterns: <ul>
 * <li>applications</li>
 * <li>applicationsByName</li>
 * <li>components</li>
 * <li>componentsByName</li>
 * <li>connections</li>
 * <li>connectionsByName</li>
 * </ul>
 * 
 * @see IPatternGroup
 * 
 */
@SuppressWarnings("all")
public final class Instancemodel extends BaseGeneratedPatternGroup {
  /**
   * Access the pattern group.
   * 
   * @return the singleton instance of the group
   * @throws IncQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static Instancemodel instance() throws IncQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new Instancemodel();
    }
    return INSTANCE;
  }
  
  private static Instancemodel INSTANCE;
  
  private Instancemodel() throws IncQueryException {
    querySpecifications.add(ApplicationsQuerySpecification.instance());
    querySpecifications.add(ApplicationsByNameQuerySpecification.instance());
    querySpecifications.add(ComponentsQuerySpecification.instance());
    querySpecifications.add(ComponentsByNameQuerySpecification.instance());
    querySpecifications.add(ConnectionsQuerySpecification.instance());
    querySpecifications.add(ConnectionsByNameQuerySpecification.instance());
  }
  
  public ApplicationsQuerySpecification getApplications() throws IncQueryException {
    return ApplicationsQuerySpecification.instance();
  }
  
  public ApplicationsMatcher getApplications(final IncQueryEngine engine) throws IncQueryException {
    return ApplicationsMatcher.on(engine);
  }
  
  public ApplicationsByNameQuerySpecification getApplicationsByName() throws IncQueryException {
    return ApplicationsByNameQuerySpecification.instance();
  }
  
  public ApplicationsByNameMatcher getApplicationsByName(final IncQueryEngine engine) throws IncQueryException {
    return ApplicationsByNameMatcher.on(engine);
  }
  
  public ComponentsQuerySpecification getComponents() throws IncQueryException {
    return ComponentsQuerySpecification.instance();
  }
  
  public ComponentsMatcher getComponents(final IncQueryEngine engine) throws IncQueryException {
    return ComponentsMatcher.on(engine);
  }
  
  public ComponentsByNameQuerySpecification getComponentsByName() throws IncQueryException {
    return ComponentsByNameQuerySpecification.instance();
  }
  
  public ComponentsByNameMatcher getComponentsByName(final IncQueryEngine engine) throws IncQueryException {
    return ComponentsByNameMatcher.on(engine);
  }
  
  public ConnectionsQuerySpecification getConnections() throws IncQueryException {
    return ConnectionsQuerySpecification.instance();
  }
  
  public ConnectionsMatcher getConnections(final IncQueryEngine engine) throws IncQueryException {
    return ConnectionsMatcher.on(engine);
  }
  
  public ConnectionsByNameQuerySpecification getConnectionsByName() throws IncQueryException {
    return ConnectionsByNameQuerySpecification.instance();
  }
  
  public ConnectionsByNameMatcher getConnectionsByName(final IncQueryEngine engine) throws IncQueryException {
    return ConnectionsByNameMatcher.on(engine);
  }
}
