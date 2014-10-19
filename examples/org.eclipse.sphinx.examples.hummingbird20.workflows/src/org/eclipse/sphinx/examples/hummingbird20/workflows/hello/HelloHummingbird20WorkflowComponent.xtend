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
package org.eclipse.sphinx.examples.hummingbird20.workflows.hello

import org.eclipse.emf.mwe.core.WorkflowContext
import org.eclipse.emf.mwe.core.issues.Issues
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor
import org.eclipse.sphinx.emf.mwe.dynamic.IWorkflowSlots
import org.eclipse.sphinx.emf.mwe.dynamic.components.AbstractWorkspaceWorkflowComponent
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable

class HelloHummingbird20WorkflowComponent extends AbstractWorkspaceWorkflowComponent {

	override protected invokeInternal(WorkflowContext ctx, ProgressMonitor monitor, Issues issues) {
		println("Executing Hello Hummingbird 2.0 workflow component")
		val model = ctx.get(IWorkflowSlots.MODEL_SLOT_NAME)
		if (model == null) {
			println("Model slot is empty, nothing to do!")
			return
		}

		var msg = "Model slot contains : "
		if (model instanceof Identifiable) {
			msg += if(model.name != null && !model.name.empty) model.name else "Unnamed " + model.eClass.name
		} else {
			msg += model.toString
		}
		println(msg)

		println("Done!")
	}
}
