package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.InterfacesByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.interfacesByName pattern,
 * to be used in conjunction with {@link InterfacesByNameMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see InterfacesByNameMatcher
 * @see InterfacesByNameProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class InterfacesByNameMatch extends BasePatternMatch {
  private Interface fInterface;
  
  private String fName;
  
  private static List<String> parameterNames = makeImmutableList("interface", "name");
  
  private InterfacesByNameMatch(final Interface pInterface, final String pName) {
    this.fInterface = pInterface;
    this.fName = pName;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("interface".equals(parameterName)) return this.fInterface;
    if ("name".equals(parameterName)) return this.fName;
    return null;
    
  }
  
  public Interface getInterface() {
    return this.fInterface;
    
  }
  
  public String getName() {
    return this.fName;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("interface".equals(parameterName) ) {
    	this.fInterface = (org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface) newValue;
    	return true;
    }
    if ("name".equals(parameterName) ) {
    	this.fName = (java.lang.String) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setInterface(final Interface pInterface) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fInterface = pInterface;
    
  }
  
  public void setName(final String pName) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fName = pName;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.interfacesByName";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return InterfacesByNameMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fInterface, fName};
    
  }
  
  @Override
  public InterfacesByNameMatch toImmutable() {
    return isMutable() ? newMatch(fInterface, fName) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"interface\"=" + prettyPrintValue(fInterface) + ", ");
    result.append("\"name\"=" + prettyPrintValue(fName));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fInterface == null) ? 0 : fInterface.hashCode());
    result = prime * result + ((fName == null) ? 0 : fName.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof InterfacesByNameMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    InterfacesByNameMatch other = (InterfacesByNameMatch) obj;
    if (fInterface == null) {if (other.fInterface != null) return false;}
    else if (!fInterface.equals(other.fInterface)) return false;
    if (fName == null) {if (other.fName != null) return false;}
    else if (!fName.equals(other.fName)) return false;
    return true;
  }
  
  @Override
  public InterfacesByNameQuerySpecification specification() {
    try {
    	return InterfacesByNameQuerySpecification.instance();
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
  public static InterfacesByNameMatch newEmptyMatch() {
    return new Mutable(null, null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pInterface the fixed value of pattern parameter interface, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static InterfacesByNameMatch newMutableMatch(final Interface pInterface, final String pName) {
    return new Mutable(pInterface, pName);
    
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
  public static InterfacesByNameMatch newMatch(final Interface pInterface, final String pName) {
    return new Immutable(pInterface, pName);
    
  }
  
  private static final class Mutable extends InterfacesByNameMatch {
    Mutable(final Interface pInterface, final String pName) {
      super(pInterface, pName);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends InterfacesByNameMatch {
    Immutable(final Interface pInterface, final String pName) {
      super(pInterface, pName);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
