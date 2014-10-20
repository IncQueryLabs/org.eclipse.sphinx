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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mwe2.runtime.workflow.WorkflowContextImpl;

// TODO Check if type of model should be List<EObject> rather than Object
// TODO Provide "model" spezialization of org.eclipse.emf.mwe.core.WorkflowContext and use it in AbstractWorkspaceWorkflowComponent.WorkspaceMwe2Bridge#invoke(IWorkflowContext)
public class ModelWorkflowContext extends WorkflowContextImpl {

	public ModelWorkflowContext(Object model, IProgressMonitor monitor) {
		put(IWorkflowSlots.MODEL_SLOT_NAME, model);
		put(IWorkflowSlots.PROGRESS_MONTIOR_SLOT_NAME, monitor);
	}

	@SuppressWarnings("unchecked")
	public List<EObject> getModel() {
		return (List<EObject>) get(IWorkflowSlots.MODEL_SLOT_NAME);
	}

	public IProgressMonitor getProgressMonitor() {
		return (IProgressMonitor) get(IWorkflowSlots.PROGRESS_MONTIOR_SLOT_NAME);
	}
}