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

public abstract class CheckMode {

	public final static String KEY = "check.mode"; //$NON-NLS-1$

	public final static CheckMode FAST_ONLY = new CheckMode() {
		@Override
		public boolean shouldCheck(CheckType type) {
			return type == CheckType.FAST;
		}

		@Override
		public String toString() {
			return CheckType.FAST.toString();
		}
	};

	public final static CheckMode NORMAL_ONLY = new CheckMode() {
		@Override
		public boolean shouldCheck(CheckType type) {
			return type == CheckType.NORMAL;
		}

		@Override
		public String toString() {
			return CheckType.NORMAL.toString();
		}
	};

	public final static CheckMode EXPENSIVE_ONLY = new CheckMode() {
		@Override
		public boolean shouldCheck(CheckType type) {
			return type == CheckType.EXPENSIVE;
		}

		@Override
		public String toString() {
			return CheckType.EXPENSIVE.toString();
		}
	};

	public final static CheckMode NORMAL_AND_FAST = new CheckMode() {
		@Override
		public boolean shouldCheck(CheckType type) {
			return type == CheckType.NORMAL || type == CheckType.FAST;
		}

		@Override
		public String toString() {
			return CheckType.NORMAL + "|" + CheckType.FAST; //$NON-NLS-1$
		}
	};

	public final static CheckMode ALL = new CheckMode() {
		@Override
		public boolean shouldCheck(CheckType type) {
			return true;
		}

		@Override
		public String toString() {
			return "ALL"; //$NON-NLS-1$
		}
	};

	public abstract boolean shouldCheck(CheckType type);

	public static CheckMode getCheckMode(Map<Object, Object> context) {
		CheckMode checkMode = CheckMode.ALL;
		if (context != null) {
			Object object2 = context.get(CheckMode.KEY);
			if (object2 instanceof CheckMode) {
				checkMode = (CheckMode) object2;
			} else if (object2 != null) {
				throw new IllegalArgumentException("Context object for key " + CheckMode.KEY + " should be of Type " + CheckMode.class.getName() //$NON-NLS-1$//$NON-NLS-2$
						+ " but was " + object2.getClass().getName()); //$NON-NLS-1$
			}
		}
		return checkMode;
	}
}