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
package org.eclipse.sphinx.jdt.loaders

import org.eclipse.core.runtime.Assert

class DelegatingClassLoader extends ClassLoader {

	private ClassLoader delegate;

	new(ClassLoader parent, ClassLoader delegate) {
		super(parent);

		Assert.isNotNull(delegate);
		this.delegate = delegate;
	}

	override findClass(String name) {
		delegate.loadClass(name);
	}

	override toString() {
		class.getSimpleName() + " [delegate=" + delegate + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
