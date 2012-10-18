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

import net.sourceforge.fastupload.exception.ThresholdException;

/**
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 * 
 */
public abstract class MultiPartData {

	private String name;
	/*
	 * default charset iso-8859-1
	 */
	protected String charset = "ISO-8859-1";

	/*
	 * the content header map of current boundary or sub-boundary
	 */
	protected ContentHeaderMap contentHeaderMap;

	/*
	 * the count of bytes in the current {@link MultiPartData} object, excludes
	 * the bytes of head
	 */
	private int bytes;

	/*
	 * the threshold of a {@link MultiPartData} object.
	 */
	protected int threshold = 0;
	
	public MultiPartData(String name) {
		super();
		this.name = name;
	}

	public MultiPartData(String name, String charset) {
		super();
		this.name = name;
		this.charset = charset;
	}

	/**
	 * abstract method that write the content of multipart/data into a file.
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public abstract boolean toFile(String name) throws IOException;

	/**
	 * return the content type of content-header.
	 * 
	 * @return String
	 */
	public String getContentType() {
		return this.contentHeaderMap.getContentType();
	}

	/**
	 * determine multipart/data is a file
	 * 
	 * @return
	 */
	public boolean isFile() {
		return this.contentHeaderMap.isFile();
	}

	/**
	 * abstract append method that just judge the <em>bytes</em> whether exceeds
	 * the <em>threshold</em>. Concreted sub-class must implement the IO append
	 * perform.
	 * 
	 * @param buff
	 * @param off
	 * @param len
	 * @throws IOException
	 */
	public void append(byte[] buff, int off, int len) throws IOException {
		bytes += len;
		if (threshold > 0 && bytes > threshold)
			throw ThresholdException.newThresholdException(this);
	}

	public ContentHeaderMap getContentHeaderMap() {
		return contentHeaderMap;
	}

	protected void setContentHeaderMap(ContentHeaderMap contentHeaderMap) {
		this.contentHeaderMap = contentHeaderMap;
	}

	/**
	 * the content length in bytes for current {@MultiPartData }
	 * object
	 * 
	 * @return the bytes
	 */
	public int getBytes() {
		return bytes;
	}

	protected int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public String getFieldName() {
		return this.contentHeaderMap.getName();
	}

	public String getFileName() {
		return this.contentHeaderMap.getFileName();
	}

	/**
	 * @deprecated
	 * 
	 * @return the name, is the full name of file system if in direct writing
	 *         disk model, else is the name regarding the form input name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}

}
