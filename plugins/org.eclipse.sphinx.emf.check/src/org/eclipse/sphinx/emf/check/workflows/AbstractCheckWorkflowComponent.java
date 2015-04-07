/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [458976] Validators are not singleton when they implement checks for different EPackages
 *     itemis - [463895] org.eclipse.sphinx.emf.check.AbstractCheckValidator.validate(EClass, EObject, DiagnosticChain, Map<Object, Object>) throws NPE
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check.workflows;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.mwe.core.WorkflowContext;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;
import org.eclipse.sphinx.emf.check.ICheckValidator;
import org.eclipse.sphinx.emf.check.services.CheckProblemMarkerService;
import org.eclipse.sphinx.emf.mwe.dynamic.IWorkflowSlots;
import org.eclipse.sphinx.emf.mwe.dynamic.components.AbstractModelWorkflowComponent;
import org.eclipse.sphinx.emf.mwe.dynamic.components.IModelWorkflowComponent;

/**
 * An abstract workflow component which makes use of the check validation service.
 */
public abstract class AbstractCheckWorkflowComponent extends AbstractModelWorkflowComponent implements IModelWorkflowComponent {

	protected Set<String> categories = new HashSet<String>();

	@Override
	protected void invokeInternal(WorkflowContext ctx, ProgressMonitor monitor, Issues issues) {
		@SuppressWarnings("unchecked")
		List<EObject> models = (List<EObject>) ctx.get(IWorkflowSlots.MODEL_SLOT_NAME);
		if (models != null && !models.isEmpty()) {
			for (EObject model : models) {
				try {

					// Put the categories in the context entries
					Map<Object, Object> contextEntries = new HashMap<Object, Object>();
					contextEntries.put(ICheckValidator.OPTION_CATEGORIES, categories);

					// Run validation (use standard validation entry point)
					Diagnostic diagnostic = Diagnostician.INSTANCE.validate(model, contextEntries);

					// Generate error markers and update check validation view
					CheckProblemMarkerService.INSTANCE.updateProblemMarkers(model, diagnostic);

				} catch (Exception ex) {
					issues.addError(this, ex.getMessage(), model, ex, null);
				}
			}
		}
	}
}
