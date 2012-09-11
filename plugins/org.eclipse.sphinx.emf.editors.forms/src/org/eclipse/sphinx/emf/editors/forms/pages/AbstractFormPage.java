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
package org.eclipse.sphinx.emf.editors.forms.pages;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryContentProvider;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryLabelProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.sphinx.emf.editors.forms.BasicTransactionalFormEditor;
import org.eclipse.sphinx.emf.editors.forms.internal.Activator;
import org.eclipse.sphinx.emf.editors.forms.sections.IFormSection;
import org.eclipse.sphinx.emf.ui.forms.messages.IFormMessage;
import org.eclipse.sphinx.emf.ui.forms.messages.IFormMessageProvider;
import org.eclipse.sphinx.emf.validation.IValidationProblemMarkersChangeListener;
import org.eclipse.sphinx.emf.validation.ValidationProblemMarkersChangeNotifier;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public abstract class AbstractFormPage extends FormPage {

	public static final String VIEW_POINT = "org.eclipse.sphinx.emf.editors.forms.formMessageProvider"; //$NON-NLS-1$

	/**
	 * The formMessageProvider node name for a configuration element.
	 * <p>
	 * Equal to the word: <code>formMessageProvider</code>
	 * </p>
	 */
	public static final String FORMMESSAGEPROVIDER = "formMessageProvider"; //$NON-NLS-1$
	/**
	 * The class node name for a configuration element.
	 * <p>
	 * Equal to the word: <code>class</code>
	 * </p>
	 */
	public static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	protected Object pageInput = null;

	protected boolean created = false;

	protected boolean creating = false;

	protected List<IFormSection> sections = new ArrayList<IFormSection>();

	private IFormSection activeSection;

	protected List<IFormMessageProvider> messageProviders = new ArrayList<IFormMessageProvider>();

	protected ITreeContentProvider contentProvider;

	protected ILabelProvider labelProvider;

	protected IPropertyListener inputChangeListener = new IPropertyListener() {
		public void propertyChanged(Object source, int propId) {
			if (source.equals(getEditor()) && AbstractFormPage.this.equals(((FormEditor) source).getActivePageInstance())
					&& propId == IWorkbenchPartConstants.PROP_INPUT) {
				getSite().getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (!created) {
							createFormContent(getManagedForm());
						} else {
							setPageInput(getPageInputFromEditor());
						}
					}
				});
			}
		}
	};

	protected IPageChangedListener pageChangedListener = new IPageChangedListener() {
		public void pageChanged(PageChangedEvent event) {
			if (event.getSelectedPage().equals(AbstractFormPage.this)) {
				getSite().getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						refreshPage();
					}
				});
			}
		}
	};

	// TODO Replace with ResourceSetListener
	protected IOperationHistoryListener operationHistoryListener = new IOperationHistoryListener() {
		public void historyNotification(OperationHistoryEvent event) {
			switch (event.getEventType()) {
			case OperationHistoryEvent.DONE:
			case OperationHistoryEvent.UNDONE:
			case OperationHistoryEvent.REDONE:
				if (AbstractFormPage.this.equals(getEditor().getActivePageInstance())) {
					getSite().getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							refreshPage();
						}
					});
				}
				break;
			}
		}
	};

	protected IValidationProblemMarkersChangeListener validationProblemMarkersChangeListener = new IValidationProblemMarkersChangeListener() {
		public void validationProblemMarkersChanged(final EventObject event) {
			if (AbstractFormPage.this.equals(getEditor().getActivePageInstance())) {
				if ((EObject) event.getSource() == pageInput) {
					getSite().getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							refreshMessages();
						}
					});
				}
			}
		}
	};

	public AbstractFormPage(FormEditor editor, String title) {
		this(editor, title.replaceAll("[^A-Z]", ""), title); //$NON-NLS-1$//$NON-NLS-2$
	}

	public AbstractFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		getEditor().addPropertyListener(inputChangeListener);
		getEditor().addPageChangedListener(pageChangedListener);
		IOperationHistory operationHistory = getTransactionalFormEditor().getOperationHistory();
		if (operationHistory != null) {
			operationHistory.addOperationHistoryListener(operationHistoryListener);
		}
		ValidationProblemMarkersChangeNotifier.INSTANCE.addListener(validationProblemMarkersChangeListener);

		IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(VIEW_POINT);
		messageProviders = new ArrayList<IFormMessageProvider>();
		for (IConfigurationElement cfgElem : extension.getConfigurationElements()) {
			if (FORMMESSAGEPROVIDER.equals(cfgElem.getName())) {
				try {
					IFormMessageProvider msgProvider = (IFormMessageProvider) cfgElem.createExecutableExtension(CLASS_ATTR);
					messageProviders.add(msgProvider);
				} catch (CoreException ex) {
					PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
				}
			}
		}
	}

	/**
	 * @return The parent transactional form editor of this form page.
	 */
	public BasicTransactionalFormEditor getTransactionalFormEditor() {
		return (BasicTransactionalFormEditor) getEditor();
	}

	public AdapterFactoryItemDelegator getItemDelegator() {
		return getTransactionalFormEditor().getItemDelegator();
	}

	public ITreeContentProvider getContentProvider() {
		if (contentProvider == null) {
			contentProvider = createContentProvider();
		}

		return contentProvider;
	}

	protected ITreeContentProvider createContentProvider() {
		EditingDomain editingDomain = getTransactionalFormEditor().getEditingDomain();
		if (editingDomain instanceof TransactionalEditingDomain) {
			AdapterFactory adapterFactory = getTransactionalFormEditor().getAdapterFactory();
			if (adapterFactory != null) {
				return new TransactionalAdapterFactoryContentProvider((TransactionalEditingDomain) editingDomain, adapterFactory);
			}
		}
		return null;
	}

	public ILabelProvider getLabelProvider() {
		if (labelProvider == null) {
			labelProvider = createLabelProvider();
		}
		return labelProvider;
	}

	protected ILabelProvider createLabelProvider() {
		EditingDomain editingDomain = getTransactionalFormEditor().getEditingDomain();
		if (editingDomain instanceof TransactionalEditingDomain) {
			AdapterFactory adapterFactory = getTransactionalFormEditor().getAdapterFactory();
			if (adapterFactory != null) {
				return new TransactionalAdapterFactoryLabelProvider((TransactionalEditingDomain) editingDomain, adapterFactory);
			}
		}
		return null;
	}

	/**
	 * Returns the list of sections added to this page by calling {@link AbstractFormPage#addSection(IFormSection)}
	 * 
	 * @return
	 */
	public List<IFormSection> getFormSections() {
		return sections;
	}

	protected Object getPageInputFromEditor() {
		return getTransactionalFormEditor().getModelRoot();
	}

	protected final void setPageInput(Object pageInput) {
		// Initialize page input
		this.pageInput = pageInput;

		// Propagate new page input to sections
		for (IFormSection section : sections) {
			section.setSectionInput(pageInput);
		}
	}

	protected boolean canCreateFormContent() {
		return pageInput != null;
	}

	@Override
	protected final synchronized void createFormContent(final IManagedForm managedForm) {
		if (!created && !creating) {
			// Set creating flag immediately to true in order to avoid that creation is invoked multiple times by the
			// same thread (different threads get blocked due to the synchronized keyword but the same thread isn't
			// because synchronization is reentrant; see
			// http://java.sun.com/docs/books/tutorial/essential/concurrency/locksync.html for
			// details)
			creating = true;

			// Initialize page input
			setPageInput(getPageInputFromEditor());
			if (managedForm != null && canCreateFormContent()) {
				// Set form title
				managedForm.getForm().setText(getTitle());

				// Create form content
				doCreateFormContent(managedForm);

				// Lay out form content to make sure that it gets visible after deferred creation
				managedForm.getForm().getBody().layout(true);

				created = true;

				// Display validation problem messages
				refreshMessages();
			}

			creating = false;
		}
	}

	protected abstract void doCreateFormContent(IManagedForm managedForm);

	protected void addSection(IFormSection section) {
		if (section != null) {
			sections.add(section);
		}
	}

	@Override
	public void dispose() {
		sections.clear();
		ValidationProblemMarkersChangeNotifier.INSTANCE.removeListener(validationProblemMarkersChangeListener);
		IOperationHistory operationHistory = getTransactionalFormEditor().getOperationHistory();
		if (operationHistory != null) {
			operationHistory.removeOperationHistoryListener(operationHistoryListener);
		}
		getEditor().removePageChangedListener(pageChangedListener);
		getEditor().removePropertyListener(inputChangeListener);
		super.dispose();
	}

	protected final void refreshPage() {
		if (!created) {
			createFormContent(getManagedForm());
		} else {
			doRefreshPage();
		}
	}

	protected void doRefreshPage() {
		for (IFormSection section : sections) {
			section.refreshSection();
		}
	}

	protected final void refreshMessages() {
		getManagedForm().getMessageManager().removeAllMessages();

		for (IFormMessageProvider provider : messageProviders) {
			Map<EStructuralFeature, Set<IFormMessage>> messages = provider.getMessages(pageInput);
			if (messages.size() > 0) {
				doRefreshMessages(getManagedForm().getMessageManager(), messages);
			}
		}
	}

	protected void doRefreshMessages(IMessageManager messageManager, Map<EStructuralFeature, Set<IFormMessage>> messages) {
		for (IFormSection section : sections) {
			section.refreshMessages(messageManager, messages);
		}
	}

	public boolean isEmpty() {
		return !created;
	}

	/**
	 * Sets the active section on this page.
	 * 
	 * @param section
	 */
	public void setActiveSection(IFormSection section) {
		activeSection = section;
	}

	/**
	 * @return the active section in this page (e.g. the section which have the focus).
	 */
	public IFormSection getActiveSection() {
		return activeSection;
	}
}
