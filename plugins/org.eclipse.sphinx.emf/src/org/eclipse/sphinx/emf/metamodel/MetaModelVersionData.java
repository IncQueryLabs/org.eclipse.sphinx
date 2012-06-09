/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 BMW Car IT, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 *     See4sys - Added support for EPackage URIs
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.metamodel;

import org.eclipse.core.runtime.Assert;

public class MetaModelVersionData {

	private String fNsPostfix;
	private String fEPackageNsURIPostfixPattern;
	private String fName;

	/**
	 * @deprecated See {@link MetaModelVersionData#getOrdinal()} for details.
	 */
	@Deprecated
	private int fOrdinal;

	public MetaModelVersionData(String nsPostfix, String ePackageNsURIPostfixPattern, String name) {
		Assert.isNotNull(name);

		fNsPostfix = nsPostfix;
		fEPackageNsURIPostfixPattern = ePackageNsURIPostfixPattern;
		fName = name;
	}

	/**
	 * @deprecated Use #MetaModelVersionData(String, String, String) instead. See
	 *             {@link MetaModelVersionData#getOrdinal()} for details.
	 */
	@Deprecated
	public MetaModelVersionData(String nsPostfix, String ePackageNsURIPostfixPattern, String name, int ordinal) {
		Assert.isNotNull(name);

		fNsPostfix = nsPostfix;
		fEPackageNsURIPostfixPattern = ePackageNsURIPostfixPattern;
		fName = name;
		fOrdinal = ordinal;
	}

	public String getNsPostfix() {
		return fNsPostfix;
	}

	/**
	 * @deprecated Instead of relying on this ordinal with unclear semantics meta models should provide a subclass which
	 *             defines its own version number semantics (see bug #363915 for details).
	 */
	@Deprecated
	public int getOrdinal() {
		return fOrdinal;
	}

	public String getEPackageNsURIPostfixPattern() {
		return fEPackageNsURIPostfixPattern;
	}

	public String getName() {
		return fName;
	}
}
