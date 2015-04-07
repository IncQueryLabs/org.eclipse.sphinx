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
package org.eclipse.sphinx.emf.check.ui.markers.tests;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sphinx.emf.check.ICheckValidationMarker;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.ui.views.markers.MarkerViewUtil;

public class GenerateErrorMarkersHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Job addJob = new Job("Add Error Markers") { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					Map<String, Object> attribs = new HashMap<String, Object>();
					for (int i = 0; i < 1000; i++) {
						if (i / 2 == 0) {
							attribs.put(MarkerViewUtil.NAME_ATTRIBUTE, "Test Name " + i); //$NON-NLS-1$
							attribs.put(MarkerViewUtil.PATH_ATTRIBUTE, "Test Path " + i); //$NON-NLS-1$
						}
						attribs.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
						attribs.put(IMarker.SOURCE_ID, "Error source"); //$NON-NLS-1$
						attribs.put(IMarker.MESSAGE, "Error message " + i); //$NON-NLS-1$
						attribs.put(IMarker.LOCATION, "Location " + i); //$NON-NLS-1$
						attribs.put("testAttribute", String.valueOf(i / 2)); //$NON-NLS-1$
						MarkerUtilities.createMarker(root, attribs, ICheckValidationMarker.CHECK_VALIDATION_PROBLEM);
					}
				} catch (CoreException e) {
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}
		};
		addJob.schedule();
		return this;
	}
}