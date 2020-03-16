package exec;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import utilities.ConfigConstants;
import utilities.PTLogger;

public class ConfigLoader
{
	public static String JOB_NAME = "";
	public static String JOB_ID = "";
	public static String REPORT_FILE_TYPE = ConfigConstants.PDF;
	public static String REPORT_FOLDER_PATH = "";
	
	public static Date BEGIN_DATE = new Date(0);
	public static String SERVER = "";
	public static String PORT = "";
	public static String UID = "";
	public static String PWD = "";

	static
	{
		// "static constructor"
		PTLogger.setClassName(ConfigLoader.class.getName());
	}


	public static boolean loadCommandlineParams(String[] args)
	{
		boolean isServerSpecified = false;
		boolean isUidSpecified = false;
		boolean isPwdSpecified = false;
		
		for (String argument : args)
		{
			String[] argValues = argument.split("=");
			if (argValues.length == 2)
			{
				argValues[1] = argValues[1].trim();

				switch (argValues[0].toLowerCase())
				{
				case "server":
					if(!setValidServer(argValues[1]))
						return false;
					isServerSpecified = true;
					break;	
				case "port":
					if(!setValidPort(argValues[1]))
						return false;
					break;	
				case "uid":
					if(!setValidUid(argValues[1]))
						return false;
					isUidSpecified = true;
					break;	
				case "pwd":
					if(!setValidPwd(argValues[1]))
						return false;
					isPwdSpecified = true;
					break;	
				case "jobname":
					if(!setValidJobName(argValues[1]))
						return false;
					break;
				case "jobid":
					if(!setValidJobId(argValues[1]))
						return false;
					break;
				// optional
				case "begindate":
					setValidBeginDate(argValues[1]);
					break;
				case "reportfolderpath":
					if(!setValidReportFolderPath(argValues[1]))
						return false;
					break;
				case "reportfiletype":
					if(!setValidReportFileType(argValues[1]))
						return false;
					break;
				default:
					PTLogger.error(String.format("PTREPORTCL1 - Invalid argument name. Please specify correct argument name."), true);
					return false;
				}
			} 
			else
			{
				PTLogger.error(
						String.format("PTREPORTCL2 - Invalid arguments. Please specify: argumentname=argumentvalue"),
						true);
				return false;
			}
		}
		if(isServerSpecified && !isUidSpecified)
		{
			PTLogger.error(
					String.format("PTREPORTCL3 - Please specify database user id."),
					true);
			return false;
		}
		else if(isServerSpecified && !isPwdSpecified)
		{
			PTLogger.error(
					String.format("PTREPORTCL4 - Please specify database password."),
					true);
			return false;
		}
		else if(isUidSpecified && !isServerSpecified)
		{
			PTLogger.error(
					String.format("PTREPORTCL5 - You have specified uid for database, please specify server."),
					true);
			return false;
		}
		return true;
	}
	
	private static boolean setValidJobName(String jobName)
	{
		try
		{
			if(jobName == null || jobName.isEmpty())
			{
				PTLogger.error(
						String.format("PTREPORTCL6 - Job Name is not specified. Please specify."),
						true);
				return false;
			}
			JOB_NAME = jobName.trim();
			
			boolean match = Pattern.matches("[_a-zA-Z0-9\\-]+", jobName);  //job name allowed chars
			if(!match)
			{
				PTLogger.error(
						String.format("PTREPORTCL6 - Please only contain alphabetical letters, numbers, underscore or hyphen in the job name."),
						true);
				return false;
			}
		}
		catch(Exception ex)
		{
			PTLogger.error(
					String.format("PTREPORTCL6 - Job name error: %s", ex.getMessage()),
					true);
			return false;
		}
		
		return true;
	}

	
	private static boolean setValidReportFileType(String reportFileType)
	{
		try
		{
			if(reportFileType == null || reportFileType.isEmpty())
			{
				PTLogger.info(
						String.format("PTREPORTCL7 - ReportFileType is not set in job file. Setting to PDF by default."));
				REPORT_FILE_TYPE = ConfigConstants.PDF;
			}
			else
			{
				REPORT_FILE_TYPE = reportFileType.trim();
				if(!REPORT_FILE_TYPE.equalsIgnoreCase(ConfigConstants.PDF) && !reportFileType.equalsIgnoreCase(ConfigConstants.XLS)
						&& !REPORT_FILE_TYPE.equalsIgnoreCase(ConfigConstants.HTML) && !reportFileType.equalsIgnoreCase(ConfigConstants.DOCX))
				{
					PTLogger.error(
							String.format("PTREPORTCL7 - Invalid ReportFileType. Please specify from: %s, %s, %s, %s.", 
									ConfigConstants.PDF, ConfigConstants.XLS, ConfigConstants.HTML, ConfigConstants.DOCX));
					return false;
				}	
			}
		}
		catch(Exception ex)
		{
			PTLogger.error(
					String.format("PTREPORTCL7 - Reference file type error: %s", ex.getMessage()),
					true);
			return false;
		}
		
		return true;
	}
	
