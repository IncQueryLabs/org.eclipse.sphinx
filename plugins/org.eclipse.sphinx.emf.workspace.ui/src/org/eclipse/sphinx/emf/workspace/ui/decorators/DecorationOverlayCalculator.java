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
package org.eclipse.sphinx.emf.workspace.ui.decorators;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.sphinx.emf.workspace.ui.internal.Activator;
import org.eclipse.sphinx.emf.workspace.ui.viewers.ITreeContentProviderIterator;
import org.eclipse.sphinx.emf.workspace.ui.viewers.TreeContentProviderIterator;
import org.eclipse.sphinx.platform.resources.IProblemMarkerFinder;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class DecorationOverlayCalculator {

	public enum DecorationOverlayKind {
		NONE, WARNING, ERROR;

		public DecorationOverlayKind update(DecorationOverlayKind overlayKind) {
			return isLessThan(overlayKind) ? overlayKind : this;
		}

		public boolean isLessThan(DecorationOverlayKind otherOverlayKind) {
			Assert.isNotNull(otherOverlayKind);
			return ordinal() < otherOverlayKind.ordinal();
		}

		public boolean isGreaterThan(DecorationOverlayKind otherOverlayKind) {
			Assert.isNotNull(otherOverlayKind);
			return ordinal() > otherOverlayKind.ordinal();
		}
	}

	protected IProblemMarkerFinder problemMarkerFinder;

	private ITreeContentProviderIterator.IItemFilter itemFilter;

	protected Map<Object, DecorationOverlayCalculator.DecorationOverlayKind> decorationOverlayCache = new HashMap<Object, DecorationOverlayCalculator.DecorationOverlayKind>();

	public DecorationOverlayCalculator(IProblemMarkerFinder problemMarkerFinder) {
		Assert.isNotNull(problemMarkerFinder);
		this.problemMarkerFinder = problemMarkerFinder;
	}

	public ITreeContentProviderIterator.IItemFilter getItemFilter() {
		return itemFilter;
	}

	public void setItemFilter(ITreeContentProviderIterator.IItemFilter itemFilter) {
		this.itemFilter = itemFilter;
	}

	public DecorationOverlayCalculator.DecorationOverlayKind getDecorationOverlayKind(ITreeContentProvider contentProvider, Object item) {
		Assert.isNotNull(contentProvider);
		try {
			DecorationOverlayKind itemOverlayKind = decorationOverlayCache.get(item);
			if (itemOverlayKind != null) {
				return itemOverlayKind;
			}

			ITreeContentProviderIterator iter = new TreeContentProviderIterator(contentProvider, item, itemFilter);
			while (iter.hasNext()) {
				Object childItem = iter.next();
				DecorationOverlayKind childItemOverlayKind = decorationOverlayCache.get(childItem);
				if (childItemOverlayKind != null) {
					iter.prune();
				} else {
					int severity = problemMarkerFinder.getSeverity(childItem);
					childItemOverlayKind = getDecorationOverlayKind(severity);

					if (iter.isRecurrent()) {
						resetCachedAncestorDecorationKinds(contentProvider, childItem, item);
					} else {
						decorationOverlayCache.put(childItem, childItemOverlayKind);
						updateCachedAncestorDecorationOverlayKinds(contentProvider, childItem, childItemOverlayKind);
					}
				}

				if (itemOverlayKind == null || childItemOverlayKind.isGreaterThan(itemOverlayKind)) {
					itemOverlayKind = childItemOverlayKind;
				}
				if (itemOverlayKind == DecorationOverlayKind.ERROR) {
					break;
				}
			}

			decorationOverlayCache.put(item, itemOverlayKind);
			updateCachedAncestorDecorationOverlayKinds(contentProvider, item, itemOverlayKind);
			return itemOverlayKind;
		} catch (CoreException ex) {
			PlatformLogUtil.logAsWarning(Activator.getPlugin(), ex);
		}
		return DecorationOverlayKind.NONE;
	}

	protected DecorationOverlayKind getDecorationOverlayKind(int severity) {
		switch (severity) {
		case IMarker.SEVERITY_WARNING:
			return DecorationOverlayKind.WARNING;
		case IMarker.SEVERITY_ERROR:
			return DecorationOverlayKind.ERROR;
		default:
			return DecorationOverlayKind.NONE;
		}
	}

	protected void updateCachedAncestorDecorationOverlayKinds(ITreeContentProvider contentProvider, Object object,
			DecorationOverlayKind newOverlayKind) {
		Assert.isNotNull(contentProvider);
		Assert.isNotNull(newOverlayKind);

		Object parent = contentProvider.getParent(object);
		while (parent != null) {
			DecorationOverlayKind cachedOverlayKind = decorationOverlayCache.get(parent);
			if (cachedOverlayKind != null && newOverlayKind.isGreaterThan(cachedOverlayKind)) {
				decorationOverlayCache.put(parent, newOverlayKind);
			} else {
				break;
			}
			parent = contentProvider.getParent(parent);
		}
	}

	protected void resetCachedAncestorDecorationKinds(ITreeContentProvider contentProvider, Object object, Object limit) {
		Assert.isNotNull(contentProvider);

		Object parent = contentProvider.getParent(object);
		while (parent != limit && parent != null) {
			decorationOverlayCache.remove(parent);
			parent = contentProvider.getParent(parent);
		}
	}

	public void reset() {
		decorationOverlayCache.clear();
		problemMarkerFinder.reset();
	}
}