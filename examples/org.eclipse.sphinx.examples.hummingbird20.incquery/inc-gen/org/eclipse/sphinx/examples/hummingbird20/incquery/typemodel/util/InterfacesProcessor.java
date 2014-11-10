package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.InterfacesMatch;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.interfaces pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class InterfacesProcessor implements IMatchProcessor<InterfacesMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pInterface the value of pattern parameter interface in the currently processed match
   * 
   */
  public abstract void process(final Interface pInterface);
  
  @Override
  public void process(final InterfacesMatch match) {
    process(match.getInterface());
    
  }
}
