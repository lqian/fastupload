package net.sourceforge.fastupload;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * parse the bytes of multipart data, read those bytes into a buffer, also the
 * {@link StreamMultipartFile} object contains headers of multipart data
 * 
 * @author linkqian
 * 
 */
public class MemoryMultiPartData extends MultiPartData {

	private byte[] buffer;

	public MemoryMultiPartData(String name) {
		super(name);
	}

	public MemoryMultiPartData(String name, String charset) {
		super(name, charset);
	}

	@Override
	public void append(byte[] buff, int off, int len) throws IOException {
		super.append(buff, off, len);
		buffer = new byte[len];
		System.arraycopy(buff, off, buffer, 0, len);
	}

	/**
	 * write the bytes to a file with correct charset
	 * 
	 * @param target
	 * @throws IOException
	 */
	public boolean toFile(String target) throws IOException {

		if (this.getBytes() == 0)
			return false;
		
		// TODO convert charset if it's text format
		FileOutputStream out = new FileOutputStream(target);
		out.write(buffer);
		out.flush();
		out.close();
		return true;
	}

	@Override
	public byte[] getContentBuffer() {
		return this.buffer;
	}

}
