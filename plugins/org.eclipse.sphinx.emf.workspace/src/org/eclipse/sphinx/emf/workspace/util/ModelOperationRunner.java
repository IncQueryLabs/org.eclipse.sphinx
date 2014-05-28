package org.eclipse.sphinx.emf.workspace.util;

import java.util.Map;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.sphinx.emf.saving.SaveIndicatorUtil;
import org.eclipse.sphinx.emf.util.WorkspaceEditingDomainUtil;
import org.eclipse.sphinx.emf.util.WorkspaceTransactionUtil;
import org.eclipse.sphinx.emf.workspace.Activator;
import org.eclipse.sphinx.platform.util.StatusUtil;

public class ModelOperationRunner {

	public static void performModelAccess(Resource affectedResource, Runnable runnable) throws CoreException {
		Assert.isNotNull(runnable);

		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(affectedResource);
		if (editingDomain != null) {
			try {
				editingDomain.runExclusive(runnable);
			} catch (InterruptedException ex) {
				throw new CoreException(StatusUtil.createErrorStatus(Activator.getPlugin(), ex));
			}
		} else {
			runnable.run();
		}
	}

	public static <T> T performModelAccess(Resource affectedResource, RunnableWithResult<T> runnable) throws CoreException {
		Assert.isNotNull(runnable);

		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(affectedResource);
		if (editingDomain != null) {
			try {
				return TransactionUtil.runExclusive(editingDomain, runnable);
			} catch (InterruptedException ex) {
				throw new CoreException(StatusUtil.createErrorStatus(Activator.getPlugin(), ex));
			}
		} else {
			runnable.run();
			return runnable.getResult();
		}
	}

	public static void performModelModification(Resource affectedResource, LabeledRunnable runnable) throws CoreException {
		performModelModification(affectedResource, runnable, true);
	}

	public static void performModelModification(Resource affectedResource, LabeledRunnable runnable, boolean affectsDirtyState) throws CoreException {
		Assert.isNotNull(runnable);

		TransactionalEditingDomain editingDomain = WorkspaceEditingDomainUtil.getEditingDomain(affectedResource);
		if (editingDomain != null) {
			boolean wasDirtyBefore = SaveIndicatorUtil.isDirty(editingDomain, affectedResource);

			try {
				IOperationHistory operationHistory = WorkspaceTransactionUtil.getOperationHistory(editingDomain);
				Map<String, Object> options = WorkspaceTransactionUtil.getDefaultTransactionOptions();
				options.put(Transaction.OPTION_NO_UNDO, affectsDirtyState ? Boolean.FALSE : Boolean.TRUE);
				WorkspaceTransactionUtil.executeInWriteTransaction(editingDomain, runnable, runnable.getLabel(), operationHistory, options, null);
			} catch (OperationCanceledException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new CoreException(StatusUtil.createErrorStatus(Activator.getPlugin(), ex));
			}

			if (!wasDirtyBefore && !affectsDirtyState) {
				SaveIndicatorUtil.unsetDirty(editingDomain, affectedResource);
			}
		} else {
			runnable.run();
		}
	}
}
