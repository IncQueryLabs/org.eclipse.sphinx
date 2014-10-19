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
package org.eclipse.sphinx.platform.cli;

import org.eclipse.sphinx.platform.internal.messages.Messages;

public interface ICommonCLIConstants {

	/*
	 * Command line syntax without workspace (-data) option.
	 */
	String COMMAND_LINE_SYNTAX_FORMAT_BASIC = "eclipse -noSplash -application %s [options]"; //$NON-NLS-1$

	/*
	 * Command line syntax with workspace (-data) option.
	 */
	String COMMAND_LINE_SYNTAX_FORMAT_WITH_WORKSPACE = "eclipse -noSplash -data <" + Messages.cliOption_workspaceLocation_argName + "> -application %s [" + Messages.cliHelp_options + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/*
	 * Help option.
	 */
	String OPTION_HELP = "help"; //$NON-NLS-1$
	String OPTION_HELP_DESCRIPTION = Messages.cliOption_help;
}
