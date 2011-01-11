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
package org.eclipse.sphinx.emf.validation.diagnostic.filters.util;

public enum ConstraintFilterValue {

	WIZARD("WIZARD", 1), PROPERTY_SHEET("PROPERTY_SHEET", 2); //$NON-NLS-1$ //$NON-NLS-2$

	static private ConstraintFilterValue enums[] = { WIZARD, PROPERTY_SHEET };

	private final String literal;
	private final int value;

	private ConstraintFilterValue(String literal, int value) {
		this.literal = literal;
		this.value = value;
	}

	public String literal() {
		return literal;
	}

	public double value() {
		return value;
	}

	static public ConstraintFilterValue convert(String v) {
		for (ConstraintFilterValue current : enums) {
			if (current.literal().equals(v)) {
				return current;
			}
		}
		return null;
	}
}
