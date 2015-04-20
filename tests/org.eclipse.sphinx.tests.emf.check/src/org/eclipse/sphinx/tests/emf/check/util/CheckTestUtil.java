/**
 * <copyright>
 *
 * Copyright (c) 2015 itemis and others.
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
package org.eclipse.sphinx.tests.emf.check.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.Application;
import org.eclipse.sphinx.examples.hummingbird20.instancemodel.InstanceModel20Factory;

public class CheckTestUtil {

	public static List<Diagnostic> findDiagnositcsWithMsg(List<Diagnostic> diagnostics, String issueMsg) {
		List<Diagnostic> result = new ArrayList<Diagnostic>();
		for (Diagnostic diag : diagnostics) {
			if (diag.getMessage().contains(issueMsg)) {
				result.add(diag);
			}
		}
		return result;
	}

	public static Application createApplication(String appName) {
		Application app = InstanceModel20Factory.eINSTANCE.createApplication();
		app.setName(appName);
		return app;
	}
}
