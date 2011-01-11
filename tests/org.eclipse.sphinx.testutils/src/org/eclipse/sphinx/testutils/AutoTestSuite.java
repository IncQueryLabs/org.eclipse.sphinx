/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.testutils;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Platform;
import org.eclipse.sphinx.testutils.internal.Activator;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

@SuppressWarnings({ "nls" })
public class AutoTestSuite {

	public static Test suite() {
		String pluginPattern = System.getProperty("autotestsuite.plugin.pattern");
		if (pluginPattern != null) {
			TestSuite testSuite = new TestSuite(pluginPattern);

			Bundle[] bs = Activator.getPlugin().getBundle().getBundleContext().getBundles();
			for (Bundle b : bs) {
				if (b.getSymbolicName().matches(pluginPattern)) {
					TestSuite ts = new TestSuite(b.getSymbolicName());

					setup(ts, b);

					testSuite.addTest(ts);
				}
			}

			return testSuite;
		} else {
			String pluginName = System.getProperty("autotestsuite.plugin");

			Bundle plugin = Platform.getBundle(pluginName);

			TestSuite ts = new TestSuite(pluginName);

			setup(ts, plugin);

			return ts;
		}
	}

	@SuppressWarnings("unchecked")
	private static void setup(TestSuite ts, Bundle plugin) {
		Enumeration<URL> entries = plugin.findEntries("/", "*.class", true);

		int i = 0;

		if (entries != null) {
			for (URL url : Collections.list(entries)) {
				String path = url.getPath();
				path = path.replaceAll("^/(bin/)?(.*)\\.class$", "$2");
				path = path.replaceAll("/", ".");

				String classname = path;

				if (isUiTest(classname) && isWorkbenchRunning() == false) {
					continue;
				}

				try {
					Class<?> c = loadClass(plugin, classname);
					if (isInstantiableTest(c)) {
						ts.addTestSuite((Class<? extends TestCase>) c);
						i++;
					}
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}

		log("Found " + i + " test classes in '" + plugin.getSymbolicName() + "'.");
	}

	private static Class<?> loadClass(Bundle b, String classname) throws ClassNotFoundException {
		if (Platform.isFragment(b)) {
			return Platform.getHosts(b)[0].loadClass(classname);
		} else {
			return b.loadClass(classname);
		}
	}

	private static boolean isUiTest(String classname) {
		return classname.contains(".ui.");
	}

	private static boolean isWorkbenchRunning() {
		return PlatformUI.isWorkbenchRunning();
	}

	private static boolean isInstantiableTest(Class<?> testCandidate) {
		return isNotAbstract(testCandidate) && isNoInterface(testCandidate) && isTestCase(testCandidate);
	}

	private static boolean isTestCase(Class<?> testCandidate) {
		return TestCase.class.isAssignableFrom(testCandidate);
	}

	private static boolean isNotAbstract(Class<?> testCandidate) {
		int modifiers = testCandidate.getModifiers();
		return !((modifiers & Modifier.ABSTRACT) > 0);
	}

	private static boolean isNoInterface(Class<?> testCandidate) {
		int modifiers = testCandidate.getModifiers();
		return !((modifiers & Modifier.INTERFACE) > 0);
	}

	private static void log(String message) {
		System.out.println("AutoTestSuite: " + message);
	}
}
