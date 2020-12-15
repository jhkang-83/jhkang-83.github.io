package volcano.custom.control;

import java.util.HashMap;
import java.util.Random;

import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;


public class UserController {
	private int pwdLength = 10;
	private final char[] passwordTable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 
								            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
								            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
								            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
								            'w', 'x', 'y', 'z', '!', '@', '#', '$', '%', '^', '&', '*',
								            '(', ')', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
	
	protected JdbcAgency jdbc = null;
	
	public static String chr_email = "";
	public static String send_email = "";
	
	public UserController() throws FoundationException
	{
		
	}
	
	public String randomPwd(){
		Random random = new Random(System.currentTimeMillis());
		int tablelength = passwordTable.length;
		StringBuffer buf = new StringBuffer();
		
		for(int i = 0; i < pwdLength; i++) {
            buf.append(passwordTable[random.nextInt(tablelength)]);
        }
        
        return buf.toString();
	}
	
	public int getPwdLength() {
        return pwdLength;
    }

    public void setPwdLength(int pwdLength) {
        this.pwdLength = pwdLength;
    }
    
    public boolean loadUserInfo(String usr_id, String biz_reg_no, String usr_name) throws FoundationException
    {
    	java.util.Map<String, Object> map;
    	java.util.Map<String, Object> param = new HashMap<String, Object>();
    	
    	param.put("usr_id", usr_id);
		param.put("vd_sn", biz_reg_no);
		param.put("usr_nm", usr_name);
    	
		jdbc = new JdbcAgency();
		map = jdbc.executeQuery("admin/user", "select.user.findUser", param);
		
		if(map == null)
		{
			return false;
		}
		else
		{
			chr_email = (String)map.get("usr_email");
			send_email = (String)map.get("send_email");
			return true;
		}
    }
    
    public String savePw(String shaPw, String usr_id, String biz_reg_no, String usr_name) throws Exception
    {
    	java.util.Map<String, Object> param = new HashMap<String, Object>();
    	
    	param.put("pw", shaPw);
		param.put("usr_id", usr_id);
		param.put("vd_sn", biz_reg_no);
		param.put("usr_nm", usr_name);
		
		TransactionManager.getInstance().transact();
		
		try {
			jdbc = new JdbcAgency();
			jdbc.executeUpdate("admin/user", "update.user.tempPw", param);
			TransactionManager.getInstance().accept();
			return "sucess";
			
		} catch (Exception e) {
			TransactionManager.getInstance().restore();
			return "error";
			// TODO: handle exception
		}finally{
			TransactionManager.getInstance().removeSession();
		}
		
		
    }

}
