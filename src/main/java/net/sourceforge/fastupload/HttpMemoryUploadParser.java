/**
 
 */
package net.sourceforge.fastupload;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * @author linkqian
 * 
 */
public class HttpMemoryUploadParser extends AbstractUploadParser {

	private MultiPartDataFactory multiPartDataFactory;

	private MemoryUploadParser memoryUploadParser;

	private ByteBuffer byteBuffer;

	public HttpMemoryUploadParser(HttpServletRequest request, MultiPartDataFactory multiPartDataFactory)
			throws IOException {
		super();
		this.request = request;
		this.multiPartDataFactory = multiPartDataFactory;
		this.init();
		this.memoryUploadParser = new MemoryUploadParser(byteBuffer.array(), this.boundary, multiPartDataFactory);
	}

	private void init() throws IOException {
		this.parseEnctype();
		this.parseContentLength();
		byteBuffer = ByteBuffer.allocate((int) this.contentLength);

		byte[] buffer = new byte[0x2000];
		ServletInputStream inputStream = this.request.getInputStream();
		for (int c = 0; c != -1; c = inputStream.read(buffer)) {
			byteBuffer.put(buffer, 0, c);
		}
	}
	
	
	public List<MultiPartData> parseList() throws IOException {
		return memoryUploadParser.parseList();
	}

	
	public HashMap<String, MultiPartData> parseMap() throws IOException {
		return memoryUploadParser.parseMap();
	}
	
	
	
	@Override
	protected int getParseThreshold() {
		return multiPartDataFactory.getParseThreshold();
	}

}
