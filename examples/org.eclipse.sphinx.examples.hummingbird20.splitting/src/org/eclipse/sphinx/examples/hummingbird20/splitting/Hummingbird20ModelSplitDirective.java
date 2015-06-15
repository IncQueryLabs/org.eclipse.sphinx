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
package org.eclipse.sphinx.examples.hummingbird20.splitting;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.splitting.BasicModelSplitDirective;

public class Hummingbird20ModelSplitDirective extends BasicModelSplitDirective {

	public Hummingbird20ModelSplitDirective(EObject eObject, URI targetResourceURI) {
		super(eObject, targetResourceURI);
	}

	public Hummingbird20ModelSplitDirective(EObject eObject, URI targetResourceURI, boolean ignoreAncestorAttributes) {
		super(eObject, targetResourceURI, ignoreAncestorAttributes);

		// Always replicate "name" attributes of ancestors of split model objects, i.e., even when all other
		// ancestor attributes are requested to be ignored as per ignoreAncestorAttributes
		// getMandatoryAncestorAttributes().add(Common20Package.eINSTANCE.getIdentifiable_Name());
	}
}
