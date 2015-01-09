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
package org.eclipse.sphinx.examples.workflows.lib

import java.util.List
import org.eclipse.core.runtime.OperationCanceledException
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator
import org.eclipse.emf.mwe.core.WorkflowContext
import org.eclipse.sphinx.emf.mwe.dynamic.IWorkflowSlots

class ModelWorkflowExtensions {

	def static List<EObject> getModelSlot(WorkflowContext ctx) throws OperationCanceledException {
		val modelSlot = ctx.get(IWorkflowSlots.MODEL_SLOT_NAME) as List<EObject>
		if (modelSlot == null || modelSlot.empty) {
			println("Model slot is empty, nothing to do!")
			throw new OperationCanceledException
		}
		return modelSlot
	}

	def static String getLabel(EObject modelObject) {
		val editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(modelObject) as AdapterFactoryEditingDomain
		if (editingDomain != null) {
			val delegator = new AdapterFactoryItemDelegator(editingDomain.adapterFactory)
			val label = delegator.getText(modelObject)
			if (label != null && !label.empty) {
				return label
			} else {
				return modelObject.eClass.name + " <Unnamed>"
			}
		} else {
			return modelObject.toString
		}
	}
}
