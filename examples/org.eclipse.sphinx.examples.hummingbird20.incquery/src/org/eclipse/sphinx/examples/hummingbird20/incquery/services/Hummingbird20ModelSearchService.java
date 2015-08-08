/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.examples.hummingbird20.incquery.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.search.ui.ModelSearchMatch;
import org.eclipse.sphinx.emf.search.ui.QuerySpecification;
import org.eclipse.sphinx.emf.search.ui.incquery.services.AbstractIncQueryModelSearchService;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Package;
import org.eclipse.sphinx.examples.hummingbird20.incquery.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

public class Hummingbird20ModelSearchService extends AbstractIncQueryModelSearchService {

	@Override
	protected List<ModelSearchMatch> getMatches(ViatraQueryEngine engine, QuerySpecification querySpec) {
		List<ModelSearchMatch> result = new ArrayList<ModelSearchMatch>();
		try {
			NavigationHelper baseIndex = EMFScope.extractUnderlyingEMFIndex(engine);
			// TODO Check with EMF-IncQuery guys if simple patterns and/or RegEx can be supported
			Set<EObject> allValuesOfidentifiable = baseIndex.findByAttributeValue(querySpec.getPattern(),
					Common20Package.Literals.IDENTIFIABLE__NAME);
			for (EObject identifiable : allValuesOfidentifiable) {
				result.add(new ModelSearchMatch(identifiable));
			}
		} catch (ViatraQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return result;
	}
}
