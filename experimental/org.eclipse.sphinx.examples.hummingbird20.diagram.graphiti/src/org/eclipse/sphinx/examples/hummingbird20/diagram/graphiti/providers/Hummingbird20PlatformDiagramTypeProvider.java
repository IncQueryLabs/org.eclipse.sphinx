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
package org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.providers;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;

/**
 * 
 */
public class Hummingbird20PlatformDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public static String DIAGRAM_TYPE_ID = "org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.diagramTypes.platform"; //$NON-NLS-1$
	public static String DIAGRAM_TYPE = "Hummingbird20PlatformDiagramType"; //$NON-NLS-1$
	public static String DIAGRAM_TYPE_NAME = "Hummingbird 2.0 Platform Diagram Type"; //$NON-NLS-1$

	public Hummingbird20PlatformDiagramTypeProvider() {
		setFeatureProvider(new Hummingbird20PlatformDiagramFeatureProvider(this));
	}
}
