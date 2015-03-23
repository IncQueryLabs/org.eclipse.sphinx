/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
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
package org.eclipse.sphinx.documentationview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.sphinx.documentationview.bootstrap.BootstrapDescriptionWrapper;
import org.eclipse.sphinx.documentationview.bootstrap.BootstrapFormatterHTML;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

/**
 * The view to show any additional documentation. It collects the data to actually display Through various extension
 * points.
 */
public class DocumentationView extends ViewPart {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DocumentationView.class);

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.sphinx.documentationview.DocumentationView";

	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	private Browser browser;
	protected IDescriptionWrapper descWrapper = new BootstrapDescriptionWrapper();

	protected Ordering<IConfigurationElement> configOrdering = new Ordering<IConfigurationElement>() {
		@Override
		public int compare(IConfigurationElement left, IConfigurationElement right) {
			String lorder = left.getAttribute("order");
			String rorder = right.getAttribute("order");
			int ileft = 0;
			int iright = 0;
			if (lorder.length() > 0) {
				ileft = Integer.parseInt(lorder);
			}
			if (rorder.length() > 0) {
				iright = Integer.parseInt(rorder);
			}
			return Ints.compare(ileft, iright);
		}
	};

	/*
	 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in
	 * adapters or simply return objects as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 */

	ISelectionListener listener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection sel) {
			if (!(sel instanceof IStructuredSelection)) {
				return;
			}
			IStructuredSelection ss = (IStructuredSelection) sel;
			StringBuilder builder = new StringBuilder();
			Iterator iterator = ss.iterator();
			String topTitle = "Documentation";
			ArrayList<IDescriptionSection> sections = Lists.<IDescriptionSection> newArrayList();
			while (iterator.hasNext()) {
				Object xo = iterator.next();
				// ImmutableList<EObject> ol = SemanticResolver.INSTANCE
				// .resolveSemanticObjects(xo);

				// for (final EObject o : Iterables.filter(ol,
				// Predicates.notNull())) {
				final Object o = xo;
				IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
				IConfigurationElement[] configurationElementsFor = extensionRegistry
						.getConfigurationElementsFor("org.eclipse.sphinx.documentationview.contribution");

				ArrayList<IConfigurationElement> cels = Lists.newArrayList(configurationElementsFor);
				Iterable<IConfigurationElement> applicable = Iterables.filter(cels, new Predicate<IConfigurationElement>() {
					@Override
					public boolean apply(IConfigurationElement input) {
						String targetC = input.getAttribute("targetClass");

						if (logger.isDebugEnabled()) {

							logger.debug(targetC);
							logger.debug(o.getClass().getCanonicalName());
						}
						try {
							Expression expression = ExpressionConverter.getDefault().perform(input.getChildren("instanceof")[0]);
							if (logger.isDebugEnabled()) {
								logger.debug(expression);
							}
							EvaluationContext context = new EvaluationContext(null, o);
							EvaluationResult evaluate = expression.evaluate(new EvaluationContext(null, o));
							if (logger.isDebugEnabled()) {
								logger.debug(evaluate);
							}
							return evaluate.equals(EvaluationResult.TRUE);
						} catch (InvalidRegistryObjectException e) {

							e.printStackTrace();
							return false;
						} catch (CoreException e) {

							e.printStackTrace();
							return false;
						}

					}
				});

				List<IConfigurationElement> sortedApplicable = configOrdering.sortedCopy(applicable);
				Iterable<IDescriptionFormatter> formatters = Iterables.transform(sortedApplicable,
						new Function<IConfigurationElement, IDescriptionFormatter>() {
							@Override
							public IDescriptionFormatter apply(IConfigurationElement input) {
								try {
									if (logger.isDebugEnabled()) {
										logger.debug("Mapping " + input);
									}
									return (IDescriptionFormatter) input.createExecutableExtension("contributingFormatter");
								} catch (CoreException e) {
									e.printStackTrace();
									return null;
								}
							}
						});

				for (IDescriptionFormatter formatter : formatters) {
					if (logger.isDebugEnabled()) {
						logger.debug("Formatter " + formatter);
					}
					if (formatter != null) {
						sections.addAll(formatter.descriptionSections(o));
						for (IDescriptionSection s : formatter.descriptionSections(o)) {
							builder.append(descWrapper.textPre());
							if (s.getSectionTitle() != null && s.getSectionTitle().length() > 0 && s.getSectionBody() != null
									&& s.getSectionBody().trim().length() > 0) {
								builder.append("<h3>" + s.getSectionTitle() + "</h3>");
								builder.append(s.getSectionBody() + descWrapper.textPost());
							}
						}
					}
					if (formatter instanceof ITitleProvider) {
						ITitleProvider tp = (ITitleProvider) formatter;
						topTitle = tp.getObjectTitle(o);

					}
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug(builder);
			}
			String documentationText = BootstrapFormatterHTML.pre(topTitle, sections) + builder.toString() + BootstrapFormatterHTML.post();
			browser.setText(documentationText);
		}
	};

	class ViewContentProvider implements IStructuredContentProvider {
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object parent) {
			return new String[] { "One", "Two", "Three" };
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public DocumentationView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		try {
			browser = new Browser(parent, SWT.NONE);

			String html = "<HTML><HEAD><TITLE>Model documentation</TITLE></HEAD><BODY>";
			html += "<P>Select a model element to display information about it.</P>";
			html += "</BODY></HTML>";

			browser.setText(html);
			getSite().getPage().addSelectionListener(listener);
		} catch (SWTError e) {
			logger.error("Could not instantiate Browser: " + e.getMessage());

			return;
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				DocumentationView.this.fillContextMenu(manager);
			}
		});
		// Menu menu = menuMgr.createContextMenu(viewer.getControl());
		// viewer.getControl().setMenu(menu);
		// getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			@Override
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			@Override
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			@Override
			public void run() {
				// ISelection selection = viewer.getSelection();
				// Object obj =
				// ((IStructuredSelection)selection).getFirstElement();
				// showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		// viewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// doubleClickAction.run();
		// }
		// });
	}

	private void showMessage(String message) {
		// MessageDialog.openInformation(
		// viewer.getControl().getShell(),
		// "Sphinx Documentation View",
		// message);
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