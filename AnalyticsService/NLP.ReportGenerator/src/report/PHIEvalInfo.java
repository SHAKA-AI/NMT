package report;

/**
 * This package contains analytic measurements for a 
 * single PHI type
 * 
 * As report columns grow, so should this Java bean
 * 
 * @author jkane
 *
 */
public class PHIEvalInfo {

	// Analytics
	private int numPHIByTypeForAnalytics;
	
	
	public int getNumPHIByTypeForAnalytics() {
		return numPHIByTypeForAnalytics;
	}
	public void setNumPHIByTypeForAnalytics(int numPHIByTypeForAnalytics) {
		this.numPHIByTypeForAnalytics = numPHIByTypeForAnalytics;
	}
	public void addToNumPHIByTypeForAnalytics(int amount) {
		this.numPHIByTypeForAnalytics += amount;
	}
	
	
}
