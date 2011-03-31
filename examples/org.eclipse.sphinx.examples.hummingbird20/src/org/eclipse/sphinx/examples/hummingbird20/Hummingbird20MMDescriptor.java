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
package org.eclipse.sphinx.examples.hummingbird20;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelVersionData;
import org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor;

/**
 * Implementation of {@linkplain IMetaModelDescriptor} for the Hummingbird 2.0 meta-model.
 */
public class Hummingbird20MMDescriptor extends HummingbirdMMDescriptor {

	/**
	 * The id of the content type for Hummingbird 2.0 XMI files.
	 */
	public static final String XMI_CONTENT_TYPE_ID = "org.eclipse.sphinx.examples.hummingbird20.hummingbird20XMIFile"; //$NON-NLS-1$

	private static final String ID = "org.eclipse.sphinx.examples.hummingbird20"; //$NON-NLS-1$
	private static final String NS_POSTFIX = "2.0.1"; //$NON-NLS-1$
	private static final String EPKG_NS_URI_POSTFIX_PATTERN = "2\\.0\\.1(/\\w+)*"; //$NON-NLS-1$
	private static final String NAME = "Hummingbird 2.0"; //$NON-NLS-1$
	private static final int ORDINAL = 2;

	/**
	 * Singleton instance.
	 */
	public static final Hummingbird20MMDescriptor INSTANCE = new Hummingbird20MMDescriptor();

	/**
	 * Private default constructor for singleton pattern.
	 */
	private Hummingbird20MMDescriptor() {
		super(ID, new MetaModelVersionData(NS_POSTFIX, EPKG_NS_URI_POSTFIX_PATTERN, NAME, ORDINAL));
	}

	/*
	 * @see org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor#getDefaultContentTypeId()
	 */
	@Override
	public String getDefaultContentTypeId() {
		return XMI_CONTENT_TYPE_ID;
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor#getCompatibleResourceVersionDescriptors()
	 */
	@Override
	public Collection<IMetaModelDescriptor> getCompatibleResourceVersionDescriptors() {
		Set<IMetaModelDescriptor> descriptors = new HashSet<IMetaModelDescriptor>();
		descriptors.add(Hummingbird20MMCompatibility.HUMMINGBIRD_2_0_0_RESOURCE_DESCRIPTOR);
		return Collections.unmodifiableSet(descriptors);
	}
}
