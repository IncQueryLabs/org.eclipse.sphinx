package org.eclipse.sphinx.emf.workspace.ui.internal.views;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.sphinx.emf.workspace.ui.views.ReferencesView;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

public class ReferencesHierarchyTransferDropAdapter extends ViewerDropAdapter implements TransferDropTargetListener {

	private ReferencesView referencesView;

	public ReferencesHierarchyTransferDropAdapter(ReferencesView referencesView, Viewer viewer) {
		super(viewer);
		setExpandEnabled(false);
		setFeedbackEnabled(false);
		this.referencesView = referencesView;
	}

	@Override
	public Transfer getTransfer() {
		return LocalSelectionTransfer.getTransfer();
	}

	@Override
	public boolean isEnabled(DropTargetEvent event) {
		System.out.println("#isEnabled");
		Object target = event.item != null ? event.item.getData() : null;
		if (target == null) {
			return false;
		}
		System.out.println(target.toString());
		return target instanceof EObject;
	}

	@Override
	public boolean performDrop(Object data) {
		if (data instanceof IStructuredSelection) {
			referencesView.setViewInput(((IStructuredSelection) data).getFirstElement());
			return true;
		}
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		System.out.println("#validateDrop");
		System.out.println(target);
		// TODO Auto-generated method stub
		return target instanceof EObject;
	}
}