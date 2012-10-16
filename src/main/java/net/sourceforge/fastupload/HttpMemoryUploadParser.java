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
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
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
