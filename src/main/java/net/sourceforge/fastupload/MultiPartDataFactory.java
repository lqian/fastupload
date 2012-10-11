/**
 
 */
package net.sourceforge.fastupload;

/**
 * 
 * an interface declares methods that create object which is instance of
 * sub-class of {@link MultiPartData}
 * 
 * @author linkqian
 * 
 */
public interface MultiPartDataFactory extends ParseThreshold {

	/**
	 * 
	 * @param name
	 *            , the name for identifying the instance of sub-class of
	 *            {@link MultiPartData}
	 * @param cls
	 *            the class of sub-class of {@link MultiPartData}
	 * @return an object that is instance of sub-class of {@link MultiPartData}
	 */
	public <T extends MultiPartData> T createMultiPartData(String name, Class<? extends MultiPartData> cls);
}
