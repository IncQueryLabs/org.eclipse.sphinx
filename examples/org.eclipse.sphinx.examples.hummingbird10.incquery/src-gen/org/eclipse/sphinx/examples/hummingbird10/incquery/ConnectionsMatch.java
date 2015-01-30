package org.eclipse.sphinx.examples.hummingbird10.incquery;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird10.Connection;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.ConnectionsQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird10.incquery.connections pattern,
 * to be used in conjunction with {@link ConnectionsMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ConnectionsMatcher
 * @see ConnectionsProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ConnectionsMatch extends BasePatternMatch {
  private Connection fConnection;
  
  private static List<String> parameterNames = makeImmutableList("connection");
  
  private ConnectionsMatch(final Connection pConnection) {
    this.fConnection = pConnection;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("connection".equals(parameterName)) return this.fConnection;
    return null;
    
  }
  
  public Connection getConnection() {
    return this.fConnection;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("connection".equals(parameterName) ) {
    	this.fConnection = (org.eclipse.sphinx.examples.hummingbird10.Connection) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setConnection(final Connection pConnection) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fConnection = pConnection;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird10.incquery.connections";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return ConnectionsMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fConnection};
    
  }
  
  @Override
  public ConnectionsMatch toImmutable() {
    return isMutable() ? newMatch(fConnection) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"connection\"=" + prettyPrintValue(fConnection));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fConnection == null) ? 0 : fConnection.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ConnectionsMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    ConnectionsMatch other = (ConnectionsMatch) obj;
    if (fConnection == null) {if (other.fConnection != null) return false;}
    else if (!fConnection.equals(other.fConnection)) return false;
    return true;
  }
  
  @Override
  public ConnectionsQuerySpecification specification() {
    try {
    	return ConnectionsQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  /**
   * Returns an empty, mutable match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @return the empty match.
   * 
   */
  public static ConnectionsMatch newEmptyMatch() {
    return new Mutable(null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ConnectionsMatch newMutableMatch(final Connection pConnection) {
    return new Mutable(pConnection);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ConnectionsMatch newMatch(final Connection pConnection) {
    return new Immutable(pConnection);
    
  }
  
  private static final class Mutable extends ConnectionsMatch {
    Mutable(final Connection pConnection) {
      super(pConnection);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ConnectionsMatch {
    Immutable(final Connection pConnection) {
      super(pConnection);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
