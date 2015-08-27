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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.search.ui.ModelSearchMatch;
import org.eclipse.sphinx.emf.search.ui.QuerySpecification;
import org.eclipse.sphinx.emf.search.ui.incquery.internal.Activator;
import org.eclipse.sphinx.emf.search.ui.services.IModelSearchService;
import org.eclipse.sphinx.emf.workspace.incquery.IWorkspaceIncQueryEngineHelper;
import org.eclipse.sphinx.emf.workspace.incquery.WorkspaceIncQueryEngineHelper;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public abstract class AbstractIncQueryModelSearchService implements IModelSearchService {

	private IWorkspaceIncQueryEngineHelper workspaceIncQueryEngineHelper;

	protected abstract List<ModelSearchMatch> getMatches(IncQueryEngine engine, QuerySpecification querySpec);

	@Override
	public List<ModelSearchMatch> getMatches(Collection<Resource> resources, QuerySpecification querySpec) {
		List<ModelSearchMatch> result = new ArrayList<ModelSearchMatch>();
		for (Resource resource : resources) {
			try {
				IncQueryEngine engine = getWorkspaceIncQueryEngineHelper().getEngine(resource, true);
				result.addAll(getMatches(engine, querySpec));
			} catch (IncQueryException ex) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			}
		}
		return result;
	}

	@Override
	public List<ModelSearchMatch> getMatches(IModelDescriptor modelDescriptor, QuerySpecification querySpec) {
		try {
			IncQueryEngine engine = getWorkspaceIncQueryEngineHelper().getEngine(modelDescriptor);
			return getMatches(engine, querySpec);
		} catch (IncQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			return Collections.emptyList();
		}
	}

	protected IWorkspaceIncQueryEngineHelper getWorkspaceIncQueryEngineHelper() {
		if (workspaceIncQueryEngineHelper == null) {
			workspaceIncQueryEngineHelper = createWorkspaceIncQueryEngineHelper();
		}
		return workspaceIncQueryEngineHelper;
	}

	protected IWorkspaceIncQueryEngineHelper createWorkspaceIncQueryEngineHelper() {
		return new WorkspaceIncQueryEngineHelper();
	}
}
