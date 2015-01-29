/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird10.incquery.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.sphinx.examples.hummingbird10.Hummingbird10Package;

public class Hummingbird10IncQueryHelper {

	public static final String SEGMENT_PREFIX = "@"; //$NON-NLS-1$
	public static final String DOT = "."; //$NON-NLS-1$

	private Map<String, EClass> featureNameToEClassMap;

	public EClass getEClass(URI uri) {
		String fragment = uri.fragment();
		int segPrefixIndex = fragment.lastIndexOf(SEGMENT_PREFIX);
		int dotIndex = fragment.lastIndexOf(DOT);
		String featureName = fragment.substring(segPrefixIndex + 1, dotIndex);
		return getEClass(featureName);
	}

	private EClass getEClass(String featureName) {
		if (featureNameToEClassMap == null) {
			featureNameToEClassMap = new HashMap<String, EClass>();
			featureNameToEClassMap.put(Hummingbird10Package.eINSTANCE.getApplication_Interfaces().getName(),
					Hummingbird10Package.eINSTANCE.getInterface());
			featureNameToEClassMap.put(Hummingbird10Package.eINSTANCE.getApplication_Components().getName(),
					Hummingbird10Package.eINSTANCE.getComponent());
			featureNameToEClassMap.put(Hummingbird10Package.eINSTANCE.getComponent_OutgoingConnections().getName(),
					Hummingbird10Package.eINSTANCE.getConnection());
		}
		return featureNameToEClassMap.get(featureName);
	}
}
