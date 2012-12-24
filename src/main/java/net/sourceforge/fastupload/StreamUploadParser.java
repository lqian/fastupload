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
import java.util.List;

import net.sourceforge.fastupload.util.BoundaryFinder;
import net.sourceforge.fastupload.util.UploadChunk;

/**
 * A concrete class of {@link UploadParser} that override <em> parseList() </em>
 * function. It parse the buffer that read <em>0x2000</em> bytes from
 * {@link ServletInputStream} , till all bytes was read and parsed.
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class StreamUploadParser extends UploadParser {

	private int bufferSize = 0x2000;

	private UploadChunk chunk;

	private MultiPart multiPartFile;

	private ContentHeaderMap contentMap;

	private BoundaryFinder boundaryFinder;

	public StreamUploadParser(InputStream inputSteam, FileFactory fileFactory, byte[] boundary) {
		super(inputSteam, fileFactory, boundary);
		this.boundaryFinder = new BoundaryFinder(boundary);
		this.chunk = new UploadChunk(boundaryFinder, fileFactory.getEncoding());
	}

	@Override
	public List<MultiPart> parseList() throws IOException {
		byte[] delta = null;
		int c = 0;
		byte[] buff = new byte[bufferSize];
		while ((c = inputSteam.read(buff)) != -1) {
			readBytes += c;
			if (delta != null) {
				chunk.setBuffer(delta);
				// chunk = new UploadChunk(delta, boundaryFinder, 0);
				chunk.append(buff, 0, c);
				delta = null;
			} else {
				// chunk = new UploadChunk(buffer, boundaryFinder, 0, c);
				chunk.setBuffer(buff, 0, c);
			}
			while (chunk.find()) {
				chunk.readContentHeader();
				contentMap = chunk.getContentHeaderMap();

				if (fileFactory.acceptable(contentMap)) {
					if (contentMap.hasMultiPartMixed()) {
						this.writeMixedMultiPart();
					} else {
						if (multiPartFile != null && !multiPartFile.isClosed()) {
							multiPartFile.append(chunk.getBuffer(), 0, chunk.getBoundStart() - 2);
							multiPartFile.close();
							files.add(multiPartFile);
						}
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
			if (chunk.getBoundStart() == -1 && chunk.getBoundEnd() == -1 && multiPartFile != null && !multiPartFile.isClosed()) {
				multiPartFile.append(chunk.getBuffer(), 0, chunk.getBufferLength());
			} else if (chunk.getBoundStart() >= 0 && chunk.getBoundEnd() == -1) {
				if (multiPartFile != null && !multiPartFile.isClosed()) {
					multiPartFile.append(chunk.getBuffer(), 0, chunk.getBoundStart() - 2);
					multiPartFile.close();
					files.add(multiPartFile);
				}

				// append to delta?
				int ct = chunk.readContentHeader();
				if (ct != -1) { // has whole content header
					contentMap = chunk.getContentHeaderMap();
					if (fileFactory.acceptable(contentMap)) {
						if (contentMap.hasMultiPartMixed()) {
							// parse mixed multipart data
							if (chunk.readContentHeader() != -1) {
								this.writeMixedMultiPart();

								// if not found the whole sub-boundary
								if (chunk.getBoundStart() > 0 && chunk.getBoundEnd() == -1) {
									ct = chunk.readContentHeader();
									if (ct == -1)
										delta = createDelta();
									else {
										contentMap = chunk.getContentHeaderMap();
										if (fileFactory.acceptable(contentMap)) {
											multiPartFile = fileFactory.createMultiPartFile(contentMap);
											if (multiPartFile != null) {
												multiPartFile.append(chunk.getBuffer(), ct, chunk.getBufferLength() - ct);
											}
										}
									}
								}
							} else {
								delta = createDelta();
							}
						} else {
							// create a multipartfile when not multipart/mixed
							// content type
							multiPartFile = fileFactory.createMultiPartFile(contentMap);
							if (multiPartFile != null && !multiPartFile.isClosed()) {
								multiPartFile.append(chunk.getBuffer(), ct, chunk.getBufferLength() - ct);
							}
						}
					}
				} else {
					delta = createDelta(); // create delta buffer as first part
											// of buffer in next loop
				}
			}
		}

		inputSteam.close();

		return files;
	}

	private byte[] createDelta() {
		int len = chunk.getBufferLength() - chunk.getBoundStart();
		byte[] delta = new byte[len];
		System.arraycopy(chunk.getBuffer(), chunk.getBoundStart(), delta, 0, len);
		return delta;
	}

	private void writeMultiPart() throws IOException {
		contentMap = chunk.getContentHeaderMap();
		if (fileFactory.acceptable(contentMap)) {
			multiPartFile = fileFactory.createMultiPartFile(contentMap);

			int s = chunk.getContentStart();
			int len = chunk.getBoundEnd() - s - 2;
			multiPartFile.append(chunk.getBuffer(), s, len);
			multiPartFile.close();
			files.add(multiPartFile);
		}
	}

	private void writeMixedMultiPart() throws IOException {
		byte[] subBound = contentMap.getSubBoundary();
		chunk.setSubBoundary(subBound);
		while (chunk.findSub()) {
			this.writeMultiPart();
			if (chunk.endSub())
				break;
		}
	}
}
