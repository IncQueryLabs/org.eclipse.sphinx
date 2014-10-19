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
package org.eclipse.sphinx.emf.mwe.dynamic.components;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.mwe.core.WorkflowComponentWithID;
import org.eclipse.emf.mwe.core.WorkflowContext;
import org.eclipse.emf.mwe.core.issues.IssuesImpl;
import org.eclipse.emf.mwe.core.issues.MWEDiagnostic;
import org.eclipse.emf.mwe.core.lib.AbstractWorkflowComponent2;
import org.eclipse.emf.mwe.core.lib.Mwe2Bridge;
import org.eclipse.emf.mwe.core.monitor.NullProgressMonitor;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitorAdapter;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.sphinx.emf.mwe.dynamic.IWorkflowSlots;
import org.eclipse.sphinx.emf.mwe.dynamic.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public abstract class AbstractWorkspaceWorkflowComponent extends AbstractWorkflowComponent2 implements IWorkspaceWorkflowComponent {

	private final ISchedulingRule rule;

	private Mwe2Bridge bridge;

	public AbstractWorkspaceWorkflowComponent() {
		this(null);
	}

	public AbstractWorkspaceWorkflowComponent(ISchedulingRule rule) {
		this.rule = rule;
	}

	/*
	 * @see org.eclipse.sphinx.emf.mwe.dynamic.components.IWorkspaceWorkflowComponent#getRule()
	 */
	@Override
	public ISchedulingRule getRule() {
		return rule;
	}

	protected static class WorkspaceMwe2Bridge extends Mwe2Bridge {

		private final WorkflowComponentWithID delegate;

		public WorkspaceMwe2Bridge(WorkflowComponentWithID delegate) {
			super(delegate);
			this.delegate = delegate;
		}

		@Override
		public void invoke(final IWorkflowContext ctx) {
			IssuesImpl issues = new IssuesImpl();

			Object monitorAsObject = ctx.get(IWorkflowSlots.PROGRESS_MONTIOR_SLOT_NAME);
			ProgressMonitor monitor = monitorAsObject instanceof IProgressMonitor ? new ProgressMonitorAdapter((IProgressMonitor) monitorAsObject)
					: new NullProgressMonitor();

			delegate.invoke(new WorkflowContext() {

				@Override
				public void set(String slotName, Object value) {
					ctx.put(slotName, value);
				}

				@Override
				public String[] getSlotNames() {
					return ctx.getSlotNames().toArray(new String[ctx.getSlotNames().size()]);
				}

				@Override
				public Object get(String slotName) {
					return ctx.get(slotName);
				}
			}, monitor, issues);

			handleIssues(issues);
		}

		@Override
		protected void handleIssues(IssuesImpl issues) {
			for (MWEDiagnostic diag : issues.getErrors()) {
				PlatformLogUtil.logAsError(Activator.getPlugin(), diag.toString());
			}
			for (MWEDiagnostic diag : issues.getWarnings()) {
				PlatformLogUtil.logAsWarning(Activator.getPlugin(), diag.toString());
			}
			for (MWEDiagnostic diag : issues.getInfos()) {
				PlatformLogUtil.logAsInfo(Activator.getPlugin(), diag.toString());
			}
		}
	};

	/*
	 * @see org.eclipse.emf.mwe.core.lib.AbstractWorkflowComponent#getBridge()
	 */
	@Override
	protected Mwe2Bridge getBridge() {
		if (bridge == null) {
			bridge = new WorkspaceMwe2Bridge(this);
		}
		return bridge;
	}
}
