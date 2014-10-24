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
package org.eclipse.sphinx.examples.hummingbird20.workflows.util

import java.util.List
import org.eclipse.core.runtime.OperationCanceledException
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.mwe.core.WorkflowContext
import org.eclipse.sphinx.emf.mwe.dynamic.IWorkflowSlots
import org.eclipse.sphinx.emf.resource.ExtendedResourceAdapterFactory
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable

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
		if (modelObject instanceof Identifiable) {
			if (modelObject.name != null && !modelObject.name.empty) {
				return modelObject.eClass.name + " " + modelObject.name
			} else {
				return modelObject.eClass.name + " <Unnamed>" 
			}
		} else {
			return modelObject.toString
		}
	}

	def static URI getURL(EObject modelObject) {
		val extendedResource = ExtendedResourceAdapterFactory.INSTANCE.adapt(modelObject.eResource)
		if (extendedResource != null) {
			return extendedResource.getURI(modelObject)
		} else {
			return EcoreUtil.getURI(modelObject)
		}
	}
}
