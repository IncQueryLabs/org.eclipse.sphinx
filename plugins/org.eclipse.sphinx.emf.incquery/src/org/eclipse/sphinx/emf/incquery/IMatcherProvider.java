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
package org.eclipse.sphinx.emf.incquery;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * @deprecated Use {@link NavigationHelper} instead to get all instances of a type!
 */
@SuppressWarnings("rawtypes")
@Deprecated
public interface IMatcherProvider {

	boolean isProviderForType(EObject eObject);

	boolean isProviderForType(EClass eType);

	<T> boolean isProviderForType(Class<T> type);

	IncQueryMatcher getMatcher(IncQueryEngine engine, EObject eObject) throws IncQueryException;

	IncQueryMatcher getMatcher(IncQueryEngine engine, EClass eType) throws IncQueryException;

	<T> IncQueryMatcher getMatcher(IncQueryEngine engine, Class<T> type) throws IncQueryException;
}
