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
package org.eclipse.sphinx.platform.ui.views.documentation;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.platform.ui.internal.Activator;
import org.eclipse.sphinx.platform.ui.internal.messages.Messages;
import org.eclipse.sphinx.platform.ui.views.documentation.bootstrap.BootstrapDescriptionWrapper;
import org.eclipse.sphinx.platform.ui.views.documentation.bootstrap.BootstrapFormatterHTML;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

/**
 * The view to show any additional documentation. It collects the data to actually display through various extension
 * points.
 */
public class DocumentationView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.sphinx.platform.ui.views.documentation.DocumentationView"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_DOCUMENTATION_VIEW_FORMATTERS = "org.eclipse.sphinx.platform.ui.documentationViewFormatters"; //$NON-NLS-1$
	private static final String NODE_INSTANCEOF = "instanceof"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$

	private Browser browser;
	protected IDescriptionWrapper wrapper = new BootstrapDescriptionWrapper();
	private IExtensionRegistry extensionRegistry;

	protected Ordering<IConfigurationElement> configOrdering = new Ordering<IConfigurationElement>() {

		@Override
		public int compare(IConfigurationElement left, IConfigurationElement right) {
			String leftPriority = left.getAttribute(ATTR_PRIORITY);
			String rightPriority = right.getAttribute(ATTR_PRIORITY);
			int leftPriorityInt = 0;
			int rightPriorityInt = 0;
			if (leftPriority.length() > 0) {
				leftPriorityInt = Integer.parseInt(leftPriority);
			}
			if (rightPriority.length() > 0) {
				rightPriorityInt = Integer.parseInt(rightPriority);
			}
			return Ints.compare(leftPriorityInt, rightPriorityInt);
		}
	};

	/*
	 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in
	 * adapters or simply return objects as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 */
	private ISelectionListener listener = new ISelectionListener() {

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (!(selection instanceof IStructuredSelection)) {
				return;
			}

			StringBuilder builder = new StringBuilder();
			Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
			String topTitle = Messages.title_documentation;
			List<IDocumentationSection> allSections = Lists.<IDocumentationSection> newArrayList();

			while (iterator.hasNext()) {
				final Object selectedObject = iterator.next();
				List<IConfigurationElement> applicableDocViewFormatters = configOrdering.sortedCopy(getApplicableDocViewFormatterfor(selectedObject));
				Iterable<IDocumentationViewFormatter> formatters = Iterables.transform(applicableDocViewFormatters,
						new Function<IConfigurationElement, IDocumentationViewFormatter>() {

							@Override
							public IDocumentationViewFormatter apply(IConfigurationElement input) {
								try {
									return (IDocumentationViewFormatter) input.createExecutableExtension(ATTR_CLASS);
								} catch (CoreException ex) {
									PlatformLogUtil.logAsError(Activator.getDefault(), ex);
									return null;
								}
							}
						});

				for (IDocumentationViewFormatter formatter : formatters) {
					if (formatter != null) {
						List<IDocumentationSection> formatterSections = formatter.getDocumentationSectionFor(selectedObject);
						allSections.addAll(formatterSections);
						for (IDocumentationSection section : formatterSections) {
							if (section.getSectionTitle() != null && section.getSectionTitle().length() > 0 && section.getSectionBody() != null
									&& section.getSectionBody().trim().length() > 0) {
								builder.append(wrapper.textPre());
								builder.append("<h3>" + section.getSectionTitle() + "</h3>"); //$NON-NLS-1$ //$NON-NLS-2$
								builder.append(section.getSectionBody() + wrapper.textPost());
							}
						}
					}
					if (formatter instanceof ITitleProvider) {
						// FIXME
						topTitle = ((ITitleProvider) formatter).getTitle(selectedObject);
					}
				}
			}

			browser.setText(BootstrapFormatterHTML.format(topTitle, builder.toString(), allSections));
		}
	};

	private IExtensionRegistry getExtensionRegistry() {
		if (extensionRegistry == null) {
			extensionRegistry = Platform.getExtensionRegistry();
		}
		return extensionRegistry;
	}

	protected IConfigurationElement[] readContributedDocViewFormatters() {
		IExtensionRegistry extensionRegistry = getExtensionRegistry();
		if (extensionRegistry != null) {
			return extensionRegistry.getConfigurationElementsFor(EXTENSION_POINT_DOCUMENTATION_VIEW_FORMATTERS);
		}
		return new IConfigurationElement[0];
	}

	protected Iterable<IConfigurationElement> getApplicableDocViewFormatterfor(final Object selectedObject) {
		Iterable<IConfigurationElement> applicableDocViewFormatters = Iterables.filter(Lists.newArrayList(readContributedDocViewFormatters()),
				new Predicate<IConfigurationElement>() {

					@Override
					public boolean apply(IConfigurationElement configElement) {
						try {
							IConfigurationElement instanceOfConfigElement = configElement.getChildren(NODE_INSTANCEOF)[0];
							Expression expression = ExpressionConverter.getDefault().perform(instanceOfConfigElement);
							EvaluationResult evaluate = expression.evaluate(new EvaluationContext(null, selectedObject));
							return evaluate.equals(EvaluationResult.TRUE);
						} catch (Exception ex) {
							PlatformLogUtil.logAsError(Activator.getDefault(), ex);
							return false;
						}
					}
				});
		return applicableDocViewFormatters;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		try {
			browser = new Browser(parent, SWT.NONE);

			String html = "<HTML><BODY>";
			html += "<P>" + Messages.desc_model_object_selection + "</P>";
			html += "</BODY></HTML>";

			browser.setText(html);
			getSite().getPage().addSelectionListener(listener);
		} catch (SWTError ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
			return;
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		browser.setFocus();
	}

	@Override
	public void dispose() {
		getSite().getPage().removeSelectionListener(listener);
		super.dispose();
	}
}