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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

public class ModelSplitDirective {

	private EObject eObject;
	private URI targetResourceURI;
	private boolean suppressAncestorsAttributes;

	public ModelSplitDirective(EObject eObject, URI targetURI) {
		this(eObject, targetURI, false);
	}

	public ModelSplitDirective(EObject eObject, URI targetResourceURI, boolean suppressAncestorsAttributes) {
		this.eObject = eObject;
		this.targetResourceURI = targetResourceURI;
		this.suppressAncestorsAttributes = suppressAncestorsAttributes;
	}

	public EObject getEObject() {
		return eObject;
	}

	public URI getTargetResourceURI() {
		return targetResourceURI;
	}

	public boolean isSuppressAncestorsAttributes() {
		return suppressAncestorsAttributes;
	}
}
