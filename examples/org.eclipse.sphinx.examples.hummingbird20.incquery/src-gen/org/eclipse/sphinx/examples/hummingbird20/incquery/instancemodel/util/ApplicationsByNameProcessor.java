package org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.ApplicationsByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.applicationsByName pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ApplicationsByNameProcessor implements IMatchProcessor<ApplicationsByNameMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pApp the value of pattern parameter app in the currently processed match
   * @param pName the value of pattern parameter name in the currently processed match
   * 
   */
  public abstract void process(final Application pApp, final String pName);
  
  @Override
  public void process(final ApplicationsByNameMatch match) {
    process(match.getApp(), match.getName());
  }
}
