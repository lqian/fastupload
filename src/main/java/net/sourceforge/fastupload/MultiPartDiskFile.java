package net.sourceforge.fastupload;

import java.io.File;

public abstract class MultiPartDiskFile extends MultiPartFile {


	public MultiPartDiskFile(String name) {
		super(name);
	}

	public MultiPartDiskFile(String name, String charset) {
		super(name, charset);
	}

	public boolean toFile(String dest) {
		return new File(this.name).renameTo(new File(dest));
	}
	
	/**
	 * do not return byte for {@link MultiPartDiskFile}
	 */
	public byte[] getContentBuffer() {
		return  null;
	}
}
