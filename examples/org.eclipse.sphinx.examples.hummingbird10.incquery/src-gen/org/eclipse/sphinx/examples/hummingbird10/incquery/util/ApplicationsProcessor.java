package org.eclipse.sphinx.examples.hummingbird10.incquery.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird10.Application;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ApplicationsMatch;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird10.incquery.applications pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ApplicationsProcessor implements IMatchProcessor<ApplicationsMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pApp the value of pattern parameter app in the currently processed match
   * 
   */
  public abstract void process(final Application pApp);
  
  @Override
  public void process(final ApplicationsMatch match) {
    process(match.getApp());
    
  }
}
