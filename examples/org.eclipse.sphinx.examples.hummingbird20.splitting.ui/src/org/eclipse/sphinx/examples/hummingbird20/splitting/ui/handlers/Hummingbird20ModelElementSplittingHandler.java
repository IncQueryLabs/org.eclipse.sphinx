/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
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
package org.eclipse.sphinx.examples.hummingbird20.splitting.ui.handlers;

import org.eclipse.sphinx.emf.splitting.IModelElementSplittingListener;
import org.eclipse.sphinx.emf.splitting.ui.handlers.AbstractModelElementSplittingHandler;
import org.eclipse.sphinx.examples.hummingbird20.splitting.Hummingbird20TypeModelElementSplittingListener;

public class Hummingbird20ModelElementSplittingHandler extends AbstractModelElementSplittingHandler {

	private IModelElementSplittingListener modelElementSplittingListener;

	public Hummingbird20ModelElementSplittingHandler() {
		super();
	}

	@Override
	protected IModelElementSplittingListener getModelElementSplittingListerners() {
		if (modelElementSplittingListener == null) {
			modelElementSplittingListener = new Hummingbird20TypeModelElementSplittingListener();
		}
		return modelElementSplittingListener;
	}
}
