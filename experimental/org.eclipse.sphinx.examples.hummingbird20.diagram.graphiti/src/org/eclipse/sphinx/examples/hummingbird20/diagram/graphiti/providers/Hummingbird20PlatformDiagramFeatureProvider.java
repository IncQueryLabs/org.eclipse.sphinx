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

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.features.add.AddComponentTypeFeature;
import org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.features.create.CreateComponentTypeFeature;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;

/**
 * 
 */
public class Hummingbird20PlatformDiagramFeatureProvider extends DefaultFeatureProvider {

	public Hummingbird20PlatformDiagramFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// Is object for add request a ComponentType?
		if (context.getNewObject() instanceof ComponentType) {
			return new AddComponentTypeFeature(this);
		}
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] { new CreateComponentTypeFeature(this) };
	}
}
