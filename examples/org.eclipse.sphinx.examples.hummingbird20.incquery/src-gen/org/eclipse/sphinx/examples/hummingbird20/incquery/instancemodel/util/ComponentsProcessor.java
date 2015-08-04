package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ComponentsMatch;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.components pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ComponentsProcessor implements IMatchProcessor<ComponentsMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pComponent the value of pattern parameter component in the currently processed match
   * 
   */
  public abstract void process(final Component pComponent);
  
  @Override
  public void process(final ComponentsMatch match) {
    process(match.getComponent());
  }
}
