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
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.InterfacesMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.InterfacesMatcher;

/**
 * A pattern-specific query specification that can instantiate InterfacesMatcher in a type-safe way.
 * 
 * @see InterfacesMatcher
 * @see InterfacesMatch
 * 
 */
@SuppressWarnings("all")
public final class InterfacesQuerySpecification extends BaseGeneratedEMFQuerySpecification<InterfacesMatcher> {
  private InterfacesQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static InterfacesQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected InterfacesMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return InterfacesMatcher.on(engine);
  }
  
  @Override
  public InterfacesMatch newEmptyMatch() {
    return InterfacesMatch.newEmptyMatch();
  }
  
  @Override
  public InterfacesMatch newMatch(final Object... parameters) {
    return InterfacesMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface) parameters[0]);
  }
  
  private static class LazyHolder {
    private final static InterfacesQuerySpecification INSTANCE = make();
    
    public static InterfacesQuerySpecification make() {
      return new InterfacesQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static InterfacesQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.interfaces";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("interface");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("interface", "org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_interface = body.getOrCreateVariableByName("interface");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_interface, "interface")
      	));
      	new TypeConstraint(body, new FlatTuple(var_interface), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Interface")));
      	new TypeConstraint(body, new FlatTuple(var_interface), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Interface")));
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
