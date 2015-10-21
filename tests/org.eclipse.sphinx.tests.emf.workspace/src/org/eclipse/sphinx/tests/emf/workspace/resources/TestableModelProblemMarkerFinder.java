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
package org.eclipse.sphinx.tests.emf.workspace.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.workspace.resources.BasicModelProblemMarkerFinder;
import org.eclipse.sphinx.tests.emf.workspace.resources.scenarios.BasicModelProblemMarkerFinderScenario;

public class TestableModelProblemMarkerFinder extends BasicModelProblemMarkerFinder {

	private BasicModelProblemMarkerFinderScenario scenario;

	public TestableModelProblemMarkerFinder(BasicModelProblemMarkerFinderScenario scenario) {
		Assert.isNotNull(scenario);
		this.scenario = scenario;
	}

	@Override
	protected IFile getFile(EObject eObject) {
		return scenario.getFile(eObject);
	}

	@Override
	protected URI getURI(EObject eObject) {
		return scenario.getURI(eObject);
	}
}