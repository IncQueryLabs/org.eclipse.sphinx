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

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.sphinx.platform.ui.fields.IField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

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
	 * The listeners of this group. These listeners are notified when group changes append.
	 * <p>
	 * Listeners added to this list must be instances of {@link IGroupListener}.
	 */
	private ListenerList groupListeners = new ListenerList();

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

	@Override
	public void createContent(Composite parent, int numColumns) {
		createContent(parent, numColumns, false);
	}

	@Override
	public void createContent(Composite parent, int numColumns, boolean suppressGroupFrame) {
		Composite parentComposite;
		if (suppressGroupFrame) {
			parentComposite = new Composite(parent, SWT.SHADOW_NONE);
		} else {
			Group group = new Group(parent, SWT.SHADOW_NONE);
			group.setText(groupName);
			parentComposite = group;
		}

		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		doCreateContent(parentComposite, numColumns);

		// Load the group settings
		loadGroupSettings();
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
	protected void notifyGroupChanged(IField field) {
		// Iterates over all listeners
		for (Object listener : groupListeners.getListeners()) {
			// Fires dialog group changed notification
			((IGroupListener) listener).groupChanged(field);
		}
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

	/*
	 * @see
	 * org.eclipse.sphinx.platform.ui.groups.IGroup#addGroupListener(org.eclipse.sphinx.platform.ui.groups.IGroupListener
	 * )
	 */
	@Override
	public final void addGroupListener(IGroupListener listener) {
		groupListeners.add(listener);
	}

	/*
	 * @see
	 * org.eclipse.sphinx.platform.ui.groups.IGroup#removeGroupListener(org.eclipse.sphinx.platform.ui.groups.IGroupListener
	 * )
	 */
	@Override
	public void removeGroupListener(IGroupListener listener) {
		groupListeners.remove(listener);
	}

	/**
	 * Returns true or false if the group is complete or not.
	 */
	@Override
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
