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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;

import net.sourceforge.fastupload.exception.ThresholdException;

/**
 * 
 * Abstract class represent content between two boundary, called a
 * <em>MultiPart</em>, refer to <a
 * href="http://www.ietf.org/rfc/rfc1867.txt">RFC1867</a>. A multipart contains
 * <em>content type, composition-data, name, and optional filename</em> key
 * value pairs, content bytes start behind them.
 * 
 * @since 0.5.1
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public abstract class MultiPart {

	/*
	 * name of current object.
	 */
	protected String name;
	/*
	 * default charset name of current JVM
	 */
	protected String charset = Charset.defaultCharset().name();

	/*
	 * the content header map of current boundary or sub-boundary
	 */
	protected ContentHeaderMap contentHeaderMap;

	/*
	 * the count of bytes in the current {@link MultiPartFile} object, excludes
	 * the bytes of head
	 */
	private int bytes;

	/*
	 * the threshold of a {@link MultiPartFile} object.
	 */
	protected long threshold = 0;

	protected boolean closed = false;

	public MultiPart(String name) throws UnsupportedEncodingException {
		this.name = name;
	}

	public MultiPart(String name, String charset) throws UnsupportedEncodingException {
		this.name = name;
		this.charset = charset;
	}

	/**
	 * abstract method defines write a boundary content into a system file
	 * specified with <em>name</em>
	 * 
	 * @param name
	 *            , the full file name
	 * @return true if success.
	 * @throws IOException
	 */
	public abstract boolean toFile(String name) throws IOException;

	/**
	 * write the file with the buffer, specified the start position and length
	 * to be write.
	 * 
	 * @param buff
	 * @param off
	 * @param len
	 * @throws IOException
	 */
	protected void append(byte[] buff, int off, int len) throws IOException {
		if (len < 0)
			throw new java.lang.RuntimeException("negative  length");
		bytes += len;
		if (threshold > 0 && bytes > threshold)
			throw ThresholdException.fileThresholdException(this);
	}

	/**
	 * abstract method that close {@link Writer} or {@link OutputStream} is open
	 * in parsing stage. also, the override method of sub-class make sure data
	 * was flushed.
	 * 
	 * @throws IOException
	 */
	protected abstract void close() throws IOException;

	/**
	 * open a {@link InputStream} object for the {@link MultiPart} object if
	 * current boundary header contains file entity information
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream getInputStream() throws IOException;

	/**
	 * get all bytes of content in a boundary contains in the {@link MultiPart}
	 * object
	 * 
	 * @return
	 */
	public abstract byte[] getContentBuffer();

	/**
	 * convert bytes of buffer with default charset specified in factory if
	 * current boundary header does not contain file information
	 * 
	 * @return
	 */
	public String getString() throws UnsupportedEncodingException {
		if (this.isFile())
			throw new RuntimeException("not support operation");
		else
			return this.getString(charset);
	}

	/**
	 * convert bytes of buffer with default charset specified, if current
	 * boundary header does not contain file information
	 * 
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getString(String charset) throws UnsupportedEncodingException {
		if (this.isFile()) {
			throw new RuntimeException("not support operation");
		} else {
			return new String(this.getContentBuffer(), charset);
		}
	}

	/**
	 * check the current writer or out is closed
	 * 
	 * @return
	 */
	protected boolean isClosed() {
		return closed;
	}

	public String getCharset() {
		return charset;
	}

	protected void setCharset(String charset) {
		this.charset = charset;
	}

	public int getBytes() {
		return bytes;
	}

	protected void setBytes(int bytes) {
		this.bytes = bytes;
	}

	/**
	 * return the content type of content-header.
	 * 
	 * @return String
	 */
	public String getContentType() {
		return this.contentHeaderMap.getContentType();
	}

	/**
	 * determine whether current multipart/data-form boundary is a file
	 * 
	 * @return
	 */
	public boolean isFile() {
		return this.contentHeaderMap.isFile();
	}

	public ContentHeaderMap getContentHeaderMap() {
		return contentHeaderMap;
	}

	protected void setContentHeaderMap(ContentHeaderMap contentHeaderMap) {
		this.contentHeaderMap = contentHeaderMap;
	}

	protected long getThreshold() {
		return threshold;
	}

	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}

	public String getFieldName() {
		return this.contentHeaderMap.getName();
	}

	public String getFileName() {
		return this.contentHeaderMap.getFileName();
	}

	/**
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