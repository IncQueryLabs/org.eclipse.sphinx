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
package org.eclipse.sphinx.platform.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.sphinx.platform.internal.messages.Messages"; //$NON-NLS-1$

	public static String job_persistingContentTypeIdProperties;
	public static String task_persistingContentTypeIdPropertiesFor;

	public static String converting;
	public static String job_convertProjectToPlugin;
	public static String job_performingGarbageCollection;
	public static String perfLog_$0$1runningTimeAndUserRunningTime;
	public static String perfLog_$0runningTime;
	public static String perfLog_$0$1$2contextInfos;
	public static String perfLog_$0runningTimeExceedTimeout;
	public static String perfLog_$0runningTimeZero;
	public static String perfLog_performanceStatsNotActivated;
	public static String perfLog_$0$1$2$3statEventToStringSimple;
	public static String perfLog_$0$1$2$3statEventToStringWithRunCount;

	static {
		// Load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
