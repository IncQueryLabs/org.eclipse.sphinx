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
package org.eclipse.sphinx.examples.hummingbird.ide.metamodel;

import org.eclipse.core.runtime.Assert;
import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelVersionData;

/**
 * Version-independent descriptor for all Hummingbird metamodels and base type of all version-specific Hummingbird
 * metamodel descriptors.
 */
public class HummingbirdMMDescriptor extends AbstractMetaModelDescriptor implements Comparable<HummingbirdMMDescriptor> {

	/**
	 * The base namespace of all Hummingbird metamodel versions.
	 */
	public static final String BASE_NAMESPACE = "http://www.eclipse.org/sphinx/examples/hummingbird"; //$NON-NLS-1$

	/**
	 * The id of the base content type for all version-specific Hummingbird XMI file content types.
	 */
	public static final String XMI_BASE_CONTENT_TYPE_ID = "org.eclipse.sphinx.examples.hummingbird.ide.hummingbirdXMIFile"; //$NON-NLS-1$

	/**
	 * Singleton instance.
	 */
	public static final HummingbirdMMDescriptor INSTANCE = new HummingbirdMMDescriptor();

	private static final String ID = "org.eclipse.sphinx.examples.hummingbird"; //$NON-NLS-1$

	/**
	 * Private default constructor for singleton pattern.
	 */
	private HummingbirdMMDescriptor() {
		this(ID, null);
	}

	protected HummingbirdMMDescriptor(String identifier, MetaModelVersionData versionData) {
		super(identifier, BASE_NAMESPACE, versionData);
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor#getDefaultContentTypeId()
	 */
	@Override
	public String getDefaultContentTypeId() {
		return XMI_BASE_CONTENT_TYPE_ID;
	}

	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(HummingbirdMMDescriptor otherMMDescriptor) {
		Assert.isNotNull(otherMMDescriptor);
		int result = 0;
		int revision = parseInt(getNsPostfix());
		int otherRevision = parseInt(otherMMDescriptor.getNsPostfix());
		result = revision < otherRevision ? -1 : revision > otherRevision ? +1 : 0;
		return result;
	}

	protected int parseInt(String nsPostfix) {
		Assert.isNotNull(nsPostfix);
		int result = 0;
		String[] split = nsPostfix.split("\\."); //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer();
		for (String element : split) {
			buffer.append(element);
		}
		try {
			result = Integer.parseInt(buffer.toString());
		} catch (NumberFormatException nfe) {
			// Fail silent
		}
		return result;
	}
}
