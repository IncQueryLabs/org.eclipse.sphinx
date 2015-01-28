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
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.InterfacesByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.InterfacesByNameMatcher;

/**
 * A pattern-specific query specification that can instantiate InterfacesByNameMatcher in a type-safe way.
 * 
 * @see InterfacesByNameMatcher
 * @see InterfacesByNameMatch
 * 
 */
@SuppressWarnings("all")
public final class InterfacesByNameQuerySpecification extends BaseGeneratedQuerySpecification<InterfacesByNameMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static InterfacesByNameQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected InterfacesByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return InterfacesByNameMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.interfacesByName";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("interface","name");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("interface", "org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface"),new PParameter("name", "java.lang.String"));
  }
  
  @Override
  public InterfacesByNameMatch newEmptyMatch() {
    return InterfacesByNameMatch.newEmptyMatch();
  }
  
  @Override
  public InterfacesByNameMatch newMatch(final Object... parameters) {
    return InterfacesByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface) parameters[0], (java.lang.String) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_interface = body.getOrCreateVariableByName("interface");
      PVariable var_name = body.getOrCreateVariableByName("name");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_interface, "interface"), 
        new ExportedParameter(body, var_name, "name")
      ));
      
      
      new TypeUnary(body, var_interface, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Interface"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel/Interface");
      new TypeBinary(body, CONTEXT, var_interface, var_name, getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common/Identifiable.name");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static InterfacesByNameQuerySpecification INSTANCE = make();
    
    public static InterfacesByNameQuerySpecification make() {
      return new InterfacesByNameQuerySpecification();					
      
    }
  }
}
