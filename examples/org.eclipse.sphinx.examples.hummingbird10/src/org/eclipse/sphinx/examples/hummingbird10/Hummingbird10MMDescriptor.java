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
package org.eclipse.sphinx.examples.hummingbird10;

import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelVersionData;
import org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor;

/**
 * Implementation of {@linkplain IMetaModelDescriptor} for the Hummingbird 1.0 meta-model.
 */
public class Hummingbird10MMDescriptor extends HummingbirdMMDescriptor {

	private static final String ID = "org.eclipse.sphinx.examples.hummingbird10"; //$NON-NLS-1$
	private static final String NS_POSTFIX = "1.0.0"; //$NON-NLS-1$
	private static final String EPKG_NS_URI_POSTFIX_PATTERN = "1\\.0\\.0(/\\w+)*"; //$NON-NLS-1$
	private static final String NAME = "Hummingbird 1.0"; //$NON-NLS-1$
	private static final int ORDINAL = 1;

	/**
	 * Singleton instance.
	 */
	public static final Hummingbird10MMDescriptor INSTANCE = new Hummingbird10MMDescriptor();

	/**
	 * Default constructor.
	 */
	public Hummingbird10MMDescriptor() {
		super(ID, new MetaModelVersionData(NS_POSTFIX, EPKG_NS_URI_POSTFIX_PATTERN, NAME, ORDINAL));
	}

	/*
	 * @see org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor#getDefaultContentTypeId()
	 */
	@Override
	public String getDefaultContentTypeId() {
		return getRootEPackageContentTypeId();
	}
}
