/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.search.ui.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.sphinx.emf.search.ui.ModelSearchQuery;
import org.eclipse.sphinx.emf.search.ui.QuerySpecification;
import org.eclipse.sphinx.emf.search.ui.internal.Activator;
import org.eclipse.sphinx.emf.search.ui.internal.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ModelSearchPage extends DialogPage implements ISearchPage {

	private final static String PAGE_NAME = "ModelSearchPage"; //$NON-NLS-1$

	private final static String STORE_CASE_SENSITIVE = "CASE_SENSITIVE"; //$NON-NLS-1$
	private final static String STORE_HISTORY = "HISTORY"; //$NON-NLS-1$
	private final static String STORE_HISTORY_SIZE = "HISTORY_SIZE"; //$NON-NLS-1$

	private static final int HISTORY_SIZE = 12;

	private ISearchPageContainer pageContainer;
	private IDialogSettings dialogSettings;
	private boolean isCaseSensitive;

	private Combo pattern;
	private Button caseSensitive;

	private final List<SearchPatternData> previousSearchPatterns;
	private SearchPatternData initialData;

	public ModelSearchPage() {
		super(PAGE_NAME);
		previousSearchPatterns = new ArrayList<SearchPatternData>();
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		readConfiguration();

		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(2, true);
		layout.horizontalSpacing = 10;
		composite.setLayout(layout);

		Control expressionComposite = createExpression(composite);
		expressionComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		setControl(composite);

		Dialog.applyDialogFont(composite);
	}

	private Control createExpression(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		result.setLayout(layout);

		// Pattern text + info
		Label label = new Label(result, SWT.LEFT);
		label.setText(Messages.ModelSearchPage_expression_label);
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));

		// Pattern combo
		pattern = new Combo(result, SWT.SINGLE | SWT.BORDER);
		pattern.setVisibleItemCount(HISTORY_SIZE);
		pattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handlePatternSelected();
				updateOKStatus();
			}
		});
		pattern.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateOKStatus();

			}
		});
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		data.widthHint = convertWidthInCharsToPixels(50);
		pattern.setLayoutData(data);

		// Ignore case checkbox
		caseSensitive = new Button(result, SWT.CHECK);
		caseSensitive.setText(Messages.ModelSearchPage_expression_caseSensitive);
		caseSensitive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isCaseSensitive = caseSensitive.getSelection();
			}
		});
		caseSensitive.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		return result;
	}

	private String getPattern() {
		return pattern.getText();
	}

	private SearchPatternData findInPrevious(String pattern) {
		for (SearchPatternData element : previousSearchPatterns) {
			if (pattern.equals(element.getPattern())) {
				return element;
			}
		}
		return null;
	}

	private void handlePatternSelected() {
		int selectionIndex = pattern.getSelectionIndex();
		if (selectionIndex < 0 || selectionIndex >= previousSearchPatterns.size()) {
			return;
		}

		SearchPatternData initialData = previousSearchPatterns.get(selectionIndex);

		pattern.setText(initialData.getPattern());
		isCaseSensitive = initialData.isCaseSensitive();
		caseSensitive.setEnabled(true);
		caseSensitive.setSelection(isCaseSensitive);

		this.initialData = initialData;
	}

	protected void updateOKStatus() {
		boolean isValidPattern = isValidSearchPattern();
		getContainer().setPerformActionEnabled(isValidPattern);
	}

	private boolean isValidSearchPattern() {
		return !getPattern().isEmpty();
	}

	@Override
	public boolean performAction() {
		SearchPatternData patternData = getPatternData();

		QuerySpecification querySpec = new QuerySpecification(patternData.getPattern(), patternData.isCaseSensitive(),
				Collections.<IProject> emptySet());

		ModelSearchQuery modelSearchQuery = new ModelSearchQuery(querySpec);
		NewSearchUI.runQueryInBackground(modelSearchQuery);
		return true;
	}

	/**
	 * Return search pattern data and update previous searches. An existing entry will be updated.
	 *
	 * @return the pattern data
	 */
	private SearchPatternData getPatternData() {
		String pattern = getPattern();
		SearchPatternData match = findInPrevious(pattern);
		if (match != null) {
			previousSearchPatterns.remove(match);
		}
		match = new SearchPatternData(pattern, caseSensitive.getSelection());

		previousSearchPatterns.add(0, match); // insert on top
		return match;
	}

	@Override
	public void setContainer(ISearchPageContainer container) {
		pageContainer = container;
	}

	/**
	 * Returns the search page's container.
	 *
	 * @return the search page container
	 */
	private ISearchPageContainer getContainer() {
		return pageContainer;
	}

	@Override
	public void dispose() {
		writeConfiguration();
		super.dispose();
	}

	/**
	 * Returns the page settings for this Java search page.
	 *
	 * @return the page settings to be used
	 */
	private IDialogSettings getDialogSettings() {
		if (dialogSettings == null) {
			dialogSettings = Activator.getDefault().getDialogSettingsSection(PAGE_NAME);
		}
		return dialogSettings;
	}

	/**
	 * Initializes itself from the stored page settings.
	 */
	private void readConfiguration() {
		IDialogSettings s = getDialogSettings();
		isCaseSensitive = s.getBoolean(STORE_CASE_SENSITIVE);

		try {
			int historySize = s.getInt(STORE_HISTORY_SIZE);
			for (int i = 0; i < historySize; i++) {
				IDialogSettings histSettings = s.getSection(STORE_HISTORY + i);
				if (histSettings != null) {
					SearchPatternData data = SearchPatternData.create(histSettings);
					if (data != null) {
						previousSearchPatterns.add(data);
					}
				}
			}
		} catch (NumberFormatException e) {
			// ignore
		}
	}

	/**
	 * Stores the current configuration in the dialog store.
	 */
	private void writeConfiguration() {
		IDialogSettings s = getDialogSettings();
		s.put(STORE_CASE_SENSITIVE, isCaseSensitive);

		int historySize = Math.min(previousSearchPatterns.size(), HISTORY_SIZE);
		s.put(STORE_HISTORY_SIZE, historySize);
		for (int i = 0; i < historySize; i++) {
			IDialogSettings histSettings = s.addNewSection(STORE_HISTORY + i);
			SearchPatternData data = previousSearchPatterns.get(i);
			data.store(histSettings);
		}
	}

	private static class SearchPatternData {
		private final String pattern;
		private final boolean isCaseSensitive;

		public SearchPatternData(String pattern, boolean isCaseSensitive) {
			this.pattern = pattern;
			this.isCaseSensitive = isCaseSensitive;
		}

		public String getPattern() {
			return pattern;
		}

		public boolean isCaseSensitive() {
			return isCaseSensitive;
		}

		public void store(IDialogSettings settings) {
			settings.put("isCaseSensitive", isCaseSensitive); //$NON-NLS-1$
			settings.put("pattern", pattern); //$NON-NLS-1$
		}

		public static SearchPatternData create(IDialogSettings settings) {

			String pattern = settings.get("pattern"); //$NON-NLS-1$
			if (pattern.length() == 0) {
				return null;
			}

			boolean isCaseSensitive = settings.getBoolean("isCaseSensitive"); //$NON-NLS-1$

			return new SearchPatternData(pattern, isCaseSensitive);
		}
	}
}
