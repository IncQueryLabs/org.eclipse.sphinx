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
package org.eclipse.sphinx.gmfgen.tasks;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.tools.ant.BuildException;
import org.eclipse.gmf.codegen.gmfgen.GenEditorGenerator;

public class GMFGenModelAdjustTask extends GMFTask {

	private String copyrightTextFile;

	private String templateDirectory;

	private boolean dynamicTemplates;

	public String getCopyrightTextFile() {
		return copyrightTextFile;
	}

	public void setCopyrightTextFile(String copyrightTextFile) {
		this.copyrightTextFile = copyrightTextFile;
	}

	public boolean isDynamicTemplates() {
		return dynamicTemplates;
	}

	public void setDynamicTemplates(boolean dynamicTemplates) {
		this.dynamicTemplates = dynamicTemplates;
	}

	public String getTemplateDirectory() {
		return templateDirectory;
	}

	public void setTemplateDirectory(String templateDirectory) {
		this.templateDirectory = templateDirectory;
	}

	@Override
	public void doExecute() throws BuildException {
		System.out.println("Adjusting GenModel..."); //$NON-NLS-1$
		String readTextFileContent = readTextFileContent(getCopyrightTextFile());
		GenEditorGenerator genModel = getGenModel();
		genModel.setCopyrightText(readTextFileContent);
		if (isDynamicTemplates()) {
			if (getTemplateDirectory() != null && getTemplateDirectory().length() > 0) {
				genModel.setDynamicTemplates(isDynamicTemplates());
				genModel.setTemplateDirectory(getTemplateDirectory());
			} else {
				throw new BuildException("Dynamic templates is set to true and no templates directory is given"); //$NON-NLS-1$
			}
		}
	}

	private String readTextFileContent(String path) {
		StringBuilder content = new StringBuilder();
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = input.readLine()) != null) {
				content.append(line);
				content.append(System.getProperty("line.separator")); //$NON-NLS-1$
			}
		} catch (Exception ex) {
			System.err.println("Unable to read content from file " + path + ": " + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception ex) {
				// Do nothing
			}
		}

		return content.toString();
	}
}
