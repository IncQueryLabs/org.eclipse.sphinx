package org.eclipse.sphinx.examples.hummingbird10.incquery.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird10.Connection;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ConnectionsMatch;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird10.incquery.connections pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ConnectionsProcessor implements IMatchProcessor<ConnectionsMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pConnection the value of pattern parameter connection in the currently processed match
   * 
   */
  public abstract void process(final Connection pConnection);
  
  @Override
  public void process(final ConnectionsMatch match) {
    process(match.getConnection());
    
  }
}
