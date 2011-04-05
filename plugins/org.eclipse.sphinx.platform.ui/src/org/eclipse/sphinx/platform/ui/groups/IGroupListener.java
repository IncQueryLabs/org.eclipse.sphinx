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
package org.eclipse.sphinx.platform.ui.groups;

import org.eclipse.sphinx.platform.ui.fields.IField;

/**
 * Classes which implement this interface provide a method to deal with change events of dialog group.
 */
public interface IGroupListener {

	/**
	 * Sent when the content of a dialog group has changed.
	 */
	void groupChanged(IField field);
}