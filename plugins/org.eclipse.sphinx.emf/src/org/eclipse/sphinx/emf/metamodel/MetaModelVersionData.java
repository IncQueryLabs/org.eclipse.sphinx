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
	private int fOrdinal;

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

	public int getOrdinal() {
		return fOrdinal;
	}

	public String getName() {
		return fName;
	}

	public String getEPackageNsURIPostfixPattern() {
		return fEPackageNsURIPostfixPattern;
	}
}
