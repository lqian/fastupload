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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * the class provides write binary data of multipart/form-data boundary into a
 * disk file. just convert <em>file name</em> with specified charset
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public abstract class MultiPartDiskFile extends MultiPart {

	
	/**
	 * default constructor 
	 * @param  full file name 
	 * @throws UnsupportedEncodingException 
	 */
	public MultiPartDiskFile(String fileName) throws UnsupportedEncodingException {
		super(fileName);
	}

	public MultiPartDiskFile(String name, String charset) throws UnsupportedEncodingException {
		super(name, charset);
	}

	public boolean toFile(String dest) {
		return new File(getName()).renameTo(new File(dest));
	}

	@Override
	public byte[] getContentBuffer() {
		// not support the operation
		throw new RuntimeException("not a closed file, open denied");
	}

	/**
	 * open a {@link FileInputStream} object for the current
	 * {@link MultiPartDiskFile} object
	 */
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(getName());
	}

}
