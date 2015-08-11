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

import java.util.List;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.internal.Activator;
import org.eclipse.sphinx.emf.incquery.services.AbstractModelQueryService;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.workspace.incquery.IWorkspaceIncQueryEngineHelper;
import org.eclipse.sphinx.emf.workspace.incquery.WorkspaceIncQueryEngineHelper;
import org.eclipse.sphinx.emf.workspace.query.IWorkspaceModelQueryService;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public abstract class AbstractWorkspaceModelQueryService extends AbstractModelQueryService implements IWorkspaceModelQueryService {

	@Override
	public <T> List<T> getAllInstancesOf(IModelDescriptor modelDescriptor, Class<T> type) {
		try {
			IncQueryEngine engine = ((IWorkspaceIncQueryEngineHelper) getIncQueryEngineHelper()).getEngine(modelDescriptor);
			return getAllInstancesOf(type, engine);
		} catch (IncQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
			return null;
		}
	}

	@Override
	protected IIncQueryEngineHelper createIncQueryEngineHelper() {
		return new WorkspaceIncQueryEngineHelper();
	}
}
