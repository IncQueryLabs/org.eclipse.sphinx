package org.eclipse.sphinx.examples.hummingbird10.incquery.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird10.Parameter;
import org.eclipse.sphinx.examples.hummingbird10.incquery.ParametersMatch;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird10.incquery.parameters pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ParametersProcessor implements IMatchProcessor<ParametersMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pParameter the value of pattern parameter parameter in the currently processed match
   * 
   */
  public abstract void process(final Parameter pParameter);
  
  @Override
  public void process(final ParametersMatch match) {
    process(match.getParameter());
    
  }
}
