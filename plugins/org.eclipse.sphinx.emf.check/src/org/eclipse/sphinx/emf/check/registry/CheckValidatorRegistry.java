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
package org.eclipse.sphinx.emf.check.registry;

import static org.eclipse.sphinx.platform.util.StatusUtil.createErrorStatus;
import static org.eclipse.sphinx.platform.util.StatusUtil.createWarningStatus;

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
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.check.AbstractCheckValidator;
import org.eclipse.sphinx.emf.check.CompositeValidator;
import org.eclipse.sphinx.emf.check.ICheckValidator;
import org.eclipse.sphinx.emf.check.internal.Activator;

/**
 * A validation registry singleton which backs a standard EMF validation
 * {@link org.eclipse.emf.ecore.EValidator.Registry registry}. When a check validator is called for the first time, the
 * registry reads all the contributed check validators and initializes its internal map, which is then used to retrieve
 * check catalogs from validator class names. Check validators are contributed through the
 * <code>org.eclipse.sphinx.emf.check.chekvalidators</code> extension point. A check validator contribution is a couple
 * of a validator class and optionally a check catalog. By default, the {@link org.eclipse.emf.ecore.EPackage ePackage}
 * affected by the validator is inferred from the set of the annotated method parameters provided by the validator.
 */
public class CheckValidatorRegistry {

	private static final String checkValidator_extenstion = "org.eclipse.sphinx.emf.check.checkvalidators"; //$NON-NLS-1$
	private static final String checkValidator_configElement = "validator"; //$NON-NLS-1$

	private org.eclipse.emf.ecore.EValidator.Registry eValidatorRegistry = null;

	private static CheckValidatorRegistry INSTANCE = null;

	private Map<String, CheckValidatorDescriptor> validatorToCheckModelMap = Collections
			.synchronizedMap(new LinkedHashMap<String, CheckValidatorDescriptor>());

	private ILog logger;

	private IExtensionRegistry extensionRegistry;

	private static final Plugin PLUGIN = Activator.getDefault();

