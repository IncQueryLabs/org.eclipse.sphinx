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
import org.eclipse.sphinx.emf.mwe.dynamic.components.AbstractModelWorkflowComponent

import static extension org.eclipse.sphinx.examples.hummingbird20.workflows.util.ModelWorkflowExtensions.*

class HelloHummingbird20WorkflowComponent extends AbstractModelWorkflowComponent {

	override protected invokeInternal(WorkflowContext ctx, ProgressMonitor monitor, Issues issues) {
		println("Executing Hello Hummingbird 2.0 workflow component")
		val modelObjects = ctx.modelSlot

		var msg = "Model slot contains: "
		val iter = modelObjects.iterator
		while (iter.hasNext) {
			msg += iter.next.label
			
			if (iter.hasNext) {
				msg += ", "
			}
		}
		println(msg)

		println("Done!")
	}
}
