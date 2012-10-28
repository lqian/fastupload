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
import java.io.OutputStream;
import java.io.Writer;

import net.sourceforge.fastupload.exception.ThresholdException;


/**
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 *
 */
public abstract class MultiPartFile extends MultiPartData {

	/*
	 * a valid full file name
	 */
	protected String name;
	protected int start;
	protected int end;
	protected int bytes = 0;
	protected boolean closed = false;
	
	
	
	public MultiPartFile(String name) {
		super(name);
	}
	
	public MultiPartFile(String name, String charset) {
		 super(name, charset);
	}
	
	/**
	 * write the file with the buffer, specified the start position and length to be write.
	 * @param buff
	 * @param off
	 * @param len
	 * @throws IOException
	 */
	protected void append(byte[] buff, int off, int len) throws IOException {
		bytes += len;
		if (threshold > 0 && bytes > threshold)
			throw ThresholdException.fileThresholdException(this);
	}

	
	/**
	 * abstract method that close {@link Writer} or {@link OutputStream} is open in parsing stage.
	 * also, the override method of sub-class make sure data was flushed. 
	 * @throws IOException
	 */
	public abstract void close() throws IOException;
	
	
	
	/**
	 * get all bytes in a boundary contains in the {@link  MultiPartFile} object 
	 * @return
	 */
	public abstract  byte[] getContentBuffer(); 
	
	/**
	 * check the current writer or out is closed
	 * 
	 * @return
	 */
	protected boolean closed() {
		return closed;
	}

	protected int getStart() {
		return start;
	}

	protected void setStart(int start) {
		this.start = start;
	}

	protected int getEnd() {
		return end;
	}

	protected void setEnd(int end) {
		this.end = end;
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
	 * always return <em>true</em> for {@link MultipartFile} object
	 */
	public boolean isFile() {
		return this.contentHeaderMap.isFile();
	}
}