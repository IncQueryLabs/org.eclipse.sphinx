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
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.InterfacesByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.InterfacesByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;

/**
 * Generated pattern matcher API of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.interfacesByName pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link InterfacesByNameMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern interfacesByName(interface : Interface,name){
 * 	Interface.name(interface, name);
 * }
 * </pre></code>
 * 
 * @see InterfacesByNameMatch
 * @see InterfacesByNameProcessor
 * @see InterfacesByNameQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class InterfacesByNameMatcher extends BaseMatcher<InterfacesByNameMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<InterfacesByNameMatcher> querySpecification() throws IncQueryException {
    return InterfacesByNameQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static InterfacesByNameMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    InterfacesByNameMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new InterfacesByNameMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_INTERFACE = 0;
  
  private final static int POSITION_NAME = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(InterfacesByNameMatcher.class);
  
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
  public InterfacesByNameMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public InterfacesByNameMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return matches represented as a InterfacesByNameMatch object.
   * 
   */
  public Collection<InterfacesByNameMatch> getAllMatches(final Interface pInterface, final String pName) {
    return rawGetAllMatches(new Object[]{pInterface, pName});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return a match represented as a InterfacesByNameMatch object, or null if no match is found.
   * 
   */
  public InterfacesByNameMatch getOneArbitraryMatch(final Interface pInterface, final String pName) {
    return rawGetOneArbitraryMatch(new Object[]{pInterface, pName});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Interface pInterface, final String pName) {
    return rawHasMatch(new Object[]{pInterface, pName});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Interface pInterface, final String pName) {
    return rawCountMatches(new Object[]{pInterface, pName});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Interface pInterface, final String pName, final IMatchProcessor<? super InterfacesByNameMatch> processor) {
    rawForEachMatch(new Object[]{pInterface, pName}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Interface pInterface, final String pName, final IMatchProcessor<? super InterfacesByNameMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pInterface, pName}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<InterfacesByNameMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Interface pInterface, final String pName) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pInterface, pName});
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public InterfacesByNameMatch newMatch(final Interface pInterface, final String pName) {
    return InterfacesByNameMatch.newMatch(pInterface, pName);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for interface.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Interface> rawAccumulateAllValuesOfinterface(final Object[] parameters) {
    Set<Interface> results = new HashSet<Interface>();
    rawAccumulateAllValues(POSITION_INTERFACE, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for interface.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Interface> getAllValuesOfinterface() {
    return rawAccumulateAllValuesOfinterface(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for interface.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Interface> getAllValuesOfinterface(final InterfacesByNameMatch partialMatch) {
    return rawAccumulateAllValuesOfinterface(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for interface.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Interface> getAllValuesOfinterface(final String pName) {
    return rawAccumulateAllValuesOfinterface(new Object[]{null, pName});
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
  public Set<String> getAllValuesOfname(final InterfacesByNameMatch partialMatch) {
    return rawAccumulateAllValuesOfname(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for name.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfname(final Interface pInterface) {
    return rawAccumulateAllValuesOfname(new Object[]{pInterface, null});
  }
  
  @Override
  protected InterfacesByNameMatch tupleToMatch(final Tuple t) {
    try {
      return InterfacesByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface) t.get(POSITION_INTERFACE), (java.lang.String) t.get(POSITION_NAME));
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in tuple not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected InterfacesByNameMatch arrayToMatch(final Object[] match) {
    try {
      return InterfacesByNameMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface) match[POSITION_INTERFACE], (java.lang.String) match[POSITION_NAME]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected InterfacesByNameMatch arrayToMatchMutable(final Object[] match) {
    try {
      return InterfacesByNameMatch.newMutableMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface) match[POSITION_INTERFACE], (java.lang.String) match[POSITION_NAME]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
}
