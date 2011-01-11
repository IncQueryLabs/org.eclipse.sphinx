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
package org.eclipse.sphinx.emf.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.ProcessingInstruction;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;
import org.eclipse.sphinx.platform.util.ReflectUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * An extended {@link XMLSave} implementation that provides support for on-the-fly migration of instances of newer
 * metamodel implementations to older {@link XMLResource resource}s using {@link IModelConverter model converter}s.
 * <p>
 * The {@link IModelConverter model converter} to be used for on-the-fly resource migration must be contributed to the
 * <code>org.eclipse.sphinx.emf.modelConverters</code> extension point.
 * </p>
 */
public class ExtendedXMLSaveImpl extends XMLSaveImpl {

	/**
	 * The save options.
	 */
	protected Map<?, ?> options;

	/*
	 * @see XMLSaveImpl#XMLSaveImpl(XMLHelper)
	 */
	public ExtendedXMLSaveImpl(XMLHelper helper) {
		super(helper);
	}

	/*
	 * @see XMLSaveImpl#XMLSaveImpl(Map, XMLHelper, String)
	 */
	public ExtendedXMLSaveImpl(Map<?, ?> options, XMLHelper helper, String encoding) {
		super(options, helper, encoding);
	}

	/*
	 * @see XMLSaveImpl#XMLSaveImpl(Map, XMLHelper, String, String)
	 */
	public ExtendedXMLSaveImpl(Map<?, ?> options, XMLHelper helper, String encoding, String xmlVersion) {
		super(options, helper, encoding, xmlVersion);
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#save(org.eclipse.emf.ecore.xmi.XMLResource, java.io.Writer,
	 * java.util.Map)
	 */
	@Override
	public void save(XMLResource resource, Writer writer, Map<?, ?> options) throws IOException {
		this.options = options;
		super.save(resource, writer, options);
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#save(org.eclipse.emf.ecore.xmi.XMLResource, java.io.OutputStream,
	 * java.util.Map)
	 */
	@Override
	public void save(XMLResource resource, OutputStream outputStream, Map<?, ?> options) throws IOException {
		this.options = options;

		if (outputStream instanceof URIConverter.Writeable) {
			URIConverter.Writeable writeable = (URIConverter.Writeable) outputStream;
			resource.setEncoding(writeable.getEncoding());
			save(resource, writeable.asWriter(), options);
			return;
		}
		xmlResource = resource;
		init(resource, options);
		@SuppressWarnings("unchecked")
		List<? extends EObject> contents = roots = (List<? extends EObject>) options.get(XMLResource.OPTION_ROOT_OBJECTS);
		if (contents == null) {
			contents = resource.getContents();
		}
		// FIXME File bug to EMF: NPE is raised when contents is empty
		if (contents.size() > 0) {
			traverse(contents);
		}

		// If an applicable model converter is around let it migrate and save the document
		boolean didConvert = false;
		IModelConverter applicableConverter = ModelConverterRegistry.INSTANCE.getSaveConverter(xmlResource, options);
		if (applicableConverter != null) {
			try {
				applicableConverter.convertSave(doc, flushThreshold, xmlResource.getURI(), outputStream, encoding, helper, options);
				didConvert = true;
			} catch (Exception ex) {
				PlatformLogUtil.logAsError(Activator.getDefault(), ex);
			} finally {
				applicableConverter.dispose();
			}
		}

		if (!didConvert) {
			if ("US-ASCII".equals(encoding) || "ASCII".equals(encoding)) { //$NON-NLS-1$//$NON-NLS-2$
				writeAscii(outputStream);
				outputStream.flush();
			} else {
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, helper.getJavaEncoding(encoding));
				write((Writer) outputStreamWriter);
				outputStreamWriter.flush();
			}
		}

