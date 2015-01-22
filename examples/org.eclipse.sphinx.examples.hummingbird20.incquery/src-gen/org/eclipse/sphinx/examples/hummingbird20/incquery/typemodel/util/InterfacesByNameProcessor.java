package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.InterfacesByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.interfacesByName pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class InterfacesByNameProcessor implements IMatchProcessor<InterfacesByNameMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pInterface the value of pattern parameter interface in the currently processed match
   * @param pName the value of pattern parameter name in the currently processed match
   * 
   */
  public abstract void process(final Interface pInterface, final String pName);
  
  @Override
  public void process(final InterfacesByNameMatch match) {
    process(match.getInterface(), match.getName());
    
  }
}
