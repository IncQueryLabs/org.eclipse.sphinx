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
package org.eclipse.sphinx.platform.ui.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * <p align=center> <b><em>UI Layout Utility</em></b> </p> <p align=justify> Provides utility methods to set parameters
 * of UI layouts. </p>
 */
public class LayoutUtil {

	/**
	 * The maximum width allowed for this field.
	 */
	protected static final int MAX_WITDH = 175;

	/**
	 * Sets the width hint of a control. Assumes that GridData is used.
	 */
	public static void setWidthHint(Control control, int widthHint) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).widthHint = widthHint;
		}
	}

	/**
	 * Sets the horizontal grabbing of a control to true. Assumes that GridData is used.
	 */
	public static void setHorizontalGrabbing(Control control) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).grabExcessHorizontalSpace = true;
		}
	}

	/**
	 * Sets the span of a control. Assumes that GridData is used.
	 */
	public static void setHorizontalSpan(Control control, int span) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).horizontalSpan = span;
		} else if (span != 1) {
			GridData gd = new GridData();
			gd.horizontalSpan = span;
			control.setLayoutData(gd);
		}
	}

	// ------------------------------------------------------------------------
	// Layout helpers
	// ------------------------------------------------------------------------

	/*
	 * Combo
	 */

	public static final GridData gridDataForCombo(int span) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = span;
		return gd;
	}

	public static final TableWrapData tableWrapDataForCombo(int span) {
		TableWrapData twd = new TableWrapData();
		twd.align = TableWrapData.FILL;
		twd.grabHorizontal = false;
		twd.colspan = span;
		return twd;
	}

	public static final GridData gridDataForCCombo(int span) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = span;
		return gd;
	}

	public static final TableWrapData tableWrapDataForCCombo(int span) {
		TableWrapData twd = new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE);
		twd.grabHorizontal = true;
		twd.colspan = span;
		// TODO Calculate with according to page width, number of columns horizontal spacing, etc.
		twd.maxWidth = MAX_WITDH;
		return twd;
	}

	/*
	 * Button
	 */

	public static final GridData gridDataForButtons(int span) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = false;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = span;
		return gd;
	}

	public static final TableWrapData tableWrapDataForButtons(int span) {
		TableWrapData twd = new TableWrapData();
		twd.align = TableWrapData.FILL;
		twd.grabHorizontal = false;
		twd.valign = TableWrapData.FILL;
		twd.grabVertical = true;
		twd.colspan = span;
		return twd;
	}

	public static final GridData gridDataForButton(Button button, int span) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = span;
		if (button.getStyle() == SWT.PUSH) {
			gd.widthHint = SWTUtil.getButtonWidthHint(button);
		}
		return gd;
	}

	public static final TableWrapData tableWrapDataForButton(Button button, int span) {
		// FIXME Find the right one
		// TableWrapData twd = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
		TableWrapData twd = new TableWrapData();
		twd.align = TableWrapData.FILL;
		twd.grabHorizontal = false;
		twd.colspan = span;
		// FIXME See this.
		if (button.getStyle() == SWT.PUSH) {
			twd.maxWidth = SWTUtil.getButtonWidthHint(button);
		}
		return twd;
	}

	/*
	 * Label
	 */

	public static final GridData gridDataForLabel(int span) {
		return gridDataForLabel(span, false);
	}

	public static final GridData gridDataForLabel(int span, boolean multiLine) {
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = span;
		return gd;
	}

	public static final TableWrapData tableWrapDataForLabel(int span) {
		return tableWrapDataForLabel(span, false);
	}

	public static final TableWrapData tableWrapDataForLabel(int span, boolean multiLine) {
		TableWrapData twd = new TableWrapData();
		twd.colspan = span;
		twd.valign = multiLine ? TableWrapData.TOP : TableWrapData.MIDDLE;
		return twd;
	}

	/*
	 * List
	 */

	public static final GridData gridDataForList(int span, PixelConverter converter) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = span;
		gd.widthHint = converter.convertWidthInCharsToPixels(50);
		gd.heightHint = converter.convertHeightInCharsToPixels(6);
		return gd;
	}

	public static final TableWrapData tableWrapDataForList(int span, PixelConverter converter) {
		TableWrapData twd = new TableWrapData();
		twd.align = TableWrapData.FILL;
		twd.grabHorizontal = true;
		twd.valign = TableWrapData.FILL;
		twd.grabVertical = true;
		twd.colspan = span;
		twd.maxWidth = converter.convertWidthInCharsToPixels(50);
		twd.heightHint = converter.convertHeightInCharsToPixels(6);
		return twd;
	}

	/*
	 * Text
	 */

	public static final GridData gridDataForText(int span) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = span;
		return gd;
	}

	public static final TableWrapData tableWrapDataForText(int span) {
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		// twd.align = TableWrapData.FILL;
		// twd.grabHorizontal = true;
		twd.colspan = span;
		twd.valign = TableWrapData.MIDDLE;
		return twd;
	}

	/*
	 * Separator
	 */

	public static final GridData gridDataForSeperator(int span, int height) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.heightHint = height;
		gd.horizontalSpan = span;
		return gd;
	}

	/*
	 * Specific composite
	 */

	public static final GridData gridDataForSpecificComposite(int span) {
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = span;
		return gd;
	}

	public static final GridLayout gridLayoutForSpecificComposite(int numColumns) {
		GridLayout gl = new GridLayout();
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.numColumns = numColumns;
		return gl;
	}

	public static final TableWrapData tableWrapDataForSpecificComposite(int span) {
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.colspan = span;
		return twd;
	}

	public static final TableWrapLayout tableWrapLayoutForSpecificComposite(int numColumns) {
		TableWrapLayout twl = new TableWrapLayout();
		twl.topMargin = 0;
		twl.bottomMargin = 0;
		twl.leftMargin = 0;
		twl.rightMargin = 0;
		twl.numColumns = numColumns;
		return twl;
	}
}
