package org.eclipse.sphinx.examples.hummingbird10.incquery;

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
import org.eclipse.sphinx.examples.hummingbird10.Parameter;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ParametersMatch;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.ParametersQuerySpecification;

/**
 * Generated pattern matcher API of the org.eclipse.sphinx.examples.hummingbird10.incquery.parameters pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link ParametersMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern parameters(parameter : Parameter) {
 * 	Parameter(parameter);
 * }
 * </pre></code>
 * 
 * @see ParametersMatch
 * @see ParametersProcessor
 * @see ParametersQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class ParametersMatcher extends BaseMatcher<ParametersMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<ParametersMatcher> querySpecification() throws IncQueryException {
    return ParametersQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static ParametersMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    ParametersMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new ParametersMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_PARAMETER = 0;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(ParametersMatcher.class);
  
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
  public ParametersMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public ParametersMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pParameter the fixed value of pattern parameter parameter, or null if not bound.
   * @return matches represented as a ParametersMatch object.
   * 
   */
  public Collection<ParametersMatch> getAllMatches(final Parameter pParameter) {
    return rawGetAllMatches(new Object[]{pParameter});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pParameter the fixed value of pattern parameter parameter, or null if not bound.
   * @return a match represented as a ParametersMatch object, or null if no match is found.
   * 
   */
  public ParametersMatch getOneArbitraryMatch(final Parameter pParameter) {
    return rawGetOneArbitraryMatch(new Object[]{pParameter});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pParameter the fixed value of pattern parameter parameter, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Parameter pParameter) {
    return rawHasMatch(new Object[]{pParameter});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pParameter the fixed value of pattern parameter parameter, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Parameter pParameter) {
    return rawCountMatches(new Object[]{pParameter});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pParameter the fixed value of pattern parameter parameter, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Parameter pParameter, final IMatchProcessor<? super ParametersMatch> processor) {
    rawForEachMatch(new Object[]{pParameter}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pParameter the fixed value of pattern parameter parameter, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Parameter pParameter, final IMatchProcessor<? super ParametersMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pParameter}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pParameter the fixed value of pattern parameter parameter, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<ParametersMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Parameter pParameter) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pParameter});
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pParameter the fixed value of pattern parameter parameter, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public ParametersMatch newMatch(final Parameter pParameter) {
    return ParametersMatch.newMatch(pParameter);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for parameter.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Parameter> rawAccumulateAllValuesOfparameter(final Object[] parameters) {
    Set<Parameter> results = new HashSet<Parameter>();
    rawAccumulateAllValues(POSITION_PARAMETER, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for parameter.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Parameter> getAllValuesOfparameter() {
    return rawAccumulateAllValuesOfparameter(emptyArray());
  }
  
  @Override
  protected ParametersMatch tupleToMatch(final Tuple t) {
    try {
      return ParametersMatch.newMatch((org.eclipse.sphinx.examples.hummingbird10.Parameter) t.get(POSITION_PARAMETER));
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in tuple not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected ParametersMatch arrayToMatch(final Object[] match) {
    try {
      return ParametersMatch.newMatch((org.eclipse.sphinx.examples.hummingbird10.Parameter) match[POSITION_PARAMETER]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected ParametersMatch arrayToMatchMutable(final Object[] match) {
    try {
      return ParametersMatch.newMutableMatch((org.eclipse.sphinx.examples.hummingbird10.Parameter) match[POSITION_PARAMETER]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
}
