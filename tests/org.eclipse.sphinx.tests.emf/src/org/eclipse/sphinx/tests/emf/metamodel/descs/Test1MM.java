/**
 * <copyright>
 *
 * Copyright (c) 2008-2013 BMW Car IT, itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BMW Car IT - Initial API and implementation
 *     itemis - [409367] Add a custom URI scheme to metamodel descriptor allowing mapping URI scheme to metamodel descriptor
 *
 * </copyright>
 */
package org.eclipse.sphinx.tests.emf.metamodel.descs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelVersionData;
import org.eclipse.sphinx.tests.emf.metamodel.mocks.MockEPkgRegistry;

@SuppressWarnings("nls")
public class Test1MM extends AbstractMetaModelDescriptor implements Comparable<Test1MM> {

	public static final String ID = "org.eclipse.sphinx.emf.workspace.internal.tests.test1mm";
	public static final String NS = "http://testA.sphinx.org";
	public static final String URI_SCHEME = "tr1"; //$NON-NLS-1$

	public static final MockEPkgRegistry MOCK_EPKG_REGISTRY = new MockEPkgRegistry();

	public static final Test1MM INSTANCE = new Test1MM();

	public Test1MM() {
		this(ID, null);
	}

	protected Test1MM(String identifier, MetaModelVersionData versionData) {
		super(identifier, NS, versionData);
		setEPackageRegistry(MOCK_EPKG_REGISTRY);
	}

	@Override
	public int compareTo(Test1MM otherMMDescriptor) {
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

	@Override
	public String getDefaultContentTypeId() {
		return "";
	}

	/*
	 * @see org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor# getCustomURIScheme()
	 */
	@Override
	public String getCustomURIScheme() {
		return URI_SCHEME;
	}
}
