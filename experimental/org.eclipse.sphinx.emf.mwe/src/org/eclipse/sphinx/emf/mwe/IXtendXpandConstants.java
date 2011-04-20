/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.emf.mwe;

// TODO Convert IXtendXpandConstants into XtendXpandUtil and move getQualifiedName() 
// and getUnderlyingFile() from IWorkspaceResourceLoader/BasicWorkspaceResourceLoader to XtendXpandUtil;
// use XpandXtendUtil in org.eclipse.sphinx.xtendxpand.ui.groups.TemplateGroup#getDefinitionName() and remove 
// redundant method org.eclipse.sphinx.xtendxpand.ui.groups.TemplateGroup#getQualifiedName(IFile, String)
public interface IXtendXpandConstants {

	String NS_DELIMITER = "::"; //$NON-NLS-1$

	String TEMPLATE_EXTENSION = "xpt"; //$NON-NLS-1$

	String EXTENSION_EXTENSION = "ext"; //$NON-NLS-1$

	String CHECK_EXTENSION = "chk"; //$NON-NLS-1$
}