	public static CheckValidatorRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CheckValidatorRegistry(Platform.getExtensionRegistry(), Activator.getPlugin().getLog());
		}
		return INSTANCE;
	}

	/**
	 * Retrieve a check-based validator contributed through the <code>org.eclipse.sphinx.emf.check.chekvalidators</code>
	 * extension point for the given package.
	 * 
	 * @param ePackage
	 * @return
	 * @throws CoreException
	 */
	public ICheckValidator getValidator(EPackage ePackage) throws CoreException {
		if (getValidatorToCheckModelMap().isEmpty()) {
			initialize();
		}
		EValidator eValidator = getRegistry().getEValidator(ePackage);
		if (eValidator instanceof ICheckValidator) {
			return (ICheckValidator) eValidator;
		}
		return null;
	}

	/**
	 * Returns the URI of a check model given a fully qualified name of a check validator.
	 * 
	 * @param qualifiedName
	 * @return
	 */
	public URI getCheckModelURI(String qualifiedName) {
		return getValidatorToCheckModelMap().get(qualifiedName).getURI();
	}

	private CheckValidatorRegistry(IExtensionRegistry extensionRegistry, ILog logger) {
		eValidatorRegistry = EValidator.Registry.INSTANCE;
		this.extensionRegistry = extensionRegistry;
		this.logger = logger;
	}

	private void initialize() {
		if (extensionRegistry == null) {
			return;
		}
		IConfigurationElement[] config = extensionRegistry.getConfigurationElementsFor(checkValidator_extenstion);
		if (config.length != 0) {

			// First iteration to detect wrong check contributions and initialize the check map
			for (IConfigurationElement iConfigElement : config) {
				if (!iConfigElement.getName().toLowerCase().equals(checkValidator_configElement.toLowerCase())) {
					continue;
				}
				CheckValidatorDescriptor checkValidatorDescriptor = new CheckValidatorDescriptor(iConfigElement);
				String validatorClassName = checkValidatorDescriptor.getValidatorClassName();

				// populate the check map
				if (getValidatorToCheckModelMap().containsKey(validatorClassName)) {
					logWarning("Duplicate validator contribution found for: " + validatorClassName); //$NON-NLS-1$
					continue;
				}
				getValidatorToCheckModelMap().put(validatorClassName, checkValidatorDescriptor);
			}

			// Second iteration on the check map to register the validators
			for (CheckValidatorDescriptor descriptor : getValidatorToCheckModelMap().values()) {

				// load the validator class from the fully qualified name and register it
				String bundleName = descriptor.getContributorName();
				String validatorClassName = descriptor.getValidatorClassName();
				try {
					Class<?> clazz = Platform.getBundle(bundleName).loadClass(validatorClassName);
					// register the ePackages within the scope of the validator
					registerPackagesInScope(clazz);
				} catch (ClassNotFoundException ex) {
					logError(ex);
				}
			}
		}
	}

	private void registerPackagesInScope(Class<?> clazz) {
		Set<Class<?>> classes = findClassesInScope(clazz);
		Set<EPackage> packagesToRegister = findEPackagesInScope(classes);
		for (EPackage p : packagesToRegister) {
			register(p, clazz);
		}
	}

	/**
	 * Retrieve the classes from the arguments of the methods annotated with @Check
	 * 
	 * @param validatorClass
	 * @return
	 */
	private Set<Class<?>> findClassesInScope(Class<?> validatorClass) {
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
	 * The following method makes the assumption that the ePackages associated to a validator are located in the same
	 * Java package than the classes given as input, which is the default behavior of the EMF API model generator.
	 * 
	 * @param classes
	 * @return
	 */
	private Set<EPackage> findEPackagesInScope(Set<Class<?>> classes) {
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
		// do it in two steps so that concurrent modification exceptions are avoided
		for (Object object : EPackage.Registry.INSTANCE.values()) {
			if (object instanceof EPackage || object instanceof EPackage.Descriptor) {
				packages.add(object);
			}
		}
		EPackage ePackage = null;
		for (Object object : packages) {
			if (object instanceof EPackage) {
				ePackage = (EPackage) object;
			} else if (object instanceof EPackage.Descriptor) {
				ePackage = ((EPackage.Descriptor) object).getEPackage();
			} else {
				logError("Could not recognize type of regitered EPackage"); //$NON-NLS-1$
			}
			String instanceClassName = ePackage.getClass().getCanonicalName();
			if (instanceClassName.contains(packageName)) {
				break;
			}
		}
		return ePackage;
	}

	private void register(EPackage ePackage, Class<?> clazz) {
		EValidator validatorInstance;
		try {
			validatorInstance = (EValidator) clazz.newInstance();
			register(ePackage, validatorInstance);
		} catch (InstantiationException ex) {
			logError(ex);
		} catch (IllegalAccessException ex) {
			logError(ex);
		}
	}

	private void register(EPackage ePackage, EValidator newValidator) {

		EValidator existingValidator = getRegistry().getEValidator(ePackage);

		// there is no validator already registered, register the new validator
		if (existingValidator == null) {
			getRegistry().put(ePackage, newValidator);
		}

		// there is a check-based validator already registered, replace it by the injected one
		else if (existingValidator instanceof AbstractCheckValidator) {
			// getRegistry().put(ePackage, newValidator);
			CompositeValidator validator = new CompositeValidator();
			validator.addChild(existingValidator);
			validator.addChild(newValidator);
			getRegistry().put(ePackage, validator);
		}

		// there is a composite validator already registered, get its delegates, find the check-based validator and
		// replace it with the new one
		else if (existingValidator instanceof CompositeValidator) {
			((CompositeValidator) existingValidator).addChild(newValidator);
		}
		// there is another type of validator registered, create a composite validator and wrap the validators
		else {
			CompositeValidator dispatcher = new CompositeValidator();
			dispatcher.addChild(existingValidator);
			dispatcher.addChild(newValidator);
			getRegistry().put(ePackage, dispatcher);
		}
	}

	private void logWarning(String msgId, Object... objects) {
		logWarning(new RuntimeException(NLS.bind(msgId, objects)));
	}

	private void logWarning(Throwable throwable) {
		logger.log(createWarningStatus(PLUGIN, throwable));
	}

	private void logError(String msgId, Object... objects) {
		logError(new RuntimeException(NLS.bind(msgId, objects)));
	}

	private void logError(Throwable throwable) {
		logger.log(createErrorStatus(PLUGIN, throwable));
	}

	private EValidator.Registry getRegistry() {
		return eValidatorRegistry;
	}

	private Map<String, CheckValidatorDescriptor> getValidatorToCheckModelMap() {
		return validatorToCheckModelMap;
	}
}