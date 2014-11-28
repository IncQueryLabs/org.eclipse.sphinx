package org.eclipse.sphinx.emf.check.catalog;

import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EOperation.Internal.InvocationDelegate;
import org.eclipse.emf.ecore.EOperation.Internal.InvocationDelegate.Factory;

public class CategoryComparatorFactory implements Factory {

	public static final String URI = "org.eclipse.sphinx.emf.check.catalog.similarity"; //$NON-NLS-1$

	public static CategoryComparatorFactory INSTANCE = (CategoryComparatorFactory) Factory.Registry.INSTANCE
			.getFactory(URI);

	public CategoryComparatorFactory() {
	}

	@Override
	public InvocationDelegate createInvocationDelegate(EOperation operation) {
		return new CategoryComparatorDelegate(operation);
	}

}
