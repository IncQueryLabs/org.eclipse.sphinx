/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
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
package org.eclipse.sphinx.emf.check;

import java.util.Map;

public enum CheckValidationMode {

	FAST_ONLY {
		@Override
		public boolean shouldCheck(CheckType type) {
			return type == CheckType.FAST;
		}

		@Override
		public String toString() {
			return CheckType.FAST.toString();
		}
	},

	NORMAL_ONLY {
		@Override
		public boolean shouldCheck(CheckType type) {
			return type == CheckType.NORMAL;
		}

		@Override
		public String toString() {
			return CheckType.NORMAL.toString();
		}
	},

	EXPENSIVE_ONLY {
		@Override
		public boolean shouldCheck(CheckType type) {
			return type == CheckType.EXPENSIVE;
		}

		@Override
		public String toString() {
			return CheckType.EXPENSIVE.toString();
		}
	},

	NORMAL_AND_FAST {
		@Override
		public boolean shouldCheck(CheckType type) {
			return type == CheckType.NORMAL || type == CheckType.FAST;
		}

		@Override
		public String toString() {
			return CheckType.NORMAL + "|" + CheckType.FAST; //$NON-NLS-1$
		}
	},

	ALL {
		@Override
		public boolean shouldCheck(CheckType type) {
			return true;
		}

		@Override
		public String toString() {
			return "ALL"; //$NON-NLS-1$
		}
	};

	public static CheckValidationMode getFromContext(Map<Object, Object> context) {
		CheckValidationMode mode = CheckValidationMode.ALL;
		if (context != null) {
			Object object2 = context.get(CheckValidationMode.KEY);
			if (object2 instanceof CheckValidationMode) {
				mode = (CheckValidationMode) object2;
			} else if (object2 != null) {
				throw new IllegalArgumentException(
						"Context object for key " + CheckValidationMode.KEY + " should be of Type " + CheckValidationMode.class.getName() //$NON-NLS-1$//$NON-NLS-2$
								+ " but was " + object2.getClass().getName()); //$NON-NLS-1$
			}
		}
		return mode;
	}

	public final static String KEY = "check.mode"; //$NON-NLS-1$

	public abstract boolean shouldCheck(CheckType type);
}