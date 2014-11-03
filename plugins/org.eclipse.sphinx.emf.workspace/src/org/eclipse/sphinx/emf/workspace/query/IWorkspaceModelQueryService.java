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
package org.eclipse.sphinx.emf.workspace.query;

import java.util.List;

import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.query.IModelQueryService;

public interface IWorkspaceModelQueryService extends IModelQueryService {

	<T> List<T> getAllInstancesOf(IModelDescriptor modelDescriptor, Class<T> type);
}
