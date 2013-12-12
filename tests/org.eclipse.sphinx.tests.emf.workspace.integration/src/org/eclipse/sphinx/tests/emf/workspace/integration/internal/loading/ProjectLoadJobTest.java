/**
 * <copyright>
 *
 * Copyright (c) 2008-2013 See4sys, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     itemis - [423676] AbstractIntegrationTestCase unable to remove project references that are no longer needed
 *
 * </copyright>
 */
package org.eclipse.sphinx.tests.emf.workspace.integration.internal.loading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.workspace.internal.loading.LoadJob;
import org.eclipse.sphinx.emf.workspace.internal.loading.ModelLoadJob;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultTestReferenceWorkspace;

/**
 *
 */
@SuppressWarnings({ "restriction" })
public class ProjectLoadJobTest extends AbstractLoadJobTest {

	public ProjectLoadJobTest() {
		// Set project references as follows:
		// HB_PROJECT_NAME_20_C -> HB_PROJECT_NAME_20_B -> HB_PROJECT_NAME_20_A
		Map<String, Set<String>> projectReferences = getProjectReferences();
		projectReferences.clear();
		projectReferences.put(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B,
				Collections.singleton(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
		projectReferences.put(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C,
				Collections.singleton(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project {@linkplain DefaultTestReferenceWorkspace#hbProject20_C} <b>with</b> its
	 * referenced projects.
	 */
	public void testShouldCreateJob_20C_RefProjectsIncluded() {
		// Local initialization of this test

		Collection<IProject> projectsToLoad = Collections.singletonList(refWks
				.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

		ModelLoadManager.INSTANCE.loadProjects(projectsToLoad, true, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[14];
		// The messages to display in case of violated assertions
		String[] messages = new String[14];

		IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

		int index = -1;
		{ // 0
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 1
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 3
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 5
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 6
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
			;
		}
		{ // 7
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 8
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 9
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 10
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 11
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 12
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 13
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		assertFalse(messages[0], shouldCreateJob[0]);
		assertFalse(messages[1], shouldCreateJob[1]);
		assertTrue(messages[2], shouldCreateJob[2]);
		assertTrue(messages[3], shouldCreateJob[3]);
		assertTrue(messages[4], shouldCreateJob[4]);
		assertTrue(messages[5], shouldCreateJob[5]);
		assertTrue(messages[6], shouldCreateJob[6]);
		assertTrue(messages[7], shouldCreateJob[7]);
		assertTrue(messages[8], shouldCreateJob[8]);
		assertTrue(messages[9], shouldCreateJob[9]);
		assertTrue(messages[10], shouldCreateJob[10]);
		assertTrue(messages[11], shouldCreateJob[11]);
		assertTrue(messages[12], shouldCreateJob[12]);
		assertTrue(messages[13], shouldCreateJob[13]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project {@linkplain DefaultTestReferenceWorkspace#arProject20_C} <b>without</b> its
	 * referenced projects.
	 */
	public void testShouldCreateJob_20C_RefProjectsExcluded() {
		// Local initialization of this test

		Collection<IProject> projectsToLoad = Collections.singletonList(refWks
				.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

		ModelLoadManager.INSTANCE.loadProjects(projectsToLoad, false, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[14];
		// The messages to display in case of violated assertions
		String[] messages = new String[14];

		IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

		int index = -1;
		{ // 0
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 1
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 3
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 5
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 6
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 7
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 8
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 9
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 10
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 11
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 12
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 13
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		assertFalse(messages[0], shouldCreateJob[0]);
		assertFalse(messages[1], shouldCreateJob[1]);
		assertTrue(messages[2], shouldCreateJob[2]);
		assertTrue(messages[3], shouldCreateJob[3]);
		assertTrue(messages[4], shouldCreateJob[4]);
		assertTrue(messages[5], shouldCreateJob[5]);
		assertTrue(messages[6], shouldCreateJob[6]);
		assertTrue(messages[7], shouldCreateJob[7]);
		assertTrue(messages[8], shouldCreateJob[8]);
		assertTrue(messages[9], shouldCreateJob[9]);
		assertTrue(messages[10], shouldCreateJob[10]);
		assertTrue(messages[11], shouldCreateJob[11]);
		assertTrue(messages[12], shouldCreateJob[12]);
		assertTrue(messages[13], shouldCreateJob[13]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project
	 * {@linkplain DefaultTestReferenceWorkspace#getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B)}
	 * <b>with</b> its referenced projects.
	 */
	public void testShouldCreateJob_20B_RefProjectsIncluded() {
		// Local initialization of this test

		Collection<IProject> projectsToLoad = Collections.singletonList(refWks
				.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

		ModelLoadManager.INSTANCE.loadProjects(projectsToLoad, true, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[14];
		// The messages to display in case of violated assertions
		String[] messages = new String[14];

		IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

		int index = -1;
		{ // 0
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 1
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 3
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 5
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 6
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 7
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 8
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 9
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 10
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 11
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 12
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 13
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		assertTrue(messages[0], shouldCreateJob[0]);
		assertTrue(messages[1], shouldCreateJob[1]);
		assertFalse(messages[2], shouldCreateJob[2]);
		assertFalse(messages[3], shouldCreateJob[3]);
		assertTrue(messages[4], shouldCreateJob[4]);
		assertTrue(messages[5], shouldCreateJob[5]);
		assertTrue(messages[6], shouldCreateJob[6]);
		assertTrue(messages[7], shouldCreateJob[7]);
		assertTrue(messages[8], shouldCreateJob[8]);
		assertTrue(messages[9], shouldCreateJob[9]);
		assertTrue(messages[10], shouldCreateJob[10]);
		assertTrue(messages[11], shouldCreateJob[11]);
		assertTrue(messages[12], shouldCreateJob[12]);
		assertTrue(messages[13], shouldCreateJob[13]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project
	 * {@linkplain DefaultTestReferenceWorkspace#getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B)}
	 * <b>without</b> its referenced projects.
	 */
	public void testShouldCreateJob_20B_RefProjectsExcluded() {
		// Local initialization of this test

		Collection<IProject> projectsToLoad = Collections.singletonList(refWks
				.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

		ModelLoadManager.INSTANCE.loadProjects(projectsToLoad, false, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[14];
		// The messages to display in case of violated assertions
		String[] messages = new String[14];

		IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

		int index = -1;
		{ // 0
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 1
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 3
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 5
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 6
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 7
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 8
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 9
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 10
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 11
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 12
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 13
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		assertTrue(messages[0], shouldCreateJob[0]);
		assertTrue(messages[1], shouldCreateJob[1]);
		assertFalse(messages[2], shouldCreateJob[2]);
		assertFalse(messages[3], shouldCreateJob[3]);
		assertTrue(messages[4], shouldCreateJob[4]);
		assertTrue(messages[5], shouldCreateJob[5]);
		assertTrue(messages[6], shouldCreateJob[6]);
		assertTrue(messages[7], shouldCreateJob[7]);
		assertTrue(messages[8], shouldCreateJob[8]);
		assertTrue(messages[9], shouldCreateJob[9]);
		assertTrue(messages[10], shouldCreateJob[10]);
		assertTrue(messages[11], shouldCreateJob[11]);
		assertTrue(messages[12], shouldCreateJob[12]);
		assertTrue(messages[13], shouldCreateJob[13]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project
	 * {@linkplain DefaultTestReferenceWorkspace#getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A)}
	 * <b>with</b> its referenced projects.
	 */
	public void testShouldCreateJob_20A_RefProjectsIncluded() {
		// Local initialization of this test

		Collection<IProject> projectsToLoad = Collections.singletonList(refWks
				.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

		ModelLoadManager.INSTANCE.loadProjects(projectsToLoad, true, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[14];
		// The messages to display in case of violated assertions
		String[] messages = new String[14];

		IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

		int index = -1;
		{ // 0
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 1
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 3
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 5
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 6
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 7
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 8
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 9
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 10
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 11
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 12
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 13
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		assertTrue(messages[0], shouldCreateJob[0]);
		assertTrue(messages[1], shouldCreateJob[1]);
		assertTrue(messages[2], shouldCreateJob[2]);
		assertTrue(messages[3], shouldCreateJob[3]);
		assertFalse(messages[4], shouldCreateJob[4]);
		assertFalse(messages[5], shouldCreateJob[5]);
		assertTrue(messages[6], shouldCreateJob[6]);
		assertTrue(messages[7], shouldCreateJob[7]);
		assertTrue(messages[8], shouldCreateJob[8]);
		assertTrue(messages[9], shouldCreateJob[9]);
		assertTrue(messages[10], shouldCreateJob[10]);
		assertTrue(messages[11], shouldCreateJob[11]);
		assertTrue(messages[12], shouldCreateJob[12]);
		assertTrue(messages[13], shouldCreateJob[13]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project
	 * {@linkplain DefaultTestReferenceWorkspace#getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A)}
	 * <b>without</b> its referenced projects.
	 */
	public void testShouldCreateJob_20A_RefProjectsExcluded() {
		// Local initialization of this test

		Collection<IProject> projectsToLoad = Collections.singletonList(refWks
				.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

		ModelLoadManager.INSTANCE.loadProjects(projectsToLoad, false, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[14];
		// The messages to display in case of violated assertions
		String[] messages = new String[14];

		IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

		int index = -1;
		{ // 0
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 1
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 3
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 5
			Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 6
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 7
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 8
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 9
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 10
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 11
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 12
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, true, mmDescriptor);
		}
		{ // 13
			Collection<IProject> projects = new ArrayList<IProject>();
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_C));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A));
			projects.add(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_E));

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		assertTrue(messages[0], shouldCreateJob[0]);
		assertTrue(messages[1], shouldCreateJob[1]);
		assertTrue(messages[2], shouldCreateJob[2]);
		assertTrue(messages[3], shouldCreateJob[3]);
		assertFalse(messages[4], shouldCreateJob[4]);
		assertFalse(messages[5], shouldCreateJob[5]);
		assertTrue(messages[6], shouldCreateJob[6]);
		assertTrue(messages[7], shouldCreateJob[7]);
		assertTrue(messages[8], shouldCreateJob[8]);
		assertTrue(messages[9], shouldCreateJob[9]);
		assertTrue(messages[10], shouldCreateJob[10]);
		assertTrue(messages[11], shouldCreateJob[11]);
		assertTrue(messages[12], shouldCreateJob[12]);
		assertTrue(messages[13], shouldCreateJob[13]);

		// Ends the test by verifying that everything is fine
		finish();
	}
}
