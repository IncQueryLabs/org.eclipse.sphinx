/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 BMW Car IT and others.
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

import java.io.BufferedReader;
import java.io.FileReader;
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
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.osgi.framework.Bundle;

@SuppressWarnings({ "nls", "restriction" })
public class AutoTestSuite {

	public static Test suite() {
		// 1: look first for a regexp pattern
		String property = System.getProperty("autotestsuite.plugin.pattern");
		if (property != null) {
			TestSuite testSuite = new TestSuite(property);

			Bundle[] bs = Activator.getPlugin().getBundle().getBundleContext().getBundles();
			for (Bundle b : bs) {
				if (b.getSymbolicName().matches(property)) {
					TestSuite ts = new TestSuite(b.getSymbolicName());

					setup(ts, b);

					testSuite.addTest(ts);
				}
			}

			return testSuite;
		}

		// 1: look for a single plugin
		property = System.getProperty("autotestsuite.plugin");
		if (property != null) {

			TestSuite ts = new TestSuite(property);
			setup(ts, Platform.getBundle(property));
			return ts;
		}

		property = System.getProperty("autotestsuite.plugins");
		if (property != null) {
			if (property.startsWith("@")) {
				try {
					TestSuite testSuite = new TestSuite("Running tests from " + property);
					BufferedReader reader = new BufferedReader(new FileReader(property.substring(1)));
					try {
						String line = reader.readLine();
						while (line != null) {
							line = line.trim();
							if (line.length() > 0 && line.charAt(0) != '#') {
								// process this line
								TestSuite ts = new TestSuite(line);
								setup(ts, Platform.getBundle(line));
								testSuite.addTest(ts);
							}
							line = reader.readLine();
						}
					} finally {
						reader.close();
					}
					return testSuite;
				} catch (Exception ex) {
					throw new AssertionError(ex);
				}
			} else {
				String[] plugins = property.split(",");
				TestSuite testSuite = new TestSuite("Running tests from " + plugins.length + " plugins");
				for (String pluginName : plugins) {
					TestSuite ts = new TestSuite(pluginName);

					setup(ts, Platform.getBundle(pluginName));

					testSuite.addTest(ts);
				}
				return testSuite;
			}
		}

		throw new AssertionError(
				"Exactly one of the system properties 'autotestsuite.plugin.pattern', 'autotestsuite.plugin' or 'autotestsuite.plugins' need to be set");
	}

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
					} else if (isJUnit4Test(c)) {
						test = new JUnit4TestAdapter(c);
					} else if (isTestCase(c)) {
						test = new TestSuite(c);
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

		if (runner instanceof JUnit38ClassRunner) {
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
