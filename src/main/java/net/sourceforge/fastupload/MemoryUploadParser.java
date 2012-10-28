
/*
 * 
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package net.sourceforge.fastupload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.fastupload.util.UploadChunk;

/**
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 *
 */

public class MemoryUploadParser {

	private byte[] buffer;

	private byte[] boundary;

	private int off;

	private int length;

	private FileFactory fileFactory;

	private UploadChunk chunk;

	private ContentHeaderMap contentHeaderMap;

	public MemoryUploadParser(byte[] buffer, byte[] boundary, FileFactory fileFactory) {
		super();
		this.buffer = buffer;
		this.boundary = boundary;
		this.fileFactory = fileFactory;
		this.off = 0;
		this.length = buffer.length;
		this.chunk = new UploadChunk(this.buffer, this.boundary, off, length);
	}

	public MemoryUploadParser(byte[] buffer, byte[] boundary, int off, int length,
			FileFactory fileFactory) {
		super();
		this.buffer = buffer;
		this.boundary = boundary;
		this.off = off;
		this.length = length;
		this.fileFactory = fileFactory;
		this.chunk = new UploadChunk(this.buffer, this.boundary, off, length);
	}

	/**
	 * parse the bytes of <em>buffer</em>, and create a {@link MultiPartData}
	 * object for every content within two <em>boundary</em> or
	 * <em>sub-boundary</em>
	 * 
	 * @return list of MultiPartData
	 */
	public List<MultiPartFile> parseList() throws IOException {
		List<MultiPartFile> multiparts = new ArrayList<MultiPartFile>();

		while (chunk.find()) {
			chunk.readContentHeader();
			contentHeaderMap = chunk.getContentHeaderMap();
			if (fileFactory.acceptable(contentHeaderMap)){
				if (contentHeaderMap.hasMultiPartMixed()) {
					this.writeMixedPartData(multiparts);
				} else {
					this.writeData(multiparts);
				}
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
	public HashMap<String, ? extends MultiPartFile> parseMap() throws IOException {
		HashMap<String, MemoryMultiPartData> multiparts = new HashMap<String, MemoryMultiPartData>();
		UploadChunk chunk = new UploadChunk(this.buffer, this.boundary, off, length);
		while (chunk.find()) {
			chunk.readContentHeader();
			contentHeaderMap = chunk.getContentHeaderMap();
			this.writeData(multiparts);
		}
		return multiparts;
	}

	private void writeData(HashMap<String, MemoryMultiPartData> multiparts) throws IOException {
		MemoryMultiPartData mpd = this.doWriteData();
		multiparts.put(mpd.getFieldName(), mpd);
	}

	/**
	 * 
	 * @param multiparts
	 */
	private void writeData(List<MultiPartFile> multiparts) throws IOException {
		multiparts.add(this.doWriteData());
	}

	private MemoryMultiPartData  doWriteData() throws IOException {
		MemoryMultiPartData mpd = this.contentHeaderMap.createMultiPartData(this.fileFactory);
		int s = chunk.readContentHeader() + 1;
		int len = chunk.getBoundEnd() - s - 2;
		if (len > 0)
			mpd.append(chunk.getBuffer(), s, len);
		return mpd;
	}

	private void writeMixedPartData(List<MultiPartFile> multiparts) {
		// TODO maybe to do

	}

}
