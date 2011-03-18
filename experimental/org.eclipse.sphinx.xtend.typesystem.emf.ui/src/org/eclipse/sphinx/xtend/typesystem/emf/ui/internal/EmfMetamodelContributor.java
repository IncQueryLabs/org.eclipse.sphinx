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
package org.eclipse.sphinx.xtend.typesystem.emf.ui.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.model.ModelDescriptorRegistry;
import org.eclipse.xtend.expression.TypeSystem;
import org.eclipse.xtend.shared.ui.MetamodelContributor;
import org.eclipse.xtend.typesystem.MetaModel;
import org.eclipse.xtend.typesystem.emf.EmfRegistryMetaModel;

public class EmfMetamodelContributor implements MetamodelContributor {

	public MetaModel[] getMetamodels(IJavaProject project, TypeSystem ctx) {
		final Collection<EPackage> ePackages = getAllEPackages(project.getProject());
		return new MetaModel[] { new EmfRegistryMetaModel() {
			@Override
			protected EPackage[] allPackages() {
				return ePackages.toArray(new EPackage[ePackages.size()]);
			}
		} };
	}

	private Collection<EPackage> getAllEPackages(IProject project) {
		Set<EPackage> allEPackages = new HashSet<EPackage>();
		for (IModelDescriptor modelDescriptor : ModelDescriptorRegistry.INSTANCE.getModels(project)) {
			allEPackages.addAll(modelDescriptor.getMetaModelDescriptor().getEPackages());
		}
		return Collections.unmodifiableSet(allEPackages);
	}
}
