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
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ApplicationsByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ApplicationsByNameMatcher;

/**
 * A pattern-specific query specification that can instantiate ApplicationsByNameMatcher in a type-safe way.
 * 
 * @see ApplicationsByNameMatcher
 * @see ApplicationsByNameMatch
 * 
 */
@SuppressWarnings("all")
public final class ApplicationsByNameQuerySpecification extends BaseGeneratedEMFQuerySpecification<ApplicationsByNameMatcher> {
  private ApplicationsByNameQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ApplicationsByNameQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected ApplicationsByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ApplicationsByNameMatcher.on(engine);
  }
  
  @Override
  public ApplicationsByNameMatch newEmptyMatch() {
    return ApplicationsByNameMatch.newEmptyMatch();
  }
  
  @Override
  public ApplicationsByNameMatch newMatch(final Object... parameters) {
    return ApplicationsByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) parameters[0], (java.lang.String) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static ApplicationsByNameQuerySpecification INSTANCE = make();
    
    public static ApplicationsByNameQuerySpecification make() {
      return new ApplicationsByNameQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static ApplicationsByNameQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.applicationsByName";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("app","name");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("app", "org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application"),new PParameter("name", "java.lang.String"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_app = body.getOrCreateVariableByName("app");
      	PVariable var_name = body.getOrCreateVariableByName("name");
      	PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_app, "app"),
      				
      		new ExportedParameter(body, var_name, "name")
      	));
      	new TypeConstraint(body, new FlatTuple(var_app), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel", "Application")));
      	new TypeConstraint(body, new FlatTuple(var_app), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel", "Application")));
      	new TypeConstraint(body, new FlatTuple(var_app, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name")));
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
