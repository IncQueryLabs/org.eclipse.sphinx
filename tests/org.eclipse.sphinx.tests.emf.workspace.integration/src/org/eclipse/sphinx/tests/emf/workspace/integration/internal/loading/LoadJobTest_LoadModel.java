/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.tests.emf.workspace.integration.internal.loading;

import java.util.Collection;
import java.util.Collections;

import junit.framework.Assert;

import org.eclipse.core.resources.IProject;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.workspace.internal.loading.LoadJob;
import org.eclipse.sphinx.emf.workspace.internal.loading.ModelLoadJob;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.Hummingbird20MMDescriptor;
import org.eclipse.sphinx.examples.uml2.ide.metamodel.UML2MMDescriptor;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultTestReferenceWorkspace;

/**
 * 
 */
@SuppressWarnings({ "restriction" })
public class LoadJobTest_LoadModel extends AbstractLoadJobTest {
	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B };
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * {@linkplain Hummingbird10MMDescriptor} as meta-model descriptor (consider or not its referenced projects is not
	 * relevant in the context of this test and corresponding boolean will be kept to <code>false</code>).
	 */
	public void testShouldCreateJob_20B_hb20RD() {
		// Local initialization of this test

		ModelLoadManager.INSTANCE.loadProject(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B), false,
				Hummingbird10MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[5];
		// The messages to display in case of violated assertions
		String[] messages = new String[5];

		Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

		int index = -1;
		{ // 0
			IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 1
			IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 3
			IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			IMetaModelDescriptor mmDescriptor = null;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertFalse(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * {@linkplain Hummingbird20MMDescriptor} as meta-model descriptor (consider or not its referenced projects is not
	 * relevant in the context of this test and corresponding boolean will be kept to <code>false</code>).
	 */
	public void testShouldCreateJob_20B_Hb20RD() {
		// Local initialization of this test

		ModelLoadManager.INSTANCE.loadProject(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B), false,
				Hummingbird20MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[5];
		// The messages to display in case of violated assertions
		String[] messages = new String[5];

		Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

		int index = -1;
		{ // 0
			IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 1
			IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 3
			IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			IMetaModelDescriptor mmDescriptor = null;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertFalse(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * {@linkplain UML2MMDescriptor} as meta-model descriptor (consider or not its referenced projects is not relevant
	 * in the context of this test and corresponding boolean will be kept to <code>false</code>).
	 */
	public void testShouldCreateJob_20B_UML2MMD() {
		// Local initialization of this test

		ModelLoadManager.INSTANCE.loadProject(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B), false,
				UML2MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[5];
		// The messages to display in case of violated assertions
		String[] messages = new String[5];

		Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

		int index = -1;
		{ // 0
			IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 1
			IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 3
			IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			IMetaModelDescriptor mmDescriptor = null;

			messages[++index] = getMessage(SHOULD_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertFalse(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * {@linkplain MetaModelDescriptorRegistry#ANY_MM} as meta-model descriptor (consider or not its referenced projects
	 * is not relevant in the context of this test and corresponding boolean will be kept to <code>false</code>).
	 * 
	 * @throws Exception
	 */
	public void testShouldCreateJob_20B_ANYMM() throws Exception {
		// Local initialization of this test

		ModelLoadManager.INSTANCE.loadProject(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B), false,
				MetaModelDescriptorRegistry.ANY_MM, true, null);
		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[5];
		// The messages to display in case of violated assertions
		String[] messages = new String[5];

		Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

		int index = -1;
		{ // 0
			IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 1
			IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 3
			IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			IMetaModelDescriptor mmDescriptor = null;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertFalse(messages[0], shouldCreateJob[0]);
		Assert.assertFalse(messages[1], shouldCreateJob[1]);
		Assert.assertFalse(messages[2], shouldCreateJob[2]);
		Assert.assertFalse(messages[3], shouldCreateJob[3]);
		Assert.assertFalse(messages[4], shouldCreateJob[4]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, boolean, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of project {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * <code>null</code> as meta-model descriptor (consider or not its referenced projects is not relevant in the
	 * context of this test and corresponding boolean will be kept to <code>false</code>).
	 */
	public void testShouldCreateJob_20B_null() {
		// Local initialization of this test

		ModelLoadManager.INSTANCE
				.loadProject(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B), false, null, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(ModelLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[5];
		// The messages to display in case of violated assertions
		String[] messages = new String[5];

		Collection<IProject> projects = Collections.singletonList(refWks.getReferenceProject(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B));

		int index = -1;
		{ // 0
			IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 1
			IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 2
			IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 3
			IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}
		{ // 4
			IMetaModelDescriptor mmDescriptor = null;

			messages[++index] = getMessage(SHOULD_NOT_CREATE, projects, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(projects, false, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertFalse(messages[0], shouldCreateJob[0]);
		Assert.assertFalse(messages[1], shouldCreateJob[1]);
		Assert.assertFalse(messages[2], shouldCreateJob[2]);
		Assert.assertFalse(messages[3], shouldCreateJob[3]);
		Assert.assertFalse(messages[4], shouldCreateJob[4]);

		// Ends the test by verifying that everything is fine
		finish();
	}
}
