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
package net.sourceforge.fastupload.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import net.sourceforge.fastupload.ContentHeaderMap;

public class UploadChunk {

	public static final byte[] _CRLF = { 0X0D, 0X0A };

	/*
	 * current position of the buffer parsing
	 */
	private int pos;

	private int length;

	private byte[] buffer;

	private ContentHeaderMap contentHeaderMap;

	/*
	 * start position of the boundary found
	 */
	private int boundStart = -1;

	/*
	 * end position of the boundary found
	 */
	private int boundEnd = -1;
	
	private BoundayFinder boundayFinder;
	
	private BoundayFinder subBoundayFinder;

	public UploadChunk(byte[] buffer, byte[] boundary, int pos) {
		this.boundayFinder = new BoundayFinder(boundary);
		this.buffer = buffer;
		this.pos = pos;
	}

	public UploadChunk(byte[] buffer, byte[] boundary, int pos, int length) {
		this.boundayFinder = new BoundayFinder(boundary);
		this.buffer = buffer;
		this.pos = pos;
		this.length = length;
	}

	public void append(byte[] buffer) {
		this.append(buffer, 0, buffer.length);
	}

	public void insertDelta(byte[] delta, int pos, int len) {
		ByteBuffer bb = ByteBuffer.allocate(this.length + len);
		bb.put(delta, pos, len);
		bb.put(this.buffer, len, this.length);
		this.buffer = bb.array();
		this.length = this.buffer.length;
	}

	public void append(byte[] buffer, int pos, int len) {
		ByteBuffer bb = ByteBuffer.allocate(this.length + len);
		bb.put(this.buffer, 0, this.length);
		bb.put(buffer, 0, len);
		this.buffer = bb.array();
		this.length += len;
	}

	public int getBufferLength() {
		return this.length;
	}

	/**
	 * find a whole uploading data chunk in the current buffer. the
	 * <em>start</em> and <em>end</em> variable indicates the chunk start
	 * position and end position.
	 * 
	 * @return boolean if found
	 */
	public boolean find() {
		boundStart = boundayFinder.indexOf(buffer, pos, length);
		if (boundStart != -1)
			boundEnd = boundayFinder.indexOf(buffer,  boundStart + boundayFinder.getBoundaryLength(), length);
		if (boundEnd != -1) {
			pos = boundEnd;
		}
		return boundStart >= 0 && boundEnd > 0;
	}

	/**
	 * find the sub-boundary from the position behind current content header
	 * 
	 * @return true if found the sub-boundary
	 */
	public boolean findSub() {
		int s = readContentHeader();
		if (s == -1)
			return false;
		boundStart = subBoundayFinder.indexOf(buffer, s, length);
		if (boundStart != -1)
			boundEnd = subBoundayFinder.indexOf(buffer, boundStart + subBoundayFinder.getBoundaryLength(), length);
		if (boundEnd != -1) {
			pos = boundEnd;
		}
		return boundStart >= 0 && boundEnd > 0;
	}

	/**
	 * determine if the sub-boundary ends within the current boundary
	 * 
	 * @return
	 */
	public boolean endSub() {
		if (boundEnd > 0) {
			boundEnd = boundayFinder.indexOf(buffer, boundEnd, length);
			if (boundEnd != -1) {
				pos = boundEnd;
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the start position that the boundary found
	 */
	public int getBoundStart() {
		return boundStart;
	}

	/**
	 * the end of the data chunk for a uploading file, also, it's the start of
	 * next boundary in the current buffer.
	 * 
	 * @return
	 */
	public int getBoundEnd() {
		return boundEnd;
	}

	/**
	 * from the <em>boundStart</em> position of buffer, read each line of
	 * content header, put header section into a map if found a whole content
	 * header successfully. meanwhile, the function return the end
	 * <em>position</em> of content header in the buffer.
	 * 
	 * @return position at the buffer if read a whole content header
	 *         successfully, else return -1;
	 */
	public int readContentHeader() {
		this.contentHeaderMap = null;

		int s = boundStart, p;
		p = readLine(s + 2); // read line boundary/sub-boundary
		if (p == -1)
			return p;

		s = p;
		p = readLine(s + 2);
		if (p == -1)
			return p;
		HashMap<String, String> dispostion = this.parseLine(this.substitute(buffer, s, p));

		this.contentHeaderMap = new ContentHeaderMap();
		this.contentHeaderMap.putAll(dispostion);

		if (this.contentHeaderMap.isFile()) {
			s = p;
			p = readLine(s + 2);
			if (p == -1)
				return p;
			HashMap<String, String> type = this.parseLine(this.substitute(buffer, s, p));

			this.contentHeaderMap.putAll(type);
			
			// TODO: perhaps, need to parse the content type to determine whether
			// read transfer encoding into header map.
		}

		return p + 2;
	}

	/**
	 * from the boundary start position, substitute the buffer for content
	 * header in the current chunk that found in the buffer
	 * 
	 * @return a new string contains contents.
	 * @throws IOException
	 */
	public String substituteContent() {
		int s = readLine(boundStart + 2);
		int e = readLine(s + 2);
		e = readLine(e + 2);
		return substitute(buffer, s + 2, e);
	}

	private String substitute(byte[] buffer, int start, int end) {
		return new String(subBuffer(buffer, start, end));
	}

	private byte[] subBuffer(byte[] buffer, int start, int end) {
		byte[] bs = new byte[end - start];
		System.arraycopy(buffer, start, bs, 0, bs.length);
		return bs;
	}

	/**
	 * read the current buffer from <em>pos</em>,
	 * 
	 * @param pos
	 *            start position of current buffer
	 * @return the position of next CRLF character if found, else return
	 *         <em>-1</em>
	 */
	private int readLine(int pos) {
		for (int i = pos; i < length; i++) {
			if (buffer[i] == 0x0a)
				return i;
		}
		return -1;
	}


	/**
	 * create {@link BoundaryFinder} object for the sub-boundary bytes.
	 * @param subBoundary
	 */
	public void setSubBoundary(byte[] subBoundary) {
		this.subBoundayFinder = new BoundayFinder(subBoundary);
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public HashMap<String, String> parseLine(String line) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		String[] sections = line.split(";");
		for (String sec : sections) {
			String[] vals = sec.split(":|(=\")");
			if (vals.length > 1)
				hashMap.put(vals[0].trim(), vals[1].trim().replaceAll("\"", ""));
		}
		return hashMap;
	}

	static public void main(String args[]) {
		UploadChunk chunk = new UploadChunk(null, null, 0);
		HashMap<String, String> header = chunk.parseLine("Content-Disposition: form-data; name=\"_file1\"; filename=\"oauthapi key.txt\"");
		System.out.println(header);
	}

	/**
	 * @return the contentHeaderMap
	 */
	public ContentHeaderMap getContentHeaderMap() {
		return contentHeaderMap;
	}
}
