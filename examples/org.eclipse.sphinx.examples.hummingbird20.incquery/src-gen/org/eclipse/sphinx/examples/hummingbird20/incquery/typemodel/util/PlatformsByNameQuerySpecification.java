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
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PlatformsByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PlatformsByNameMatcher;

/**
 * A pattern-specific query specification that can instantiate PlatformsByNameMatcher in a type-safe way.
 * 
 * @see PlatformsByNameMatcher
 * @see PlatformsByNameMatch
 * 
 */
@SuppressWarnings("all")
public final class PlatformsByNameQuerySpecification extends BaseGeneratedQuerySpecification<PlatformsByNameMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static PlatformsByNameQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected PlatformsByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return PlatformsByNameMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.platformsByName";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("platform","name");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("platform", "org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform"),new PParameter("name", "java.lang.String"));
  }
  
  @Override
  public PlatformsByNameMatch newEmptyMatch() {
    return PlatformsByNameMatch.newEmptyMatch();
  }
  
  @Override
  public PlatformsByNameMatch newMatch(final Object... parameters) {
    return PlatformsByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform) parameters[0], (java.lang.String) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_platform = body.getOrCreateVariableByName("platform");
      PVariable var_name = body.getOrCreateVariableByName("name");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_platform, "platform"), 
        new ExportedParameter(body, var_name, "name")
      ));
      
      new TypeUnary(body, var_platform, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Platform"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel/Platform");
      
      new TypeBinary(body, CONTEXT, var_platform, var_name, getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common/Identifiable.name");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static PlatformsByNameQuerySpecification INSTANCE = make();
    
    public static PlatformsByNameQuerySpecification make() {
      return new PlatformsByNameQuerySpecification();					
      
    }
  }
}
