package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.ComponentTypesQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.componentTypes pattern,
 * to be used in conjunction with {@link ComponentTypesMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ComponentTypesMatcher
 * @see ComponentTypesProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ComponentTypesMatch extends BasePatternMatch {
  private ComponentType fComponentType;
  
  private static List<String> parameterNames = makeImmutableList("componentType");
  
  private ComponentTypesMatch(final ComponentType pComponentType) {
    this.fComponentType = pComponentType;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("componentType".equals(parameterName)) return this.fComponentType;
    return null;
    
  }
  
  public ComponentType getComponentType() {
    return this.fComponentType;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("componentType".equals(parameterName) ) {
    	this.fComponentType = (org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setComponentType(final ComponentType pComponentType) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fComponentType = pComponentType;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.componentTypes";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return ComponentTypesMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fComponentType};
    
  }
  
  @Override
  public ComponentTypesMatch toImmutable() {
    return isMutable() ? newMatch(fComponentType) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"componentType\"=" + prettyPrintValue(fComponentType));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fComponentType == null) ? 0 : fComponentType.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ComponentTypesMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    ComponentTypesMatch other = (ComponentTypesMatch) obj;
    if (fComponentType == null) {if (other.fComponentType != null) return false;}
    else if (!fComponentType.equals(other.fComponentType)) return false;
    return true;
  }
  
  @Override
  public ComponentTypesQuerySpecification specification() {
    try {
    	return ComponentTypesQuerySpecification.instance();
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
  public static ComponentTypesMatch newEmptyMatch() {
    return new Mutable(null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pComponentType the fixed value of pattern parameter componentType, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ComponentTypesMatch newMutableMatch(final ComponentType pComponentType) {
    return new Mutable(pComponentType);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pComponentType the fixed value of pattern parameter componentType, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ComponentTypesMatch newMatch(final ComponentType pComponentType) {
    return new Immutable(pComponentType);
    
  }
  
  private static final class Mutable extends ComponentTypesMatch {
    Mutable(final ComponentType pComponentType) {
      super(pComponentType);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ComponentTypesMatch {
    Immutable(final ComponentType pComponentType) {
      super(pComponentType);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
