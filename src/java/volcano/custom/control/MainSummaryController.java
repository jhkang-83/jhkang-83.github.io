package volcano.custom.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;

public class MainSummaryController {
	
	protected JdbcAgency jdbc = null;
	
	public List<java.util.Map> getSummaryReq() throws FoundationException {
		List<java.util.Map> result = null;
		TransactionManager.getInstance().transact();
		try {
			jdbc = new JdbcAgency();
			result = jdbc.executeQueryList("skb/common", "select.summary.reqestimate", null);
			TransactionManager.getInstance().accept();
		}catch(Exception e) {
			TransactionManager.getInstance().restore();
		}finally {
			TransactionManager.getInstance().removeSession();
		}
		return result;
	}
	
	public List<java.util.Map> getSummaryCoopOfferEsti() throws FoundationException {
		List<java.util.Map> result = null;
		TransactionManager.getInstance().transact();
		try {
			jdbc = new JdbcAgency();
			result = jdbc.executeQueryList("skb/common", "select.summary.coopofferestimate", null);
			TransactionManager.getInstance().accept();
		}catch(Exception e) {
			TransactionManager.getInstance().restore();
		}finally {
			TransactionManager.getInstance().removeSession();
		}
		return result;
	}
	
	public List<java.util.Map> getSummaryBidding() throws FoundationException {
		List<java.util.Map> result = null;
		TransactionManager.getInstance().transact();
		try {
			jdbc = new JdbcAgency();
			result = jdbc.executeQueryList("skb/common", "select.summary.bidding", null);
			TransactionManager.getInstance().accept();
		}catch(Exception e) {
			TransactionManager.getInstance().restore();
		}finally {
			TransactionManager.getInstance().removeSession();
		}
		return result;
	}
	
	public List<java.util.Map> getSummaryNotice() throws FoundationException {
		List<java.util.Map> result = null;
		TransactionManager.getInstance().transact();
		try {
			jdbc = new JdbcAgency();
			result = jdbc.executeQueryList("skb/common", "select.summary.notice", null);
			TransactionManager.getInstance().accept();
		}catch(Exception e) {
			TransactionManager.getInstance().restore();
		}finally {
			TransactionManager.getInstance().removeSession();
		}
		return result;
	}
	
	public List<java.util.Map> getSummaryCowork() throws FoundationException {
		List<java.util.Map> result = null;
		TransactionManager.getInstance().transact();
		try {
			jdbc = new JdbcAgency();
			result = jdbc.executeQueryList("skb/common", "select.summary.cowork", null);
			TransactionManager.getInstance().accept();
		}catch(Exception e) {
			TransactionManager.getInstance().restore();
		}finally {
			TransactionManager.getInstance().removeSession();
		}
		return result;
	}
	

}
