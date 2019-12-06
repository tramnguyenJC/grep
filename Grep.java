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
import java.lang.*;
import java.util.ArrayList;

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

	@Parameters(index = "1..*", paramLabel = "FILE", description = "File(s) or folder(s) to process.")
	private File[] inputFilesOrDirectories;

	private void getFilesToProcess(File file, ArrayList<File> filesToProcess) {
		if (file.isFile()) {
			filesToProcess.add(file);
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		// file is a directory
		File[] filesList = file.listFiles();
		for (File fileInDirectory : filesList) {
			getFilesToProcess(fileInDirectory, filesToProcess);
		}
	}

	private void processFiles(ArrayList<File> files) {
		if (numThreads > files.size()) {
			numThreads = files.size();
		}
		int blockSize = files.size()/numThreads;
		try {
			Thread[] threads = new Thread[numThreads];
			for (int threadId = 0; threadId < numThreads; threadId++) {
				int start = threadId*blockSize;
				int end = (threadId + 1)*blockSize;
				if (threadId == numThreads - 1) {
					end = files.size();
				}
				Thread thread = new Thread(
					new LineProcessingThread(files, pattern, start, end)); 
				threads[threadId] = thread;
            	thread.start(); 
			}
			for (int threadId = 0; threadId < numThreads; threadId++) {
				threads[threadId].join();
			}
	  	} catch (Exception e) {
	  		e.printStackTrace();
	  	}
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		ArrayList<File> filesToProcess = new ArrayList<>();
		// Populate 'filesToProcess' with all files in directory.
		for (File file : inputFilesOrDirectories) {
			getFilesToProcess(file, filesToProcess);
		}
		processFiles(filesToProcess);

	  	long endTime = System.currentTimeMillis();
	  	System.out.println("The number of file(s) to process: " + filesToProcess.size());
	  	System.out.println("The number of processors: " + numThreads);
	  	System.out.println("The time it takes to process file(s) (in milliseconds): " 
	  		+ (endTime - startTime));
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new Grep()).execute(args);
	  	System.exit(exitCode);
	}
}