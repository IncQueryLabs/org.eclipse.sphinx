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

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelVersionData;
import org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor;

/**
 * Provides {@link IMetaModelDescriptor descriptor}s of Hummingbird {@link Resource resource} versions which are
 * compatible with the {@link Hummingbird20MMDescriptor} metamodel implementation.
 */
public final class Hummingbird20MMCompatibility {

	/**
	 * Private default constructor.
	 */
	private Hummingbird20MMCompatibility() {
	}

	/**
	 * {@link IMetaModelDescriptor Descriptor} of Hummingbird 2.0.0 {@link Resource resource}s.
	 */
	public static final HummingbirdMMDescriptor HUMMINGBIRD_2_0_0_RESOURCE_DESCRIPTOR = new Hummingbird200MMDescriptor();

	private static final class Hummingbird200MMDescriptor extends HummingbirdMMDescriptor {

		private static final String ID = "org.eclipse.sphinx.examples.hummingbird200"; //$NON-NLS-1$
		private static final String NS_POSTFIX = "2.0.0"; //$NON-NLS-1$
		private static final String EPKG_NS_URI_POSTFIX_PATTERN = "2\\.0\\.0(/\\w+)*"; //$NON-NLS-1$
		private static final String NAME = "Hummingbird 2.0.0"; //$NON-NLS-1$
		private static final int ORDINAL = 200;

		/*
		 * Private default constructor.
		 */
		private Hummingbird200MMDescriptor() {
			super(ID, new MetaModelVersionData(NS_POSTFIX, EPKG_NS_URI_POSTFIX_PATTERN, NAME, ORDINAL));
		}

		/*
		 * @see org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor#getDefaultContentTypeId()
		 */
		@Override
		public String getDefaultContentTypeId() {
			return Hummingbird20MMDescriptor.XMI_CONTENT_TYPE_ID;
		}
	}
}
