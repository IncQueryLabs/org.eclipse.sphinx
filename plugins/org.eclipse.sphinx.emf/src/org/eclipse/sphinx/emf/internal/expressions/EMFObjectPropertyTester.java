/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.internal.expressions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.sphinx.emf.edit.TransientItemProvider;
import org.eclipse.sphinx.emf.util.EObjectUtil;

/**
 * A property tester for various properties of EMF Objects.
 */
public class EMFObjectPropertyTester extends PropertyTester {

	/**
	 * A property testing that an object is an EObject, IWrapperItemProvider, or FeatureMap.Entry of the specified type
	 * or a subtype of the same.
	 */
	private static final String INSTANCE_OF = "instanceOf"; //$NON-NLS-1$

	/**
	 * A property testing that an object is an {@link EObject}, {@link IWrapperItemProvider}, or
	 * {@link FeatureMap.Entry}, and its qualified instance class name matches specified regular expression.
	 */
	private static final String CLASS_NAME_MATCHES = "classNameMatches"; //$NON-NLS-1$

	/**
	 * A property testing that an object is an {@link IWrapperItemProvider}, and its owner qualified instance class name
	 * matches specified regular expression.
	 */
	private static final String OWNER_CLASS_NAME_MATCHES = "ownerClassNameMatches"; //$NON-NLS-1$

	/**
	 * A property testing that an object is an {@link TransientItemProvider}, and its parent name matches specified
	 * regular expression.
	 */
	private static final String PARENT_CLASS_NAME_MATCHES = "parentClassNameMatches";//$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof TransientItemProvider && PARENT_CLASS_NAME_MATCHES.equals(property)) {
			TransientItemProvider provider = (TransientItemProvider) receiver;
			Notifier target = provider.getTarget();
			if (target != null) {
				return target.getClass().getName().matches(expectedValue.toString());
			}
			return false;
		}

		if (receiver instanceof IWrapperItemProvider && OWNER_CLASS_NAME_MATCHES.equals(property)) {
			IWrapperItemProvider provider = (IWrapperItemProvider) receiver;
			Object owner = provider.getOwner();
			if (owner != null) {
				return owner.getClass().getName().matches(expectedValue.toString());
			}
			return false;
		}

		// Unwrap wrapped model objects
		receiver = AdapterFactoryEditingDomain.unwrap(receiver);

		// instanceOf property
		if (receiver instanceof EObject && INSTANCE_OF.equals(property)) {
			EObject eObject = (EObject) receiver;
			return EObjectUtil.isAssignableFrom(eObject.eClass(), expectedValue.toString());
		}

		// classNameMatches property
		else if (receiver instanceof EObject && CLASS_NAME_MATCHES.equals(property)) {
			EObject eObject = (EObject) receiver;
			String instanceClassName = eObject.eClass().getInstanceClassName();
			if (instanceClassName != null) {
				return instanceClassName.matches(expectedValue.toString());
			}
		}

		return false;
	}
}
