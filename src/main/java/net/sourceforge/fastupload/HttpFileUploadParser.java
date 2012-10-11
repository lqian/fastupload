package net.sourceforge.fastupload;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * the class provides the high level API that parse uploading file of
 * multipart/form-data in {@link HttpServletRequest} input stream.
 * 
 * @author linkqian
 * 
 */
public class HttpFileUploadParser extends AbstractUploadParser {

	private FileFactory fileFactory;

	private ProgressListener progressListener;

	private StreamUploadParser uploadParser;

	public HttpFileUploadParser(HttpServletRequest request, FileFactory fileFactory) {
		this.request = request;
		this.fileFactory = fileFactory;
	}

	/**
	 * parse the request of multiple part data of form. and write content of
	 * file into file at the directory specified by a {@link FileFactory}
	 * object. finally, return a list contains the object of
	 * {@link MultiPartFile}
	 * 
	 * @return List<MultiPartFile>
	 * @throws IOException
	 */
	public List<MultiPartFile> parse() throws IOException {
		this.parseContentLength();
		this.parseEnctype();
		this.uploadParser = new StreamUploadParser(request.getInputStream(), boundary, fileFactory);
		return uploadParser.parse();
	}

	@Override
	public int getParseThreshold() {
		return this.fileFactory.getParseThreshold();
	}
	
	
	public double getReadBytes() {
		return uploadParser.getReadBytes();
	}
	
	public ProgressListener getProgressListener() {
		return progressListener;
	}

	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	public byte[] getBoundary() {
		return boundary;
	}

	public void setBoundary(byte[] boundary) {
		this.boundary = boundary;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public FileFactory getFileFactory() {
		return fileFactory;
	}

	public void setFileFactory(FileFactory fileFactory) {
		this.fileFactory = fileFactory;
	}

	public StreamUploadParser getUploadParser() {
		return uploadParser;
	}

	public void setUploadParser(StreamUploadParser uploadParser) {
		this.uploadParser = uploadParser;
	}


	

}
