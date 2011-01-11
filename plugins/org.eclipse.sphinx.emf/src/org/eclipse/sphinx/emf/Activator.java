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
package org.eclipse.sphinx.emf;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
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

		private InstanceScope instanceScope;

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

			ModelDescriptorRegistryInitializer modelDescriptorRegistryInitializer = new ModelDescriptorRegistryInitializer();
			modelDescriptorRegistryInitializer.setPriority(Job.BUILD);
			modelDescriptorRegistryInitializer.schedule();
		}

		@Override
		public void stop(BundleContext context) throws Exception {
			super.stop(context);

			stopWorkspaceSynchronizing();
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
		 * Starts automatic synchronization of models wrt resource changes in the workspace. Supports
		 * loading/unloading/reloading of complete models when underlying projects are
		 * created/opened/renamed/closed/deleted or their description or settings are changed as well as
		 * loading/unloading/reloading of individual model resources when underlying files are created/changed/deleted.
		 */
		public void startWorkspaceSynchronizing() {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(
					ModelDescriptorSynchronizer.INSTANCE,
					IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
							| IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.POST_CHANGE);

			ResourcesPlugin.getWorkspace().addResourceChangeListener(
					ResourceScopeMarkerSynchronizer.INSTANCE,
					IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
							| IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.POST_CHANGE);
		}

		/**
		 * Stops automatic synchronization of models wrt resource changes in the workspace.
		 * 
		 * @see #startWorkspaceSynchronizing()
		 */
		public void stopWorkspaceSynchronizing() {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(ModelDescriptorSynchronizer.INSTANCE);
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(ResourceScopeMarkerSynchronizer.INSTANCE);
		}
	}
}
