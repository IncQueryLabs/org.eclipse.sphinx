/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.emf.check.registry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.common.util.URI;

public class CheckValidatorDescriptor {

	private Pattern uriPattern = Pattern.compile("platform:/plugin/.+"); //$NON-NLS-1$

	private static final String validator_class = "class"; //$NON-NLS-1$
	private static final String validator_model = "catalog"; //$NON-NLS-1$
	private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

	private String checkCatalogPath = null;
	private String checkValidatorClassName = null;
	private String contributorPluginId = null;

	public CheckValidatorDescriptor(IConfigurationElement iConfigElement) {
		String modelPath = iConfigElement.getAttribute(validator_model);
		String clazz = iConfigElement.getAttribute(validator_class);
		Assert.isNotNull(clazz);
		String contributorPluginId = iConfigElement.getContributor().getName();
		setCatalogPath(modelPath);
		setValidatorClassName(clazz);
		setContributorPluginId(contributorPluginId);
	}

	public String getContributorPluginId() {
		return contributorPluginId;
	}

	public void setContributorPluginId(String contributorPluginId) {
		this.contributorPluginId = contributorPluginId;
	}

	public String getCatalogPath() {
		return checkCatalogPath;
	}

	public void setCatalogPath(String checkModelPath) {
		checkCatalogPath = checkModelPath;
	}

	public String getValidatorClassName() {
		return checkValidatorClassName;
	}

	public void setValidatorClassName(String validatorClassName) {
		this.checkValidatorClassName = validatorClassName;
	}

	public URI getCheckCatalogURI() {
		if (checkCatalogPath != null) {
			Matcher matcher = uriPattern.matcher(checkCatalogPath);
			if (matcher.matches()) {
				return URI.createURI(checkCatalogPath);
			}
			String stringURI = getContributorPluginId() + PATH_SEPARATOR + checkCatalogPath;
			return URI.createPlatformPluginURI(stringURI, false);
		}
		return null;
	}
}
