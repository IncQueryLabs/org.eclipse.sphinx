package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util.ComponentsQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.components pattern,
 * to be used in conjunction with {@link ComponentsMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ComponentsMatcher
 * @see ComponentsProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ComponentsMatch extends BasePatternMatch {
  private Component fComponent;
  
  private static List<String> parameterNames = makeImmutableList("component");
  
  private ComponentsMatch(final Component pComponent) {
    this.fComponent = pComponent;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("component".equals(parameterName)) return this.fComponent;
    return null;
  }
  
  public Component getComponent() {
    return this.fComponent;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("component".equals(parameterName) ) {
    	this.fComponent = (org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component) newValue;
    	return true;
    }
    return false;
  }
  
  public void setComponent(final Component pComponent) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fComponent = pComponent;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.components";
  }
  
  @Override
  public List<String> parameterNames() {
    return ComponentsMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fComponent};
  }
  
  @Override
  public ComponentsMatch toImmutable() {
    return isMutable() ? newMatch(fComponent) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"component\"=" + prettyPrintValue(fComponent)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fComponent == null) ? 0 : fComponent.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ComponentsMatch)) { // this should be infrequent
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
    ComponentsMatch other = (ComponentsMatch) obj;
    if (fComponent == null) {if (other.fComponent != null) return false;}
    else if (!fComponent.equals(other.fComponent)) return false;
    return true;
  }
  
  @Override
  public ComponentsQuerySpecification specification() {
    try {
    	return ComponentsQuerySpecification.instance();
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
  public static ComponentsMatch newEmptyMatch() {
    return new Mutable(null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pComponent the fixed value of pattern parameter component, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ComponentsMatch newMutableMatch(final Component pComponent) {
    return new Mutable(pComponent);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pComponent the fixed value of pattern parameter component, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ComponentsMatch newMatch(final Component pComponent) {
    return new Immutable(pComponent);
  }
  
  private static final class Mutable extends ComponentsMatch {
    Mutable(final Component pComponent) {
      super(pComponent);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ComponentsMatch {
    Immutable(final Component pComponent) {
      super(pComponent);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
