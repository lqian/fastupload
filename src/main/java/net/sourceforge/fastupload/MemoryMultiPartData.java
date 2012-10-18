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

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * parse the bytes of multipart data, read those bytes into a buffer, also the
 * {@link StreamMultipartFile} object contains headers of multipart data
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class MemoryMultiPartData extends MultiPartData {

	private byte[] buffer;

	public MemoryMultiPartData(String name) {
		super(name);
	}
	
	public MemoryMultiPartData(String name, String charset) {
		super(name, charset);
	}

	@Override
	public void append(byte[] buff, int off, int len) throws IOException {
		super.append(buff, off, len);
		buffer = new byte[len];
		System.arraycopy(buff, off, buffer, 0, len);
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
		FileOutputStream out = new FileOutputStream(target);
		out.write(buffer);
		out.flush();
		out.close();
		return true;
	}

	/**
	 * return the buffer that contains whole content of a uploading-file
	 * represented by current {@MemoryMultiPartData}
	 * object, the method do not convert encoding.
	 * 
	 * @return byte[], bytes in the buffer
	 */
	public byte[] getContentBuffer() {
		return this.buffer;
	}

}
