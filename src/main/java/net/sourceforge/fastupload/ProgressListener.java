package net.sourceforge.fastupload;

public class ProgressListener {
	
	private HttpFileUploadParser fileUploadParser;
	
	public ProgressListener(HttpFileUploadParser fileUploadParser) {
		super();
		this.fileUploadParser = fileUploadParser;
	}

	public double progress() {
		return fileUploadParser.getReadBytes() * 1.0 / fileUploadParser.getContentLength();
	}

}
