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
 package org.eclipse.sphinx.emf.serialization.generators.xsd

import java.io.File
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.OperationCanceledException
import org.eclipse.core.runtime.SubMonitor
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.xsd.XSDSchema

class Ecore2XSDGenerator {

	protected Ecore2XSDFactory xsdFactory 
	protected XSDSchema xsdSchema  
	protected File schemaFile
	protected EPackage ecoreModel
	protected String filePathName
	protected URI xsdFileURI	

	new (URI xsdFileURI, File schemaFile,  EPackage ecoreModel) {
		this.schemaFile = schemaFile
		this.ecoreModel = ecoreModel
		this.filePathName = filePathName
		this.xsdFileURI = xsdFileURI
	} 
  
	def void run(IProgressMonitor monitor) {
		val SubMonitor progress = SubMonitor.convert(monitor, 100);
		if (progress.isCanceled()) {
			throw new OperationCanceledException();
		}
		
		if (schemaFile == null) {
			return;
		}	
			
		val ResourceSet resourceSet = new ResourceSetImpl();
			
		// initialize schema: 1.EA Schema
		xsdFactory = createEcore2XSDFactory(); 
		xsdSchema = xsdFactory.initSchema(ecoreModel, resourceSet, monitor);
		
		// convert root package: 2.PackageSchema
		val Ecore2XSDConverter converter = createEcore2XSDConverter(xsdFactory, xsdSchema);
		xsdSchema = converter.doConvertRMFPackageSchema2(ecoreModel, progress.newChild(90));
		
		// Change the namespace to the global namespace
		refineXSDSchemaNamespace(progress.newChild(5));

		saveSchema(xsdFileURI, xsdSchema, resourceSet);
	}
	
	

	def void saveSchema(URI xsdFileURI, XSDSchema xsdSchema, ResourceSet resourceSet){
		var Resource resource = resourceSet.createResource(xsdFileURI);
		resource.getContents().add(xsdSchema);
		resource.save(null);
	}
	
	// This method can be overriden by custom to provide the custom specific generator
	def protected Ecore2XSDConverter createEcore2XSDConverter(Ecore2XSDFactory xsdFactory, XSDSchema xsdSchema){
		return new Ecore2XSDConverter(xsdFactory, xsdSchema)
	}
	
	// This method can be overriden by custom to provide the custom specific generator
	def protected Ecore2XSDFactory createEcore2XSDFactory(){
		return new Ecore2XSDFactory(ecoreModel);
	}
	 
	/**
	 * Change the namespace to the global namespace. 
	 * This method can be overriden by custom
	 */
	def protected void refineXSDSchemaNamespace(IProgressMonitor monitor){
	}

	}