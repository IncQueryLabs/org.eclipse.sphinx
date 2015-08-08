package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.PlatformsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.platforms pattern,
 * to be used in conjunction with {@link PlatformsMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see PlatformsMatcher
 * @see PlatformsProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class PlatformsMatch extends BasePatternMatch {
  private Platform fPlatform;
  
  private static List<String> parameterNames = makeImmutableList("platform");
  
  private PlatformsMatch(final Platform pPlatform) {
    this.fPlatform = pPlatform;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("platform".equals(parameterName)) return this.fPlatform;
    return null;
  }
  
  public Platform getPlatform() {
    return this.fPlatform;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("platform".equals(parameterName) ) {
    	this.fPlatform = (org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform) newValue;
    	return true;
    }
    return false;
  }
  
  public void setPlatform(final Platform pPlatform) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fPlatform = pPlatform;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.platforms";
  }
  
  @Override
  public List<String> parameterNames() {
    return PlatformsMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fPlatform};
  }
  
  @Override
  public PlatformsMatch toImmutable() {
    return isMutable() ? newMatch(fPlatform) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"platform\"=" + prettyPrintValue(fPlatform)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fPlatform == null) ? 0 : fPlatform.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof PlatformsMatch)) { // this should be infrequent
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
    PlatformsMatch other = (PlatformsMatch) obj;
    if (fPlatform == null) {if (other.fPlatform != null) return false;}
    else if (!fPlatform.equals(other.fPlatform)) return false;
    return true;
  }
  
  @Override
  public PlatformsQuerySpecification specification() {
    try {
    	return PlatformsQuerySpecification.instance();
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
  public static PlatformsMatch newEmptyMatch() {
    return new Mutable(null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static PlatformsMatch newMutableMatch(final Platform pPlatform) {
    return new Mutable(pPlatform);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pPlatform the fixed value of pattern parameter platform, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static PlatformsMatch newMatch(final Platform pPlatform) {
    return new Immutable(pPlatform);
  }
  
  private static final class Mutable extends PlatformsMatch {
    Mutable(final Platform pPlatform) {
      super(pPlatform);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends PlatformsMatch {
    Immutable(final Platform pPlatform) {
      super(pPlatform);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
