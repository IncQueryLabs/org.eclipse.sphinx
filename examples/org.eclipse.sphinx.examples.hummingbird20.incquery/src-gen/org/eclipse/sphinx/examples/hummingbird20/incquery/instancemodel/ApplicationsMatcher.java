package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ApplicationsMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ApplicationsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;

/**
 * Generated pattern matcher API of the org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.applications pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link ApplicationsMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern applications(app : Application){
 * 	Application(app);
 * }
 * </pre></code>
 * 
 * @see ApplicationsMatch
 * @see ApplicationsProcessor
 * @see ApplicationsQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class ApplicationsMatcher extends BaseMatcher<ApplicationsMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static ApplicationsMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    ApplicationsMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new ApplicationsMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_APP = 0;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(ApplicationsMatcher.class);
  
  /**
   * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet).
   * If a pattern matcher is already constructed with the same root, only a light-weight reference is returned.
   * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
   * The match set will be incrementally refreshed upon updates from this scope.
   * <p>The matcher will be created within the managed {@link IncQueryEngine} belonging to the EMF model root, so
   * multiple matchers will reuse the same engine and benefit from increased performance and reduced memory footprint.
   * @param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * @deprecated use {@link #on(IncQueryEngine)} instead, e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}
   * 
   */
  @Deprecated
  public ApplicationsMatcher(final Notifier emfRoot) throws IncQueryException {
    this(IncQueryEngine.on(emfRoot));
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * @deprecated use {@link #on(IncQueryEngine)} instead
   * 
   */
  @Deprecated
  public ApplicationsMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @return matches represented as a ApplicationsMatch object.
   * 
   */
  public Collection<ApplicationsMatch> getAllMatches(final Application pApp) {
    return rawGetAllMatches(new Object[]{pApp});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @return a match represented as a ApplicationsMatch object, or null if no match is found.
   * 
   */
  public ApplicationsMatch getOneArbitraryMatch(final Application pApp) {
    return rawGetOneArbitraryMatch(new Object[]{pApp});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Application pApp) {
    return rawHasMatch(new Object[]{pApp});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Application pApp) {
    return rawCountMatches(new Object[]{pApp});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Application pApp, final IMatchProcessor<? super ApplicationsMatch> processor) {
    rawForEachMatch(new Object[]{pApp}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Application pApp, final IMatchProcessor<? super ApplicationsMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pApp}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public ApplicationsMatch newMatch(final Application pApp) {
    return ApplicationsMatch.newMatch(pApp);
  }
  
  /**
   * Retrieve the set of values that occur in matches for app.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Application> rawAccumulateAllValuesOfapp(final Object[] parameters) {
    Set<Application> results = new HashSet<Application>();
    rawAccumulateAllValues(POSITION_APP, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for app.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Application> getAllValuesOfapp() {
    return rawAccumulateAllValuesOfapp(emptyArray());
  }
  
  @Override
  protected ApplicationsMatch tupleToMatch(final Tuple t) {
    try {
    	return ApplicationsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) t.get(POSITION_APP));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected ApplicationsMatch arrayToMatch(final Object[] match) {
    try {
    	return ApplicationsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) match[POSITION_APP]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected ApplicationsMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return ApplicationsMatch.newMutableMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) match[POSITION_APP]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<ApplicationsMatcher> querySpecification() throws IncQueryException {
    return ApplicationsQuerySpecification.instance();
  }
}
