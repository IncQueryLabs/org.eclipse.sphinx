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
package org.eclipse.sphinx.examples.hummingbird20.workflows.dump

import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.mwe.core.WorkflowContext
import org.eclipse.emf.mwe.core.issues.Issues
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor
import org.eclipse.sphinx.emf.mwe.dynamic.IWorkflowSlots
import org.eclipse.sphinx.emf.mwe.dynamic.components.AbstractModelWorkflowComponent
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable

class DumpHummingbird20WorkflowComponent extends AbstractModelWorkflowComponent {

	override protected invokeInternal(WorkflowContext ctx, ProgressMonitor monitor, Issues issues) {
		println("Executing Dump Hummingbird 2.0 workflow component")
		val modelObjects = ctx.get(IWorkflowSlots.MODEL_SLOT_NAME) as List<EObject>
		if (modelObjects == null || modelObjects.empty) {
			println("Model slot is empty, nothing to do!")
			return
		}

		for (modelObject : modelObjects) {
			val eAllContents = modelObject.eAllContents
			while (eAllContents.hasNext) {
				val element = eAllContents.next
				if (element instanceof Identifiable) {
					println("=> " + element.name)
				}
			}
		}

		println("Done!")
	}
}
