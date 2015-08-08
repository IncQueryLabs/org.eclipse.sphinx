package org.eclipse.sphinx.examples.hummingbird20.incquery.common.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.IdentifiablesByNameMatch;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.common.identifiablesByName pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class IdentifiablesByNameProcessor implements IMatchProcessor<IdentifiablesByNameMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pIdentifiable the value of pattern parameter identifiable in the currently processed match
   * @param pName the value of pattern parameter name in the currently processed match
   * 
   */
  public abstract void process(final Identifiable pIdentifiable, final String pName);
  
  @Override
  public void process(final IdentifiablesByNameMatch match) {
    process(match.getIdentifiable(), match.getName());
  }
}
