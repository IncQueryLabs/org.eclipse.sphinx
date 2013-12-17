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
package org.eclipse.sphinx.platform.ui.util;

import java.io.PrintStream;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.platform.messages.PlatformMessages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ISetSelectionTarget;

/**
 * 
 */
public final class ExtendedPlatformUI {

	public static final String SYSTEM_CONSOLE_NAME = "System Console"; //$NON-NLS-1$

	// Prevent from instantiation
	private ExtendedPlatformUI() {
	}

	public static Shell getActiveShell() {
		Display display = getDisplay();
		return display != null ? display.getActiveShell() : null;
	}

	/**
	 * @return The display from which active shell can be retrieved.
	 */
	public static Display getDisplay() {
		if (PlatformUI.isWorkbenchRunning()) {
			Display display = PlatformUI.getWorkbench().getDisplay();
			if (!display.isDisposed()) {
				return display;
			}
		}
		return null;
	}

	/**
	 * @return The active workbench page or <code>null</code> if no active workbench page can be determined
	 */
	public static IWorkbenchPage getActivePage() {
		final IWorkbenchPage[] page = new IWorkbenchPage[1];
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null) {
					page[0] = window.getActivePage();
				}
			}
		});
		return page[0];
	}

	/**
	 * A convenience method to write on a Sphinx console.
	 * 
	 * @return The message console stream that can be used for printing.
	 */
	public static MessageConsoleStream out() {
		// TODO Created the dedicated message and use NLS
		MessageConsole myConsole = findConsole("Extended Console"); //$NON-NLS-1$
		MessageConsoleStream out = myConsole.newMessageStream();

		IWorkbenchPage page = getActivePage();
		if (page == null) {
			throw new NullPointerException(NLS.bind(PlatformMessages.error_mustNotBeNull, "page")); //$NON-NLS-1$
		}
		String id = IConsoleConstants.ID_CONSOLE_VIEW;
		IConsoleView view;
		try {
			view = (IConsoleView) page.showView(id);
			view.display(myConsole);
		} catch (PartInitException ex) {
			// FIXME Should exception be logged?
		}
		return out;
	}

	private static MessageConsole findConsole(String consoleName) {
		Assert.isNotNull(consoleName);
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = plugin.getConsoleManager();
		IConsole[] consoles = consoleManager.getConsoles();
		for (IConsole element : consoles) {
			if (consoleName.equals(element.getName())) {
				return (MessageConsole) element;
			}
		}
		// No console found, create a new one
		MessageConsole console = new MessageConsole(consoleName, null);
		consoleManager.addConsoles(new IConsole[] { console });
		return console;
	}

	/**
	 * @param objectName
	 *            The name of the object for which deletion must be confirmed.
	 * @return <ul>
	 *         <li><tt><b>true</b>&nbsp;&nbsp;</tt> if the concerned object can be deleted;</li>
	 *         <li><tt><b>false</b>&nbsp;</tt> otherwise.</li>
	 *         </ul>
	 */
	public static boolean openConfirmDeleteDialog(String objectName) {
		// TODO Created the dedicated message and use NLS
		String title = "Confirm Delete";
		String msg = "Are you sure you want to delete object \"{0}\"?";
		String message = NLS.bind(msg, objectName);
		return openQuestionDialog(title, message);
	}

	public static boolean openConfirmResetSelectionDialog() {
		// TODO Created the dedicated message and use NLS
		String title = "Update Selection";
		String msg = "Selection changed and is now empty. Are you sure you want reset selection? (if no, previous selection will be restored";
		return openQuestionDialog(title, msg);
	}

	/**
	 * @param title
	 *            The title of the 'confirm delete' dialog.
	 * @param message
	 *            The message to display in the 'confirm delete' dialog.
	 * @return <ul>
	 *         <li><tt><b>true</b>&nbsp;&nbsp;</tt> if the concerned object can be deleted;</li>
	 *         <li><tt><b>false</b>&nbsp;</tt> otherwise.</li>
	 *         </ul>
	 */
	public static boolean openQuestionDialog(String title, String message) {
		return MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
	}

	public static void showObjectsInActiveView(Collection<?> objects) {
		showObjectsInView(objects, null);
	}

	public static void showObjectsInView(Collection<?> objects, String viewId) {
		if (objects != null && objects.size() > 0) {
			IWorkbenchPage page = getActivePage();
			if (page != null) {
				if (viewId == null) {
					IWorkbenchPart activePart = page.getActivePart();
					if (activePart instanceof IViewPart && activePart.getSite() != null) {
						viewId = activePart.getSite().getId();
					}
				}
				if (viewId != null) {
					try {
						IViewPart view = page.showView(viewId);
						if (view instanceof ISetSelectionTarget) {
							try {
								ISelection selection = new StructuredSelection(objects.toArray());
								((ISetSelectionTarget) view).selectReveal(selection);
							} catch (RuntimeException ex) {
								// Ignore exception
							}
						}
					} catch (PartInitException ex) {
						// Ignore exception, be fail-silent
					}
				}
			}
		}
	}

	public static void showSystemConsole() {
		// Try to retrieve already existing system console
		MessageConsole systemConsole = null;
		for (IConsole console : ConsolePlugin.getDefault().getConsoleManager().getConsoles()) {
			if (SYSTEM_CONSOLE_NAME.equals(console.getName())) {
				systemConsole = (MessageConsole) console;
				break;
			}
		}
		if (systemConsole == null) {
			// Create new system console
			systemConsole = new MessageConsole(SYSTEM_CONSOLE_NAME, null);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { systemConsole });

			// Redirect system output and error streams to system console
			MessageConsoleStream systemOutStream = systemConsole.newMessageStream();
			MessageConsoleStream systemErrStream = systemConsole.newMessageStream();
			systemErrStream.setColor(new Color(PlatformUI.getWorkbench().getDisplay(), 255, 0, 0));
			System.setOut(new PrintStream(systemOutStream));
			System.setErr(new PrintStream(systemErrStream));
		}
		// Show system console
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(systemConsole);
	}
}
