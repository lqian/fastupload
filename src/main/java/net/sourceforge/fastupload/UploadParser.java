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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class defines basic structure of parsing multipart/form-data input stream.
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public abstract class UploadParser {
	
	/**
	 * charset encoding of {@link HttpServletRequest}
	 */
	protected String encoding;

	protected InputStream inputSteam;

	protected FileFactory fileFactory;

	protected byte[] boundary;

	protected byte[] subBoundary;
	
	protected List<MultiPart> files = new ArrayList<MultiPart>();
	
	protected int readBytes;

	public UploadParser(InputStream inputSteam, FileFactory fileFactory, byte[] boundary) {
		super();
		this.inputSteam = inputSteam;
		this.fileFactory = fileFactory;
		this.boundary = boundary;
	}

	/**
	 * define the method that read bytes of multipart/form-data input stream,
	 * and parse {@link net.sourceforge.fastupload.MultiPart} from it.
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract List<MultiPart> parseList() throws IOException;

	/**
	 * convert list of {@link net.sourceforge.fastupload.MultiPart} object
	 * to a map, the key is input field name in the uploading form.
	 * 
	 * @return
	 * @throws IOException
	 */
	public Map<String, MultiPart> parseMap() throws IOException {
		Map<String, MultiPart> fileMap = new HashMap<String, MultiPart>();
		for (MultiPart e : parseList()) {
			fileMap.put(e.getFieldName(), e);
		}
		return fileMap;
	}
	
	public int getReadBytes() {
		return readBytes;
	}
	
}
