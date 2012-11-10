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

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.fastupload.MultiPartFile;

/**
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class FastUploadMultiPartRequestWrapper extends MultiPartRequestWrapper {

	private FastUploadMultiPartRequest fastUploadMultiPartRequest;

	/**
	 * in super constructor method, requested multipart/form-data has been
	 * parsed by the method
	 * <em>MultiPartRequest.parse(HttpServletRequest, String)</em>.
	 * 
	 * @param multiPartRequest
	 * @param request
	 * @param saveDir
	 */
	public FastUploadMultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request, String saveDir) {
		super(multiPartRequest, request, saveDir);
		fastUploadMultiPartRequest = (FastUploadMultiPartRequest) multi;
	}

	public MultiPartFile[] getMultiPartFile(String fieldName) {
		MultiPartFile val = fastUploadMultiPartRequest.getFieldParams().get(fieldName);
		ArrayList<MultiPartFile> files = new ArrayList<MultiPartFile>();
		if (val != null) {
			files.add(val);
		}
		return files.toArray(new MultiPartFile[files.size()]);
	}

	@Override
	public Enumeration getParameterNames() {
		return fastUploadMultiPartRequest.getParameterNames();
	}

	public boolean isFile(String fieldName) {
		MultiPartFile val = fastUploadMultiPartRequest.getFieldParams().get(fieldName);
		return val == null ? false : val.isFile();
	}
}
