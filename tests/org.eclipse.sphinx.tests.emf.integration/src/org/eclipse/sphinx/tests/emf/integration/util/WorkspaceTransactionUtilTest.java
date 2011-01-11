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
package org.eclipse.sphinx.tests.emf.integration.util;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultIntegrationTestCase;
import org.eclipse.sphinx.testutils.integration.referenceworkspace.DefaultTestReferenceWorkspace;

@SuppressWarnings("nls")
public class WorkspaceTransactionUtilTest extends DefaultIntegrationTestCase {

	@Override
	protected String[] getProjectsToLoad() {
		return new String[] { DefaultTestReferenceWorkspace.HB_PROJECT_NAME_20_A, DefaultTestReferenceWorkspace.HB_PROJECT_NAME_10_A };
	}

	/**
	 * Test method for {@link WorkspaceTransactionUtil#getOperationHistory(TransactionalEditingDomain editingDomain)} .
	 */
	public void testGetOperationHistory() throws Exception {
		IOperationHistory history = WorkspaceTransactionUtil.getOperationHistory(refWks.editingDomain20);
		assertNotNull(history);
		CommandStack commandStack = refWks.editingDomain20.getCommandStack();
		assertTrue(commandStack instanceof IWorkspaceCommandStack);
		IOperationHistory expectedOperationHistory = ((IWorkspaceCommandStack) commandStack).getOperationHistory();
		assertSame(expectedOperationHistory, history);

		history = WorkspaceTransactionUtil.getOperationHistory(refWks.editingDomain10);
		assertNotNull(history);
		commandStack = refWks.editingDomain10.getCommandStack();
		assertTrue(commandStack instanceof IWorkspaceCommandStack);
		expectedOperationHistory = ((IWorkspaceCommandStack) commandStack).getOperationHistory();
		assertSame(expectedOperationHistory, history);
	}

	/**
	 * Test method for {@link WorkspaceTransactionUtil#getUndoContext(TransactionalEditingDomain editingDomain)} .
	 */
	public void testGetUndoContext() throws Exception {
		IUndoContext undo = WorkspaceTransactionUtil.getUndoContext(refWks.editingDomain20);
		assertNotNull(undo);
		assertEquals(IOperationHistory.GLOBAL_UNDO_CONTEXT, undo);
		undo = WorkspaceTransactionUtil.getUndoContext(refWks.editingDomain10);
		assertNotNull(undo);
		assertEquals(IOperationHistory.GLOBAL_UNDO_CONTEXT, undo);
	}

	/**
	 * Test method for
	 * {@link WorkspaceTransactionUtil#executeInWriteTransaction(TransactionalEditingDomain editingDomain, Runnable runnable, String operationLabel)}
	 * .
	 */
	public void testExecuteInWriteTransaction() throws Exception {
		final String newNameValue = "NewName";

		Resource resource20 = null;
		for (Resource res : refWks.editingDomain20.getResourceSet().getResources()) {
			if (DefaultTestReferenceWorkspace.HB_FILE_NAME_20_20A_1.equals(res.getURI().lastSegment())) {
				resource20 = res;
				break;
			}
		}
		assertNotNull(resource20);
		assertFalse(resource20.getContents().isEmpty());
		EObject object = resource20.getContents().get(0);
		assertTrue(object instanceof Application);
		final Application modelRoot = (Application) object;
		assertFalse(newNameValue.equals(modelRoot.getName()));
		Runnable runnable = new Runnable() {
			public void run() {
				modelRoot.setName(newNameValue);
			}
		};
		WorkspaceTransactionUtil.executeInWriteTransaction(refWks.editingDomain20, runnable, "TestWrite");
		Job.getJobManager().join(IExtendedPlatformConstants.FAMILY_MODEL_LOADING, new NullProgressMonitor());
		assertTrue(newNameValue.equals(modelRoot.getName()));
	}

}
