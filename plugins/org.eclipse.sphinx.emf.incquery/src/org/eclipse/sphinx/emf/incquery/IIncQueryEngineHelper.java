/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [475954] Proxies with fragment-based proxy URIs may get resolved across model boundaries
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.incquery;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

public interface IIncQueryEngineHelper {

	ViatraQueryEngine getEngine(EObject contextObject) throws ViatraQueryException;

	ViatraQueryEngine getEngine(Resource contextResource) throws ViatraQueryException;

	ViatraQueryEngine getEngine(Resource resource, boolean strict) throws ViatraQueryException;

	ViatraQueryEngine getEngine(ResourceSet resourceSet) throws ViatraQueryException;
}
