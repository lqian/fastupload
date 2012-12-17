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

/**
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
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

	private int contentStart;

	private BoundaryFinder boundayFinder;

	private BoundaryFinder subBoundayFinder;

	private final int PRE_SKIP = 40;
	
	

	public UploadChunk(BoundaryFinder boundayFinder) {
		super();
		this.boundayFinder = boundayFinder;
	}

	public UploadChunk(byte[] buffer, BoundaryFinder boundayFinder) {
		super();
		this.pos = 0;
		this.buffer = buffer;
		this.length = buffer.length;
		this.boundayFinder = boundayFinder;
	}

	public UploadChunk(byte[] buffer, BoundaryFinder boundayFinder, int pos) {
		super();
		this.pos = pos;
		this.buffer = buffer;
		this.length = buffer.length;
		this.boundayFinder = boundayFinder;
	}

	public UploadChunk(byte[] buffer, BoundaryFinder boundayFinder, int pos, int length) {
		super();
		this.pos = pos;
		this.buffer = buffer;
		this.boundayFinder = boundayFinder;
		this.length = length;
	}

	public UploadChunk(byte[] buffer, byte[] boundary, int pos) {
		this.boundayFinder = new BoundaryFinder(boundary);
		this.buffer = buffer;
		this.pos = pos;
	}

	public UploadChunk(byte[] buffer, byte[] boundary, int pos, int length) {
		this.boundayFinder = new BoundaryFinder(boundary);
		this.buffer = buffer;
		this.pos = pos;
		this.length = length;
	}

	/**
	 * convenience method append whole bytes of buffer to current buffer
	 * @param buff
	 */
	public void append(byte[] buff) {
		this.append(buff, 0, buff.length);
	}
	
	/**
	 * insert bytes of delta from <em>pos</em> with length <em>len</em> before current buffer 
	 * @param delta
	 * @param pos
	 * @param len
	 */
	public void insertDelta(byte[] delta, int pos, int len) {
		ByteBuffer bb = ByteBuffer.allocate(this.length + len);
		bb.put(delta, pos, len);
		bb.put(this.buffer, len, this.length);
		this.buffer = bb.array();
		this.length = this.buffer.length;
	}

	/**
	 * append bytes of <em>buff</em> from <em>pos</em> with length <em>len</em> behind current buffer
	 * @param buff
	 * @param pos
	 * @param len
	 */
	public void append(byte[] buff, int pos, int len) {
		ByteBuffer bb = ByteBuffer.allocate(this.length + len);
		bb.put(this.buffer, 0, this.length);
		bb.put(buff, 0, len);
		this.buffer = bb.array();
		this.length += len;
	}

	/**
	 * convenience method set whole <em>buff</em> as current chunk buffer
	 * @param buff
	 */
	public void setBuffer(byte[] buff) {
		setBuffer(buff, 0, buff.length);
	}
	
	/**
	 * set current buffer with <em>buff</em> start at <em>pos</em> with length <em>len</em> as current chunk buffer
	 * @param buff
	 * @param pos
	 * @param len
	 */
	public void setBuffer(byte[] buff, int pos, int len) {
		this.buffer = buff;
		this.pos = pos;
		this.length = len;
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
		boundStart = boundayFinder.indexOf(buffer, pos);
		if (boundStart != -1) {
			pos = boundStart + boundayFinder.getBoundaryLength() + PRE_SKIP;
			if (pos >= length - 1) {// if out of bound index
				boundEnd = -1;
				return false;
			}	
			else {
				boundEnd = boundayFinder.indexOf(buffer, pos);
				if (boundEnd != -1) {
					pos = boundEnd;
					return true;
				}
				else 
					return false;
			}
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
			boundEnd = subBoundayFinder.indexOf(buffer, boundStart + subBoundayFinder.getBoundaryLength() + PRE_SKIP, length);
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
	 * <em>contentStart</em> position of current boundary within current buffer.
	 * 
	 * @return position at the buffer if read a whole content header
	 *         successfully, else return -1;
	 */
	public int readContentHeader() {
		int s = boundStart + boundayFinder.getBoundaryLength() + 2, p;  
		p = readLine(s); // read disposition
		if (p == -1)
			return p;
		if (p - s < PRE_SKIP)
			return -1;
		HashMap<String, String> disposition = this.parseLine(this.substitute(buffer, s, p));

		contentHeaderMap = new ContentHeaderMap();
		contentHeaderMap.putAll(disposition);

		if (contentHeaderMap.isFile()) {
			s = p;
			p = readLine(s + 2);
			if (p == -1)
				return p;
			HashMap<String, String> type = this.parseLine(this.substitute(buffer, s, p));

			contentHeaderMap.putAll(type);

			// TODO: perhaps, need to parse the content type to determine
			// whether
			// read transfer encoding into header map.
		}

		contentStart = p + 3;
		return contentStart;
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
	 * 
	 * @param subBoundary
	 */
	public void setSubBoundary(byte[] subBoundary) {
		this.subBoundayFinder = new BoundaryFinder(subBoundary);
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public HashMap<String, String> parseLine(String line) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		String[] sections = line.split(";\\s");
		for (String sec : sections) {
			String[] vals = sec.split(":\\s|(=\")");
			if (vals.length > 1)
				hashMap.put(vals[0].trim(), vals[1].trim().replaceAll("\"", ""));
		}
		return hashMap;
	}

	/**
	 * @return the contentHeaderMap
	 */
	public ContentHeaderMap getContentHeaderMap() {
		return contentHeaderMap;
	}

	/**
	 * @return the contentStart
	 */
	public int getContentStart() {
		return contentStart;
	}

}
