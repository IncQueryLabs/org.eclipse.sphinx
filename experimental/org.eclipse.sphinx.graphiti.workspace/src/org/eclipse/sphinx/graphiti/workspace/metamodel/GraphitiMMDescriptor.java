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
package org.eclipse.sphinx.graphiti.workspace.metamodel;

import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.MmPackage;
import org.eclipse.sphinx.emf.metamodel.AbstractMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.graphiti.workspace.internal.messages.Messages;

/**
 * Implementation of {@linkplain IMetaModelDescriptor} for meta-model Graphiti Pictogram that declares the descriptor
 * identifier and the name-space {@linkplain URI} describing the Pictogram meta-model.
 */
public class GraphitiMMDescriptor extends AbstractMetaModelDescriptor {

	/**
	 * Singleton instance.
	 */
	public static final GraphitiMMDescriptor INSTANCE = new GraphitiMMDescriptor();

	/**
	 * The id of the content type for Graphiti diagram files: <tt>org.eclipse.sphinx.graphiti.diagramFile</tt>.
	 */
	public static final String GRAPHITI_DIAGRAM_CONTENT_TYPE_ID = "org.eclipse.sphinx.graphiti.diagramFile"; //$NON-NLS-1$

	/**
	 * The default file extension for Graphiti diagram files: <tt>diagram</tt>.
	 */
	public static final String GRAPHITI_DIAGRAM_DEFAULT_FILE_EXTENSION = "diag"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * <p>
	 * Set the identifier of this {@linkplain IMetaModelDescriptor descriptor}: <code>org.eclipse.graphiti.mm</code>.<br>
	 * Set the package name-space URI of Graphiti Pictogram meta-model: <code>http://eclipse.org/graphiti/mm</code>.
	 */
	public GraphitiMMDescriptor() {
		super("org.eclipse.graphiti.mm", MmPackage.eNS_URI, "(pictograms|algorithms)(/\\w)*"); //$NON-NLS-1$ //$NON-NLS-2$ 
	}

	@Override
	public String getDefaultContentTypeId() {
		return GRAPHITI_DIAGRAM_CONTENT_TYPE_ID;
	}

	@Override
	public String getName() {
		return Messages.label_GraphitiPictogram;
	}
}
