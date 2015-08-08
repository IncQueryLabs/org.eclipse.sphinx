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
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
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
public final class IdentifiablesByNameQuerySpecification extends BaseGeneratedEMFQuerySpecification<IdentifiablesByNameMatcher> {
  private IdentifiablesByNameQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IdentifiablesByNameQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected IdentifiablesByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return IdentifiablesByNameMatcher.on(engine);
  }
  
  @Override
  public IdentifiablesByNameMatch newEmptyMatch() {
    return IdentifiablesByNameMatch.newEmptyMatch();
  }
  
  @Override
  public IdentifiablesByNameMatch newMatch(final Object... parameters) {
    return IdentifiablesByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.common.Identifiable) parameters[0], (java.lang.String) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static IdentifiablesByNameQuerySpecification INSTANCE = make();
    
    public static IdentifiablesByNameQuerySpecification make() {
      return new IdentifiablesByNameQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static IdentifiablesByNameQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
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
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_identifiable = body.getOrCreateVariableByName("identifiable");
      	PVariable var_name = body.getOrCreateVariableByName("name");
      	PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_identifiable, "identifiable"),
      				
      		new ExportedParameter(body, var_name, "name")
      	));
      	new TypeConstraint(body, new FlatTuple(var_identifiable), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable")));
      	new TypeConstraint(body, new FlatTuple(var_identifiable), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable")));
      	new TypeConstraint(body, new FlatTuple(var_identifiable, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name")));
      	new Equality(body, var__virtual_0_, var_name);
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
