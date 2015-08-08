package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

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
public final class InterfacesByNameQuerySpecification extends BaseGeneratedEMFQuerySpecification<InterfacesByNameMatcher> {
  private InterfacesByNameQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static InterfacesByNameQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected InterfacesByNameMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return InterfacesByNameMatcher.on(engine);
  }
  
  @Override
  public InterfacesByNameMatch newEmptyMatch() {
    return InterfacesByNameMatch.newEmptyMatch();
  }
  
  @Override
  public InterfacesByNameMatch newMatch(final Object... parameters) {
    return InterfacesByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface) parameters[0], (java.lang.String) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static InterfacesByNameQuerySpecification INSTANCE = make();
    
    public static InterfacesByNameQuerySpecification make() {
      return new InterfacesByNameQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static InterfacesByNameQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
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
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_interface = body.getOrCreateVariableByName("interface");
      	PVariable var_name = body.getOrCreateVariableByName("name");
      	PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_interface, "interface"),
      				
      		new ExportedParameter(body, var_name, "name")
      	));
      	new TypeConstraint(body, new FlatTuple(var_interface), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Interface")));
      	new TypeConstraint(body, new FlatTuple(var_interface), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Interface")));
      	new TypeConstraint(body, new FlatTuple(var_interface, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/common", "Identifiable", "name")));
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
