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
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ParametersByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ParametersByNameMatcher;

/**
 * A pattern-specific query specification that can instantiate ParametersByNameMatcher in a type-safe way.
 * 
 * @see ParametersByNameMatcher
 * @see ParametersByNameMatch
 * 
 */
@SuppressWarnings("all")
public final class ParametersByNameQuerySpecification extends BaseGeneratedEMFQuerySpecification<ParametersByNameMatcher> {
  private ParametersByNameQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ParametersByNameQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected ParametersByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ParametersByNameMatcher.on(engine);
  }
  
  @Override
  public ParametersByNameMatch newEmptyMatch() {
    return ParametersByNameMatch.newEmptyMatch();
  }
  
  @Override
  public ParametersByNameMatch newMatch(final Object... parameters) {
    return ParametersByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter) parameters[0], (java.lang.String) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static ParametersByNameQuerySpecification INSTANCE = make();
    
    public static ParametersByNameQuerySpecification make() {
      return new ParametersByNameQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static ParametersByNameQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.parametersByName";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("param","name");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("param", "org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter"),new PParameter("name", "java.lang.String"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_param = body.getOrCreateVariableByName("param");
      	PVariable var_name = body.getOrCreateVariableByName("name");
      	PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_param, "param"),
      				
      		new ExportedParameter(body, var_name, "name")
      	));
      	new TypeConstraint(body, new FlatTuple(var_param), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Parameter")));
      	new TypeConstraint(body, new FlatTuple(var_param), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Parameter")));
      	new TypeConstraint(body, new FlatTuple(var_param, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name")));
      	new Equality(body, var__virtual_0_, var_name);
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
