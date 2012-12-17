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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * parse the bytes of multipart data, read those bytes into a buffer, also the
 * {@link StreamMultipartFile} object contains headers of multipart data
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class MemoryMultiPartFile extends MultiPartFile {

	/**
	 * shared buffer of current multipart/form-data input stream
	 */
	private byte[] buffer;
	
	/**
	 * content start position of a boundary
	 */
	private int contentStart;
	
	/**
	 * length of bytes for content of current boundary
	 */
	protected int len;

	public MemoryMultiPartFile(String name) throws UnsupportedEncodingException {
		super(name);
	}

	public MemoryMultiPartFile(String name, String charset) throws UnsupportedEncodingException {
		super(name, charset);
	}

	@Override
	public void append(byte[] buff, int off, int len) throws IOException {
		super.append(buff, off, len);
		this.buffer = buff;
		this.contentStart = off;
		this.len = len;
		
	}

	/**
	 * write the bytes to a file with correct charset
	 * 
	 * @param target
	 * @throws IOException
	 */
	public boolean toFile(String target) throws IOException {
		if (this.getBytes() == 0)
			return false;

		// TODO convert charset if it's text format
		if (contentHeaderMap.isTextable()) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), charset));
			byte[] buff = new byte[len];
			System.arraycopy(buffer, contentStart, buff, 0, len);
			writer.write(new String(buff, charset));
			writer.flush();
			writer.close();
			return true;
		}
		else {
			FileOutputStream out = new FileOutputStream(target);
			out.write(buffer, contentStart, len);
			out.flush();
			out.close();
			return true;
		}
	}

	/**
	 * return the buffer that contains whole content of a uploading-file
	 * represented by current {@MemoryMultiPartData}
	 * object, the method do not convert encoding.
	 * 
	 * @return byte[], bytes in the buffer
	 */
	public byte[] getContentBuffer() {
		byte[] buff = new byte[len];
		System.arraycopy(buffer, contentStart, buff, 0, len);
		return buff;
	}

	@Override
	public void close() throws IOException {
		this.closed = true;
	}

	/**
	 * 
	 */
	public InputStream getInputStream() throws IOException {
		if (this.isFile())
			return new ByteArrayInputStream(this.getContentBuffer());
		else
			return null;
	}
}
