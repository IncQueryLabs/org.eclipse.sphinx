/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.xtendxpand.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.mwe.core.resources.ResourceLoader;
import org.eclipse.internal.xtend.type.baseimpl.TypesComparator;
import org.eclipse.sphinx.emf.mwe.IXtendXpandConstants;
import org.eclipse.sphinx.emf.mwe.resources.BasicWorkspaceResourceLoader;
import org.eclipse.xtend.typesystem.Callable;
import org.eclipse.xtend.typesystem.Feature;
import org.eclipse.xtend.typesystem.ParameterizedCallable;
import org.eclipse.xtend.typesystem.Type;

public final class XtendXpandUtil {

	// Prevent from instantiation
	private XtendXpandUtil() {
	}

	public static final String XTEND_XPAND_NATURE_ID = "org.eclipse.xtend.shared.ui.xtendXPandNature"; //$NON-NLS-1$

	public static String getQualifiedName(IFile underlyingFile, String featureName) {
		Assert.isNotNull(underlyingFile);

		if (underlyingFile.exists()) {
			StringBuilder path = new StringBuilder();
			IPath templateNamespace = underlyingFile.getProjectRelativePath().removeFileExtension();
			for (Iterator<String> iter = Arrays.asList(templateNamespace.segments()).iterator(); iter.hasNext();) {
				String segment = iter.next();
				path.append(segment);
				if (iter.hasNext()) {
					path.append(IXtendXpandConstants.NS_DELIMITER);
				}
			}
			if (featureName != null && featureName.length() > 0) {
				path.append(IXtendXpandConstants.NS_DELIMITER);
				path.append(featureName);
			}
			return path.toString();
		}
		return null;
	}

	public static IFile getUnderlyingFile(String qualifiedName) {
		return getUnderlyingFile(qualifiedName, new BasicWorkspaceResourceLoader());
	}

	public static IFile getUnderlyingFile(String qualifiedName, ResourceLoader resourceLoader) {
		Assert.isNotNull(resourceLoader);

		URL resourceURL = resourceLoader.getResource(qualifiedName);
		if (resourceURL != null) {
			try {
				Path location = new Path(resourceURL.toURI().getPath());
				return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(location);
			} catch (URISyntaxException ex) {
				// Ignore exception
			}
		}
		return null;
	}

	public static List<Callable> getApplicableFeatures(final List<? extends Callable> features, Class<?> featureType, String featureName,
			List<? extends Type> paramTypes) {
		final List<Callable> applicableFeatures = new ArrayList<Callable>();
		Comparator<List<? extends Type>> typesComparator = new TypesComparator();
		for (Callable feature : features) {
			if (featureType.isInstance(feature) && (featureName == null || feature.getName().equals(featureName))) {
				final List<? extends Type> featureParamTypes = getParamTypes(feature);
				if (featureParamTypes.size() == paramTypes.size() && typesComparator.compare(featureParamTypes, paramTypes) >= 0) {
					applicableFeatures.add(feature);
				}
			}
		}
		return applicableFeatures;
	}

	private static List<? extends Type> getParamTypes(Callable feature) {
		final List<Type> result = new ArrayList<Type>();
		if (feature instanceof Feature) {
			result.add(((Feature) feature).getOwner());
		}
		if (feature instanceof ParameterizedCallable) {
			if (((ParameterizedCallable) feature).getParameterTypes() != null) {
				result.addAll(((ParameterizedCallable) feature).getParameterTypes());
			}
		}
		return result;
	}
}
