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

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.sphinx.emf.resource.ExtendedResourceSetImpl;
import org.eclipse.uml2.uml.internal.resource.UMLHandler;

@SuppressWarnings("restriction")
public class ExtendedUMLHandler extends UMLHandler {

	public ExtendedUMLHandler(XMLResource xmiResource, XMLHelper helper, Map<?, ?> options) {
		super(xmiResource, helper, options);
	}

	/*
	 * Overridden to enrich proxy URIs being created with context information required to honor their {@link
	 * IResourceScope resource scope}s when they are being resolved and to support the resolution of proxified
	 * references between objects from different metamodels.
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLHandler#handleProxy(org.eclipse.emf.ecore.InternalEObject,
	 * java.lang.String)
	 */
	@Override
	protected void handleProxy(InternalEObject proxy, String uriLiteral) {
		super.handleProxy(proxy, uriLiteral);

		// Augment the proxy's URI to a context-aware proxy URI
		if (resourceSet instanceof ExtendedResourceSetImpl) {
			ExtendedResourceSetImpl extendedResourceSet = (ExtendedResourceSetImpl) resourceSet;

			URI contextURI = null;
			IModelDescriptor modelDescriptor = ModelDescriptorRegistry.INSTANCE.getModel(xmlResource);
			if (modelDescriptor != null) {
				contextURI = URI.createPlatformResourceURI(modelDescriptor.getRoot().toString(), true);
			}

			extendedResourceSet.augmentToContextAwareURI(proxy, contextURI);
		}
	}
}
