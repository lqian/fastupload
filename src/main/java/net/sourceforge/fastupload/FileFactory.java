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
 * a factory interface that declare method create object which is instance of sub-class of
 * {@link MultiPartFile}
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public interface FileFactory extends ParseThreshold {

	/**
	 * create a object instance which is sub-class of {@link MultiPartFile}
	 * specified. afterwards, open a file output stream when the object is
	 * created.
	 * 
	 * @param name
	 *            , a correct operation system file name with full path.
	 * @param cls
	 *            , sub-class of {@link MultiPartFile}
	 * @return an object of sub-class of {@link MultiPartFile} specified in last
	 *         parameter in the function declaration.
	 */
	public <T extends MultiPartFile> T createMulitPartFile(String name, Class<? extends MultiPartFile> cls);

}
