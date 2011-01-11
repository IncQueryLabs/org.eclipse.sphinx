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
package org.eclipse.sphinx.gmfgen;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends EMFPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.sphinx.gmfgen"; //$NON-NLS-1$

	// The one instance of this class.
	private static Implementation plugin;

	// The singleton instance of the plugin.
	public static final Activator INSTANCE = new Activator();

	/**
	 * The constructor
	 */
	private Activator() {
		super(new ResourceLocator[] {});
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Implementation getPlugin() {
		return plugin;
	}

	@Override
	public ResourceLocator getPluginResourceLocator() {

		return plugin;
	}

	/**
	 * The actual implementation of the Eclipse <b>Plugin</b>.
	 */
	public static class Implementation extends EclipsePlugin {

		/**
		 * Creates an instance.
		 */
		public Implementation() {
			super();

			// Remember the static instance.
			//
			plugin = this;
		}
	}
}
