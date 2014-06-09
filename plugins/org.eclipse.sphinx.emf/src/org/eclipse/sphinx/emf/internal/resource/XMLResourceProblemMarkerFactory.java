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
package org.eclipse.sphinx.emf.internal.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.xmi.XMIException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.resource.ExtendedResource;
import org.eclipse.sphinx.emf.resource.IXMLMarker;
import org.eclipse.sphinx.emf.resource.ProxyURIIntegrityException;
import org.eclipse.sphinx.emf.resource.XMLIntegrityException;
import org.eclipse.sphinx.emf.resource.XMLValidityException;
import org.eclipse.sphinx.emf.resource.XMLWellformednessException;
import org.eclipse.sphinx.platform.resources.MarkerDescriptor;
import org.xml.sax.SAXParseException;

public class XMLResourceProblemMarkerFactory extends BasicResourceProblemMarkerFactory {

	@Override
	protected MarkerDescriptor createProblemMarkerDescriptor(Diagnostic diagnostic, int severity, Map<Object, Object> problemHandlingOptions) {
		MarkerDescriptor markerDescriptor = super.createProblemMarkerDescriptor(diagnostic, severity, problemHandlingOptions);

		// Handle XMI exceptions
		if (diagnostic instanceof XMIException) {
			XMIException xmiException = (XMIException) diagnostic;

			// Handle XML well-formedness exceptions
			if (xmiException.getCause() instanceof XMLWellformednessException) {
				String format = problemHandlingOptions != null ? (String) problemHandlingOptions
						.get(ExtendedResource.OPTION_XML_WELLFORMEDNESS_PROBLEM_FORMAT_STRING) : null;
				markerDescriptor.getAttributes().put(IMarker.MESSAGE, createProblemMarkerMessage(format, xmiException));
				markerDescriptor.setType(IXMLMarker.XML_WELLFORMEDNESS_PROBLEM);
			}

			// Handle schema validation exceptions
			else if (xmiException.getCause() instanceof XMLValidityException || xmiException.getCause() instanceof SAXParseException
					&& xmiException.getMessage().contains("cvc-")) { //$NON-NLS-1$
				Integer resourceDefinedSeverity = problemHandlingOptions != null ? (Integer) problemHandlingOptions
						.get(ExtendedResource.OPTION_XML_VALIDITY_PROBLEM_SEVERITY) : null;
				if (resourceDefinedSeverity != null) {
					markerDescriptor.getAttributes().put(IMarker.SEVERITY, resourceDefinedSeverity);
				}

				String format = problemHandlingOptions != null ? (String) problemHandlingOptions
						.get(ExtendedResource.OPTION_XML_VALIDITY_PROBLEM_FORMAT_STRING) : null;
				markerDescriptor.getAttributes().put(IMarker.MESSAGE, createProblemMarkerMessage(format, xmiException));
				markerDescriptor.setType(IXMLMarker.XML_VALIDITY_PROBLEM);
			}

			// Handle XML integrity exceptions
			else if (xmiException instanceof XMLIntegrityException) {
				markerDescriptor.getAttributes().put(IMarker.MESSAGE, createProblemMarkerMessage(xmiException));
				markerDescriptor.setType(IXMLMarker.XML_INTEGRITY_PROBLEM);
			}

			// Handle proxy URI integrity exceptions
			else if (xmiException instanceof ProxyURIIntegrityException) {
				markerDescriptor.getAttributes().put(IMarker.MESSAGE, createProblemMarkerMessage(xmiException));
				markerDescriptor.setType(ResourceProblemMarkerService.PROXY_URI_INTEGRITY_PROBLEM);
			}

			// Handle other XMI exceptions
			else {
				markerDescriptor.getAttributes().put(IMarker.MESSAGE, createProblemMarkerMessage(xmiException));
			}
		}
		return markerDescriptor;
	}

	protected String createProblemMarkerMessage(String format, Exception exception) {
		Assert.isNotNull(exception);

		String msg = createProblemMarkerMessage(exception);
		if (format == null) {
			return msg;
		}

		if (format.contains("{0}")) { //$NON-NLS-1$
			return NLS.bind(format, msg);
		} else {
			if (!format.endsWith(" ")) { //$NON-NLS-1$
				format.concat(" "); //$NON-NLS-1$
			}
			return format.concat(msg);
		}
	}

	/**
	 * @see IXMLMarker#XML_WELLFORMEDNESS_PROBLEM
	 * @see IXMLMarker#XML_VALIDITY_PROBLEM
	 * @see IXMLMarker#XML_INTEGRITY_PROBLEM
	 */
	@Override
	protected List<String> getProblemMarkerTypesToDelete() {
		return Arrays.asList(new String[] { IMarker.PROBLEM, IXMLMarker.XML_WELLFORMEDNESS_PROBLEM, IXMLMarker.XML_VALIDITY_PROBLEM,
				IXMLMarker.XML_INTEGRITY_PROBLEM });
	}
}
