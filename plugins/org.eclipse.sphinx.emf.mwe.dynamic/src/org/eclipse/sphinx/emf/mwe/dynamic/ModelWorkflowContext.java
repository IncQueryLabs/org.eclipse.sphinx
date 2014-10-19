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

package org.eclipse.sphinx.emf.mwe.dynamic;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.mwe2.runtime.workflow.WorkflowContextImpl;

public class ModelWorkflowContext extends WorkflowContextImpl {

	public ModelWorkflowContext(Object model, IProgressMonitor monitor) {
		put(IWorkflowSlots.MODEL_SLOT_NAME, model);
		put(IWorkflowSlots.PROGRESS_MONTIOR_SLOT_NAME, monitor);
	}
}