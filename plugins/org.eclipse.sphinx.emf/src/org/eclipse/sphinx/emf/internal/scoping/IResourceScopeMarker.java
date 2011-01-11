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
package org.eclipse.sphinx.emf.internal.scoping;

import org.eclipse.sphinx.emf.Activator;

public interface IResourceScopeMarker {

	/**
	 * Resource Scope problem marker type.
	 */
	public static final String RESOURCE_SCOPING_PROBLEM = Activator.getPlugin().getSymbolicName() + ".resourcescopingproblemmarker"; //$NON-NLS-1$

}
