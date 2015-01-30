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
import org.eclipse.sphinx.examples.hummingbird10.incquery.ConnectionsMatch;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ConnectionsMatcher;

/**
 * A pattern-specific query specification that can instantiate ConnectionsMatcher in a type-safe way.
 * 
 * @see ConnectionsMatcher
 * @see ConnectionsMatch
 * 
 */
@SuppressWarnings("all")
public final class ConnectionsQuerySpecification extends BaseGeneratedQuerySpecification<ConnectionsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ConnectionsQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected ConnectionsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ConnectionsMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird10.incquery.connections";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("connection");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("connection", "org.eclipse.sphinx.examples.hummingbird10.Connection"));
  }
  
  @Override
  public ConnectionsMatch newEmptyMatch() {
    return ConnectionsMatch.newEmptyMatch();
  }
  
  @Override
  public ConnectionsMatch newMatch(final Object... parameters) {
    return ConnectionsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird10.Connection) parameters[0]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_connection = body.getOrCreateVariableByName("connection");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_connection, "connection")
      ));
      
      new TypeUnary(body, var_connection, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/1.0.0", "Connection"), "http://www.eclipse.org/sphinx/examples/hummingbird/1.0.0/Connection");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static ConnectionsQuerySpecification INSTANCE = make();
    
    public static ConnectionsQuerySpecification make() {
      return new ConnectionsQuerySpecification();					
      
    }
  }
}
