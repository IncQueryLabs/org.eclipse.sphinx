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
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ConnectionsByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ConnectionsByNameMatcher;

/**
 * A pattern-specific query specification that can instantiate ConnectionsByNameMatcher in a type-safe way.
 * 
 * @see ConnectionsByNameMatcher
 * @see ConnectionsByNameMatch
 * 
 */
@SuppressWarnings("all")
public final class ConnectionsByNameQuerySpecification extends BaseGeneratedQuerySpecification<ConnectionsByNameMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ConnectionsByNameQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected ConnectionsByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ConnectionsByNameMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.connectionsByName";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("connection","name");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("connection", "org.eclipse.sphinx.examples.hummingbird20.instancemodel.Connection"),new PParameter("name", "java.lang.String"));
  }
  
  @Override
  public ConnectionsByNameMatch newEmptyMatch() {
    return ConnectionsByNameMatch.newEmptyMatch();
  }
  
  @Override
  public ConnectionsByNameMatch newMatch(final Object... parameters) {
    return ConnectionsByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Connection) parameters[0], (java.lang.String) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_connection = body.getOrCreateVariableByName("connection");
      PVariable var_name = body.getOrCreateVariableByName("name");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_connection, "connection"), 
        new ExportedParameter(body, var_name, "name")
      ));
      
      new TypeUnary(body, var_connection, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel", "Connection"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/instancemodel/Connection");
      
      new TypeBinary(body, CONTEXT, var_connection, var_name, getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common/Identifiable.name");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static ConnectionsByNameQuerySpecification INSTANCE = make();
    
    public static ConnectionsByNameQuerySpecification make() {
      return new ConnectionsByNameQuerySpecification();					
      
    }
  }
}
