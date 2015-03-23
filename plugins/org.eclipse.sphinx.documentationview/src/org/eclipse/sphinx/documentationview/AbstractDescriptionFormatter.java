/**
 * <copyright>
 *
 * Copyright (c) 2014-2015 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.documentationview;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDescriptionFormatter implements IDescriptionFormatter {

	@Override
	public List<IDescriptionSection> descriptionSections(Object o) {
		List<IDescriptionSection> newArrayList = new ArrayList<IDescriptionSection>();
		try {
			newArrayList.add(new DescriptionSection(formatHeader(o), format(o)));
		} catch (Exception ex) {
			newArrayList.add(new DescriptionSection("Error", "<pre>" + ex.getMessage() + "</pre>"));
		}
		return newArrayList;
	}

	public abstract String format(Object o);

	public abstract String formatHeader(Object o);
}
