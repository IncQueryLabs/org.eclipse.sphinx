package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.ParametersQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.parameters pattern,
 * to be used in conjunction with {@link ParametersMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ParametersMatcher
 * @see ParametersProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ParametersMatch extends BasePatternMatch {
  private Parameter fParam;
  
  private static List<String> parameterNames = makeImmutableList("param");
  
  private ParametersMatch(final Parameter pParam) {
    this.fParam = pParam;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("param".equals(parameterName)) return this.fParam;
    return null;
    
  }
  
  public Parameter getParam() {
    return this.fParam;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("param".equals(parameterName) ) {
    	this.fParam = (org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setParam(final Parameter pParam) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fParam = pParam;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.parameters";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return ParametersMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fParam};
    
  }
  
  @Override
  public ParametersMatch toImmutable() {
    return isMutable() ? newMatch(fParam) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"param\"=" + prettyPrintValue(fParam));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fParam == null) ? 0 : fParam.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ParametersMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    ParametersMatch other = (ParametersMatch) obj;
    if (fParam == null) {if (other.fParam != null) return false;}
    else if (!fParam.equals(other.fParam)) return false;
    return true;
  }
  
  @Override
  public ParametersQuerySpecification specification() {
    try {
    	return ParametersQuerySpecification.instance();
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
  public static ParametersMatch newEmptyMatch() {
    return new Mutable(null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ParametersMatch newMutableMatch(final Parameter pParam) {
    return new Mutable(pParam);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pParam the fixed value of pattern parameter param, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ParametersMatch newMatch(final Parameter pParam) {
    return new Immutable(pParam);
    
  }
  
  private static final class Mutable extends ParametersMatch {
    Mutable(final Parameter pParam) {
      super(pParam);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ParametersMatch {
    Immutable(final Parameter pParam) {
      super(pParam);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
