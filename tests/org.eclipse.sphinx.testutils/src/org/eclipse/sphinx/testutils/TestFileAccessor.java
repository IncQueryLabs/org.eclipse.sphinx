/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys, BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 *     BMW Car IT - Bug 358559, introduced base directory that can be explicitly set
 * 
 * </copyright>
 */
package org.eclipse.sphinx.testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;

@SuppressWarnings("nls")
public class TestFileAccessor {

	public static void copyInputStreamToFile(InputStream in, File targetFile) throws IOException {
		OutputStream out = new FileOutputStream(targetFile);
		try {
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				// Ignore exception
			}
		}
	}

	private static final String INPUT_DIR = "resources" + IPath.SEPARATOR + "input";
	private static final String BUNDLE_RESOURCE_SCHEME = "bundleresource";
	private static final String BUNDLE_ENTRY_SCHEME = "bundleentry";

	private Plugin targetPlugin;
	private File baseDirectory;

	public TestFileAccessor(Plugin targetPlugin, File baseDirectory) {
		Assert.isNotNull(targetPlugin);
		Assert.isNotNull(baseDirectory);
		this.targetPlugin = targetPlugin;
		this.baseDirectory = baseDirectory;

		baseDirectory.mkdirs();
	}

	public TestFileAccessor(Plugin targetPlugin) {
		this(targetPlugin, new File(".")); // use current working directory as default base directory
	}

	public Plugin getTargetPlugin() {
		return targetPlugin;
	}

	public java.net.URI getInputFileURI(String inputFileName) throws URISyntaxException, IOException {
		return getInputFileURI(inputFileName, false);
	}

	public java.net.URI getInputFileURI(String inputFileName, boolean fileScheme) throws URISyntaxException, IOException {
		Path inputFilePath = new Path(INPUT_DIR + IPath.SEPARATOR + inputFileName);
		URL url = FileLocator.find(targetPlugin.getBundle(), inputFilePath, null);
		if (url == null) {
			throw new FileNotFoundException(inputFileName);
		}

		if (fileScheme) {
			url = FileLocator.toFileURL(url);
		}

		String path = url.getPath();
		String os = Platform.getOS();
		if (os.contains("win")) {
			// Replace all white spaces in the path by "%20"
			path = path.replaceAll("\\s", "%20");
		}
		return new URL(url.getProtocol(), null, path).toURI();
	}

	public InputStream openInputFileInputStream(String inputFileName) throws IOException {
		Path inputFilePath = new Path(INPUT_DIR + IPath.SEPARATOR + inputFileName);
		return FileLocator.openStream(targetPlugin.getBundle(), inputFilePath, false);
	}

	public File createWorkingFile(String workingFileName) {
		return new File(baseDirectory, workingFileName);
	}

	public java.net.URI getWorkingFileURI(String workingFileName) {
		return createWorkingFile(workingFileName).toURI();
	}

	public InputStream openWorkingFileInputStream(String workingFileName) throws FileNotFoundException {
		return new FileInputStream(createWorkingFile(workingFileName));
	}

	public OutputStream openWorkingFileOutputStream(String workingFileName, boolean append) throws FileNotFoundException {
		return new FileOutputStream(createWorkingFile(workingFileName), append);
	}

	public File createWorkingCopyOfInputFile(String inputFileName) throws IOException {
		InputStream in = openInputFileInputStream(inputFileName);
		File workingCopyOfInputFile = createWorkingFile(inputFileName);
		copyInputStreamToFile(in, workingCopyOfInputFile);
		return workingCopyOfInputFile;
	}

	public URI convertToEMFURI(java.net.URI uri) {
		if (BUNDLE_RESOURCE_SCHEME.equals(uri.getScheme()) || BUNDLE_ENTRY_SCHEME.equals(uri.getScheme())) {
			return URI.createPlatformPluginURI(targetPlugin + uri.getPath(), true);
		}
		return URI.createURI(uri.toString(), true);
	}
}
