public final class Line {
	private String content;
	private int lineNumber;
	private String fileName;

	public Line(String _content, int _lineNumber, String _fileName) {
		content = _content;
		lineNumber = _lineNumber;
		fileName = _fileName;
	}

	public String getContent() {
		return content;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getFileName() {
		return fileName;
	}
}