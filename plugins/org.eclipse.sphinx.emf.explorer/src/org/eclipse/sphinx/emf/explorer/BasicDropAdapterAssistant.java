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
package org.eclipse.sphinx.emf.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.DragAndDropCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.edit.provider.WrapperItemProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.explorer.internal.Activator;
import org.eclipse.sphinx.platform.util.StatusUtil;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

public class BasicDropAdapterAssistant extends CommonDropAdapterAssistant {

	protected static final int operations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;

	/*
	 * @see org.eclipse.ui.navigator.CommonDropAdapterAssistant#handleDrop(org.eclipse.ui.navigator.CommonDropAdapter,
	 * org.eclipse.swt.dnd.DropTargetEvent, java.lang.Object)
	 */
	@Override
	public IStatus handleDrop(CommonDropAdapter dropAdapter, DropTargetEvent event, Object target) {
		TransferData transferType = dropAdapter.getCurrentTransfer();
		if (LocalSelectionTransfer.getTransfer().isSupportedType(transferType)) {
			switch (dropAdapter.getCurrentOperation()) {
			case DND.DROP_MOVE:
				handleDropMove(target, dropAdapter, event);
				break;
			case DND.DROP_COPY:
				handleDropCopy(target, dropAdapter, event);
				break;
			case DND.DROP_LINK:
				break;
			}
			return Status.OK_STATUS;
		}
		return new Status(IStatus.ERROR, Activator.getPlugin().getBundle().getSymbolicName(), "Cannot drop, transfer type is not supported"); //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.ui.navigator.CommonDropAdapterAssistant#validateDrop(java.lang.Object, int,
	 * org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	public IStatus validateDrop(Object target, int operation, TransferData transferType) {
		if (LocalSelectionTransfer.getTransfer().isSupportedType(transferType)) {
			List<EObject> selectedEObjects = getSelectedEObjects();

			if (!(target instanceof EObject) && !(target instanceof WrapperItemProvider)) {
				return StatusUtil.createStatus(IStatus.INFO, 0,
						"Target object must be either an EObject or an WrapperItemProvider.", Activator.getPlugin() //$NON-NLS-1$
								.getSymbolicName(), new RuntimeException());
			}

			TransactionalEditingDomain editingDomain = getEditingDomain(target);
			if (editingDomain == null) {
				return StatusUtil.createStatus(IStatus.ERROR, 0,
						"Target object has no editing domain.", Activator.getPlugin().getBundle().getSymbolicName(), new RuntimeException()); //$NON-NLS-1$
			}

			Object unwrappedTarget = AdapterFactoryEditingDomain.unwrap(target);
			Command command = DragAndDropCommand.create(editingDomain, unwrappedTarget, 0.0f, operations, operation, selectedEObjects);

			if (command.canExecute()) {
				return Status.OK_STATUS;
			} else if (!command.canExecute() && operation == DND.DROP_NONE) {
				Command command2 = DragAndDropCommand.create(editingDomain, unwrappedTarget, 0.0f, operations, operations, selectedEObjects);
				if (command2.canExecute()) {
					return Status.OK_STATUS;
				}
			} else {
				return StatusUtil.createStatus(IStatus.INFO, 0,
						"Cannot execute drop command.", Activator.getPlugin().getBundle().getSymbolicName(), new RuntimeException()); //$NON-NLS-1$
			}
		}
		return StatusUtil.createStatus(IStatus.ERROR, 0,
				"Cannot drop, transfer type is not supported.", Activator.getPlugin().getBundle().getSymbolicName(), new RuntimeException()); //$NON-NLS-1$
	}

	protected IStatus handleDropCopy(Object target, CommonDropAdapter dropAdapter, DropTargetEvent event) {
		TransactionalEditingDomain domain = getEditingDomain(target);
		if (domain == null) {
			return StatusUtil.createStatus(IStatus.ERROR, 0,
					"Target object has no editing domain.", Activator.getPlugin().getBundle().getSymbolicName(), new RuntimeException()); //$NON-NLS-1$
		}
		List<EObject> selectedEObjects = getSelectedEObjects();
		Command command = DragAndDropCommand.create(domain, target, dropAdapter.getCurrentLocation(), event.operations, DND.DROP_COPY,
				selectedEObjects);
		return promptAndExecuteDropCopy(domain, target, command, "Confirm Copy", "Press OK to confirm copy.");
	}

	protected IStatus handleDropMove(Object target, CommonDropAdapter dropAdapter, DropTargetEvent event) {
		TransactionalEditingDomain domain = getEditingDomain(target);
		if (domain == null) {
			return StatusUtil.createStatus(IStatus.ERROR, 0,
					"Target object has no editing domain.", Activator.getPlugin().getBundle().getSymbolicName(), new RuntimeException()); //$NON-NLS-1$
		}

		List<EObject> selectedEObjects = getSelectedEObjects();

		Object unwrappedTarget = AdapterFactoryEditingDomain.unwrap(target);
		Command command = DragAndDropCommand.create(domain, unwrappedTarget, dropAdapter.getCurrentLocation(), event.operations, DND.DROP_MOVE,
				selectedEObjects);

		return promptAndExecuteDropMove(domain, target, command, "Confirm move", "Press OK to confirm move");
	}

	@SuppressWarnings("unchecked")
	protected List<Object> getSelection() {
		ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (selection instanceof IStructuredSelection) {
			return ((IStructuredSelection) selection).toList();
		}
		return Collections.emptyList();
	}

	protected List<EObject> getSelectedEObjects() {
		ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (selection instanceof IStructuredSelection) {
			return getSelectedEObjects((IStructuredSelection) selection);
		}
		return Collections.emptyList();
	}

	protected List<EObject> getSelectedEObjects(IStructuredSelection selection) {
		List<EObject> selectedEObject = new ArrayList<EObject>();

		for (Iterator<?> i = selection.iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof EObject) {
				selectedEObject.add((EObject) o);
			} else if (o instanceof WrapperItemProvider) {
				selectedEObject.add((EObject) ((WrapperItemProvider) o).getValue());
			} else if (o instanceof IAdaptable) {
				IAdaptable a = (IAdaptable) o;
				EObject eo = (EObject) a.getAdapter(EObject.class);
				if (eo != null) {
					selectedEObject.add(eo);
				}
			}
		}
		return selectedEObject;
	}

