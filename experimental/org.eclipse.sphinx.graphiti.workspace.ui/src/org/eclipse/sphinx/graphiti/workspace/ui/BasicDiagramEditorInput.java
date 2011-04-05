/**
 * <copyright>
 * 
 * Copyright (c) 2008-2011 See4sys and others.
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
package org.eclipse.sphinx.graphiti.workspace.ui;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;

public class BasicDiagramEditorInput extends DiagramEditorInput {

	public BasicDiagramEditorInput(String diagramUriString, TransactionalEditingDomain domain, String providerId) {
		super(diagramUriString, domain, providerId, false);
	}

	public BasicDiagramEditorInput(String diagramUriString, TransactionalEditingDomain domain, String providerId, boolean disposeEditingDomain) {
		super(diagramUriString, domain, providerId, disposeEditingDomain);
	}

	public BasicDiagramEditorInput(URI diagramUri, TransactionalEditingDomain domain, String providerId) {
		this(diagramUri, domain, providerId, false);
	}

	public BasicDiagramEditorInput(URI diagramUri, TransactionalEditingDomain domain, String providerId, boolean disposeEditingDomain) {
		super(diagramUri, domain, providerId, disposeEditingDomain);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

	/**
	 * Returns the factory ID for creating {@link BasicDiagramEditorInput}s from memento(s).
	 * 
	 * @return The ID of the associated factory
	 */
	@Override
	public String getFactoryId() {
		return BasicDiagramEditorFactory.class.getName();
	}
}
