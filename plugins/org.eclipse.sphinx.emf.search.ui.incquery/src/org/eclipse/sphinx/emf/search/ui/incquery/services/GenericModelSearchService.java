/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.search.ui.incquery.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.sphinx.emf.search.ui.ModelSearchMatch;
import org.eclipse.sphinx.emf.search.ui.QuerySpecification;

public class GenericModelSearchService extends AbstractModelSearchService {

	private Map<IncQueryEngine, Set<EStructuralFeature>> engineToFeaturesMap = new HashMap<IncQueryEngine, Set<EStructuralFeature>>();

	@Override
	protected List<ModelSearchMatch> getMatches(IncQueryEngine engine, QuerySpecification querySpec) {
		// engine.getBaseIndex().registerEStructuralFeatures(features);
		// Set<IProject> projects = querySpec.getProjects();
		// ModelDescriptorRegistry.INSTANCE.get
		// MetaModelDescriptorRegistry.INSTANCE.getdesc
		return null;
	}

	protected Set<EStructuralFeature> getFeatures(IncQueryEngine engine) {
		Set<EStructuralFeature> value = engineToFeaturesMap.get(engine);
		if (value == null) {
			if (engine.getScope() instanceof EMFScope) {
				Set<? extends Notifier> scopeRoots = ((EMFScope) engine.getScope()).getScopeRoots();
				if (!scopeRoots.isEmpty()) {
					getFeatures(scopeRoots.iterator().next());
				}
			}
		}
		return value;
	}

	protected Set<EStructuralFeature> getFeatures(Notifier notifier) {
		Set<EStructuralFeature> result = new HashSet<EStructuralFeature>();
		if (notifier instanceof ResourceSet) {

		} else if (notifier instanceof Resource) {

		} else if (notifier instanceof EObject) {

		}
		return result;
	}
}
