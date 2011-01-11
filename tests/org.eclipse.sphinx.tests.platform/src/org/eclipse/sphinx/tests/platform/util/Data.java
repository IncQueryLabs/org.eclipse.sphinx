/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.tests.platform.util;

import java.util.List;

public class Data {
	public String publicField;
	private String privateField;
	protected String protectedField;

	public static final String pubSField = "public static field";
	protected static final String proSField = "protected static field";
	private static final String priSField = "private static field";

	private List<String> strList;

	public Data(String strPublicValue, String strPrivateValue, String strProtectedValue) {
		publicField = strPublicValue;
		setPrivateField(strPrivateValue);
		protectedField = strProtectedValue;
	}

	public static String getPrisfield() {
		return priSField;
	}

	public static String getProsfield() {
		return proSField;
	}

	public void setPrivateField(String privateField) {
		this.privateField = privateField;
	}

	public String getPrivateField() {
		return privateField;
	}

	protected static String proctectedStaticMethod() {
		return proSField;
	}

	private static String privateStaticMethod() {
		return priSField;
	}

}
