package report;

import java.util.Date;
import java.util.List;

import dal.domain.PTAnalytics;

public class ReportPackage {

	private long numOfFilesProcessed;
	private long totalNumOfFiles;
	private Date beginDate;
	private Date endDate;
	private String jobName;
	private String deIdentProcess;
	private List<PTAnalytics> ptAnalyticsList;
	
	public List<PTAnalytics> getPtAnalyticsList() {
		return ptAnalyticsList;
	}
	
	public void setPtAnalyticsList(List<PTAnalytics> ptAnalyticsList) {
		this.ptAnalyticsList = ptAnalyticsList;
	}
	
	public long getNumOfFilesProcessed() {
		return numOfFilesProcessed;
	}
	
	public void setNumOfFilesProcessed(long numOfFilesProcessed) {
		this.numOfFilesProcessed = numOfFilesProcessed;
	}
	
	public long getTotalNumOfFiles() {
		return totalNumOfFiles;
	}

	public void setTotalNumOfFiles(long totalNumOfFiles) {
		this.totalNumOfFiles = totalNumOfFiles;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getDeIdentProcess() {
		return deIdentProcess;
	}

	public void setDeIdentProcess(String deIdentProcess) {
		this.deIdentProcess = deIdentProcess;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
