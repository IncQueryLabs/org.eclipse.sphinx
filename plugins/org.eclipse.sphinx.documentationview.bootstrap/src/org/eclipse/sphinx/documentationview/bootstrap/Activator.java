/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.documentationview.bootstrap;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * General Activator to return all the files required by others to the views.
 * 
 * Uses the Bootstrap and JQuery libraries as approved by Eclipse IP process.
 * 
 * @author graf
 *
 */
public class Activator implements BundleActivator {

	private static final String JQUERY_DIR = "jquery";
	private static final String BOOTSTRAP_DIR = "dist";
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	public static String bootstrapDir() {
		Bundle bundle = context.getBundle();
		
		  Path path = new Path(BOOTSTRAP_DIR);
		  URL url = FileLocator.findEntries(bundle, path)[0];
		  URI uri = null;
		try {
			uri = FileLocator.toFileURL(url).toURI();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  File file = new File(uri);
		  return file.getAbsolutePath();
		  
	}
	
	public static String jqueryDir() {
		Bundle bundle = context.getBundle();
		
		  Path path = new Path(JQUERY_DIR);
		  URL url = FileLocator.findEntries(bundle, path)[0];
		  URI uri = null;
		try {
			uri = FileLocator.toFileURL(url).toURI();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  File file = new File(uri);
		  return file.getAbsolutePath();
		  
	}

}
