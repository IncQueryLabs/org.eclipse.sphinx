package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ComponentsByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.componentsByName pattern,
 * to be used in conjunction with {@link ComponentsByNameMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ComponentsByNameMatcher
 * @see ComponentsByNameProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ComponentsByNameMatch extends BasePatternMatch {
  private Component fComponent;
  
  private String fName;
  
  private static List<String> parameterNames = makeImmutableList("component", "name");
  
  private ComponentsByNameMatch(final Component pComponent, final String pName) {
    this.fComponent = pComponent;
    this.fName = pName;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("component".equals(parameterName)) return this.fComponent;
    if ("name".equals(parameterName)) return this.fName;
    return null;
  }
  
  public Component getComponent() {
    return this.fComponent;
  }
  
  public String getName() {
    return this.fName;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("component".equals(parameterName) ) {
    	this.fComponent = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component) newValue;
    	return true;
    }
    if ("name".equals(parameterName) ) {
    	this.fName = (java.lang.String) newValue;
    	return true;
    }
    return false;
  }
  
  public void setComponent(final Component pComponent) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fComponent = pComponent;
  }
  
  public void setName(final String pName) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fName = pName;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.componentsByName";
  }
  
  @Override
  public List<String> parameterNames() {
    return ComponentsByNameMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fComponent, fName};
  }
  
  @Override
  public ComponentsByNameMatch toImmutable() {
    return isMutable() ? newMatch(fComponent, fName) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"component\"=" + prettyPrintValue(fComponent) + ", ");
    
    result.append("\"name\"=" + prettyPrintValue(fName)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fComponent == null) ? 0 : fComponent.hashCode());
    result = prime * result + ((fName == null) ? 0 : fName.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ComponentsByNameMatch)) { // this should be infrequent
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
    ComponentsByNameMatch other = (ComponentsByNameMatch) obj;
    if (fComponent == null) {if (other.fComponent != null) return false;}
    else if (!fComponent.equals(other.fComponent)) return false;
    if (fName == null) {if (other.fName != null) return false;}
    else if (!fName.equals(other.fName)) return false;
    return true;
  }
  
  @Override
  public ComponentsByNameQuerySpecification specification() {
    try {
    	return ComponentsByNameQuerySpecification.instance();
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
  public static ComponentsByNameMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pComponent the fixed value of pattern parameter component, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ComponentsByNameMatch newMutableMatch(final Component pComponent, final String pName) {
    return new Mutable(pComponent, pName);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pComponent the fixed value of pattern parameter component, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ComponentsByNameMatch newMatch(final Component pComponent, final String pName) {
    return new Immutable(pComponent, pName);
  }
  
  private static final class Mutable extends ComponentsByNameMatch {
    Mutable(final Component pComponent, final String pName) {
      super(pComponent, pName);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ComponentsByNameMatch {
    Immutable(final Component pComponent, final String pName) {
      super(pComponent, pName);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
