/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
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
package org.eclipse.sphinx.emf.incquery.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.IncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.internal.Activator;
import org.eclipse.sphinx.emf.query.IModelQueryService;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

// TODO Rename to AbstractIncQueryModelQueryService and move to org.eclipse.sphinx.emf.incquery package
public abstract class AbstractModelQueryService implements IModelQueryService {

	private IIncQueryEngineHelper incQueryEngineHelper;

	public AbstractModelQueryService() {
	}

	protected IIncQueryEngineHelper getIncQueryEngineHelper() {
		if (incQueryEngineHelper == null) {
			incQueryEngineHelper = createIncQueryEngineHelper();
		}
		return incQueryEngineHelper;
	}

	protected IIncQueryEngineHelper createIncQueryEngineHelper() {
		return new IncQueryEngineHelper();
	}

	@Override
	public <T> List<T> getAllInstancesOf(EObject contextObject, Class<T> type) {
		return getAllInstancesOf(contextObject.eResource(), type);
	}

	protected abstract EClass getEClassForType(Class<?> type);

	@Override
	public <T> List<T> getAllInstancesOf(Resource contextResource, Class<T> type) {
		List<T> result = new ArrayList<T>();
		try {
			ViatraQueryEngine engine = getIncQueryEngineHelper().getEngine(contextResource);
			NavigationHelper baseIndex = EMFScope.extractUnderlyingEMFIndex(engine);
			for (EObject element : baseIndex.getAllInstances(getEClassForType(type))) {
				result.add(type.cast(element));
			}
		} catch (ViatraQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return result;
	}
}
