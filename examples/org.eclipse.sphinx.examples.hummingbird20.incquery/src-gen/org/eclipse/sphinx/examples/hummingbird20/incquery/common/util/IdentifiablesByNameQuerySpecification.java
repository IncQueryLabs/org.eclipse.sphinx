package org.eclipse.sphinx.examples.hummingbird20.incquery.common.util;

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
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.IdentifiablesByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.IdentifiablesByNameMatcher;

/**
 * A pattern-specific query specification that can instantiate IdentifiablesByNameMatcher in a type-safe way.
 * 
 * @see IdentifiablesByNameMatcher
 * @see IdentifiablesByNameMatch
 * 
 */
@SuppressWarnings("all")
public final class IdentifiablesByNameQuerySpecification extends BaseGeneratedQuerySpecification<IdentifiablesByNameMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IdentifiablesByNameQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected IdentifiablesByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return IdentifiablesByNameMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.common.identifiablesByName";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("identifiable","name");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("identifiable", "org.eclipse.sphinx.examples.hummingbird20.common.Identifiable"),new PParameter("name", "java.lang.String"));
  }
  
  @Override
  public IdentifiablesByNameMatch newEmptyMatch() {
    return IdentifiablesByNameMatch.newEmptyMatch();
  }
  
  @Override
  public IdentifiablesByNameMatch newMatch(final Object... parameters) {
    return IdentifiablesByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.common.Identifiable) parameters[0], (java.lang.String) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_identifiable = body.getOrCreateVariableByName("identifiable");
      PVariable var_name = body.getOrCreateVariableByName("name");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_identifiable, "identifiable"), 
        new ExportedParameter(body, var_name, "name")
      ));
      
      
      new TypeBinary(body, CONTEXT, var_identifiable, var_name, getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common/Identifiable.name");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static IdentifiablesByNameQuerySpecification INSTANCE = make();
    
    public static IdentifiablesByNameQuerySpecification make() {
      return new IdentifiablesByNameQuerySpecification();					
      
    }
  }
}
