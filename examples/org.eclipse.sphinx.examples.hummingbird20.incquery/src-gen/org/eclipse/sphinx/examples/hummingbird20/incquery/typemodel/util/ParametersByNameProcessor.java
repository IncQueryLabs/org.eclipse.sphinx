package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.ParametersByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.parametersByName pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ParametersByNameProcessor implements IMatchProcessor<ParametersByNameMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pParam the value of pattern parameter param in the currently processed match
   * @param pName the value of pattern parameter name in the currently processed match
   * 
   */
  public abstract void process(final Parameter pParam, final String pName);
  
  @Override
  public void process(final ParametersByNameMatch match) {
    process(match.getParam(), match.getName());
  }
}
