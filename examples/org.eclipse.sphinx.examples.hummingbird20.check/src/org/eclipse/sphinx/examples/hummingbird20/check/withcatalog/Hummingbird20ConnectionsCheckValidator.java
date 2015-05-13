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
package org.eclipse.sphinx.examples.hummingbird20.check.withcatalog;

import org.eclipse.emf.common.util.EList;
import org.eclipse.sphinx.emf.check.AbstractCheckValidator;
import org.eclipse.sphinx.emf.check.Check;
import org.eclipse.sphinx.emf.check.CheckValidatorRegistry;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Connection;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;

public class Hummingbird20ConnectionsCheckValidator extends AbstractCheckValidator {

	public static final String ISSUE_MSG_1 = "(Case #1: The application does not contains any component with at least one outgoing connection)"; //$NON-NLS-1$

	public Hummingbird20ConnectionsCheckValidator() {
		super();
	}

	public Hummingbird20ConnectionsCheckValidator(CheckValidatorRegistry checkValidatorRegistry) {
		super(checkValidatorRegistry);
	}

	@Check(constraint = "ConnectionTargetComponentNotValid", categories = { "Category1" })
	void checkConnectionTargetComponent(Connection connection) {
		ComponentType componentType = null;
		Component targetComponent = connection.getTargetComponent();
		if (targetComponent != null) {
			componentType = targetComponent.getType();
		}

		Interface requiredInterface = null;
		Port sourcePort = connection.getSourcePort();
		if (sourcePort != null) {
			requiredInterface = sourcePort.getRequiredInterface();
		}

		if (componentType != null && requiredInterface != null) {
			if (!componentType.getProvidedInterfaces().contains(requiredInterface)) {
				issue(connection, InstanceModel20Package.eINSTANCE.getConnection_TargetComponent(), targetComponent.getName(), connection.getName(),
						requiredInterface.getName());
			}
		}
	}

	/**
	 * This method is called by this validator only when the user selects "Category1" even though the associated
	 * constraint is applicable for the set {"Category1", "Category2"}.
	 *
	 * @param application
	 */
	@Check(constraint = "ApplicationNameNotValid", categories = { "Category1" })
	void checkApplicationContainsComponentWithConnection(Application application) {
		boolean foundComponentWithOutgoingConnection = false;
		EList<Component> components = application.getComponents();
		for (Component component : components) {
			if (component.getOutgoingConnections().size() > 0) {
				foundComponentWithOutgoingConnection = true;
			}
		}
		if (!foundComponentWithOutgoingConnection) {
			issue(application, InstanceModel20Package.Literals.APPLICATION__COMPONENTS, ISSUE_MSG_1);
		}
	}
}