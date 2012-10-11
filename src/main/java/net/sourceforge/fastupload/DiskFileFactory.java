package net.sourceforge.fastupload;

import java.lang.reflect.Constructor;

public class DiskFileFactory extends AbstractParseThresholdFactory implements FileFactory {

	private String path;

	private String charset;

	private int threshold;
	
	public DiskFileFactory(String path) {
		this.path = this.marshal(path);
	}

	private String marshal(String path) {
		int i = path.lastIndexOf("/");
		if (i < path.length()) {
			path += "/";
		}
		return path;
	}

	public DiskFileFactory(String path, String charset) {
		super();
		this.path = this.marshal(path);
		this.charset = charset;
	}

	/**
	 * 
	 * @param path
	 *            should end with '/' character
	 * @param charset
	 * @param threshold
	 */
	public DiskFileFactory(String path, String charset, int threshold) {
		super();
		this.path = path;
		this.charset = charset;
		this.threshold = threshold;
	}

	@SuppressWarnings("unchecked")
	public <F extends MultiPartFile> F createMulitPartFile(String name, Class<? extends MultiPartFile> cls) {
		MultiPartFile instance = null;
		try {
			instance = charset != null ? doCreate(name, charset, cls) : doCreate(name, cls);
			instance.setThreshold(threshold);
		} catch (Exception e) {
			// ignore the exception
			e.printStackTrace();
		}
		return (F) instance;
	}

	@SuppressWarnings("unchecked")
	private <F extends MultiPartFile> F doCreate(String name, Class<? extends MultiPartFile> cls) throws Exception {
		Constructor<? extends MultiPartFile> constructor = cls.getConstructor(String.class);
		MultiPartFile instance = constructor.newInstance(path + name);
		return (F) instance;
	}

	@SuppressWarnings("unchecked")
	private <F extends MultiPartFile> F doCreate(String name, String charset, Class<? extends MultiPartFile> cls)
			throws Exception {
		name = new String(name.getBytes(), charset);

		Constructor<? extends MultiPartFile> constructor = cls.getConstructor(String.class, String.class);
		MultiPartFile instance = constructor.newInstance(path + name, charset);
		return (F) instance;
	}

	public String getPath() {
		return path;
	}

	public String getCharset() {
		return charset;
	}

	public int getThreshold() {
		return threshold;
	}

	//@SuppressWarnings("unchecked")
	//@Override
	//public <T extends MultiPartData> T createMulitPartFile(String name, Class<? extends MultiPartData> cls) {
	//	Class<? extends MultiPartFile> subClass = (Class<? extends MultiPartFile>) cls.asSubclass(cls);
	//	return (T) this.createMulitPartDiskFile(name, subClass);
	//}
}
