package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ComponentTypesByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.componentTypesByName pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ComponentTypesByNameProcessor implements IMatchProcessor<ComponentTypesByNameMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pComponentType the value of pattern parameter componentType in the currently processed match
   * @param pName the value of pattern parameter name in the currently processed match
   * 
   */
  public abstract void process(final ComponentType pComponentType, final String pName);
  
  @Override
  public void process(final ComponentTypesByNameMatch match) {
    process(match.getComponentType(), match.getName());
    
  }
}
