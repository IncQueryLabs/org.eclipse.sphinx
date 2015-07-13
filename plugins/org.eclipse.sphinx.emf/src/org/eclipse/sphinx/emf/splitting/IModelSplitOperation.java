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
package org.eclipse.sphinx.emf.splitting;

import org.eclipse.sphinx.platform.operations.IWorkspaceOperation;

public interface IModelSplitOperation extends IWorkspaceOperation {

	IModelSplitPolicy getModelSplitPolicy();

	void setDeleteOriginalResources(boolean deleteOriginalResources);

	boolean isDeleteOriginalResources();
}