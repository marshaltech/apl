package com.marshal.bizdirector.util;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;

import com.marshal.bizdirector.constants.WFConstants;
import com.marshal.bizdirector.interfaces.IBizDirectorWorkflow;
import com.marshal.bizdirector.model.UserInfo;
import com.shm.persistence.shmConnectionsManager;
import com.shm.session.shmSessionManager;
import com.shm.utils.shmUtil;

/**
 * 
 * @author MSarwar
 *
 */
public class BizDirectorAPLHelper implements IBizDirectorWorkflow {

	private static Logger logger	= Logger.getLogger(BizDirectorAPLHelper.class.getName());	
	
	/*UserInfo userInfo;
	
	public BizDirectorAPLHelper(UserInfo userInfo){
		this.userInfo = userInfo;
		client = new JerseyClient(ConfigurationLoader.getPropertyValue("activitiserver", "http://localhost")
						+":"+ConfigurationLoader.getPropertyValue("activitiport", "8181"),userInfo);
		logger.log(Level.CONFIG, client.getBASE_URI());
	}*/
	
	/**
	 * Update the SHMA esuite database after initiating action from BizDirector
	 *
	 * URL = http://{server}:{prot}/WorkflowService/update/table/{table}?.....
	 *
	 * @path param {table} table name to be updated
	 * @param fields i.e comma seperated values of table fields to be updated
	 * @param values  comma seperated values of user inputs to be updated against each respective filed
	 * @param where i.e where clasue of query
	 * @param operation i.e type of ddl operation supported are INSERT, UPDATE and DELETE 
	 *
	 * @return XML String
	 */
	@Override
	public String f_UpdateDB(String strTable, String strFields, String strFieldValues, String	strWhere, String strDdlOperation) throws Exception {
		String strSQLError = "";
		shmConnectionsManager manager = shmConnectionsManager.getInstance();
		Connection conn = null;
		try {
			conn = manager.get();
			conn.setAutoCommit(false);
		
			String arrFields[] = strFields.split(",");
			String arrFieldValues[] = strFieldValues.split(",");
			if(strTable != null && !strTable.equals("") &&  strDdlOperation != null) {
				if(strDdlOperation.equalsIgnoreCase(WFConstants.DB_OPERATIONS.DELETE.toString())) {
					strSQLError = shmUtil.updateDatabase(conn, strDdlOperation, strTable, null, null, strWhere);
				}
				else if(strDdlOperation.equalsIgnoreCase(WFConstants.DB_OPERATIONS.INSERT.toString()) && 
						arrFields.length > 0 && arrFieldValues.length > 0 && arrFields.length == arrFieldValues.length) {
					strSQLError = shmUtil.updateDatabase(conn, strDdlOperation, strTable, arrFields, arrFieldValues, null);	
				}
				else if(strDdlOperation.equalsIgnoreCase(WFConstants.DB_OPERATIONS.UPDATE.toString()) && 
						arrFields.length > 0 && arrFieldValues.length > 0 && arrFields.length == arrFieldValues.length) {
					strSQLError = shmUtil.updateDatabase(conn, strDdlOperation, strTable, arrFields, arrFieldValues, strWhere);	
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			strSQLError =  "<error><exception>Start Process Service : " + e.toString() + "</exception></error>";
		}
		finally {
			try {
				if (strSQLError.equals("")) conn.commit();
				else conn.rollback();
				conn.setAutoCommit(true);
			}
			catch (SQLException e) {}
			manager.free(conn);
		}
		return strSQLError;
	}
	@Override
	//Function to start a process against a specified process definition
	public String f_ExecProcess(String as_ProcessDefId,Map<String, Object> am_vars) throws Exception {

		/*ProcessEngineConfiguration procConfEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromInputStream(
				this.getClass().getResourceAsStream("/com/marshal/bizdirector/conf/activiti-cfg.xml"));*/
		ProcessEngineConfiguration procEngineConf = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		procEngineConf.setJdbcDriver(shmSessionManager.getInstance().getJDBCDriver());
		procEngineConf.setJdbcUrl(shmSessionManager.getInstance().getJDBCURL());
		procEngineConf.setJdbcUsername(shmSessionManager.getInstance().getDBUserName());
		procEngineConf.setJdbcPassword(shmSessionManager.getInstance().getDBPassword());
		procEngineConf.setJdbcMaxActiveConnections(500);
		ProcessEngine proceEngine = procEngineConf.buildProcessEngine();
		RuntimeService rtService = proceEngine.getRuntimeService();
		ProcessInstance procInstance = rtService.startProcessInstanceByKey(as_ProcessDefId, am_vars);
		return procInstance.getId();

	}
	@Override
	//@ TODO
	//Web Service  implementation to pabort the specified process
	public String f_TerminateProcess(String as_ProcessId) throws Exception{
		return "";
	}

	
	
	public static void main(String args[]){
		
		BizDirectorAPLHelper wfAPIHelper  = new BizDirectorAPLHelper();
		HashMap<String, Object> variables = new HashMap<String,Object>();
					
		variables.put("VEH_REG_NO", "111");
		variables.put("VEH_REG_DATE", "21-11-2013");
		variables.put("VEH_OWNER_NAME", "M. Sarwar");
		variables.put("VEH_DRIVER_NAME", "M. XYZ DRV");
		variables.put("VEH_DRIVER_ID", "DRV_ID");
		variables.put("VEH_DRIVER_NIC", "DRV_CNIC");
		variables.put("VEH_CHASIS_NO", "CHASIS_NO");
		variables.put("VEH_COMMENTS", "INIT_COMMENTS");
		variables.put("VEH_VERIFY_COMMENTS", "");
		variables.put("VEH_UNVERIFY_COMMENTS", "");
		
		
		try {
			String procInstId = wfAPIHelper.f_ExecProcess("VehicleBlackListingAPL", variables);
			System.out.println("Process Instance Id for vehicle black listing is >> " + procInstId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
