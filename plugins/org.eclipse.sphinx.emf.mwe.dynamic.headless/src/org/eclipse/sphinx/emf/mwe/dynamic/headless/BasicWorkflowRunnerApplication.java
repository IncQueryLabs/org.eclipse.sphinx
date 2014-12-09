/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [454532] NPE in BasicWorkflowRunnerApplication
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.mwe.dynamic.headless;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.mwe.dynamic.headless.internal.messages.Messages;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.BasicWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.saving.SaveIndicatorUtil;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.platform.cli.AbstractCLIApplication;
import org.eclipse.sphinx.platform.cli.ICommonCLIConstants;

/**
 * Usage examples:
 * <ul>
 * <li>eclipse<br/>
 * -noSplash<br/>
 * -data /my/workspace/location<br/>
 * -application org.eclipse.sphinx.emf.mwe.dynamic.headless.WorkflowRunner<br/>
 * -workflow org.example/src/org/example/MyWorkflowFile.java<br/>
 * -model org.example/model/MyModelFile.hummingbird</li>
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
 * -workflow org.example/src/org/example/MyWorkflowFile.xtend<br/>
 * -model platform:/resource/org.example/model/MyModelFile.hummingbird</li>
 * <li>eclipse<br/>
 * -noSplash<br/>
 * -data /my/workspace/location<br/>
 * -application org.eclipse.sphinx.emf.mwe.dynamic.headless.WorkflowRunner<br/>
 * -workflow org.example/src/org/example/MyWorkflowFile.xtend<br/>
 * -model file:/my/workspace/location/org.example/model/MyModelFile.hummingbird</li>
 * <li>eclipse<br/>
 * -noSplash<br/>
 * -data /my/workspace/location<br/>
 * -application org.eclipse.sphinx.emf.mwe.dynamic.headless.WorkflowRunner<br/>
 * -workflow org.example/src/org/example/MyWorkflowFile.xtend<br/>
 * -model platform:/resource/org.example/model/MyModelFile.hummingbird#//@components.0</li>
 * <li>eclipse<br/>
 * -noSplash<br/>
 * -data /my/workspace/location<br/>
 * -application org.eclipse.sphinx.emf.mwe.dynamic.headless.WorkflowRunner<br/>
 * -workflow org.example/src/org/example/MyWorkflowFile.xtend</li>
 * </ul>
 */
public class BasicWorkflowRunnerApplication extends AbstractCLIApplication {

	private static final Pattern JAVA_CLASS_NAME_PATTERN = Pattern.compile("((?:\\w|\\.)+)(\\.)([A-Z](?:\\w)+)"); //$NON-NLS-1$

	protected Resource modelResource = null;

	/*
	 * @see org.eclipse.sphinx.platform.cli.AbstractCLIApplication#getCommandLineSyntax()
	 */
	@Override
	protected String getCommandLineSyntax() {
		return String.format(ICommonCLIConstants.COMMAND_LINE_SYNTAX_FORMAT_WITH_WORKSPACE, IWorkflowRunnerCLIConstants.APPLICATION_NAME);
	}

	/*
	 * @see org.eclipse.sphinx.platform.cli.AbstractCLIApplication#defineOptions()
	 */
	@Override
	protected void defineOptions() {
		super.defineOptions();

		OptionBuilder.isRequired();
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

		SubMonitor progress = SubMonitor.convert(createProgressMonitor(), 100);

		// Retrieve options
		CommandLine commandLine = getCommandLine();
		String workflowOptionValue = commandLine.getOptionValue(IWorkflowRunnerCLIConstants.OPTION_WORKFLOW);
		String modelOptionValue = commandLine.getOptionValue(IWorkflowRunnerCLIConstants.OPTION_MODEL);

		// Load model
		Object model = loadModel(modelOptionValue, progress.newChild(5));

		// Create the workflow operation
		IWorkflowRunnerOperation operation = createWorkflowRunnerOperation();
		operation.setWorkflow(getWorkflow(workflowOptionValue));
		operation.setModel(model);

		// Run workflow operation
		operation.run(progress.newChild(90));

		// Save model if needed
		saveModel(progress.newChild(5));

		return ERROR_NO;
	}

	protected IProgressMonitor createProgressMonitor() {
		return new NullProgressMonitor();
	}

	protected BasicWorkflowRunnerOperation createWorkflowRunnerOperation() {
		return new BasicWorkflowRunnerOperation(IWorkflowRunnerCLIConstants.APPLICATION_NAME);
	}

	protected Object getWorkflow(String workflowOptionValue) throws ClassNotFoundException, FileNotFoundException {
		Assert.isNotNull(workflowOptionValue);

		Matcher matcher = JAVA_CLASS_NAME_PATTERN.matcher(workflowOptionValue);
		if (matcher.find()) {
			// Workflow is a fully qualified Java class name
			try {
				return Class.forName(workflowOptionValue);
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException(NLS.bind(Messages.cliError_workflowClassNotFound, workflowOptionValue));
			}
		} else {
			// Workflow is assumed to be a workspace-relative path
			Path workflowPath = new Path(workflowOptionValue);
			IFile workflowFile = ResourcesPlugin.getWorkspace().getRoot().getFile(workflowPath);
			if (!workflowFile.exists()) {
				IPath workflowLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(workflowPath);
				throw new FileNotFoundException(NLS.bind(Messages.cliError_workflowFileDoesNotExist, workflowLocation.toOSString()));
			}
			return workflowFile;
		}
	}

	protected Object loadModel(String modelOptionValue, IProgressMonitor monitor) throws FileNotFoundException, NoSuchElementException {
		if (modelOptionValue == null) {
			return null;
		}

		URI uri = EcorePlatformUtil.createURI(new Path(modelOptionValue).makeAbsolute()).trimFragment();
		if (!EcoreResourceUtil.exists(uri)) {
			throw new FileNotFoundException(NLS.bind(Messages.cliError_modelResourceDoesNotExist, uri.toPlatformString(true)));
		}

		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(uri);
		ResourceSet resourceSet = editingDomain != null ? editingDomain.getResourceSet() : null;

		String fragment = URI.createURI(modelOptionValue).fragment();
		if (fragment != null) {
			// URI refers to a model object
			EObject eObject = EcoreResourceUtil.loadEObject(resourceSet, uri.appendFragment(fragment));
			if (eObject == null) {
				throw new NoSuchElementException(NLS.bind(Messages.cliError_modelResourceContainsNoMatchingElement, uri.toPlatformString(true),
						fragment));
			}
			modelResource = eObject.eResource();
			return Collections.singletonList(eObject);
		} else {
			// URI refers to a model resource
			modelResource = EcoreResourceUtil.loadResource(resourceSet, uri, null);
			if (modelResource == null) {
				throw new NoSuchElementException(NLS.bind(Messages.cliError_modelResourceCouldNotBeLoaded, uri.toPlatformString(true)));
			}
			if (modelResource.getContents().isEmpty()) {
				throw new NoSuchElementException(NLS.bind(Messages.cliError_modelResourceHasNoContent, uri.toPlatformString(true)));
			}

			return modelResource.getContents();
		}
	}

	protected void saveModel(IProgressMonitor monitor) {
		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(modelResource);
		if (SaveIndicatorUtil.isDirty(editingDomain, modelResource)) {
			EcorePlatformUtil.saveModel(modelResource, false, monitor);
		}
	}
}