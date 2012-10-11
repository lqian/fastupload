package net.sourceforge.fastupload.exception;

import net.sourceforge.fastupload.MultiPartData;
import net.sourceforge.fastupload.MultiPartFile;


public class ThresholdException extends RuntimeException {

	/**
	 * Generated automatically
	 */
	private static final long serialVersionUID = -1004551904095764397L;

	public ThresholdException(String msg) {
		super(msg);
	}

	public static ThresholdException parseThresholdException() {
		return new ThresholdException("ServletRequest inpustream length exceeds ParseThreshold");
	}
	
	public static ThresholdException fileThresholdException(MultiPartFile multiPartFile) {
		return new ThresholdException("a MultiPartFile length exceeds ParseThreshold: " + multiPartFile);
	}

	
	public static ThresholdException newThresholdException(MultiPartData multiPartData) {
		return new ThresholdException("a MultiPartFile length exceeds ParseThreshold: " + multiPartData);
	}
}
