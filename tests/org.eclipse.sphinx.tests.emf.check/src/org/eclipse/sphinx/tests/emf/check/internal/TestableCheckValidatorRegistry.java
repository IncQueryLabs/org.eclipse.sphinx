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
package org.eclipse.sphinx.tests.emf.check.internal;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.sphinx.emf.check.CheckValidatorRegistry;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class TestableCheckValidatorRegistry extends CheckValidatorRegistry {

	public static final TestableCheckValidatorRegistry INSTANCE = new TestableCheckValidatorRegistry(Platform.getExtensionRegistry(),
			EValidator.Registry.INSTANCE, PlatformLogUtil.getLog(Activator.getPlugin()));

	public TestableCheckValidatorRegistry(IExtensionRegistry extensionRegistry, EValidator.Registry eValidatorRegistry, ILog log) {
		super(extensionRegistry, eValidatorRegistry, log);
	}

	public void setExtensionRegistry(IExtensionRegistry extensionRegistry) {
		this.extensionRegistry = extensionRegistry;
	}

	public void setEValidatorRegistry(EValidator.Registry eValidatorRegistry) {
		this.eValidatorRegistry = eValidatorRegistry;
	}

	public void clear() {
		checkValidatorToCheckCatalogURIMap = null;
		checkCatalogURIToCheckValidatorsMap = null;
		uriToCheckCatalogMap.clear();
	}
}
