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

import org.eclipse.sphinx.emf.check.AbstractCheckValidator;
import org.eclipse.sphinx.emf.check.Check;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Component;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Connection;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Package;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.ComponentType;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Interface;
import org.eclipse.sphinx.examples.hummingbird20.typemodel.Port;

public class Hummingbird20ConnectionsCheckValidator extends AbstractCheckValidator {

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
}