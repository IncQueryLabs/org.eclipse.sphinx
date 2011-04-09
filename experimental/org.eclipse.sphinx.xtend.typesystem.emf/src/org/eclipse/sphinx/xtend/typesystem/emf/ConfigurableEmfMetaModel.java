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

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.xtend.typesystem.emf.EmfRegistryMetaModel;

/**
 * An EMF MetaModel that is based on the {@link EPackage}s of a configurable collection of {@link IMetaModelDescriptor
 * metamodel descriptor}s.
 */
public class ConfigurableEmfMetaModel extends EmfRegistryMetaModel {

	private Set<IMetaModelDescriptor> metaModelDescriptors = new HashSet<IMetaModelDescriptor>();

	private Collection<EPackage> allEPackages = null;

	public void addMetaModelDescriptor(IMetaModelDescriptor mmDescriptor) {
		Assert.isNotNull(mmDescriptor);
		metaModelDescriptors.add(mmDescriptor);
	}

	@Override
	protected EPackage[] allPackages() {
		if (allEPackages == null) {
			allEPackages = doAllPackages();
		}
		return allEPackages.toArray(new EPackage[allEPackages.size()]);
	}

	protected Collection<EPackage> doAllPackages() {
		Collection<EPackage> allEPackages = new HashSet<EPackage>();
		for (IMetaModelDescriptor mmDescriptor : metaModelDescriptors) {
			allEPackages.addAll(mmDescriptor.getEPackages());
		}
		return allEPackages;
	}
}
