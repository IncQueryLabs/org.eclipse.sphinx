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
package org.eclipse.sphinx.xtend;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;

public class XtendEvaluationRequest {

	/**
	 * The Xtend extension name.
	 */
	private String extensionName;

	/**
	 * The parameters to be use.
	 */
	private List<Object> parameterList;

	/**
	 * The target model object for model transformation.
	 */
	private Object targetObject;

	/**
	 * Constructs an Xtend model transformation request.
	 * 
	 * @param extensionName
	 *            the Xtend extension name to be use.
	 * @param targetObject
	 *            the target model object.
	 */
	public XtendEvaluationRequest(String extensionName, Object targetObject) {
		this(extensionName, targetObject, Collections.emptyList());
	}

	/**
	 * Constructs an Xtend model transformation request.
	 * 
	 * @param extensionName
	 *            the Xtend extension name to be use.
	 * @param targetObject
	 *            the target model object.
	 * @param parameterList
	 *            a list of parameters to be used in model transformation.
	 */
	public XtendEvaluationRequest(String extensionName, Object targetObject, Object... parameters) {
		Assert.isNotNull(extensionName);
		Assert.isNotNull(targetObject);

		this.extensionName = extensionName;
		this.targetObject = targetObject;
		parameterList = parameters != null ? Arrays.asList(parameters) : Collections.emptyList();
	}

	/**
	 * Gets the extension name used in model transformation.
	 */
	public String getExtensionName() {
		return extensionName;
	}

	/**
	 * Gets the target model object.
	 */
	public Object getTargetObject() {
		return targetObject;
	}

	/**
	 * Gets the list of parameters used in model transformation.
	 */
	public List<Object> getParameterList() {
		return parameterList;
	}
}
