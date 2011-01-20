package org.eclipse.sphinx.emf.ui.actions.providers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.ui.internal.messages.Messages;
import org.eclipse.ui.navigator.ICommonMenuConstants;

public class BasicOpenWithMenuProvider extends BasicActionProvider {

	@Override
	public void fillContextMenu(IMenuManager menu) {

		ISelection selection = getContext().getSelection();
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			return;
		}
		IStructuredSelection sSelection = (IStructuredSelection) selection;
		if (sSelection.size() != 1) {
			return;
		}
		Object obj = sSelection.getFirstElement();
		if (!(obj instanceof EObject)) {
			return;
		}
		if (workbenchPart != null) {
			// Create a menu
			IMenuManager submenu = new MenuManager(Messages.label_openWithMenu);
			submenu.add(new ObjectOpenWithMenu(workbenchPart.getSite().getPage(), (EObject) obj));
			// Add the sub-menu
			menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, submenu);
		}
	}
}
