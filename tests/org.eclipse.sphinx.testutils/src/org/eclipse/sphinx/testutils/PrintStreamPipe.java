package org.eclipse.sphinx.testutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

/**
 * Provides an {@link OutputStream}/{@link InputStream} pipe consisting of a {@link PrintStream} whose output is
 * redirected to a {@link BufferedReader}. Can be used to substitute output streams in classes under test and to capture
 * and check the output the latter are producing.
 * <p>
 * Usage example:
 * </p>
 *
 * <pre>
 * &#064;Test
 * public void testSomeClass() throws Exception {
 * 	PrintStreamPipe pipe = new PrintStreamPipe();
 * 
 * 	SomeClassWritingToAPrintStream classUnderTest = new SomeClassWritingToAPrintStream();
 * 	classUnderTest.setPrintStream(pipe.getPipedPrintStream());
 * 
 * 	classUnderTest.doSomething(); // Supposed to write &quot;Something&quot; to enclosed print stream
 * 	assertEquals(&quot;Something&quot;, pipe.getPipedReader().readLine());
 * 
 * 	classUnderTest.doSomethingElse(); // Supposed to write &quot;Something else&quot; to enclosed print stream
 * 	assertEquals(&quot;Something else&quot;, pipe.getPipedReader().readLine());
 * }
 * </pre>
 */
public class PrintStreamPipe {

	private final PrintStream pipedPrintStream;
	private final BufferedReader pipedReader;

	public PrintStreamPipe() throws IOException {
		PipedInputStream pipedInputStream = new PipedInputStream();
		PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
		pipedPrintStream = new PrintStream(pipedOutputStream);
		pipedReader = new BufferedReader(new InputStreamReader(pipedInputStream));
	}

	public PrintStream getPipedPrintStream() {
		return pipedPrintStream;
	}

	public BufferedReader getPipedReader() {
		return pipedReader;
	}

	public void close() throws IOException {
		pipedReader.close();
		pipedPrintStream.close();
	}
}
