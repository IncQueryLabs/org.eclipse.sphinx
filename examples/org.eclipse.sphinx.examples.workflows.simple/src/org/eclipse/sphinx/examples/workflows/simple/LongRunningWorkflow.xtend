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
package org.eclipse.sphinx.examples.workflows.simple

import org.eclipse.sphinx.emf.mwe.dynamic.WorkspaceWorkflow

class LongRunningWorkflow extends WorkspaceWorkflow {
	
	new(){
		children += new LongRunningWorkflowComponent 
	}
}