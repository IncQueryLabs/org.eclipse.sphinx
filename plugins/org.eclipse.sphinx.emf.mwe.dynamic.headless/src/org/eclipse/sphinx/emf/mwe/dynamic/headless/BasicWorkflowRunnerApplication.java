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
 *     itemis - [463978] org.eclipse.sphinx.emf.mwe.dynamic.headless.BasicWorkflowRunnerApplication.interrogate() handling of null URI
 *     itemis - [472576] Headless workflow runner application unable to resolve model element URIs
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.mwe.dynamic.headless;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.mwe.dynamic.headless.internal.messages.Messages;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.BasicWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.mwe.dynamic.operations.IWorkflowRunnerOperation;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.util.EcoreResourceUtil;
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

		addOption(new Option(IWorkflowRunnerCLIConstants.OPTION_SKIP_SAVE, IWorkflowRunnerCLIConstants.OPTION_SKIP_SAVE_DESCRIPTION));
	}

	/*
	 * @see org.eclipse.sphinx.platform.cli.AbstractCLIApplication#interrogate()
	 */
	@Override
	protected Object interrogate() throws Throwable {
		super.interrogate();

		// Retrieve options
		CommandLine commandLine = getCommandLine();
		String workflowOptionValue = commandLine.getOptionValue(IWorkflowRunnerCLIConstants.OPTION_WORKFLOW);
		String modelOptionValue = commandLine.getOptionValue(IWorkflowRunnerCLIConstants.OPTION_MODEL);
		boolean skipSaveOption = commandLine.hasOption(IWorkflowRunnerCLIConstants.OPTION_SKIP_SAVE);

		final URI modelURI = getModelURI(modelOptionValue);
		if (modelURI != null) {
			// Check if model resource exists
			URI uri = modelURI.trimFragment();
			if (!EcoreResourceUtil.exists(uri)) {
				throw new FileNotFoundException(NLS.bind(Messages.cliError_modelResourceDoesNotExist, uri.toPlatformString(true)));
			}
		}

		// Create the workflow operation
		Object workflow = getWorkflow(workflowOptionValue);
		IWorkflowRunnerOperation operation = createWorkflowRunnerOperation(workflow);
		if (modelURI != null) {
			operation.getModelURIs().add(modelURI);
		}
		operation.setAutoSave(!skipSaveOption);

		// Run workflow operation
		operation.run(createProgressMonitor());

		return ERROR_NO;
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

	protected URI getModelURI(String modelOptionValue) {
		if (modelOptionValue != null) {
			URI modelURI = URI.createURI(modelOptionValue);
			Path modelFilePath = new Path(modelURI.trimFragment().toString());
			URI absoluteModelFileURI = EcorePlatformUtil.createURI(modelFilePath.makeAbsolute());
			return absoluteModelFileURI.appendFragment(modelURI.fragment());
		}
		return null;
	}

	protected BasicWorkflowRunnerOperation createWorkflowRunnerOperation(Object workflow) {
		return new BasicWorkflowRunnerOperation(IWorkflowRunnerCLIConstants.APPLICATION_NAME, workflow);
	}

	protected IProgressMonitor createProgressMonitor() {
		return new NullProgressMonitor();
	}
}