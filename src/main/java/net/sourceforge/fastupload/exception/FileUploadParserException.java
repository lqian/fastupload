package net.sourceforge.fastupload.exception;

public class FileUploadParserException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3541635704807439099L;

	public FileUploadParserException() {
		super("incorrect encripty type, expected: multipart/form-data");
	}

}
