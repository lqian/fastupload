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

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.fastupload.exception.FileUploadParserException;
import net.sourceforge.fastupload.exception.ThresholdException;


/**
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 *
 */
public abstract class AbstractUploadParser {

	
	protected final String _ENCTYPE = "multipart/form-data";
	protected final String _HEADER_CONTENT_TYPE = "Content-type";
	protected final String _HEADER_CONTENT_LENGTH = "Content-Length";
	protected final String _BOUNDARY_PREFIX = "--";

	protected byte[] boundary;

	protected HttpServletRequest request;
	/**
	 * content length of HttpServletRequest header
	 */
	protected int contentLength;
	
	
	

	/**
	 * parse the encrypt type and boundary from the header of request, throws a
	 * {@link FileUploadParserException} if found the request is not
	 * <em>multipart/form-data</em>
	 * 
	 * @param buffer
	 * @param bound
	 * @param subBound
	 */

	protected void parseEnctype() {
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
	protected void parseContentLength() {
//		String entryValue = this.request.getHeader(_HEADER_CONTENT_LENGTH);
//		contentLength = Long.parseLong(entryValue);
		int parseThreshold = this.getParseThreshold();
		
		contentLength = request.getContentLength();
		if (parseThreshold > 0 && contentLength > parseThreshold)
			throw ThresholdException.parseThresholdException();
	}

	public long getContentLength() {
		return contentLength;
	}

	
	protected abstract int getParseThreshold() ; 
	
}
