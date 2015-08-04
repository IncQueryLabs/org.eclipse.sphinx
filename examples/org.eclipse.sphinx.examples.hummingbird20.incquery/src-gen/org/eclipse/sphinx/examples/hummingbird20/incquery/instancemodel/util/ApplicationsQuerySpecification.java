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
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ApplicationsMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ApplicationsMatcher;

/**
 * A pattern-specific query specification that can instantiate ApplicationsMatcher in a type-safe way.
 * 
 * @see ApplicationsMatcher
 * @see ApplicationsMatch
 * 
 */
@SuppressWarnings("all")
public final class ApplicationsQuerySpecification extends BaseGeneratedEMFQuerySpecification<ApplicationsMatcher> {
  private ApplicationsQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ApplicationsQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected ApplicationsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ApplicationsMatcher.on(engine);
  }
  
  @Override
  public ApplicationsMatch newEmptyMatch() {
    return ApplicationsMatch.newEmptyMatch();
  }
  
  @Override
  public ApplicationsMatch newMatch(final Object... parameters) {
    return ApplicationsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) parameters[0]);
  }
  
  private static class LazyHolder {
    private final static ApplicationsQuerySpecification INSTANCE = make();
    
    public static ApplicationsQuerySpecification make() {
      return new ApplicationsQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static ApplicationsQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.applications";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("app");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("app", "org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_app = body.getOrCreateVariableByName("app");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_app, "app")
      	));
      	new TypeConstraint(body, new FlatTuple(var_app), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel", "Application")));
      	new TypeConstraint(body, new FlatTuple(var_app), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel", "Application")));
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
