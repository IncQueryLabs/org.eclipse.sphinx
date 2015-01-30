package org.eclipse.sphinx.examples.hummingbird10.incquery.util;

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
import org.eclipse.sphinx.examples.hummingbird10.incquery.ParametersMatch;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ParametersMatcher;

/**
 * A pattern-specific query specification that can instantiate ParametersMatcher in a type-safe way.
 * 
 * @see ParametersMatcher
 * @see ParametersMatch
 * 
 */
@SuppressWarnings("all")
public final class ParametersQuerySpecification extends BaseGeneratedQuerySpecification<ParametersMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ParametersQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected ParametersMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ParametersMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird10.incquery.parameters";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("parameter");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("parameter", "org.eclipse.sphinx.examples.hummingbird10.Parameter"));
  }
  
  @Override
  public ParametersMatch newEmptyMatch() {
    return ParametersMatch.newEmptyMatch();
  }
  
  @Override
  public ParametersMatch newMatch(final Object... parameters) {
    return ParametersMatch.newMatch((org.eclipse.sphinx.examples.hummingbird10.Parameter) parameters[0]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_parameter = body.getOrCreateVariableByName("parameter");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_parameter, "parameter")
      ));
      
      new TypeUnary(body, var_parameter, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/1.0.0", "Parameter"), "http://www.eclipse.org/sphinx/examples/hummingbird/1.0.0/Parameter");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static ParametersQuerySpecification INSTANCE = make();
    
    public static ParametersQuerySpecification make() {
      return new ParametersQuerySpecification();					
      
    }
  }
}
