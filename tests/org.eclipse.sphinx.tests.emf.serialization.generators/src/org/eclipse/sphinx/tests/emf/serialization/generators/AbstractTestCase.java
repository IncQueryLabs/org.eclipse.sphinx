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
package org.eclipse.sphinx.tests.emf.serialization.generators;

import static org.junit.Assert.assertSame;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.sphinx.emf.serialization.XMLPersistenceMappingResourceFactoryImpl;
import org.eclipse.sphinx.emf.serialization.XMLPersistenceMappingResourceSetImpl;
import org.eclipse.sphinx.emf.serialization.generators.xsd.Ecore2XSDGenerator;
import org.eclipse.sphinx.tests.emf.serialization.generators.model.ModelBuilder;
import org.eclipse.sphinx.testutils.EcoreEqualityAssert;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

@SuppressWarnings("nls")
public abstract class AbstractTestCase {
	public static final String WORKING_DIR_NAME = "working-dir";
	public static final String ADDITIONAL_SCHEMA_FOLDER_NAME = "resources/schema/";
	public static final String MODEL_FILE_EXTESNION = "nodes";

	public static final boolean SKIP_SCHEMA_VALIDATION = true;

	protected void validate(List<EPackage> metamodel, EObject model, String modelName, boolean skipSchemaValidation) throws Exception {

		// write metamodel
		ResourceSet metamodelResourceSet = new ResourceSetImpl();
		Resource metamodelResource;
		for (int i = 0; i < metamodel.size(); i++) {
			metamodelResource = new EcoreResourceFactoryImpl().createResource(URI.createURI(getMetaModelFileName(modelName + (i + 1))));
			metamodelResource.getContents().add(metamodel.get(i));
			metamodelResourceSet.getResources().add(metamodelResource);
		}

		for (int i = 0; i < metamodel.size(); i++) {
			metamodelResourceSet.getResources().get(i).save(null);
		}

		// save instance
		Resource modelSaveResource = new XMLPersistenceMappingResourceFactoryImpl().createResource(URI.createURI(getModelFileName(modelName)));
		modelSaveResource.getContents().add(model);
		modelSaveResource.save(null);

		// load instance
		ResourceSet resourceSet = new XMLPersistenceMappingResourceSetImpl();
		for (int i = 0; i < metamodel.size(); i++) {
			EPackage ePackage = metamodel.get(i);
			resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		}
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(MODEL_FILE_EXTESNION, new XMLPersistenceMappingResourceFactoryImpl());
		Resource modelLoadResource = resourceSet.getResource(URI.createURI(getModelFileName(modelName)), true);
		EObject loadedModel = modelLoadResource.getContents().get(0);

		// compare
		EcoreEqualityAssert.assertEquals(model, loadedModel);

		// check load problems
		assertSame(0, modelLoadResource.getErrors().size());
		assertSame(0, modelLoadResource.getWarnings().size());

		// generate schema
		for (int i = 0; i < metamodel.size(); i++) {
			URI xsdFileURI = URI.createURI(getSchemaFileName(metamodel.get(i).getName()));
			File schemaFile = new File(getSchemaFileName(metamodel.get(i).getName()));
			final Ecore2XSDGenerator ecore2XSDGenerator = new Ecore2XSDGenerator(xsdFileURI, schemaFile, metamodel.get(i));
			ecore2XSDGenerator.run(new NullProgressMonitor());
		}

		if (!skipSchemaValidation) {
			// validate instance
			validateAgainstSchema(getModelFileName(modelName), getSchemaFileName(metamodel.get(0).getName()));
		} else {
			System.out.println("Skipping schema validation since feature is not yet implemented in XSD generator: " + modelName);
		}
	}

	protected String getModelFileName(String modelName) {
		return getWorkingDirName() + modelName + "." + MODEL_FILE_EXTESNION;
	}

	protected String getMetaModelFileName(String modelName) {
		return getWorkingDirName() + modelName + ".ecore";
	}

	protected String getSchemaFileName(String modelName) {
		return getWorkingDirName() + modelName + ".xsd";
	}

	protected String getWorkingDirName() {
		return "file://" + getAbsoluteFileName(WORKING_DIR_NAME + "/" + this.getClass().getName()) + "\\";
	}

	protected String getRelativeWorkingDirName() {
		return WORKING_DIR_NAME + "/" + this.getClass().getName() + "/";
	}

	protected String getAbsoluteFileName(String fileName) {
		File file = new File(fileName);
		return file.getAbsolutePath();
	}

	public class Input implements LSInput {

		private String publicId;

		private String systemId;

		@Override
		public String getPublicId() {
			return publicId;
		}

		@Override
		public void setPublicId(String publicId) {
			this.publicId = publicId;
		}

