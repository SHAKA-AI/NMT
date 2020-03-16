package dal.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import utilities.PTLogger;
import dal.PersistenceManager;

/**
 * DAO design pattern.
 * 
 * Methods use JPQL to perform database operations.
 * @author jkane
 *
 */
public class ReportDAO 
{
	public ReportDAO()
	{
		PersistenceManager.getInstance();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> readFileNameList(String tableName, Date beginDate)
	{
		DateFormat beginDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String beginDateStr = beginDateFormat.format(beginDate);
		
		PersistenceManager.openTransaction();
		
		// TODO for now, use native query
		StringBuilder query = new StringBuilder("SELECT DISTINCT dto.[FileName] FROM ").append(tableName)
				.append(" dto WHERE dto.DateAdded >= '").append(beginDateStr).append("';"); 
		
		List<String> fileNameList = (List<String>) PersistenceManager.entityManager
				.createNativeQuery(query.toString()).getResultList();
		
		PersistenceManager.commitAndCloseTransaction();
		
		return fileNameList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> readPHIValueAndType(String fileName, String tableName)
	{
		PersistenceManager.openTransaction();
		
		// TODO for now, use native query
		StringBuilder query = new StringBuilder("SELECT dto.[OriginalValue], dto.[Type] FROM ").append(tableName)
				.append(" dto WHERE dto.[FileName] = '").append(fileName).append("';"); 
		
		List<Object[]> phiList = (List<Object[]>) PersistenceManager.entityManager
												.createNativeQuery(query.toString()).getResultList();
		
		PersistenceManager.commitAndCloseTransaction();
		
		return phiList;
	}
	
	public long readNumberProcessedFiles(String tableName, String jobId)
	{
		PersistenceManager.openTransaction();
		
		// TODO for now, use native query
		StringBuilder query = new StringBuilder("SELECT TOP 1 dto.[NumberProcessedFiles] FROM ").append(tableName)
				.append(" dto WHERE dto.[ID] = ").append(jobId).append(";");
		
		long numProcessed = 0;
		try {
			numProcessed = (long) PersistenceManager.entityManager
													.createNativeQuery(query.toString()).getSingleResult();
		} catch (Exception e) {
			PTLogger.warning("PTREPORTDB3: Problem with db transaction: " + e.getMessage());
			PTLogger.exception(e);
		}
		
		PersistenceManager.commitAndCloseTransaction();
		
		return numProcessed;
	}
	
	public long readNumberTotalFiles(String tableName, String jobId)
	{
		PersistenceManager.openTransaction();
		
		// TODO for now, use native query
		StringBuilder query = new StringBuilder("SELECT TOP 1 dto.[TotalNumberFiles] FROM ").append(tableName)
				.append(" dto WHERE dto.[ID] = ").append(jobId).append(";");
		
		long totalNum = 0;
		try {
			totalNum = (long) PersistenceManager.entityManager
													.createNativeQuery(query.toString()).getSingleResult();
		} catch (Exception e) {
			PTLogger.warning("PTREPORTDB4: Problem with db transaction: " + e.getMessage());
			PTLogger.exception(e);
		}
		
		PersistenceManager.commitAndCloseTransaction();
		
		return totalNum;
	}
	
	
	public Date readBeginDate(String tableName, String jobId)
	{
		PersistenceManager.openTransaction();
		
		// TODO for now, use native query
		StringBuilder query = new StringBuilder("SELECT TOP 1 dto.[StartTime] FROM ").append(tableName)
				.append(" dto WHERE dto.[ID] = ").append(jobId).append(";");
		
		Date beginDate = new Date(0);
		try {
			beginDate = (Date) PersistenceManager.entityManager
													.createNativeQuery(query.toString()).getSingleResult();
		} catch (Exception e) {
			PTLogger.warning("PTREPORTDB1: Problem with db transaction: " + e.getMessage());
			PTLogger.exception(e);
		}
		
		PersistenceManager.commitAndCloseTransaction();
		
		return beginDate;
	}
	
	public Date readEndDate(String tableName, String jobId)
	{
		PersistenceManager.openTransaction();
		
		// TODO for now, use native query
		StringBuilder query = new StringBuilder("SELECT TOP 1 dto.[TimeCompleted] FROM ").append(tableName)
				.append(" dto WHERE dto.[ID] = ").append(jobId).append(";");
		
		Date endDate = null;
		try {
			endDate = (Date) PersistenceManager.entityManager
													.createNativeQuery(query.toString()).getSingleResult();
		} catch (Exception e) {
			PTLogger.warning("PTREPORTDB0: Problem with db transaction: " + e.getMessage());
			PTLogger.exception(e);
		}
		
		PersistenceManager.commitAndCloseTransaction();
		
		if(endDate == null) endDate = new Date();
		
		return endDate;
	}
	
	public String readDeIdentProcess(String tableName, String fileName, Date beginDate)
	{
		DateFormat beginDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String beginDateStr = beginDateFormat.format(beginDate);
		
		PersistenceManager.openTransaction();
		
		// TODO for now, use native query
		StringBuilder query = new StringBuilder("SELECT DISTINCT dto.DeIdentProcess FROM ").append(tableName)
				.append(" dto WHERE dto.[FileName] = '").append(fileName)
				.append("' AND dto.DateAdded >= '").append(beginDateStr).append("';"); 
		
		String deIdentProcess = "";
		try {
			deIdentProcess = (String) PersistenceManager.entityManager
													.createNativeQuery(query.toString()).getSingleResult();
		} catch (Exception e) {
			PTLogger.warning("PTREPORTDB2: Problem with db transaction: " + e.getMessage());
			PTLogger.exception(e);
		}
		
		PersistenceManager.commitAndCloseTransaction();
		
		return deIdentProcess;
	}
}
