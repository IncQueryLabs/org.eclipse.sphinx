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

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Platform;
import org.eclipse.sphinx.testutils.internal.Activator;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.osgi.framework.Bundle;

@SuppressWarnings({ "nls", "restriction" })
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

				try {
					Class<?> c = loadClass(plugin, classname);

					Test test;

					if (isAbstract(c)) {
						continue;
					} else if (isTestCase(c)) {
						test = new TestSuite(c);
					} else if (isJUnit4Test(c)) {
						test = new JUnit4TestAdapter(c);
					} else {
						continue;
					}

					ts.addTest(test);
					i++;
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

	private static boolean isJUnit4Test(Class<?> c) {
		Runner runner = Request.classWithoutSuiteMethod(c).getRunner();

		if (runner == null) {
			return false;
		}

		if (runner instanceof ErrorReportingRunner) {
			return false;
		}

		if (runner instanceof Parameterized) {
			// only allow the Parameterized suite, all other suites are excluded below
			return true;
		}

		if (runner instanceof Suite) {
			return false;
		}

		return true;
	}

	private static boolean isTestCase(Class<?> c) {
		return TestCase.class.isAssignableFrom(c);
	}

	private static boolean isAbstract(Class<?> c) {
		int modifiers = c.getModifiers();
		return (modifiers & Modifier.ABSTRACT) > 0;
	}

	private static void log(String message) {
		System.out.println("AutoTestSuite: " + message);
	}
}
