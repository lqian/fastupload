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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.fastupload.exception.FileUploadParserException;
import net.sourceforge.fastupload.exception.ThresholdException;

/**
 * Refer to <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC1867</a>, High
 * level API for multipart/form-data <code>ServletInputStream</code> parsing.
 * Sometime web site need to receives large size of uploading file, sometime web
 * site need to receives a plenty of small size of uploading file in the high
 * throughput. Fastupload provides two way to parse
 * <code>ServletInputStream</code> and very simple APIs for variant requirements
 * from high performance web site.
 * <p/>
 * 
 * <h2>Basic Usage</h2>
 * 
 * The first way is read all bytes from <code>ServletInputStream</code> into a
 * buffer, then parse multipart data within two boundaries.
 * <code>FastUploadParser</code> parse multipart/form-data input stream with the
 * way in default. we recommend it. Here are a sample code snippet.
 * 
 * <pre>
 *  FastUploadParser fastUploadParser = new FastUploadParser(request);
 *  List&lt;MultiPart&gt; list = fastUploadParser.parseList();
 *  for (MultiPart e: list){
 *  	if (e.isFile()){
 *  		System.out.format("input field name: %s, file name:%s%n", e.getFieldName(), e.getFileName());
 *  		e.toFile( /file/ ) ;  //write data to a file where you want to place
 *  	}
 *  	else {
 *  		System.out.format("input field name: %s, value:%s%n", e.getFieldName(), e.getString());
 *  	}
 * }
 * </pre>
 * 
 * In the code snippet, after <code>fastUploadParser.parseList()</code> execute,
 * all data parts split by boundary are parsed an put into a list.
 * {@link MultiPart} provides <code>isFile()</code> method determine a multipart
 * data whether is a uploading file or normal input. For example, in a form HTML
 * code is
 * <li><code>&lt;input type="text" /&gt;</code>, regarding
 * <code>MultiPart</code> object is not a file, to a no-file type
 * <code>MultiPart</code> object, Fastupload limits to provides get content of
 * it as <code>String</code></li>
 * <li><code>&lt;input type="file" /&gt;</code>, regarding
 * <code>MultiPart</code> object is a file, to a file type
 * <code>MultiPart</code> object, Fastupload provides
 * <code>toFile(String)</code> method to write content of current multipart
 * stream into a file. And <code>getInputStream()</code> to open a inputream for
 * current multipart stream. so APIs user can access it directly.</li>
 * <p />
 * 
 * The second way is read 8K bytes from <code>ServletInputStream</code> to a
 * buffer, then parse multipart data within two boundaries, create disk file for
 * it and write content data in the temporary file when found a multipart data.
 * read 8K bytes next until all byte of <code>ServletInputStream</code> is read
 * and parsed. Finally return a list of {@link MultiPart}. This way only need 8K
 * buffer extra. It's very useful when receive a large size of file. Here are
 * simple code snippet to show this usage way.
 * 
 * <pre>
 * FileFactory fileFactory = FileFactory.getInstance();
 * fileFactory.setRepository(System.getProperty("user.home")+"/fastupload");
 * FastUploadParser fastUploadParser = new FastUploadParser(request, fileFactory);
 * List&lt;MultiPart&gt; list = fastUploadParser.parseList();
 * for (MultiPart e: list){
 * 	if (e.isFile()){
 * 		System.out.format("input field name: %s, file name:%s%n", e.getFieldName(), e.getFileName());
 * 		e.toFile( /target/ ) ;  //move temporary file to where you want to place
 * 	}
 * 	else {
 * 		System.out.format("input field name: %s, value:%s%n", e.getFieldName(), e.getString());
 * 	}
 * }
 * </pre>
 * 
 * As you see, most code is like the first way, the code snippet change the
 * parse way. It set a temporary repository for <code>FileFactory</code> object
 * 
 * <pre>
 * <code>
 * FileFactory fileFactory = FileFactory.getInstance();
 * fileFactory.setRepository(System.getProperty("user.home")+"/fastupload");
 * </code>
 * </pre>
 * 
 * <code>e.toFile( /target/ )</code> move the temporary file to the target place
 * where you want because it isn't as same as first way that store multipart
 * data in memory.
 * 
 * <h2>Advanced APIs</h2>
 * {@link FileFactory} controls parse way, give some advanced features as well.
 * <li><code>FileFactory fileFactory = FileFactory.getInstance("UTF-8");</code></li>
 * create a {@link FileFactory} instance with specific charset name. Content of
 * a multipart will convert with the charset name when find the content type is
 * text. Another thing is it converts the file name of content header with the
 * charset when find it. It is a key feature of Fastupload open-source project.
 * 
 * <li><code>
 * FileFactory fileFactory = FileFactory.getInstance("UTF-8");
 * fileFactory.setAllowedTypes("image/jpeg");
 * fileFactory.setAllowedExtensions(".jpg, .png");
 * </code></li> <code>FastUploadParser</code> provides content type and file
 * name filter function. Parser ignore a multipart content when found its
 * content header does not match <em>AllowedTypes</em> or extension name of
 * <em>filename</em> entity does not match <em>AllowedExtensions</em>.
 * 
 * <li><code>fileFactory.setThreshold(200000);</code> limit parse content length of a part excludes headers, does not exceed the threshold. throw a runtime type of {@link ThresholdException} 
 * <li><code>fileFactory.setMaxContentLength(2000000);</code> limit parse a content length of current multipart request, does not exceed the value. throw a runtime type of {@link ThresholdException}
 *
 * @since 0.5.1
 * 
 * @see net.sourceforge.fastupload.FileFactory
 * @see net.sourceforge.fastupload.MultiPart
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class FastUploadParser {

	private final String _ENCTYPE = "multipart/form-data";
	private final String _HEADER_CONTENT_TYPE = "Content-type";
	private final String _BOUNDARY_PREFIX = "--";
	private final String _DEFALT_ENCODING = "iso-8859-1";

	private byte[] boundary;

	private HttpServletRequest request;

	/**
	 * content length of HttpServletRequest header
	 */
	private int contentLength;

	/**
	 * progress listener
	 */
	private ProgressListener progressListener;

	/**
	 * UploadParser that parse ServletInputStream indeed, and return parsing
	 * result.
	 */
	private UploadParser uploadParser;

	/**
	 * default FileFactory
	 */
	private FileFactory fileFactory = FileFactory.getInstance();

	/**
	 * default constructor with <code>HttpServletRequest</code>
	 * 
	 * @param request
	 *            {@link HttpServletRequest}
	 */
	public FastUploadParser(HttpServletRequest request) throws IOException {
		super();
		this.request = request;
		this.init();
	}

	/**
	 * constructor with <code>HttpServletRequest</code> and
	 * <code>FileFactory</code>
	 * 
	 * @param request
	 * @param fileFactory
	 * @throws IOException
	 */
	public FastUploadParser(HttpServletRequest request, FileFactory fileFactory) throws IOException {
		super();
		this.request = request;
		this.fileFactory = fileFactory;
		this.init();
	}

	/**
	 * parse enctype, content length, boundary and encoding
	 * 
	 * @throws IOException
	 */
	private void init() throws IOException {
		this.parseEnctype();
		this.parseContentLength();
		
		fileFactory.setEncoding(request.getCharacterEncoding() == null ? _DEFALT_ENCODING : request.getCharacterEncoding() );
		
		if (fileFactory.repository == null || fileFactory.repository.trim().equals("")) {
			uploadParser = new MemoryUploadParser(request.getInputStream(), fileFactory, boundary, contentLength);
		} else {
			uploadParser = new StreamUploadParser(request.getInputStream(), fileFactory, boundary);
		}
		uploadParser.encoding = fileFactory.getEncoding() ;
		
	}

	/**
	 * delegate execution of <code>UploadParser.parseList()</code> method;
	 * 
	 * @return List&lt;MultiPartFile&gt;
	 * @throws IOException
	 */
	public List<MultiPart> parseList() throws IOException {
		return uploadParser.parseList();
	}

	/**
	 * delegate execution of <code>UploadParser.parseMap()</code> method;
	 * 
	 * @return Map&lt;String, MultiPartFile&gt;
	 * @throws IOException
	 */
	public Map<String, MultiPart> parseMap() throws IOException {
		return uploadParser.parseMap();
	}

	/**
	 * parse the encrypt type and boundary from the header of request, throws a
	 * {@link FileUploadParserException} if found the request is not
	 * <em>multipart/form-data</em>
	 * 
	 */
	private void parseEnctype() {
		String[] content = request.getHeader(_HEADER_CONTENT_TYPE).split(";");
		if (content.length > 1) {
			if (!_ENCTYPE.equalsIgnoreCase(content[0])) {
				throw new FileUploadParserException();
			}
			boundary = (_BOUNDARY_PREFIX + content[1].split("=")[1]).getBytes();
		} else {
			throw new FileUploadParserException();
		}
	}

	/**
	 * parse the length of submitted request, DO NOT catch any converting
	 * runtime exception
	 */
	private void parseContentLength() {
		long maxContentLength = this.fileFactory.getMaxContentLength();

		contentLength = request.getContentLength();
		if (maxContentLength > 0 && contentLength > maxContentLength)
			throw ThresholdException.parseThresholdException();
	}

	public long getContentLength() {
		return contentLength;
	}

	/**
	 * @return the progressListener
	 */
	public ProgressListener getProgressListener() {
		return progressListener;
	}

	/**
	 * @param progressListener
	 *            the progressListener to set
	 */
	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	/**
	 * delegate
	 * 
	 * @return
	 */
	public int getReadBytes() {
		return this.uploadParser.getReadBytes();
	}
}
