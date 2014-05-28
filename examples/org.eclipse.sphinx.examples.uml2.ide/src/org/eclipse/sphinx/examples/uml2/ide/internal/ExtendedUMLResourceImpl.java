/**
 * <copyright>
 *
 * Copyright (c) 2008-2013 itemis and others.
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
package org.eclipse.sphinx.examples.uml2.ide.internal;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.uml2.uml.internal.resource.UMLResourceImpl;

@SuppressWarnings("restriction")
public class ExtendedUMLResourceImpl extends UMLResourceImpl {

	public ExtendedUMLResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	protected XMLLoad createXMLLoad() {
		return new ExtendedUMLLoadImpl(createXMLHelper());
	}
}
