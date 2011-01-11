/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
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
package org.eclipse.sphinx.gmfgen.util;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.gmf.codegen.gmfgen.GenEditorGenerator;
import org.eclipse.gmf.codegen.util.CodegenEmitters;
import org.eclipse.gmf.codegen.util.EmitterSource;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation.Kind;
import org.eclipse.sphinx.emf.metamodel.IMetaModelDescriptor;
import org.eclipse.sphinx.emf.metamodel.MetaModelDescriptorRegistry;

public class GMFCodeGenUtil {
	public GMFCodeGenUtil() {

	}

	public CodegenEmitters getEmitters(GenEditorGenerator genModel) {
		final EmitterSource<GenEditorGenerator, CodegenEmitters> emitterSource = new EmitterSource<GenEditorGenerator, CodegenEmitters>() {
			@Override
			protected CodegenEmitters newEmitters(GenEditorGenerator genModel) {
				// return new CodegenEmitters(!genModel.isDynamicTemplates(), genModel.getTemplateDirectory(),
				// genModel.getModelAccess() != null);
				return new CodegenEmitters(true, genModel.getTemplateDirectory(), genModel.getModelAccess() != null);
			}
		};

		return emitterSource.getEmitters(genModel, genModel.isDynamicTemplates());
	}

	@Operation(contextual = false, kind = Kind.HELPER)
	public static String getMetaModelDescriptorClassName(GenEditorGenerator editorGen) {
		GenModel domainGenModel = editorGen.getDomainGenModel();
		if (domainGenModel != null) {
			GenPackage ecoreGenPackage = domainGenModel.getEcoreGenPackage();
			if (ecoreGenPackage != null) {
				EPackage ecorePackage = ecoreGenPackage.getEcorePackage();
				if (ecorePackage != null) {
					IMetaModelDescriptor descriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(ecorePackage);
					if (descriptor != null) {
						return descriptor.getClass().getName();
					}
					return "MMDescriptor is null"; //$NON-NLS-1$
				}
				return "Ecore Package is null"; //$NON-NLS-1$
			}
			return "Ecore GenPackage is null"; //$NON-NLS-1$
		}
		return "Domain GenModel is null"; //$NON-NLS-1$
	}
}
