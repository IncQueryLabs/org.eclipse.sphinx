package org.eclipse.sphinx.examples.hummingbird20.incquery.common;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.IdentifiablesByNameMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.IdentifiablesMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.util.IdentifiablesByNameQuerySpecification;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.util.IdentifiablesQuerySpecification;

/**
 * A pattern group formed of all patterns defined in common.eiq.
 * 
 * <p>Use the static instance as any {@link org.eclipse.incquery.runtime.api.IPatternGroup}, to conveniently prepare
 * an EMF-IncQuery engine for matching all patterns originally defined in file common.eiq,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package org.eclipse.sphinx.examples.hummingbird20.incquery.common, the group contains the definition of the following patterns: <ul>
 * <li>identifiables</li>
 * <li>identifiablesByName</li>
 * </ul>
 * 
 * @see IPatternGroup
 * 
 */
@SuppressWarnings("all")
public final class Common extends BaseGeneratedPatternGroup {
  /**
   * Access the pattern group.
   * 
   * @return the singleton instance of the group
   * @throws IncQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static Common instance() throws IncQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new Common();
    }
    return INSTANCE;
  }
  
  private static Common INSTANCE;
  
  private Common() throws IncQueryException {
    querySpecifications.add(IdentifiablesQuerySpecification.instance());
    querySpecifications.add(IdentifiablesByNameQuerySpecification.instance());
  }
  
  public IdentifiablesQuerySpecification getIdentifiables() throws IncQueryException {
    return IdentifiablesQuerySpecification.instance();
  }
  
  public IdentifiablesMatcher getIdentifiables(final IncQueryEngine engine) throws IncQueryException {
    return IdentifiablesMatcher.on(engine);
  }
  
  public IdentifiablesByNameQuerySpecification getIdentifiablesByName() throws IncQueryException {
    return IdentifiablesByNameQuerySpecification.instance();
  }
  
  public IdentifiablesByNameMatcher getIdentifiablesByName(final IncQueryEngine engine) throws IncQueryException {
    return IdentifiablesByNameMatcher.on(engine);
  }
}
