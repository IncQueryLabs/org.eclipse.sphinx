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
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PortsMatch;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.PortsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;

/**
 * Generated pattern matcher API of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ports pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link PortsMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern ports(port : Port){
 * 	Port(port);
 * }
 * </pre></code>
 * 
 * @see PortsMatch
 * @see PortsProcessor
 * @see PortsQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class PortsMatcher extends BaseMatcher<PortsMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static PortsMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    PortsMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new PortsMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_PORT = 0;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(PortsMatcher.class);
  
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
  public PortsMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public PortsMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @return matches represented as a PortsMatch object.
   * 
   */
  public Collection<PortsMatch> getAllMatches(final Port pPort) {
    return rawGetAllMatches(new Object[]{pPort});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @return a match represented as a PortsMatch object, or null if no match is found.
   * 
   */
  public PortsMatch getOneArbitraryMatch(final Port pPort) {
    return rawGetOneArbitraryMatch(new Object[]{pPort});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Port pPort) {
    return rawHasMatch(new Object[]{pPort});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Port pPort) {
    return rawCountMatches(new Object[]{pPort});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Port pPort, final IMatchProcessor<? super PortsMatch> processor) {
    rawForEachMatch(new Object[]{pPort}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Port pPort, final IMatchProcessor<? super PortsMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pPort}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public PortsMatch newMatch(final Port pPort) {
    return PortsMatch.newMatch(pPort);
  }
  
  /**
   * Retrieve the set of values that occur in matches for port.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Port> rawAccumulateAllValuesOfport(final Object[] parameters) {
    Set<Port> results = new HashSet<Port>();
    rawAccumulateAllValues(POSITION_PORT, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for port.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Port> getAllValuesOfport() {
    return rawAccumulateAllValuesOfport(emptyArray());
  }
  
  @Override
  protected PortsMatch tupleToMatch(final Tuple t) {
    try {
    	return PortsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Port) t.get(POSITION_PORT));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected PortsMatch arrayToMatch(final Object[] match) {
    try {
    	return PortsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Port) match[POSITION_PORT]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected PortsMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return PortsMatch.newMutableMatch((org.eclipse.sphinx.examples.hummingbird20.typemodel.Port) match[POSITION_PORT]);
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
  public static IQuerySpecification<PortsMatcher> querySpecification() throws IncQueryException {
    return PortsQuerySpecification.instance();
  }
}
