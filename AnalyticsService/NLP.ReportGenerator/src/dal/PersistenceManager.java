package dal;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.eclipse.persistence.config.TargetServer;

import exec.ConfigLoader;
import utilities.ConfigConstants;
import utilities.PTLogger;
import static org.eclipse.persistence.config.PersistenceUnitProperties.*;


/**
 * 
 * This class follows the Singleton design pattern
 * 
 * This class uses the Java SE javax.persistence bootstrapping API to maintain
 * database connections and transactions.
 * 
 * This is made possible using the EclipseLink framework.
 * 
 * @author jkane
 *
 */
public class PersistenceManager {

	private static PersistenceManager INSTANCE;
	
	protected static final String PERSISTENCE_UNIT_NAME = "reportDataPU";
	protected static EntityManagerFactory factory;
	
	public static EntityManager entityManager;
	
	private PersistenceManager(Map<String, String> properties)
	{
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
	}
	
	public static PersistenceManager getInstance()
	{
		if(INSTANCE == null)
		{
			String server = ConfigLoader.SERVER;	
			int index = server.indexOf("\\");
			String instance = "";
			if(index != -1)
			{
				instance = server.substring(index+1);
				server = server.substring(0, index);
			}
			
			Map<String, String> properties = new HashMap<String, String>();

			// Ensure RESOURCE_LOCAL transactions is used.
			properties.put(TRANSACTION_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL.name());

			// Configure the internal EclipseLink connection pool
			properties.put(JDBC_DRIVER, ConfigConstants.DRIVERCLASS);
			properties.put(JDBC_URL, getJDBCUrl(server, ConfigLoader.PORT, instance));
			properties.put(JDBC_USER, ConfigLoader.UID);
			properties.put(JDBC_PASSWORD, ConfigLoader.PWD);

			// Configure logging. FINE ensures all SQL is shown
			properties.put(LOGGING_LEVEL, "OFF");
			properties.put(LOGGING_TIMESTAMP, "false");
			properties.put(LOGGING_THREAD, "false");
			properties.put(LOGGING_SESSION, "false");

			// Ensure that no server-platform is configured
			properties.put(TARGET_SERVER, TargetServer.None);

			INSTANCE = new PersistenceManager(properties);
		}
		return INSTANCE;
	}
	
	public static void open()
	{
		entityManager = factory.createEntityManager();
	}
	
	public static void close()
	{
		entityManager.close();
	}
	
	public static void openTransaction()
	{
		open();
		entityManager.getTransaction().begin();
	}
	
	public static void commitAndCloseTransaction()
	{
		entityManager.getTransaction().commit();
		close();
	}
	
	/**
	 * TODO update so that server type and driver type are dynamic
	 * (this would mean that ConfigLoader would have to supply these details)
	 * @param server
	 * @param port
	 * @param instance
	 * @return
	 */
	private static String getJDBCUrl(String server, String port, String instance)
	{
		String url = "";
		StringBuilder sb = new StringBuilder();
		try
		{
			//<url>jdbc:jtds:<server_type>://<server>[:<port>][/<database>];instance=<instance_name></url>
			sb.append("jdbc:").append(ConfigConstants.JDBCDRIVER).append(":").append(ConfigConstants.SQLSERVER).append("://").append(server);
			if(port != null && !port.isEmpty())
				sb.append(":").append(port);
			sb.append("/").append(ConfigConstants.DATABASE);
			if(instance != null && !instance.isEmpty())
				sb.append(";instance=").append(instance);
			url= sb.toString();
		}
		catch(Exception e)
		{
			PTLogger.error(
					String.format("Cannot connect to database: %s", e.getMessage()),
					true);	
		}
	
		return url;
	}
		
}
