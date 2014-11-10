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
package org.eclipse.sphinx.examples.hummingbird20.ide.ui.actions;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.examples.actions.BasicWalkUpAncestorsAction;
import org.eclipse.sphinx.examples.hummingbird20.ide.ui.providers.Hummingbird20ItemProviderAdapterFactory;

public class Hummingbird20WalkUpAncestorsAction extends BasicWalkUpAncestorsAction {

	public Hummingbird20WalkUpAncestorsAction(Viewer viewer) {
		super(viewer);
	}

	@Override
	protected AdapterFactory getCustomAdapterFactory() {
		return Hummingbird20ItemProviderAdapterFactory.INSTANCE;
	}
}
