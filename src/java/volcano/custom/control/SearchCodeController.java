package volcano.custom.control;

import java.util.List;
import java.util.Map;

import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;

public class SearchCodeController {
	
	protected JdbcAgency jdbc = null;
	
	public List<Map> getCode(String queryId, String sqlId, String key, String codeName) throws FoundationException {
		List<Map> resultCode = null;
		Map<String, Object> codeSearchSo = new eswf.dataobject.Map();
		
		codeSearchSo.put(key, codeName);
		
		TransactionManager.getInstance().transact();
		
		try {
			jdbc = new JdbcAgency();
			resultCode = jdbc.executeQueryList(queryId, sqlId, codeSearchSo);
			
			TransactionManager.getInstance().accept();
		} catch (Exception e) {
			// TODO: handle exception
			TransactionManager.getInstance().restore();
		}finally {
			TransactionManager.getInstance().removeSession();
		}
		return resultCode;
	}
	
	public List<Map> getStorageCode(String queryId, String sqlId, Map<String, Object> param) throws FoundationException {
		List<Map> resultCode = null;
		
		TransactionManager.getInstance().transact();
		
		try {
			jdbc = new JdbcAgency();
			resultCode = jdbc.executeQueryList(queryId, sqlId, param);
			
			TransactionManager.getInstance().accept();
		} catch (Exception e) {
			// TODO: handle exception
			TransactionManager.getInstance().restore();
		}finally {
			TransactionManager.getInstance().removeSession();
		}
		
		return resultCode;
	}

}
