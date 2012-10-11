package net.sourceforge.fastupload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.fastupload.util.UploadChunk;


public class MemoryUploadParser {

	private byte[] buffer;

	private byte[] boundary;

	private int off;

	private int length;

	private MultiPartDataFactory multiPartDataFactory;

	private UploadChunk chunk;

	private ContentHeaderMap contentHeaderMap;

	public MemoryUploadParser(byte[] buffer, byte[] boundary, MultiPartDataFactory multiPartDataFactory) {
		super();
		this.buffer = buffer;
		this.boundary = boundary;
		this.multiPartDataFactory = multiPartDataFactory;
		this.off = 0;
		this.length = buffer.length;
		this.chunk = new UploadChunk(this.buffer, this.boundary, off, length);
	}

	public MemoryUploadParser(byte[] buffer, byte[] boundary, int off, int length,
			MultiPartDataFactory multiPartDataFactory) {
		super();
		this.buffer = buffer;
		this.boundary = boundary;
		this.off = off;
		this.length = length;
		this.multiPartDataFactory = multiPartDataFactory;
		this.chunk = new UploadChunk(this.buffer, this.boundary, off, length);
	}

	/**
	 * parse the bytes of <em>buffer</em>, and create a {@link MultiPartData}
	 * object for every content within two <em>boundary</em> or
	 * <em>sub-boundary</em>
	 * 
	 * @return list of MultiPartData
	 */
	public List<MultiPartData> parseList() throws IOException {
		List<MultiPartData> multiparts = new ArrayList<MultiPartData>();

		while (chunk.find()) {
			chunk.readContentHeader();
			contentHeaderMap = chunk.getContentHeaderMap();
			if (contentHeaderMap.hasMultiPartMixed()) {
				this.writeMixedPartData(multiparts);
			} else {
				this.writeData(multiparts);
			}
		}

		return multiparts;
	}

	/**
	 * parse the bytes of <em>buffer</em>, and create a {@link MultiPartData}
	 * object for every content within two <em>boundary</em> or
	 * <em>sub-boundary</em>
	 * 
	 * @return name as key, MultiPartData object as value
	 */
	public HashMap<String, MultiPartData> parseMap() throws IOException {
		HashMap<String, MultiPartData> multiparts = new HashMap<String, MultiPartData>();
		UploadChunk chunk = new UploadChunk(this.buffer, this.boundary, off, length);
		while (chunk.find()) {
			chunk.readContentHeader();
			contentHeaderMap = chunk.getContentHeaderMap();
			this.writeData(multiparts);
		}
		return multiparts;
	}

	private void writeData(HashMap<String, MultiPartData> multiparts) throws IOException {
		MultiPartData mpd = this.doWriteData();
		multiparts.put(mpd.getName(), mpd);
	}

	/**
	 * 
	 * @param multiparts
	 */
	private void writeData(List<MultiPartData> multiparts) throws IOException {
		multiparts.add(this.doWriteData());
	}

	private MultiPartData doWriteData() throws IOException {
		MultiPartData mpd = this.contentHeaderMap.createMultiPartData(this.multiPartDataFactory);
		int s = chunk.readContentHeader() + 1;
		int len = chunk.getBoundEnd() - s - 2;
		if (len > 0)
			mpd.append(chunk.getBuffer(), s, len);
		return mpd;
	}

	private void writeMixedPartData(List<MultiPartData> multiparts) {
		// TODO maybe to do

	}

}
