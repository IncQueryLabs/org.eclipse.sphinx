package org.eclipse.sphinx.emf.check.catalog;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EOperation.Internal.InvocationDelegate;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicInvocationDelegate;
import org.eclipse.sphinx.emf.check.catalog.checkcatalog.Category;

public class CategoryComparatorDelegate extends BasicInvocationDelegate
		implements InvocationDelegate {

	public CategoryComparatorDelegate(EOperation operation) {
		super(operation);
	}

	/**
	 * The equality between categories is based on the following assumption: Two
	 * categories are equals iff they have equal ids
	 */
	@Override
	public Object dynamicInvoke(InternalEObject target, EList<?> arguments)
			throws InvocationTargetException {
		if (target instanceof Category) {
			Category lhs = (Category) target;
			Category rhs = (Category) arguments.get(0);
			String lhs_id = lhs.getId();
			String rhs_id = rhs.getId();
			return lhs_id.equals(rhs_id);
		}
		return super.dynamicInvoke(target, arguments);
	}
}
