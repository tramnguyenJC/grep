public class GrapeOptions
{
	public boolean printMatchedLinesWithoutFileName;
	public boolean printMatchedLinesWithFileName;
	public boolean noOutput;
	public boolean ignoreCases;
	public boolean fileNameOnly;

	public GrapeOptions(boolean printMatchedLinesWithoutFileName, boolean printMatchedLinesWithFileName,
		boolean noOutput, boolean ignoreCases, boolean fileNameOnly) {
		this.printMatchedLinesWithFileName = printMatchedLinesWithFileName;
		this.printMatchedLinesWithoutFileName = printMatchedLinesWithoutFileName;
		this.noOutput = noOutput;
		this.ignoreCases = ignoreCases;
		this.fileNameOnly = fileNameOnly;
	}
 };