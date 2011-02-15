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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.sphinx.platform.ui.internal.util.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * Utility class to simplify access to some SWT resources.
 */
public class SWTUtil {

	/**
	 * Returns the shell for the given widget. If the widget doesn't represent a SWT object that manage a shell,
	 * <code>null</code> is returned.
	 * 
	 * @return the shell for the given widget
	 */
	public static Shell getShell(Widget widget) {
		if (widget instanceof Control) {
			return ((Control) widget).getShell();
		}
		if (widget instanceof Caret) {
			return ((Caret) widget).getParent().getShell();
		}
		if (widget instanceof DragSource) {
			return ((DragSource) widget).getControl().getShell();
		}
		if (widget instanceof DropTarget) {
			return ((DropTarget) widget).getControl().getShell();
		}
		if (widget instanceof Menu) {
			return ((Menu) widget).getParent().getShell();
		}
		if (widget instanceof ScrollBar) {
			return ((ScrollBar) widget).getParent().getShell();
		}

		return null;
	}

	/**
	 * Returns a width hint for a button control.
	 */
	public static int getButtonWidthHint(Button button) {
		button.setFont(JFaceResources.getDialogFont());
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	/**
	 * Sets width and height hint for the button control. <b>Note:</b> This is a NOP if the button's layout data is not
	 * an instance of <code>GridData</code>.
	 * 
	 * @param button
	 *            the button for which to set the dimension hint
	 */
	public static void setButtonDimensionHint(Button button) {
		Assert.isNotNull(button);
		Object gd = button.getLayoutData();
		if (gd instanceof GridData) {
			((GridData) gd).widthHint = getButtonWidthHint(button);
			((GridData) gd).horizontalAlignment = GridData.FILL;
		}
	}

	public static Button createButton(Composite parent, String text, int style) {
		Button button = new Button(parent, style);
		button.setText(text);
		button.setLayoutData(getButtonGridData(button));
		return button;
	}

	/**
	 * Return the grid data for the button.
	 * 
	 * @param button
	 *            the button
	 * @return the grid data
	 */
	public static GridData getButtonGridData(Button button) {
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = getButtonWidthHint(button);
		return data;
	}

	// public static int getTableHeightHint(Table table, int rows) {
	// if (table.getFont().equals(JFaceResources.getDefaultFont()))
	// table.setFont(JFaceResources.getDialogFont());
	// int result= table.getItemHeight() * rows + table.getHeaderHeight();
	// if (table.getLinesVisible())
	// result+= table.getGridLineWidth() * (rows - 1);
	// return result;
	// }
}
