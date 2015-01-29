package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

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
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PlatformsByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.PlatformsByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;

/**
 * Generated pattern matcher API of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.platformsByName pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link PlatformsByNameMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern platformsByName(platform : Platform, name){
 * 	Platform.name(platform, name);
 * }
 * </pre></code>
 * 
 * @see PlatformsByNameMatch
 * @see PlatformsByNameProcessor
 * @see PlatformsByNameQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class PlatformsByNameMatcher extends BaseMatcher<PlatformsByNameMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<PlatformsByNameMatcher> querySpecification() throws IncQueryException {
    return PlatformsByNameQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static PlatformsByNameMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    PlatformsByNameMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new PlatformsByNameMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_PLATFORM = 0;
  
  private final static int POSITION_NAME = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(PlatformsByNameMatcher.class);
  
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
  public PlatformsByNameMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public PlatformsByNameMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return matches represented as a PlatformsByNameMatch object.
   * 
   */
  public Collection<PlatformsByNameMatch> getAllMatches(final Platform pPlatform, final String pName) {
    return rawGetAllMatches(new Object[]{pPlatform, pName});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return a match represented as a PlatformsByNameMatch object, or null if no match is found.
   * 
   */
  public PlatformsByNameMatch getOneArbitraryMatch(final Platform pPlatform, final String pName) {
    return rawGetOneArbitraryMatch(new Object[]{pPlatform, pName});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Platform pPlatform, final String pName) {
    return rawHasMatch(new Object[]{pPlatform, pName});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Platform pPlatform, final String pName) {
    return rawCountMatches(new Object[]{pPlatform, pName});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Platform pPlatform, final String pName, final IMatchProcessor<? super PlatformsByNameMatch> processor) {
    rawForEachMatch(new Object[]{pPlatform, pName}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Platform pPlatform, final String pName, final IMatchProcessor<? super PlatformsByNameMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pPlatform, pName}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<PlatformsByNameMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Platform pPlatform, final String pName) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pPlatform, pName});
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public PlatformsByNameMatch newMatch(final Platform pPlatform, final String pName) {
    return PlatformsByNameMatch.newMatch(pPlatform, pName);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for platform.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Platform> rawAccumulateAllValuesOfplatform(final Object[] parameters) {
    Set<Platform> results = new HashSet<Platform>();
    rawAccumulateAllValues(POSITION_PLATFORM, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for platform.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Platform> getAllValuesOfplatform() {
    return rawAccumulateAllValuesOfplatform(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for platform.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Platform> getAllValuesOfplatform(final PlatformsByNameMatch partialMatch) {
    return rawAccumulateAllValuesOfplatform(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for platform.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Platform> getAllValuesOfplatform(final String pName) {
    return rawAccumulateAllValuesOfplatform(new Object[]{null, pName});
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
  public Set<String> getAllValuesOfname(final PlatformsByNameMatch partialMatch) {
    return rawAccumulateAllValuesOfname(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for name.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfname(final Platform pPlatform) {
    return rawAccumulateAllValuesOfname(new Object[]{pPlatform, null});
  }
  
  @Override
  protected PlatformsByNameMatch tupleToMatch(final Tuple t) {
    try {
      return PlatformsByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform) t.get(POSITION_PLATFORM), (java.lang.String) t.get(POSITION_NAME));
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in tuple not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected PlatformsByNameMatch arrayToMatch(final Object[] match) {
    try {
      return PlatformsByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform) match[POSITION_PLATFORM], (java.lang.String) match[POSITION_NAME]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected PlatformsByNameMatch arrayToMatchMutable(final Object[] match) {
    try {
      return PlatformsByNameMatch.newMutableMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform) match[POSITION_PLATFORM], (java.lang.String) match[POSITION_NAME]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
}