	private static boolean setValidReportFolderPath(String reportFolderPath)
	{
		try
		{
			if(reportFolderPath == null || reportFolderPath.isEmpty())
			{
				PTLogger.error(
						String.format("PTREPORTCL8 - ReportFolderPath is not specified. Please specify."));
				return false;
			}
			REPORT_FOLDER_PATH = reportFolderPath.trim();

			File f = new File(REPORT_FOLDER_PATH);
			if (!f.exists())
			{
				try
				{
					f.mkdir();	
				}
				catch (Exception e)
				{
					// if we cannot make the necessary report directory, then halt!
					PTLogger.error("PTREPORTCL8 - Cannot create report folder: " + ConfigLoader.REPORT_FOLDER_PATH);
					PTLogger.exception(e);
					return false;
				}
			}
			else if (!f.canRead())
			{
				PTLogger.error(
						String.format("PTREPORTCL8 - Can not read from Report folder %s. Please provide read permissions.", REPORT_FOLDER_PATH));
				return false;
			}
			else if (!f.canWrite())
			{
				PTLogger.error(
						String.format("PTREPORTCL8 - Can not write to Report folder %s. Please provide write permissions.", REPORT_FOLDER_PATH));
				return false;
			}
		}
		catch(Exception ex)
		{
			PTLogger.error(
					String.format("PTREPORTCL8 - Report folder path error (See log for details)"));
			PTLogger.exception(ex);
			return false;
		}
		
		return true;
	}
	
	private static boolean setValidJobId(String jobId)
	{
		try
		{
			if(jobId == null || jobId.isEmpty())
			{
				PTLogger.error(
						String.format("PTREPORTCL9 - Job ID is not specified. Please specify."),
						true);
				return false;
			}
			JOB_ID = jobId.trim();
		}
		catch(Exception ex)
		{
			PTLogger.error(
					String.format("PTREPORTCL9 - Job ID error: %s", ex.getMessage()),
					true);
			return false;
		}
		
		return true;
	}
	
	private static boolean setValidBeginDate(String beginDate)
	{
		try
		{
			if(beginDate == null || beginDate.isEmpty())
			{
				PTLogger.info(
						String.format("BeginDate is not specified. Reporting on entire history"));
				return false;
			}
			
			// date format = yyyy-MM-dd HH:mm:ss
			String beginDateStr = beginDate.trim();
			DateFormat beginDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			BEGIN_DATE = beginDateFormat.parse(beginDateStr);
		}
		catch(ParseException pex)
		{
			PTLogger.error(
					String.format("Invalid format for Begin Date: %s", pex.getMessage()));
			return false;
		}
		catch(Exception ex)
		{
			PTLogger.error(
					String.format("Begin Date error: %s", ex.getMessage()));
			return false;
		}
		
		return true;
	}

	
	private static boolean setValidServer(String server)
	{
		try
		{
			if(server == null || server.isEmpty())
			{
				PTLogger.info(
						String.format("Server is not specified."));
				return true;
			}
			
			SERVER = server.trim();
		}
		catch(Exception ex)
		{
			PTLogger.error(
					String.format("Set valid server error: %s", ex.getMessage()));
			return false;
		}
		return true;
	}
	
	private static boolean setValidPort(String port)
	{
		try
		{
			if(port == null || port.isEmpty())
			{
				PTLogger.info(
						String.format("Port is not specified."));
				return true;
			}
			
			PORT = port.trim();
		}
		catch(Exception ex)
		{
			PTLogger.error(
					String.format("Set valid port error: %s", ex.getMessage()));
			return false;
		}
		return true;
	}
	
	private static boolean setValidUid(String uid)
	{
		try
		{
			if(uid == null || uid.isEmpty())
			{
				PTLogger.info(
						String.format("Uid is not specified."));
				return true;
			}
			
			UID = uid.trim();
		}
		catch(Exception ex)
		{
			PTLogger.error(
					String.format("Set valid uid error: %s", ex.getMessage()));
			return false;
		}
		return true;
	}
	
	private static boolean setValidPwd(String pwd)
	{
		try
		{
			if(pwd == null || pwd.isEmpty())
			{
				PTLogger.info(
						String.format("Pwd is not specified."));
				return true;
			}
			
			PWD = pwd.trim();
		}
		catch(Exception ex)
		{
			PTLogger.error(
					String.format("Set valid pwd error: %s", ex.getMessage()));
			return false;
		}
		return true;
	}
}
