package report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import utilities.ConfigConstants.PHITokenEnum;
import dal.dao.ReportDAO;
import dal.domain.PTAnalytics;
import utilities.PTLogger;

public class AnalyticsCalcService {

	private Map<PHITokenEnum, PHIEvalInfo> evaluationMap;
	
	private List<String> fileNameList;
	private long numFilesProcessed = 0;
	
	public AnalyticsCalcService(List<String> fileNameList, long numFilesProcessed) {
		evaluationMap = new HashMap<PHITokenEnum, PHIEvalInfo>();
		this.fileNameList = fileNameList;
		this.numFilesProcessed = numFilesProcessed;
	}

	public List<PTAnalytics> getAnalytics(String jobName) {

		List<PTAnalytics> analyticsList = new ArrayList<PTAnalytics>();
		try 
		{
			for (String fileName : this.fileNameList) {	
				Hashtable<PHITokenEnum, List<String>> annotationsAnnie = extractAnnieAnnotations(fileName, jobName);

				// Look over all PHI types for Annie detected annotations
				for (PHITokenEnum type : annotationsAnnie.keySet()) {
					List<String> annotationsA = annotationsAnnie.get(type);
					
					PHIEvalInfo evalInfo = this.evaluationMap.get(type); 
					if(evalInfo == null) evalInfo = new PHIEvalInfo();	
					
					evalInfo.addToNumPHIByTypeForAnalytics(annotationsA.size());
					this.evaluationMap.put(type, evalInfo);
				}

			}
			// Averaging scores by types: first_name,middle_name, ...
			if (this.numFilesProcessed!=0)
			{
				for (PHITokenEnum phiToken : PHITokenEnum.values()) 
				{					
					PHIEvalInfo evalInfo = this.evaluationMap.get(phiToken); 
					if(evalInfo == null) continue;

					PTAnalytics analytic = new PTAnalytics();
					analytic.setPhiType(phiToken.toString());
					analytic.setNumOfPHIsDetected(evalInfo.getNumPHIByTypeForAnalytics());
					analytic.setAvgNumPerDocument(((float) evalInfo.getNumPHIByTypeForAnalytics()) / ((float) this.numFilesProcessed));
					
					analyticsList.add(analytic);
				}
			}
			
		}
		catch (Exception e) {
			PTLogger.error("PTRACSERVICE1: Problem with analytics calculations" + e.getMessage());
		}
		return analyticsList;
	}
	
	// Extract Annie annotations with their types and put them in a hashtable.
	// <type, List<Annotations>>
	public Hashtable<PHITokenEnum, List<String>> extractAnnieAnnotations(String fileName, String jobName) {
		Hashtable<PHITokenEnum, List<String>> annotationsAnnie = new Hashtable<PHITokenEnum, List<String>>() ;
		try {
			ReportDAO reportDAO = new ReportDAO();
			List<Object[]> phiList = reportDAO.readPHIValueAndType(fileName, jobName);
			 
			for (Object[] phiRecord : phiList) {
				String ann = (String) phiRecord[0];
				ann = ann.replaceAll(" +", " ");
				String type = ((String) phiRecord[1]).toUpperCase();
				List<String> annotations = new ArrayList<String>();

				if (PHITokenEnum.contains(type))
				{
					PHITokenEnum tokenType = PHITokenEnum.valueOf(type);

					if (!annotationsAnnie.containsKey(tokenType)) {
						annotations.add(ann);
						annotationsAnnie.put(tokenType, annotations);
					}
					else {
						annotations = annotationsAnnie.remove(tokenType);
						annotations.add(ann);
						annotationsAnnie.put(tokenType, annotations);
					}  
				}
				else
				{
					PTLogger.warning(String.format("PTRACSERVICE2: GATE issue.  The type: '%s' is not a discovery annotation type, so it will be excluded from the evaluation.",
							type));
				}
			} 
		}
		catch (Exception e) {
			PTLogger.error("PTRACSERVICE3: Cannot extract PHIs from database for file: " + fileName + " - " + e.getMessage());
		}
		return annotationsAnnie;
	}

	public List<String> getFileNameList() {
		return fileNameList;
	}

	public void setFileNameList(List<String> fileNameList) {
		this.fileNameList = fileNameList;
	}
	
}
