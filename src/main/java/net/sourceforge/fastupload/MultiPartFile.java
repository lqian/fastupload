package net.sourceforge.fastupload;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import net.sourceforge.fastupload.exception.ThresholdException;


public abstract class MultiPartFile extends MultiPartData {

	protected int start;
	protected int end;
	protected int bytes = 0;
	protected boolean closed = false;
	
	public MultiPartFile(String name) {
		super(name);
	}
	
	public MultiPartFile(String name, String charset) {
		super(name, charset);
	}
	
	/**
	 * write the file with the buffer, specified the start position and length to be write.
	 * @param buff
	 * @param off
	 * @param len
	 * @throws IOException
	 */
	public void append(byte[] buff, int off, int len) throws IOException {
		bytes += len;
		if (threshold > 0 && bytes > threshold)
			throw ThresholdException.fileThresholdException(this);
	}

	
	/**
	 * abstract method that close {@link Writer} or {@link OutputStream} is open in parsing stage.
	 * also, the override method of sub-class make sure data was flushed. 
	 * @throws IOException
	 */
	public abstract void close() throws IOException;
	
	/**
	 * check the current writer or out is closed
	 * 
	 * @return
	 */
	protected boolean closed() {
		return closed;
	}

	protected int getStart() {
		return start;
	}

	protected void setStart(int start) {
		this.start = start;
	}

	protected int getEnd() {
		return end;
	}

	protected void setEnd(int end) {
		this.end = end;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getBytes() {
		return bytes;
	}

	public void setBytes(int bytes) {
		this.bytes = bytes;
	}

	/**
	 * always return <em>true</em> for {@link MultipartFile} object
	 */
	public boolean isFile() {
		return true;
	}

	
	
	
}