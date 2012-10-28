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
import java.util.HashMap;

/**
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
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
		return _MULTIPART_MIXED_VALUE.equalsIgnoreCase(this.getContentType());
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
			String fileName = this.getFileName();
			if (fileName.trim().length() > 0) { // prevent
												// application/octet-stream
				String ct = this.getContentType();
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

	/**
	 * @param multiPartDataFactory
	 * @return
	 */
	public MemoryMultiPartData createMultiPartData(FileFactory multiPartDataFactory) {
		MemoryMultiPartData mpd = multiPartDataFactory.createMulitPartFile(this.getFileName(), MemoryMultiPartData.class);
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
		String fn = this.get(_FILE_NAME_KEY);
		if (fn != null) {
			int i = fn.lastIndexOf("\\");
			if (i!=-1) fn = fn.substring(i+1);
		}
		return fn;
	}

	public String getContentType() {
		return this.get(_CONTENT_TYPE_KEY);
	}

}
