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
package org.eclipse.sphinx.examples.workflows.simple.java;

import org.eclipse.emf.mwe.core.WorkflowContext;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.emf.mwe.core.lib.AbstractWorkflowComponent2;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;
import org.eclipse.sphinx.examples.workflows.lib.ExampleWorkflowHelper;

@SuppressWarnings("nls")
public class SimpleJavaWorkflowComponent extends AbstractWorkflowComponent2 {

	@Override
	protected void invokeInternal(WorkflowContext ctx, ProgressMonitor monitor, Issues issues) {
		System.out.println("Executing simple Java-based workflow component");

		System.out.println("Using some class from another project: " + ExampleWorkflowHelper.class);
		ExampleWorkflowHelper helper = new ExampleWorkflowHelper();
		helper.doSomething();
		helper.doSomethingUsingAnonymousClass();

		System.out.println("Done!");
	}
}
