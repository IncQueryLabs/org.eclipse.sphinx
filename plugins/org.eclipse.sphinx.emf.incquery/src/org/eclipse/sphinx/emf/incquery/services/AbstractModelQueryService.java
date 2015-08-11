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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.IMatcherProvider;
import org.eclipse.sphinx.emf.incquery.IncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.internal.Activator;
import org.eclipse.sphinx.emf.query.IModelQueryService;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

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
	public <T> List<T> getAllInstancesOf(Resource contextResource, Class<T> type) {
		try {
			IncQueryEngine engine = getIncQueryEngineHelper().getEngine(contextResource);
			return getAllInstancesOf(type, engine);
		} catch (IncQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> List<T> getAllInstancesOf(Class<T> type, IncQueryEngine engine) throws IncQueryException {
		List<T> result = new ArrayList<T>();
		NavigationHelper index = EMFScope.extractUnderlyingEMFIndex(engine);
		EClass eClass = findEClassForClass(type);
		if (eClass != null) {
			index.registerEClasses(ImmutableSet.of(eClass));
			Set allValues = index.getAllInstances(eClass);
			for (Object val : allValues) {
				result.add((T) val);
			}
		}
		return result;
	}

	private static <T> EClass findEClassForClass(Class<T> type) {
		for (Object value : EPackage.Registry.INSTANCE.values()) {
			EPackage ePackage = null;
			if (value instanceof EPackage) {
				ePackage = (EPackage) value;
			} else if (value instanceof EPackage.Descriptor) {
				EPackage.Descriptor descriptor = (EPackage.Descriptor) value;
				ePackage = descriptor.getEPackage();
			}
			if (ePackage != null) {
				for (EClass eClass : Iterables.filter(ePackage.getEClassifiers(), EClass.class)) {
					if (eClass.getInstanceClass() == type) {
						return eClass;
					}
				}
			}
		}
		return null;
	}

}
