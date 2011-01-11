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
package org.eclipse.sphinx.emf.internal.metamodel;

import org.eclipse.core.resources.IFile;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;

public final class InternalMetaModelDescriptorRegistry {

	/**
	 * The singleton instance of this registry.
	 */
	public static final InternalMetaModelDescriptorRegistry INSTANCE = new InternalMetaModelDescriptorRegistry();

	/**
	 * Private constructor for the singleton pattern.
	 */
	private InternalMetaModelDescriptorRegistry() {
	}

	public void addCachedDescriptor(IFile file, IMetaModelDescriptor mmDescriptor) {
		IFileMetaModelDescriptorCache cache = (IFileMetaModelDescriptorCache) MetaModelDescriptorRegistry.INSTANCE
				.getAdapter(IFileMetaModelDescriptorCache.class);
		cache.addDescriptor(file, mmDescriptor);
	}

	public void moveCachedDescriptor(IFile oldFile, IFile newFile) {
		IFileMetaModelDescriptorCache cache = (IFileMetaModelDescriptorCache) MetaModelDescriptorRegistry.INSTANCE
				.getAdapter(IFileMetaModelDescriptorCache.class);
		cache.moveDescriptor(oldFile, newFile);
	}

	public void removeCachedDescriptor(IFile file) {
		IFileMetaModelDescriptorCache cache = (IFileMetaModelDescriptorCache) MetaModelDescriptorRegistry.INSTANCE
				.getAdapter(IFileMetaModelDescriptorCache.class);
		cache.removeDescriptor(file);
	}

	public void clearCachedOldDescriptors() {
		IFileMetaModelDescriptorCache cache = (IFileMetaModelDescriptorCache) MetaModelDescriptorRegistry.INSTANCE
				.getAdapter(IFileMetaModelDescriptorCache.class);
		cache.clearOldDescriptors();
	}
}
