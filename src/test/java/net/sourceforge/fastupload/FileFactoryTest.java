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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class FileFactoryTest {

	private FileFactory fileFactory = FileFactory.getInstance();

	private ContentHeaderMap textFileHeader = new ContentHeaderMap();

	private ContentHeaderMap imageFileHeader = new ContentHeaderMap();

	private ContentHeaderMap textHeader = new ContentHeaderMap();

	private MultiPart part1;

	private MultiPart part2;

	private MultiPart part3;

	@Before
	public void setup() throws Exception {
		textFileHeader.put("Content-disposition", "form-data");
		textFileHeader.put("name", "text1");
		textFileHeader.put("filename", "upload.txt");
		textFileHeader.put("Content-Type", "text/plain");

		imageFileHeader.put("Content-disposition", "form-data");
		imageFileHeader.put("name", "photo");
		imageFileHeader.put("filename", "fastuplod.ico");
		imageFileHeader.put("Content-Type", "image/ico");

		textHeader.put("Content-disposition", "form-data");
		textHeader.put("name", "text2");
	}

	@Test
	public void testNoRepo() throws IOException {
		part1 = fileFactory.createMultiPartFile(textHeader);
		assertTrue(part1 instanceof MemoryMultiPart);

		part2 = fileFactory.createMultiPartFile(textFileHeader);
		assertTrue(part2 instanceof MemoryMultiPart);

		part3 = fileFactory.createMultiPartFile(imageFileHeader);
		assertTrue(part3 instanceof MemoryMultiPart);

	}

	@Test
	public void testRepo() throws IOException {
		// set repository
		fileFactory.setRepository(System.getProperty("user.home"));

		part1 = fileFactory.createMultiPartFile(textHeader);
		assertTrue(part1 instanceof MemoryMultiPart);

		part2 = fileFactory.createMultiPartFile(textFileHeader);
		assertTrue(part2 instanceof MultiPartTextFile);

		part3 = fileFactory.createMultiPartFile(imageFileHeader);
		assertTrue(part3 instanceof MultiPartBinaryFile);

	}

}
