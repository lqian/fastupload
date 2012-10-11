package net.sourceforge.fastupload;

import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * @author linkqian
 * 
 */
public class ContentHeaderMap extends HashMap<String, String> {

	/**
	 * serial uid generated automatically
	 */
	private static final long serialVersionUID = 4167193869087487721L;

	private static final String _FILE_NAME_KEY = "filename";
	private static final String _NAME_KEY = "name";
	private static final String _CONTENT_TYPE_KEY = "Content-Type";
	private static final String _TEXT_CONTENT_TYPE_PREFIX = "text/";
	private static final String _MULTIPART_MIXED_VALUE = "multipart/mixed";
	private static final String _BOUNDARY_KEY = "boundary";

	/**
	 * 
	 * determine current the header is MultiPart mixed data. the content
	 * contains sub-boundary if it is.
	 * 
	 * @return
	 */
	public boolean hasMultiPartMixed() {
		return _MULTIPART_MIXED_VALUE.equalsIgnoreCase(this.get(_CONTENT_TYPE_KEY));
	}

	/**
	 * parse file name and start position of its content in the buffer and
	 * determines whether file is binary or text, the function changes the
	 * <em>p</em> value by find the boundary.
	 * 
	 * @param buffer
	 * @param pos
	 *            , point the current byte of the buffer.
	 * @return a {@link MultiPartFile} object if find a uploading file, else
	 *         return null.
	 * @throws IOException
	 */
	public MultiPartFile createMultiPartFile(FileFactory fileFactory) {
		MultiPartFile mpf = null;
		if (this.isFile()) {
			String fileName = this.get(_FILE_NAME_KEY);
			if (fileName.trim().length() > 0) {  // prevent application/octet-stream
				String ct = this.get(_CONTENT_TYPE_KEY);
				if (ct.indexOf(_TEXT_CONTENT_TYPE_PREFIX) != -1) {
					mpf = fileFactory.createMulitPartFile(fileName, MultiPartTextFile.class);
				} else {
					mpf = fileFactory.createMulitPartFile(fileName, MultiPartBinaryFile.class);
				}
				mpf.setContentHeaderMap(this); // set content header map
			}
		}
		return mpf;
	}

	public MultiPartData createMultiPartData(MultiPartDataFactory multiPartDataFactory) {
		MultiPartData mpd = multiPartDataFactory.createMultiPartData(this.getName(), MemoryMultiPartData.class);
		mpd.setContentHeaderMap(this);
		return mpd;
	}

	public byte[] getSubBoundary() {
		return this.get(_BOUNDARY_KEY).getBytes();
	}

	public boolean isFile() {
		return this.containsKey(_FILE_NAME_KEY);
	}

	public String getName() {
		return this.get(_NAME_KEY);
	}

	public String getFileName() {
		return this.get(_FILE_NAME_KEY);
	}

	public String getContentType() {
		return this.get(_CONTENT_TYPE_KEY);
	}

}
