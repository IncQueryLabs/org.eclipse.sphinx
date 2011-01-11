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

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sphinx.emf.resource.BasicMigrationExtendedMetaData;
import org.eclipse.sphinx.emf.resource.ValidatingResourceFactoryImpl;
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
public class Hummingbird20ResourceFactoryImpl extends ValidatingResourceFactoryImpl {

	protected ExtendedMetaData extendedMetaData;

	/**
	 * Creates an instance of the resource factory.
	 * 
	 * @generated NOT
	 */
	public Hummingbird20ResourceFactoryImpl() {
		// Configure on-the-fly schema validation during resource loading
		setSchemaPlugin(Activator.getPlugin());

		// Register schema files for current metamodel version
		Map<String, String> nsToSchemaPathMap = getNsToSchemaPathMap();
		nsToSchemaPathMap.put(TypeModel20Package.eNS_URI, "model/TypeModel20XMI.xsd"); //$NON-NLS-1$
		nsToSchemaPathMap.put(InstanceModel20Package.eNS_URI, "model/InstanceModel20XMI.xsd"); //$NON-NLS-1$

		Map<String, String> nsToExternalSchemaResourcePathMap = getNsToExternalSchemaResourcePathMap();
		nsToExternalSchemaResourcePathMap.put(XMIResource.XMI_URI, "model/XMI.xsd"); //$NON-NLS-1$
		nsToExternalSchemaResourcePathMap.put(Common20Package.eNS_URI, "model/Common20XMI.xsd"); //$NON-NLS-1$
		nsToExternalSchemaResourcePathMap.put(TypeModel20Package.eNS_URI, "model/TypeModel20XMI.xsd"); //$NON-NLS-1$

		// Register schema files for compatible metamodel versions
		nsToSchemaPathMap.put(HummingbirdMMDescriptor.BASE_NAMESPACE + "/2.0.0/typemodel", "model/archive/TypeModel200XMI.xsd"); //$NON-NLS-1$ //$NON-NLS-2$
		nsToSchemaPathMap.put(HummingbirdMMDescriptor.BASE_NAMESPACE + "/2.0.0/instancemodel", "model/archive/InstanceModel200XMI.xsd"); //$NON-NLS-1$ //$NON-NLS-2$

		nsToExternalSchemaResourcePathMap.put(HummingbirdMMDescriptor.BASE_NAMESPACE + "/2.0.0/common", "model/archive/Common200XMI.xsd"); //$NON-NLS-1$ //$NON-NLS-2$
		nsToExternalSchemaResourcePathMap.put(HummingbirdMMDescriptor.BASE_NAMESPACE + "/2.0.0/typemodel", "model/archive/TypeModel200XMI.xsd"); //$NON-NLS-1$ //$NON-NLS-2$

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
		result.getDefaultSaveOptions().put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.FALSE);

		activateSchemaValidation(result);

		return result;
	}
} // Hummingbird20ResourceFactoryImpl
