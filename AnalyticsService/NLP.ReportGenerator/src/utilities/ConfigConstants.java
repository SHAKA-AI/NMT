package utilities;

import java.util.Arrays;
import java.util.List;

public final class ConfigConstants 
{
	public final static String SQLSERVER = "sqlserver";
	public final static String JDBCDRIVER = "jtds";
	public final static String DRIVERCLASS = "net.sourceforge.jtds.jdbc.Driver";
	public final static String DATABASE = "ParatText";
	
	public final static String JOB_DETAILS_TABLE = "JobDetails";
	
	public final static String YES = "y";
	public final static String NO = "n";
	
	public final static String PDF = "PDF";
	public final static String XLS = "XLS";
	public final static String HTML = "HTML";
	public final static String DOCX = "DOCX";
	
	 // PHI tokens
	public enum PHITokenEnum {
		FIRST_NAME("First Name"),
		MIDDLE_NAME("Middle Name"),
		LAST_NAME("Last Name"),
		EMAIL_ADDRESS("Email Address"),
		STREET("Street"),
		CITY("City"),
		STATE("State"),
		COUNTRY("Country"),
		ZIPCODE("Zip Code"),
		PHONENUMBER("Phone Number"),
		DATE("Date"),
		ORGANIZATION("Organization"),
		IPADDRESS("IP Address"),
		URL("URL"),
		POBOX("PO Box"),
		AGE("Age"),
		ID("ID");
		
		private final String label;
		PHITokenEnum(String label)
		{
			this.label = label;
		}
		
		public static boolean contains(String token)
		{
			try {
				List<PHITokenEnum> tokenList = Arrays.asList(values());
				return (tokenList.contains(valueOf(token)));
			} catch (Exception e) {
				return false;
			}
		}
		
		@Override
		public String toString()
		{
			return this.label;
		}
	}
}
