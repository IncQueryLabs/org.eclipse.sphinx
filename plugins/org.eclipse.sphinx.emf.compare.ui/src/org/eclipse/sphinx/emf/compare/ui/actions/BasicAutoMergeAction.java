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
package org.eclipse.sphinx.emf.compare.ui.actions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.compare.diff.merge.service.MergeService;
import org.eclipse.emf.compare.diff.metamodel.ComparisonSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.util.AdapterUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.compare.ui.internal.Activator;
import org.eclipse.sphinx.emf.compare.ui.internal.messages.Messages;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * 
 */
public class BasicAutoMergeAction extends BaseSelectionListenerAction implements ISelectionChangedListener {

	/**
	 * The comparison snapshot owning the match and diff models. This snapshot is needed in order to create the compare
	 * editor input.
	 */
	protected ComparisonSnapshot comparisonSnapshot;

	/**
	 * The selected objects that must be compared.
	 */
	protected List<WeakReference<EObject>> selectedObjects = null;
	/**
	 * The selected files that must be compared.
	 */
	protected List<WeakReference<IFile>> selectedFiles = null;

	/**
	 * Constructor.
	 */
	public BasicAutoMergeAction() {
		super(Messages.action_mergeWithEachOther);
		// TODO Add action description
		// setDescription(TO BE DEFINED);
	}

	/**
	 * @param selection
	 *            The selection in the viewer onto which this action should perform an operation.
	 * @return <ul>
	 *         <li><tt><b>true</b>&nbsp;&nbsp;</tt> if compare action is available (i.e. if {@link IStructuredSelection
	 *         selection} matches enablement criteria);</li>
	 *         <li><tt><b>false</b>&nbsp;</tt> otherwise.</li>
	 *         </ul>
	 */
	@Override
	public boolean updateSelection(IStructuredSelection selection) {
		Assert.isNotNull(selection);

		if (selection.size() != 2) {
			return false;
		}

		// Reset attributes
		comparisonSnapshot = null;
		selectedFiles = null;
		selectedObjects = null;

		for (Iterator<?> it = selection.iterator(); it.hasNext();) {
			Object obj = it.next();

			if (obj instanceof EObject) {
				if (selectedObjects == null) {
					selectedObjects = new ArrayList<WeakReference<EObject>>();
				}
				selectedObjects.add(new WeakReference<EObject>((EObject) obj));
			} else if (obj instanceof IFile) {
				IFile file = (IFile) obj;
				if (MetaModelDescriptorRegistry.INSTANCE.getDescriptor(file) != null) {
					if (selectedFiles == null) {
						selectedFiles = new ArrayList<WeakReference<IFile>>();
					}
					selectedFiles.add(new WeakReference<IFile>(file));
				}
			}
		}
		return selectedFiles != null ? selectedFiles.size() == 2 : false ^ selectedObjects != null ? selectedObjects.size() == 2 : false;
	}

	@Override
	public void run() {
		EObject leftObject = null;
		EObject rightObject = null;
		if (selectedObjects != null && selectedObjects.size() == 2) {
			leftObject = selectedObjects.get(0).get();
			rightObject = selectedObjects.get(1).get();
		} else if (selectedFiles != null && selectedFiles.size() == 2) {
			leftObject = getModelRoot(selectedFiles.get(0).get());
			rightObject = getModelRoot(selectedFiles.get(1).get());
		}
		if (leftObject == null || rightObject == null) {
			return;
		}
		// Ask user for the direction of the merge
		int direction = promptMergeDirection(leftObject, rightObject);

		/*
		 * !! Important note !! Performs the automatic merge inside a write-transaction in order to be able to offer the
		 * possibility to cancel the entire merge operation in one and only one call to the cancel operation. Without
		 * this, if user wants to cancel its merge, he has to cancel one by one all the operations that have been
		 * executed during merge; which may be very long process!
		 */

		final EObject rightEObject = rightObject;
		final EObject leftEObject = leftObject;

		switch (direction) {
		case -1: // Merge is cancelled.
			break;
		case 0: // Merge from "Left to Right"
			mergeModelElements(rightEObject, leftEObject, true);
			break;
		case 1: // Merge from "Right to Left"
			mergeModelElements(rightEObject, leftEObject, false);
			break;
		}
	}

	/**
	 * @param file
	 *            The file whose root model object must be returned.
	 * @return The root object of the model contained in the specified {@link IFile file}.
	 */
	protected EObject getModelRoot(IFile file) {
		Assert.isNotNull(file);

		// Get model from workspace file and force it to be loaded in case that this has not been done yet
		return EcorePlatformUtil.loadModelRoot(file);
	}

