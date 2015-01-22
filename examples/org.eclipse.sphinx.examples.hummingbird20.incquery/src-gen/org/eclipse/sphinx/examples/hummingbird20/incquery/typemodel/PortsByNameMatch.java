package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.PortsByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.portsByName pattern,
 * to be used in conjunction with {@link PortsByNameMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see PortsByNameMatcher
 * @see PortsByNameProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class PortsByNameMatch extends BasePatternMatch {
  private Port fPort;
  
  private String fName;
  
  private static List<String> parameterNames = makeImmutableList("port", "name");
  
  private PortsByNameMatch(final Port pPort, final String pName) {
    this.fPort = pPort;
    this.fName = pName;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("port".equals(parameterName)) return this.fPort;
    if ("name".equals(parameterName)) return this.fName;
    return null;
    
  }
  
  public Port getPort() {
    return this.fPort;
    
  }
  
  public String getName() {
    return this.fName;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("port".equals(parameterName) ) {
    	this.fPort = (org.eclipse.sphinx.examples.hummingbird20.typemodel.Port) newValue;
    	return true;
    }
    if ("name".equals(parameterName) ) {
    	this.fName = (java.lang.String) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setPort(final Port pPort) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fPort = pPort;
    
  }
  
  public void setName(final String pName) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fName = pName;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.portsByName";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return PortsByNameMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fPort, fName};
    
  }
  
  @Override
  public PortsByNameMatch toImmutable() {
    return isMutable() ? newMatch(fPort, fName) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"port\"=" + prettyPrintValue(fPort) + ", ");
    result.append("\"name\"=" + prettyPrintValue(fName));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fPort == null) ? 0 : fPort.hashCode());
    result = prime * result + ((fName == null) ? 0 : fName.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof PortsByNameMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    PortsByNameMatch other = (PortsByNameMatch) obj;
    if (fPort == null) {if (other.fPort != null) return false;}
    else if (!fPort.equals(other.fPort)) return false;
    if (fName == null) {if (other.fName != null) return false;}
    else if (!fName.equals(other.fName)) return false;
    return true;
  }
  
  @Override
  public PortsByNameQuerySpecification specification() {
    try {
    	return PortsByNameQuerySpecification.instance();
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
  public static PortsByNameMatch newEmptyMatch() {
    return new Mutable(null, null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static PortsByNameMatch newMutableMatch(final Port pPort, final String pName) {
    return new Mutable(pPort, pName);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static PortsByNameMatch newMatch(final Port pPort, final String pName) {
    return new Immutable(pPort, pName);
    
  }
  
  private static final class Mutable extends PortsByNameMatch {
    Mutable(final Port pPort, final String pName) {
      super(pPort, pName);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends PortsByNameMatch {
    Immutable(final Port pPort, final String pName) {
      super(pPort, pName);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
