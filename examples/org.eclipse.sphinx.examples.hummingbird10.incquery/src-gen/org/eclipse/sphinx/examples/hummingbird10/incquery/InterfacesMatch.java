package org.eclipse.sphinx.examples.hummingbird10.incquery;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird10.Interface;
import org.eclipse.sphinx.examples.hummingbird10.incquery.util.InterfacesQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird10.incquery.interfaces pattern,
 * to be used in conjunction with {@link InterfacesMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see InterfacesMatcher
 * @see InterfacesProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class InterfacesMatch extends BasePatternMatch {
  private Interface fInterface;
  
  private static List<String> parameterNames = makeImmutableList("interface");
  
  private InterfacesMatch(final Interface pInterface) {
    this.fInterface = pInterface;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("interface".equals(parameterName)) return this.fInterface;
    return null;
    
  }
  
  public Interface getInterface() {
    return this.fInterface;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("interface".equals(parameterName) ) {
    	this.fInterface = (org.eclipse.sphinx.examples.hummingbird10.Interface) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setInterface(final Interface pInterface) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fInterface = pInterface;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird10.incquery.interfaces";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return InterfacesMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fInterface};
    
  }
  
  @Override
  public InterfacesMatch toImmutable() {
    return isMutable() ? newMatch(fInterface) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"interface\"=" + prettyPrintValue(fInterface));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fInterface == null) ? 0 : fInterface.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof InterfacesMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    InterfacesMatch other = (InterfacesMatch) obj;
    if (fInterface == null) {if (other.fInterface != null) return false;}
    else if (!fInterface.equals(other.fInterface)) return false;
    return true;
  }
  
  @Override
  public InterfacesQuerySpecification specification() {
    try {
    	return InterfacesQuerySpecification.instance();
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
  public static InterfacesMatch newEmptyMatch() {
    return new Mutable(null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static InterfacesMatch newMutableMatch(final Interface pInterface) {
    return new Mutable(pInterface);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static InterfacesMatch newMatch(final Interface pInterface) {
    return new Immutable(pInterface);
    
  }
  
  private static final class Mutable extends InterfacesMatch {
    Mutable(final Interface pInterface) {
      super(pInterface);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends InterfacesMatch {
    Immutable(final Interface pInterface) {
      super(pInterface);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
