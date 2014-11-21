/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.emf.check.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.sphinx.emf.check.AbstractCheckValidator;
import org.eclipse.sphinx.emf.check.CheckModelHelper;
import org.eclipse.sphinx.emf.check.ComposedValidator;
import org.eclipse.sphinx.emf.check.ICheckValidator;
import org.eclipse.sphinx.emf.check.catalog.checkcatalog.Category;
import org.eclipse.sphinx.emf.check.registry.CheckValidatorRegistry;
import org.eclipse.sphinx.emf.check.services.CheckProblemMarkerService;
import org.eclipse.sphinx.emf.check.ui.IValidationUIConstants;
import org.eclipse.sphinx.emf.check.ui.actions.dialog.CategorySelectionContentProvider;
import org.eclipse.sphinx.emf.check.ui.actions.dialog.CategorySelectionDialog;
import org.eclipse.sphinx.emf.check.ui.actions.dialog.CategorySelectionLabelProvider;
import org.eclipse.sphinx.emf.check.ui.internal.Activator;
import org.eclipse.sphinx.emf.check.ui.internal.CheckValidationImageProvider;
import org.eclipse.sphinx.emf.util.BasicWrappingEList;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;

public class BasicCheckValidationAction extends BaseSelectionListenerAction {

	public BasicCheckValidationAction() {
		this(IValidationUIConstants.SUBMENU_VALIDATE_LABEL);
	}

	protected BasicCheckValidationAction(String text) {
		super(text);
		setImageDescriptor(Activator.getImageDescriptor(CheckValidationImageProvider.CHECK_ICO));
	}

	protected Object getSelectedObject() {
		return getStructuredSelection().getFirstElement();
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return selection.size() == 1 && getSelectedObject() != null;
	}

	protected Object getValidationInput() {
		Object selectedObject = getSelectedObject();
		// model object
		if (selectedObject instanceof EObject) {
			return selectedObject;
		}
		// model resource
		Resource resource = EcorePlatformUtil.getResource(selectedObject);
		if (resource != null && !resource.getContents().isEmpty()) {
			return resource.getContents().get(0);
		}
		return null;
	}

	@Override
	public void run() {
		final EObject validationInput = unwrap(getValidationInput());
		if (validationInput != null) {
			final EPackage ePackage = validationInput.eClass().getEPackage();
			try {
				// get the associated validator
				final EValidator validator = CheckValidatorRegistry.INSTANCE.getValidator(ePackage);
				Assert.isNotNull(validator);

				// query validation sets from user
				final Set<String> validationSets = queryValidationSets(validator);
				if (validationSets == null) {
					return;
				}
				// set the validation categories for the current check validator
				if (validator instanceof AbstractCheckValidator) {
					((ICheckValidator) validator).setFilter(validationSets);
				} else if (validator instanceof ComposedValidator) {
					((ComposedValidator) validator).setFilter(validationSets);
				}

				// launch validation
				try {
					IRunnableWithProgress operation = new WorkspaceModifyDelegatingOperation(new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							// use standard entry point
							final Diagnostic diagnostic = Diagnostician.INSTANCE.validate(validationInput);
							// generate error markers and update check validation view
							CheckProblemMarkerService.INSTANCE.updateProblemMarkers(validationInput, diagnostic);
						}
					});
					// Run the validation operation, and show progress
					new ProgressMonitorDialog(ExtendedPlatformUI.getActiveShell()).run(true, true, operation);
				} catch (Exception ex) {
					PlatformLogUtil.logAsError(Activator.getDefault(), ex);
				}
			} catch (CoreException ex) {
				PlatformLogUtil.logAsError(Activator.getDefault(), ex);
			}
		}
	}

	protected EObject unwrap(Object object) {
		// TODO support other case like transient item provider...
		if (object instanceof BasicWrappingEList.IWrapper<?>) {
			Object target = ((BasicWrappingEList.IWrapper<?>) object).getTarget();
			if (target instanceof EObject) {
				return (EObject) target;
			}
		} else if (object instanceof EObject) {
			return (EObject) object;
		}
		return null;
	}

	private Set<String> queryValidationSets(EValidator validator) {
		Shell shell = ExtendedPlatformUI.getActiveShell();
		IStructuredContentProvider contentProvider = createContentProvider(validator);
		ILabelProvider labelProvider = createLabelProvider();

		// Creates the dialog allowing user to choose categories of constraints to validate
		CategorySelectionDialog dialog = new CategorySelectionDialog(shell, new Object(), contentProvider, labelProvider,
				IValidationUIConstants.CONSTRAINT_CATEGORIES_SELECTION_MESSAGE);
		dialog.setTitle(IValidationUIConstants.CONSTRAINT_CATEGORIES_SELECTION_TITLE);
		dialog.setBlockOnOpen(true);

		Set<String> selectedCategories = new HashSet<String>();
		int result = dialog.open();
		if (result == Window.OK) {
			for (Object obj : dialog.getResult()) {
				if (obj instanceof Category) {
					selectedCategories.add(((Category) obj).getId());
				}
			}
			return selectedCategories;
		}

		return null;
	}

	protected ILabelProvider createLabelProvider() {
		return new CategorySelectionLabelProvider();
	}

	protected IStructuredContentProvider createContentProvider(EValidator validator) {
		Set<CheckModelHelper> helpers = new HashSet<CheckModelHelper>();
		if (validator instanceof AbstractCheckValidator) {
			helpers.add(((ICheckValidator) validator).getCheckModelHelper());
			return new CategorySelectionContentProvider(helpers);
		} else if (validator instanceof ComposedValidator) {
			List<EValidator> delegates = ((ComposedValidator) validator).getDelegates();
			for (EValidator delegate : delegates) {
				if (delegate instanceof AbstractCheckValidator) {
					CheckModelHelper helper = ((ICheckValidator) delegate).getCheckModelHelper();
					helpers.add(helper);
				} else {
					throw new RuntimeException("Could not recognize type of delegate validator"); //$NON-NLS-1$
				}
			}
			return new CategorySelectionContentProvider(helpers);
		}
		throw new RuntimeException("Could not recognize type of validator"); //$NON-NLS-1$
	}
}
