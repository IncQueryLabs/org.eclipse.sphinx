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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PortsMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PortsMatcher;

/**
 * A pattern-specific query specification that can instantiate PortsMatcher in a type-safe way.
 * 
 * @see PortsMatcher
 * @see PortsMatch
 * 
 */
@SuppressWarnings("all")
public final class PortsQuerySpecification extends BaseGeneratedQuerySpecification<PortsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static PortsQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected PortsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return PortsMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ports";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("port");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("port", "org.eclipse.sphinx.examples.hummingbird20.typemodel.Port"));
  }
  
  @Override
  public PortsMatch newEmptyMatch() {
    return PortsMatch.newEmptyMatch();
  }
  
  @Override
  public PortsMatch newMatch(final Object... parameters) {
    return PortsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Port) parameters[0]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_port = body.getOrCreateVariableByName("port");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_port, "port")
      ));
      
      new TypeUnary(body, var_port, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Port"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel/Port");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static PortsQuerySpecification INSTANCE = make();
    
    public static PortsQuerySpecification make() {
      return new PortsQuerySpecification();					
      
    }
  }
}
