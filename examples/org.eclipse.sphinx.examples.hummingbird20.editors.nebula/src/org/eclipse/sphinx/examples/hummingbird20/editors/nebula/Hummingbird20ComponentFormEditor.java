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
package org.eclipse.sphinx.examples.hummingbird20.editors.nebula;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sphinx.emf.editors.forms.BasicTransactionalFormEditor;
import org.eclipse.sphinx.emf.ui.util.EcoreUIUtil;
import org.eclipse.sphinx.examples.hummingbird20.editors.nebula.internal.Activator;
import org.eclipse.sphinx.examples.hummingbird20.editors.nebula.pages.EditableParameterValuesOverviewPage;
import org.eclipse.sphinx.examples.hummingbird20.editors.nebula.pages.GenericParameterValuesOverviewPage;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class Hummingbird20ComponentFormEditor extends BasicTransactionalFormEditor {

	@Override
	protected void addPages() {
		try {
			Object input = getModelRoot();
			if (input instanceof Component) {
				addPage(new GenericParameterValuesOverviewPage(this));
				addPage(new EditableParameterValuesOverviewPage(this));
			}
		} catch (PartInitException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}

	@Override
	protected Image getEditorInputImage() {
		IFile file = EcoreUIUtil.getFileFromEditorInput(getEditorInput());
		if (file != null) {
			IContentType contentType = IDE.getContentType(file);
			ImageDescriptor imageDescriptor = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(file.getName(), contentType);
			return ExtendedImageRegistry.getInstance().getImage(imageDescriptor);
		}

		return super.getEditorInputImage();
	}

	@Override
	public String getTitleToolTip() {
		// Has editor been opened on some object?
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof URIEditorInput) {
			// Make sure that tool tip yields workspace-relative (but not platform:/resource) URI to object
			URI uri = EcoreUIUtil.getURIFromEditorInput(editorInput);
			if (uri.isPlatform()) {
				String path = uri.toPlatformString(true);
				return new Path(path).makeRelative().toString();
			}
		}

		return super.getTitleToolTip();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
}
