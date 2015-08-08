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
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ParametersMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ParametersMatcher;

/**
 * A pattern-specific query specification that can instantiate ParametersMatcher in a type-safe way.
 * 
 * @see ParametersMatcher
 * @see ParametersMatch
 * 
 */
@SuppressWarnings("all")
public final class ParametersQuerySpecification extends BaseGeneratedEMFQuerySpecification<ParametersMatcher> {
  private ParametersQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ParametersQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected ParametersMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ParametersMatcher.on(engine);
  }
  
  @Override
  public ParametersMatch newEmptyMatch() {
    return ParametersMatch.newEmptyMatch();
  }
  
  @Override
  public ParametersMatch newMatch(final Object... parameters) {
    return ParametersMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter) parameters[0]);
  }
  
  private static class LazyHolder {
    private final static ParametersQuerySpecification INSTANCE = make();
    
    public static ParametersQuerySpecification make() {
      return new ParametersQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static ParametersQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.parameters";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("param");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("param", "org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_param = body.getOrCreateVariableByName("param");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_param, "param")
      	));
      	new TypeConstraint(body, new FlatTuple(var_param), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Parameter")));
      	new TypeConstraint(body, new FlatTuple(var_param), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Parameter")));
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
