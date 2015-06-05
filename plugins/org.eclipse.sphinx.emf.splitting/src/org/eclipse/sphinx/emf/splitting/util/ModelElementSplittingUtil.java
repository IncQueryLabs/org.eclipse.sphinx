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
package org.eclipse.sphinx.emf.splitting.util;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

public final class ModelElementSplittingUtil {

	private ModelElementSplittingUtil() {
	}

	@SuppressWarnings("unchecked")
	public static void setPropertyValue(EObject eObject, EStructuralFeature feature, Object value) {
		if (eObject != null && feature != null) {
			if (feature.isMany()) {
				((List<Object>) eObject.eGet(feature)).add(value);
			} else {
				eObject.eSet(feature, value);
			}
		}
	}

	public static <T extends EObject> T copy(T eObject, boolean copyContainment) {
		ModelElementSplittingUtil.Copier copier = new ModelElementSplittingUtil.Copier();
		EObject result = copier.copy(eObject, copyContainment);
		copier.copyReferences();

		@SuppressWarnings("unchecked")
		T t = (T) result;
		return t;
	}

	public static class Copier extends EcoreUtil.Copier {

		private static final long serialVersionUID = 1L;

		public Copier() {
			super();
		}

		public Copier(boolean resolveProxies) {
			super(resolveProxies);
		}

		public Copier(boolean resolveProxies, boolean useOriginalReferences) {
			super(resolveProxies, useOriginalReferences);
		}

		public EObject copy(EObject eObject, boolean copyContainment) {
			if (eObject == null) {
				return null;
			}

			if (copyContainment) {
				return copy(eObject);
			}

			EObject copyEObject = createCopy(eObject);
			if (copyEObject != null) {
				put(eObject, copyEObject);
				EClass eClass = eObject.eClass();
				for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i) {
					EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(i);
					if (eStructuralFeature.isChangeable() && !eStructuralFeature.isDerived()) {
						if (eStructuralFeature instanceof EAttribute) {
							copyAttribute((EAttribute) eStructuralFeature, eObject, copyEObject);
						}
					}
				}

				copyProxyURI(eObject, copyEObject);
			}
			return copyEObject;
		}
	}
}
