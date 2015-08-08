package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ConnectionsByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Connection;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.connectionsByName pattern,
 * to be used in conjunction with {@link ConnectionsByNameMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ConnectionsByNameMatcher
 * @see ConnectionsByNameProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ConnectionsByNameMatch extends BasePatternMatch {
  private Connection fConnection;
  
  private String fName;
  
  private static List<String> parameterNames = makeImmutableList("connection", "name");
  
  private ConnectionsByNameMatch(final Connection pConnection, final String pName) {
    this.fConnection = pConnection;
    this.fName = pName;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("connection".equals(parameterName)) return this.fConnection;
    if ("name".equals(parameterName)) return this.fName;
    return null;
  }
  
  public Connection getConnection() {
    return this.fConnection;
  }
  
  public String getName() {
    return this.fName;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("connection".equals(parameterName) ) {
    	this.fConnection = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Connection) newValue;
    	return true;
    }
    if ("name".equals(parameterName) ) {
    	this.fName = (java.lang.String) newValue;
    	return true;
    }
    return false;
  }
  
  public void setConnection(final Connection pConnection) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fConnection = pConnection;
  }
  
  public void setName(final String pName) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fName = pName;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.connectionsByName";
  }
  
  @Override
  public List<String> parameterNames() {
    return ConnectionsByNameMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fConnection, fName};
  }
  
  @Override
  public ConnectionsByNameMatch toImmutable() {
    return isMutable() ? newMatch(fConnection, fName) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"connection\"=" + prettyPrintValue(fConnection) + ", ");
    
    result.append("\"name\"=" + prettyPrintValue(fName)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fConnection == null) ? 0 : fConnection.hashCode());
    result = prime * result + ((fName == null) ? 0 : fName.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ConnectionsByNameMatch)) { // this should be infrequent
    	if (obj == null) {
    		return false;
    	}
    	if (!(obj instanceof IPatternMatch)) {
    		return false;
    	}
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    ConnectionsByNameMatch other = (ConnectionsByNameMatch) obj;
    if (fConnection == null) {if (other.fConnection != null) return false;}
    else if (!fConnection.equals(other.fConnection)) return false;
    if (fName == null) {if (other.fName != null) return false;}
    else if (!fName.equals(other.fName)) return false;
    return true;
  }
  
  @Override
  public ConnectionsByNameQuerySpecification specification() {
    try {
    	return ConnectionsByNameQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException (ex);
    }
  }
  
  /**
   * Returns an empty, mutable match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @return the empty match.
   * 
   */
  public static ConnectionsByNameMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ConnectionsByNameMatch newMutableMatch(final Connection pConnection, final String pName) {
    return new Mutable(pConnection, pName);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pConnection the fixed value of pattern parameter connection, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ConnectionsByNameMatch newMatch(final Connection pConnection, final String pName) {
    return new Immutable(pConnection, pName);
  }
  
  private static final class Mutable extends ConnectionsByNameMatch {
    Mutable(final Connection pConnection, final String pName) {
      super(pConnection, pName);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ConnectionsByNameMatch {
    Immutable(final Connection pConnection, final String pName) {
      super(pConnection, pName);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
