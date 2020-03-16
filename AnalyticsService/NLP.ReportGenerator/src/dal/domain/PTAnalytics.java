package dal.domain;

public class PTAnalytics {

	private String phiType;	
	private int numOfPHIsDetected;
	private float avgNumPerDocument;
	

	public String getPhiType() {
		return phiType;
	}
	public void setPhiType(String phiType) {
		this.phiType = phiType;
	}
	public int getNumOfPHIsDetected() {
		return numOfPHIsDetected;
	}
	public void setNumOfPHIsDetected(int numOfPHIsDetected) {
		this.numOfPHIsDetected = numOfPHIsDetected;
	}
	public float getAvgNumPerDocument() {
		return avgNumPerDocument;
	}
	public void setAvgNumPerDocument(float avgNumPerDocument) {
		this.avgNumPerDocument = avgNumPerDocument;
	}

} 