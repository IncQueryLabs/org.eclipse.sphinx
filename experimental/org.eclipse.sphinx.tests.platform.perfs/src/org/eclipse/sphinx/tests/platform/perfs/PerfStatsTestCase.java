package org.eclipse.sphinx.tests.platform.perfs;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.platform.perfs.Measurement;
import org.junit.Test;

public class PerfStatsTestCase extends TestCase {

	public static final String PERF_STATS_FILE_PATH = "model/PerformanceStats.xmi"; //$NON-NLS-1$

	@Test
	public void testPerfStats() throws CoreException {
		// Load performance stats model resource
		File perfsModel = getPerfStatsModelFile();
		assertNotNull(perfsModel);
		assertTrue(perfsModel.exists());

		PerfStatsExample application = new PerfStatsExample(perfsModel);
		Collection<Measurement> measurements = application.getPerfMeasurements();

		Resource resource = application.getPerfsModelResource();
		assertNotNull(resource);

		PerfStatsExample.updatePerfsModel(resource, measurements);
		PerfStatsExample.logPerfSats(resource);
	}

	private File getPerfStatsModelFile() throws CoreException {
		try {
			URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(PERF_STATS_FILE_PATH), null);
			// Use file scheme
			url = FileLocator.toFileURL(url);
			String path = url.getPath();
			String os = Platform.getOS();
			if (os.contains("win")) { //$NON-NLS-1$
				// Replace all white spaces in the path by "%20"
				path = path.replaceAll("\\s", "%20"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return new File(new URL(url.getProtocol(), null, path).toURI());
		} catch (Exception ex) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getPlugin().getSymbolicName(), ex.getMessage(), ex));
		}
	}
}
