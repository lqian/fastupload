package net.sourceforge.fastupload;

/**
 * a factory interface that declare method create object which is instance of sub-class of
 * {@link MultiPartFile}
 * 
 * @author linkqian
 * 
 */
public interface FileFactory extends ParseThreshold {

	/**
	 * create a object instance which is sub-class of {@link MultiPartFile}
	 * specified. afterwards, open a file output stream when the object is
	 * created.
	 * 
	 * @param name
	 *            , a correct operation system file name with full path.
	 * @param cls
	 *            , sub-class of {@link MultiPartFile}
	 * @return an object of sub-class of {@link MultiPartFile} specified in last
	 *         parameter in the function declaration.
	 */
	public <T extends MultiPartFile> T createMulitPartFile(String name, Class<? extends MultiPartFile> cls);

}
