import java.util.concurrent.LinkedBlockingQueue;
import java.io.File;
import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.IOException; 
import java.io.FileNotFoundException; 

public class IOThread implements Runnable {
	private final LinkedBlockingQueue<Line> queue;
	private final File[] inputFiles;

	public IOThread(LinkedBlockingQueue<Line> _queue, File[] _inputFiles) {
		queue = _queue;
		inputFiles = _inputFiles;
	}

	@Override
	public void run() {
		BufferedReader br = null;
		try {
			for (File file : inputFiles) {
				int lineCount = 0;
				br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					lineCount++;
	        		// block if the queue is full
					queue.put(new Line(line, lineCount, file.getName()));
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
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