	/**
	 * @param leftObject
	 *            The left element supposed to be merged.
	 * @param rightObject
	 *            The right element supposed to be merged.
	 * @return <ul>
	 *         <li><code>&nbsp;0</code> if merge must be made from <em><b>Left to Right</b></em>;</li>
	 *         <li><code>&nbsp;1</code> if merge must be made from <em><b>Right to Left</b></em>;</li>
	 *         <li><code>-1</code> if user cancelled operation.</li>
	 *         </ul>
	 */
	protected int promptMergeDirection(EObject leftObject, EObject rightObject) {
		ILabelProvider labelProvider = new AdapterFactoryLabelProvider(AdapterUtils.getAdapterFactory());

		Resource leftResource = leftObject.eResource();
		IFile leftFile = EcorePlatformUtil.getFile(leftResource);
		IProject leftProject = leftFile.getProject();

		Resource rightResource = rightObject.eResource();
		IFile rightFile = EcorePlatformUtil.getFile(rightResource);
		IProject rightProject = rightFile.getProject();

		String leftProperties = NLS.bind(Messages.dlg_mergeAuto_messageLeftProperties, new Object[] { leftProject.getName(), leftFile.getName(),
				labelProvider.getText(leftObject) });
		String rightProperties = NLS.bind(Messages.dlg_mergeAuto_messageRightProperties, new Object[] { rightProject.getName(), rightFile.getName(),
				labelProvider.getText(rightObject) });

		String[] dialogButtonLabels = new String[] { Messages.dlg_mergeAuto_buttonLabel_leftToRight, Messages.dlg_mergeAuto_buttonLabel_rightToLeft,
				IDialogConstants.CANCEL_LABEL };

		MessageDialog dialog = new MessageDialog(ExtendedPlatformUI.getActiveShell(), Messages.dlg_mergeAuto_title, null,
				Messages.dlg_mergeAuto_message + "\n" + leftProperties + "\n" + rightProperties, //$NON-NLS-1$ //$NON-NLS-2$
				MessageDialog.QUESTION, dialogButtonLabels, 0);

		// Open the dialog and get the user's choice
		int result = dialog.open();

		if (result == 2) {
			result = -1;
		}
		return result;
	}

	/**
	 * @param rightEObject
	 * @param leftEObject
	 * @param leftToRight
	 */
	protected void mergeModelElements(final EObject rightEObject, final EObject leftEObject, final boolean leftToRight) {

		final TransactionalEditingDomain rightEditingDomain = WorkspaceEditingDomainUtil.getEditingDomain(rightEObject);
		final TransactionalEditingDomain leftEditingDomain = WorkspaceEditingDomainUtil.getEditingDomain(leftEObject);
		// Same editing domain on left and right, create one write transaction
		if (rightEditingDomain.equals(leftEditingDomain)) {
			executeInIndividualTransaction(rightEObject, leftEObject, leftEditingDomain, leftToRight);
		} else { // Different editing domain on left and right, create two aggregate write transaction where aggregation
			// depends on merge direction
			if (leftToRight) {
				// Open a write transaction on the right model which aggregates a write transaction on the left one.
				executeInAggregateTransactions(rightEObject, leftEObject, rightEditingDomain, leftEditingDomain, leftToRight);
			} else {
				// Open a write transaction on the left model which aggregates a write transaction on the right one.
				executeInAggregateTransactions(rightEObject, leftEObject, leftEditingDomain, rightEditingDomain, leftToRight);
			}
		}
	}

	/**
	 * @param rightEObject
	 * @param leftEObject
	 * @param leftEditingDomain
	 * @param leftToRight
	 */
	protected void executeInIndividualTransaction(final EObject rightEObject, final EObject leftEObject,
			final TransactionalEditingDomain leftEditingDomain, final boolean leftToRight) {
		try {
			WorkspaceTransactionUtil.executeInWriteTransaction(leftEditingDomain, new Runnable() {
				public void run() {
					doRun(rightEObject, leftEObject, leftToRight);
				}
			}, Messages.action_mergeWithEachOther);
		} catch (OperationCanceledException ex) {
			// Nothing to do
		} catch (ExecutionException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}

	/**
	 * @param rightEObject
	 * @param leftEObject
	 * @param outerTransactionEditingDomain
	 * @param innerTransactionEditingDomain
	 * @param leftToRight
	 */
	protected void executeInAggregateTransactions(final EObject rightEObject, final EObject leftEObject,
			final TransactionalEditingDomain outerTransactionEditingDomain, final TransactionalEditingDomain innerTransactionEditingDomain,
			final boolean leftToRight) {
		try {
			WorkspaceTransactionUtil.executeInWriteTransaction(outerTransactionEditingDomain, new Runnable() {
				public void run() {
					try {
						WorkspaceTransactionUtil.executeInWriteTransaction(innerTransactionEditingDomain, new Runnable() {
							public void run() {
								doRun(rightEObject, leftEObject, leftToRight);
							}
						}, Messages.action_mergeWithEachOther);
					} catch (OperationCanceledException ex) {
						// Nothing to do
					} catch (ExecutionException ex) {
						PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
					}
				}
			}, Messages.action_mergeWithEachOther);
		} catch (OperationCanceledException ex) {
			// Nothing to do
		} catch (ExecutionException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}

	/**
	 * @param rightEObject
	 * @param leftEObject
	 * @param leftToRight
	 */
	protected void doRun(final EObject rightEObject, final EObject leftEObject, final boolean leftToRight) {
		try {
			MatchModel matchModel = MatchService.doContentMatch(leftEObject, rightEObject, null);
			DiffModel diffModel = DiffService.doDiff(matchModel);

			// Request the Merge Service to automatically merge the selected model elements
			MergeService.merge(diffModel.getOwnedElements(), leftToRight);
		} catch (InterruptedException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}
}
