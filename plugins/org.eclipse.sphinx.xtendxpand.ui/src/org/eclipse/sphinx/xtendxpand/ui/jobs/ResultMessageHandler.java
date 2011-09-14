/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.xtendxpand.ui.jobs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.sphinx.xtendxpand.jobs.CheckJob;
import org.eclipse.sphinx.xtendxpand.jobs.M2MJob;
import org.eclipse.sphinx.xtendxpand.jobs.M2TJob;
import org.eclipse.sphinx.xtendxpand.jobs.XpandJob;
import org.eclipse.sphinx.xtendxpand.jobs.XtendJob;
import org.eclipse.sphinx.xtendxpand.ui.internal.messages.Messages;
import org.eclipse.swt.widgets.Display;

/**
 * Implements handlers that can be registered as {@link IJobChangeListener} on a {@link M2TJob} instance or a
 * {@link M2MJob} instance that encloses the latter and open a message dialog indicating the status result of M2x job.
 * 
 * @see {@link M2TJob} class.
 * @see {@link M2MJob} class.
 */
public class ResultMessageHandler extends JobChangeAdapter {

	/**
	 * The {@link M2TJob} job instance or {@link M2MJob} job instance to be use for displaying message dialog.
	 */
	protected Job m2xJob = null;

	/**
	 * This bit field indicate when the message is to be displayed (on completion only, or on cancellation only or on
	 * completion and cancellation, etc.).
	 * 
	 * @see {@link ResultMessageConstants} interface.
	 */
	private int openDialogOn;

	/**
	 * This construct a result message dialog handler that opens a message dialog indicating the status result of M2x
	 * job, if failed.
	 * 
	 * @param m2xJob
	 *            the M2TJob or M2MJob instance to be use.
	 */
	public ResultMessageHandler(Job m2xJob) {
		this(m2xJob, ResultMessageConstants.OPEN_DIALOG_ON_FAILED);
	}

	/**
	 * This construct a result message dialog handler that opens a message dialog indicating the status result of M2x
	 * job.
	 * 
	 * @param m2xJob
	 *            the M2TJob or M2MJob instance to be use.
	 * @param openDialogOn
	 *            a bit indicating when the message is to be displayed (on completion only, or on cancellation only or
	 *            on completion and cancellation, etc.).
	 */
	public ResultMessageHandler(Job m2xJob, int openDialogOn) {
		Assert.isTrue(m2xJob instanceof M2TJob || m2xJob instanceof M2MJob);

		this.m2xJob = m2xJob;
		this.openDialogOn = openDialogOn;
	}

	/**
	 * Returns used CheckJob into M2x job.
	 */
	protected CheckJob getCheckJob() {
		if (m2xJob != null) {
			if (m2xJob instanceof M2TJob) {
				// Gets associated CheckJob
				return ((M2TJob) m2xJob).getCheckJob();
			}
			if (m2xJob instanceof M2MJob) {
				// Gets associated CheckJob
				return ((M2MJob) m2xJob).getCheckJob();
			}
		}
		return null;
	}

	/**
	 * Returns used XpandJob into M2x job.
	 */
	protected XpandJob getXpandJob() {
		if (m2xJob != null && m2xJob instanceof M2TJob) {
			// Gets associated XpandJob
			return ((M2TJob) m2xJob).getXpandJob();
		}
		return null;
	}

	/**
	 * Returns used XtendJob into M2x job.
	 */
	protected XtendJob getXtendJob() {
		if (m2xJob != null && m2xJob instanceof M2MJob) {
			// Gets associated XtendJob
			return ((M2MJob) m2xJob).getXtendJob();
		}
		return null;
	}

	@Override
	public void done(IJobChangeEvent event) {
		// Opens message dialog that inform the status of M2x job result
		handleResultMessage();
	}

	/**
	 * Opens a message dialog that inform the status of M2x job result.
	 */
	protected void handleResultMessage() {
		final Display display = ExtendedPlatformUI.getDisplay();
		if (display != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					if (m2xJob != null) {
						CheckJob checkJob = getCheckJob();
						MessageDialog dialog;
						String prefix = m2xJob instanceof M2TJob ? Messages.title_codeGen : Messages.title_modelTransformation;
						// If check enabled and check job ends with errors then open a dialog message with check failed
						// before.
						if (checkJob != null && checkJob.getResult() != null && checkJob.getResult().getSeverity() == IStatus.ERROR) {
							dialog = new MessageDialog(ExtendedPlatformUI.getActiveShell(), prefix, null, NLS.bind(Messages.msg_M2x_Check_failed,
									prefix), MessageDialog.ERROR, new String[] { IDialogConstants.OK_LABEL }, 0);
							dialog.open();
							return;
						}

						/*
						 * !!! Important: not that Eclipse automatically opens a message dialog with a status message as
						 * soon as we return an error status. In this case, we do not open explicitly a message dialog
						 * when M2x job ends with errors.
						 */
						IStatus m2xResult = m2xJob.getResult();
						String message = ""; //$NON-NLS-1$
						int imageType = -1;
						switch (openDialogOn) {
						// The case when the message is to be displayed on completion or failed only
						case ResultMessageConstants.OPEN_DIALOG_ON_FAILED_OR_COMPLETION:
							if (m2xResult.getSeverity() == IStatus.OK) {
								message = NLS.bind(Messages.msg_M2x_successfull, prefix);
								imageType = MessageDialog.INFORMATION;
							}
							break;

						// The case when the message is to be displayed on failed or completion or cancellation
						case ResultMessageConstants.OPEN_DIALOG_ON_FAILED_OR_COMPLETION_OR_CANCELLATION:
							if (m2xResult != null && m2xResult.getSeverity() == IStatus.OK) {
								message = NLS.bind(Messages.msg_M2x_successfull, prefix);
								imageType = MessageDialog.INFORMATION;
							} else if (m2xResult != null && m2xResult.getSeverity() == IStatus.CANCEL) {
								message = NLS.bind(Messages.msg_M2x_cancelled, prefix);
								imageType = Window.CANCEL;
							}
							break;

						default:
							// The default case open message dialog if M2x job failed or if check done before M2x ends
							// with errors.
							break;
						}
						// If dialog message is required then open it
						if (!message.equals("") && imageType != -1) { //$NON-NLS-1$
							dialog = new MessageDialog(ExtendedPlatformUI.getActiveShell(), prefix, null, message, imageType,
									new String[] { IDialogConstants.OK_LABEL }, 0);
							dialog.open();
						}
					}
				}
			});
		}
	}
}
