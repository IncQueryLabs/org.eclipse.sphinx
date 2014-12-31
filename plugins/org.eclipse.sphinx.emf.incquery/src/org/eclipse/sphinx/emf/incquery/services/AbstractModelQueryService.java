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
package org.eclipse.sphinx.emf.incquery.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.IMatcherProvider;
import org.eclipse.sphinx.emf.incquery.IncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.internal.Activator;
import org.eclipse.sphinx.emf.query.IModelQueryService;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public abstract class AbstractModelQueryService implements IModelQueryService {

	private List<IMatcherProvider> matcherProviders = new ArrayList<IMatcherProvider>();
	private IIncQueryEngineHelper incQueryEngineHelper;

	public AbstractModelQueryService() {
		initMatcherProviders();
	}

	protected abstract void initMatcherProviders();

	protected List<IMatcherProvider> getMatcherProviders() {
		if (matcherProviders == null) {
			matcherProviders = new ArrayList<IMatcherProvider>();
		}
		return matcherProviders;
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

	protected <T> IMatcherProvider getMatcherProvider(Class<T> type) {
		for (IMatcherProvider provider : matcherProviders) {
			if (provider.isProviderForType(type)) {
				return provider;
			}
		}
		return null;
	}

	@Override
	public <T> List<T> getAllInstancesOf(EObject contextObject, Class<T> type) {
		return getAllInstancesOf(contextObject.eResource(), type);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> getAllInstancesOf(Resource contextResource, Class<T> type) {
		List<T> result = new ArrayList<T>();
		try {
			IMatcherProvider provider = getMatcherProvider(type);
			if (provider != null) {
				IncQueryMatcher matcher = provider.getMatcher(getIncQueryEngineHelper().getEngine(contextResource), type);
				Set allValues = matcher.getAllValues((String) matcher.getParameterNames().get(0));
				for (Object val : allValues) {
					result.add((T) val);
				}
			}
		} catch (IncQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return result;
	}
}
