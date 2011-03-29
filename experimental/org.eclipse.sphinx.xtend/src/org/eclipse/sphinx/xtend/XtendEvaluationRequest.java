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

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;

public class XtendEvaluationRequest {

	private String extensionName;
	private List<Object> parameterList;
	private Object targetObject;

	public XtendEvaluationRequest(String extensionName, Object targetObject) {
		this(extensionName, targetObject, Collections.emptyList());
	}

	public XtendEvaluationRequest(String extensionName, Object targetObject, List<Object> parameterList) {
		Assert.isNotNull(extensionName);
		Assert.isNotNull(targetObject);

		this.extensionName = extensionName;
		this.targetObject = targetObject;
		this.parameterList = parameterList != null ? parameterList : Collections.emptyList();
	}

	public String getExtensionName() {
		return extensionName;
	}

	public Object getTargetObject() {
		return targetObject;
	}

	public List<Object> getParameterList() {
		return parameterList;
	}

}
