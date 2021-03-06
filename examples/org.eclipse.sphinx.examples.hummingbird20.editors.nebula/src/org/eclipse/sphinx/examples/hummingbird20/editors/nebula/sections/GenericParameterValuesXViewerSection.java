/**
 * <copyright>
 *
 * Copyright (c) 2012-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [460260] Expanded paths are collapsed on resource reload
 *
 * </copyright>
 */
package org.eclipse.sphinx.examples.hummingbird20.editors.nebula.sections;

import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryContentProvider;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.sphinx.emf.editors.forms.nebula.providers.BasicModelXViewerLabelProvider;
import org.eclipse.sphinx.emf.editors.forms.nebula.sections.BasicXViewerSection;
import org.eclipse.sphinx.emf.editors.forms.pages.AbstractFormPage;
import org.eclipse.sphinx.examples.hummingbird20.editors.nebula.messages.Messages;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Factory;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.ParameterValue;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.edit.ComponentItemProvider;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.edit.InstanceModel20ItemProviderAdapterFactory;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

public class GenericParameterValuesXViewerSection extends BasicXViewerSection {

	private static final String XCOL_ID_EXTRA_INFO = "ExtraInfo"; //$NON-NLS-1$

	public GenericParameterValuesXViewerSection(AbstractFormPage formPage, Object sectionInput) {
		this(formPage, sectionInput, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
	}

	public GenericParameterValuesXViewerSection(AbstractFormPage formPage, Object sectionInput, int style) {
		super(formPage, sectionInput, InstanceModel20Factory.eINSTANCE.createParameterValue(), style);

		title = Messages.title_ParameterValues_Section;
		description = Messages.desc_ParameterValues_Section;
	}

	/**
	 * Strategy 1: Create Xviewer content through (optionally customized) AdapterFactoryContentProvider exposing
	 * ItemProviderAdapters generated by EMF Edit. Convenient when all children EObjects owned by some parent EObject
	 * are to be displayed.
	 */
	@Override
	protected IContentProvider createContentProvider() {
		InstanceModel20ItemProviderAdapterFactory adapterFactory = new InstanceModel20ItemProviderAdapterFactory() {
			@Override
			public Adapter createComponentAdapter() {
				if (componentItemProvider == null) {
					componentItemProvider = new ComponentItemProvider(this) {
						@Override
						public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
							if (childrenFeatures == null) {
								super.getChildrenFeatures(object);
								// Consider only parameter value children of component
								childrenFeatures.remove(InstanceModel20Package.Literals.COMPONENT__OUTGOING_CONNECTIONS);
								childrenFeatures.remove(InstanceModel20Package.Literals.COMPONENT__PARAMETER_EXPRESSIONS);
							}
							return childrenFeatures;
						};
					};
				}
				return componentItemProvider;
			}
		};
		TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain((EObject) sectionInput);
		return new TransactionalAdapterFactoryContentProvider(editingDomain, adapterFactory);
	}

	/**
	 * Strategy 2: Create Xviewer content through own content provider implementation returning flat list of interesting
	 * EObjects as children. Useful when not all but a filtered or calculated subset of children EObjects owned by some
	 * parent EObject are to be displayed.
	 */
	// @Override
	// protected IContentProvider createContentProvider() {
	// return new ITreeContentProvider() {
	//
	// public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	// }
	//
	// public Object[] getElements(Object inputElement) {
	// return getChildren(inputElement);
	// }
	//
	// public boolean hasChildren(Object element) {
	// return getChildren(element).length > 0;
	// }
	//
	// public Object[] getChildren(Object parentElement) {
	// return viewedParameterValues.toArray(new ParameterValue[viewedParameterValues.size()]);
	// }
	//
	// public Object getParent(Object element) {
	// return null;
	// }
	//
	// public void dispose() {
	// }
	// };
	// }

	/**
	 * Addition of an extra column which displaying some calculated information that not part of the model.
	 */
	@Override
	protected void registerColumns(XViewerFactory xViewerFactory) {
		super.registerColumns(xViewerFactory);
		xViewerFactory.registerColumns(new XViewerColumn(XCOL_ID_EXTRA_INFO, Messages.xcol_ColumnName_Extra_INFO, 50, XViewerAlign.Left, true,
				SortDataType.Float, false, Messages.xcol_ColumnDesc_Extra_INFO));
	}

	@Override
	protected BasicModelXViewerLabelProvider createLabelProvider() {
		return new BasicModelXViewerLabelProvider((XViewer) getViewer(), formPage.getItemDelegator()) {
			@Override
			public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
				if (element instanceof ParameterValue && XCOL_ID_EXTRA_INFO.equals(xCol.getId())) {
					return "0.17"; //$NON-NLS-1$
				}
				return super.getColumnText(element, xCol, columnIndex);
			}
		};
	}
}
