/*
This program allows user to search for a pattern within specified file(s)
or directory (similar to the grep command), and it uses multi-threading
to improve the performance of the search. 
We also used the Java library picocli, which is a command line parsing framework
that helps make processing command line options and parameters become easier.
Read more about picocli here: https://picocli.info/

To compile this program run:
	javac Grape.java
To learn about the options and parameters to execute this grep, run:
  java Grape --help
Example to find the word 'picocli' in Grape.java, using 2 threads:
  java Grape --nt 2 picocli Grape.java
*/

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.lang.*;
import java.util.ArrayList;

@Command(name = "grep", mixinStandardHelpOptions = true, version = "Grape Final Version")
public class Grape implements Runnable {

  	// @TODO: Implement more options as listed on http://man7.org/linux/man-pages/man1/grep.1.html
 	//        or https://www.geeksforgeeks.org/grep-command-in-unixlinux/
	@Option(names = { "-nt" }, defaultValue = "1",
		description = "The number of threads used to execute the grep command in parallel. " +
		"Defaults to ${DEFAULT-VALUE} if not specified. This option is nly applicable if " +
		"there are more than one file to process.")
	private int numThreads = 1;

	// @Option(names = {"-c"},
	// 	description = "This prints only a count of the lines that match a pattern")
	// private boolean printCountOnly = false;

	@Option(names = {"-h"},
		description = "Display the matched lines, but do not display the filenames. " +
		              "This is the default when there is only one file to search. ")
	private boolean printMatchedLinesWithoutFileName = false;

	@Option(names = {"-H"},
		description = "Display the matched lines with the filenames. This is the default "
		            + "when there are more than one files to search. ")
	private boolean printMatchedLinesWithFileName= false;

	@Option(names = {"-noop"},
		description = "Display no output (for testing purposes).")
	private boolean noOutput;

	@Option(names = {"-i"},
		description = "Ignore cases for matching.")
	private boolean ignoreCases= false;

	@Option(names = {"-l"},
		description = "Displays list of filenames only.")
	private boolean fileNameOnly= false;

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

	private void processFiles(ArrayList<File> files,
		GrapeOptions options) {
		int[] startIndices = new int[numThreads];
		int[] endIndices = new int[numThreads];
		int blockSize = files.size()/numThreads;
		int remainder = files.size()%numThreads;
		int id = 0;
		while (id < remainder) {
			startIndices[id] = id*(blockSize + 1);
			endIndices[id] = (id + 1)*(blockSize + 1);
			id++;
		}
		while (id < numThreads) {
			startIndices[id] = remainder + (id*blockSize);
			endIndices[id] = remainder + (id + 1)*blockSize;
			id++;
		}
		try {
			Thread[] threads = new Thread[numThreads];
			for (int threadId = 0; threadId < numThreads; threadId++) {
				Thread thread = new Thread(
					new LineProcessingThread(threadId, files, pattern,
						startIndices[threadId], endIndices[threadId],
						options)); 
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
			if (!file.exists()) {
				System.err.println("File " + file.getAbsolutePath() + " does not exist.");
				System.exit(1);
			}
			getFilesToProcess(file, filesToProcess);
		}
		if (numThreads > filesToProcess.size()) {
			numThreads = filesToProcess.size();
		}
		if (filesToProcess.size() > 1) {
			// If this option is unset by user, then by default we print matched
			// lines with file names if there are more than one files to search.
			if (!printMatchedLinesWithFileName) {
				printMatchedLinesWithFileName = true;
			}
		} else if (filesToProcess.size() == 1) {
			// If this option is unset by user, then by default we print matched
			// lines without file names if there is only one file to search.
			if (!printMatchedLinesWithoutFileName) {
				printMatchedLinesWithoutFileName = true;
			}
		}
		GrapeOptions options = new GrapeOptions(printMatchedLinesWithoutFileName,
			printMatchedLinesWithFileName, noOutput, ignoreCases, fileNameOnly);
		processFiles(filesToProcess, options);

	  	long endTime = System.currentTimeMillis();
	  	// System.out.println("The number of file(s) to process: " + filesToProcess.size());
	  	// System.out.println("The number of processors: " + numThreads);
	  	// System.out.println("The time it takes to process file(s) (in milliseconds): " 
	  	// 	+ (endTime - startTime));
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new Grape()).execute(args);
	  	System.exit(exitCode);
	}
}