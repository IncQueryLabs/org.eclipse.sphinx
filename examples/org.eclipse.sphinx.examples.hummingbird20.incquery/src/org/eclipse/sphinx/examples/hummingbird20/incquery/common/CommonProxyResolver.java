package org.eclipse.sphinx.examples.hummingbird20.incquery.common;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;
import org.eclipse.sphinx.examples.hummingbird20.incquery.AbstractHb20ProxyResolver;

public class CommonProxyResolver extends AbstractHb20ProxyResolver {

	@Override
	protected void initSupportedTypes() {
		getSupportedTypes().add(Identifiable.class);
	}

	@Override
	protected EObject[] doGetEObjectCandidates(Class<?> type, String name, IncQueryEngine engine) throws IncQueryException {
		if (Identifiable.class == type) {
			IdentifiablesByNameMatcher matcher = IdentifiablesByNameMatcher.on(engine);
			Set<Identifiable> candidates = matcher.getAllValuesOfidentifiable(name);
			return candidates.toArray(new EObject[candidates.size()]);
		}
		return null;
	}
}
