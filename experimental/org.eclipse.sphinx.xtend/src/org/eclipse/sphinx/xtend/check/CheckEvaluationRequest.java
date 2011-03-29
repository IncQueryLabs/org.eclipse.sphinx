/**
 * <copyright>
 * 
 * Copyright (c) 2011 See4sys and others.
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
package org.eclipse.sphinx.xtend.check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class CheckEvaluationRequest {

	protected static Collection<Object> getAllContents(Object modelObject) {
		Assert.isNotNull(modelObject);

		Collection<Object> modelObjects = new ArrayList<Object>();
		modelObjects.add(modelObject);
		if (modelObject instanceof EObject) {
			TreeIterator<EObject> eAllContents = ((EObject) modelObject).eAllContents();
			while (eAllContents.hasNext()) {
				EObject element = eAllContents.next();
				modelObjects.add(element);
			}
		}
		return modelObjects;
	}

	private Collection<IFile> checkFiles;
	private Collection<Object> modelObjects;

	public CheckEvaluationRequest(IFile checkFile, Object modelRootObject) {
		this(Collections.singleton(checkFile), modelRootObject);
	}

	public CheckEvaluationRequest(Collection<IFile> checkFiles, Object modelRootObject) {
		this(checkFiles, getAllContents(modelRootObject));
	}

	public CheckEvaluationRequest(IFile checkFile, Collection<Object> modelObjects) {
		this(Collections.singleton(checkFile), modelObjects);
	}

	public CheckEvaluationRequest(Collection<IFile> checkFiles, Collection<Object> modelObjects) {
		Assert.isNotNull(checkFiles);

		this.checkFiles = checkFiles;
		this.modelObjects = modelObjects;
	}

	public Collection<IFile> getCheckFiles() {
		return checkFiles;
	}

	public Collection<Object> getModelObjects() {
		return modelObjects;
	}

	public Object getModelRootObject() {
		if (modelObjects != null) {
			Object object = modelObjects.iterator().next();
			if (object instanceof EObject) {
				return EcoreUtil.getRootContainer((EObject) object);
			}
		}
		return null;
	}
}
