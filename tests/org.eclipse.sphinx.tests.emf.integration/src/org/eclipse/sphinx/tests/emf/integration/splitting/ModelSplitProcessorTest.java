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
package org.eclipse.sphinx.tests.emf.integration.splitting;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.splitting.IModelSplitDirective;
import org.eclipse.sphinx.emf.splitting.IModelSplitPolicy;
import org.eclipse.sphinx.emf.splitting.ModelSplitProcessor;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.examples.hummingbird20.splitting.Hummingbird20TypeModelSplitPolicy;
import org.eclipse.sphinx.tests.emf.integration.internal.Activator;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultTestReferenceWorkspace;

public class ModelSplitProcessorTest extends DefaultIntegrationTestCase {

	public ModelSplitProcessorTest() {
		// Set subset of projects to load
		Set<String> projectsToLoad = getProjectSubsetToLoad();
		projectsToLoad.add(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A);
		projectsToLoad.add(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A);
		projectsToLoad.add(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B);
		projectsToLoad.add(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C);
		projectsToLoad.add(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D);
		projectsToLoad.add(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_E);

		// Remove all project references except:
		// HB_PROJECT_NAME_20_E -> HB_PROJECT_NAME_20_D
		Map<String, Set<String>> projectReferences = getProjectReferences();
		projectReferences.remove(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E);
		projectReferences.remove(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_D);

		// Set test plug-in for retrieving test input resources
		setTestPlugin(Activator.getPlugin());
	}

	public void testGetModelSplitDirectives() throws Exception {
		IFile modelFile = refWks.hbProject20_B.getFile(DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20B_1);
		assertNotNull(modelFile);

		Resource resource = getResource(modelFile);
		assertNotNull(resource);

		ModelSplitProcessor processor = new ModelSplitProcessor();
		IModelSplitPolicy modelSplitPolicy = new Hummingbird20TypeModelSplitPolicy();

		for (TreeIterator<EObject> iterator = resource.getAllContents(); iterator.hasNext();) {
			EObject eObject = iterator.next();
			IModelSplitDirective directive = modelSplitPolicy.getSplitDirective(eObject);
			if (directive != null) {
				processor.getModelSplitDirectives().add(directive);
				iterator.prune();
			}
		}

		assertEquals(4, processor.getModelSplitDirectives().size());
	}

	private Resource getResource(IFile modelFile) {
		if (modelFile != null) {
			if (ModelDescriptorRegistry.INSTANCE.isModelFile(modelFile)) {
				return EcorePlatformUtil.loadResource(modelFile, EcoreResourceUtil.getDefaultLoadOptions());
			} else {
				return EcoreResourceUtil.loadResource(null, EcorePlatformUtil.createURI(modelFile.getFullPath()),
						EcoreResourceUtil.getDefaultLoadOptions());
			}
		}
		return null;
	}
}
