package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.ParametersByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.parametersByName pattern,
 * to be used in conjunction with {@link ParametersByNameMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ParametersByNameMatcher
 * @see ParametersByNameProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ParametersByNameMatch extends BasePatternMatch {
  private Parameter fParam;
  
  private String fName;
  
  private static List<String> parameterNames = makeImmutableList("param", "name");
  
  private ParametersByNameMatch(final Parameter pParam, final String pName) {
    this.fParam = pParam;
    this.fName = pName;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("param".equals(parameterName)) return this.fParam;
    if ("name".equals(parameterName)) return this.fName;
    return null;
    
  }
  
  public Parameter getParam() {
    return this.fParam;
    
  }
  
  public String getName() {
    return this.fName;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("param".equals(parameterName) ) {
    	this.fParam = (org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter) newValue;
    	return true;
    }
    if ("name".equals(parameterName) ) {
    	this.fName = (java.lang.String) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setParam(final Parameter pParam) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fParam = pParam;
    
  }
  
  public void setName(final String pName) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fName = pName;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.parametersByName";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return ParametersByNameMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fParam, fName};
    
  }
  
  @Override
  public ParametersByNameMatch toImmutable() {
    return isMutable() ? newMatch(fParam, fName) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"param\"=" + prettyPrintValue(fParam) + ", ");
    result.append("\"name\"=" + prettyPrintValue(fName));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fParam == null) ? 0 : fParam.hashCode());
    result = prime * result + ((fName == null) ? 0 : fName.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ParametersByNameMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    ParametersByNameMatch other = (ParametersByNameMatch) obj;
    if (fParam == null) {if (other.fParam != null) return false;}
    else if (!fParam.equals(other.fParam)) return false;
    if (fName == null) {if (other.fName != null) return false;}
    else if (!fName.equals(other.fName)) return false;
    return true;
  }
  
  @Override
  public ParametersByNameQuerySpecification specification() {
    try {
    	return ParametersByNameQuerySpecification.instance();
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
  public static ParametersByNameMatch newEmptyMatch() {
    return new Mutable(null, null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ParametersByNameMatch newMutableMatch(final Parameter pParam, final String pName) {
    return new Mutable(pParam, pName);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ParametersByNameMatch newMatch(final Parameter pParam, final String pName) {
    return new Immutable(pParam, pName);
    
  }
  
  private static final class Mutable extends ParametersByNameMatch {
    Mutable(final Parameter pParam, final String pName) {
      super(pParam, pName);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ParametersByNameMatch {
    Immutable(final Parameter pParam, final String pName) {
      super(pParam, pName);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
