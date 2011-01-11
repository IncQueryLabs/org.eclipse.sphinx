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
package org.eclipse.sphinx.platform.internal;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.sphinx.platform.IExtendedPlatformConstants;
import org.eclipse.sphinx.platform.internal.util.ContentTypeIdCachePurger;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	public static BundleContext getContext() {
		return context;
	}

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.eclipse.sphinx.platform"; //$NON-NLS-1$

	/** The shared instance */
	private static Activator plugin;

	/** The bundle context */
	private static BundleContext context;

	private IResourceChangeListener contentTypeIdPropertyInvalidator;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		Activator.context = context;

		System.setProperty(IExtendedPlatformConstants.SYSTEM_PROPERTY_PLATFORM_FEATURE_VERSION, ExtendedPlatform.getFeatureVersion());

		contentTypeIdPropertyInvalidator = new ContentTypeIdCachePurger();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(contentTypeIdPropertyInvalidator);
	}

	/*
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(contentTypeIdPropertyInvalidator);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns the shared bundle context
	 * 
	 * @return the shared bundle context
	 */
	public static BundleContext getBundleContext() {
		return context;
	}
}
