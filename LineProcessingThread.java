/*
This class process each line stored in the LinkedBlockingQueue to determine if the 
pattern exists in the line, and if so it prints out the line.xw
*/

import java.io.File;
import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.IOException; 
import java.io.FileNotFoundException; 
import java.util.ArrayList;
import java.util.HashSet;

public class LineProcessingThread implements Runnable {
  private final int threadId;
	private final ArrayList<File> filesToProcess;
	private final String pattern;
  private final int startIdx;
  private final int endIdx;
  private final GrepOptions options;

	public LineProcessingThread(int _threadId, ArrayList<File> _filesToProcess,
                              String _pattern, int _startIdx, int _endIdx,
                              GrepOptions _options) {
    threadId = _threadId;
		filesToProcess = _filesToProcess;
		pattern = _pattern;
    startIdx = _startIdx;
    endIdx = _endIdx;
    options = _options;
	}

  @Override
  public void run() {
    BufferedReader br = null;
    ArrayList<Line> matchingLines = new ArrayList<>();
    try {
      for (int i = startIdx; i < endIdx; i++) {
        File file = filesToProcess.get(i);
        int lineCount = 0;
        br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
          lineCount++;
          boolean matched = line.contains(pattern);
          if (!matched) {
            matched = (options.ignoreCases && 
              line.toLowerCase().contains(pattern.toLowerCase()));
          }
          if (matched) {
            matchingLines.add(new Line(line, lineCount, file.getName()));
          }
        }
        br.close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        br.close();
        printMatchingLines(matchingLines);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void printMatchingLines(ArrayList<Line> matchingLines) {
    if (!options.noOutput) {
      HashSet<String> fileNames = new HashSet<>();
      for (Line line : matchingLines) {
        if (options.printMatchedLinesWithoutFileName) {
          System.out.println(line.getLineNumber() + ": " + line.getContent());
        } else if (options.fileNameOnly) {
          fileNames.add(line.getFileName());
        } else if (options.printMatchedLinesWithFileName) {
          System.out.println(line.getFileName() + ": " + line.getLineNumber()
           + " " + line.getContent());
        }
      }
      if (options.fileNameOnly) {
        for (String fileName : fileNames) {
          System.out.println(fileName);
        }
      }
    }
  }
}