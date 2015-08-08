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
package org.eclipse.sphinx.emf.workspace.incquery.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.incquery.IIncQueryEngineHelper;
import org.eclipse.sphinx.emf.incquery.services.AbstractModelQueryService;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.workspace.incquery.IWorkspaceIncQueryEngineHelper;
import org.eclipse.sphinx.emf.workspace.incquery.WorkspaceIncQueryEngineHelper;
import org.eclipse.sphinx.emf.workspace.incquery.internal.Activator;
import org.eclipse.sphinx.emf.workspace.query.IWorkspaceModelQueryService;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

// TODO Rename to AbstractWorkspaceIncQueryModelQueryService and move to org.eclipse.sphinx.emf.workspace.incquery package
public abstract class AbstractWorkspaceModelQueryService extends AbstractModelQueryService implements IWorkspaceModelQueryService {

	@Override
	public <T> List<T> getAllInstancesOf(IModelDescriptor modelDescriptor, Class<T> type) {
		List<T> result = new ArrayList<T>();
		try {
			IWorkspaceIncQueryEngineHelper engineHelper = (IWorkspaceIncQueryEngineHelper) getIncQueryEngineHelper();
			ViatraQueryEngine engine = engineHelper.getEngine(modelDescriptor);
			NavigationHelper baseIndex = EMFScope.extractUnderlyingEMFIndex(engine);
			for (EObject element : baseIndex.getAllInstances(getEClassForType(type))) {
				result.add(type.cast(element));
			}
		} catch (ViatraQueryException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return result;
	}

	@Override
	protected IIncQueryEngineHelper createIncQueryEngineHelper() {
		return new WorkspaceIncQueryEngineHelper();
	}
}
