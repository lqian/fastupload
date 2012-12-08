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
package org.apache.struts2.dispatcher.multipart;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.fastupload.DiskFileFactory;
import net.sourceforge.fastupload.FileFactory;
import net.sourceforge.fastupload.HttpFileUploadParser;
import net.sourceforge.fastupload.HttpMemoryUploadParser;
import net.sourceforge.fastupload.MemoryMultiPartDataFactory;
import net.sourceforge.fastupload.MultiPartFile;

import org.apache.struts2.StrutsConstants;

import com.opensymphony.xwork2.inject.Inject;

/**
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class FastUploadMultiPartRequest implements MultiPartRequest {

	private final String PARSE_DIRECT = "direct";

	protected long maxSize;

	private List<? extends MultiPartFile> files;

	protected Map<String, MultiPartFile> fieldParams = new HashMap<String, MultiPartFile>();

	private String parseType;

	private FileFactory fileFactory;

	private String allowedTypes;

	private String allowedExtensions;

	@Inject(StrutsConstants.STRUTS_MULTIPART_MAXSIZE)
	public void setMaxSize(String maxSize) {
		this.maxSize = Long.parseLong(maxSize);
	}

	@Inject("fastupload.parse.type")
	public void setParseType(String parseType) {
		this.parseType = parseType;
	}

	/**
	 * not exposed yet
	 * 
	 * @param allowedTypes
	 *            the allowedTypes to set
	 */
	// @Inject("fastupload.allowed.types")
	public void setAllowedTypes(String allowedTypes) {
		this.allowedTypes = allowedTypes;
	}

	/**
	 * not exposed yet
	 * 
	 * @param allowedExtensions
	 *            the allowedExtensions to set
	 */
	// @Inject("fastupload.allowed.extensions")
	public void setAllowedExtensions(String allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.struts2.dispatcher.multipart.MultiPartRequest#parse(javax.
	 * servlet.http.HttpServletRequest, java.lang.String)
	 */
	public void parse(HttpServletRequest httpRequest, String saveDir) throws IOException {
		if (parseType == null || parseType.equalsIgnoreCase(PARSE_DIRECT)) {
			DiskFileFactory dff = new DiskFileFactory(saveDir, httpRequest.getCharacterEncoding());
			dff.setRandomFileName(true);
			fileFactory = dff;
			fileFactory.setParseThreshold(maxSize);
			fileFactory.setAllowedExtensions(allowedExtensions);
			fileFactory.setAllowedTypes(allowedTypes);
			HttpFileUploadParser parser = new HttpFileUploadParser(httpRequest, fileFactory);
			files = parser.parse();
		} else {
			fileFactory = new MemoryMultiPartDataFactory(httpRequest.getCharacterEncoding());
			fileFactory.setParseThreshold(maxSize);
			fileFactory.setAllowedExtensions(allowedExtensions);
			fileFactory.setAllowedTypes(allowedTypes);
			HttpMemoryUploadParser parser = new HttpMemoryUploadParser(httpRequest, fileFactory);
			files = parser.parseList();
		}

		processParameters();
	}

	private void processParameters() throws UnsupportedEncodingException {
		for (MultiPartFile f : files) {
			fieldParams.put(f.getFieldName(), f);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterNames
	 * ()
	 */
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(this.fieldParams.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#
	 * getFileParameterNames()
	 */
	public Enumeration<String> getFileParameterNames() {
		return Collections.enumeration(fieldParams.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.struts2.dispatcher.multipart.MultiPartRequest#getContentType
	 * (java.lang.String)
	 */
	public String[] getContentType(String fieldName) {
		if (this.fieldParams.containsKey(fieldName)) {
			ArrayList<String> types = new ArrayList<String>();
			if (this.fieldParams.get(fieldName) != null) {
				types.add(this.fieldParams.get(fieldName).getContentType());
			}
			return types.toArray(new String[types.size()]);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getErrors()
	 */
	@SuppressWarnings("rawtypes")
	public List getErrors() {
		return Arrays.asList(fileFactory.getExceptionals().toArray());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFile(java
	 * .lang.String)
	 */
	public File[] getFile(String fieldName) {
		if (this.fieldParams.containsKey(fieldName)) {
			ArrayList<File> files = new ArrayList<File>();
			if (this.fieldParams.get(fieldName) != null) {
				
				files.add(new File(fieldParams.get(fieldName).getName())); // use getName() since getFileName() changed
			}
			return files.toArray(new File[files.size()]);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileNames
	 * (java.lang.String)
	 */
	public String[] getFileNames(String fieldName) {
		if (this.fieldParams.containsKey(fieldName)) {
			ArrayList<String> names = new ArrayList<String>();
			if (this.fieldParams.get(fieldName) != null) {
				names.add(this.fieldParams.get(fieldName).getFileName());
			}
			return names.toArray(new String[names.size()]);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFilesystemName
	 * (java.lang.String)
	 */
	public String[] getFilesystemName(String fieldName) {
		if (this.fieldParams.containsKey(fieldName)) {
			ArrayList<String> names = new ArrayList<String>();
			if (this.fieldParams.get(fieldName) != null) {
				names.add(this.fieldParams.get(fieldName).getName());
			}
			return names.toArray(new String[names.size()]);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameter
	 * (java.lang.String)
	 */
	public String getParameter(String fieldName) {
		if (this.fieldParams.containsKey(fieldName)) {
			if (this.fieldParams.get(fieldName) != null && this.fieldParams.get(fieldName).isFile() == false) {
				try {
					return this.fieldParams.get(fieldName).getString();
				} catch (UnsupportedEncodingException e) {
					// TODO logger the error
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterValues
	 * (java.lang.String)
	 */
	public String[] getParameterValues(String fieldName) {
		if (this.fieldParams.containsKey(fieldName)) {
			ArrayList<String> names = new ArrayList<String>();
			if (this.fieldParams.get(fieldName) != null && this.fieldParams.get(fieldName).isFile() == false) {
				try {
					names.add(this.fieldParams.get(fieldName).getString());
					return names.toArray(new String[names.size()]);
				} catch (UnsupportedEncodingException e) {
					// TODO Logger the error;
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public long getMaxSize() {
		return maxSize;
	}

	/**
	 * @return the allowedExtensions
	 */
	public String getAllowedExtensions() {
		return allowedExtensions;
	}

	/**
	 * @return the allowedTypes
	 */
	public String getAllowedTypes() {
		return allowedTypes;
	}

	public Map<String, MultiPartFile> getFieldParams() {
		return fieldParams;
	}

	public void setFieldParams(Map<String, MultiPartFile> fieldParams) {
		this.fieldParams = fieldParams;
	}

}
