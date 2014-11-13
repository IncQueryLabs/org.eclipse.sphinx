/**
 * <copyright>
 *
 * Copyright (c) 2014 iteimport org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
ls
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.check.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.sphinx.emf.Activator;
import org.eclipse.sphinx.emf.check.AbstractCheckValidator;
import org.eclipse.sphinx.emf.check.DispatchingValidator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class CheckValidationRegistry {

	private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

	private org.eclipse.emf.ecore.EValidator.Registry eValidatorRegistry = null;

	private static CheckValidationRegistry INSTANCE = null;

	private static final String checkValidator_extenstion = "org.eclipse.sphinx.emf.check.checkvalidator"; //$NON-NLS-1$
	private static final String checkValidator_configElement = "validator"; //$NON-NLS-1$
	private static final String validator_class = "class"; //$NON-NLS-1$
	private static final String validator_model = "model"; //$NON-NLS-1$

	private Map<String, URI> validatorToCheckModelMap = new HashMap<String, URI>();

	public static CheckValidationRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CheckValidationRegistry();
		}
		return INSTANCE;
	}

	private CheckValidationRegistry() {
		setValidatorRegistry(EValidator.Registry.INSTANCE);
		try {
			initValidatorToCheckModelMap();
		} catch (CoreException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}

	/**
	 * Initializes the registered check validator to uri map.
	 * 
	 * @throws CoreException
	 */
	private void initValidatorToCheckModelMap() throws CoreException {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(checkValidator_extenstion);
		if (config.length != 0) {
			for (IConfigurationElement iConfigElement : config) {
				if (iConfigElement.getName().toLowerCase().equals(checkValidator_configElement.toLowerCase())) {
					String modelPath = iConfigElement.getAttribute(validator_model);
					String pluginName = iConfigElement.getContributor().getName();
					String fullPath = pluginName + PATH_SEPARATOR + modelPath;
					String fqn = iConfigElement.getAttribute(validator_class);
					getValidatorToCheckModelMap().put(fqn, URI.createPlatformPluginURI(fullPath, false));

					// Class<?> clazz =
					// Platform.getBundle(configElement.getContributor().getName()).loadClass(className);
					// parcourir la classe, retourner un set de classes, récupérer les epackages à partir des classes,
					// enregistrer ensuite les validateurs
					// getRegistry().put(EPackage, EValidator);
				}
			}
		}
	}

	/**
	 * Retrieve a check-based validator contributed through the checkvalidator extension point for the given package.
	 * 
	 * @param ePackage
	 * @return
	 * @throws CoreException
	 */
	public AbstractCheckValidator getCheckValidator(EPackage ePackage) throws CoreException {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(checkValidator_extenstion);
		if (config.length != 0) {
			for (IConfigurationElement iConfigElement : config) {
				if (iConfigElement.getName().toLowerCase().equals(checkValidator_configElement.toLowerCase())) {
					return (AbstractCheckValidator) iConfigElement.createExecutableExtension(validator_class);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the URI of a check model given a fully qualified name of a check validator.
	 * 
	 * @param fqn
	 * @return
	 */
	public URI getCheckModelURI(String fqn) {
		return getValidatorToCheckModelMap().get(fqn);
	}

	public void register(EPackage ePackage) {
		try {
			EValidator checkValidator = getCheckValidator(ePackage);
			register(ePackage, checkValidator);
		} catch (CoreException ex) {
			PlatformLogUtil.logAsError(Activator.getPlugin(), ex);
		}
	}

	public void register(EPackage ePackage, EValidator newValidator) {

		EValidator existingValidator = getRegistry().getEValidator(ePackage);

		// there is no validator already registered, register the new validator
		if (existingValidator == null) {
			getRegistry().put(ePackage, newValidator);
		}
		// there is a check-based validator already registered, replace it by the injected one
		else if (existingValidator instanceof AbstractCheckValidator) {
			getRegistry().put(ePackage, newValidator);
		}
		// there is a dispatching validator already registered, get its delegates, find the check-based validator and
		// replace it with the new one
		else if (existingValidator instanceof DispatchingValidator) {
			List<EValidator> delegates = ((DispatchingValidator) existingValidator).getDelegates();
			for (EValidator delegate : delegates) {
				if (delegate instanceof AbstractCheckValidator) {
					((DispatchingValidator) existingValidator).removeDelegate(delegate);
					((DispatchingValidator) existingValidator).addDelegate(newValidator);
					getRegistry().put(ePackage, existingValidator);
					break;
				}
			}
		}
		// there is another type of validator registered, create a dispatching validator and wrap the existing validator
		// and the new validator
		else {
			DispatchingValidator dispatcher = new DispatchingValidator(existingValidator);
			dispatcher.addDelegate(newValidator);
			getRegistry().put(ePackage, dispatcher);
		}
	}

	private EValidator.Registry getRegistry() {
		return eValidatorRegistry;
	}

	private Map<String, URI> getValidatorToCheckModelMap() {
		return validatorToCheckModelMap;
	}

	private void setValidatorRegistry(EValidator.Registry eValidatorRegistry) {
		this.eValidatorRegistry = eValidatorRegistry;
	}
}