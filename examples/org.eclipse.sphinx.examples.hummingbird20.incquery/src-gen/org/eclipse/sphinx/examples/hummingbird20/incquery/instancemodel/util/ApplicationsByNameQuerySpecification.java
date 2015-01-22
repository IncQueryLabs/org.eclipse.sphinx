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
public final class ApplicationsByNameQuerySpecification extends BaseGeneratedQuerySpecification<ApplicationsByNameMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ApplicationsByNameQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected ApplicationsByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ApplicationsByNameMatcher.on(engine);
  }
  
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
  public ApplicationsByNameMatch newEmptyMatch() {
    return ApplicationsByNameMatch.newEmptyMatch();
  }
  
  @Override
  public ApplicationsByNameMatch newMatch(final Object... parameters) {
    return ApplicationsByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) parameters[0], (java.lang.String) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_app = body.getOrCreateVariableByName("app");
      PVariable var_name = body.getOrCreateVariableByName("name");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_app, "app"), 
        new ExportedParameter(body, var_name, "name")
      ));
      
      
      new TypeUnary(body, var_app, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel", "Application"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel/Application");
      new TypeBinary(body, CONTEXT, var_app, var_name, getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common/Identifiable.name");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static ApplicationsByNameQuerySpecification INSTANCE = make();
    
    public static ApplicationsByNameQuerySpecification make() {
      return new ApplicationsByNameQuerySpecification();					
      
    }
  }
}
