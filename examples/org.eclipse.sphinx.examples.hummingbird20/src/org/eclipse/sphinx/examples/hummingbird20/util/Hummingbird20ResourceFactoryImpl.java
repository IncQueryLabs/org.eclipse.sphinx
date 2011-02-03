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
package org.eclipse.sphinx.examples.hummingbird20.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import org.apache.xerces.impl.Constants;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sphinx.emf.resource.BasicMigrationExtendedMetaData;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.SchemaLocationURIHandler;
import org.eclipse.sphinx.examples.hummingbird.ide.metamodel.HummingbirdMMDescriptor;
import org.eclipse.sphinx.examples.hummingbird20.Activator;
import org.eclipse.sphinx.examples.hummingbird20.common.Common20Package;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.TypeModel20Package;

/**
 * The <b>Resource Factory</b> associated with the package.
 * 
 * @see org.eclipse.sphinx.examples.hummingbird20.util.Hummingbird20ResourceImpl
 * @generated NOT
 */
public class Hummingbird20ResourceFactoryImpl extends ResourceFactoryImpl {

	protected ExtendedMetaData extendedMetaData;

	protected Map<String, String> schemaLocationCatalog;

	protected SchemaLocationURIHandler schemaLocationURIHandler;

	/**
	 * Creates an instance of the resource factory.
	 * 
	 * @generated NOT
	 */
	public Hummingbird20ResourceFactoryImpl() {
		// Configure on-the-fly schema validation during resource loading
		schemaLocationCatalog = new HashMap<String, String>();
		schemaLocationCatalog.put(Common20Package.eNS_URI, "Common20XMI.xsd"); //$NON-NLS-1$
		schemaLocationCatalog.put(TypeModel20Package.eNS_URI, "TypeModel20XMI.xsd"); //$NON-NLS-1$
		schemaLocationCatalog.put(InstanceModel20Package.eNS_URI, "InstanceModel20XMI.xsd"); //$NON-NLS-1$
		schemaLocationCatalog.put(XMIResource.XMI_URI, "XMI.xsd"); //$NON-NLS-1$
		schemaLocationCatalog.put(HummingbirdMMDescriptor.BASE_NAMESPACE + "/2.0.0/common", "Common200XMI.xsd"); //$NON-NLS-1$ //$NON-NLS-2$
		schemaLocationCatalog.put(HummingbirdMMDescriptor.BASE_NAMESPACE + "/2.0.0/typemodel", "TypeModel200XMI.xsd"); //$NON-NLS-1$ //$NON-NLS-2$
		schemaLocationCatalog.put(HummingbirdMMDescriptor.BASE_NAMESPACE + "/2.0.0/instancemodel", "InstanceModel200XMI.xsd"); //$NON-NLS-1$ //$NON-NLS-2$

		schemaLocationURIHandler = new SchemaLocationURIHandler();
		schemaLocationURIHandler.addSchemaLocationBaseURI(Activator.getPlugin(), "model"); //$NON-NLS-1$
		schemaLocationURIHandler.addSchemaLocationBaseURI(Activator.getPlugin(), "model/archive"); //$NON-NLS-1$ 

		// Create and initialize migration-enabled extended meta data enabling Hummingbird resources whose version is
		// not the same but compatible with that of current Hummingbird metamodel implementation to be loaded
		extendedMetaData = new BasicMigrationExtendedMetaData(new EPackageRegistryImpl(EPackage.Registry.INSTANCE));

		// Map relevant EPackages to their namespaces in current metamodel version
		extendedMetaData.putPackage(TypeModel20Package.eNS_URI, TypeModel20Package.eINSTANCE);
		extendedMetaData.putPackage(InstanceModel20Package.eNS_URI, InstanceModel20Package.eINSTANCE);

		// Map relevant EPackages to their namespaces in compatible metamodel versions
		extendedMetaData.putPackage(HummingbirdMMDescriptor.BASE_NAMESPACE + "/2.0.0/typemodel", TypeModel20Package.eINSTANCE); //$NON-NLS-1$
		extendedMetaData.putPackage(HummingbirdMMDescriptor.BASE_NAMESPACE + "/2.0.0/instancemodel", InstanceModel20Package.eINSTANCE); //$NON-NLS-1$
	}

	/**
	 * Creates an instance of the resource.
	 * 
	 * @generated NOT
	 */
	@Override
	public Resource createResource(URI uri) {
		XMIResource result = new Hummingbird20ResourceImpl(uri);

		result.getDefaultLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
		result.getDefaultSaveOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);

		result.getDefaultLoadOptions().put(XMLResource.OPTION_USE_LEXICAL_HANDLER, Boolean.TRUE);

		result.getDefaultSaveOptions().put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
		result.getDefaultSaveOptions().put(ExtendedResource.OPTION_SCHEMA_LOCATION_CATALOG, schemaLocationCatalog);
		result.getDefaultLoadOptions().put(XMLResource.OPTION_URI_HANDLER, schemaLocationURIHandler);
		result.getDefaultSaveOptions().put(XMLResource.OPTION_URI_HANDLER, schemaLocationURIHandler);

		Map<String, Object> parserProperties = new HashMap<String, Object>();
		parserProperties.put(Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_LANGUAGE, XMLConstants.W3C_XML_SCHEMA_NS_URI);
		result.getDefaultLoadOptions().put(XMLResource.OPTION_PARSER_PROPERTIES, parserProperties);

		return result;
	}
} // Hummingbird20ResourceFactoryImpl
