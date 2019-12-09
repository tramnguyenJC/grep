import java.util.*;
import java.io.*;
import picocli.CommandLine;

public class Statistics {

	private static final String COMMAND_STARTER = "java Grep";
	private static final String PATTERN = "and indefinitely directed";

	private static final int MAX_NUM_THREADS = 10;

	private static final int SAMPLE_SIZE = 10;

	private static final HashMap<Integer, String> TEST_FOLDER_SIZE_TO_NAME = new HashMap<>();

    static {
        TEST_FOLDER_SIZE_TO_NAME.put(10, "test_input_10");
        // TEST_FOLDER_SIZE_TO_NAME.put(100, "test_input_100");
        // TEST_FOLDER_SIZE_TO_NAME.put(250, "test_input_250");
    }

    private static long getAverageGrepTime(String[] args) {
    	long sumProcessingTime = 0;
    	for (int i = 0; i < SAMPLE_SIZE; i++) {
    		long startTime = System.currentTimeMillis();
    		int exitCode = new CommandLine(new Grep()).execute(args);
			long endTime = System.currentTimeMillis();
			sumProcessingTime += (endTime - startTime);
		}
		return sumProcessingTime/SAMPLE_SIZE;
    }

	public static void main(String[] args) throws IOException {
		FileWriter csvWriter = new FileWriter("statistics.csv");
		csvWriter.append("Args");
		csvWriter.append(",");
		csvWriter.append("Number of files");
		csvWriter.append(",");
		csvWriter.append("Number of threads");
		csvWriter.append(",");
		csvWriter.append("Sample size");
		csvWriter.append(",");
		csvWriter.append("Average processing time (in milliseconds)");
		csvWriter.append("\n");

		for (Map.Entry<Integer,String> entry : TEST_FOLDER_SIZE_TO_NAME.entrySet()) {
			for (int numThreads = 1; numThreads <= MAX_NUM_THREADS; numThreads++) {
				int numFilesToProcess = entry.getKey();
				String directoryName = entry.getValue();
				String[] argsCommand = {PATTERN, directoryName, "-nt", numThreads + "", "-noop"};
				String argsStr = String.join(" ", Arrays.asList(argsCommand));
				System.out.println("Processing command: " + argsStr);
				long processingTime = getAverageGrepTime(argsCommand);
				List<String> rowData = Arrays.asList(argsStr, numFilesToProcess + "", 
					numThreads + "", SAMPLE_SIZE + "", processingTime + "");
				csvWriter.append(String.join(",", rowData));
				csvWriter.append("\n");
			}
			csvWriter.append("\n");
		}
    	
		csvWriter.flush();
		csvWriter.close();
	}
}