package org.eclipse.sphinx.examples.hummingbird20.check.basic;

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
public class Hummingbird20BasicCheckValidator extends AbstractCheckValidator {

	@Check
	void checkApplicationName(Application application) {
		error("Application name is not valid", application, Common20Package.Literals.IDENTIFIABLE__NAME); //$NON-NLS-1$
		//info("Application name is not valid", application, Common20Package.Literals.IDENTIFIABLE__NAME); //$NON-NLS-1$
		//warning("Application name is not valid", application, Common20Package.Literals.IDENTIFIABLE__NAME); //$NON-NLS-1$
	}
}
