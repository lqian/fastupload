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
package net.sourceforge.fastupload.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sourceforge.fastupload.HttpMemoryUploadParser;
import net.sourceforge.fastupload.MemoryMultiPartDataFactory;
import net.sourceforge.fastupload.MultiPartData;
import net.sourceforge.fastupload.MultiPartFile;

import org.junit.Before;
import org.junit.Test;

/**
 *  @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class HttpMemoryUploadParserTest extends UploadParserTest {

	private MemoryMultiPartDataFactory multiPartDataFactory;

	private HttpMemoryUploadParser httpMemoryUploadParser;

	@Before
	public void setUp() throws IOException {
		super.setUp();
		multiPartDataFactory = new MemoryMultiPartDataFactory();
		httpMemoryUploadParser = new HttpMemoryUploadParser(simpleHttpServletRequestMock, multiPartDataFactory);
	}

	@Test
	public void testParse() throws IOException {
		List<MultiPartFile> parts = httpMemoryUploadParser.parseList();
		assertEquals(parts.size(), 3);

		MultiPartData part1 = parts.get(0);
		assertEquals(part1.isFile(), true);
	}
	
	
	@Test
	public void testGetInputStream() throws IOException {
		List<MultiPartFile> parts = httpMemoryUploadParser.parseList();
		assertEquals(parts.size(), 3);

		MultiPartFile part1 = parts.get(0);
		assertEquals(part1.isFile(), true);
		
		assertEquals(part1.getInputStream().available(), 62057);
	}
	
	
	@Test
	public void testParseContentType() throws IOException {
		
		multiPartDataFactory.setAllowedTypes("image/jpg");
		multiPartDataFactory.setAllowedExtensions(".jpg");
		List<MultiPartFile> parts =httpMemoryUploadParser.parseList();
		assertEquals(parts.size(), 1);

		MultiPartData part1 = parts.get(0);
		assertEquals(part1.isFile(), false);
	}
	
	
	
	 

}
