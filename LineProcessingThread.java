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

public class LineProcessingThread implements Runnable {
	private final ArrayList<File> filesToProcess;
	private final String pattern;
  private final int startIdx;
  private final int endIdx;


	public LineProcessingThread(ArrayList<File> _filesToProcess,
                              String _pattern, int _startIdx, int _endIdx) {
		filesToProcess = _filesToProcess;
		pattern = _pattern;
    startIdx = _startIdx;
    endIdx = _endIdx;
	}

  @Override
  public void run() {
    BufferedReader br = null;
    try {
      for (int i = startIdx; i < endIdx; i++) {
        File file = filesToProcess.get(i);
        int lineCount = 0;
        br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
          lineCount++;
          if (line.contains(pattern)) {
             System.out.println(file.getName() + ":" + lineCount + " " + line);
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
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}