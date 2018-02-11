package com.marshal.bizdirector.interfaces;

import java.util.Map;

public interface IBizDirectorWorkflow {

	public String f_UpdateDB(String strTableName, String strFields, String strFieldValues, String	strWhere, String strDdlOperation) throws Exception;
	public String f_ExecProcess(String as_ProcessDefId, Map<String, Object> am_vars) throws Exception;
	public String f_TerminateProcess(String as_ProcessId) throws Exception;
}
