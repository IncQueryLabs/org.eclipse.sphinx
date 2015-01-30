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
import org.eclipse.sphinx.examples.hummingbird10.Connection;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ConnectionsMatch;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.ConnectionsQuerySpecification;

/**
 * Generated pattern matcher API of the org.eclipse.sphinx.examples.hummingbird10.incquery.connections pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link ConnectionsMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern connections(connection : Connection) {
 * 	Connection(connection);
 * }
 * </pre></code>
 * 
 * @see ConnectionsMatch
 * @see ConnectionsProcessor
 * @see ConnectionsQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class ConnectionsMatcher extends BaseMatcher<ConnectionsMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<ConnectionsMatcher> querySpecification() throws IncQueryException {
    return ConnectionsQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static ConnectionsMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    ConnectionsMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new ConnectionsMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_CONNECTION = 0;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(ConnectionsMatcher.class);
  
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
  public ConnectionsMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public ConnectionsMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @return matches represented as a ConnectionsMatch object.
   * 
   */
  public Collection<ConnectionsMatch> getAllMatches(final Connection pConnection) {
    return rawGetAllMatches(new Object[]{pConnection});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @return a match represented as a ConnectionsMatch object, or null if no match is found.
   * 
   */
  public ConnectionsMatch getOneArbitraryMatch(final Connection pConnection) {
    return rawGetOneArbitraryMatch(new Object[]{pConnection});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Connection pConnection) {
    return rawHasMatch(new Object[]{pConnection});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Connection pConnection) {
    return rawCountMatches(new Object[]{pConnection});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Connection pConnection, final IMatchProcessor<? super ConnectionsMatch> processor) {
    rawForEachMatch(new Object[]{pConnection}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Connection pConnection, final IMatchProcessor<? super ConnectionsMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pConnection}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<ConnectionsMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Connection pConnection) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pConnection});
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public ConnectionsMatch newMatch(final Connection pConnection) {
    return ConnectionsMatch.newMatch(pConnection);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for connection.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Connection> rawAccumulateAllValuesOfconnection(final Object[] parameters) {
    Set<Connection> results = new HashSet<Connection>();
    rawAccumulateAllValues(POSITION_CONNECTION, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for connection.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Connection> getAllValuesOfconnection() {
    return rawAccumulateAllValuesOfconnection(emptyArray());
  }
  
  @Override
  protected ConnectionsMatch tupleToMatch(final Tuple t) {
    try {
      return ConnectionsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird10.Connection) t.get(POSITION_CONNECTION));
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in tuple not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected ConnectionsMatch arrayToMatch(final Object[] match) {
    try {
      return ConnectionsMatch.newMatch((org.eclipse.sphinx.examples.hummingbird10.Connection) match[POSITION_CONNECTION]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected ConnectionsMatch arrayToMatchMutable(final Object[] match) {
    try {
      return ConnectionsMatch.newMutableMatch((org.eclipse.sphinx.examples.hummingbird10.Connection) match[POSITION_CONNECTION]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
}
