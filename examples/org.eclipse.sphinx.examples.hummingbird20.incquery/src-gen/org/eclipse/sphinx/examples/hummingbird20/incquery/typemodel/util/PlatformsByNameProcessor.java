package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PlatformsByNameMatch;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.platformsByName pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class PlatformsByNameProcessor implements IMatchProcessor<PlatformsByNameMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pPlatform the value of pattern parameter platform in the currently processed match
   * @param pName the value of pattern parameter name in the currently processed match
   * 
   */
  public abstract void process(final Platform pPlatform, final String pName);
  
  @Override
  public void process(final PlatformsByNameMatch match) {
    process(match.getPlatform(), match.getName());
    
  }
}
