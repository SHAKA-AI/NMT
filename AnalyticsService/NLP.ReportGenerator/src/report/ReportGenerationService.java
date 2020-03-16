package report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import exec.ConfigLoader;
import utilities.ConfigConstants;
import utilities.PTLogger;
import dal.dao.ReportDAO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
// import net.sf.jasperreports.engine.query.JRJpaQueryExecuterFactory;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ReportGenerationService {
		
	// Suppress default constructor for noninstantiability
	private ReportGenerationService()
	{
		throw new AssertionError();
	}
	
	public static void generateReport() 
	{
		try {
			// TODO - as of December 23rd 2013: JapserReports 5.50 contains bug - requires ServletOutputStream even for Java SE applications
			// once fix is available, download it and remove servlet-api.jar
			
			// object that contains the compiled jrxml file (a compiled report is equivalent to a .jasper file)
			JasperReport paratTextEvalReport = JasperCompileManager.compileReport("./reportGenTemplate/ParatTextStatusReport.jrxml");
			JasperReport analyticsSubReport = JasperCompileManager.compileReport("./reportGenTemplate/AnalyticsSubReport.jrxml");

			// parameter container that will be sent from application to the jrxml
			HashMap<String, Object> jasperParameterMap = new HashMap<String, Object>();
			// Use this line for incorporating JPA in the report
	//		jasperParameterMap.put(JRJpaQueryExecuterFactory.PARAMETER_JPA_ENTITY_MANAGER, PersistenceManager.entityManager);
			jasperParameterMap.put("analyticsSubReportParameter", analyticsSubReport);
			
			// Build a package that will be sent to the Jasper Report via the JRBeanDataSource
			List<ReportPackage> packageList = buildReportPackage();
			
			// object that contains the report after it has been populated with data
			JasperPrint jasperPrint = JasperFillManager.fillReport(paratTextEvalReport, jasperParameterMap, new JRBeanCollectionDataSource(packageList)); 

			exportReportToFile(jasperPrint);
		}
		catch (Exception e) {
			PTLogger.error("PTRSTATUS1: Error occurred while generating report");
			PTLogger.exception(e);
		}
	}
	
	private static void exportReportToFile(JasperPrint jasperPrint)
	{
		String reportFilePath = getReportFilePathWithName();

		try {
			// export to PDF
			if(ConfigLoader.REPORT_FILE_TYPE.equalsIgnoreCase(ConfigConstants.PDF))
				JasperExportManager.exportReportToPdfFile(jasperPrint, reportFilePath);

			// export to HTML
			else if(ConfigLoader.REPORT_FILE_TYPE.equalsIgnoreCase(ConfigConstants.HTML))
				JasperExportManager.exportReportToHtmlFile(jasperPrint, reportFilePath); 

			// export to Excel sheet
			else if(ConfigLoader.REPORT_FILE_TYPE.equalsIgnoreCase(ConfigConstants.XLS))
			{
				JRXlsExporter exporter = new JRXlsExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, reportFilePath);
				exporter.exportReport();
			}
			// export to Word document
			else if(ConfigLoader.REPORT_FILE_TYPE.equalsIgnoreCase(ConfigConstants.DOCX))
			{
				JRDocxExporter exporter = new JRDocxExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, reportFilePath);
				exporter.exportReport();
			}
		} 
		catch (JRException e) {
			PTLogger.error("PTRSTATUS2: Error occurred while exporting report to file: " + reportFilePath);
			PTLogger.exception(e);
		}
	}
	
	private static List<ReportPackage> buildReportPackage()
	{
		ReportPackage reportPackage = new ReportPackage();
		ReportDAO reportDAO = new ReportDAO();
		List<String> fileNameList = reportDAO.readFileNameList(ConfigLoader.JOB_NAME, ConfigLoader.BEGIN_DATE);
		
		reportPackage.setJobName(ConfigLoader.JOB_NAME);
		
		reportPackage.setTotalNumOfFiles(reportDAO.readNumberTotalFiles(ConfigConstants.JOB_DETAILS_TABLE, ConfigLoader.JOB_ID));
		
		Date beginDate = ConfigLoader.BEGIN_DATE;
		if(beginDate.equals(new Date(0)))
			beginDate = reportDAO.readBeginDate(ConfigConstants.JOB_DETAILS_TABLE, ConfigLoader.JOB_ID);
		reportPackage.setBeginDate(beginDate);
		
		reportPackage.setEndDate(reportDAO.readEndDate(ConfigConstants.JOB_DETAILS_TABLE, ConfigLoader.JOB_ID));
		
		long numFilesProcessed = reportDAO.readNumberProcessedFiles(ConfigConstants.JOB_DETAILS_TABLE, ConfigLoader.JOB_ID);
		reportPackage.setNumOfFilesProcessed(numFilesProcessed);
		
		AnalyticsCalcService analyticsCalc = new AnalyticsCalcService(fileNameList, numFilesProcessed);
		reportPackage.setPtAnalyticsList(analyticsCalc.getAnalytics(ConfigLoader.JOB_NAME));
		
		String deIdentProcess = reportDAO.readDeIdentProcess(ConfigLoader.JOB_NAME, (fileNameList.isEmpty())? "" : fileNameList.get(0), ConfigLoader.BEGIN_DATE);
		reportPackage.setDeIdentProcess(determineDeIdentProcessLabel(deIdentProcess));
		// JRBeanDataSources only accept lists
		List<ReportPackage> inputList = new ArrayList<ReportPackage>();
		inputList.add(reportPackage);
		
		return inputList;
	}
	
	private static String determineDeIdentProcessLabel(String deIdentProcess)
	{
		switch(deIdentProcess)
		{
			case "REDACT": return "Redact";
			case "REDACTANDTAG": return "Redact and Tag";
			case "RANDOMIZE": return "Randomize";
			default: return "";
		}
	}
	
	/**
	 * Report folder path is supplied by job file.
	 * File name is: "jobName_stat_timestamp.reportFileType"
	 * 
	 * @return
	 */
	private static String getReportFilePathWithName()
	{
		StringBuilder filePathName = new StringBuilder(ConfigLoader.REPORT_FOLDER_PATH);
		if(filePathName.lastIndexOf("\\") != filePathName.length() - 1)
			filePathName.append("\\");
		filePathName.append(ConfigLoader.JOB_NAME);
		filePathName.append("_stat_");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss");
		filePathName.append(dateFormat.format(new Date()));
		
		filePathName.append(".");
		filePathName.append(ConfigLoader.REPORT_FILE_TYPE.toLowerCase());
		return filePathName.toString();
	}
}
