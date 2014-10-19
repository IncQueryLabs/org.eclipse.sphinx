/**
 * <copyright>
 *
 * Copyright (c) See4sys and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.mwe.dynamic.headless;

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.BasicWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.platform.cli.AbstractCLIApplication;

/**
 * Usage examples:
 * <ul>
 * <li>eclipse<br/>
 * -noSplash<br/>
 * -data /my/workspace/location<br/>
 * -application org.eclipse.sphinx.emf.mwe.dynamic.headless.WorkflowRunner<br/>
 * -workflow org.example/src/org/example/MyWorkflowFile.xtend<br/>
 * -model org.example/model/MyModelFile.hummingbird</li>
 * <li>eclipse<br/>
 * -noSplash<br/>
 * -data /my/workspace/location<br/>
 * -application org.eclipse.sphinx.emf.mwe.dynamic.headless.WorkflowRunner<br/>
 * -workflow /org.example/src/org/example/MyWorkflowFile.java<br/>
 * -model /org.example/model/MyModelFile.hummingbird</li>
 * <li>eclipse<br/>
 * -noSplash<br/>
 * -data /my/workspace/location<br/>
 * -application org.eclipse.sphinx.emf.mwe.dynamic.headless.WorkflowRunner<br/>
 * -workflow org.example/src/org/example/MyWorkflowFile.xtend<br/>
 * -model platform:/resource/org.example/model/MyModelFile.hummingbird</li>
 * <li>eclipse<br/>
 * -noSplash<br/>
 * -data /my/workspace/location<br/>
 * -application org.eclipse.sphinx.emf.mwe.dynamic.headless.WorkflowRunner<br/>
 * -workflow org.example/src/org/example/MyWorkflowFile.xtend<br/>
 * -model platform:/resource/org.example/model/MyModelFile.hummingbird#//@components.0</li>
 * </ul>
 */
public class BasicWorkflowRunnerApplication extends AbstractCLIApplication {

	private static final Pattern JAVA_CLASS_NAME_PATTERN = Pattern.compile("((?:\\w|\\.)+)(\\.)([A-Z](?:\\w)+)"); //$NON-NLS-1$

	/*
	 * @see org.eclipse.sphinx.platform.cli.AbstractCLIApplication#getCommandLineSyntax()
	 */
	@Override
	protected String getCommandLineSyntax() {
		return IWorkflowRunnerCLIConstants.COMMAND_LINE_SYNTAX;
	}

	// TODO Externalize exceptions

	/*
	 * @see org.eclipse.sphinx.platform.cli.AbstractCLIApplication#defineOptions()
	 */
	@Override
	protected void defineOptions() {
		super.defineOptions();

		OptionBuilder.hasArg();
		OptionBuilder.withArgName(IWorkflowRunnerCLIConstants.OPTION_WORKFLOW_ARG_NAME);
		OptionBuilder.withDescription(IWorkflowRunnerCLIConstants.OPTION_WORKFLOW_DESCRIPTION);
		addOption(OptionBuilder.create(IWorkflowRunnerCLIConstants.OPTION_WORKFLOW));

		OptionBuilder.hasArg();
		OptionBuilder.withArgName(IWorkflowRunnerCLIConstants.OPTION_MODEL_ARG_NAME);
		OptionBuilder.withDescription(IWorkflowRunnerCLIConstants.OPTION_MODEL_DESCRIPTION);
		addOption(OptionBuilder.create(IWorkflowRunnerCLIConstants.OPTION_MODEL));
	}

	/*
	 * @see org.eclipse.sphinx.platform.cli.AbstractCLIApplication#interrogate()
	 */
	@Override
	protected Object interrogate() throws Throwable {
		super.interrogate();

		// Retrieve options
		CommandLine commandLine = getCommandLine();
		String workflow = commandLine.getOptionValue(IWorkflowRunnerCLIConstants.OPTION_WORKFLOW);
		String model = commandLine.getOptionValue(IWorkflowRunnerCLIConstants.OPTION_MODEL);

		// Create the workflow operation
		IWorkflowRunnerOperation operation = createWorkflowRunnerOperation();
		operation.setWorkflow(getWorkflow(workflow));
		operation.setModel(loadModel(model));

		// Run the operation
		operation.run(new NullProgressMonitor());

		// TODO Save model if workflow has model modifying components

		return ERROR_NO;
	}

	protected BasicWorkflowRunnerOperation createWorkflowRunnerOperation() {
		return new BasicWorkflowRunnerOperation(IWorkflowRunnerCLIConstants.APPLICATION_NAME);
	}

	protected Object getWorkflow(String workflow) throws ClassNotFoundException, FileNotFoundException {
		// FIXME Make workflow a required at CLI option
		Assert.isNotNull(workflow);

		Matcher matcher = JAVA_CLASS_NAME_PATTERN.matcher(workflow);
		if (matcher.find()) {
			// Workflow is a fully qualified Java class name
			try {
				return Class.forName(workflow);
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Workflow class '" + workflow + "' could not be found");
			}
		} else {
			// Workflow is assumed to be a workspace-relative path
			Path workflowPath = new Path(workflow);
			IFile workflowFile = ResourcesPlugin.getWorkspace().getRoot().getFile(workflowPath);
			if (!workflowFile.exists()) {
				IPath workflowLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(workflowPath);
				throw new FileNotFoundException("Workflow file '" + workflowLocation.toOSString() + "' does not exist");
			}
			return workflowFile;
		}
	}

	protected Object loadModel(String model) throws FileNotFoundException, NoSuchElementException {
		// FIXME Support launching workflows without providing any model
		Assert.isNotNull(model);

		URI uri = EcorePlatformUtil.createURI(new Path(model).makeAbsolute()).trimFragment();
		if (!EcoreResourceUtil.exists(uri)) {
			throw new FileNotFoundException("Model resource '" + uri.toPlatformString(true) + "' does not exist");
		}

		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(uri);
		ResourceSet resourceSet = editingDomain != null ? editingDomain.getResourceSet() : null;

		String fragment = URI.createURI(model).fragment();
		if (fragment != null) {
			// URI refers to a model object
			EObject eObject = EcoreResourceUtil.loadEObject(resourceSet, uri.appendFragment(fragment));
			if (eObject == null) {
				throw new NoSuchElementException("Model resource '" + uri.toPlatformString(true) + "' contains no '" + fragment + "' element");
			}
			return eObject;
		} else {
			// URI refers to a model resource
			Resource resource = EcoreResourceUtil.loadResource(resourceSet, uri, null);
			if (resource == null) {
				throw new NoSuchElementException("Model resource '" + uri.toPlatformString(true) + "' could not be loaded");
			}
			if (resource.getContents().isEmpty()) {
				throw new NoSuchElementException("Model resource '" + uri.toPlatformString(true) + "' has no content");
			}

			return resource.getContents().get(0);
		}
	}
}