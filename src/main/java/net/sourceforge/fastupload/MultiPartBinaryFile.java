package net.sourceforge.fastupload;

import java.io.FileOutputStream;
import java.io.IOException;

public class MultiPartBinaryFile extends MultiPartDiskFile {
	private FileOutputStream fos;
	
	public void append(byte[] buff, int off, int len) throws IOException {
		super.append(buff, off, len);
		fos.write(buff, off, len);
	}

	public void close() throws IOException {
		closed = true;
		fos.flush();
		fos.close();
	}
	
	public MultiPartBinaryFile(String name) throws IOException {
		super(name);
		fos = new FileOutputStream(name);
	}

	public MultiPartBinaryFile(String name, String charset) throws IOException {
		super(name, charset);
		fos = new FileOutputStream(name);
	}
	
}
