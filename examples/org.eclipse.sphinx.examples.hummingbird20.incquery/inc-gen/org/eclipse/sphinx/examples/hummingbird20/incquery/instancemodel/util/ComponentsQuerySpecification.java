package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ComponentsMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ComponentsMatcher;

/**
 * A pattern-specific query specification that can instantiate ComponentsMatcher in a type-safe way.
 * 
 * @see ComponentsMatcher
 * @see ComponentsMatch
 * 
 */
@SuppressWarnings("all")
public final class ComponentsQuerySpecification extends BaseGeneratedQuerySpecification<ComponentsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ComponentsQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected ComponentsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ComponentsMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.components";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("component");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("component", "org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component"));
  }
  
  @Override
  public ComponentsMatch newEmptyMatch() {
    return ComponentsMatch.newEmptyMatch();
  }
  
  @Override
  public ComponentsMatch newMatch(final Object... parameters) {
    return ComponentsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component) parameters[0]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_component = body.getOrCreateVariableByName("component");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_component, "component")
      ));
      
      new TypeUnary(body, var_component, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel", "Component"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel/Component");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static ComponentsQuerySpecification INSTANCE = make();
    
    public static ComponentsQuerySpecification make() {
      return new ComponentsQuerySpecification();					
      
    }
  }
}
