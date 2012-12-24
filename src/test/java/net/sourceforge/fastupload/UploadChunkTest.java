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

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.util.HashMap;

import net.sourceforge.fastupload.util.UploadChunk;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 *
 */
public class UploadChunkTest {
	
	private UploadChunk uploadChunk;
	
	@Before
	public void setUp() {
		uploadChunk = new UploadChunk(null, Charset.defaultCharset().name());
	}
	
	@Test
	public void testParseLine1() {
		HashMap<String, String> header = uploadChunk.parseLine("Content-Disposition: form-data; name=\"file1\"; filename=\"C:\\Users\\admin\\Desktop\\hosts\"");
		assertEquals(header.entrySet().size(), 3);
	}
	
	@Test
	public void testParseLine2() {
		HashMap<String, String> header = uploadChunk.parseLine("Content-Type: text/plain");
		assertEquals(header.entrySet().size(), 1);
	}

}
