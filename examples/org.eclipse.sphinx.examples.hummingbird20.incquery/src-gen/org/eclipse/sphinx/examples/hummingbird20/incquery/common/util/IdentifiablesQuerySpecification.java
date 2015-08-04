package org.eclipse.sphinx.examples.hummingbird20.incquery.common.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
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
public final class IdentifiablesQuerySpecification extends BaseGeneratedEMFQuerySpecification<IdentifiablesMatcher> {
  private IdentifiablesQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IdentifiablesQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected IdentifiablesMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return IdentifiablesMatcher.on(engine);
  }
  
  @Override
  public IdentifiablesMatch newEmptyMatch() {
    return IdentifiablesMatch.newEmptyMatch();
  }
  
  @Override
  public IdentifiablesMatch newMatch(final Object... parameters) {
    return IdentifiablesMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.common.Identifiable) parameters[0]);
  }
  
  private static class LazyHolder {
    private final static IdentifiablesQuerySpecification INSTANCE = make();
    
    public static IdentifiablesQuerySpecification make() {
      return new IdentifiablesQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static IdentifiablesQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
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
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_identifiable = body.getOrCreateVariableByName("identifiable");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_identifiable, "identifiable")
      	));
      	new TypeConstraint(body, new FlatTuple(var_identifiable), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable")));
      	new TypeConstraint(body, new FlatTuple(var_identifiable), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable")));
      	bodies.add(body);
      }
      	// to silence compiler error
      	if (false) throw new IncQueryException("Never", "happens");
      } catch (IncQueryException ex) {
      	throw processDependencyException(ex);
      }
      return bodies;
    }
  }
}
