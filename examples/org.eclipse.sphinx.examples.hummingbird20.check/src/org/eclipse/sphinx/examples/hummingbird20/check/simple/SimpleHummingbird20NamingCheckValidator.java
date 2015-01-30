package org.eclipse.sphinx.examples.hummingbird20.check.simple;

import org.eclipse.sphinx.emf.check.AbstractCheckValidator;
import org.eclipse.sphinx.emf.check.Check;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Package;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;

/**
 * An example of check validator which does <em>not</em> make use of a catalog. Severities, error messages, and
 * additional information to be displayed in the problems view are specified inside the validator using explicit error,
 * warning, and info methods.
 *
 * @see org.eclipse.sphinx.emf.check.AbstractCheckValidator
 */
// TODO Add message arguments parameter to error()/warning()/info() methods, consider to remove issueData
// parameter
public class SimpleHummingbird20NamingCheckValidator extends AbstractCheckValidator {

	private final static String INVALID_NAME_PREFIX = "_"; //$NON-NLS-1$

	@Check
	void checkApplicationName(Application application) {
		String name = application.getName();
		if (name != null && name.startsWith(INVALID_NAME_PREFIX)) {
			//error("The application name has an invalid prefix", application, Common20Package.Literals.IDENTIFIABLE__NAME); //$NON-NLS-1$
			warning("The application name has an invalid prefix", application, Common20Package.Literals.IDENTIFIABLE__NAME); //$NON-NLS-1$
			//info("The application name has an invalid prefix", application, Common20Package.Literals.IDENTIFIABLE__NAME); //$NON-NLS-1$
		}
	}
}
