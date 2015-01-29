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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ComponentsByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ComponentsByNameMatcher;

/**
 * A pattern-specific query specification that can instantiate ComponentsByNameMatcher in a type-safe way.
 * 
 * @see ComponentsByNameMatcher
 * @see ComponentsByNameMatch
 * 
 */
@SuppressWarnings("all")
public final class ComponentsByNameQuerySpecification extends BaseGeneratedQuerySpecification<ComponentsByNameMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ComponentsByNameQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected ComponentsByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ComponentsByNameMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.componentsByName";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("component","name");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("component", "org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component"),new PParameter("name", "java.lang.String"));
  }
  
  @Override
  public ComponentsByNameMatch newEmptyMatch() {
    return ComponentsByNameMatch.newEmptyMatch();
  }
  
  @Override
  public ComponentsByNameMatch newMatch(final Object... parameters) {
    return ComponentsByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component) parameters[0], (java.lang.String) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_component = body.getOrCreateVariableByName("component");
      PVariable var_name = body.getOrCreateVariableByName("name");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_component, "component"), 
        new ExportedParameter(body, var_name, "name")
      ));
      
      new TypeUnary(body, var_component, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel", "Component"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel/Component");
      
      new TypeBinary(body, CONTEXT, var_component, var_name, getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common/Identifiable.name");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static ComponentsByNameQuerySpecification INSTANCE = make();
    
    public static ComponentsByNameQuerySpecification make() {
      return new ComponentsByNameQuerySpecification();					
      
    }
  }
}
