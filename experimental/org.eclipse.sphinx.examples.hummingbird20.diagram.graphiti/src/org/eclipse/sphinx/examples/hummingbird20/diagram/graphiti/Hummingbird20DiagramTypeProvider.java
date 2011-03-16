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
package org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti;

import org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.features.Hummingbird20FeatureProvider;

/**
 * 
 */
public class Hummingbird20DiagramTypeProvider extends AbstractDiagramTypeProvider {

	public Hummingbird20DiagramTypeProvider() {
		setFeatureProvider(new Hummingbird20FeatureProvider(this));
	}
}
