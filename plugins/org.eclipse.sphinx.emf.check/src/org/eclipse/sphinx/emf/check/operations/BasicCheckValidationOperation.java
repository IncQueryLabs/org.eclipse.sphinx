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
package org.eclipse.sphinx.emf.check.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sphinx.emf.check.ICheckValidator;
import org.eclipse.sphinx.emf.check.internal.messages.Messages;
import org.eclipse.sphinx.emf.check.services.CheckProblemMarkerService;
import org.eclipse.sphinx.platform.operations.AbstractWorkspaceOperation;

public class BasicCheckValidationOperation extends AbstractWorkspaceOperation {

	private List<EObject> inputs;
	private Set<String> categories;

	public BasicCheckValidationOperation(List<EObject> inputs, Set<String> categories) {
		super(Messages.operation_checkValidation_label);
		Assert.isNotNull(inputs);
		Assert.isNotNull(categories);

		this.categories = categories;
		this.inputs = inputs;
	}

	@Override
	public ISchedulingRule getRule() {
		// TODO Compute scheduling rules
		return null;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {

		try {
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					Map<Object, Object> contextEntries = new HashMap<Object, Object>();
					contextEntries.put(ICheckValidator.OPTION_CATEGORIES, categories.toArray(new String[categories.size()]));
					for (EObject input : inputs) {
						Diagnostic diagnostic = Diagnostician.INSTANCE.validate(input, contextEntries);
						updateProblemMarkers(input, diagnostic);
					}
				}
			};

			TransactionalEditingDomain editingDomain = getEditingDomain(inputs.get(0));
			if (editingDomain != null) {
				editingDomain.runExclusive(runnable);
			} else {
				runnable.run();
			}
		} catch (OperationCanceledException ex) {
			// Nothing to do
		} catch (InterruptedException ex) {
			// Nothing to do
		}
	}

	protected TransactionalEditingDomain getEditingDomain(EObject input) {
		return TransactionUtil.getEditingDomain(input);
	}

	protected Set<EPackage> getEPacakges(EObject input) {
		Set<EPackage> result = new HashSet<EPackage>();
		List<EObject> objectsToValidate = getObjectsToValidate(input);
		for (EObject obj : objectsToValidate) {
			result.add(obj.eClass().getEPackage());
		}
		return result;
	}

	protected List<EObject> getObjectsToValidate(EObject input) {
		List<EObject> result = new ArrayList<EObject>();
		result.add(input);
		TreeIterator<EObject> iter = input.eAllContents();
		while (iter.hasNext()) {
			result.add(iter.next());

		}
		return result;
	}

	/**
	 * Generate error markers and update check validation view.
	 */
	protected void updateProblemMarkers(EObject eObject, final Diagnostic diagnostic) {
		CheckProblemMarkerService.INSTANCE.updateProblemMarkers(eObject, diagnostic);
	}
}
