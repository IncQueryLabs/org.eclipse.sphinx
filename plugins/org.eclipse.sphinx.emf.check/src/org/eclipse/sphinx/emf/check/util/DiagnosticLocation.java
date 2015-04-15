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
package org.eclipse.sphinx.emf.check.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class DiagnosticLocation {

	private EObject object;

	private EStructuralFeature feature;

	private int index;

	public DiagnosticLocation(EObject object, EStructuralFeature feature, int index) {
		setObject(object);
		setFeature(feature);
		setIndex(index);
	}

	public EObject getObject() {
		return object;
	}

	public void setObject(EObject object) {
		this.object = object;
	}

	public EStructuralFeature getFeature() {
		return feature;
	}

	public void setFeature(EStructuralFeature feature) {
		this.feature = feature;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
