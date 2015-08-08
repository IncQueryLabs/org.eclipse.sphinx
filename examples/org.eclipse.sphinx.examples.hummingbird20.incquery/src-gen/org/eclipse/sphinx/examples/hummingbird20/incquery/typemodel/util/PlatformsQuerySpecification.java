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
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PlatformsMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PlatformsMatcher;

/**
 * A pattern-specific query specification that can instantiate PlatformsMatcher in a type-safe way.
 * 
 * @see PlatformsMatcher
 * @see PlatformsMatch
 * 
 */
@SuppressWarnings("all")
public final class PlatformsQuerySpecification extends BaseGeneratedEMFQuerySpecification<PlatformsMatcher> {
  private PlatformsQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static PlatformsQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected PlatformsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return PlatformsMatcher.on(engine);
  }
  
  @Override
  public PlatformsMatch newEmptyMatch() {
    return PlatformsMatch.newEmptyMatch();
  }
  
  @Override
  public PlatformsMatch newMatch(final Object... parameters) {
    return PlatformsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform) parameters[0]);
  }
  
  private static class LazyHolder {
    private final static PlatformsQuerySpecification INSTANCE = make();
    
    public static PlatformsQuerySpecification make() {
      return new PlatformsQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static PlatformsQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.platforms";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("platform");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("platform", "org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_platform = body.getOrCreateVariableByName("platform");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_platform, "platform")
      	));
      	new TypeConstraint(body, new FlatTuple(var_platform), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Platform")));
      	new TypeConstraint(body, new FlatTuple(var_platform), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/sphinx/examples/hummingbird/2.0.1/typemodel", "Platform")));
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
