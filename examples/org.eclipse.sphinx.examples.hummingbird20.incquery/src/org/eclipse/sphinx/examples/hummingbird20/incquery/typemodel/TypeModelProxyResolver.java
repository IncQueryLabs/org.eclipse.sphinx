package org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.examples.hummingbird20.incquery.AbstractHummingbird20ProxyResolver;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Parameter;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Platform;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;

public class TypeModelProxyResolver extends AbstractHummingbird20ProxyResolver {

	@Override
	protected void initSupportedTypes() {
		getSupportedTypes().add(ComponentType.class);
		getSupportedTypes().add(Port.class);
		getSupportedTypes().add(Interface.class);
		getSupportedTypes().add(Parameter.class);
		getSupportedTypes().add(Platform.class);
	}

	@Override
	protected EObject[] doGetEObjectCandidates(Class<?> type, String name, IncQueryEngine engine) throws IncQueryException {
		if (ComponentType.class == type) {
			ComponentTypesByNameMatcher matcher = ComponentTypesByNameMatcher.on(engine);
			Set<ComponentType> candidates = matcher.getAllValuesOfcomponentType(name);
			return candidates.toArray(new EObject[candidates.size()]);
		}
		if (Port.class == type) {
			PortsByNameMatcher matcher = PortsByNameMatcher.on(engine);
			Set<Port> candidates = matcher.getAllValuesOfport(name);
			return candidates.toArray(new EObject[candidates.size()]);
		}
		if (Interface.class == type) {
			InterfacesByNameMatcher matcher = InterfacesByNameMatcher.on(engine);
			Set<Interface> candidates = matcher.getAllValuesOfinterface(name);
			return candidates.toArray(new EObject[candidates.size()]);
		}
		if (Parameter.class == type) {
			ParametersByNameMatcher matcher = ParametersByNameMatcher.on(engine);
			Set<Parameter> candidates = matcher.getAllValuesOfparam(name);
			return candidates.toArray(new EObject[candidates.size()]);
		}
		if (Platform.class == type) {
			PlatformsByNameMatcher matcher = PlatformsByNameMatcher.on(engine);
			Set<Platform> candidates = matcher.getAllValuesOfplatform(name);
			return candidates.toArray(new EObject[candidates.size()]);
		}
		return null;
	}
}
