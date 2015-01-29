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
package org.eclipse.sphinx.emf.search.ui.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sphinx.emf.search.ui.ModelSearchResult;
import org.eclipse.sphinx.emf.search.ui.pages.ModelSearchResultViewPage;

public abstract class AbstractModelSearchContentProvider implements IStructuredContentProvider {

	protected final Object[] EMPTY_ARR = new Object[0];

	private ModelSearchResult searchResult;
	private ModelSearchResultViewPage searchResultPage;

	public AbstractModelSearchContentProvider(ModelSearchResultViewPage page) {
		searchResultPage = page;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		initialize((ModelSearchResult) newInput);

	}

	protected void initialize(ModelSearchResult result) {
		searchResult = result;
	}

	public abstract void elementsChanged(Object[] updatedElements);

	public abstract void clear();

	@Override
	public void dispose() {
		// nothing to do
	}

	ModelSearchResultViewPage getPage() {
		return searchResultPage;
	}

	ModelSearchResult getSearchResult() {
		return searchResult;
	}
}
