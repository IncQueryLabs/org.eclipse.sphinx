/**
 * <copyright>
 * 
 * Copyright (c) 2012 itemis and others.
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
package org.eclipse.sphinx.emf.editors.forms.nebula.sections;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.sphinx.emf.editors.forms.BasicTransactionalFormEditor;
import org.eclipse.sphinx.emf.editors.forms.nebula.providers.BasicModelXViewerLabelProvider;
import org.eclipse.sphinx.emf.editors.forms.pages.AbstractFormPage;
import org.eclipse.sphinx.emf.editors.forms.sections.AbstractViewerFormSection;
import org.eclipse.sphinx.platform.ui.util.SelectionUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;

// TODO Provide this as a field rather than a section
// TODO Pull this into Sphinx; find a solution to Nebula releng problem
// TODO Get rid of obsolete AbstractViewerFormSection#isEmpty() and AbstractFormPage#isEmpty() methods
public class BasicXViewerSection extends AbstractViewerFormSection {

	protected EObject value;

	public BasicXViewerSection(AbstractFormPage formPage, Object sectionInput, EObject value) {
		this(formPage, sectionInput, value, SWT.NONE);
	}

	public BasicXViewerSection(AbstractFormPage formPage, Object sectionInput, EObject value, int style) {
		super(formPage, sectionInput, style);

		Assert.isNotNull(value);

		this.value = value;
	}

	@Override
	protected int getNumberOfColumns() {
		return 1;
	}

	@Override
	protected void createSectionClientContent(final IManagedForm managedForm, final SectionPart sectionPart, Composite sectionClient) {
		Assert.isNotNull(managedForm);
		Assert.isNotNull(sectionPart);
		Assert.isNotNull(sectionClient);

		// Define table columns
		XViewerFactory xViewerFactory = createXViewerFactory(sectionClient);

		// Create table viewer
		XViewer xViewer = createXViewer(sectionClient, xViewerFactory);
		viewer = xViewer;
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.minimumWidth = 640;
		xViewer.getTree().setLayoutData(layoutData);

		// Provide table content
		xViewer.setContentProvider(createContentProvider());
		xViewer.setLabelProvider(createLabelProvider());

		xViewer.setInput(sectionInput);
	}

	protected XViewer createXViewer(Composite sectionClient, XViewerFactory xViewerFactory) {
		final BasicTransactionalFormEditor formEditor = formPage.getTransactionalFormEditor();
		XViewer xViewer = new XViewer(sectionClient, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER, xViewerFactory) {
			@Override
			public void setSelection(ISelection selection) {
				selection = !SelectionUtil.getStructuredSelection(selection).isEmpty() ? selection : formEditor.getDefaultSelection();
				super.setSelection(selection);
			}
		};
		return xViewer;
	}

	protected XViewerFactory createXViewerFactory(Composite sectionClient) {
		XViewerFactory xViewerFactory = new XViewerFactory(value.eClass().getName()) {
			public boolean isAdmin() {
				return true;
			}
		};

		registerColumns(xViewerFactory);
		return xViewerFactory;
	}

	protected void registerColumns(XViewerFactory xViewerFactory) {
		List<IItemPropertyDescriptor> propertyDescriptors = formPage.getItemDelegator().getPropertyDescriptors(value);
		for (IItemPropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyDescriptor.getFeature(value) instanceof EAttribute) {
				xViewerFactory.registerColumns(new XViewerColumn(propertyDescriptor.getId(value).toString(),
						propertyDescriptor.getDisplayName(value), 50, SWT.LEFT, true, getSortDataType(propertyDescriptor, value), false,
						propertyDescriptor.getDescription(value)));
			}
		}
	}

	protected SortDataType getSortDataType(IItemPropertyDescriptor propertyDescriptor, Object object) {
		EClassifier propertyType = ((EStructuralFeature) propertyDescriptor.getFeature(object)).getEType();
		if (propertyType == EcorePackage.eINSTANCE.getEDate()) {
			return SortDataType.Date;
		} else if (propertyType == EcorePackage.eINSTANCE.getEFloat() || propertyType == EcorePackage.eINSTANCE.getEDouble()) {
			return SortDataType.Float;
		} else if (propertyType == EcorePackage.eINSTANCE.getEBoolean()) {
			return SortDataType.Check;
		} else if (propertyType == EcorePackage.eINSTANCE.getEInt()) {
			return SortDataType.Integer;
		} else if (propertyDescriptor.isMultiLine(object)) {
			return SortDataType.String_MultiLine;
		}
		return SortDataType.String;
	}

	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new BasicModelXViewerLabelProvider((XViewer) viewer, formPage.getItemDelegator());
	}
}
