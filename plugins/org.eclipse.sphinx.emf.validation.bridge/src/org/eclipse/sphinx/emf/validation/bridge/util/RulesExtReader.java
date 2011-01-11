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
package org.eclipse.sphinx.emf.validation.bridge.util;

import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.validation.bridge.Activator;
import org.eclipse.sphinx.emf.validation.bridge.extensions.RulesExtInternal;

public class RulesExtReader {

	final static public String TAG_RULE_EXT = "model"; //$NON-NLS-1$
	final static public String ATT_RULE_EXT_NAME = "Name"; //$NON-NLS-1$
	final static public String ATT_RULE_EXT_NSURI = "NsURI"; //$NON-NLS-1$
	final static public String ATT_RULE_EXT_MODELCLASS = "class"; //$NON-NLS-1$
	final static public String ATT_RULE_EXT_MARKER = "id"; //$NON-NLS-1$
	final static public String ATT_RULE_EXT_FILTER = "filter"; //$NON-NLS-1$

	private static RulesExtReader rulesExtReader = null;

	private RulesExtReader() {
	}

	public static RulesExtReader getSingleton() {
		if (rulesExtReader == null) {
			rulesExtReader = new RulesExtReader();
		}

		return rulesExtReader;
	}

	// TODO EXCEPTION IS REALLY UGLY DUDE...
	public RulesExtInternal readExtension(IExtension ext) {

		if (!ext.getExtensionPointUniqueIdentifier().equals(Activator.RULES_EXT_ID)) {
			return null;
		}

		RulesExtInternal tgt = new RulesExtInternal();

		IConfigurationElement[] configurationElement = ext.getConfigurationElements();

		for (int j = 0; j < configurationElement.length; ++j) {
			internalReadElement(configurationElement[j], tgt);
		}

		return tgt;
	}

	private void internalReadElement(IConfigurationElement element, RulesExtInternal tgt) {
		boolean recognized = readElement(element, tgt);
		if (recognized) {
			IConfigurationElement[] children = element.getChildren();
			for (int i = 0; i < children.length; ++i) {
				internalReadElement(children[i], tgt);
			}
		} else {
			tgt = null;
		}

		return;
	}

	// TODO Re-organise method's body.
	private boolean readElement(IConfigurationElement element, RulesExtInternal tgt) {
		int r = 0;

		if (element.getName().equals(TAG_RULE_EXT)) {
			if (element.getAttribute(ATT_RULE_EXT_MARKER) != null) {
				tgt.setMarker(element.getAttribute(ATT_RULE_EXT_MARKER));
			} else {
				logMissingAttribute(element, ATT_RULE_EXT_MARKER);
				r++;
			}

			if (element.getAttribute(ATT_RULE_EXT_FILTER) != null) {
				tgt.setFilter(element.getAttribute(ATT_RULE_EXT_FILTER));
			} else {
				logMissingAttribute(element, ATT_RULE_EXT_FILTER);
				r++;
			}

			if (element.getAttribute(ATT_RULE_EXT_NAME) != null) {
				tgt.setModelID(element.getAttribute(ATT_RULE_EXT_NAME));
			} else {
				logMissingAttribute(element, ATT_RULE_EXT_NAME);
				r++;
			}

			if (element.getAttribute(ATT_RULE_EXT_NSURI) != null) {
				tgt.setNsURI(URI.createURI(element.getAttribute(ATT_RULE_EXT_NSURI), true));
			} else {
				logMissingAttribute(element, ATT_RULE_EXT_NSURI);
				r++;
			}

			// Let's check if the namespace is ok
			EPackage ePackage = null;
			if (tgt.getNsURI() != null) {
				try {
					ePackage = EPackage.Registry.INSTANCE.getEPackage(tgt.getNsURI().toString());
				} catch (WrappedException e) {
					String msg = NLS.bind(Messages.errNsURIRootPackageObject, tgt.getNsURI());
					logError(element, new WrappedException(msg, e));
					r++;
				} catch (ExceptionInInitializerError e) {
					String msg = NLS.bind(Messages.errNsURIRootPackageObject, tgt.getNsURI());
					logError(element, new ExceptionInInitializerError(msg));
					r++;
				}
			}

			if (element.getAttribute(ATT_RULE_EXT_MODELCLASS) != null) {
				if (ePackage != null) { // we can try to reach the root object eClass
					String value = element.getAttribute(ATT_RULE_EXT_MODELCLASS);
					String separator = "."; //$NON-NLS-1$
					String eclassifierName = value.contains(separator) ? value.substring(value.lastIndexOf(separator) + 1) : value;

					tgt.setRootModelEClassifierName(eclassifierName);
					tgt.setRootModelObjectName(value);

				}
			} else {
				logMissingAttribute(element, ATT_RULE_EXT_MODELCLASS);
				r++;
			}

		} else {
			tgt = null;
			return false;
		}

		if (r != 0) {
			logError(element, NLS.bind(Messages.errOnExtensionModelNotRegistered, tgt.getModelId()));
			tgt = null;
		}

		return r == 0 ? true : false;

	}

	/**
	 * Logs the error in the desktop log using the provided text and the information in the configuration element.
	 */
	protected void logError(IConfigurationElement element, String text) {

		// IExtension extension = element.getDeclaringExtension();

		// PlatformLogUtil.logAsError(Activator.getDefault(), NLS.bind(Messages.errOnExtensionIntro, new Object[] {
		// extension.getContributor().getName(), extension.getExtensionPointUniqueIdentifier() })
		// + " " + text); //$NON-NLS-1$

		return;
	}

	/**
	 * Logs the error in the desktop log using the provided text and the information in the configuration element.
	 */
	protected void logError(IConfigurationElement element, Exception e) {

		String msg = e.getMessage();

		IExtension extension = element.getDeclaringExtension();
		msg += "\n" //$NON-NLS-1$
				+ NLS.bind(Messages.errOnExtensionIntro, new Object[] { extension.getContributor().getName(),
						extension.getExtensionPointUniqueIdentifier() });

		// PlatformLogUtil.logAsError(Activator.getDefault(), new Exception(msg));

		return;
	}

	/**
	 * Logs the error in the desktop log using the provided text and the information in the configuration element.
	 */
	protected void logError(IConfigurationElement element, ExceptionInInitializerError e) {

		String msg = e.getMessage();

		IExtension extension = element.getDeclaringExtension();
		msg += "\n" //$NON-NLS-1$
				+ NLS.bind(Messages.errOnExtensionIntro, new Object[] { extension.getContributor().getName(),
						extension.getExtensionPointUniqueIdentifier() });

		PlatformLogUtil.logAsError(Activator.getDefault(), new Exception(msg));

		return;
	}

	/**
	 * Logs a very common error when a required attribute is missing.
	 */
	protected void logMissingAttribute(IConfigurationElement element, String attributeName) {

		logError(element, NLS.bind(Messages.errMissingAttributeOnExtensionPoint, attributeName));

		return;
	}

}
