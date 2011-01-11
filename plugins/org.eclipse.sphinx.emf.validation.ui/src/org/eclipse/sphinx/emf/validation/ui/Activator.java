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
package org.eclipse.sphinx.emf.validation.ui;

import java.net.URL;
import java.util.Hashtable;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	/** The shared instance */
	private static Activator plugin;

	/** The icon folder */
	public static final String ICON_PATH = "$nl$/icons/"; //$NON-NLS-1$

	/** The image descriptor registry */
	private Hashtable<String, ImageDescriptor> imageDescriptors = new Hashtable<String, ImageDescriptor>(20);

	private IPreferenceStore corePreferenceStore;

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
	}

	/*
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
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

	public static String getPluginId() {
		return getDefault().getBundle().getSymbolicName();
	}

	/**
	 * Returns the image descriptor for the given image ID. Returns null if there is no such image.
	 * 
	 * @param id
	 *            the identifier for the image to retrieve
	 * @return the image associated with the given ID
	 */
	public static ImageDescriptor getImageDescriptor(String id) {
		/*
		 * Delegates to the plug-in instance in order to avoid concurrent class loading problems.
		 */
		return getDefault().privateGetImageDescriptor(id);
	}

	/**
	 * Creates an image and places it in the image registry.
	 * 
	 * @param id
	 *            the identifier for the image
	 * @param baseURL
	 *            the base URL for the image
	 */
	private static void createImageDescriptor(Activator plugin, String id) {
		/*
		 * Delegates to the plug-in instance in order to avoid concurrent class loading problems.
		 */
		plugin.createImageDescriptor(id);
	}

	private void createImageDescriptor(String id) {
		ImageDescriptor desc = ImageDescriptor.createFromURL(getImageUrl(id));
		imageDescriptors.put(id, desc);
	}

	public ImageDescriptor privateGetImageDescriptor(String id) {
		if (!imageDescriptors.containsKey(id)) {
			createImageDescriptor(getDefault(), id);
		}
		return imageDescriptors.get(id);
	}

	private URL getImageUrl(String relative) {
		return FileLocator.find(Platform.getBundle(getPluginId()), new Path(ICON_PATH + relative), null);
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		if (corePreferenceStore == null) {
			org.eclipse.sphinx.emf.validation.Activator coreActivator = org.eclipse.sphinx.emf.validation.Activator.getDefault();
			corePreferenceStore = new ScopedPreferenceStore(coreActivator.getInstanceScope(), coreActivator.getBundle().getSymbolicName());
		}
		return corePreferenceStore;
	}
}
