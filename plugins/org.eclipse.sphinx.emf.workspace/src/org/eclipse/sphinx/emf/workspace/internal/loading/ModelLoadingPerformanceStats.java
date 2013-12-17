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
package org.eclipse.sphinx.emf.workspace.internal.loading;

import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.platform.stats.AbstractPerformanceStats;
import org.eclipse.sphinx.platform.stats.IEventTypeEnumerator;

/**
 * 
 */
final public class ModelLoadingPerformanceStats extends AbstractPerformanceStats<ModelLoadingPerformanceStats.ModelEvent> {

	/**
	 * The singleton instance.
	 */
	public static ModelLoadingPerformanceStats INSTANCE = new ModelLoadingPerformanceStats();

	/*
	 * @see org.eclipse.sphinx.platform.stats.AbstractPerformanceStats#getPluginId()
	 */
	@Override
	protected String getPluginId() {
		return Activator.getPlugin().getSymbolicName();
	}

	/**
	 * 
	 */
	public enum ModelEvent implements IEventTypeEnumerator {

		/**
		 * Event for reading model loader contributions.
		 */
		EVENT_READ_CONTRIBUTED_MODEL_LOADERS("readContributedModelLoaders"), //$NON-NLS-1$

		/**
		 * Event for resource loading.
		 */
		EVENT_LOAD_FILE("loadFile"), //$NON-NLS-1$

		/**
		 * Event for proxy resolution.
		 */
		EVENT_RESOLVE_PROXY("resolveProxies"), //$NON-NLS-1$

		/**
		 * Event for creating model load requests.
		 */
		EVENT_DETECT_FILES_TO_LOAD("detectFilesToLoad"); //$NON-NLS-1$

		/**
		 * The name of this event.
		 */
		private String name;

		/**
		 * Private enumerator constructor.
		 * 
		 * @param eventName
		 *            The name of this event.
		 */
		private ModelEvent(String eventName) {
			name = eventName;
		}

		/*
		 * @see org.eclipse.sphinx.platform.stats.IEventTypeEnumerator#getName()
		 */
		@Override
		public String getName() {
			return name;
		}
	}

	/**
	 * 
	 */
	public enum ModelContext implements IEventTypeEnumerator {

		/**
		 * Context for initial workspace loading.
		 */
		CONTEXT_INITIAL_LOAD_WORKSPACE("Workspace Initial loading"), //$NON-NLS-1$

		/**
		 * Context for model loading.
		 */
		CONTEXT_LOAD_MODEL("Load Model"), //$NON-NLS-1$

		/**
		 * Context for project loading.
		 */
		CONTEXT_LOAD_PROJECT("Load Project"), //$NON-NLS-1$

		/**
		 * Context for resource loading.
		 */
		CONTEXT_LOAD_FILES("Load Files"), //$NON-NLS-1$

		/**
		 * Context for resource loading.
		 */
		CONTEXT_LOAD_RESOURCE("Load Resource"); //$NON-NLS-1$

		/**
		 * The name of this context.
		 */
		private String name;

		/**
		 * Private enumerator constructor.
		 * 
		 * @param contextName
		 *            The name of this context.
		 */
		private ModelContext(String contextName) {
			name = contextName;
		}

		/*
		 * @see org.eclipse.sphinx.platform.stats.IEventTypeEnumerator#getName()
		 */
		@Override
		public String getName() {
			return name;
		}
	}
}
