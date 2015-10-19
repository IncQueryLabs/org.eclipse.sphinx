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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.sphinx.emf.workspace.resources.BasicModelProblemMarkerFinder;
import org.eclipse.sphinx.emf.workspace.ui.decorators.DecorationOverlayCalculator.DecorationOverlayKind;
import org.eclipse.sphinx.emf.workspace.ui.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.decorators.DecorationBuilder;

@SuppressWarnings("restriction")
public class BasicModelProblemDecorator implements ILightweightLabelDecorator {

	protected DecorationOverlayCalculator decorationOverlayCalculator;
	protected IResourceChangeListener problemMarkerChangeListener;

	public BasicModelProblemDecorator() {
		decorationOverlayCalculator = createDeclarationOverlayCalculator();

		problemMarkerChangeListener = createProblemMarkerChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(problemMarkerChangeListener, IResourceChangeEvent.POST_CHANGE);
	}

	protected DecorationOverlayCalculator createDeclarationOverlayCalculator() {
		return new DecorationOverlayCalculator(new BasicModelProblemMarkerFinder());
	}

	/**
	 * Creates an {@link IResourceChangeListener} in order to wake up decoration of IContainer and IResource.
	 */
	protected IResourceChangeListener createProblemMarkerChangeListener() {
		return new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				Assert.isNotNull(event);

				IMarkerDelta[] markerDelta = event.findMarkerDeltas(IMarker.PROBLEM, true);
				if (markerDelta != null && markerDelta.length > 0) {
					decorationOverlayCalculator.reset();
				}
			}
		};
	}

	/*
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
	 * org.eclipse.jface.viewers.IDecoration)
	 */
	@Override
	public void decorate(Object element, IDecoration decoration) {
		ITreeContentProvider contentProvider = getTreeContentProvider(decoration);
		if (contentProvider != null) {
			String overlayImageName = null;
			DecorationOverlayKind overlayKind = decorationOverlayCalculator.getDecorationOverlayKind(contentProvider, element);
			switch (overlayKind) {
			case NONE:
				break;
			case WARNING:
				overlayImageName = ISharedImages.IMG_DEC_FIELD_WARNING;
				break;
			case ERROR:
				overlayImageName = ISharedImages.IMG_DEC_FIELD_ERROR;
				break;
			default:
				PlatformLogUtil.logAsError(Activator.getPlugin(), new RuntimeException("Invalid decoration overlay kind: " + overlayKind)); //$NON-NLS-1$
				break;
			}

			if (overlayImageName != null) {
				ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
				ImageDescriptor overlayImageDescriptor = sharedImages.getImageDescriptor(overlayImageName);
				decoration.addOverlay(overlayImageDescriptor, IDecoration.BOTTOM_LEFT);
			}
		}
	}

	protected ITreeContentProvider getTreeContentProvider(IDecoration decoration) {
		if (decoration instanceof DecorationBuilder) {
			DecorationBuilder builder = (DecorationBuilder) decoration;
			IDecorationContext context = builder.getDecorationContext();
			return (ITreeContentProvider) context.getProperty(ITreeContentProvider.class.getName());
		}
		return null;
	}

	/*
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	/*
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {

	}

	/*
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(problemMarkerChangeListener);
	}
}