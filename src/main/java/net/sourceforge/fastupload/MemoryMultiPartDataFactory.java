package net.sourceforge.fastupload;

import java.lang.reflect.Constructor;

/**
 * 
 * @author linkqian
 * 
 */
public class MemoryMultiPartDataFactory extends AbstractParseThresholdFactory implements MultiPartDataFactory {

	private String charset;

	private int threshold = 0;

	
	public MemoryMultiPartDataFactory() {
		super();
	}

	public MemoryMultiPartDataFactory(String charset) {
		super();
		this.charset = charset;
	}

	public MemoryMultiPartDataFactory(String charset, int threshold) {
		super();
		this.charset = charset;
		this.threshold = threshold;
	}

	@SuppressWarnings("unchecked")
	public <T extends MultiPartData> T createMultiPartData(String name, Class<? extends MultiPartData> cls) {
		try {
			MultiPartData instance = charset == null ? doCreate(name, cls) : doCreate(name, charset, cls);
			instance.setThreshold(threshold);
			return (T) instance;
		} catch (Exception e) {
			// ignore the exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * create an object which class extends {@link MultiPartData}.
	 * 
	 * @param name
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected <T extends MultiPartData> T doCreate(String name, Class<? extends MultiPartData> cls) throws Exception {
		MultiPartData mpd = null;
		Constructor<? extends MultiPartData> constructor = cls.getConstructor(String.class);
		mpd = constructor.newInstance(name);
		return (T) mpd;
	}

	/**
	 * create an object which class extends {@link MultiPartData}. the method
	 * just convert the name with specified <em>charset</em>.
	 * 
	 * @param name
	 * @param charset
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected <T extends MultiPartData> T doCreate(String name, String charset, Class<? extends MultiPartData> cls)
			throws Exception {
		MultiPartData mpd = null;
		Constructor<? extends MultiPartData> constructor = cls.getConstructor(String.class, String.class);

		// convert the charset specified
		name = new String(name.getBytes(), charset);
		mpd = constructor.newInstance(name, charset);
		return (T) mpd;
	}
}
