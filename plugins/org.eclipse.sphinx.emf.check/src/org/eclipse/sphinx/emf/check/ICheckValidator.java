package org.eclipse.sphinx.emf.check;

import java.util.Set;

import org.eclipse.emf.ecore.EValidator;

public interface ICheckValidator extends EValidator {

	void setFilter(Set<String> validationSets);

	Set<String> getFilter();

	CheckModelHelper getCheckModelHelper();

}