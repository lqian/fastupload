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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sourceforge.fastupload.mock.SimpleHttpServletRequestMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class FastUploadParserTest {

	private FileFactory fileFactory = FileFactory.getInstance();

	private FastUploadParser fastUploadParser;

	private SimpleHttpServletRequestMock request;

	private InputStream inputStream;
	
	private String repository = System.getProperty("user.home")+"/fastupload";

	@Before
	public void setup() throws IOException {
		inputStream = FastUploadParserTest.class.getResourceAsStream("uploading.data");
		request = new SimpleHttpServletRequestMock(inputStream);
		
		File dir = new File(repository);
		dir.mkdirs();
	}

	@After
	public void clean() throws IOException {
		inputStream.close();
		File dir = new File(repository);
		dir.deleteOnExit();
	}

	@Test
	public void testNoRepoParse() throws IOException {
		fastUploadParser = new FastUploadParser(request);
		List<MultiPartFile> parts = fastUploadParser.parseList();
		assertEquals(parts.size(), 3);
	}

	@Test
	public void testRepoParse() throws IOException {
		// set repository
		fileFactory.setRepository(repository);
		
		fastUploadParser = new FastUploadParser(request, fileFactory);

		List<MultiPartFile> parts = fastUploadParser.parseList();
		assertEquals(parts.size(), 3);

		assertEquals(parts.get(0).getBytes(), 62057);
		assertTrue(parts.get(0).toFile(System.getProperty("user.home") + "/" + parts.get(0).getFileName()));

		assertEquals(parts.get(1).getBytes(), 28253);
		assertTrue(parts.get(1).toFile(System.getProperty("user.home") + "/" + parts.get(1).getFileName()));

		assertEquals(parts.get(2).getString(), "11111");
	}

}
