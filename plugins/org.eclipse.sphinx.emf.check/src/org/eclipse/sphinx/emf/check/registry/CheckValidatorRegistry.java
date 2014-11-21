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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.sphinx.emf.check.AbstractCheckValidator;
import org.eclipse.sphinx.emf.check.ComposedValidator;
import org.eclipse.sphinx.emf.check.ICheckValidator;
import org.eclipse.sphinx.emf.check.internal.Activator;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

public class CheckValidatorRegistry {

	public static CheckValidatorRegistry INSTANCE = new CheckValidatorRegistry();

	private static final String checkValidator_extenstion = "org.eclipse.sphinx.emf.check.checkvalidator"; //$NON-NLS-1$
	private static final String checkValidator_configElement = "validator"; //$NON-NLS-1$
	private static final String validator_class = "class"; //$NON-NLS-1$
	private static final String validator_model = "model"; //$NON-NLS-1$

	private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

	private org.eclipse.emf.ecore.EValidator.Registry eValidatorRegistry = null;

	private Map<String, URI> validatorToCheckModelMap = Collections.synchronizedMap(new LinkedHashMap<String, URI>());

	private CheckValidatorRegistry() {
		eValidatorRegistry = EValidator.Registry.INSTANCE;
		// initialize();
	}

	public void initialize() throws CoreException, InvalidRegistryObjectException, ClassNotFoundException {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(checkValidator_extenstion);
		if (config.length != 0) {
			for (IConfigurationElement iConfigElement : config) {
				if (iConfigElement.getName().toLowerCase().equals(checkValidator_configElement.toLowerCase())) {
					String modelPath = iConfigElement.getAttribute(validator_model);
					String pluginName = iConfigElement.getContributor().getName();
					String fullPath = pluginName + PATH_SEPARATOR + modelPath;
					String validatorClassName = iConfigElement.getAttribute(validator_class);

					// populate the check map
					getValidatorToCheckModelMap().put(validatorClassName, URI.createPlatformPluginURI(fullPath, false));

					// load the validator class from the fully qualified name and retrieve the classes from the
					// arguments of the methods annotated with @Check
					Class<?> clazz = Platform.getBundle(iConfigElement.getContributor().getName()).loadClass(validatorClassName);

					// register the ePackages within the scope of the validator
					registerPackagesInScope(clazz);
				}
			}
		}
	}

	private void registerPackagesInScope(Class<?> clazz) {
		Set<Class<?>> classes = getClassesInScope(clazz);
		Set<EPackage> packagesToRegister = getEPackagesInScope(classes);
		for (EPackage p : packagesToRegister) {
			register(p, clazz);
		}
	}

	private Set<Class<?>> getClassesInScope(Class<?> validatorClass) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		Method[] methods = validatorClass.getDeclaredMethods();
		for (Method m : methods) {
			Annotation[] annotations = m.getAnnotations();
			for (Annotation a : annotations) {
				Class<? extends Annotation> annotationType = a.annotationType();
				if (annotationType.equals(org.eclipse.sphinx.emf.check.Check.class)) {
					Class<?>[] parameterTypes = m.getParameterTypes();
					for (Class<?> parameterType : parameterTypes) {
						classes.add(parameterType);
					}
				}
			}
		}
		return classes;
	}

	/**
	 * The following method makes the assumption that the EPackage is located in the same Java package than the classes
	 * given as input, which is the default behavior of the EMF API model generator.
	 *
	 * @param classes
	 * @return
	 */
	private Set<EPackage> getEPackagesInScope(Set<Class<?>> classes) {
		Set<EPackage> packagesInScope = new HashSet<EPackage>();
		Iterator<Class<?>> iterator = classes.iterator();
		while (iterator.hasNext()) {
			Class<?> c = iterator.next();
			String packageName = c.getPackage().getName();
			packagesInScope.add(findEPackage(packageName));
		}
		return packagesInScope;
	}

	private EPackage findEPackage(String packageName) {
		List<Object> packages = new ArrayList<Object>();
		// do it in two steps so that concurrent modification exceptions are avoided, as during getEPackage call, the
		// Descriptor gets substituted by the actual EPackage
		for (Object obj : EPackage.Registry.INSTANCE.values()) {
			if (obj instanceof EPackage || obj instanceof EPackage.Descriptor) {
				packages.add(obj);
			}
		}
		EPackage ePackage = null;
		for (Object obj : packages) {
			if (obj instanceof EPackage) {
				ePackage = (EPackage) obj;
			} else if (obj instanceof EPackage.Descriptor) {
				ePackage = ((EPackage.Descriptor) obj).getEPackage();
			} else {
				throw new RuntimeException("Could not recognize type of regitered EPackage"); //$NON-NLS-1$
			}
			String instanceClassName = ePackage.getClass().getCanonicalName();
			if (instanceClassName.contains(packageName)) {
				break;
			}
		}
		return ePackage;
	}

	/**
	 * Retrieve a check-based validator contributed through the checkvalidator extension point for the given package.
	 *
	 * @param ePackage
	 * @return
	 * @throws CoreException
	 */
	public ICheckValidator getValidator(EPackage ePackage) throws CoreException {
		EValidator eValidator = getRegistry().getEValidator(ePackage);
		if (eValidator instanceof ICheckValidator) {
			return (ICheckValidator) eValidator;
		}
		return null;
	}

	public List<EValidator> getAllValidators(EPackage ePackage) throws CoreException {
		List<EValidator> result = new ArrayList<EValidator>();
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(checkValidator_extenstion);
		if (config.length != 0) {
			for (IConfigurationElement iConfigElement : config) {
				if (iConfigElement.getName().toLowerCase().equals(checkValidator_configElement.toLowerCase())) {
					result.add((EValidator) iConfigElement.createExecutableExtension(validator_class));
				}
			}
		}
		return result;
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

	public void register(EPackage ePackage, Class<?> clazz) {
		EValidator validatorInstance;
		try {
			validatorInstance = (EValidator) clazz.newInstance();
			register(ePackage, validatorInstance);
		} catch (InstantiationException ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
		} catch (IllegalAccessException ex) {
			PlatformLogUtil.logAsError(Activator.getDefault(), ex);
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
			// getRegistry().put(ePackage, newValidator);
			ComposedValidator validator = new ComposedValidator();
			validator.addDelegate(existingValidator);
			validator.addDelegate(newValidator);
			getRegistry().put(ePackage, validator);
		}
		// there is a dispatching validator already registered, get its delegates, find the check-based validator and
		// replace it with the new one
		else if (existingValidator instanceof ComposedValidator) {
			List<EValidator> delegates = ((ComposedValidator) existingValidator).getDelegates();
			for (EValidator delegate : delegates) {
				if (delegate instanceof AbstractCheckValidator) {
					((ComposedValidator) existingValidator).removeDelegate(delegate);
					((ComposedValidator) existingValidator).addDelegate(newValidator);
					getRegistry().put(ePackage, existingValidator);
					break;
				}
			}
		}
		// there is another type of validator registered, create a dispatching validator and wrap the existing validator
		// and the new validator
		else {
			ComposedValidator dispatcher = new ComposedValidator(existingValidator);
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
}