package utilities;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class PTLogger
{
	static Logger logger; 
	static Logger displayLogger  = Logger.getLogger("displayLogger");
	
	static boolean defaultWriteToConsole = true;

	// Suppress default constructor for noninstantiability
	private PTLogger()
	{
		throw new AssertionError();
	}
	
	public static void setClassName(String classname)
	{
		try{
			logger = Logger.getLogger(classname);
			PropertyConfigurator.configure("./reportGenConfig/log4j.properties");
		}catch(Exception ex){
			logger = null;
		}
	}
	
	/**
	 * Only ERROR / exceptions displayed in the log file
	 * (INFO only displayed to the console)
	 * @param message
	 */
	public static void info(String message)
	{				
		if(displayLogger != null)
			displayLogger.info(message);	
	}
	
	/**
	 * Only ERROR / INFO is displayed to the console
	 * (Exceptions only displayed in log file)
	 * @param e
	 */
	public static void exception(Exception e)
	{		
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		
		String message = String.format("ParatText EXCEPTION: %s", sw.toString());
		
		if(logger != null)
			logger.error(message);	
	}
	
	private static void logMessage(String message, boolean writeToConsole)
	{
		if(writeToConsole)
		{
			if(displayLogger != null)
				displayLogger.error(message);	
		}
		
		if(logger != null)
			logger.error(message);	
	}
	
	public static void error(String message, boolean writeToConsole)
	{		
		message = String.format("Parat Text Error: %s", message);
		logMessage(message, writeToConsole);
	}
	
	public static void error(String message)
	{
		error(message, defaultWriteToConsole);
	}

	public static void warning(String message,boolean writeToConsole)
	{		
		message = String.format("Parat Text Warning: %s", message);
		logMessage(message, writeToConsole);
	}
	
	public static void warning(String message)
	{
		warning(message, defaultWriteToConsole);
	}
}
