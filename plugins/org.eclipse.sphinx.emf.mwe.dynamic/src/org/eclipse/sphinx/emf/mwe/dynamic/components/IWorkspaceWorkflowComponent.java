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
package org.eclipse.sphinx.emf.mwe.dynamic.components;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowComponent;

public interface IWorkspaceWorkflowComponent extends IWorkflowComponent {

	/**
	 * Returns the {@link ISchedulingRule scheduling rule} required by this operation.
	 *
	 * @return The scheduling rule or <code>null</code> if no such is required.
	 */
	ISchedulingRule getRule();
}
