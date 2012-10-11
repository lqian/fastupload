package net.sourceforge.fastupload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.fastupload.exception.FileUploadParserException;
import net.sourceforge.fastupload.util.BoyerMoore;


/**
 * @deprecated since V0.23
 * 
 * 
 * @author linkqian
 *
 */

public class FileUploadParser {

	
	private static final String _ENCTYPE = "multipart/form-data";
	private final String _FILE_NAME_KEY = "filename";
	private final String _TEXT_CONTENT_TYPE_PREFIX = "text/";

	private final byte[] _CRLF = { 0X0D, 0X0A };

	private int bufferSize = 0x20000;

	private byte[] boundary;

	private byte[] subBoundary;

	private HttpServletRequest request;

	private FileFactory fileFactory;

	/**
	 * content length of HttpServletRequest header
	 */
	private long contentLength;

	/**
	 * read bytes of form uploading request
	 */
	private long readBytes;

	private ProgressListener progressListener;

	private boolean isSubBoundary = false;

	public FileUploadParser(HttpServletRequest request, FileFactory fileFactory) {
		super();
		this.request = request;
		this.fileFactory = fileFactory;
		this.parseFormContentLength();
	}

	/**
	 * parse the request of multiple part data of form. and write content of
	 * file into file at the directory specified. finally, return a list
	 * contains the object of {@link MultiPartFile}
	 * 
	 * @return List<MultiPartFile>
	 * @throws IOException
	 */
	public List<MultiPartFile> parse() throws IOException {
		List<MultiPartFile> files = new ArrayList<MultiPartFile>();
		parseEnctype();
		byte[] buffer = new byte[bufferSize];
		boolean hasFile = false;
		boolean end = true;
		MultiPartFile multiPartFile = null;
		int c = 0;
		while ((c = request.getInputStream().read(buffer)) != -1) {
			readBytes += c;
			boolean isNewSegment = true;
			for (int p = 0; p != -1;) {
				if (isSubBoundary)
					p = BoyerMoore.indexOf(buffer, subBoundary, p);
				else
					p = BoyerMoore.indexOf(buffer, boundary, p);

				if (end) {
					MultiPartFile mpf = parseFile(buffer, p);
					if (mpf != null) {
						files.add(mpf);
						p = mpf.getStart();
						end = false;
						hasFile = true;
					} else {
						hasFile = false;
						end = true;
						p += boundary.length;
					}
				} else if (hasFile) {
					// write buffer to last opening file if current index
					// identifies the start of boundary. and close the file.
					MultiPartFile writer = files.get(files.size() - 1);
					if (isNewSegment) {
						writer.append(buffer, 0, p - 4);
					} else {
						int off = writer.getStart();
						writer.append(buffer, off, p - off - 4);
					}
					writer.close();

					// start a new parse action
					MultiPartFile next = parseFile(buffer, p);
					if (next != null) {
						files.add(next);
						p = next.getStart();
						end = false;
						hasFile = true;
					} else {
						hasFile = false;
						end = true;
						p += boundary.length;
					}

				}
				isNewSegment = false;
			}
			// not found boundary, append the buffer into file
			if (!end) {
				MultiPartFile writer = files.get(files.size() - 1);
				if (isNewSegment) {
					writer.append(buffer, 0, c);
				} else {
					int off = writer.getStart();
					writer.append(buffer, off, c - off);
				}
			}
		}
		return files;
	}

	/**
	 * parse file name and start position of its content in the buffer and
	 * determines whether file is binary or text, the function changes the
	 * <em>p</em> value by find the boundary.
	 * 
	 * @param buffer
	 * @param pos
	 *            , point the current byte of the buffer.
	 * @return a {@link MultiPartFile} object if find a uploading file, else
	 *         return null.
	 * @throws IOException
	 */
	private MultiPartFile parseFile(byte[] buffer, int pos) throws IOException {
		int s = pos + boundary.length + _CRLF.length; // line start position
		int e = BoyerMoore.indexOf(buffer, _CRLF, s); // line end position
		String[] content = substitute(buffer, s, e).split(";");

		if (content.length == 3 && content[2].indexOf(_FILE_NAME_KEY) > 0) {
			String name = content[2].split("=")[1].replace("\"", "");
			if (name != null && name.trim().length() > 0) {
				MultiPartFile mpf = null;

				// determine the start position of content-type line
				s = BoyerMoore.indexOf(buffer, _CRLF, e);

				// determine the line of content-type
				e = BoyerMoore.indexOf(buffer, _CRLF, s + _CRLF.length);
				String contentType = substitute(buffer, s, e);
				if (contentType.indexOf(_TEXT_CONTENT_TYPE_PREFIX) != -1) {
					mpf = this.fileFactory.createMulitPartFile(name, MultiPartTextFile.class);
				} else {
					mpf = this.fileFactory.createMulitPartFile(name, MultiPartBinaryFile.class);
				}

				pos = e + _CRLF.length * 2;
				mpf.setStart(pos);

				return mpf;
			} else
				return null;
		} else
			return null;
	}
	
	/**
	 * 
	 * @param buffer
	 * @param bound
	 * @param subBound
	 */
	private void doParseSubBoundary(byte[] buffer, int pos) {

	}

	private void doParseBoundary(byte[] buffer, int start) {
		int p=0;
		do {
			p = BoyerMoore.indexOf(buffer, boundary, start);
		} while (p != -1);
	}


	private String substitute(byte[] buffer, int start, int end) {
		byte[] bs = new byte[end - start];
		System.arraycopy(buffer, start, bs, 0, bs.length);
		return new String(bs);
	}

	

	private void parseEnctype() {
		String[] content = request.getHeader("content-type").split(";");
		if (content.length > 1) {
			if (!_ENCTYPE.equalsIgnoreCase(content[0])) {
				throw new FileUploadParserException();
			}
			boundary = content[1].split("=")[1].getBytes();
		} else {
			throw new FileUploadParserException();
		}
	}

	private void parseFormContentLength() {
		String entryHeader = "Content-Length";
		String entryValue = this.request.getHeader(entryHeader);
		contentLength = Long.parseLong(entryValue);
	}

	public long getContentLength() {
		return contentLength;
	}

	public long getReadBytes() {
		return readBytes;
	}

	public ProgressListener getProgressListener() {
		return progressListener;
	}

	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	public static void main(String args[]) {
		String section[] = "Content-type: multipart/mixed, boundary=BbC04y".split(";");
		System.out.println();
	}
}
