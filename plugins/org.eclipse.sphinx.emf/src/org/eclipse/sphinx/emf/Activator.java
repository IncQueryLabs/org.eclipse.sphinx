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

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.sphinx.emf.internal.ecore.proxymanagement.ProxyHelperAdapterFactory;
import org.eclipse.sphinx.emf.internal.model.ModelDescriptorRegistryInitializer;
import org.eclipse.sphinx.emf.internal.model.ModelDescriptorSynchronizer;
import org.eclipse.sphinx.emf.scoping.ResourceScopeMarkerSynchronizer;
import org.eclipse.sphinx.platform.resources.MarkerJob;
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

		private InstanceScope instanceScope;

		private MarkerJob markerJob;

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

			new ModelDescriptorRegistryInitializer().schedule();

			ProxyHelperAdapterFactory.INSTANCE.start();
		}

		@Override
		public void stop(BundleContext context) throws Exception {
			ProxyHelperAdapterFactory.INSTANCE.stop();

			stopWorkspaceSynchronizing();

			super.stop(context);
		}

		/**
		 * Returns the InstanceScope of the Plug-in.
		 * 
		 * @return The Plug-in's InstanceScope.
		 */
		public InstanceScope getInstanceScope() {
			if (instanceScope == null) {
				instanceScope = new InstanceScope();
			}
			return instanceScope;
		}

		/**
		 * Returns the shared marker job instance that can be used to asynchronously manipulate resource markers to
		 * avoid deadlocks. After registering manipulations with the marker job the job must be explicitly scheduled by
		 * the caller.
		 * 
		 * @return The shared marker job instance.
		 */
		public MarkerJob getMarkerJob() {
			if (markerJob == null) {
				markerJob = new MarkerJob();
			}

			return markerJob;
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
