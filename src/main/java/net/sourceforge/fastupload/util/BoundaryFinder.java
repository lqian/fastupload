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

/**
 * 
 * The class references the <em>Boyer-Moore string search algorithm</em> in <a
 * href
 * ="http://en.wikipedia.org/wiki/Boyer%E2%80%93Moore_string_search_algorithm"
 * >wiki page</a>. The algorithm was developed by <a
 * href="http://en.wikipedia.org/wiki/Robert_S._Boyer">Robert S. Boyer</a> and
 * <a href="http://en.wikipedia.org/wiki/J_Strother_Moore">J Strother Moore</a>
 * in 1977.
 * 
 * The class make a bit enhancement that enable original code searching java
 * bytes. also, the class refactor the original code removing static functions.
 * 
 * 
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class BoundaryFinder {

	private byte[] boundary;

	private int[] charTable;

	private int[] offsetTable;

	public BoundaryFinder(byte[] boundary) {
		super();
		this.boundary = boundary;
		charTable = makeCharTable(boundary);
		offsetTable = makeOffsetTable(boundary);
	}
	
	public int getBoundaryLength() {
		return boundary.length;
	}
 
	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring. If it is not a substring, return -1.
	 * 
	 * @param buffer
	 *            The byte buffer to be scanned
	 
	 * @return The start index of the boundary
	 */
	public int indexOf(byte[] buffer) {
		return indexOf(buffer, 0);
	}

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring. If it is not a substring, return -1.
	 * 
	 * @param text
	 *            The string to be scanned
	 
	 * @return The start index of the boundary
	 */
	public int indexOf(byte[] buffer, int start) {
		return indexOf(buffer, start, buffer.length);
	}

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring. If it is not a substring, return -1.
	 * 
	 * @param buffer
	 *            The byte buffer to be scanned
 
	 * @return The start index of the boundary
	 */
	public int indexOf(byte[] buffer, int start, int end) {
		if (boundary.length == 0) {
			return 0;
		}
		for (int i = boundary.length - 1 + start, j; i < end;) {
			for (j = boundary.length - 1; boundary[j] == buffer[i]; --i, --j) {
				if (j == 0) {
					return i;
				}
			}
			// i += needle.length - j; // For naive method
			int offset = Math.max(offsetTable[boundary.length - 1 - j], charTable[buffer[i] < 0 ? buffer[i] + 256 : buffer[i]]);
			i += offset;
		}
		return -1;
	}

	/**
	 * Makes the jump table based on the mismatched character information.
	 */
	private int[] makeCharTable(byte[] needle) {
		final int ALPHABET_SIZE = 256;
		int[] table = new int[ALPHABET_SIZE];
		for (int i = 0; i < table.length; ++i) {
			table[i] = needle.length;
		}
		for (int i = 0; i < needle.length - 1; ++i) {
			table[needle[i]] = needle.length - 1 - i;
		}
		return table;
	}

	/**
	 * Makes the jump table based on the scan offset which mismatch occurs.
	 */
	private int[] makeOffsetTable(byte[] needle) {
		int[] table = new int[needle.length];
		int lastPrefixPosition = needle.length;
		for (int i = needle.length - 1; i >= 0; --i) {
			if (isPrefix(needle, i + 1)) {
				lastPrefixPosition = i + 1;
			}
			table[needle.length - 1 - i] = lastPrefixPosition - i + needle.length - 1;
		}
		for (int i = 0; i < needle.length - 1; ++i) {
			int slen = suffixLength(needle, i);
			table[slen] = needle.length - 1 - i + slen;
		}
		return table;
	}

	/**
	 * Is needle[p:end] a prefix of needle?
	 */
	private boolean isPrefix(byte[] needle, int p) {
		for (int i = p, j = 0; i < needle.length; ++i, ++j) {
			if (needle[i] != needle[j]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the maximum length of the substring ends at p and is a suffix.
	 */
	private int suffixLength(byte[] needle, int p) {
		int len = 0;
		for (int i = p, j = needle.length - 1; i >= 0 && needle[i] == needle[j]; --i, --j) {
			len += 1;
		}
		return len;
	}
	
	public static void main(String[] args) {
		byte[] boundary = "---------------------------194760164713951335551783200939".getBytes();
		byte[] boundary2 = new byte[boundary.length];
		BoundaryFinder bf = new BoundaryFinder(boundary);
		byte[] text= ("-----------------------------194760164713951335551783200939\r\n" +
				"Content-Disposition: form-data; name=\"_text1.text1\"\r\n" +
				"123123123123dfdfsdfsdafsdaffdafasfa0939ttttttttttttttttttttertertqweqweqwrrrrr9391112235551783200939\r\n" +
				"-----------------------------194760164713951335551783200939--").getBytes();
		int i = bf.indexOf(text, 20);
		System.arraycopy(text, i, boundary2, 0, boundary.length);
		System.out.println("index: " + i  + " " + new String(boundary2) );
	}
}
