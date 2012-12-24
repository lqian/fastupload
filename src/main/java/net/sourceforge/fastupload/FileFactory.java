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
package net.sourceforge.fastupload;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.servlet.ServletRequest;

/**
 * 
 * A factory that indicate which concrete sub class of {@link MultiPart} to be created. Also it provides single part <em>threshold</em> limitation
 * , <em>maxContentLength</em> limitation, <em>content type and file extension name</em> filter for form-based upload
 * 
 * @see net.sourceforge.fastupload.FastUploadParser
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 * @since 0.5.1
 */
public class FileFactory {

	/**
	 * charset name for FileFactory, initial as JVM default charset name
	 */
	protected String charset = Charset.defaultCharset().name();

	/**
	 * a file system path for storing temporary file while parsing
	 */
	protected String repository;

	/**
	 * allowed file extend name while parsing content header after a boundary
	 */
	protected String allowedExtensions;

	/**
	 * allowed content type while parsing content header after a boundary
	 */
	protected String allowedTypes;

	/**
	 * max content length while parsing multipart/form-data input stream
	 */
	private long maxContentLength;

	/**
	 * threshold of file length parsing a single file from multipart/form-data
	 * input stream
	 */
	protected int threshold;

	/**
	 * if the property is true, the factory generates a universal random name
	 * for current {@link MultiPart} object;
	 */
	protected boolean randomFileName = false;

	private Set<ContentHeaderMap> exceptionalSet = new HashSet<ContentHeaderMap>();

	private HashSet<String> allowedExtensionsSet;

	private HashSet<String> allowedTypesSet;
	
	/**
	 * charset encoding of ServletRequest
	 */
	private String encoding;

	/**
	 * return a FileFactory instance with default charset
	 */
	public static FileFactory getInstance() {
		return getInstance(Charset.defaultCharset().name());
	}

	/**
	 * return a FileFactory instance with specific charset
	 */
	public static FileFactory getInstance(String charset) {
		FileFactory ff = new FileFactory();
		ff.setCharset(charset);
		return ff;
	}

	/**
	 * return a NgFileFactory instance with specific charset and file threshold
	 * 
	 * @param charset
	 * @param threshold
	 * @return
	 */
	public static FileFactory getInstance(String charset, int threshold) {
		FileFactory ff = new FileFactory();
		ff.setCharset(charset);
		ff.setThreshold(threshold);
		return ff;
	}

	/**
	 * create an object extends {@link net.sourceforge.fastupload.MultiPart}
	 * with a {@link ContentHeaderMap} parameter. It always create a
	 * {@link net.sourceforge.fastupload.MemoryMultiPart} object when don't
	 * find the header is file.
	 * 
	 * @param header
	 * @return
	 * @throws IOException
	 */
	protected MultiPart createMultiPartFile(ContentHeaderMap header) throws IOException {
		MultiPart mpf = null ;

		if (!header.isFile()) { // always MemoryMultiPartData if not a file
			mpf = charset == null ? new MemoryMultiPart(header.getName()) : new MemoryMultiPart(header.getName(), charset);
		} else if (repository == null) { // memory file
			mpf = charset == null ? new MemoryMultiPart(header.getName()) : new MemoryMultiPart(header.getName(), charset);
		} else { // disk file
			if (header.isTextable()) {
				MultiPartTextFile mptf = charset == null ? new MultiPartTextFile(marshalFileName(header.getFileName())) : new MultiPartTextFile(
						marshalFileName(header.getFileName()), charset);
				mptf.setEncoding(encoding);
				mpf = mptf;
			} else {
				mpf = charset == null ? new MultiPartBinaryFile(marshalFileName(header.getFileName())) : new MultiPartBinaryFile(
						marshalFileName(header.getFileName()), charset);
			}
		}
		
		if (mpf != null) {
			mpf.setContentHeaderMap(header);
			mpf.setThreshold(threshold);
		}
		return mpf;
	}

	private String marshalFileName(String name) {
		String fn = null;
		int i = repository.lastIndexOf("/");
		if (randomFileName) {
			fn = repository + (i == repository.length() - 1 ? "" : "/") + UniveralNameGenerator.generate();
		} else {
			fn = repository + (i == repository.length() - 1 ? "" : "/") + name;
		}
		return fn;
	}

	/**
	 * check whether a given {@link ContentHeaderMap} object is acceptable
	 * 
	 * @param contentHeaderMap
	 *            ContentHeaderMap
	 * @return boolean
	 */
	protected boolean acceptable(ContentHeaderMap contentHeaderMap) {
		if (allowedExtensions == null && allowedTypes == null)
			return true;

		if (allowedTypesSet != null && contentHeaderMap.getContentType() != null) {
			if (allowedTypesSet.contains(contentHeaderMap.getContentType())) {
				return true;
			} else {
				exceptionalSet.add(contentHeaderMap);
				return false;
			}
		}
		if (contentHeaderMap.isFile() && allowedExtensionsSet != null) {
			String extName = this.getExtension(contentHeaderMap.getFileName());
			if (extName != null && !allowedExtensionsSet.contains(extName)) {
				exceptionalSet.add(contentHeaderMap);
				return false;
			}
			return true;
		} else
			return true;

	}

	/**
	 * comma splitting, for example,
	 * 
	 * <pre>
	 * image/jpg, image/png
	 * </pre>
	 * 
	 * @return
	 */
	public void setAllowedTypes(String allowedTypes) {
		this.allowedTypes = allowedTypes;
		this.allowedTypesSet = marshalSet(allowedTypes);
	}

	/**
	 * comma splitting, for examples,
	 * 
	 * <pre>
	 * .jpg, .png, .zip
	 * </pre>
	 * 
	 * @param allowedExtensions
	 */
	public void setAllowedExtensions(String allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
		this.allowedExtensionsSet = marshalSet(allowedExtensions);
	}

	/**
	 * return a map object contains all not-acceptable {@link ContentHeaderMap},
	 * the key is the file name parsed from the {@link ServletRequest} input
	 * stream.
	 * 
	 * @return
	 */
	public Set<ContentHeaderMap> getExceptionals() {
		return exceptionalSet;
	}

	public String getAllowedTypes() {
		return allowedTypes;
	}

	public String getAllowedExtensions() {
		return allowedExtensions;
	}

	private HashSet<String> marshalSet(String types) {
		if (types == null)
			return null;
		HashSet<String> set = new HashSet<String>(0);
		String[] strs = types.split(",");
		for (String s : strs) {
			set.add(s);
		}
		return set;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public long getMaxContentLength() {
		return maxContentLength;
	}

	public void setMaxContentLength(long maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	private String getExtension(String name) {
		int i = name.lastIndexOf(".");
		return i == -1 ? null : name.substring(i);
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setRandomFileName(boolean randomFileName) {
		this.randomFileName = randomFileName;
	}

	public boolean isRandomFileName() {
		return randomFileName;
	}

	static class UniveralNameGenerator {
		static final char[] alpha = { '-', '~', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
				'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

		static String generate(String name) {
			return String.format("%d_%s_%d_%s", System.currentTimeMillis(), random(), System.nanoTime(), name);
		}

		public static String generate() {
			return String.format("%d_%s_%d.tmp", System.currentTimeMillis(), random(), System.nanoTime());
		}

		private static String random() {
			char[] chars = new char[12];
			Random random = new Random();
			for (int i = 0; i < 12; i++) {
				chars[i] = alpha[random.nextInt(alpha.length)];
			}
			return new String(chars);
		}

	}

}
