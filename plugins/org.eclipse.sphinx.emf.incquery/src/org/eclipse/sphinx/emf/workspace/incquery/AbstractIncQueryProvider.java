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
package org.eclipse.sphinx.emf.workspace.incquery;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public abstract class AbstractIncQueryProvider {

	private List<Class> supportedTypes;
	private IIncQueryEngineHelper incQueryEngineHelper;

	public AbstractIncQueryProvider() {
		initSupportedTypes();
	}

	protected abstract void initSupportedTypes();

	protected List<Class> getSupportedTypes() {
		if (supportedTypes == null) {
			supportedTypes = new ArrayList<Class>();
		}
		return supportedTypes;
	}

	protected IIncQueryEngineHelper getIncQueryEngineHelper() {
		if (incQueryEngineHelper == null) {
			incQueryEngineHelper = createIncQueryEngineHelper();
		}
		return incQueryEngineHelper;
	}

	protected IIncQueryEngineHelper createIncQueryEngineHelper() {
		return new IncQueryEngineHelper();
	}
}
