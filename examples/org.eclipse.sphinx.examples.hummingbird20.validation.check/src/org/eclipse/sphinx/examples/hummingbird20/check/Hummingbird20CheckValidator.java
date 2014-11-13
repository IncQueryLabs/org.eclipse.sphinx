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

package org.eclipse.sphinx.examples.hummingbird20.check;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.sphinx.emf.check.AbstractCheckValidator;
import org.eclipse.sphinx.emf.check.Check;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Package;
import org.eclipse.sphinx.examples.hummingbird20.common.Identifiable;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.ParameterValue;

public class Hummingbird20CheckValidator extends AbstractCheckValidator {

	private static final Pattern ILLEGAL_CHARACTERS_PATTERN = Pattern.compile(".*[ \\t\\.,;].*"); //$NON-NLS-1$

	@Check(constraint = "ApplicationNameNotValid", categories = { "Category1" })
	void checkApplicationName(Application application) {
		issue(application, Common20Package.Literals.IDENTIFIABLE__NAME);
	}

	@Check(constraint = "ComponentNameNotValid", categories = "Category2")
	void checkComponentName(Component component) {
		if (!isValidName(component)) {
			issue(component, Common20Package.Literals.IDENTIFIABLE__DESCRIPTION);
		}
	}

	@Check(constraint = "ParameterValuesNotValid", categories = { "Category1", "Category2" })
	void checkComponentValues(Component component) {
		EList<ParameterValue> parameterValues = component.getParameterValues();
		for (ParameterValue value : parameterValues) {
			try {
				Integer.parseInt(value.getValue());
			} catch (NumberFormatException ex) {
				issue(value, InstanceModel20Package.Literals.PARAMETER_VALUE__VALUE);
			}
		}
	}

	private boolean isValidName(Identifiable identifiable) {
		Assert.isNotNull(identifiable);
		return !ILLEGAL_CHARACTERS_PATTERN.matcher(identifiable.getName()).matches();
	}
}