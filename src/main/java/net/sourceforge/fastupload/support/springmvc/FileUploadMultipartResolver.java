package net.sourceforge.fastupload.support.springmvc;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.fastupload.DiskFileFactory;
import net.sourceforge.fastupload.FileFactory;
import net.sourceforge.fastupload.HttpFileUploadParser;
import net.sourceforge.fastupload.HttpMemoryUploadParser;
import net.sourceforge.fastupload.MemoryMultiPartDataFactory;
import net.sourceforge.fastupload.MultiPartFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

/**
 * Servlet-based {@link org.springframework.web.multipart.MultipartResolver}
 * implementation for fastUpload 0.4.7
 * 
 * <p>
 * Provides "maxUploadSize", "tmpDir"
 * ,"critical","maxFileSizeSingle","allowedExtensions","allowedTypes" and
 * "defaultEncoding" settings as bean properties
 * 
 * @author <a href="mailto:lkclkc88@sina.com>">Liao-Kucheng </a>
 * 
 */

public class FileUploadMultipartResolver implements MultipartResolver {

	private static MultipartResult EMPTY = new MultipartResult(new HashMap<Object,Object>(0), new HashMap<Object,Object>(0));

	protected final String _ENCTYPE = "multipart/";

	/*
	 * the tmp dirctor
	 */
	private String tmpDir = System.getProperty("user.home");

	/*
	 * it's will decide what way to parse the request
	 */

	private long critical = 0xfffff;
	/*
	 * the max size of total upload files
	 */
	private int maxUploadSize = 0xafffff;
	/*
	 * the max size of single file,teh default value is the maxUploadSize
	 */
	private int maxFileSizeSingle = maxUploadSize;

	private String allowedExtensions;

	private String allowedTypes;
	/*
	 * ignore upload size Exception
	 */
	private boolean ignoreUpLoadSizeException = false;

	private boolean ignoreUpLoadSize = false;

	/*
	 * character set，it's will use the character of request if not set it
	 */
	private String defaultEncoding;

	private final static Log log = LogFactory.getLog(FileUploadMultipartResolver.class);

