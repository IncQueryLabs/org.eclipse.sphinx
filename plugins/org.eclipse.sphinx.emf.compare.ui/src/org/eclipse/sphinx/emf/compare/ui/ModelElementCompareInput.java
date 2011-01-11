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
package org.eclipse.sphinx.emf.compare.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.compare.diff.merge.service.MergeService;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.DiffResourceSet;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.metamodel.MatchResourceSet;
import org.eclipse.emf.compare.ui.ICompareInputDetailsProvider;
import org.eclipse.emf.compare.ui.ModelCompareInput;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.compare.ui.internal.Activator;
import org.eclipse.sphinx.emf.compare.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class ModelElementCompareInput extends ModelCompareInput {

	/**
	 * {@inheritDoc}
	 */
	public ModelElementCompareInput(MatchModel matchModel, DiffModel diffModel) {
		super(matchModel, diffModel);
	}

	/**
	 * {@inheritDoc}
	 */
	public ModelElementCompareInput(MatchResourceSet matchResourceset, DiffResourceSet diffResourceSet) {
		super(matchResourceset, diffResourceSet);
	}

	/**
	 * {@inheritDoc}
	 */
	public ModelElementCompareInput(MatchModel matchModel, DiffModel diffModel, ICompareInputDetailsProvider provider) {
		super(matchModel, diffModel, provider);
	}

	/**
	 * {@inheritDoc}
	 */
	public ModelElementCompareInput(MatchResourceSet matchResourceset, DiffResourceSet diffResourceSet, ICompareInputDetailsProvider provider) {
		super(matchResourceset, diffResourceSet, provider);
	}

	@Override
	protected void doCopy(DiffElement element, boolean leftToRight) {
		doTransactionalCopy(leftToRight ? WorkspaceEditingDomainUtil.getEditingDomain(getRightResource()) : WorkspaceEditingDomainUtil
				.getEditingDomain(getLeftResource()), Collections.singletonList(element), leftToRight);
	}

	@Override
	protected void doCopy(List<DiffElement> elements, boolean leftToRight) {
		doTransactionalCopy(leftToRight ? WorkspaceEditingDomainUtil.getEditingDomain(getRightResource()) : WorkspaceEditingDomainUtil
				.getEditingDomain(getLeftResource()), elements, leftToRight);
	}

	protected void doTransactionalCopy(TransactionalEditingDomain editingDomain, final List<DiffElement> elements, final boolean leftToRight) {
		try {
			WorkspaceTransactionUtil.executeInWriteTransaction(editingDomain, new Runnable() {

				public void run() {
					MergeService.merge(elements, leftToRight);
				}
			}, leftToRight ? Messages.action_copyLeftToRight : Messages.action_copyRightToLeft);
		} catch (OperationCanceledException ex) {
			// Nothing to do
		} catch (ExecutionException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}
}
