package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util;

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
public final class ComponentsQuerySpecification extends BaseGeneratedEMFQuerySpecification<ComponentsMatcher> {
  private ComponentsQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ComponentsQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected ComponentsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ComponentsMatcher.on(engine);
  }
  
  @Override
  public ComponentsMatch newEmptyMatch() {
    return ComponentsMatch.newEmptyMatch();
  }
  
  @Override
  public ComponentsMatch newMatch(final Object... parameters) {
    return ComponentsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component) parameters[0]);
  }
  
  private static class LazyHolder {
    private final static ComponentsQuerySpecification INSTANCE = make();
    
    public static ComponentsQuerySpecification make() {
      return new ComponentsQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static ComponentsQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
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
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_component = body.getOrCreateVariableByName("component");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_component, "component")
      	));
      	new TypeConstraint(body, new FlatTuple(var_component), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel", "Component")));
      	new TypeConstraint(body, new FlatTuple(var_component), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel", "Component")));
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
