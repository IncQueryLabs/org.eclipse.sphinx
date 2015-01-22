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
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PortsByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PortsByNameMatcher;

/**
 * A pattern-specific query specification that can instantiate PortsByNameMatcher in a type-safe way.
 * 
 * @see PortsByNameMatcher
 * @see PortsByNameMatch
 * 
 */
@SuppressWarnings("all")
public final class PortsByNameQuerySpecification extends BaseGeneratedQuerySpecification<PortsByNameMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static PortsByNameQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected PortsByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return PortsByNameMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.portsByName";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("port","name");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("port", "org.eclipse.sphinx.examples.hummingbird20.typemodel.Port"),new PParameter("name", "java.lang.String"));
  }
  
  @Override
  public PortsByNameMatch newEmptyMatch() {
    return PortsByNameMatch.newEmptyMatch();
  }
  
  @Override
  public PortsByNameMatch newMatch(final Object... parameters) {
    return PortsByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Port) parameters[0], (java.lang.String) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_port = body.getOrCreateVariableByName("port");
      PVariable var_name = body.getOrCreateVariableByName("name");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_port, "port"), 
        new ExportedParameter(body, var_name, "name")
      ));
      
      new TypeUnary(body, var_port, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Port"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel/Port");
      
      new TypeBinary(body, CONTEXT, var_port, var_name, getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common/Identifiable.name");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static PortsByNameQuerySpecification INSTANCE = make();
    
    public static PortsByNameQuerySpecification make() {
      return new PortsByNameQuerySpecification();					
      
    }
  }
}
