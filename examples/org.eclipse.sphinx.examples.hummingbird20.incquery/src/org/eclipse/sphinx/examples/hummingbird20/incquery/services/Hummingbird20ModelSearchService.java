package org.eclipse.sphinx.examples.hummingbird20.incquery.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.search.ui.ModelSearchMatch;
import org.eclipse.sphinx.emf.search.ui.QuerySpecification;
import org.eclipse.sphinx.emf.search.ui.incquery.services.AbstractIncQueryModelSearchService;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.IdentifiablesByNameMatcher;
import org.eclipse.sphinx.examples.hummingbird20.incquery.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class Hummingbird20ModelSearchService extends AbstractIncQueryModelSearchService {

	@Override
	protected List<ModelSearchMatch> getMatches(IncQueryEngine engine, QuerySpecification querySpec) {
		List<ModelSearchMatch> result = new ArrayList<ModelSearchMatch>();
		try {
			IdentifiablesByNameMatcher matcher = IdentifiablesByNameMatcher.on(engine);
			// TODO Check with EMF-IncQuery guys if simple patterns and/or RegEx can be supported
			Set<Identifiable> allValuesOfidentifiable = matcher.getAllValuesOfidentifiable(querySpec.getPattern());
			for (Identifiable identifiable : allValuesOfidentifiable) {
				result.add(new ModelSearchMatch(identifiable));
			}
		} catch (IncQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return result;
	}
}
