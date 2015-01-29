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
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ApplicationsByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ApplicationsByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;

/**
 * Generated pattern matcher API of the org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.applicationsByName pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link ApplicationsByNameMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern applicationsByName(app : Application, name){
 * 	Application.name(app, name);
 * }
 * </pre></code>
 * 
 * @see ApplicationsByNameMatch
 * @see ApplicationsByNameProcessor
 * @see ApplicationsByNameQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class ApplicationsByNameMatcher extends BaseMatcher<ApplicationsByNameMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<ApplicationsByNameMatcher> querySpecification() throws IncQueryException {
    return ApplicationsByNameQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static ApplicationsByNameMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    ApplicationsByNameMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new ApplicationsByNameMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_APP = 0;
  
  private final static int POSITION_NAME = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(ApplicationsByNameMatcher.class);
  
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
  public ApplicationsByNameMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public ApplicationsByNameMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return matches represented as a ApplicationsByNameMatch object.
   * 
   */
  public Collection<ApplicationsByNameMatch> getAllMatches(final Application pApp, final String pName) {
    return rawGetAllMatches(new Object[]{pApp, pName});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return a match represented as a ApplicationsByNameMatch object, or null if no match is found.
   * 
   */
  public ApplicationsByNameMatch getOneArbitraryMatch(final Application pApp, final String pName) {
    return rawGetOneArbitraryMatch(new Object[]{pApp, pName});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Application pApp, final String pName) {
    return rawHasMatch(new Object[]{pApp, pName});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Application pApp, final String pName) {
    return rawCountMatches(new Object[]{pApp, pName});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Application pApp, final String pName, final IMatchProcessor<? super ApplicationsByNameMatch> processor) {
    rawForEachMatch(new Object[]{pApp, pName}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Application pApp, final String pName, final IMatchProcessor<? super ApplicationsByNameMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pApp, pName}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<ApplicationsByNameMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Application pApp, final String pName) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pApp, pName});
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public ApplicationsByNameMatch newMatch(final Application pApp, final String pName) {
    return ApplicationsByNameMatch.newMatch(pApp, pName);
    
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
  
  /**
   * Retrieve the set of values that occur in matches for app.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Application> getAllValuesOfapp(final ApplicationsByNameMatch partialMatch) {
    return rawAccumulateAllValuesOfapp(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for app.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Application> getAllValuesOfapp(final String pName) {
    return rawAccumulateAllValuesOfapp(new Object[]{null, pName});
  }
  
  /**
   * Retrieve the set of values that occur in matches for name.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<String> rawAccumulateAllValuesOfname(final Object[] parameters) {
    Set<String> results = new HashSet<String>();
    rawAccumulateAllValues(POSITION_NAME, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for name.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfname() {
    return rawAccumulateAllValuesOfname(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for name.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfname(final ApplicationsByNameMatch partialMatch) {
    return rawAccumulateAllValuesOfname(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for name.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfname(final Application pApp) {
    return rawAccumulateAllValuesOfname(new Object[]{pApp, null});
  }
  
  @Override
  protected ApplicationsByNameMatch tupleToMatch(final Tuple t) {
    try {
      return ApplicationsByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) t.get(POSITION_APP), (java.lang.String) t.get(POSITION_NAME));
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in tuple not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected ApplicationsByNameMatch arrayToMatch(final Object[] match) {
    try {
      return ApplicationsByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) match[POSITION_APP], (java.lang.String) match[POSITION_NAME]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected ApplicationsByNameMatch arrayToMatchMutable(final Object[] match) {
    try {
      return ApplicationsByNameMatch.newMutableMatch((org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) match[POSITION_APP], (java.lang.String) match[POSITION_NAME]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
}