		endSave(contents);
		xmlResource = null;
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#traverse(java.util.List)
	 */
	@Override
	public void traverse(List<? extends EObject> contents) {
		super.traverse(contents);

		// If an applicable model converter is around let it complement the root element attributes
		IModelConverter applicableConverter = ModelConverterRegistry.INSTANCE.getSaveConverter(xmlResource, options);
		if (applicableConverter != null) {
			try {
				applicableConverter.addExtraAttributesToSavedRootElement(doc, options);
			} catch (Exception ex) {
				PlatformLogUtil.logAsError(Activator.getDefault(), ex);
			} finally {
				applicableConverter.dispose();
			}
		}
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#addNamespaceDeclarations()
	 */
	@Override
	protected void addNamespaceDeclarations() {
		EPackage noNamespacePackage = helper.getNoNamespacePackage();
		EPackage[] packages = helper.packages();
		StringBuffer buffer = getBuffer();
		if (buffer == null) {
			buffer = new StringBuffer();
		}
		buffer.setLength(0);
		StringBuffer xsiSchemaLocation = buffer;
		String xsiNoNamespaceSchemaLocation = null;
		if (declareSchemaLocation) {
			Map<String, String> handledBySchemaLocationMap = Collections.emptyMap();

			if (extendedMetaData != null) {
				Resource resource = helper.getResource();
				if (resource != null && resource.getContents().size() >= 1) {
					EObject root = getSchemaLocationRoot(resource.getContents().get(0));
					EClass eClass = root.eClass();

					EReference xsiSchemaLocationMapFeature = extendedMetaData.getXSISchemaLocationMapFeature(eClass);
					if (xsiSchemaLocationMapFeature != null) {
						@SuppressWarnings("unchecked")
						EMap<String, String> xsiSchemaLocationMap = (EMap<String, String>) root.eGet(xsiSchemaLocationMapFeature);
						if (!xsiSchemaLocationMap.isEmpty()) {
							handledBySchemaLocationMap = xsiSchemaLocationMap.map();
							declareXSI = true;
							for (Map.Entry<String, String> entry : xsiSchemaLocationMap.entrySet()) {
								String namespace = entry.getKey();
								URI location = URI.createURI(entry.getValue());
								if (namespace == null) {
									xsiNoNamespaceSchemaLocation = helper.deresolve(location).toString();
								} else {
									if (xsiSchemaLocation.length() > 0) {
										xsiSchemaLocation.append(' ');
									}
									xsiSchemaLocation.append(namespace);
									xsiSchemaLocation.append(' ');
									xsiSchemaLocation.append(helper.deresolve(location).toString());
								}
							}
						}
					}
				}
			}

			for (EPackage ePackage : packages) {
				String javaImplementationLocation = null;
				if (declareSchemaLocationImplementation) {
					// First try to see if this package's implementation class has an eInstance.
					//
					try {
						Field field = ePackage.getClass().getField("eINSTANCE"); //$NON-NLS-1$
						javaImplementationLocation = "java://" + field.getDeclaringClass().getName(); //$NON-NLS-1$
					} catch (Exception exception) {
						// If there is no field, then we can't do this.
					}
				}

				if (noNamespacePackage == ePackage) {
					if (ePackage.eResource() != null && !handledBySchemaLocationMap.containsKey(null)) {
						declareXSI = true;
						if (javaImplementationLocation != null) {
							xsiNoNamespaceSchemaLocation = javaImplementationLocation;
						} else {
							xsiNoNamespaceSchemaLocation = helper.getHREF(ePackage);
							if (xsiNoNamespaceSchemaLocation != null && xsiNoNamespaceSchemaLocation.endsWith("#/")) { //$NON-NLS-1$
								xsiNoNamespaceSchemaLocation = xsiNoNamespaceSchemaLocation.substring(0, xsiNoNamespaceSchemaLocation.length() - 2);
							}
						}
					}
				} else {
					Resource resource = ePackage.eResource();
					if (resource != null) {
						String nsURI = extendedMetaData == null ? ePackage.getNsURI() : extendedMetaData.getNamespace(ePackage);
						if (!handledBySchemaLocationMap.containsKey(nsURI)) {
							URI uri = resource.getURI();
							if (javaImplementationLocation != null || (uri == null ? nsURI != null : !uri.toString().equals(nsURI))) {
								declareXSI = true;
								if (xsiSchemaLocation.length() > 0) {
									xsiSchemaLocation.append(' ');
								}
								xsiSchemaLocation.append(nsURI);
								xsiSchemaLocation.append(' ');

								String location = javaImplementationLocation == null ? helper.getHREF(ePackage) : javaImplementationLocation;
								location = convertURI(location);
								if (location.endsWith("#/")) { //$NON-NLS-1$
									location = location.substring(0, location.length() - 2);
									if (uri != null && uri.hasFragment()) {
										location += "#" + uri.fragment(); //$NON-NLS-1$
									}
								}
								xsiSchemaLocation.append(location);
							}
						}
					}
				}
			}
		}

		if (declareXSI) {
			if (!toDOM) {
				doc.addAttribute(XSI_XMLNS, XMLResource.XSI_URI);
			} else {
				((Element) currentNode).setAttributeNS(ExtendedMetaData.XMLNS_URI, XSI_XMLNS, XMLResource.XSI_URI);
			}
		}

		// If an applicable model converter is around use its resource version namespace for substituting the metamodel
		// version namespace
		String mmNsURIBase = null;
		String resourceNsURIBase = null;
		IModelConverter applicableConverter = ModelConverterRegistry.INSTANCE.getSaveConverter(xmlResource, options);
		if (applicableConverter != null) {
			try {
				mmNsURIBase = applicableConverter.getMetaModelVersionDescriptor().getNamespace();
				resourceNsURIBase = applicableConverter.getResourceVersionDescriptor().getNamespace();
			} catch (Exception ex) {
				PlatformLogUtil.logAsError(Activator.getDefault(), ex);
			} finally {
				applicableConverter.dispose();
			}
		}

		for (EPackage package1 : packages) {
			EPackage ePackage = package1;
			if (ePackage != noNamespacePackage && ePackage != XMLNamespacePackage.eINSTANCE
					&& !ExtendedMetaData.XMLNS_URI.equals(ePackage.getNsURI())) {
				String nsURI = extendedMetaData == null ? ePackage.getNsURI() : extendedMetaData.getNamespace(ePackage);
				if (ePackage == xmlSchemaTypePackage) {
					nsURI = XMLResource.XML_SCHEMA_URI;
				}
				if (nsURI != null && !isDuplicateURI(nsURI)) {

					// Substitute metamodel version in EPackage namespace with resource version of applicable model
					// converter
					if (mmNsURIBase != null && resourceNsURIBase != null && nsURI.startsWith(mmNsURIBase)) {
						nsURI = nsURI.replace(mmNsURIBase, resourceNsURIBase);
					}

					List<String> nsPrefixes = helper.getPrefixes(ePackage);
					for (String nsPrefix : nsPrefixes) {
						if (!toDOM) {
							if (nsPrefix != null && nsPrefix.length() > 0) {
								if (!declareXSI || !"xsi".equals(nsPrefix)) { //$NON-NLS-1$
									doc.addAttributeNS(XMLResource.XML_NS, nsPrefix, nsURI);
								}
							} else {
								doc.addAttribute(XMLResource.XML_NS, nsURI);
							}
						} else {
							if (nsPrefix != null && nsPrefix.length() > 0) {
								if (!declareXSI || !"xsi".equals(nsPrefix)) { //$NON-NLS-1$
									((Element) currentNode).setAttributeNS(ExtendedMetaData.XMLNS_URI, XMLResource.XML_NS + ":" + nsPrefix, nsURI); //$NON-NLS-1$
								}
							} else {
								((Element) currentNode).setAttributeNS(ExtendedMetaData.XMLNS_URI, XMLResource.XML_NS, nsURI);
							}
						}
					}
				}
			}
		}

		if (xsiSchemaLocation.length() > 0) {
			if (!toDOM) {
				doc.addAttribute(XSI_SCHEMA_LOCATION, xsiSchemaLocation.toString());
			} else {
				((Element) currentNode).setAttributeNS(XMLResource.XSI_URI, XSI_SCHEMA_LOCATION, xsiSchemaLocation.toString());
			}
		}

		if (xsiNoNamespaceSchemaLocation != null) {
			if (!toDOM) {
				doc.addAttribute(XSI_NO_NAMESPACE_SCHEMA_LOCATION, xsiNoNamespaceSchemaLocation);
			} else {
				((Element) currentNode).setAttributeNS(XMLResource.XSI_URI, XSI_NO_NAMESPACE_SCHEMA_LOCATION, xsiNoNamespaceSchemaLocation);
			}
		}
	}

	protected StringBuffer getBuffer() {
		try {
			return (StringBuffer) ReflectUtil.getInvisibleFieldValue(this, "buffer"); //$NON-NLS-1$
		} catch (Exception ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
		return null;
	}

	/*
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#writeTopObject(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected Object writeTopObject(EObject top) {
		// Make sure that comments and processing instructions get added BEFORE (rather than behind) the root element
		saveTopCommentsAndProcessingInstructions(top);

		return super.writeTopObject(top);
	}

	/**
	 * Does the same as {@link XMLSaveImpl#saveElementFeatureMap(EObject, EStructuralFeature)} for comments and
	 * processing instructions, except that the comments are added BEFORE (rather than behind) the root element.
	 * 
	 * @param top
	 *            The {@link EObject root object} whose comments and processing instructions are to be saved.
	 */
	protected void saveTopCommentsAndProcessingInstructions(EObject top) {
		EList<EAttribute> allAttributes = top.eClass().getEAllAttributes();
		for (Object element : allAttributes) {
			EAttribute attribute = (EAttribute) element;
			if ("mixed".equals(attribute.getName())) { //$NON-NLS-1$
				@SuppressWarnings("unchecked")
				List<? extends FeatureMap.Entry> values = (List<? extends FeatureMap.Entry>) helper.getValue(top, attribute);
				int size = values.size();
				for (int i = 0; i < size; i++) {
					FeatureMap.Entry entry = values.get(i);
					EStructuralFeature entryFeature = entry.getEStructuralFeature();
					Object value = entry.getValue();
					if (entryFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__PROCESSING_INSTRUCTION) {
						ProcessingInstruction pi = (ProcessingInstruction) value;
						String target = pi.getTarget();
						String data = pi.getData();
						if (escape != null && data != null) {
							data = escape.convertLines(data);
						}
						if (!toDOM) {
							doc.addProcessingInstruction(target, data);
						} else {
							// TODO processing instructions are not sent to recordValues
							currentNode.appendChild(document.createProcessingInstruction(target, data));
						}
					} else if (entryFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__COMMENT) {
						String stringValue = value.toString();
						if (escape != null) {
							stringValue = escape.convertLines(stringValue);
						}
						if (!toDOM) {
							doc.addComment(stringValue);
						} else {
							currentNode.appendChild(document.createComment(stringValue));
						}
					}
				}
			}
		}
	}

	/**
	 * Does the same as {@link XMLSaveImpl#saveElementFeatureMap(EObject, EStructuralFeature)}, except that comments and
	 * processing instructions of the root element are no longer added here. This is done in
	 * {@link #saveTopCommentsAndProcessingInstructions(EObject)} instead.
	 * 
	 * @param top
	 *            The {@link EObject root object} whose comments are to be saved.
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl#saveElementFeatureMap(org.eclipse.emf.ecore.EObject,
	 *      org.eclipse.emf.ecore.EStructuralFeature)
	 * @see #saveTopCommentsAndProcessingInstructions(EObject)
	 */
	@Override
	protected boolean saveElementFeatureMap(EObject o, EStructuralFeature f) {
		@SuppressWarnings("unchecked")
		List<? extends FeatureMap.Entry> values = (List<? extends FeatureMap.Entry>) helper.getValue(o, f);
		int size = values.size();
		for (int i = 0; i < size; i++) {
			FeatureMap.Entry entry = values.get(i);
			EStructuralFeature entryFeature = entry.getEStructuralFeature();
			Object value = entry.getValue();
			if (entryFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__PROCESSING_INSTRUCTION) {
				// Skip adding processing instructions if current object is the root element
				if (o != root) {
					ProcessingInstruction pi = (ProcessingInstruction) value;
					String target = pi.getTarget();
					String data = pi.getData();
					if (escape != null && data != null) {
						data = escape.convertLines(data);
					}
					if (!toDOM) {
						doc.addProcessingInstruction(target, data);
					} else {
						// TODO processing instructions are not sent to recordValues
						currentNode.appendChild(document.createProcessingInstruction(target, data));
					}
				}
			} else if (entryFeature instanceof EReference) {
				if (value == null) {
					saveNil(o, entryFeature);
				} else {
					EReference referenceEntryFeature = (EReference) entryFeature;
					if (referenceEntryFeature.isContainment()) {
						saveElement((InternalEObject) value, entryFeature);
					} else if (referenceEntryFeature.isResolveProxies()) {
						saveFeatureMapElementReference((EObject) value, referenceEntryFeature);
					} else {
						saveElementIDRef(o, (EObject) value, entryFeature);
					}
				}
			} else {
				if (entryFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT) {
					String svalue = value.toString();
					if (escape != null) {
						svalue = escape.convertText(svalue);
					}
					if (!toDOM) {
						doc.addText(svalue);
					} else {
						Node text = document.createTextNode(svalue);
						currentNode.appendChild(text);
						handler.recordValues(text, o, f, entry);
					}
				} else if (entryFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__CDATA) {
					String stringValue = value.toString();
					if (escape != null) {
						stringValue = escape.convertLines(stringValue);
					}
					if (!toDOM) {
						doc.addCDATA(stringValue);
					} else {
						Node cdata = document.createCDATASection(stringValue);
						currentNode.appendChild(cdata);
						handler.recordValues(cdata, o, f, entry);
					}
				} else if (entryFeature == XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__COMMENT) {
					// Skip adding comments if current object is the root element
					if (o != root) {
						String stringValue = value.toString();
						if (escape != null) {
							stringValue = escape.convertLines(stringValue);
						}
						if (!toDOM) {
							doc.addComment(stringValue);
						} else {
							// TODO comments are not sent to recordValues
							currentNode.appendChild(document.createComment(stringValue));
						}
					}
				} else {
					saveElement(o, value, entryFeature);
				}
			}
		}
		return size > 0;
	}
}