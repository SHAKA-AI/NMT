package exec;

import report.ReportGenerationService;
import utilities.PTLogger;

/**
 * 
 * @author jkane
 */
public class ParatTextReportExec
{

	public static void main(String[] args)
	{
		try
		{
			PTLogger.setClassName(ParatTextReportExec.class.getName());
			
			String strJAVAversion = System.getProperty("java.version");
			String[] majorJavaVersion = strJAVAversion.split("(?=\\.[0-9_]+$)");
			double javaVer = Double.parseDouble(majorJavaVersion[0]);

			if (javaVer < 1.7)
			{
				PTLogger.error(
						String.format(
								"Parat for Text requires Java 1.7 or above, current installed version: %d",
								strJAVAversion), true);
				return;
			}


			if (!ConfigLoader.loadCommandlineParams(args))
			{
				PTLogger.error("PTREPORT1: An error occurred while loading parameters.  Process halted.");
				return;
			}	
			
			PTLogger.info("Report Generation Start");
			ReportGenerationService.generateReport();

			PTLogger.info("Report Generation End");

		}
		catch(Exception ex)
		{
			PTLogger.error("PTREPORT4: An error occurred while executing ParatTextReportGenerator");
			PTLogger.exception(ex);
		}
	}

}
