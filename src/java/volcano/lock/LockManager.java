package volcano.lock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;

public class LockManager {
	private JdbcAgency jdbc = new JdbcAgency();
	
	public LockManager() throws FoundationException{
		super();
	}
	
	public List retrieveLocks(String semantic) throws FoundationException{
		java.util.Map<String, Object> map = new HashMap<String, Object>();
		map.put("did", semantic);
		map.put("released", "N");
		return jdbc.executeQueryList("lock", "retrieve.locks", map);
	}
	
	public Map getLock(String semantic, String uid, String rid, String user, String description, int version) throws FoundationException{
		java.util.Map<String, Object> map = new HashMap<String, Object>();
		map.put("did", semantic);
		map.put("uid", uid);
		map.put("rid", rid);
		map.put("user", user);
		map.put("description", description);
		map.put("version", version);
		Map result = null;
		TransactionManager.getInstance().transact();
		try{
			result = jdbc.executeQuery("lock", "validate.lock", map);
			if(result == null){
				jdbc.executeUpdate("lock","insert.lock", map);
			}
			TransactionManager.getInstance().accept();
		}catch(Exception e){
			TransactionManager.getInstance().restore();
		}finally{
			TransactionManager.getInstance().removeSession();
		}
		return result;
	}
	
	public void releaseRequest(String semantic, String rid, String user, String description, int version) throws FoundationException{
		java.util.Map<String, Object> map = new HashMap<String, Object>();
		map.put("did", semantic);
		map.put("rid", rid);
		map.put("user", user);
		map.put("description", description);
		map.put("released", "N");
		map.put("version", version);
		Map result = null;
		TransactionManager.getInstance().transact();
		try{
			result = jdbc.executeQuery("lock", "retrieve.lock", map);
			if(result != null){
				map.put("uid", result.get("uid"));
				jdbc.executeUpdate("lock","release.request.lock", map);
			}
			TransactionManager.getInstance().accept();
		}catch(Exception e){
			TransactionManager.getInstance().restore();
		}finally{
			TransactionManager.getInstance().removeSession();
		}
	}
	
	public List keepLock(String semantic, String uid, String rid) throws FoundationException{
		java.util.Map<String, Object> map = new HashMap<String, Object>();
		map.put("did", semantic);
		map.put("rid", rid);
		map.put("uid", uid);
		
		List result = null;
		TransactionManager.getInstance().transact();
		try{
			jdbc.executeUpdate("lock","keep.lock", map);
			result = jdbc.executeQueryList("lock", "retrieve.lock.release.reqeust", map);
			jdbc.executeUpdate("lock","delete.lock.release.reqeust", map);
			TransactionManager.getInstance().accept();
		}catch(Exception e){
			TransactionManager.getInstance().restore();
		}finally{
			TransactionManager.getInstance().removeSession();
		}
		return result;
	}
	
	public boolean isReleased(String semantic, String rid, int version) throws FoundationException{
		java.util.Map<String, Object> map = new HashMap<String, Object>();
		map.put("did", semantic);
		map.put("rid", rid);
		map.put("version", version);
		return jdbc.executeQuery("lock", "retrieve.lock", map) == null;
	}
	
	public void release(String semantic, String uid, String rid, boolean commited) throws FoundationException{
		java.util.Map<String, Object> map = new HashMap<String, Object>();
		map.put("did", semantic);
		map.put("rid", rid);
		map.put("uid", uid);
		if(commited == true)
			jdbc.executeUpdate("lock","increase.ver.release.lock", map);
		else
			jdbc.executeUpdate("lock","release.lock", map);
	}
}
