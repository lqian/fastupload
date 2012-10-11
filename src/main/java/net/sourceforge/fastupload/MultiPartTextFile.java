package net.sourceforge.fastupload;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class MultiPartTextFile extends MultiPartDiskFile {

	private Writer writer;
	
	public MultiPartTextFile(String name) throws IOException {
		super(name);
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name)));
	}
	public MultiPartTextFile(String name, String charset) throws IOException {
		super(name, charset);
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), charset));
	}
	
	public void append(byte[] buff, int off, int len) throws IOException {
		super.append(buff, off, len);
		byte[] wb = new byte[len];
		System.arraycopy(buff, off, wb, 0, len);
		writer.write(new String(wb, super.charset));
	}

	public void close() throws IOException {
		closed = true;
		writer.flush();
		writer.close();
	}
}
