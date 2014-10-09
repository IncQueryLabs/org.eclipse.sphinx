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
package org.eclipse.sphinx.emf.mwe.dynamic.ui.handlers;

import org.eclipse.sphinx.emf.mwe.dynamic.operations.BasicWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation;

public class BasicWorkflowRunnerHandler extends AbstractWorkflowRunnerHandler {

	@Override
	protected IWorkflowRunnerOperation createWorkflowRunnerOperation() {
		IWorkflowRunnerOperation operation = new BasicWorkflowRunnerOperation(getOperationName());
		operation.setInput(getWorkflowInput());
		operation.setWorkflow(getWorkflowFile());
		return operation;
	}
}