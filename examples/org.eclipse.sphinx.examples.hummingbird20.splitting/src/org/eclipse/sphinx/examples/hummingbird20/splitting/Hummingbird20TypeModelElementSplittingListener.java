/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
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
package org.eclipse.sphinx.examples.hummingbird20.splitting;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sphinx.emf.splitting.IModelElementSplittingListener;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;

public class Hummingbird20TypeModelElementSplittingListener implements IModelElementSplittingListener {

	private static final String DEFAULT_COMPONENT_TYPES_TARGET_FILE_NAME = "ComponentTypes.typemodel"; //$NON-NLS-1$
	private static final String DEFAULT_INTERFACES_TARGET_FILE_NAME = "Interfaces.typemodel"; //$NON-NLS-1$

	@Override
	public URI getTargetResourceURI(EObject eObject) {
		if (!(eObject instanceof ComponentType) && !(eObject instanceof Interface)) {
			return null;
		}

		IFile originFile = EcorePlatformUtil.getFile(eObject);
		if (originFile != null) {
			String targetFileName = eObject instanceof ComponentType ? DEFAULT_COMPONENT_TYPES_TARGET_FILE_NAME : DEFAULT_INTERFACES_TARGET_FILE_NAME;
			IPath path = originFile.getFullPath().removeLastSegments(1).append(targetFileName);
			return EcorePlatformUtil.createURI(path);
		}

		return null;
	}
}
