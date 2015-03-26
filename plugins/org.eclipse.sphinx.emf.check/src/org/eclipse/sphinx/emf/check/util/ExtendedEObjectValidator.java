package org.eclipse.sphinx.emf.check.util;

import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EObjectValidator;

public class ExtendedEObjectValidator extends EObjectValidator {

	@Override
	public boolean validate(int classifierID, Object object, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return classifierID != EcorePackage.EOBJECT || validate_EveryDefaultConstraint((EObject) object, diagnostics, context);
	}
}
