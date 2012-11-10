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
import java.util.Random;

/**
 * factory that create {@link MultiPartFile } object which class inherits from
 * {@link MultiPartFile }. it should be {@link MultiPartTextFile} and
 * {@link MultiPartBinaryFile}
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class DiskFileFactory extends AbstractFactory implements FileFactory, ParseThreshold {

	private String path;

	private String charset;

	private long threshold;

	/**
	 * if the property is true, the factory generates a universal random name
	 * for current {@link MultiPartFile} object;
	 */
	private boolean randomFileName = false;

	public DiskFileFactory(String path) {
		this.path = this.marshal(path);
	}

	private String marshal(String path) {
		int i = path.lastIndexOf("/");
		if (i < path.length()) {
			path += "/";
		}
		return path;
	}

	public DiskFileFactory(String path, String charset) {
		super();
		this.path = this.marshal(path);
		this.charset = charset;
	}

	/**
	 * 
	 * @param path
	 *            should end with '/' character
	 * @param charset
	 * @param threshold
	 */
	public DiskFileFactory(String path, String charset, int threshold) {
		super();
		this.path = path;
		this.charset = charset;
		this.threshold = threshold;
	}

	@SuppressWarnings("unchecked")
	public <F extends MultiPartFile> F createMulitPartFile(String name, Class<? extends MultiPartFile> cls) {
		MultiPartFile instance = null;
		try {
			instance = charset != null ? doCreate(name, charset, cls) : doCreate(name, cls);
			instance.setThreshold(threshold);
		} catch (Exception e) {
			// ignore the exception
			e.printStackTrace();
		}
		return (F) instance;
	}

	@SuppressWarnings("unchecked")
	private <F extends MultiPartFile> F doCreate(String name, Class<? extends MultiPartFile> cls) throws Exception {
		Constructor<? extends MultiPartFile> constructor = cls.getConstructor(String.class);
		MultiPartFile instance = constructor.newInstance(path + (randomFileName ? UniveralNameGenerator.generate(name) : name));
		return (F) instance;
	}

	@SuppressWarnings("unchecked")
	private <F extends MultiPartFile> F doCreate(String name, String charset, Class<? extends MultiPartFile> cls) throws Exception {
		name = new String(name.getBytes(), charset);

		Constructor<? extends MultiPartFile> constructor = cls.getConstructor(String.class, String.class);
		MultiPartFile instance = constructor.newInstance(path + (randomFileName ? UniveralNameGenerator.generate(name) : name), charset);
		return (F) instance;
	}

	public String getPath() {
		return path;
	}

	public String getCharset() {
		return charset;
	}

	protected long getThreshold() {
		return threshold;
	}

	/**
	 * @return the randomFileName
	 */
	public boolean isRandomFileName() {
		return randomFileName;
	}

	/**
	 * @param randomFileName
	 *            the randomFileName to set
	 */
	public void setRandomFileName(boolean randomFileName) {
		this.randomFileName = randomFileName;
	}

	static class UniveralNameGenerator {
		static final char[] alpha = { '-', '~', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
				'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

		static String generate(String name) {
			return String.format("%d_%s_%d_%s", System.currentTimeMillis(), random(), System.nanoTime(), name);
		}

		static String random() {
			StringBuilder sb = new StringBuilder(12);
			Random random = new Random();
			for (int i = 0; i < 12; i++) {
				sb.append(alpha[random.nextInt(alpha.length)]);
			}
			return sb.toString();

		}

	}

	// @SuppressWarnings("unchecked")
	// @Override
	// public <T extends MultiPartData> T createMulitPartFile(String name,
	// Class<? extends MultiPartData> cls) {
	// Class<? extends MultiPartFile> subClass = (Class<? extends
	// MultiPartFile>) cls.asSubclass(cls);
	// return (T) this.createMulitPartDiskFile(name, subClass);
	// }

}
