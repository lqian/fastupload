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
 * Un-like {@link HttpFileUploadParser}, {@link HttpMemoryUploadParser} parse
 * all uploading-form data into {@link MultiPartData} object, does not write
 * those data into file system. the API enables user has the ability that
 * perform uploading-form data in memory directly and with higher performance
 * than {@link HttpFileUploadParser}. base usage of the class.
 * 
 * <pre>
 * MultiPartDataFactory mpdf = new MemoryMultiPartDataFactory();  
 * 
 * HttpMemoryUploadParser httpMemoryUploadParser = new HttpMemoryUploadParser(  
 *                 request, mpdf);  
 *                 List<MemoryMultiPartData> list = httpMemoryUploadParser.parseList();  
 *   
 *  for (MemoryMultiPartData e : list) {  
 *     if (e.isFile()) {  
 *         e.toFile(System.getProperty("user.home" + "/" + e.getFileName());  
 *     } else {  
 *         if (e.getBytes() > 0)  
 *             System.out.println(new String(e.getContentBuffer()));  
 *     }  
 * }
 * </pre>
 * 
 * optional constructors for {@link MultiPartDataFactory}
 * 
 * <pre>
 * MultiPartDataFactory mpdf = new MemoryMultiPartDataFactory(&quot;utf-8&quot;);
 * MultiPartDataFactory mpdf = new MemoryMultiPartDataFactory(&quot;utf-8&quot;, 0x20000);
 * 
 * </pre>
 * 
 * alternative methods like {@link DiskFileFactory}
 * 
 * <pre>
 * mpdf.setParseThreshold(0x100000);
 * mpdf.setAllowedExtensions(&quot;.jpg, .png&quot;);
 * mpdf.setAllowedTypes(&quot;image/jpg&quot;);
 * 
 * <pre>
 * 
 * @see MultiPartData
 * @see HttpFileUploadParser
 * @see AbstractFactory
 * @see MemoryMultiPartDataFactory
 * @see MultiPartDataFactory
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class HttpMemoryUploadParser extends AbstractUploadParser {

	private MultiPartDataFactory multiPartDataFactory;


	private MemoryUploadParser memoryUploadParser;

	private ByteBuffer byteBuffer;

	public HttpMemoryUploadParser(HttpServletRequest request, MultiPartDataFactory multiPartDataFactory) throws IOException {
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

	public List<MemoryMultiPartData> parseList() throws IOException {
		return memoryUploadParser.parseList();
	}

	public HashMap<String, MemoryMultiPartData> parseMap() throws IOException {
		return memoryUploadParser.parseMap();
	}

	@Override
	protected long getParseThreshold() {
		return multiPartDataFactory.getParseThreshold();
	}
	
	public MultiPartDataFactory getMultiPartDataFactory() {
		return multiPartDataFactory;
	}

	public void setMultiPartDataFactory(MultiPartDataFactory multiPartDataFactory) {
		this.multiPartDataFactory = multiPartDataFactory;
	}


}
