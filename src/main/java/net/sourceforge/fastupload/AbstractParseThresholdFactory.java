/**
 
 */
package net.sourceforge.fastupload;

/**
 * @author linkqian
 *
 */
public abstract class AbstractParseThresholdFactory {
	
	protected int parseThreshold;

	public int getParseThreshold() {
		return parseThreshold;
	}

	public void setParseThreshold(int parseThreshold) {
		this.parseThreshold = parseThreshold;
	}

}
