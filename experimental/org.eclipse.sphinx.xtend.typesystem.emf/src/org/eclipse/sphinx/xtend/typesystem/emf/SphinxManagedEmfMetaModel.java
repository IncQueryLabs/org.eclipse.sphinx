/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.xtend.typesystem.emf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.xtend.typesystem.emf.EmfRegistryMetaModel;

public class SphinxManagedEmfMetaModel extends EmfRegistryMetaModel {

	protected IProject contextProject = null;
	protected IModelDescriptor contextModel = null;

	public SphinxManagedEmfMetaModel(IProject contextProject) {
		this.contextProject = contextProject;
	}

	public SphinxManagedEmfMetaModel(IModelDescriptor contextModel) {
		this.contextModel = contextModel;
	}

	protected Collection<IModelDescriptor> getModelsInScope() {
		Set<IModelDescriptor> modelsInScope = new HashSet<IModelDescriptor>();
		if (contextModel != null) {
			modelsInScope.add(contextModel);
		} else if (contextProject != null) {
			modelsInScope.addAll(ModelDescriptorRegistry.INSTANCE.getModels(contextProject));
		}
		return modelsInScope;
	}

	@Override
	protected EPackage[] allPackages() {
		Set<EPackage> allEPackages = new HashSet<EPackage>();
		for (IModelDescriptor modelDescriptor : getModelsInScope()) {
			allEPackages.addAll(modelDescriptor.getMetaModelDescriptor().getEPackages());
		}
		return allEPackages.toArray(new EPackage[allEPackages.size()]);
	}
}
