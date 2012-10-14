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

package net.sourceforge.fastupload.exception;

import net.sourceforge.fastupload.MultiPartData;
import net.sourceforge.fastupload.MultiPartFile;


public class ThresholdException extends RuntimeException {

	/**
	 * Generated automatically
	 */
	private static final long serialVersionUID = -1004551904095764397L;

	public ThresholdException(String msg) {
		super(msg);
	}

	public static ThresholdException parseThresholdException() {
		return new ThresholdException("ServletRequest inpustream length exceeds ParseThreshold");
	}
	
	public static ThresholdException fileThresholdException(MultiPartFile multiPartFile) {
		return new ThresholdException("a MultiPartFile length exceeds ParseThreshold: " + multiPartFile);
	}

	
	public static ThresholdException newThresholdException(MultiPartData multiPartData) {
		return new ThresholdException("a MultiPartFile length exceeds ParseThreshold: " + multiPartData);
	}
}