		@Override
		public String getBaseURI() {
			return null;
		}

		@Override
		public InputStream getByteStream() {
			return null;
		}

		@Override
		public boolean getCertifiedText() {
			return false;
		}

		@Override
		public Reader getCharacterStream() {
			return null;
		}

		@Override
		public String getEncoding() {
			return null;
		}

		@Override
		public String getStringData() {
			synchronized (inputStream) {
				try {
					byte[] input = new byte[inputStream.available()];
					inputStream.read(input);
					String contents = new String(input);
					return contents;
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Exception " + e);
					return null;
				}
			}
		}

		@Override
		public void setBaseURI(String baseURI) {
		}

		@Override
		public void setByteStream(InputStream byteStream) {
		}

		@Override
		public void setCertifiedText(boolean certifiedText) {
		}

		@Override
		public void setCharacterStream(Reader characterStream) {
		}

		@Override
		public void setEncoding(String encoding) {
		}

		@Override
		public void setStringData(String stringData) {
		}

		@Override
		public String getSystemId() {
			return systemId;
		}

		@Override
		public void setSystemId(String systemId) {
			this.systemId = systemId;
		}

		public BufferedInputStream getInputStream() {
			return inputStream;
		}

		public void setInputStream(BufferedInputStream inputStream) {
			this.inputStream = inputStream;
		}

		private BufferedInputStream inputStream;

		public Input(String publicId, String sysId, InputStream input) {
			this.publicId = publicId;
			systemId = sysId;
			inputStream = new BufferedInputStream(input);
		}
	}

	protected void validateAgainstSchema(String filename, final String schemaFilePath) throws Exception {

		StreamSource[] schemaDocuments = new StreamSource[] { new StreamSource(schemaFilePath) };
		Source instanceDocument = new StreamSource(filename);

		// the resolver is required in order find imported schema files
		LSResourceResolver resolver = new LSResourceResolver() {

			@Override
			public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
				LSInput input = null;
				String schemaFileName;
				if (null != systemId) {
					int slashIndex = systemId.lastIndexOf("/");
					if (-1 == slashIndex) {
						schemaFileName = systemId;
					} else {
						schemaFileName = systemId.substring(slashIndex);
					}

					try {
						String path = getRelativeWorkingDirName() + schemaFileName;
						InputStream inputStream = new FileInputStream(path);
						input = new Input(publicId, systemId, inputStream);
					} catch (FileNotFoundException e) {
						try {
							InputStream inputStream = new FileInputStream(ADDITIONAL_SCHEMA_FOLDER_NAME + schemaFileName);
							input = new Input(publicId, systemId, inputStream);
						} catch (FileNotFoundException ex) {
							ex.printStackTrace();
						}
					}
				}
				return input;
			}
		};

		SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		sf.setResourceResolver(resolver);
		Schema s = sf.newSchema(schemaDocuments);
		Validator v = s.newValidator();
		v.validate(instanceDocument);
	}

	protected String getName(int persistenceMappingStrategy, boolean isMany) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getClass().getSimpleName());
		buffer.append(toBinaryString(persistenceMappingStrategy));
		buffer.append(isMany ? "_Many" : "_Single");
		return buffer.toString();
	}

	protected String toBinaryString(int serializationStructure) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getBinaryString(serializationStructure, 8));
		buffer.append(getBinaryString(serializationStructure, 4));
		buffer.append(getBinaryString(serializationStructure, 2));
		buffer.append(getBinaryString(serializationStructure, 1));

		return buffer.toString();
	}

	protected String getBinaryString(int serializationStructure, int mask) {
		if (mask == (serializationStructure & mask)) {
			return "1";
		} else {
			return "0";
		}
	}

	protected void runTest(int persistenceMappingStrategy, boolean isMany) throws Exception {
		runTest(persistenceMappingStrategy, isMany, false);
	}

	protected void runTest(int persistenceMappingStrategy, boolean isMany, boolean skipSchemaValidation) throws Exception {
		String name = getName(persistenceMappingStrategy, isMany);
		ModelBuilder modelBuilder = getModelBuilder(getName(persistenceMappingStrategy, isMany), persistenceMappingStrategy);
		if (isMany) {
			List<EPackage> manyMetamodel = modelBuilder.getManyMetaModel();
			EObject manyModel = modelBuilder.getManyModel(3);
			validate(manyMetamodel, manyModel, name, skipSchemaValidation);
		} else {
			List<EPackage> singleMetamodel = modelBuilder.getSingleMetaModel();
			EObject singleModel = modelBuilder.getSingleModel(1);
			validate(singleMetamodel, singleModel, name, skipSchemaValidation);
		}
	}

	abstract protected ModelBuilder getModelBuilder(String name, int persistenceMappingStrategy);

}
