package net.sourceforge.fastupload;

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
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * the class provides the high level API that parse uploading file of
 * multipart/form-data in {@link HttpServletRequest} input stream.
 * 
 * the main function
 * 
 * <pre>
 * List &lt; MultiPartFile &gt; HttpFileUploadParser.parse()
 * </pre>
 * 
 * , read a part of data of {@link ServletInputStream} into constant size
 * buffer. write those data into a file directly if found uploading-boundary.
 * the usage of the class is here.
 * 
 * <pre>
 * 
 * 
 * DiskFileFactory dff = new DiskFileFactory(System.getProperty(&quot;user.home&quot;));
 * dff.setParseThreshold(0x100000);
 * 
 * HttpFileUploadParser parser = new HttpFileUploadParser(req, dff);
 * List&lt;MultiPartFile&gt; files = parser.parse();
 * 
 * </pre>
 * 
 * optional {@link DiskFileFactory} provides a several features. specify a
 * charset,
 * 
 * <pre>
 * DiskFileFactory dff = new DiskFileFactory(System.getProperty(&quot;user.home&quot;), &quot;utf-8&quot;);
 * </pre>
 * 
 * {@link HttpFileUploadParser} converts file name with utf-8 charset, also
 * convert the content with utf-8 charset if found the form-based uploading file
 * is text format.
 * 
 * 
 * <pre>
 * DiskFileFactory dff = new DiskFileFactory(System.getProperty(&quot;user.home&quot;), &quot;utf-8&quot;, 0x20000);
 * </pre>
 * 
 * the code indicates the max size is 0x20000 per file.
 * 
 * <pre>
 * dff.setParseThreshold(0x100000);
 * </pre>
 * 
 * the code indicates the max content-length of whole requesting.
 * 
 * fastupload provides an advanced mechanism that filters uploading files. It
 * does not like others form-based uploading component or framework filter
 * feature. It filter off a boundary if found the boundary's content header
 * doesn't match the rules.
 * 
 * <pre>
 * dff.setAllowedExtensions(&quot;.jpg, .png&quot;);
 * </pre>
 * 
 * comma splitting. the code indicates {@link HttpFileUploadParser} accept two
 * types file. JPG and PNG.
 * 
 * <pre>
 * dff.setAllowedTypes(&quot;image/jpg&quot;);
 * </pre>
 * 
 * the code indicates {@link HttpFileUploadParser} accept the <em>image/jpg</em>
 * content type.
 * 
 * 
 * @see AcceptableFileFactory
 * @see AbstractFactory
 * @see DiskFileFactory
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class HttpFileUploadParser extends AbstractUploadParser {

	private FileFactory fileFactory;

	private ProgressListener progressListener;

	private StreamUploadParser uploadParser;

	public HttpFileUploadParser(HttpServletRequest request, FileFactory fileFactory) {
		this.request = request;
		this.fileFactory = fileFactory;
	}

	/**
	 * parse the request of multiple part data of form. and write content of
	 * file into file at the directory specified by a {@link FileFactory}
	 * object. finally, return a list contains the object of
	 * {@link MultiPartFile}
	 * 
	 * @return List<MultiPartFile>
	 * @throws IOException
	 */
	public List<MultiPartFile> parse() throws IOException {
		this.parseContentLength();
		this.parseEnctype();
		this.uploadParser = new StreamUploadParser(request.getInputStream(), boundary, fileFactory);
		return uploadParser.parse();
	}

	@Override
	public int getParseThreshold() {
		return this.fileFactory.getParseThreshold();
	}

	public double getReadBytes() {
		return uploadParser.getReadBytes();
	}

	public ProgressListener getProgressListener() {
		return progressListener;
	}

	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	public byte[] getBoundary() {
		return boundary;
	}

	public void setBoundary(byte[] boundary) {
		this.boundary = boundary;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public FileFactory getFileFactory() {
		return fileFactory;
	}

	public void setFileFactory(FileFactory fileFactory) {
		this.fileFactory = fileFactory;
	}

	public StreamUploadParser getUploadParser() {
		return uploadParser;
	}

	public void setUploadParser(StreamUploadParser uploadParser) {
		this.uploadParser = uploadParser;
	}

}
