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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.IdentifiablesMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.IdentifiablesMatcher;

/**
 * A pattern-specific query specification that can instantiate IdentifiablesMatcher in a type-safe way.
 * 
 * @see IdentifiablesMatcher
 * @see IdentifiablesMatch
 * 
 */
@SuppressWarnings("all")
public final class IdentifiablesQuerySpecification extends BaseGeneratedQuerySpecification<IdentifiablesMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IdentifiablesQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected IdentifiablesMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return IdentifiablesMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.common.identifiables";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("identifiable");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("identifiable", "org.eclipse.sphinx.examples.hummingbird20.common.Identifiable"));
  }
  
  @Override
  public IdentifiablesMatch newEmptyMatch() {
    return IdentifiablesMatch.newEmptyMatch();
  }
  
  @Override
  public IdentifiablesMatch newMatch(final Object... parameters) {
    return IdentifiablesMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.common.Identifiable) parameters[0]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_identifiable = body.getOrCreateVariableByName("identifiable");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_identifiable, "identifiable")
      ));
      
      new TypeUnary(body, var_identifiable, getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable"), "http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common/Identifiable");
      bodies.add(body);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static IdentifiablesQuerySpecification INSTANCE = make();
    
    public static IdentifiablesQuerySpecification make() {
      return new IdentifiablesQuerySpecification();					
      
    }
  }
}
