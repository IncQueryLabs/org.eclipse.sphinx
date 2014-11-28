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

package org.eclipse.sphinx.examples.hummingbird20.check.withcatalog;

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

/**
 * An example of validator class making use of a check catalog. It contains a set of annotated methods with @Check(...).
 * The check catalog is used to externalize the error messages/severities/etc. When a catalog is used, @Check
 * annotations reference constraints by their Id in the catalog, so there is a logical mapping from methods to
 * constraints. Moreover it is possible to narrow down the scope of methods, i.e. their applicability, by specifying the
 * set of categories for which a constraint is applicable. For a constraint to be applicable within the scope of a
 * validator, the set of categories specified in its @Check annotation should be a subset of the set of categories
 * referenced by the constraint in the check catalog.
 * 
 * @see org.eclipse.sphinx.emf.check.AbstractCheckValidator
 */
public class Hummingbird20AnotherCheckValidator extends AbstractCheckValidator {

	private static final Pattern ILLEGAL_CHARACTERS_PATTERN = Pattern.compile(".*[ \\t\\.,;].*"); //$NON-NLS-1$

	@Check(constraint = "ApplicationNameNotValid", categories = { "Category1" })
	void checkApplicationName(Application application) {
		issue(application, Common20Package.Literals.IDENTIFIABLE__NAME, ": special caracters are not allowed"); //$NON-NLS-1$
	}

	@Check(constraint = "ComponentNameNotValid")
	void checkComponentName(Component component) {
		if (!isValidName(component)) {
			issue(component, Common20Package.Literals.IDENTIFIABLE__DESCRIPTION, component.getName());
		}
	}

	@Check(constraint = "ParameterValuesNotValid", categories = { "Category1" })
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