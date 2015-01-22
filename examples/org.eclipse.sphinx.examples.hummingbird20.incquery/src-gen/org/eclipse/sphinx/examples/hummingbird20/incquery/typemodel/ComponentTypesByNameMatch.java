package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util.ComponentTypesByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;

/**
 * Pattern-specific match representation of the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.componentTypesByName pattern,
 * to be used in conjunction with {@link ComponentTypesByNameMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ComponentTypesByNameMatcher
 * @see ComponentTypesByNameProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ComponentTypesByNameMatch extends BasePatternMatch {
  private ComponentType fComponentType;
  
  private String fName;
  
  private static List<String> parameterNames = makeImmutableList("componentType", "name");
  
  private ComponentTypesByNameMatch(final ComponentType pComponentType, final String pName) {
    this.fComponentType = pComponentType;
    this.fName = pName;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("componentType".equals(parameterName)) return this.fComponentType;
    if ("name".equals(parameterName)) return this.fName;
    return null;
    
  }
  
  public ComponentType getComponentType() {
    return this.fComponentType;
    
  }
  
  public String getName() {
    return this.fName;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("componentType".equals(parameterName) ) {
    	this.fComponentType = (org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType) newValue;
    	return true;
    }
    if ("name".equals(parameterName) ) {
    	this.fName = (java.lang.String) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setComponentType(final ComponentType pComponentType) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fComponentType = pComponentType;
    
  }
  
  public void setName(final String pName) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fName = pName;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.componentTypesByName";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return ComponentTypesByNameMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fComponentType, fName};
    
  }
  
  @Override
  public ComponentTypesByNameMatch toImmutable() {
    return isMutable() ? newMatch(fComponentType, fName) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"componentType\"=" + prettyPrintValue(fComponentType) + ", ");
    result.append("\"name\"=" + prettyPrintValue(fName));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fComponentType == null) ? 0 : fComponentType.hashCode());
    result = prime * result + ((fName == null) ? 0 : fName.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ComponentTypesByNameMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    ComponentTypesByNameMatch other = (ComponentTypesByNameMatch) obj;
    if (fComponentType == null) {if (other.fComponentType != null) return false;}
    else if (!fComponentType.equals(other.fComponentType)) return false;
    if (fName == null) {if (other.fName != null) return false;}
    else if (!fName.equals(other.fName)) return false;
    return true;
  }
  
  @Override
  public ComponentTypesByNameQuerySpecification specification() {
    try {
    	return ComponentTypesByNameQuerySpecification.instance();
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
  public static ComponentTypesByNameMatch newEmptyMatch() {
    return new Mutable(null, null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pComponentType the fixed value of pattern parameter componentType, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ComponentTypesByNameMatch newMutableMatch(final ComponentType pComponentType, final String pName) {
    return new Mutable(pComponentType, pName);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pComponentType the fixed value of pattern parameter componentType, or null if not bound.
   * @param pName the fixed value of pattern parameter name, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ComponentTypesByNameMatch newMatch(final ComponentType pComponentType, final String pName) {
    return new Immutable(pComponentType, pName);
    
  }
  
  private static final class Mutable extends ComponentTypesByNameMatch {
    Mutable(final ComponentType pComponentType, final String pName) {
      super(pComponentType, pName);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ComponentTypesByNameMatch {
    Immutable(final ComponentType pComponentType, final String pName) {
      super(pComponentType, pName);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
