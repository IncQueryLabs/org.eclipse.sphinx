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

public class ModelSplitDirective {

	protected EObject eObject;
	protected URI targetResourceURI;
	protected boolean ignoreAncestorAttributes;
	protected List<EAttribute> unignorableAncestorAttributes = null;

	public ModelSplitDirective(EObject eObject, URI targetResourceURI) {
		this(eObject, targetResourceURI, false);
	}

	public ModelSplitDirective(EObject eObject, URI targetResourceURI, boolean ignoreAncestorAttributes) {
		this.eObject = eObject;
		this.targetResourceURI = targetResourceURI;
		this.ignoreAncestorAttributes = ignoreAncestorAttributes;
	}

	public EObject getEObject() {
		return eObject;
	}

	public URI getTargetResourceURI() {
		return targetResourceURI;
	}

	public boolean isIgnoreAncestorAttributes() {
		return ignoreAncestorAttributes;
	}

	public List<EAttribute> getUnIgnorableAncestorAttributes() {
		if (unignorableAncestorAttributes == null) {
			unignorableAncestorAttributes = new ArrayList<EAttribute>();
		}
		return unignorableAncestorAttributes;
	}

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
