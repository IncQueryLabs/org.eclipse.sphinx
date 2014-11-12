package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.PortsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ports pattern,
 * to be used in conjunction with {@link PortsMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see PortsMatcher
 * @see PortsProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class PortsMatch extends BasePatternMatch {
  private Port fPort;
  
  private static List<String> parameterNames = makeImmutableList("port");
  
  private PortsMatch(final Port pPort) {
    this.fPort = pPort;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("port".equals(parameterName)) return this.fPort;
    return null;
    
  }
  
  public Port getPort() {
    return this.fPort;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("port".equals(parameterName) ) {
    	this.fPort = (org.eclipse.sphinx.examples.hummingbird20.typemodel.Port) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setPort(final Port pPort) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fPort = pPort;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ports";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return PortsMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fPort};
    
  }
  
  @Override
  public PortsMatch toImmutable() {
    return isMutable() ? newMatch(fPort) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"port\"=" + prettyPrintValue(fPort));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fPort == null) ? 0 : fPort.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof PortsMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    PortsMatch other = (PortsMatch) obj;
    if (fPort == null) {if (other.fPort != null) return false;}
    else if (!fPort.equals(other.fPort)) return false;
    return true;
  }
  
  @Override
  public PortsQuerySpecification specification() {
    try {
    	return PortsQuerySpecification.instance();
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
  public static PortsMatch newEmptyMatch() {
    return new Mutable(null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static PortsMatch newMutableMatch(final Port pPort) {
    return new Mutable(pPort);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pPort the fixed value of pattern parameter port, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static PortsMatch newMatch(final Port pPort) {
    return new Immutable(pPort);
    
  }
  
  private static final class Mutable extends PortsMatch {
    Mutable(final Port pPort) {
      super(pPort);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends PortsMatch {
    Immutable(final Port pPort) {
      super(pPort);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
