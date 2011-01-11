/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.examples.library.ide.metamodel;

import org.eclipse.emf.examples.extlibrary.EXTLibraryPackage;
import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;

/**
 * EXTLibrary metamodel descriptor.
 */
public class EXTLibraryMMDescriptor extends AbstractMetaModelDescriptor {

	/**
	 * Singleton instance.
	 */
	public static final EXTLibraryMMDescriptor INSTANCE = new EXTLibraryMMDescriptor();

	/**
	 * Default constructor.
	 */
	public EXTLibraryMMDescriptor() {
		super("org.eclipse.sphinx.examples.extlibrary", EXTLibraryPackage.eNS_URI); //$NON-NLS-1$
	}

	/**
	 * Overridden to provide fix wrong {@link EXTLibraryPackage#eCONTENT_TYPE} value. Should be
	 * "org.eclipse.emf.examples.library.extendedLibrary" but actually is "extendedLibrary".
	 */
	@Override
	protected String getRootEPackageContentTypeId() {
		return "org.eclipse.emf.examples.library.extendedLibrary"; //$NON-NLS-1$
	}
}
