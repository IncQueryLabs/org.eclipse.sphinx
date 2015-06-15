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
package org.eclipse.sphinx.emf.splitting;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

public class BasicModelSplitDirective implements IModelSplitDirective {

	protected EObject eObject;
	protected URI targetResourceURI;
	protected boolean ignoreAncestorAttributes;
	protected List<EAttribute> mandatoryAncestorAttributes = null;

	public BasicModelSplitDirective(EObject eObject, URI targetResourceURI) {
		this(eObject, targetResourceURI, false);
	}

	public BasicModelSplitDirective(EObject eObject, URI targetResourceURI, boolean ignoreAncestorAttributes) {
		this.eObject = eObject;
		this.targetResourceURI = targetResourceURI;
		this.ignoreAncestorAttributes = ignoreAncestorAttributes;
	}

	/*
	 * @see org.eclipse.sphinx.emf.splitting.IModelSplitDirective#getEObject()
	 */
	@Override
	public EObject getEObject() {
		return eObject;
	}

	/*
	 * @see org.eclipse.sphinx.emf.splitting.IModelSplitDirective#getTargetResourceURI()
	 */
	@Override
	public URI getTargetResourceURI() {
		return targetResourceURI;
	}

	/*
	 * @see org.eclipse.sphinx.emf.splitting.IModelSplitDirective#isIgnoreAncestorAttributes()
	 */
	@Override
	public boolean isIgnoreAncestorAttributes() {
		return ignoreAncestorAttributes;
	}

	/*
	 * @see org.eclipse.sphinx.emf.splitting.IModelSplitDirective#getUnignorableAncestorAttributes()
	 */
	@Override
	public List<EAttribute> getMandatoryAncestorAttributes() {
		if (mandatoryAncestorAttributes == null) {
			mandatoryAncestorAttributes = new ArrayList<EAttribute>();
		}
		return mandatoryAncestorAttributes;
	}

	/*
	 * @see org.eclipse.sphinx.emf.splitting.IModelSplitDirective#isValid()
	 */
	@Override
	public boolean isValid() {
		if (eObject == null) {
			return false;
		}
		if (targetResourceURI == null) {
			return false;
		}
		// EObject to be split already in specified target resource?
		if (eObject.eResource() != null && eObject.eResource().getURI() == targetResourceURI) {
			return false;
		}
		return true;
	}
}
