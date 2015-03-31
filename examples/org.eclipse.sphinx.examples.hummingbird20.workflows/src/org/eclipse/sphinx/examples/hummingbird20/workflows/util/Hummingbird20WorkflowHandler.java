package org.eclipse.sphinx.examples.hummingbird20.workflows.util;

import org.eclipse.emf.mwe2.runtime.workflow.IWorkflow;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.sphinx.emf.mwe.dynamic.IWorkflowHandler;

public class Hummingbird20WorkflowHandler implements IWorkflowHandler {

	@Override
	public void preRun(IWorkflow workflow, IWorkflowContext context) {
		System.out.println("Pre-run workflow: " + workflow.getClass()); //$NON-NLS-1$
	}

	@Override
	public void postRun(IWorkflow workflow, IWorkflowContext context) {
		System.out.println("Post-run workflow: " + workflow.getClass()); //$NON-NLS-1$
	}
}
