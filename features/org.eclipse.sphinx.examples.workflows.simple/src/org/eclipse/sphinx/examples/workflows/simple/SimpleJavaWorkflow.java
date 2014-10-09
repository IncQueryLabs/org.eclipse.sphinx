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
package org.eclipse.sphinx.examples.workflows.simple;

import org.eclipse.emf.mwe2.runtime.workflow.Workflow;
import org.eclipse.sphinx.examples.workflows.simple.SimpleXtendWorkflowComponent;

public class SimpleJavaWorkflow extends Workflow {

	public SimpleJavaWorkflow() {
		System.out.println("Creating simple Java-based workflow");

		// Add workflow components to be executed
		getChildren().add(new SimpleJavaWorkflowComponent());
		getChildren().add(new SimpleXtendWorkflowComponent());
	}
}
