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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.workspace.internal.loading.FileLoadJob;
import org.eclipse.sphinx.emf.workspace.internal.loading.LoadJob;
import org.eclipse.sphinx.emf.workspace.loading.ModelLoadManager;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.Hummingbird20MMDescriptor;
import org.eclipse.sphinx.examples.uml2.ide.metamodel.UML2MMDescriptor;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultTestReferenceWorkspace;

/**
 * 
 */
@SuppressWarnings({ "restriction" })
public class LoadJobTest_LoadFiles extends AbstractLoadJobTest {
	private IFile hbFile10_10A_1;
	private IFile hbFile10_10A_2;
	private IFile hbFile10_10A_3;

	private IFile hbFile10_10B_1;
	private IFile hbFile10_10B_2;
	private IFile hbFile10_10B_3;

	private IFile hbFile20_20B_1;
	private IFile hbFile20_20B_2;
	private IFile hbFile20_20B_3;

	private IFile uml2File_20B_1;
	private IFile uml2File_20B_2;
	private IFile uml2File_20B_3;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		hbFile10_10A_1 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A,
				DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_1);
		hbFile10_10A_2 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A,
				DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_2);
		hbFile10_10A_3 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A,
				DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10A_3);

		hbFile10_10B_1 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_B,
				DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10B_1);
		hbFile10_10B_2 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_B,
				DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10B_2);
		hbFile10_10B_3 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_B,
				DefaultTestReferenceWorkspace.HB_FILE_NAME_10_10B_3);

		hbFile20_20B_1 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B,
				DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20B_1);
		hbFile20_20B_2 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B,
				DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20B_2);
		hbFile20_20B_3 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B,
				DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20B_3);

		uml2File_20B_1 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B,
				DefaultTestReferenceWorkspace.UML2_FILE_NAME_20B_1);
		uml2File_20B_2 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B,
				DefaultTestReferenceWorkspace.UML2_FILE_NAME_20B_2);
		uml2File_20B_3 = refWks.getReferenceFile(DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B,
				DefaultTestReferenceWorkspace.UML2_FILE_NAME_20B_3);
	}

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_B,
				DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_B };
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of file {@linkplain DefaultTestReferenceWorkspace#HB_FILE_NAME_10_10A_1} specifying
	 * {@linkplain MetaModelDescriptorRegistry#ANY_MM} as meta-model descriptor.
	 */
	public void testShouldCreateJob_10A_oneFile() {
		// Local initialization of this test

		Collection<IFile> filesToLoad = Collections.singletonList(hbFile10_10A_1);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, MetaModelDescriptorRegistry.ANY_MM, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[6];
		// The messages to display in case of violated assertions
		String[] messages = new String[6];

		IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

		int index = -1;
		{ // 0
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_1);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 1
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_2);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 2
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10B_1);
			files.add(hbFile10_10B_2);
			files.add(hbFile10_10B_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 5
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertFalse(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of files from project {@linkplain DefaultTestReferenceWorkspace#arProject10_A} specifying
	 * {@linkplain MetaModelDescriptorRegistry#ANY_MM} as meta-model descriptor.
	 */
	public void testShouldCreateJob_10A_ANYMM() {
		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile10_10A_1);
		filesToLoad.add(hbFile10_10A_2);
		filesToLoad.add(hbFile10_10A_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, MetaModelDescriptorRegistry.ANY_MM, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[12];
		// The messages to display in case of violated assertions
		String[] messages = new String[12];

		int index = -1;

		IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

		{ // 0
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_1);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 1
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_2);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 2
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_3);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10B_1);
			files.add(hbFile10_10B_2);
			files.add(hbFile10_10B_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 5
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}

		mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;

		{ // 6
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_1);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 7
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_2);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 8
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_3);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 9
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 10
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10B_1);
			files.add(hbFile10_10B_2);
			files.add(hbFile10_10B_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 11
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertFalse(messages[0], shouldCreateJob[0]);
		Assert.assertFalse(messages[1], shouldCreateJob[1]);
		Assert.assertFalse(messages[2], shouldCreateJob[2]);
		Assert.assertFalse(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertFalse(messages[6], shouldCreateJob[6]);
		Assert.assertFalse(messages[7], shouldCreateJob[7]);
		Assert.assertFalse(messages[8], shouldCreateJob[8]);
		Assert.assertFalse(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of files from project {@linkplain DefaultTestReferenceWorkspace#arProject10_A} specifying
	 * {@linkplain Hummingbird10MMDescriptor} as meta-model descriptor.
	 */
	public void testShouldCreateJob_10A_Hb10RD() {
		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile10_10A_1);
		filesToLoad.add(hbFile10_10A_2);
		filesToLoad.add(hbFile10_10A_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, Hummingbird10MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[12];
		// The messages to display in case of violated assertions
		String[] messages = new String[12];

		int index = -1;

		IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

		{ // 0
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_1);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 1
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_2);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 2
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10B_1);
			files.add(hbFile10_10B_2);
			files.add(hbFile10_10B_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 5
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}

		mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;

		{ // 6
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_1);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 7
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_2);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 8
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_3);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 9
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 10
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10B_1);
			files.add(hbFile10_10B_2);
			files.add(hbFile10_10B_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 11
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);

			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertFalse(messages[6], shouldCreateJob[6]);
		Assert.assertFalse(messages[7], shouldCreateJob[7]);
		Assert.assertFalse(messages[8], shouldCreateJob[8]);
		Assert.assertFalse(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of files from project {@linkplain DefaultTestReferenceWorkspace#arProject10_A} specifying
	 * {@linkplain UML2MMDescriptor} as meta-model descriptor.
	 */
	public void testShouldCreateJob_10A_UML2MMD() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile10_10A_1);
		filesToLoad.add(hbFile10_10A_2);
		filesToLoad.add(hbFile10_10A_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, UML2MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[6];
		// The messages to display in case of violated assertions
		String[] messages = new String[6];

		IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;

		int index = -1;
		{ // 0
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_1);
			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 1
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_2);
			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 2
			Collection<IFile> files = Collections.singletonList(hbFile10_10A_3);
			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);
			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10B_1);
			files.add(hbFile10_10B_2);
			files.add(hbFile10_10B_3);
			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}
		{ // 5
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
			shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of files from project {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * {@linkplain MetaModelDescriptorRegistry#ANY_MM} as meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_AllFiles_ANYMM() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile20_20B_1);
		filesToLoad.add(hbFile20_20B_2);
		filesToLoad.add(hbFile20_20B_3);
		filesToLoad.add(uml2File_20B_1);
		filesToLoad.add(uml2File_20B_2);
		filesToLoad.add(uml2File_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, MetaModelDescriptorRegistry.ANY_MM, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertFalse(messages[0], shouldCreateJob[0]);
		Assert.assertFalse(messages[1], shouldCreateJob[1]);
		Assert.assertFalse(messages[2], shouldCreateJob[2]);
		Assert.assertFalse(messages[3], shouldCreateJob[3]);
		Assert.assertFalse(messages[4], shouldCreateJob[4]);
		Assert.assertFalse(messages[5], shouldCreateJob[5]);
		Assert.assertFalse(messages[6], shouldCreateJob[6]);
		Assert.assertFalse(messages[7], shouldCreateJob[7]);
		Assert.assertFalse(messages[8], shouldCreateJob[8]);
		Assert.assertFalse(messages[9], shouldCreateJob[9]);
		Assert.assertFalse(messages[10], shouldCreateJob[10]);
		Assert.assertFalse(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of files from project {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * {@linkplain Hummingbird10MMDescriptor} as meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_AllFiles_Hb10RD() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile20_20B_1);
		filesToLoad.add(hbFile20_20B_2);
		filesToLoad.add(hbFile20_20B_3);
		filesToLoad.add(uml2File_20B_1);
		filesToLoad.add(uml2File_20B_2);
		filesToLoad.add(uml2File_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, Hummingbird10MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertFalse(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertFalse(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertFalse(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of files from project {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * {@linkplain Hummingbird20MMDescriptor} as meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_AllFiles_Hb20RD() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile20_20B_1);
		filesToLoad.add(hbFile20_20B_2);
		filesToLoad.add(hbFile20_20B_3);
		filesToLoad.add(uml2File_20B_1);
		filesToLoad.add(uml2File_20B_2);
		filesToLoad.add(uml2File_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, Hummingbird20MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertFalse(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertFalse(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertFalse(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of files from project {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * {@linkplain UML2MMDescriptor} as meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_AllFiles_UML2MMD() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile20_20B_1);
		filesToLoad.add(hbFile20_20B_2);
		filesToLoad.add(hbFile20_20B_3);
		filesToLoad.add(uml2File_20B_1);
		filesToLoad.add(uml2File_20B_2);
		filesToLoad.add(uml2File_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, UML2MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();

		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertFalse(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertFalse(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertFalse(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Hummingbird 10 files from project
	 * {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * {@linkplain MetaModelDescriptorRegistry#ANY_MM} as meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_Hb10Files_ANYMM() {

		// Local initialization of this test

		ModelLoadManager.INSTANCE.loadFiles(Collections.<IFile> emptyList(), MetaModelDescriptorRegistry.ANY_MM, true, null);

		// Verify prerequisites assertions
		assertNoLoadJobIsSleeping();

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);
			// 1011 1010 1001 1111 1100
			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// 1111 1110 1001 1111 1100
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Hummingbird 10 files from project
	 * {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying {@linkplain Hummingbird10MMDescriptor} as
	 * meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_Hb10Files_Hb10RD() {

		// Local initialization of this test

		ModelLoadManager.INSTANCE.loadFiles(Collections.<IFile> emptyList(), Hummingbird10MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertNoLoadJobIsSleeping();

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// 1011 1010 1001 1111 1100
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Hummingbird 10 files from project
	 * {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying {@linkplain Hummingbird20MMDescriptor} as
	 * meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_Hb10Files_Hb20RD() {

		// Local initialization of this test

		ModelLoadManager.INSTANCE.loadFiles(Collections.<IFile> emptyList(), Hummingbird20MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertNoLoadJobIsSleeping();

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Hummingbird 10 files from project
	 * {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying {@linkplain UML2MMDescriptor} as meta-model
	 * descriptor.
	 */
	public void testShouldCreateJob_20B_Hb10Files_UML2MMD() {

		// Local initialization of this test

		ModelLoadManager.INSTANCE.loadFiles(Collections.<IFile> emptyList(), UML2MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertNoLoadJobIsSleeping();

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Hummingbird 20 files from project
	 * {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying
	 * {@linkplain MetaModelDescriptorRegistry#ANY_MM} as meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_Hb20Files_ANYMM() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile20_20B_1);
		filesToLoad.add(hbFile20_20B_2);
		filesToLoad.add(hbFile20_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, MetaModelDescriptorRegistry.ANY_MM, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertFalse(messages[4], shouldCreateJob[4]);
		Assert.assertFalse(messages[5], shouldCreateJob[5]);
		Assert.assertFalse(messages[6], shouldCreateJob[6]);
		Assert.assertFalse(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Hummingbird 20 files from project
	 * {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying {@linkplain Hummingbird10MMDescriptor} as
	 * meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_Hb20Files_Hb10RD() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile20_20B_1);
		filesToLoad.add(hbFile20_20B_2);
		filesToLoad.add(hbFile20_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, Hummingbird10MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertFalse(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Hummingbird 20 files from project
	 * {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying {@linkplain Hummingbird20MMDescriptor} as
	 * meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_Hb20Files_Hb20RD() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile20_20B_1);
		filesToLoad.add(hbFile20_20B_2);
		filesToLoad.add(hbFile20_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, Hummingbird20MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertFalse(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Hummingbird 20 files from project
	 * {@linkplain DefaultTestReferenceWorkspace#arProject20_B} specifying {@linkplain UML2MMDescriptor} as meta-model
	 * descriptor.
	 */
	public void testShouldCreateJob_20B_Hb20Files_UML2MMD() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(hbFile20_20B_1);
		filesToLoad.add(hbFile20_20B_2);
		filesToLoad.add(hbFile20_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, UML2MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertFalse(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Uml2 files from project {@linkplain DefaultTestReferenceWorkspace#arProject20_B}
	 * specifying {@linkplain MetaModelDescriptorRegistry#ANY_MM} as meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_UML2Files_ANYMM() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(uml2File_20B_1);
		filesToLoad.add(uml2File_20B_2);
		filesToLoad.add(uml2File_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, MetaModelDescriptorRegistry.ANY_MM, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertFalse(messages[8], shouldCreateJob[8]);
		Assert.assertFalse(messages[9], shouldCreateJob[9]);
		Assert.assertFalse(messages[10], shouldCreateJob[10]);
		Assert.assertFalse(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	//
	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Uml2 files from project {@linkplain DefaultTestReferenceWorkspace#arProject20_B}
	 * specifying {@linkplain Hummingbird10MMDescriptor} as meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_Uml2Files_Hb10RD() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(uml2File_20B_1);
		filesToLoad.add(uml2File_20B_2);
		filesToLoad.add(uml2File_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, Hummingbird10MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertFalse(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Uml2 files from project {@linkplain DefaultTestReferenceWorkspace#arProject20_B}
	 * specifying {@linkplain Hummingbird20MMDescriptor} as meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_Uml2Files_Hb20RD() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(uml2File_20B_1);
		filesToLoad.add(uml2File_20B_2);
		filesToLoad.add(uml2File_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, Hummingbird20MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertFalse(messages[10], shouldCreateJob[10]);
		Assert.assertTrue(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

	/**
	 * Test method for
	 * {@linkplain LoadJob#shouldCreateJob(java.util.Collection, org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor)}
	 * <p>
	 * Test made on loading of Uml2 files from project {@linkplain DefaultTestReferenceWorkspace#arProject20_B}
	 * specifying {@linkplain UML2MMDescriptor} as meta-model descriptor.
	 */
	public void testShouldCreateJob_20B_Uml2Files_UML2MMD() {

		// Local initialization of this test

		Collection<IFile> filesToLoad = new ArrayList<IFile>();
		filesToLoad.add(uml2File_20B_1);
		filesToLoad.add(uml2File_20B_2);
		filesToLoad.add(uml2File_20B_3);

		ModelLoadManager.INSTANCE.loadFiles(filesToLoad, UML2MMDescriptor.INSTANCE, true, null);

		// Verify prerequisites assertions
		assertOnlyOneLoadJobIsSleeping(FileLoadJob.class);

		// The results of the method under test
		boolean[] shouldCreateJob = new boolean[20];
		// The messages to display in case of violated assertions
		String[] messages = new String[20];

		int index = -1;

		{ // 0
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 0.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 0.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 1
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);

			{ // 1.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 1.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 2
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);

			{ // 2.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 2.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_NOT_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 3
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile20_20B_1);
			files.add(hbFile20_20B_2);
			files.add(hbFile20_20B_3);
			files.add(uml2File_20B_1);
			files.add(uml2File_20B_2);
			files.add(uml2File_20B_3);
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 3.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 3.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}
		{ // 4
			Collection<IFile> files = new ArrayList<IFile>();
			files.add(hbFile10_10A_1);
			files.add(hbFile10_10A_2);
			files.add(hbFile10_10A_3);

			{ // 4.0
				IMetaModelDescriptor mmDescriptor = MetaModelDescriptorRegistry.ANY_MM;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.1
				IMetaModelDescriptor mmDescriptor = Hummingbird10MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.2
				IMetaModelDescriptor mmDescriptor = Hummingbird20MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
			{ // 4.3
				IMetaModelDescriptor mmDescriptor = UML2MMDescriptor.INSTANCE;
				messages[++index] = getMessage(SHOULD_CREATE, files, mmDescriptor);
				shouldCreateJob[index] = LoadJob.shouldCreateJob(files, mmDescriptor);
			}
		}

		// Wake up the model load job that we put sleeping before
		wakeUp();
		// Check assertions
		Assert.assertTrue(messages[0], shouldCreateJob[0]);
		Assert.assertTrue(messages[1], shouldCreateJob[1]);
		Assert.assertTrue(messages[2], shouldCreateJob[2]);
		Assert.assertTrue(messages[3], shouldCreateJob[3]);
		Assert.assertTrue(messages[4], shouldCreateJob[4]);
		Assert.assertTrue(messages[5], shouldCreateJob[5]);
		Assert.assertTrue(messages[6], shouldCreateJob[6]);
		Assert.assertTrue(messages[7], shouldCreateJob[7]);
		Assert.assertTrue(messages[8], shouldCreateJob[8]);
		Assert.assertTrue(messages[9], shouldCreateJob[9]);
		Assert.assertTrue(messages[10], shouldCreateJob[10]);
		Assert.assertFalse(messages[11], shouldCreateJob[11]);
		Assert.assertTrue(messages[12], shouldCreateJob[12]);
		Assert.assertTrue(messages[13], shouldCreateJob[13]);
		Assert.assertTrue(messages[14], shouldCreateJob[14]);
		Assert.assertTrue(messages[15], shouldCreateJob[15]);
		Assert.assertTrue(messages[16], shouldCreateJob[16]);
		Assert.assertTrue(messages[17], shouldCreateJob[17]);
		Assert.assertTrue(messages[18], shouldCreateJob[18]);
		Assert.assertTrue(messages[19], shouldCreateJob[19]);

		// Ends the test by verifying that everything is fine
		finish();
	}

}
