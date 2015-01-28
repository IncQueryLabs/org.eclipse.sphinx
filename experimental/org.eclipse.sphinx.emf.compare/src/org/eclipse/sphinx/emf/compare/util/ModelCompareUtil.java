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
package org.eclipse.sphinx.emf.compare.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.command.ICompareCommandStack;
import org.eclipse.emf.compare.command.impl.CompareCommandStack;
import org.eclipse.emf.compare.domain.ICompareEditingDomain;
import org.eclipse.emf.compare.domain.impl.EMFCompareEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.compare.domain.DelegatingEMFCompareEditingDomain;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;

public final class ModelCompareUtil {

	private ModelCompareUtil() {
	}

	public static ICompareEditingDomain createEMFCompareEditingDomain(final Object left, final Object right, Notifier origin) {
		if (left instanceof IFile && right instanceof IFile) {
			return new DelegatingEMFCompareEditingDomain();
		}

		if (!(left instanceof Notifier) || !(right instanceof Notifier)) {
			return null;
		}

		EditingDomain delegatingEditingDomain = getEditingDomain(left, right);
		CommandStack delegatingCommandStack = null;
		if (delegatingEditingDomain != null) {
			delegatingCommandStack = delegatingEditingDomain.getCommandStack();
		}

		if (delegatingCommandStack == null) {
			return EMFCompareEditingDomain.create((Notifier) left, (Notifier) right, origin);
		} else {
			return EMFCompareEditingDomain.create((Notifier) left, (Notifier) right, origin, delegatingCommandStack);
		}
	}

	public static EditingDomain getEditingDomain(Object left, Object right) {
		EditingDomain editingDomain = null;
		TransactionalEditingDomain leftEditingDomain = WorkspaceEditingDomainUtil.getEditingDomain(left);
		EditingDomain rightEditingDomain = WorkspaceEditingDomainUtil.getEditingDomain(right);
		if (leftEditingDomain != null) {
			editingDomain = leftEditingDomain;
		} else if (rightEditingDomain != null) {
			editingDomain = rightEditingDomain;
		}
		return editingDomain;
	}

	public static ICompareCommandStack createCompareCommandStack(final IFile left, final IFile right, Notifier origin) {
		EditingDomain delegatingEditingDomain = getEditingDomain(left, right);
		CommandStack delegatingCommandStack = delegatingEditingDomain != null ? delegatingEditingDomain.getCommandStack() : null;

		return delegatingCommandStack != null ? new CompareCommandStack(delegatingCommandStack) : new CompareCommandStack(new BasicCommandStack());
	}
}
