package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ComponentTypesMatch;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.componentTypes pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ComponentTypesProcessor implements IMatchProcessor<ComponentTypesMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pComponentType the value of pattern parameter componentType in the currently processed match
   * 
   */
  public abstract void process(final ComponentType pComponentType);
  
  @Override
  public void process(final ComponentTypesMatch match) {
    process(match.getComponentType());
  }
}
