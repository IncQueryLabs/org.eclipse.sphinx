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
package org.eclipse.sphinx.emf.mwe.dynamic.operations;

import org.eclipse.sphinx.platform.operations.IWorkspaceOperation;

public interface IWorkflowRunnerOperation extends IWorkspaceOperation {

	Object getWorkflow();

	void setWorkflow(Object workflow);

	Object getModel();

	void setModel(Object object);
}