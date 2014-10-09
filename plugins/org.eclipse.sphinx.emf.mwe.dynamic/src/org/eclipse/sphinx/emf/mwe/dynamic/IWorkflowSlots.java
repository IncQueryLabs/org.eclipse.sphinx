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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mwe.core.lib.WorkflowComponentWithModelSlot;
import org.eclipse.emf.mwe2.runtime.workflow.Workflow;

public interface IWorkflowSlots {

	/**
	 * Name of slot for passing a {@link EObject model element} to {@link Workflow workflow}s and
	 * {@link WorkflowComponentWithModelSlot workflow component}s.
	 */
	String MODEL_SLOT_NAME = "model"; //$NON-NLS-1$
	/**
	 * Name of slot for passing a {@link IProgressMonitor progress monitor} to {@link Workflow workflow}s and
	 * {@link WorkflowComponentWithModelSlot workflow component}s.
	 */
	String PROGRESS_MONTIOR_SLOT_NAME = "progressMonitor"; //$NON-NLS-1$
}