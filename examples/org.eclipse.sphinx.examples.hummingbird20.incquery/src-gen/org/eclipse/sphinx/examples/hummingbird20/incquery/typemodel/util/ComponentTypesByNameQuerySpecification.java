package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

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
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ComponentTypesByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ComponentTypesByNameMatcher;

/**
 * A pattern-specific query specification that can instantiate ComponentTypesByNameMatcher in a type-safe way.
 * 
 * @see ComponentTypesByNameMatcher
 * @see ComponentTypesByNameMatch
 * 
 */
@SuppressWarnings("all")
public final class ComponentTypesByNameQuerySpecification extends BaseGeneratedQuerySpecification<ComponentTypesByNameMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ComponentTypesByNameQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected ComponentTypesByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ComponentTypesByNameMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.componentTypesByName";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("componentType","name");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("componentType", "org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType"),new PParameter("name", "java.lang.String"));
  }
  
  @Override
  public ComponentTypesByNameMatch newEmptyMatch() {
    return ComponentTypesByNameMatch.newEmptyMatch();
  }
  
  @Override
  public ComponentTypesByNameMatch newMatch(final Object... parameters) {
    return ComponentTypesByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType) parameters[0], (java.lang.String) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_componentType = body.getOrCreateVariableByName("componentType");
      PVariable var_name = body.getOrCreateVariableByName("name");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_componentType, "componentType"), 
        new ExportedParameter(body, var_name, "name")
      ));
      
      new TypeUnary(body, var_componentType, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "ComponentType"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel/ComponentType");
      
      new TypeBinary(body, CONTEXT, var_componentType, var_name, getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common/Identifiable.name");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static ComponentTypesByNameQuerySpecification INSTANCE = make();
    
    public static ComponentTypesByNameQuerySpecification make() {
      return new ComponentTypesByNameQuerySpecification();					
      
    }
  }
}
