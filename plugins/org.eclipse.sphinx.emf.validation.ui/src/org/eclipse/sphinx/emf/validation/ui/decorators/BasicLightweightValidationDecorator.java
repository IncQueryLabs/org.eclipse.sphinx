/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.validation.ui.decorators;

import org.eclipse.sphinx.emf.util.EObjectUtil;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.sphinx.emf.validation.markers.ValidationMarkerManager;
import org.eclipse.sphinx.emf.validation.markers.ValidationStatusCode;
import org.eclipse.sphinx.emf.validation.ui.Activator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class BasicLightweightValidationDecorator implements ILabelDecorator, ILightweightLabelDecorator {

	// Images cached for better performance
	protected static ImageDescriptor errorImg;
	protected static ImageDescriptor warningImg;

	protected ValidationMarkerManager markerManager;

	/*
	 * Define a cached image descriptor which only creates the image data once
	 */
	public static class CachedImageDescriptor extends ImageDescriptor {
		ImageDescriptor descriptor;
		ImageData data;

		public CachedImageDescriptor(ImageDescriptor descriptor) {
			this.descriptor = descriptor;
		}

		@Override
		public ImageData getImageData() {
			if (data == null) {
				data = descriptor.getImageData();
			}
			return data;
		}
	}

	static {
		errorImg = new CachedImageDescriptor(Activator.getImageDescriptor(ISharedImages.IMG_ERROR));
		warningImg = new CachedImageDescriptor(Activator.getImageDescriptor(ISharedImages.IMG_WARNING));
	}

	public BasicLightweightValidationDecorator() {
		markerManager = ValidationMarkerManager.getInstance();
	}

	public Image decorateImage(Image image, Object element) {
		return null;
	}

	public String decorateText(String text, Object element) {
		return null;
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {

	}

	public void decorate(Object element, IDecoration decoration) {
		ImageDescriptor img = null;
		switch (computeValidationStatusCode(element)) {
		case ValidationStatusCode.SEVERITY_ERROR:
		case ValidationStatusCode.SEVERITY_LOCAL_ERROR:
		case ValidationStatusCode.SEVERITY_ERROR_ON_CHILDREN:
			img = errorImg;
			break;
		case ValidationStatusCode.SEVERITY_WARNING:
		case ValidationStatusCode.SEVERITY_LOCAL_WARNING:
		case ValidationStatusCode.SEVERITY_WARNING_ON_CHILDREN:
			img = warningImg;
			break;
		default: // do nothing!
		}

		if (img != null) {
			decoration.addOverlay(img, IDecoration.BOTTOM_LEFT);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	protected int computeValidationStatusCode(Object element) {

		int result = ValidationStatusCode.SEVERITY_OK;
		IMarker[] markers = new IMarker[0];

		try {

			if (element instanceof EObject) {
				markers = markerManager.getValidationMarkersList((EObject) element, EObjectUtil.DEPTH_INFINITE);
			} else if (element instanceof IResource) {
				IResource resource = (IResource) element;
				if (resource.exists() && resource.getProject() != null && resource.getProject().isAccessible()) {
					markers = resource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				}
			}

			if (markerManager.isError(markers)) {
				result = ValidationStatusCode.SEVERITY_ERROR;
			} else if (markerManager.isWarning(markers)) {
				result = ValidationStatusCode.SEVERITY_WARNING;
			}

		} catch (CoreException cex) {
			PlatformLogUtil.logAsWarning(Activator.getDefault(), cex);
		}
		return result;
	}
}
