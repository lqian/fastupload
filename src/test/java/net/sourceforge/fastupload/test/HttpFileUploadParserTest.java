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
package net.sourceforge.fastupload.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import net.sourceforge.fastupload.DiskFileFactory;
import net.sourceforge.fastupload.HttpFileUploadParser;
import net.sourceforge.fastupload.MultiPartFile;

import org.junit.Before;
import org.junit.Test;

/**
 * @author linkqian
 * 
 */
public class HttpFileUploadParserTest extends UploadParserTest {

	private HttpFileUploadParser httpFileUploadParser;

	private DiskFileFactory diskFileFactory;

	@Before
	public void setUp() throws IOException {
		super.setUp();
		diskFileFactory = new DiskFileFactory(System.getProperty("user.home"), "utf-8");
		httpFileUploadParser = new HttpFileUploadParser(simpleHttpServletRequestMock, diskFileFactory);
	}

	@Test
	public void testParse() throws IOException {
		List<MultiPartFile> files = httpFileUploadParser.parse();

		assertEquals(files.size(), 2);

		MultiPartFile file1 = files.get(0);
		assertEquals(file1.isFile(), true);
	}
	
	@Test
	public void testAcceptableParse() throws IOException {
		diskFileFactory.setAllowedExtensions(".jpg, .png");
		List<MultiPartFile> files = httpFileUploadParser.parse();

		assertEquals(files.size(), 0);
		assertEquals(diskFileFactory.getExceptionalMap().keySet().size(), 2);
		 
	}

}
