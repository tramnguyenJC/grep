/*
This class process each line stored in the LinkedBlockingQueue to determine if the 
pattern exists in the line, and if so it prints out the line.xw
*/

import java.util.concurrent.LinkedBlockingQueue;

public class LineProcessingThread implements Runnable {
	private final LinkedBlockingQueue<Line> queue;
	private final String pattern;

	public LineProcessingThread(LinkedBlockingQueue<Line> _queue, String _pattern) {
		queue = _queue;
		pattern = _pattern;
	}

	@Override
	public void run() {
		Line line;
		while(true) {
			try {
        // block if the queue is empty
				line = queue.take();
        checkForPattern(line);
			} catch (InterruptedException ex) {
        break; // IOThread has completed
      }
    }
    // poll() returns null if the queue is empty
    while((line = queue.poll()) != null) {
      checkForPattern(line);
    }
  }

  public void checkForPattern(Line line) {
  	if (line.getLine().contains(pattern)) {
       System.out.println(line);
    }
  }
}