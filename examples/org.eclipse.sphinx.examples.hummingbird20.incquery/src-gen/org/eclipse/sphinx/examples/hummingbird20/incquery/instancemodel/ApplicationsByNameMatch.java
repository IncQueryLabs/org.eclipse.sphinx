package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ApplicationsByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.applicationsByName pattern,
 * to be used in conjunction with {@link ApplicationsByNameMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ApplicationsByNameMatcher
 * @see ApplicationsByNameProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ApplicationsByNameMatch extends BasePatternMatch {
  private Application fApp;
  
  private String fName;
  
  private static List<String> parameterNames = makeImmutableList("app", "name");
  
  private ApplicationsByNameMatch(final Application pApp, final String pName) {
    this.fApp = pApp;
    this.fName = pName;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("app".equals(parameterName)) return this.fApp;
    if ("name".equals(parameterName)) return this.fName;
    return null;
  }
  
  public Application getApp() {
    return this.fApp;
  }
  
  public String getName() {
    return this.fName;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("app".equals(parameterName) ) {
    	this.fApp = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application) newValue;
    	return true;
    }
    if ("name".equals(parameterName) ) {
    	this.fName = (java.lang.String) newValue;
    	return true;
    }
    return false;
  }
  
  public void setApp(final Application pApp) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fApp = pApp;
  }
  
  public void setName(final String pName) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fName = pName;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.applicationsByName";
  }
  
  @Override
  public List<String> parameterNames() {
    return ApplicationsByNameMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fApp, fName};
  }
  
  @Override
  public ApplicationsByNameMatch toImmutable() {
    return isMutable() ? newMatch(fApp, fName) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"app\"=" + prettyPrintValue(fApp) + ", ");
    
    result.append("\"name\"=" + prettyPrintValue(fName)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fApp == null) ? 0 : fApp.hashCode());
    result = prime * result + ((fName == null) ? 0 : fName.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ApplicationsByNameMatch)) { // this should be infrequent
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
    ApplicationsByNameMatch other = (ApplicationsByNameMatch) obj;
    if (fApp == null) {if (other.fApp != null) return false;}
    else if (!fApp.equals(other.fApp)) return false;
    if (fName == null) {if (other.fName != null) return false;}
    else if (!fName.equals(other.fName)) return false;
    return true;
  }
  
  @Override
  public ApplicationsByNameQuerySpecification specification() {
    try {
    	return ApplicationsByNameQuerySpecification.instance();
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
  public static ApplicationsByNameMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ApplicationsByNameMatch newMutableMatch(final Application pApp, final String pName) {
    return new Mutable(pApp, pName);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pApp the fixed value of pattern parameter app, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ApplicationsByNameMatch newMatch(final Application pApp, final String pName) {
    return new Immutable(pApp, pName);
  }
  
  private static final class Mutable extends ApplicationsByNameMatch {
    Mutable(final Application pApp, final String pName) {
      super(pApp, pName);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ApplicationsByNameMatch {
    Immutable(final Application pApp, final String pName) {
      super(pApp, pName);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
