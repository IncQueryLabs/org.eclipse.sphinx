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
package org.eclipse.sphinx.examples.workflows.model

import org.eclipse.emf.mwe.core.WorkflowContext
import org.eclipse.emf.mwe.core.issues.Issues
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor
import org.eclipse.sphinx.emf.mwe.dynamic.WorkspaceWorkflow
import org.eclipse.sphinx.emf.mwe.dynamic.components.AbstractModelWorkflowComponent

import static extension org.eclipse.sphinx.examples.workflows.lib.ModelWorkflowExtensions.*

class PrintModelContentWorkflow extends WorkspaceWorkflow {

	new(){
		children += new PrintModelContentWorkflowComponent
	}
}

class PrintModelContentWorkflowComponent extends AbstractModelWorkflowComponent {

	override protected invokeInternal(WorkflowContext ctx, ProgressMonitor monitor, Issues issues) {
		println("Executing Print Model Content workflow component")
		val modelObjects = ctx.modelSlot

		for (modelObject : modelObjects) {
			println("=> " + modelObject.label + " ["+ modelObject.URL + "]")

			val eAllContents = modelObject.eAllContents
			while (eAllContents.hasNext) {
				val element = eAllContents.next
				println("=> " + element.label + " ["+ element.URL + "]")
			}
		}

		println("Done!")
	}
}
