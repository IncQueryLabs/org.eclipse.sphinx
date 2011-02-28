/**
 * <copyright>
 * 
 * Copyright (c) See4sys and others.
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

// TODO Rename to XPandEvaluationRequest
public class ExecutionContextRequest {

	private String definitionName;
	private Object targetObject;
	private List<Object> parameterList;

	public ExecutionContextRequest(String definitionName, Object targetObject) {
		this(definitionName, targetObject, Collections.emptyList());
	}

	public ExecutionContextRequest(String definitionName, Object targetObject, List<Object> parameterList) {
		Assert.isNotNull(definitionName);
		Assert.isTrue(definitionName.trim().length() != 0);
		Assert.isNotNull(targetObject);

		this.definitionName = definitionName;
		this.targetObject = targetObject;
		this.parameterList = parameterList != null ? parameterList : Collections.emptyList();
	}

	public String getDefinitionName() {
		return definitionName;
	}

	public Object getTargetObject() {
		return targetObject;
	}

	public List<Object> getParameterList() {
		return parameterList;
	}
}
