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
package org.eclipse.sphinx.xpand;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;

public class XpandEvaluationRequest {

	/**
	 * The entry statement for Xpand i.e., the the definition name.
	 */
	private String definitionName;

	/**
	 * The target object on with the code generation is applied.
	 */
	private Object targetObject;

	/**
	 * A list of objects corresponding to the parameters to be use at code generation time.
	 */
	private List<Object> parameterList;

	/**
	 * Constructs an Xpand code generation evaluation request.
	 * 
	 * @param definitionName
	 *            the Xpand definition name.
	 * @param targetObject
	 *            the target object on with the code generation is applied.
	 */
	public XpandEvaluationRequest(String definitionName, Object targetObject) {
		this(definitionName, targetObject, Collections.emptyList());
	}

	/**
	 * Constructs an Xpand code generation evaluation request.
	 * 
	 * @param definitionName
	 *            the Xpand definition name.
	 * @param targetObject
	 *            the target object on with the code generation is applied.
	 * @param parameterList
	 *            a list of parameters to be use in code generation.
	 */
	public XpandEvaluationRequest(String definitionName, Object targetObject, List<Object> parameterList) {
		Assert.isNotNull(definitionName);
		Assert.isTrue(definitionName.trim().length() != 0);
		Assert.isNotNull(targetObject);

		this.definitionName = definitionName;
		this.targetObject = targetObject;
		this.parameterList = parameterList != null ? parameterList : Collections.emptyList();
	}

	/**
	 * Gets the definition name used in this Xpand evaluation request.
	 */
	public String getDefinitionName() {
		return definitionName;
	}

	/**
	 * Gets the target object on with is applied code generation.
	 */
	public Object getTargetObject() {
		return targetObject;
	}

	/**
	 * Gets the list of parameters used in code generation.
	 */
	public List<Object> getParameterList() {
		return parameterList;
	}
}
