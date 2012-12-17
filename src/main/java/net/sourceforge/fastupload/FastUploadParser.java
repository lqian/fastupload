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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.fastupload.exception.FileUploadParserException;
import net.sourceforge.fastupload.exception.ThresholdException;


/**
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 *
 */
public class FastUploadParser {

	
	protected final String _ENCTYPE = "multipart/form-data";
	protected final String _HEADER_CONTENT_TYPE = "Content-type";
	protected final String _HEADER_CONTENT_LENGTH = "Content-Length";
	protected final String _BOUNDARY_PREFIX = "--";

	protected byte[] boundary;

	private HttpServletRequest request;
	
	/**
	 * content length of HttpServletRequest header
	 */
	private int contentLength;
	
	/**
	 * progress listener
	 */
	private ProgressListener progressListener;
	
	private UploadParser uploadParser;
	
	private FileFactory fileFactory = FileFactory.getInstance();
	
	
	/**
	 * default constructor 
	 * @param request {@link HttpServletRequest}
	 */
	public FastUploadParser(HttpServletRequest request) throws IOException {
		super();
		this.request = request;
		this.init();
	}

	public FastUploadParser(HttpServletRequest request, FileFactory fileFactory) throws IOException {
		super();
		this.request = request;
		this.fileFactory = fileFactory;
		this.init();
	}
	 
	
	private void init() throws IOException {
		this.parseEnctype();
		this.parseContentLength();
		if (fileFactory.repository==null || fileFactory.repository.trim().equals("")) {
			uploadParser = new MemoryUploadParser(request.getInputStream(),fileFactory, boundary, contentLength);
		}
		else {
			uploadParser = new StreamUploadParser(request.getInputStream(),fileFactory, boundary);
		}
	}
	
	/**
	 * delegate execution of <code>UploadParser.parseList()</code> method;
	 * 
	 * @return List&lt;MultiPartFile&gt;
	 * @throws IOException
	 */
	public   List<MultiPartFile> parseList() throws IOException  {
		return uploadParser.parseList();
	}
	
	
	/**
	 *  delegate execution of <code>UploadParser.parseMap()</code> method;
	 * 
	 * @return  Map&lt;String, MultiPartFile&gt;
	 * @throws IOException
	 */
	public  Map<String, MultiPartFile> parseMap() throws IOException {
		return uploadParser.parseMap();
	}
	

	/**
	 * parse the encrypt type and boundary from the header of request, throws a
	 * {@link FileUploadParserException} if found the request is not
	 * <em>multipart/form-data</em>
	 * 
	 * @param buffer
	 * @param bound
	 * @param subBound
	 */

	private void parseEnctype() {
		String[] content = request.getHeader(_HEADER_CONTENT_TYPE).split(";");
		if (content.length > 1) {
			if (!_ENCTYPE.equalsIgnoreCase(content[0])) {
				throw new FileUploadParserException();
			}
			boundary = (_BOUNDARY_PREFIX + content[1].split("=")[1]).getBytes();
		} else {
			throw new FileUploadParserException();
		}
	}

	/**
	 * parse the length of submitted request, DO NOT catch any converting
	 * runtime exception
	 */
	private void parseContentLength() {
		long maxContentLength = this.fileFactory.getMaxContentLength();
		
		contentLength = request.getContentLength();
		if (maxContentLength > 0 && contentLength > maxContentLength)
			throw ThresholdException.parseThresholdException();
	}

	public long getContentLength() {
		return contentLength;
	}

	/**
	 * @return the progressListener
	 */
	public ProgressListener getProgressListener() {
		return progressListener;
	}

	/**
	 * @param progressListener the progressListener to set
	 */
	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	public long getReadBytes() {
		return this.uploadParser.getReadBytes();
	}
}
