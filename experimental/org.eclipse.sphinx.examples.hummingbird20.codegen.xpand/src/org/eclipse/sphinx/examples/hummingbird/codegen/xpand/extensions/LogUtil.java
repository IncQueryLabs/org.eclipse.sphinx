package org.eclipse.sphinx.examples.hummingbird.codegen.xpand.extensions;

public class LogUtil {

  public static void log (Object o) {
		System.out.println(o!=null ? o.toString() : "<null>");
	}
}
