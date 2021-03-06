/**
 * <copyright>
 *
 * Copyright (c) 2008-2012 See4sys, BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     BMW Car IT - Avoid usage of Object.finalize
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.ProxyHelperAdapterFactory;
import org.eclipse.sphinx.emf.internal.model.ModelDescriptorRegistryInitializer;
import org.eclipse.sphinx.emf.internal.model.ModelDescriptorSynchronizer;
import org.eclipse.sphinx.emf.scoping.ResourceScopeMarkerSynchronizer;
import org.osgi.framework.BundleContext;

/**
 * This is the central singleton for this plug-in.
 */
public final class Activator extends EMFPlugin {
	/**
	 * Keep track of the singleton.
	 */
	public static final Activator INSTANCE = new Activator();

	/**
	 * Keep track of the singleton.
	 */
	private static Implementation plugin;

	/**
	 * Create the instance.
	 */
	public Activator() {
		super(new ResourceLocator[] {});
	}

	/**
	 * Returns the singleton instance of the Eclipse plug-in.
	 * 
	 * @return the singleton instance.
	 */
	@Override
	public ResourceLocator getPluginResourceLocator() {
		return plugin;
	}

	/**
	 * Returns the singleton instance of the Eclipse plug-in.
	 * 
	 * @return the singleton instance.
	 */
	public static Implementation getPlugin() {
		return plugin;
	}

	/**
	 * Returns the singleton instance of the Eclipse plug-in. This method does actually the same thing as getPlugin()
	 * and has been put in place for compatibility reasons with Activator classes which are not EMF-based but generated
	 * by PDE.
	 * 
	 * @return the singleton instance.
	 */
	public static Implementation getDefault() {
		return plugin;
	}

	/**
	 * The actual implementation of the Eclipse <b>Plug-in</b>.
	 */
	public static class Implementation extends EclipsePlugin {

		/**
		 * Creates an instance.
		 */
		public Implementation() {
			super();

			// Remember the static instance
			plugin = this;
		}

		@Override
		public void start(BundleContext context) throws Exception {
			super.start(context);

			startWorkspaceSynchronizing();

			ProxyHelperAdapterFactory.INSTANCE.start();

			new ModelDescriptorRegistryInitializer().schedule();
		}

		@Override
		public void stop(BundleContext context) throws Exception {
			ProxyHelperAdapterFactory.INSTANCE.stop();

			stopWorkspaceSynchronizing();

			super.stop(context);
		}

		/**
		 * Starts automatic synchronization of models wrt resource changes in the workspace. Supports
		 * loading/unloading/reloading of complete models when underlying projects are
		 * created/opened/renamed/closed/deleted or their description or settings are changed as well as
		 * loading/unloading/reloading of individual model resources when underlying files are created/changed/deleted.
		 */
		public void startWorkspaceSynchronizing() {
			ModelDescriptorSynchronizer.INSTANCE.start();
			ResourceScopeMarkerSynchronizer.INSTANCE.start();
		}

		/**
		 * Stops automatic synchronization of models wrt resource changes in the workspace.
		 * 
		 * @see #startWorkspaceSynchronizing()
		 */
		public void stopWorkspaceSynchronizing() {
			ModelDescriptorSynchronizer.INSTANCE.stop();
			ResourceScopeMarkerSynchronizer.INSTANCE.stop();
		}
	}
}
