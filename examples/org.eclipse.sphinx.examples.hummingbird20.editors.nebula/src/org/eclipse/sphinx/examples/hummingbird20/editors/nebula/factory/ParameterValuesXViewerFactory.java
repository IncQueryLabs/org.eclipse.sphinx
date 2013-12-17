/**
 * <copyright>
 * 
 * Copyright (c) 2012 itemis and others.
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
package org.eclipse.sphinx.examples.hummingbird20.editors.nebula.factory;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.ExtendedViewerColumn;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.impl.ParameterValueImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

public class ParameterValuesXViewerFactory extends XViewerFactory {

	public final static String COLUMN_NAMESPACE = "parameterValues.xviewer"; //$NON-NLS-1$
	public final static String PARAMETER_NAME_COLUMN_NAME = "Name"; //$NON-NLS-1$
	public final static String PARAMETER_VALUE_COLUMN_NAME = "Value"; //$NON-NLS-1$

	public static ExtendedViewerColumn nameColumn = new ExtendedViewerColumn(
			COLUMN_NAMESPACE + ".name", PARAMETER_NAME_COLUMN_NAME, 200, SWT.LEFT, true, //$NON-NLS-1$ 
			SortDataType.String, true, null);

	public static ExtendedViewerColumn valueColumn = new ExtendedViewerColumn(
			COLUMN_NAMESPACE + ".value", PARAMETER_VALUE_COLUMN_NAME, 200, SWT.LEFT, true, //$NON-NLS-1$ 
			SortDataType.String, true, null);

	public ParameterValuesXViewerFactory() {
		super(COLUMN_NAMESPACE);

		registerColumns(nameColumn, valueColumn);
		nameColumn.addMapEntry(ParameterValueImpl.class, new CellEditDescriptor(Text.class, SWT.BORDER, PARAMETER_NAME_COLUMN_NAME,
				ParameterValueImpl.class));
		valueColumn.addMapEntry(ParameterValueImpl.class, new CellEditDescriptor(Text.class, SWT.BORDER, PARAMETER_VALUE_COLUMN_NAME,
				ParameterValueImpl.class));
	}

	@Override
	public boolean isAdmin() {
		return true;
	}

	@Override
	public boolean isCellGradientOn() {
		return true;
	}
}
