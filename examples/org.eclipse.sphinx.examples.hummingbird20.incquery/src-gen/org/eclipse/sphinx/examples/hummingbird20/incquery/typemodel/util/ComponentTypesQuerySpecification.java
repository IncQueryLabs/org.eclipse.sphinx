package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ComponentTypesMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ComponentTypesMatcher;

/**
 * A pattern-specific query specification that can instantiate ComponentTypesMatcher in a type-safe way.
 * 
 * @see ComponentTypesMatcher
 * @see ComponentTypesMatch
 * 
 */
@SuppressWarnings("all")
public final class ComponentTypesQuerySpecification extends BaseGeneratedEMFQuerySpecification<ComponentTypesMatcher> {
  private ComponentTypesQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ComponentTypesQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected ComponentTypesMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ComponentTypesMatcher.on(engine);
  }
  
  @Override
  public ComponentTypesMatch newEmptyMatch() {
    return ComponentTypesMatch.newEmptyMatch();
  }
  
  @Override
  public ComponentTypesMatch newMatch(final Object... parameters) {
    return ComponentTypesMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType) parameters[0]);
  }
  
  private static class LazyHolder {
    private final static ComponentTypesQuerySpecification INSTANCE = make();
    
    public static ComponentTypesQuerySpecification make() {
      return new ComponentTypesQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static ComponentTypesQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.componentTypes";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("componentType");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("componentType", "org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_componentType = body.getOrCreateVariableByName("componentType");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_componentType, "componentType")
      	));
      	new TypeConstraint(body, new FlatTuple(var_componentType), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "ComponentType")));
      	new TypeConstraint(body, new FlatTuple(var_componentType), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "ComponentType")));
      	bodies.add(body);
      }
      	// to silence compiler error
      	if (false) throw new IncQueryException("Never", "happens");
      } catch (IncQueryException ex) {
      	throw processDependencyException(ex);
      }
      return bodies;
    }
  }
}
