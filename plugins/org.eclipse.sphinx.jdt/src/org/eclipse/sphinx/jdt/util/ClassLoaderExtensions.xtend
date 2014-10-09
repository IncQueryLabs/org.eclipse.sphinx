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
package org.eclipse.sphinx.jdt.util

import java.net.URLClassLoader
import java.util.ArrayList
import java.util.Arrays

class ClassLoaderExtensions {

	static def void printHierarchy(ClassLoader classLoader) {
		val classLoaderHierarchy = new ArrayList<ClassLoader>();
		classLoaderHierarchy.add(classLoader)

		var parentClassLoader = classLoader.parent
		while (parentClassLoader != null) {
			classLoaderHierarchy.add(parentClassLoader)
			parentClassLoader = parentClassLoader.parent
		}

		classLoaderHierarchy.reverse
		classLoaderHierarchy.forEach[print(it)]
	}

	static def void print(ClassLoader classLoader) {
		var String classLoaderAsString
		if (classLoader instanceof URLClassLoader) {
			classLoaderAsString = classLoader.getClass().name + " [urls=" + Arrays.toString(classLoader.URLs) + "]"
		} else {
			classLoaderAsString = classLoader.toString
		}

		println(classLoaderAsString)
	}
}
