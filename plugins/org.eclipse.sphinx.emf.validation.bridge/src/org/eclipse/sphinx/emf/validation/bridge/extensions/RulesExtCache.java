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
package org.eclipse.sphinx.emf.validation.bridge.extensions;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sphinx.emf.validation.bridge.Activator;
import org.eclipse.sphinx.emf.validation.bridge.util.RulesExtReader;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;

/**
 * This singleton is a cache in order to treat the extension contribution to org.eclipse.sphinx.emf.validation. It
 * implements the IRegistryChangeListener in order to catch event from the extension registry.
 */
public class RulesExtCache implements IRegistryChangeListener {

	private final HashSet<IExtension> extensions = new HashSet<IExtension>();

	private final HashMap<String, RulesExtInternal> rulesExtInternal = new HashMap<String, RulesExtInternal>();

	// The singleton instance
	private static RulesExtCache rulesExtCache = null;

	/**
	 * The constructor
	 */
	private RulesExtCache() {
	}

	/**
	 * accessor to the internal map with extension contributing to org.eclipse.sphinx.emf.validation see
	 * {@link RulesExtInternal}
	 * 
	 * @return the corresponding map
	 */
	public HashMap<String, RulesExtInternal> getRulesExtInternals() {
		return rulesExtInternal;
	}

	/**
	 * @return the singleton instance of RulesExtCache
	 */
	public static RulesExtCache getSingleton() {
		if (rulesExtCache == null) {
			rulesExtCache = new RulesExtCache();
		}

		return rulesExtCache;
	}

	/**
	 * Initialize the cache
	 */
	public void startup() {

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint pt = reg.getExtensionPoint(Activator.RULES_EXT_ID);
		IExtension[] exts = pt.getExtensions();

		addExtension(exts);

		reg.addRegistryChangeListener(this, Activator.RULES_NAMESPACE_ID);

	}

	/**
	 * shutdown the listener on the extension registry, clean the cache
	 */
	public void shutdown() {
		extensions.clear();
		rulesExtInternal.clear();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		reg.removeRegistryChangeListener(this);
	}

	public void registryChanged(IRegistryChangeEvent event) {

		IExtensionDelta[] deltas = event.getExtensionDeltas();
		for (IExtensionDelta element : deltas) {
			if (element.getExtensionPoint().getUniqueIdentifier().equals(Activator.RULES_EXT_ID) && element.getKind() == IExtensionDelta.ADDED) {
				addExtension(element.getExtension());
			} else {
				extensions.remove(element.getExtension());
			}
		}

	}

	/**
	 * add extension contributions to the cache
	 * 
	 * @param exts
	 *            contributions to the extension
	 */
	private void addExtension(IExtension[] exts) {

		if (exts == null || exts.length == 0) {
			return;
		}

		for (IExtension element : exts) {
			addExtension(element);
		}

		return;
	}

	/**
	 * add an extension contribution to the cache
	 * 
	 * @param ext
	 *            contribution to the extension
	 */
	private void addExtension(IExtension ext) {

		extensions.add(ext);

		RulesExtInternal r = null;
		r = RulesExtReader.getSingleton().readExtension(ext);

		if (r != null) {
			rulesExtInternal.put(r.getMarker(), r);
		}

		IContributor ic = ext.getContributor();
		if (ic != null) {
			ExtendedPlatform.loadBundle(ic.getName());
		}

		return;
	}
}
