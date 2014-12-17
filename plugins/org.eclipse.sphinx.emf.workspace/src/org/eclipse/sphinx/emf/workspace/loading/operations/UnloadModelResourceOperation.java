package org.eclipse.sphinx.emf.workspace.loading.operations;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.workspace.internal.messages.Messages;
import org.eclipse.sphinx.emf.workspace.loading.SchedulingRuleFactory;
import org.eclipse.sphinx.platform.operations.AbstractWorkspaceOperation;
import org.eclipse.sphinx.platform.util.ExtendedPlatform;

public class UnloadModelResourceOperation extends AbstractWorkspaceOperation {

	private Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUnload;
	private boolean memoryOptimized;

	public UnloadModelResourceOperation(Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUnload, boolean memoryOptimized) {
		super(Messages.job_unloadingModelResources);
		this.resourcesToUnload = resourcesToUnload;
		this.memoryOptimized = memoryOptimized;
	}

	@Override
	public ISchedulingRule getRule() {
		return new SchedulingRuleFactory().createLoadSchedulingRule(getResourcesToUnload());
	}

	public Map<TransactionalEditingDomain, Collection<Resource>> getResourcesToUnload() {
		return resourcesToUnload;
	}

	public boolean isMemoryOptimized() {
		return memoryOptimized;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		runUnloadModelResources(resourcesToUnload, memoryOptimized, monitor);
	}

	private void runUnloadModelResources(Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUnload, boolean memoryOptimized,
			IProgressMonitor monitor) throws OperationCanceledException {
		Assert.isNotNull(resourcesToUnload);
		SubMonitor progress = SubMonitor.convert(monitor, getResourcesToUnloadCount(resourcesToUnload));
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}

		for (TransactionalEditingDomain editingDomain : resourcesToUnload.keySet()) {
			Collection<Resource> resourcesToUnloadInEditingDomain = resourcesToUnload.get(editingDomain);
			EcorePlatformUtil.unloadResources(editingDomain, resourcesToUnloadInEditingDomain, memoryOptimized,
					progress.newChild(resourcesToUnloadInEditingDomain.size()));

			if (progress.isCanceled()) {
				throw new OperationCanceledException();
			}
		}

		// Perform a full garbage collection
		ExtendedPlatform.performGarbageCollection();
	}

	private int getResourcesToUnloadCount(Map<TransactionalEditingDomain, Collection<Resource>> resourcesToUnload) {
		Assert.isNotNull(resourcesToUnload);

		int count = 0;
		for (TransactionalEditingDomain editingDomain : resourcesToUnload.keySet()) {
			count += resourcesToUnload.get(editingDomain).size();
		}
		return count;
	}
}
