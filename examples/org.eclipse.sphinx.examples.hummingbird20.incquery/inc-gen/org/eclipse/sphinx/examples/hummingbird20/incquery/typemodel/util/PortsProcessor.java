package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PortsMatch;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ports pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class PortsProcessor implements IMatchProcessor<PortsMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pPort the value of pattern parameter port in the currently processed match
   * 
   */
  public abstract void process(final Port pPort);
  
  @Override
  public void process(final PortsMatch match) {
    process(match.getPort());
    
  }
}
