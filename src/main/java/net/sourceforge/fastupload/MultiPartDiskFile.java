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
import java.nio.ByteBuffer;

/**
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public abstract class MultiPartDiskFile extends MultiPartFile {


	public MultiPartDiskFile(String name) {
		super(name);
	}

	public MultiPartDiskFile(String name, String charset) {
		super(name, charset);
	}

	public boolean toFile(String dest) {
		return new File(this.name).renameTo(new File(dest));
	}

	public byte[] getContentBuffer() {
		if (!closed())
			throw new RuntimeException("not a closed file, open denied");

		try {
			FileInputStream fis = new FileInputStream(this.name);
			ByteBuffer buffer = ByteBuffer.allocate(fis.available());

			byte[] b = new byte[8192];
			int c = 0;

			while ((c = fis.read(b)) != -1) {
				buffer.put(b, 0, c);
			}
			buffer.flip();
			fis.close();
			return buffer.array();
		} catch (IOException e) {
			// TODO ignore the exception
		}
		return null;
	}
}
