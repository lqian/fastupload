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

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author linkqian
 * 
 */
public abstract class AbstractFactory extends AbstractParseThresholdFactory implements AcceptableFileFactory, ParseThreshold {

	protected String allowedExtensions;

	protected String allowedTypes;

	private Map<String, ContentHeaderMap> exceptionalMap = new TreeMap<String, ContentHeaderMap>();

	private HashSet<String> allowedExtensionsSet;

	private HashSet<String> allowedTypesSet;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.fastupload.AcceptableFileFactory#acceptable(java.lang
	 * .String)
	 */
	public boolean acceptable(ContentHeaderMap contentHeaderMap) {

		if (allowedExtensions == null && allowedTypes == null)
			return true;

		if (allowedTypesSet != null) {
			if (allowedTypesSet.contains(contentHeaderMap.getContentType())) {
				return true;
			} else {
				exceptionalMap.put(contentHeaderMap.getContentType(), contentHeaderMap);
				return false;
			}
		}
		if (contentHeaderMap.isFile() && allowedExtensionsSet != null) {
			String extName = this.getExtension(contentHeaderMap.getFileName());
			if (extName != null && !allowedExtensionsSet.contains(extName)) {
				exceptionalMap.put(contentHeaderMap.getFileName(), contentHeaderMap);
				return false;
			}
			return true;
		} else
			return true;
	}

	public Map<String, ContentHeaderMap> getExceptionalMap() {
		return exceptionalMap;
	}

	public String getAllowedExtensions() {
		return allowedExtensions;
	}

	public void setAllowedExtensions(String allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
		this.allowedExtensionsSet = marshalSet(allowedExtensions);
	}

	public String getAllowedTypes() {
		return allowedTypes;
	}

	public void setAllowedTypes(String allowedTypes) {
		this.allowedTypes = allowedTypes;
		this.allowedTypesSet = marshalSet(allowedTypes);
	}

	private HashSet<String> marshalSet(String types) {
		HashSet<String> set = new HashSet<String>(0);
		String[] strs = types.split(",");
		for (String s : strs) {
			set.add(s);
		}
		return set;

	}

	private String getExtension(String name) {
		int i = name.lastIndexOf(".");
		return i == -1 ? null : name.substring(i);
	}
}
