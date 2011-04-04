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
package org.eclipse.sphinx.platform.ui.groups;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractGroup implements IGroup {

	/**
	 * The name of this group.
	 */
	protected String groupName;

	/**
	 * The dialog settings for this group; <code>null</code> if none.
	 */
	private IDialogSettings dialogSettings = null;

	/**
	 * This construct a group with <code>groupName</code> group name.
	 * 
	 * @param groupName
	 *            the name of the group.
	 */
	public AbstractGroup(String groupName) {
		this(groupName, null);
	}

	/**
	 * This construct a group with <code>groupName</code> group name.
	 * 
	 * @param groupName
	 *            the name of the group.
	 * @param dialogSettings
	 *            the dialog settings for this group or null.
	 */
	public AbstractGroup(String groupName, IDialogSettings dialogSettings) {
		this.groupName = groupName;
		this.dialogSettings = dialogSettings;
	}

	public void createContent(Composite parent, int numColumns) {
		doCreateContent(parent, numColumns);
	}

	/**
	 * Creates the content i.e., all required fields of the group.
	 */
	protected abstract void doCreateContent(Composite parent, int numColumns);

	/**
	 * Sent when the content of a group field has changed. This method should be overriding for instance by wizards that
	 * contain the output group field for example to adjust the enable state of the Back, Next, and Finish buttons
	 * wizard page.
	 */
	protected void groupFieldChanged(IField field) {
		// Do nothing by default.
	}

	/**
	 * Loads the dialog settings of this group.
	 */
	protected void loadGroupSettings() {
		// Do nothing by default.
	}

	/**
	 * Saves the dialog settings of this group.
	 */
	public void saveGroupSettings() {
		// Do nothing by default.
	}

	/**
	 * Returns true or false if the group is complete or not.
	 */
	public boolean isGroupComplete() {
		return true;
	}

	/**
	 * Sets the dialog settings for this group.
	 * <p>
	 * The dialog settings is used to record state between group invocations (i.e. template path and the selected define
	 * block inside this template)
	 * </p>
	 * 
	 * @param settings
	 *            the dialog settings, or <code>null</code> if none
	 * @see #getDialogSettings
	 */
	public void setDialogSettings(IDialogSettings dialogSettings) {
		this.dialogSettings = dialogSettings;
	}

	/**
	 * Returns the dialog settings for this group.
	 * 
	 * @return the dialog settings, or <code>null</code> if none
	 */
	protected IDialogSettings getDialogSettings() {
		return dialogSettings;
	}
}
