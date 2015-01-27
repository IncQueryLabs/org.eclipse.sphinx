/**
 * <copyright>
 *
 * Copyright (c)2014 itemis and others.
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
package org.eclipse.sphinx.emf.compare.ui;

import java.util.List;

import org.eclipse.compare.structuremergeviewer.DiffElement;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.ide.ui.internal.editor.ComparisonScopeInput;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.compare.ui.internal.Activator;
import org.eclipse.sphinx.emf.compare.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class ModelElementComparisonScopeInput extends ComparisonScopeInput {

	public ModelElementComparisonScopeInput(IComparisonScope scope, AdapterFactory adapterFactory) {
		super(scope, adapterFactory);
	}

	@Override
	public void copy(boolean leftToRight) {
		System.out.println();
		// FIXME
		// doTransactionalCopy(
		// leftToRight ? WorkspaceEditingDomainUtil.getEditingDomain(getRightResource())
		// : WorkspaceEditingDomainUtil.getEditingDomain(getLeftResource()), Collections.singletonList(element),
		// leftToRight);
	}

	protected void doTransactionalCopy(TransactionalEditingDomain editingDomain, final List<DiffElement> elements, final boolean leftToRight) {
		try {
			WorkspaceTransactionUtil.executeInWriteTransaction(editingDomain, new Runnable() {

				@Override
				public void run() {
					// FIXME MergeService.merge(elements, leftToRight);
				}
			}, leftToRight ? Messages.action_copyLeftToRight : Messages.action_copyRightToLeft);
		} catch (OperationCanceledException ex) {
			// Nothing to do
		} catch (ExecutionException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}
}
