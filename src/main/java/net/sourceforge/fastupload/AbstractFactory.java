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

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;


/**
 * @author linkqian
 * 
 */
public abstract class AbstractFactory extends AbstractParseThresholdFactory implements AcceptableFileFactory, ParseThreshold {

	protected String regex;
	
	private Pattern pattern;

	private Map<String, ContentHeaderMap> exceptionalMap = new TreeMap<String, ContentHeaderMap>();

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
		this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.fastupload.AcceptableFileFactory#acceptable(java.lang
	 * .String)
	 */
	public boolean acceptable(ContentHeaderMap contentHeaderMap) {
		if (regex == null)
			return true;
		else if (contentHeaderMap.isFile()) {
			boolean f = pattern.matcher(contentHeaderMap.getFileName()).find();
			if (!f) {
				exceptionalMap.put(contentHeaderMap.getFileName(), contentHeaderMap);
			}
			return f;
		}
		else return true;
	}

	public Map<String, ContentHeaderMap> getExceptionalMap() {
		return exceptionalMap;
	}

}
