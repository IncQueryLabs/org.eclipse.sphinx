package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.PlatformsByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.platformsByName pattern,
 * to be used in conjunction with {@link PlatformsByNameMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see PlatformsByNameMatcher
 * @see PlatformsByNameProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class PlatformsByNameMatch extends BasePatternMatch {
  private Platform fPlatform;
  
  private String fName;
  
  private static List<String> parameterNames = makeImmutableList("platform", "name");
  
  private PlatformsByNameMatch(final Platform pPlatform, final String pName) {
    this.fPlatform = pPlatform;
    this.fName = pName;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("platform".equals(parameterName)) return this.fPlatform;
    if ("name".equals(parameterName)) return this.fName;
    return null;
    
  }
  
  public Platform getPlatform() {
    return this.fPlatform;
    
  }
  
  public String getName() {
    return this.fName;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("platform".equals(parameterName) ) {
    	this.fPlatform = (org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform) newValue;
    	return true;
    }
    if ("name".equals(parameterName) ) {
    	this.fName = (java.lang.String) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setPlatform(final Platform pPlatform) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fPlatform = pPlatform;
    
  }
  
  public void setName(final String pName) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fName = pName;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.platformsByName";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return PlatformsByNameMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fPlatform, fName};
    
  }
  
  @Override
  public PlatformsByNameMatch toImmutable() {
    return isMutable() ? newMatch(fPlatform, fName) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"platform\"=" + prettyPrintValue(fPlatform) + ", ");
    result.append("\"name\"=" + prettyPrintValue(fName));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fPlatform == null) ? 0 : fPlatform.hashCode());
    result = prime * result + ((fName == null) ? 0 : fName.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof PlatformsByNameMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    PlatformsByNameMatch other = (PlatformsByNameMatch) obj;
    if (fPlatform == null) {if (other.fPlatform != null) return false;}
    else if (!fPlatform.equals(other.fPlatform)) return false;
    if (fName == null) {if (other.fName != null) return false;}
    else if (!fName.equals(other.fName)) return false;
    return true;
  }
  
  @Override
  public PlatformsByNameQuerySpecification specification() {
    try {
    	return PlatformsByNameQuerySpecification.instance();
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
  public static PlatformsByNameMatch newEmptyMatch() {
    return new Mutable(null, null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static PlatformsByNameMatch newMutableMatch(final Platform pPlatform, final String pName) {
    return new Mutable(pPlatform, pName);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static PlatformsByNameMatch newMatch(final Platform pPlatform, final String pName) {
    return new Immutable(pPlatform, pName);
    
  }
  
  private static final class Mutable extends PlatformsByNameMatch {
    Mutable(final Platform pPlatform, final String pName) {
      super(pPlatform, pName);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends PlatformsByNameMatch {
    Immutable(final Platform pPlatform, final String pName) {
      super(pPlatform, pName);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
