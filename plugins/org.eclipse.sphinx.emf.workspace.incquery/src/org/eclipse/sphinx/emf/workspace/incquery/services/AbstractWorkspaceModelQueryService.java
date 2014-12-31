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
package org.eclipse.sphinx.emf.workspace.incquery.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.IMatcherProvider;
import org.eclipse.sphinx.emf.incquery.internal.Activator;
import org.eclipse.sphinx.emf.incquery.services.AbstractModelQueryService;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.workspace.incquery.IWorkspaceIncQueryEngineHelper;
import org.eclipse.sphinx.emf.workspace.incquery.WorkspaceIncQueryEngineHelper;
import org.eclipse.sphinx.emf.workspace.query.IWorkspaceModelQueryService;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public abstract class AbstractWorkspaceModelQueryService extends AbstractModelQueryService implements IWorkspaceModelQueryService {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> getAllInstancesOf(IModelDescriptor modelDescriptor, Class<T> type) {
		List<T> result = new ArrayList<T>();
		try {
			IMatcherProvider provider = getMatcherProvider(type);
			if (provider != null) {
				IWorkspaceIncQueryEngineHelper engineHelper = (IWorkspaceIncQueryEngineHelper) getIncQueryEngineHelper();
				IncQueryMatcher matcher = provider.getMatcher(engineHelper.getEngine(modelDescriptor), type);
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

	@Override
	protected IIncQueryEngineHelper createIncQueryEngineHelper() {
		return new WorkspaceIncQueryEngineHelper();
	}
}
