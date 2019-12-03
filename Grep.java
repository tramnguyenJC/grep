/*
This program allows user to search for a pattern within specified file(s)
or directory (similar to the grep command), and it uses multi-threading
to improve the performance of the search. 
We also used the Java library picocli, which is a command line parsing framework
that helps make processing command line options and parameters become easier.
Read more about picocli here: https://picocli.info/

To compile this program run:
	javac Grep.java
To learn about the options and parameters to execute this grep, run:
  java Grep --help
Example to find the word 'picocli' in Grep.java, using 2 threads:
  java Grep --nt 2 picocli Grep.java
*/

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Command(name = "grep", mixinStandardHelpOptions = true, version = "Grep 1.0")
public class Grep implements Runnable {

  // @TODO: Implement more options as listed on http://man7.org/linux/man-pages/man1/grep.1.html
  //        or https://www.geeksforgeeks.org/grep-command-in-unixlinux/
	@Option(names = { "-nthreads", "--nt" }, defaultValue = "1",
		description = "The number of threads used to execute the grep command in parallel. " +
		"Defaults to ${DEFAULT-VALUE} if not specified.")
	private int numThreads = 1;
  
	@Parameters(index = "0", paramLabel = "PATTERN", description = "Pattern to find.")
	private String pattern;

	@Parameters(index = "1..*", paramLabel = "FILE", description = "File(s) to process.")
	private File[] inputFiles;

	public void run() {
		LinkedBlockingQueue<Line> queue = new LinkedBlockingQueue<Line>();
		try {
			// Create a thread pool for the IOThread plus the processing thread(s)
			ExecutorService service = Executors.newFixedThreadPool(numThreads + 1);
			for (int i = 0; i < numThreads; i++) {
				service.submit(new LineProcessingThread(queue, pattern));
			}

	    // Wait til IOThread completes
			service.submit(new IOThread(queue, inputFiles)).get();
	    service.shutdownNow();  // interrupt any LineProcessingThread

	    // Wait til LineProcessingThread terminate
	    service.awaitTermination(365, TimeUnit.DAYS);
	  } catch (Exception e) {
	  	e.printStackTrace();
	  }
	}

	public static void main(String[] args) {
	  int exitCode = new CommandLine(new Grep()).execute(args);
	  System.exit(exitCode);
	}
}