/*
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

import java.util.HashMap;

/**
 * Represent headers of a boundary of multipart/form-data input stream. refer to
 * <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC1867</a>， Every boundary has
 * it owner header. for example,<br/>
 * -----------------------------------------------------------------------------
 * 
 * <pre>
 * --AaB03x
 *    content-disposition: form-data; name="pics"; filename="file1.txt"
 *    Content-Type: text/plain
 * </pre>
 * 
 * ----------------------------------------------------------------------------
 * <p/> The header contains key information for form-based uploading file.
 * The class use {@link Map} structure stores this information and provide some
 * convenience methods to fetch this information from the map.
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
	 * check content type in header is a textable
	 * 
	 * @return <em>true</em> if content type entity starts with <em>text/</em>
	 *         pattern, or the header doesn't contain content type entity
	 */
	public boolean isTextable() {
		return this.getContentType() == null ? true : getContentType().startsWith(_TEXT_CONTENT_TYPE_PREFIX);
	}

	/**
	 * get sub-boundary if current boundary header is mixed
	 * 
	 * @return
	 */
	public byte[] getSubBoundary() {
		return this.get(_BOUNDARY_KEY).getBytes();
	}

	/**
	 * check current boundary header whether contains <em>Content-Type</em> entity
	 * 
	 * @return
	 */
	public boolean isFile() {
		return this.containsKey(_FILE_NAME_KEY);
	}

	/**
	 * get the name entity from current boundary header, also the name is a
	 * input field name in HTML form uploading
	 * 
	 * @return
	 */
	public String getName() {
		return this.get(_NAME_KEY);
	}

	/**
	 * if current boundary header contains <em>filename</em> entity, parse its
	 * value.
	 * 
	 * some earlier IE browser encode full name of file system in the client
	 * 
	 * @return
	 */
	public String getFileName() {
		String fn = this.get(_FILE_NAME_KEY);
		if (fn != null) {
			int i = fn.lastIndexOf("\\");
			if (i != -1)
				fn = fn.substring(i + 1);
		}
		return fn;
	}

	/**
	 * get the content type of current boundary header
	 * 
	 * @return
	 */
	public String getContentType() {
		return this.get(_CONTENT_TYPE_KEY);
	}

}
