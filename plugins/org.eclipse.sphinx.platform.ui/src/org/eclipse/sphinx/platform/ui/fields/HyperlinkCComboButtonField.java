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
package org.eclipse.sphinx.platform.ui.fields;

import org.eclipse.sphinx.platform.ui.fields.adapters.IButtonAdapter;
import org.eclipse.sphinx.platform.ui.internal.util.LayoutUtil;
import org.eclipse.sphinx.platform.ui.widgets.IWidgetFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.HyperlinkAdapter;

/**
 * <p align=center> <b><em>Combo Field</em></b> </p> <p align=justify> Field containing a label, a combo control and a
 * button. </p>
 */
public class HyperlinkCComboButtonField extends HyperlinkCComboField implements ICComboButtonField {

	private Button fBrowseButton;

	private String fBrowseButtonLabel;

	private IButtonAdapter fButtonAdapter;

	private boolean fButtonEnabled;

	public HyperlinkCComboButtonField(int flags, IWidgetFactory widgetFactory, HyperlinkAdapter hyperlinkAdapter, IButtonAdapter buttonAdapter) {
		super(flags, widgetFactory, hyperlinkAdapter);
		fBrowseButtonLabel = "!Browse...!"; //$NON-NLS-1$
		fButtonEnabled = true;
		fButtonAdapter = buttonAdapter;
	}

	/**
	 * Sets the label of the button.
	 */
	public void setButtonLabel(String label) {
		fBrowseButtonLabel = label;
	}

	// ------ adapter communication

	/**
	 * Programmatical pressing of the button
	 */
	private void changeControlPressed() {
		fButtonAdapter.changeControlPressed(this);
	}

	// ------- layout helpers

	/*
	 * @see BasicField#doFillIntoGrid
	 */
	@Override
	protected Control[] doFillIntoGrid(Composite parent, int nColumns) {

		Control label = getLabelControl(parent, 1);

		CCombo combo = getCComboControl(parent, nColumns - 2);

		Button button = getChangeControl(parent, 1);

		return new Control[] { label, combo, button };
	}

	/*
	 * @see BasicField#getNumberOfControls
	 */
	@Override
	protected int getNumberOfControls() {
		return 3;
	}

	// ------- ui creation

	/**
	 * Creates button control.
	 * 
	 * @param parent
	 *            The parent composite (supposed to be not <tt>null</tt>).
	 * @param hspan
	 *            The number of columns the button widget must span.
	 * @return The created button control.
	 */
	protected final Button getChangeControl(Composite parent, int hspan) {
		Button button = fBrowseButton;
		if (button == null) {
			button = createChangeControl(parent);
			if (button.getLayoutData() == null) {
				if (fUseFormLayout) {
					button.setLayoutData(LayoutUtil.tableWrapDataForButton(button, hspan));
				} else {
					button.setLayoutData(LayoutUtil.gridDataForButton(button, hspan));
				}
			}
		}
		return button;
	}

	/**
	 * Creates or returns the created buttom widget.
	 * 
	 * @param parent
	 *            The parent composite or <code>null</code> if the widget has already been created.
	 */
	private Button createChangeControl(Composite parent) {
		if (fBrowseButton == null) {
			assertCompositeNotNull(parent);

			int style = -1;
			if (fUseFormLayout) {
				style = SWT.FLAT;
			} else {
				style = SWT.PUSH;
			}
			fBrowseButton = new Button(parent, style);
			fBrowseButton.setFont(parent.getFont());
			fBrowseButton.setText(fBrowseButtonLabel);
			fBrowseButton.setEnabled(isEnabled() && fButtonEnabled);
			fBrowseButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					changeControlPressed();
				}

				public void widgetSelected(SelectionEvent e) {
					changeControlPressed();
				}
			});

		}
		return fBrowseButton;
	}

	/*
	 * @see org.eclipse.sphinx.platform.ui.fields.ICComboButtonField#getButtonControl()
	 */
	public Control getButtonControl() {
		Control control = null;
		if (isOkToUse(fBrowseButton)) {
			control = fBrowseButton;
		}
		return control;
	}

	// ------ enable / disable management

	/**
	 * Sets the enable state of the button.
	 */
	public void enableButton(boolean enable) {
		if (isOkToUse(fBrowseButton)) {
			fBrowseButton.setEnabled(isEnabled() && enable);
		}
		fButtonEnabled = enable;
	}

	/*
	 * @see BasicField#updateEnableState
	 */
	@Override
	protected void updateEnableState() {
		super.updateEnableState();
		if (isOkToUse(fBrowseButton)) {
			fBrowseButton.setEnabled(isEnabled() && fButtonEnabled);
		}
	}
}