	public boolean isMultipart(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("FileUploadMultipartResolver valiate  Multipart");
		}
		if (!"post".equals(request.getMethod().toLowerCase())) {
			return false;
		}
		String contentType = request.getContentType();
		if (contentType == null) {
			return false;
		}
		if (contentType.toLowerCase().startsWith(_ENCTYPE)) {
			return true;
		}
		return false;
	}

	public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
		if (log.isDebugEnabled()) {
			log.debug("FileUploadMultipartResolver resolveMuptipart");
		}

		Assert.notNull(request, "Request must not be null");
		if (null == defaultEncoding)
			this.defaultEncoding = request.getCharacterEncoding();

		return new DefaultMultipartHttpServletRequest(request) {
			protected void initializeMultipart() {
				MultipartResult result = parseRequest(request);
				setMultipartFiles(result.getFiles());
				setMultipartParameters(result.getParams());

			}
		};
	}

	/*
	 * parse teh request.
	 */
	private MultipartResult parseRequest(HttpServletRequest request) throws MultipartException {

		if (request.getContentLength() < critical) {
			return MemoryParseRequest(request);
		} else {
			return DiskParseReuqest(request);
		}
	}

	/*
	 * parse the request and store date in memory
	 * 
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws MultipartException
	 */
	private MultipartResult MemoryParseRequest(HttpServletRequest request) throws MultipartException {
		if (log.isDebugEnabled()) {
			log.debug("MemoryParseRequest()");
		}
		try {
			FileFactory mpdf = null;
			if (!this.ignoreUpLoadSize) {
				mpdf = new MemoryMultiPartDataFactory(this.defaultEncoding, this.maxFileSizeSingle);
				mpdf.setParseThreshold(maxUploadSize);
			} else {
				mpdf = new MemoryMultiPartDataFactory(this.defaultEncoding);
			}

			if (null != this.allowedExtensions)
				mpdf.setAllowedExtensions(allowedExtensions);

			if (null != this.allowedTypes)
				mpdf.setAllowedTypes(allowedTypes);

			HttpMemoryUploadParser httpMemoryUploadParser = new HttpMemoryUploadParser(request, mpdf);
			List<MultiPartFile> list = httpMemoryUploadParser.parseList();
			return parseMultiPartFile(list);

		} catch (Exception e) {
			if (!this.ignoreUpLoadSizeException)
				throw new MultipartException("parse request failed", e);

			return EMPTY;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MultipartResult parseMultiPartFile(List<MultiPartFile> list) throws Exception {
		Assert.notNull(list, "Request must not be null");
		MultipartResult result = new MultipartResult();
		Map files = new HashMap(0);
		Map params = new HashMap(0);
		for (MultiPartFile file : list) {
			if (file.isFile()){
				files.put(file.getFieldName(), new FastUploadMultipartAdapter(file));
			}
			else {
				if (null != file.getContentBuffer()) {
					String value =null;
					try{
					value =new String(file.getContentBuffer(), defaultEncoding);
					}catch(Exception e){
						if (log.isWarnEnabled()) {
							log.warn("Could not decode multipart item '" + file.getFieldName() +
									"' with encoding '" + defaultEncoding + "': using platform default");
						}
						value = new String(file.getContentBuffer());
					}
					String[] current = (String[]) params.get(file.getFieldName());
					if(null==current){
						params.put(file.getFieldName(), new String[]{value});
					}else{
						String[] newParam = StringUtils.addStringToArray(current, value);
						params.put(file.getFieldName(), newParam);
					}
				}
			}
		}
		result.setFiles(files);
		result.setParams(params);
		return result;
	}

	/*
	 * parse request and store in disk
	 */
	private MultipartResult DiskParseReuqest(HttpServletRequest request) throws MultipartException {
		if (log.isDebugEnabled()) {
			log.debug("DiskParseReuqest()");
		}
		try {
			DiskFileFactory dff = null;
			if (!this.ignoreUpLoadSize) {
				dff = new DiskFileFactory(tmpDir, defaultEncoding, this.maxFileSizeSingle);
				dff.setParseThreshold(maxUploadSize);
			} else {
				dff = new DiskFileFactory(tmpDir, defaultEncoding);
			}

			if (null != this.allowedExtensions)
				dff.setAllowedExtensions(allowedExtensions);

			if (null != this.allowedTypes)
				dff.setAllowedTypes(allowedTypes);

			HttpFileUploadParser parser = new HttpFileUploadParser(request, dff);
			List<MultiPartFile> list = parser.parse();
			return parseMultiPartFile(list);
		} catch (Exception e) {
			if (!this.ignoreUpLoadSizeException)
				throw new MultipartException("parse request failed", e);

			return EMPTY;
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void cleanupMultipart(MultipartHttpServletRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("cleanup Multipart");
		}
		Map<String, FastUploadMultipartAdapter> map = request.getFileMap();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			FastUploadMultipartAdapter file = (FastUploadMultipartAdapter) map.get(key);
			cleanTmpFile(file.getTmpFileName());
		}
	}

	/*
	 * clean tmp file
	 */
	private void cleanTmpFile(String fileName) {
		if (log.isDebugEnabled()) {
			log.debug("delete tmp file :" + fileName);
		}
		File file = new File(fileName);
		if (file.exists())
			file.delete();
	}

	public int getMaxFileSizeSingle() {
		return maxFileSizeSingle;
	}

	public void setMaxFileSizeSingle(int maxFileSizeSingle) {
		this.maxFileSizeSingle = maxFileSizeSingle;
	}

	public String getAllowedExtensions() {
		return allowedExtensions;
	}

	public void setAllowedExtensions(String allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
	}

	public String getAllowedTypes() {
		return allowedTypes;
	}

	public void setAllowedTypes(String allowedTypes) {
		this.allowedTypes = allowedTypes;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public long getCritical() {
		return critical;
	}

	public void setCritical(long critical) {
		this.critical = critical;
	}

	public int getMaxUploadSize() {
		return maxUploadSize;
	}

	public void setMaxUploadSize(int maxUploadSize) {
		this.maxUploadSize = maxUploadSize;
	}

	public boolean isIgnoreUpLoadSizeException() {
		return ignoreUpLoadSizeException;
	}

	public void setIgnoreUpLoadSizeException(boolean ignoreUpLoadSizeException) {
		this.ignoreUpLoadSizeException = ignoreUpLoadSizeException;
	}

	public boolean isIgnoreUpLoadSize() {
		return ignoreUpLoadSize;
	}

	public void setIgnoreUpLoadSize(boolean ignoreUpLoadSize) {
		this.ignoreUpLoadSize = ignoreUpLoadSize;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	private static class MultipartResult {

		private MultipartResult() {
			super();
		}

		private MultipartResult(Map<?,?> files, Map<?,?> params) {
			this.files = files;
			this.params = params;
		}

		private Map<?, ?> files;

		private Map<?, ?> params;

		public Map<?, ?> getFiles() {
			return files;
		}

		public void setFiles(Map<?, ?> files) {
			this.files = files;
		}

		public Map<?, ?> getParams() {
			return params;
		}

		public void setParams(Map<?, ?> params) {
			this.params = params;
		}
	}

}
