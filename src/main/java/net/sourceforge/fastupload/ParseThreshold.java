/**
 
 */
package net.sourceforge.fastupload;


/**
 * an interface that declare the total length of {@link HttpServletRequest} input
 * stream.
 * 
 * 
 * 
 * @author linkqian
 * 
 */
public interface ParseThreshold {

	/**
	 * 
	 * @param parseThreshold
	 */
	public void setParseThreshold(int parseThreshold);

	/**
	 * 
	 * @return
	 */
	public int getParseThreshold();

}
