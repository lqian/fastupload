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

import net.sourceforge.fastupload.util.UploadChunk;



/**
 * 
 * @author linkqian
 *
 */
public class StreamUploadParser {

	private InputStream inputStream;

	private byte[] boundary;

	private int bufferSize = 0x2000;

	private FileFactory fileFactory;

	protected long readBytes;

	byte[] buffer = new byte[bufferSize];
	byte[] delta;
	private int c;
	private UploadChunk chunk;
	private List<MultiPartFile> files = new ArrayList<MultiPartFile>();
	private MultiPartFile multiPartFile;
	private ContentHeaderMap contentMap;

	public StreamUploadParser(InputStream inputStream, byte[] boundary, FileFactory fileFactory) {
		super();
		this.inputStream = inputStream;
		this.boundary = boundary;
		this.fileFactory = fileFactory;
	}
	
	public List<MultiPartFile> parse() throws IOException {

		while ((c = inputStream.read(buffer)) != -1) {
			readBytes += c;
			if (delta != null) {
				chunk = new UploadChunk(delta, boundary, 0);
				chunk.append(buffer, 0, c);
				delta = null;
			} else {
				chunk = new UploadChunk(buffer, boundary, 0, c);
			}
			while (chunk.find()) {
				chunk.readContentHeader();
				contentMap = chunk.getContentHeaderMap();
				
				if (this.fileFactory.acceptable(contentMap)) {
					if (contentMap.hasMultiPartMixed()) {
						this.writeMixedMultiPart();
					} else {
						this.writeMultiPart();
					}
				}
			}

			/*
			 * not found any boundary, append the whole chunk to the opening
			 * file; <br />
			 * 
			 * if only found the start boundary, but not find the whole content
			 * header, copy the bytes into <em>delta</em> buffer, <br />
			 * 
			 * else parse the content header to create a {@link MultiPartFile}
			 * object
			 */
			if (chunk.getBoundStart() == -1 && chunk.getBoundEnd() == -1 && multiPartFile != null && !multiPartFile.closed()) {
				multiPartFile.append(chunk.getBuffer(), 0, chunk.getBufferLength());
			} else if (chunk.getBoundStart() >= 0 && chunk.getBoundEnd() == -1) {
				if (multiPartFile != null && !multiPartFile.closed()) {
					multiPartFile.append(chunk.getBuffer(), 0, chunk.getBoundStart() );
					multiPartFile.close();
					files.add(multiPartFile);
				}
				int ce = chunk.readContentHeader();
				if (ce != -1) {
					contentMap = chunk.getContentHeaderMap();
					if (fileFactory.acceptable(contentMap)) {
						if (contentMap.hasMultiPartMixed()) {
							// parse mixed multipart data
							if (chunk.readContentHeader() != -1) {
								this.writeMixedMultiPart();
	
								// if not found the whole sub-boundary
								if (chunk.getBoundStart() > 0 && chunk.getBoundEnd() == -1) {
									ce = chunk.readContentHeader();
									if (ce == -1)
										this.createDelta();
									else {
										contentMap = chunk.getContentHeaderMap();
										if (fileFactory.acceptable(contentMap)) {
											multiPartFile = contentMap.createMultiPartFile(fileFactory);
											if (multiPartFile != null) {
												multiPartFile.append(chunk.getBuffer(), ce + 1, chunk.getBufferLength() - ce - 1);
											}
										}
									}
								}
							}
							else {
								this.createDelta();
							}
						} else {
							// parse multipart data
							multiPartFile = contentMap.createMultiPartFile(fileFactory);
							if (multiPartFile != null) {
								multiPartFile.append(chunk.getBuffer(), ce + 1, chunk.getBufferLength() - ce - 1);
							}
						}
					}
				} else {
					this.createDelta();
				}
			}
		}
		return files;
	}

	public long getReadBytes() {
		return readBytes;
	}

	private void createDelta() {
		int len = chunk.getBufferLength() - chunk.getBoundStart();
		delta = new byte[len];
		System.arraycopy(chunk.getBuffer(), chunk.getBoundStart(), delta, 0, len);
	}

	protected void writeMultiPart() throws IOException {
		contentMap = chunk.getContentHeaderMap();
		if (this.fileFactory.acceptable(contentMap)) {
			multiPartFile = contentMap.createMultiPartFile(fileFactory);

			if (multiPartFile != null) {
				int s = chunk.readContentHeader() + 1;
				int len = chunk.getBoundEnd() - s - 2;
				multiPartFile.append(chunk.getBuffer(), s, len);
				multiPartFile.close();
				files.add(multiPartFile);
			}
		}
	}

	protected void writeMixedMultiPart() throws IOException {
		byte[] subBound = contentMap.getSubBoundary();
		chunk.setSubBoundary(subBound);
		while (chunk.findSub()) {
			this.writeMultiPart();
			if (chunk.endSub())
				break;
		}
	}
}
