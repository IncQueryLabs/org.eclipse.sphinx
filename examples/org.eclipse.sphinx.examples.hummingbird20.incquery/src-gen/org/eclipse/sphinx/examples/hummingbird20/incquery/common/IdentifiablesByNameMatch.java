package org.eclipse.sphinx.examples.hummingbird20.incquery.common;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.util.IdentifiablesByNameQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.common.identifiablesByName pattern,
 * to be used in conjunction with {@link IdentifiablesByNameMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see IdentifiablesByNameMatcher
 * @see IdentifiablesByNameProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class IdentifiablesByNameMatch extends BasePatternMatch {
  private Identifiable fIdentifiable;
  
  private String fName;
  
  private static List<String> parameterNames = makeImmutableList("identifiable", "name");
  
  private IdentifiablesByNameMatch(final Identifiable pIdentifiable, final String pName) {
    this.fIdentifiable = pIdentifiable;
    this.fName = pName;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("identifiable".equals(parameterName)) return this.fIdentifiable;
    if ("name".equals(parameterName)) return this.fName;
    return null;
  }
  
  public Identifiable getIdentifiable() {
    return this.fIdentifiable;
  }
  
  public String getName() {
    return this.fName;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("identifiable".equals(parameterName) ) {
    	this.fIdentifiable = (org.eclipse.sphinx.examples.hummingbird20.common.Identifiable) newValue;
    	return true;
    }
    if ("name".equals(parameterName) ) {
    	this.fName = (java.lang.String) newValue;
    	return true;
    }
    return false;
  }
  
  public void setIdentifiable(final Identifiable pIdentifiable) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fIdentifiable = pIdentifiable;
  }
  
  public void setName(final String pName) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fName = pName;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.common.identifiablesByName";
  }
  
  @Override
  public List<String> parameterNames() {
    return IdentifiablesByNameMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fIdentifiable, fName};
  }
  
  @Override
  public IdentifiablesByNameMatch toImmutable() {
    return isMutable() ? newMatch(fIdentifiable, fName) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"identifiable\"=" + prettyPrintValue(fIdentifiable) + ", ");
    
    result.append("\"name\"=" + prettyPrintValue(fName)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fIdentifiable == null) ? 0 : fIdentifiable.hashCode());
    result = prime * result + ((fName == null) ? 0 : fName.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof IdentifiablesByNameMatch)) { // this should be infrequent
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
    IdentifiablesByNameMatch other = (IdentifiablesByNameMatch) obj;
    if (fIdentifiable == null) {if (other.fIdentifiable != null) return false;}
    else if (!fIdentifiable.equals(other.fIdentifiable)) return false;
    if (fName == null) {if (other.fName != null) return false;}
    else if (!fName.equals(other.fName)) return false;
    return true;
  }
  
  @Override
  public IdentifiablesByNameQuerySpecification specification() {
    try {
    	return IdentifiablesByNameQuerySpecification.instance();
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
  public static IdentifiablesByNameMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pIdentifiable the fixed value of pattern parameter identifiable, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static IdentifiablesByNameMatch newMutableMatch(final Identifiable pIdentifiable, final String pName) {
    return new Mutable(pIdentifiable, pName);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pIdentifiable the fixed value of pattern parameter identifiable, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static IdentifiablesByNameMatch newMatch(final Identifiable pIdentifiable, final String pName) {
    return new Immutable(pIdentifiable, pName);
  }
  
  private static final class Mutable extends IdentifiablesByNameMatch {
    Mutable(final Identifiable pIdentifiable, final String pName) {
      super(pIdentifiable, pName);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends IdentifiablesByNameMatch {
    Immutable(final Identifiable pIdentifiable, final String pName) {
      super(pIdentifiable, pName);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
