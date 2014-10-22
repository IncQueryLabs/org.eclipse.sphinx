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
package org.eclipse.sphinx.examples.workflows.lib;

import com.google.common.base.Function;

@SuppressWarnings("nls")
public class Example {

	public void doSomething() {
		System.out.println("Example class doing something");
	}

	public void doSomethingUsingAnonymousClass() {
		System.out.println("Example class doing something using anonymous class");

		Function<String, Integer> stringLengthFunction = new Function<String, Integer>() {
			@Override
			public Integer apply(String string) {
				return string.length();
			}
		};
		System.out.println("Length of 'Example String' = " + stringLengthFunction.apply("Example String"));
	}
}
