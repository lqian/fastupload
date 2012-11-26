package net.sourceforge.fastupload.support.springmvc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.fastupload.MultiPartFile;

import org.springframework.web.multipart.MultipartFile;

/**
 * FastUpload adapter for multipart resolvers that use FastUpload 0.4.7 or
 * higher it's will convert MultiPartFile to multipart
 * 
 * @author <a href="mailto:lkclkc88@sina.com>">Liao-Kucheng </a>
 * 
 */
public class FastUploadMultipartAdapter implements MultipartFile {

	private MultiPartFile file;

	public FastUploadMultipartAdapter(MultiPartFile file) {
		this.file = file;
	}

	public String getName() {
		return file.getFieldName();
	}

	public String getOriginalFilename() {
		return file.getFileName();
	}

	public String getContentType() {
		return file.getContentType();
	}

	public boolean isEmpty() {
		return file.getBytes() == 0;
	}

	/**
	 * return the file name which the file store in tmp directory
	 * 
	 * @return
	 */
	public String getTmpFileName() {
		return file.getName();
	}

	public long getSize() {
		return file.getBytes();
	}

	public byte[] getBytes() throws IOException {
		return file.getCharset().getBytes();
	}

	public String getFileName() {
		return file.getFileName();
	}

	public InputStream getInputStream() throws IOException {
		return file.getInputStream();

	}

	public void transferTo(File dest) throws IOException, IllegalStateException {
		file.toFile(dest.getAbsolutePath());

	}
}
