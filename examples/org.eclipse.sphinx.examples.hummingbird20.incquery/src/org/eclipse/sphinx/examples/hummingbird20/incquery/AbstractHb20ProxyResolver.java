package org.eclipse.sphinx.examples.hummingbird20.incquery;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.incquery.proxymanagment.AbstractProxyResolver;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;
import org.eclipse.sphinx.examples.hummingbird20.incquery.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public abstract class AbstractHb20ProxyResolver extends AbstractProxyResolver {

	public static final String QUERY_SEPARATOR = "?"; //$NON-NLS-1$
	public static final String SEGMENT_SEPARATOR = "/"; //$NON-NLS-1$

	@Override
	protected EObject[] getEObjectCandidates(EObject proxy, Object contextObject, IncQueryEngine engine) {
		Class<?> type = getInstanceClass(proxy);
		String name = getName(proxy);
		if (type != null && !isBlank(name)) {
			try {
				return doGetEObjectCandidates(type, name, engine);
			} catch (IncQueryException ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}
		return new EObject[] {};
	}

	@Override
	protected EObject[] getEObjectCandidates(URI proxyURI, Object contextObject, IncQueryEngine engine) {
		return new EObject[] {};
	}

	protected abstract EObject[] doGetEObjectCandidates(Class<?> type, String name, IncQueryEngine engine) throws IncQueryException;

	protected String getName(EObject proxy) {
		InternalEObject internalEObject = (InternalEObject) proxy;
		// Is given AROject a proxy?
		if (internalEObject.eIsProxy()) {
			// Retrieve absolute qualified name of given ARObject from its proxy URI
			String uriFragment = internalEObject.eProxyURI().fragment();
			if (uriFragment != null) {
				int queryStringIndex = uriFragment.indexOf(QUERY_SEPARATOR);
				String aqn = queryStringIndex != -1 ? uriFragment.substring(0, queryStringIndex) : uriFragment;
				int segSeparatorLastIndex = aqn.lastIndexOf(SEGMENT_SEPARATOR);
				return segSeparatorLastIndex != -1 ? aqn.substring(segSeparatorLastIndex + 1) : aqn;
			}
			return ""; //$NON-NLS-1$
		}
		return ((Identifiable) proxy).getName();
	}

	protected boolean isBlank(String text) {
		return text == null || text.isEmpty();
	}
}
