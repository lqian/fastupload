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

import java.lang.reflect.Constructor;

/**
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class MemoryMultiPartDataFactory extends AbstractFactory implements MultiPartDataFactory {

	private String charset;

	private int threshold = 0;

	
	public MemoryMultiPartDataFactory() {
		super();
	}

	public MemoryMultiPartDataFactory(String charset) {
		super();
		this.charset = charset;
	}

	public MemoryMultiPartDataFactory(String charset, int threshold) {
		super();
		this.charset = charset;
		this.threshold = threshold;
	}

	@SuppressWarnings("unchecked")
	public <T extends MultiPartData> T createMultiPartData(String name, Class<? extends MultiPartData> cls) {
		try {
			MultiPartData instance = charset == null ? doCreate(name, cls) : doCreate(name, charset, cls);
			instance.setThreshold(threshold);
			return (T) instance;
		} catch (Exception e) {
			// ignore the exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * create an object which class extends {@link MultiPartData}.
	 * 
	 * @param name
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected <T extends MultiPartData> T doCreate(String name, Class<? extends MultiPartData> cls) throws Exception {
		MultiPartData mpd = null;
		Constructor<? extends MultiPartData> constructor = cls.getConstructor(String.class);
		mpd = constructor.newInstance(name);
		return (T) mpd;
	}

	/**
	 * create an object which class extends {@link MultiPartData}. the method
	 * just convert the name with specified <em>charset</em>.
	 * 
	 * @param name
	 * @param charset
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected <T extends MultiPartData> T doCreate(String name, String charset, Class<? extends MultiPartData> cls)
			throws Exception {
		MultiPartData mpd = null;
		Constructor<? extends MultiPartData> constructor = cls.getConstructor(String.class, String.class);

		// convert the charset specified
		//name = new String(name.getBytes(), charset);
		mpd = constructor.newInstance(name, charset);
		return (T) mpd;
	}
}
