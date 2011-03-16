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
package org.eclipse.sphinx.examples.hummingbird20.diagram.graphiti.features;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.examples.hummingbird20.Hummingbird20MMDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Factory;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Graphiti feature for adding Hummingbird 2.0 {@link ComponentType} elements.
 */
public class CreateComponentTypeFeature extends AbstractCreateFeature {

	private static final String TITLE = "Create " + TypeModel20Package.eINSTANCE.getComponentType().getName(); //$NON-NLS-1$
	private static final String USER_QUESTION = "Enter new " + TypeModel20Package.eINSTANCE.getComponentType().getName() + " name"; //$NON-NLS-1$ //$NON-NLS-2$

	public CreateComponentTypeFeature(IFeatureProvider fp) {
		// Set name and description of the creation feature
		super(fp, TypeModel20Package.eINSTANCE.getComponentType().getName(), "Create " + TypeModel20Package.eINSTANCE.getComponentType().getName()); //$NON-NLS-1$
	}

	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	public Object[] create(ICreateContext context) {
		// Ask user for ComponentType name
		String newComponentTypeName = askString(TITLE, USER_QUESTION, ""); //$NON-NLS-1$
		if (newComponentTypeName == null || newComponentTypeName.trim().length() == 0) {
			return EMPTY;
		}

		// Create ComponentType
		ComponentType newComponentType = TypeModel20Factory.eINSTANCE.createComponentType();

		// Add model element to resource
		// We add the model element to the resource of the diagram for
		// simplicity's sake. Normally, a customer would use its own
		// model persistence layer for storing the business model separately.
		// getDiagram().eResource().getContents().add(newComponentType);
		// newComponentType.setName(newComponentTypeName);

		newComponentType.setName(newComponentTypeName);

		TransactionalEditingDomain editingDomain = getDiagramEditor().getEditingDomain();
		IPath path = EcorePlatformUtil.createPath(getDiagram().eResource().getURI());
		path = path.removeFileExtension().addFileExtension(TypeModel20Package.eNAME);
		EcorePlatformUtil.saveNewModelResource(editingDomain, path, Hummingbird20MMDescriptor.INSTANCE.getDefaultContentTypeId(), newComponentType,
				false, null);

		// Do the add
		addGraphicalRepresentation(context, newComponentType);

		// Return newly created component type
		return new Object[] { newComponentType };
	}

	/**
	 * Opens an simple input dialog with OK and Cancel buttons.
	 * <p>
	 * 
	 * @param dialogTitle
	 *            the dialog title, or <code>null</code> if none
	 * @param dialogMessage
	 *            the dialog message, or <code>null</code> if none
	 * @param initialValue
	 *            the initial input value, or <code>null</code> if none (equivalent to the empty string)
	 * @return the string
	 */
	public static String askString(String dialogTitle, String dialogMessage, String initialValue) {
		String ret = null;
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		InputDialog inputDialog = new InputDialog(shell, dialogTitle, dialogMessage, initialValue, null);
		int retDialog = inputDialog.open();
		if (retDialog == Window.OK) {
			ret = inputDialog.getValue();
		}
		return ret;
	}
}