	protected Object getParent(Object object) {
		return getEditingDomain(object).getParent(object);
	}

	protected Collection<?> getChildren(Object object) {
		return getEditingDomain(object).getChildren(object);
	}

	protected TransactionalEditingDomain getEditingDomain(Object target) {
		return TransactionUtil.getEditingDomain(target instanceof WrapperItemProvider ? ((WrapperItemProvider) target).getValue() : target);
	}

	protected IStatus promptAndExecuteDropCopy(TransactionalEditingDomain domain, Object target, Command command, String title, String msg) {
		return promptAndExecute(domain, command, title, msg);
	}

	protected IStatus promptAndExecuteDropMove(TransactionalEditingDomain domain, Object target, Command command, String title, String msg) {
		// If the dragged objects share a parent... don't ask for confirmation
		if (command instanceof DragAndDropCommand) {
			DragAndDropCommand dndCommand = (DragAndDropCommand) command;
			Object parent = getParent(dndCommand.getOwner());
			Collection<?> children = getChildren(parent);
			if (children.containsAll(dndCommand.getCollection())) {
				return doExecute(domain, dndCommand);
			} else if (target instanceof IWrapperItemProvider) {
				IWrapperItemProvider wrapedTarget = (IWrapperItemProvider) target;
				Collection<?> wrapedChildren = getChildren(wrapedTarget);
				if (wrapedChildren.containsAll(getSelection())) {
					return promptAndExecute(domain, command, "Confirm duplicate", "Press OK to confirm duplicate");
				}
			}
		}
		return promptAndExecute(domain, command, title, msg);
	}

	protected IStatus promptAndExecute(TransactionalEditingDomain domain, Command command, String title, String msg) {
		Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (MessageDialog.openConfirm(parentShell, title, msg)) {
			return doExecute(domain, command);
		}
		return Status.CANCEL_STATUS;
	}

	protected IStatus doExecute(EditingDomain domain, Command command) {
		// If the command can execute...
		if (command.canExecute()) {
			// Execute it
			domain.getCommandStack().execute(command);
		} else {
			// Otherwise, let's call the whole thing off
			command.dispose();
		}
		return Status.OK_STATUS;
	}
}
