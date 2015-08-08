package org.eclipse.sphinx.examples.hummingbird20.incquery.services;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.sphinx.emf.workspace.incquery.services.AbstractWorkspaceModelQueryService;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Package;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package;

public class Hummindbird20QueryService extends AbstractWorkspaceModelQueryService {

	private final Map<Class<?>, EClass> instanceClassMap;

	public Hummindbird20QueryService() {
		instanceClassMap = new HashMap<Class<?>, EClass>();
		for (EClassifier eclass : Common20Package.eINSTANCE.getEClassifiers()) {
			if (eclass instanceof EClass) {
				instanceClassMap.put(eclass.getInstanceClass(), (EClass) eclass);
			}
		}
		for (EClassifier eclass : InstanceModel20Package.eINSTANCE.getEClassifiers()) {
			if (eclass instanceof EClass) {
				instanceClassMap.put(eclass.getInstanceClass(), (EClass) eclass);
			}
		}
		for (EClassifier eclass : TypeModel20Package.eINSTANCE.getEClassifiers()) {
			if (eclass instanceof EClass) {
				instanceClassMap.put(eclass.getInstanceClass(), (EClass) eclass);
			}
		}
	}

	@Override
	protected EClass getEClassForType(Class<?> type) {
		return instanceClassMap.get(type);
	}
}
