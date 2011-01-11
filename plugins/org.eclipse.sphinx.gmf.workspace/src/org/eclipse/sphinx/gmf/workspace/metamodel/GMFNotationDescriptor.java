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
package org.eclipse.sphinx.gmf.workspace.metamodel;

import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.gmf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.gmf.workspace.util.GMFResourceUtil;

/**
 * Implementation of {@linkplain IMetaModelDescriptor} for meta-model GMF Notation that declares the descriptor
 * identifier and the name-space {@linkplain URI} describing the Notation meta-model.
 */
public class GMFNotationDescriptor extends AbstractMetaModelDescriptor {

	/**
	 * Singleton instance.
	 */
	public static final GMFNotationDescriptor INSTANCE = new GMFNotationDescriptor();

	/**
	 * Constructor.
	 * <p>
	 * Set the identifier of this {@linkplain IMetaModelDescriptor descriptor}:
	 * <code>org.eclipse.gmf.runtime.notation</code>.<br>
	 * Set the package name-space URI of GMF Notation meta-model:
	 * <code>http://www.eclipse.org/gmf/runtime/1.0.2/notation</code>.
	 */
	public GMFNotationDescriptor() {
		super("org.eclipse.gmf.runtime.notation", NotationPackage.eNS_URI); //$NON-NLS-1$ 
	}

	@Override
	public String getDefaultContentTypeId() {
		return GMFResourceUtil.eCONTENT_TYPE;
	}

	@Override
	public String getName() {
		return Messages.label_GMFDiagramNotation;
	}
}
