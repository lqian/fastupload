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

/**
 * 
 * an interface declares methods that create object which is instance of
 * sub-class of {@link MultiPartData}
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public interface MultiPartDataFactory extends ParseThreshold {

	/**
	 * 
	 * @param name
	 *            , the name for identifying the instance of sub-class of
	 *            {@link MultiPartData}
	 * @param cls
	 *            the class of sub-class of {@link MultiPartData}
	 * @return an object that is instance of sub-class of {@link MultiPartData}
	 */
	public <T extends MultiPartData> T createMultiPartData(String name, Class<? extends MultiPartData> cls);
}
