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
package org.eclipse.sphinx.examples.hummingbird20.incquery.services;

import org.eclipse.sphinx.emf.incquery.services.AbstractModelQueryService;
import org.eclipse.sphinx.examples.hummingbird20.incquery.common.CommonMatcherProvider;
import org.eclipse.sphinx.examples.hummingbird20.incquery.instancemodel.InstanceModelMatcherProvider;
import org.eclipse.sphinx.examples.hummingbird20.incquery.typemodel.TypeModelMatcherProvider;

//TODO Use AbstractWorkspaceModelQueryService
public class Hummindbird20QueryService extends AbstractModelQueryService {

	@Override
	protected void initMatcherProviders() {
		getMatcherProviders().add(new CommonMatcherProvider());
		getMatcherProviders().add(new InstanceModelMatcherProvider());
		getMatcherProviders().add(new TypeModelMatcherProvider());
	}
}
