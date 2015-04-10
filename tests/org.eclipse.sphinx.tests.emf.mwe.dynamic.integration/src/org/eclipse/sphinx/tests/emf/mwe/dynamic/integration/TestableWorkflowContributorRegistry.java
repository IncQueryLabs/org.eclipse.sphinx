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
package org.eclipse.sphinx.tests.emf.mwe.dynamic.integration;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.sphinx.emf.mwe.dynamic.WorkflowContributorRegistry;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.tests.emf.mwe.dynamic.integration.internal.Activator;

public class TestableWorkflowContributorRegistry extends WorkflowContributorRegistry {

	public TestableWorkflowContributorRegistry(IExtensionRegistry extensionRegistry) {
		this(extensionRegistry, PlatformLogUtil.getLog(Activator.getPlugin()));
	}

	public TestableWorkflowContributorRegistry(IExtensionRegistry extensionRegistry, ILog log) {
		super(extensionRegistry, log);
	}
}
