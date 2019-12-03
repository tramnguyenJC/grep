public final class Line {
	private String line;
	private int lineNumber;
	private String fileName;

	public Line(String _line, int _lineNumber, String _fileName) {
		line = _line;
		lineNumber = _lineNumber;
		fileName = _fileName;
	}

	public String toString() {
		return fileName +": " + lineNumber + " " + line;
	}

	public String getLine() {
		return line;
	}
}