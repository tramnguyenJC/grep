public class GrepOptions
{
	public boolean printMatchedLinesWithoutFileName;
	public boolean printMatchedLinesWithFileName;
	public boolean noOutput;
	public boolean ignoreCases;
	public boolean fileNameOnly;

	public GrepOptions(boolean printMatchedLinesWithoutFileName, boolean printMatchedLinesWithFileName,
		boolean noOutput, boolean ignoreCases, boolean fileNameOnly) {
		this.printMatchedLinesWithFileName = printMatchedLinesWithFileName;
		this.printMatchedLinesWithFileName = printMatchedLinesWithFileName;
		this.noOutput = noOutput;
		this.ignoreCases = ignoreCases;
		this.fileNameOnly = fileNameOnly;
	}
 };