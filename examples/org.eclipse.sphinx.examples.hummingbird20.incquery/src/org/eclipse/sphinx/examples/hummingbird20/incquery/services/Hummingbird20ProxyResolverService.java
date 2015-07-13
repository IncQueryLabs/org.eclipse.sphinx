package org.eclipse.sphinx.examples.hummingbird20.incquery.services;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.sphinx.emf.incquery.proxymanagment.AbstractProxyResolverService;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.CommonProxyResolver;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.InstanceModelProxyResolver;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.TypeModelProxyResolver;

public class Hummingbird20ProxyResolverService extends AbstractProxyResolverService {

	@Override
	protected void initProxyResolvers() {
		getProxyResolvers().add(new CommonProxyResolver());
		getProxyResolvers().add(new InstanceModelProxyResolver());
		getProxyResolvers().add(new TypeModelProxyResolver());
	}

	@Override
	protected EClass getTargetEClass(URI uri) {
		return null;
	}
}
