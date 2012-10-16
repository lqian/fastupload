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
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 *
 */
public class MultiPartBinaryFile extends MultiPartDiskFile {
	private FileOutputStream fos;
	
	public void append(byte[] buff, int off, int len) throws IOException {
		super.append(buff, off, len);
		fos.write(buff, off, len);
	}

	public void close() throws IOException {
		closed = true;
		fos.flush();
		fos.close();
	}
	
	public MultiPartBinaryFile(String name) throws IOException {
		super(name);
		fos = new FileOutputStream(name);
	}

	public MultiPartBinaryFile(String name, String charset) throws IOException {
		super(name, charset);
		fos = new FileOutputStream(name);
	}
	
}
