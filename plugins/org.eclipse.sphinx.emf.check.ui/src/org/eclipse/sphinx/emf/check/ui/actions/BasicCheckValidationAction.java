/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [456869] Duplicated Check problem markers due to URI comparison
 *     itemis - [458976] Validators are not singleton when they implement checks for different EPackages
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.sphinx.emf.check.CheckValidatorRegistry;
import org.eclipse.sphinx.emf.check.ICheckValidator;
import org.eclipse.sphinx.emf.check.catalog.Category;
import org.eclipse.sphinx.emf.check.operations.BasicCheckValidationOperation;
import org.eclipse.sphinx.emf.check.ui.IValidationUIConstants;
import org.eclipse.sphinx.emf.check.ui.dialogs.CategorySelectionContentProvider;
import org.eclipse.sphinx.emf.check.ui.dialogs.CategorySelectionDialog;
import org.eclipse.sphinx.emf.check.ui.dialogs.CategorySelectionLabelProvider;
import org.eclipse.sphinx.emf.check.ui.internal.Activator;
import org.eclipse.sphinx.emf.check.ui.internal.CheckValidationImageProvider;
import org.eclipse.sphinx.emf.edit.TransientItemProvider;
import org.eclipse.sphinx.emf.util.IWrapper;
import org.eclipse.sphinx.platform.jobs.WorkspaceOperationWorkspaceJob;
import org.eclipse.sphinx.platform.ui.operations.RunnableWithProgressAdapter;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * A basic action implementation for performing check-based validation. Given the {@link org.eclipse.emf.ecore.EPackage
 * ePackage} of the input object, the action retrieves the check validator -if any- from the validation registry
 * singleton. Then, whether the validator has an associated check catalog or not, the user is invited to select the set
 * of constraint categories to verify. To launch a validation, the standard
 * {@link org.eclipse.emf.ecore.util.Diagnostician diagnostician} is used. Finally the marker
 * {@link org.eclipse.sphinx.emf.check.services.CheckProblemMarkerService service} is used to display the error markers
 * on the problems view and the check validation view.
 */
public class BasicCheckValidationAction extends BaseSelectionListenerAction {

	private boolean runInBackground;

	public BasicCheckValidationAction() {
		this(IValidationUIConstants.SUBMENU_VALIDATE_LABEL);
		setRunInBackground(false);
	}

	public boolean isRunInBackground() {
		return runInBackground;
	}

	public void setRunInBackground(boolean runInBackground) {
		this.runInBackground = runInBackground;
	}

	protected BasicCheckValidationAction(String text) {
		super(text);
		setImageDescriptor(Activator.getImageDescriptor(CheckValidationImageProvider.CHECK_ICO));
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		return existsValidator();
	}

	protected boolean existsValidator() {
		List<EObject> validationInputs = getValidationInputs();
		for (EObject validationInput : validationInputs) {
			final EPackage ePackage = validationInput.eClass().getEPackage();
			ICheckValidator validator = CheckValidatorRegistry.INSTANCE.getValidator(ePackage);
			if (validator == null) {
				return false;
			}
		}
		return !validationInputs.isEmpty();
	}

	protected List<EObject> getValidationInputs() {
		IStructuredSelection structuredSelection = getStructuredSelection();
		List<EObject> result = new ArrayList<EObject>();
		for (Object obj : structuredSelection.toList()) {
			EObject unwrappedObj = unwrap(obj);
			if (unwrappedObj != null) {
				result.add(unwrappedObj);
			}
		}
		return result;
	}

	protected EObject unwrap(Object object) {
		if (object instanceof IWrapper<?>) {
			Object target = ((IWrapper<?>) object).getTarget();
			if (target instanceof EObject) {
				return (EObject) target;
			}
		} else if (object instanceof EObject) {
			return (EObject) object;
		} else if (object instanceof TransientItemProvider) {
			Notifier target = ((TransientItemProvider) object).getTarget();
			if (target instanceof EObject) {
				return (EObject) target;
			}
		}
		return null;
	}

	@Override
	public void run() {
		// Let the use select the categories
		Set<String> categories = promptForCheckCategories();
		if (categories == null) {
			return;
		}

		final List<EObject> validationInputs = getValidationInputs();

		// Create the check validation operation
		final BasicCheckValidationOperation operation = createCheckValidationOperation(validationInputs, categories);

		if (isRunInBackground()) {
			// Run the check validation operation in a workspace job
			WorkspaceOperationWorkspaceJob job = createWorkspaceOperationJob(operation);
			job.schedule();
		} else {
			// Run the check validation operation in a progress monitor dialog
			try {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(ExtendedPlatformUI.getActiveShell());
				dialog.run(true, true, new RunnableWithProgressAdapter(operation));
			} catch (InterruptedException ex) {
				// Operation has been canceled by user, do nothing
			} catch (InvocationTargetException ex) {
				PlatformLogUtil.logAsError(Activator.getDefault(), ex);
			}
		}
	}

	protected WorkspaceOperationWorkspaceJob createWorkspaceOperationJob(BasicCheckValidationOperation operation) {
		return new WorkspaceOperationWorkspaceJob(operation);
	}

	protected BasicCheckValidationOperation createCheckValidationOperation(List<EObject> validationInputs, Set<String> categories) {
		return new BasicCheckValidationOperation(validationInputs, categories);
	}

	// @return empty set (=> no check catalog), set with selected category ids, or null (=> check catalog but nothing
	// selected)
	private Set<String> promptForCheckCategories() {
		Set<String> selectedCategories = new HashSet<String>();

		// Creates the dialog allowing user to choose categories of constraints to validate
		IStructuredContentProvider contentProvider = createContentProvider();
		ILabelProvider labelProvider = createLabelProvider();
		CategorySelectionDialog dialog = new CategorySelectionDialog(ExtendedPlatformUI.getActiveShell(), new Object(), contentProvider,
				labelProvider, IValidationUIConstants.CONSTRAINT_CATEGORIES_SELECTION_MESSAGE);
		dialog.setTitle(IValidationUIConstants.CONSTRAINT_CATEGORIES_SELECTION_TITLE);
		dialog.setBlockOnOpen(true);

		int result = dialog.open();
		if (result == Window.OK) {
			for (Object resultObject : dialog.getResult()) {
				if (resultObject instanceof Category) {
					selectedCategories.add(((Category) resultObject).getId());
				}
			}
			return selectedCategories;
		}

		return null;
	}

	protected ILabelProvider createLabelProvider() {
		return new CategorySelectionLabelProvider();
	}

	protected IStructuredContentProvider createContentProvider() {
		return new CategorySelectionContentProvider();
	}
}
