package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.PlatformsMatch;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;

/**
 * A match processor tailored for the org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.platforms pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class PlatformsProcessor implements IMatchProcessor<PlatformsMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pPlatform the value of pattern parameter platform in the currently processed match
   * 
   */
  public abstract void process(final Platform pPlatform);
  
  @Override
  public void process(final PlatformsMatch match) {
    process(match.getPlatform());
  }
}
