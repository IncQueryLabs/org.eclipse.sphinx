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
package org.eclipse.sphinx.emf.validation.stats;

import org.eclipse.sphinx.emf.validation.Activator;
import org.eclipse.sphinx.platform.stats.AbstractPerformanceStats;
import org.eclipse.sphinx.platform.stats.IEventTypeEnumerator;

/**
 * 
 */
public class ValidationPerformanceStats extends AbstractPerformanceStats<ValidationPerformanceStats.ValidationEvent> {

	public static ValidationPerformanceStats INSTANCE = new ValidationPerformanceStats();

	public enum ValidationEvent implements IEventTypeEnumerator {
		EVENT_APPLY_CONSTRAINTS("ApplyContraints"), EVENT_UPDATE_PROBLEM_MARKERS("UpdateProblemMarkers"), EVENT_LABEL_DECORATION("LabelDecoration");
		private String name;

		private ValidationEvent(String EventName) {
			name = EventName;
		}

		public String getName() {
			// TODO Auto-generated method stub
			return name;
		}

	}

	@Override
	protected String getPluginId() {
		return Activator.PLUGIN_ID;
	}
}
