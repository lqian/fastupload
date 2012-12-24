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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fastupload.util.BoundaryFinder;
import net.sourceforge.fastupload.util.UploadChunk;
import net.sourceforge.fastupload.MultiPart;

/**
 * Concrete class of {@link UploadParser} that implements main
 * <em>parseList()</em> function that return <em>List&lt;MultiPart&gt;</em> The
 * class read all bytes from {@link ServletInputStream} before parse it, which
 * behavior is not as same as {@link StreamUploadParser}
 * 
 * @see net.sourceforge.fastupload.FastUploadParser
 * @see net.sourceforge.fastupload.StreamUploadParser
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * @since 0.5.1
 */
public class MemoryUploadParser extends UploadParser {

	/**
	 * total length of bytes to be parse
	 */
	private int length;

	/**
	 * UploadChunk object to be create, as to find {@link MultiPart}
	 */
	private UploadChunk chunk;

	/**
	 * temporary ContentHeaderMap in the parsing process
	 */
	private ContentHeaderMap contentHeaderMap;

	public MemoryUploadParser(InputStream inputSteam, FileFactory fileFactory, byte[] boundary, int length) throws IOException {
		super(inputSteam, fileFactory, boundary);
		this.length = length;
		init();
	}

	/**
	 * read all bytes from input stream, create a {@link UploadChunk} object
	 * contains all bytes
	 * 
	 * @throws IOException
	 */
	private void init() throws IOException {
		byte[] stream = new byte[length];
		byte[] b = new byte[8192];
		int pos = 0;
		for (int c = 0; c != -1; c = inputSteam.read(b)) {
			System.arraycopy(b, 0, stream, pos, c);
			pos += c;
		}
		chunk = new UploadChunk(stream, new BoundaryFinder(boundary), fileFactory.getCharset());
	}

	/**
	 * parse the bytes of <em>buffer</em>, and create a {@link MultiPartData}
	 * object for every content within two <em>boundary</em> or
	 * <em>sub-boundary</em>
	 * 
	 * @return list of MultiPartData
	 */
	public List<MultiPart> parseList() throws IOException {
		List<MultiPart> multiparts = new ArrayList<MultiPart>();

		while (chunk.find()) {
			chunk.readContentHeader();
			contentHeaderMap = chunk.getContentHeaderMap();
			if (fileFactory.acceptable(contentHeaderMap)) {
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
	 * 
	 * @param multiparts
	 */
	private void writeData(List<MultiPart> multiparts) throws IOException {
		multiparts.add(this.doWriteData());
	}

	private MultiPart doWriteData() throws IOException {
		MultiPart mpd = fileFactory.createMultiPartFile(contentHeaderMap);
		int s = chunk.getContentStart();
		int len = chunk.getBoundEnd() - s - 2;
		if (len > 0)
			mpd.append(chunk.getBuffer(), s, len);
		return mpd;
	}

	private void writeMixedPartData(List<MultiPart> multiparts) {
		// TODO maybe to do
	}
}
