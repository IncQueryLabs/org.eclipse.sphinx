/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.sphinx.testutils.integration.AbstractIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.xtendxpand.internal.Activator;

public class XtendXpandIntegrationTestCase extends AbstractIntegrationTestCase<XtendXpandTestReferenceWorkspace> {

	public XtendXpandIntegrationTestCase() {
		super(XtendXpandTestReferenceWorkspace.class.getName());
	}

	@Override
	protected XtendXpandTestReferenceWorkspace doCreateReferenceWorkspace(String[] referenceProjectNames) {
		return new XtendXpandTestReferenceWorkspace(referenceProjectNames);
	}

	@Override
	protected Plugin getTestPlugin() {
		return Activator.getPlugin();
	}

	@Override
	protected String[][] getProjectReferences() {
		return new String[][] { { XtendXpandTestReferenceWorkspace.HB_TRANSFORM_XTEND_PROJECT_NAME },
				{ XtendXpandTestReferenceWorkspace.HB_CODEGEN_XPAND_PROJECT_NAME } };
	}
}