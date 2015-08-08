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
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ParametersByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.ParametersByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter;

/**
 * Generated pattern matcher API of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.parametersByName pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link ParametersByNameMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern parametersByName(param : Parameter, name){
 * 	Parameter.name(param, name);
 * }
 * </pre></code>
 * 
 * @see ParametersByNameMatch
 * @see ParametersByNameProcessor
 * @see ParametersByNameQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class ParametersByNameMatcher extends BaseMatcher<ParametersByNameMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static ParametersByNameMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    ParametersByNameMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new ParametersByNameMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_PARAM = 0;
  
  private final static int POSITION_NAME = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(ParametersByNameMatcher.class);
  
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
  public ParametersByNameMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public ParametersByNameMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return matches represented as a ParametersByNameMatch object.
   * 
   */
  public Collection<ParametersByNameMatch> getAllMatches(final Parameter pParam, final String pName) {
    return rawGetAllMatches(new Object[]{pParam, pName});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return a match represented as a ParametersByNameMatch object, or null if no match is found.
   * 
   */
  public ParametersByNameMatch getOneArbitraryMatch(final Parameter pParam, final String pName) {
    return rawGetOneArbitraryMatch(new Object[]{pParam, pName});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Parameter pParam, final String pName) {
    return rawHasMatch(new Object[]{pParam, pName});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Parameter pParam, final String pName) {
    return rawCountMatches(new Object[]{pParam, pName});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Parameter pParam, final String pName, final IMatchProcessor<? super ParametersByNameMatch> processor) {
    rawForEachMatch(new Object[]{pParam, pName}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Parameter pParam, final String pName, final IMatchProcessor<? super ParametersByNameMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pParam, pName}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public ParametersByNameMatch newMatch(final Parameter pParam, final String pName) {
    return ParametersByNameMatch.newMatch(pParam, pName);
  }
  
  /**
   * Retrieve the set of values that occur in matches for param.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Parameter> rawAccumulateAllValuesOfparam(final Object[] parameters) {
    Set<Parameter> results = new HashSet<Parameter>();
    rawAccumulateAllValues(POSITION_PARAM, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for param.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Parameter> getAllValuesOfparam() {
    return rawAccumulateAllValuesOfparam(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for param.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Parameter> getAllValuesOfparam(final ParametersByNameMatch partialMatch) {
    return rawAccumulateAllValuesOfparam(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for param.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Parameter> getAllValuesOfparam(final String pName) {
    return rawAccumulateAllValuesOfparam(new Object[]{
    null, 
    pName
    });
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
  public Set<String> getAllValuesOfname(final ParametersByNameMatch partialMatch) {
    return rawAccumulateAllValuesOfname(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for name.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfname(final Parameter pParam) {
    return rawAccumulateAllValuesOfname(new Object[]{
    pParam, 
    null
    });
  }
  
  @Override
  protected ParametersByNameMatch tupleToMatch(final Tuple t) {
    try {
    	return ParametersByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter) t.get(POSITION_PARAM), (java.lang.String) t.get(POSITION_NAME));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected ParametersByNameMatch arrayToMatch(final Object[] match) {
    try {
    	return ParametersByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter) match[POSITION_PARAM], (java.lang.String) match[POSITION_NAME]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected ParametersByNameMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return ParametersByNameMatch.newMutableMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter) match[POSITION_PARAM], (java.lang.String) match[POSITION_NAME]);
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
  public static IQuerySpecification<ParametersByNameMatcher> querySpecification() throws IncQueryException {
    return ParametersByNameQuerySpecification.instance();
  }
}
