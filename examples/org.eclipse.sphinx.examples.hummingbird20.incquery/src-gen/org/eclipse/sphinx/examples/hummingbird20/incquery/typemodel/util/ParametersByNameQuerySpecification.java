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
public final class ParametersByNameQuerySpecification extends BaseGeneratedQuerySpecification<ParametersByNameMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ParametersByNameQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected ParametersByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ParametersByNameMatcher.on(engine);
  }
  
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
  public ParametersByNameMatch newEmptyMatch() {
    return ParametersByNameMatch.newEmptyMatch();
  }
  
  @Override
  public ParametersByNameMatch newMatch(final Object... parameters) {
    return ParametersByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter) parameters[0], (java.lang.String) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_param = body.getOrCreateVariableByName("param");
      PVariable var_name = body.getOrCreateVariableByName("name");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_param, "param"), 
        new ExportedParameter(body, var_name, "name")
      ));
      
      new TypeUnary(body, var_param, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Parameter"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel/Parameter");
      
      new TypeBinary(body, CONTEXT, var_param, var_name, getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common/Identifiable.name");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static ParametersByNameQuerySpecification INSTANCE = make();
    
    public static ParametersByNameQuerySpecification make() {
      return new ParametersByNameQuerySpecification();					
      
    }
  }
}
