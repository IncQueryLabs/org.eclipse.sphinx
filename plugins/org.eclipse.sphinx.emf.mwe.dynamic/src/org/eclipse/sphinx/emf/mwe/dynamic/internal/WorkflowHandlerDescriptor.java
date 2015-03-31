/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
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
package org.eclipse.sphinx.emf.mwe.dynamic.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflow;
import org.eclipse.sphinx.emf.mwe.dynamic.IWorkflowHandler;
import org.osgi.framework.Bundle;

public class WorkflowHandlerDescriptor {

	private final static String ATTR_CLASS = "class"; //$NON-NLS-1$
	private final static String ATTR_PRIORITY = "priority"; //$NON-NLS-1$
	private final static String NODE_APPLICABLE_FOR = "applicableFor"; //$NON-NLS-1$
	private final static String NODE_INSTANCEOF = "instanceof"; //$NON-NLS-1$
	private final static String ATTR_VALUE = "value"; //$NON-NLS-1$

	private String contributorPluginId;
	private String className;
	private int priority = -1;
	private Set<String> applicableForWorkflows;

	private IWorkflowHandler instance;

	public WorkflowHandlerDescriptor(IConfigurationElement configurationElement) {
		Assert.isNotNull(configurationElement);

		contributorPluginId = configurationElement.getContributor().getName();
		Assert.isNotNull(contributorPluginId);

		className = configurationElement.getAttribute(ATTR_CLASS);
		Assert.isNotNull(className);

		String prioAttr = configurationElement.getAttribute(ATTR_PRIORITY);
		if (prioAttr != null && !prioAttr.isEmpty()) {
			try {
				priority = Integer.valueOf(prioAttr);
			} catch (NumberFormatException ex) {
				// Ignore Exception
			}
		}

		initApplicableWorkflows(configurationElement);
	}

	private void initApplicableWorkflows(IConfigurationElement configurationElement) {
		applicableForWorkflows = new HashSet<String>();

		IConfigurationElement[] applicableForElements = configurationElement.getChildren(NODE_APPLICABLE_FOR);
		for (IConfigurationElement applicableFor : applicableForElements) {
			for (IConfigurationElement child : applicableFor.getChildren(NODE_INSTANCEOF)) {
				String workflowClass = child.getAttribute(ATTR_VALUE);
				// Missing workflowClass, continue
				if (workflowClass == null) {
					continue;
				}
				applicableForWorkflows.add(workflowClass);
			}
		}
	}

	public int getPriority() {
		return priority;
	}

	public boolean isApplicableFor(Class<? extends IWorkflow> workflowClass) {
		String workflowClassName = workflowClass.getName();
		for (String applicableForWorkflow : applicableForWorkflows) {
			if (workflowClassName.equals(applicableForWorkflow)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a cached instance of the {@code IWorkflowHandler}
	 *
	 * @return The handler class instance
	 * @throws Exception
	 */
	public IWorkflowHandler getInstance() throws Exception {
		if (instance == null) {
			synchronized (this) {
				instance = newInstance();
			}
		}
		return instance;
	}

	/**
	 * Creates a new handler class instance.
	 *
	 * @return
	 */
	public IWorkflowHandler newInstance() throws Exception {
		Bundle bundle = Platform.getBundle(contributorPluginId);
		if (bundle == null) {
			throw new IllegalStateException("Cannot locate contributor plug-in '" + contributorPluginId + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		Class<?> clazz = bundle.loadClass(className);
		return (IWorkflowHandler) clazz.newInstance();
	}
}
