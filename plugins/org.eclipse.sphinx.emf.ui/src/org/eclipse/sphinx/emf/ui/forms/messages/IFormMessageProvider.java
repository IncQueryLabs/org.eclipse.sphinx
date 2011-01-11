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
package org.eclipse.sphinx.emf.ui.forms.messages;

import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EStructuralFeature;

public interface IFormMessageProvider {
	Map<EStructuralFeature, Set<IFormMessage>> getMessages(Object pageInput);
}
