package net.sourceforge.fastupload;

import java.io.IOException;

import net.sourceforge.fastupload.exception.ThresholdException;

/**
 * @author linkqian
 * 
 * 
 */
public abstract class MultiPartData {

	protected String name;

	/*
	 * default charset iso-8859-1
	 */
	protected String charset = "ISO-8859-1";

	/*
	 * the content header map of current boundary or sub-boundary
	 */
	protected ContentHeaderMap contentHeaderMap;

	/*
	 * the count of bytes in the current {@link MultiPartData} object, excludes
	 * the bytes of head
	 */
	private int bytes;

	/*
	 * the threshold of a {@link MultiPartData} object.
	 */
	protected int threshold = 0;

	public MultiPartData(String name) {
		this.name = name;
	}

	public MultiPartData(String name, String charset) {
		this.name = name;
		this.charset = charset;
	}

	public abstract boolean toFile(String name) throws IOException;

	public abstract byte[] getContentBuffer();

	/**
	 * determine multipart/data is a file
	 * 
	 * @return
	 */
	public boolean isFile() {
		return this.contentHeaderMap.isFile();
	}

	public void append(byte[] buff, int off, int len) throws IOException {
		bytes += len;
		if (threshold > 0 && bytes > threshold)
			throw ThresholdException.newThresholdException(this);
	}

	public ContentHeaderMap getContentHeaderMap() {
		return contentHeaderMap;
	}

	protected void setContentHeaderMap(ContentHeaderMap contentHeaderMap) {
		this.contentHeaderMap = contentHeaderMap;
	}

	/**
	 * @return the bytes
	 */
	public int getBytes() {
		return bytes;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public String getName() {
		return this.name;
	}

	public String getFileName() {
		return this.contentHeaderMap.getFileName();
	}

}
