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

/**
 * a interface defines the function that checks a file name or content-type is acceptable by the given regular express pattern.
 * the factory support two types of filter rule.
 * <ul>
 * <li>allowedTypes(optional)   accept the boundary by the allowed content-type, if not set, accept all boundaries</li>
 * <li>allowedExtension(optional) accept the boundary by the allowed the file extension name, if not set, accept all boundaries</li>
 * </ul>
 * 
 * The class {@link DiskFileFactory} and {@link MemoryMultiPartDataFactory} implements the interface.
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 *
 */
public interface AcceptableFileFactory {
	
	
	/**
	 * comma splitting, for example, <pre>image/jpg, image/png</pre>
	 * @return
	 */
	public void setAllowedTypes(String allowedTypes);

	/**
	 * comma splitting, for examples, <pre>.jpg, .png, .zip</pre>
	 * @param allowedExtensions
	 */
	public void setAllowedExtensions(String allowedExtensions);
	
	/**
	 * check whether a given {@link ContentHeaderMap} object is acceptable
	 * @param contentHeaderMap ContentHeaderMap
	 * @return boolean
	 */
	public boolean acceptable(ContentHeaderMap contentHeaderMap) ;
	
	/**
	 * return a map object contains all not-acceptable {@link ContentHeaderMap}, the key is the file name parsed from the {@link ServletRequest} input stream.
	 * @return
	 */
	public Map<String, ContentHeaderMap> getExceptionalMap();
	
	public String getAllowedTypes();
	
	public String getAllowedExtensions() ; 
	

}
