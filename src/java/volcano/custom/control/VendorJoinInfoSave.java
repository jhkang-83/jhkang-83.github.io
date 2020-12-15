package volcano.custom.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import emro.util.JsonUtil;
import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;
import eswf.util.ConfigUtils;

/**
 * Servlet implementation class VendorJoinInfoSave
 */
@WebServlet("/sp/vendorJoinInfoSave")
public class VendorJoinInfoSave extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VendorJoinInfoSave() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html charset=UTF-8");
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> resultVendorRegYN = null;
		Map<String, Object> resultSeq = null;
		String status= "";
		String errMsg = "";
		JSONObject resultJson = new JSONObject();
		
		TransactionManager.getInstance().transact();
		try {
			JdbcAgency jdbc = new JdbcAgency();
			jdbc.setDefaults(ConfigUtils.getVirtualSessionUser());
			
			JSONObject objJson = new JSONObject(request.getReader().readLine());
			param = JsonUtil.getMapFromJsonObject(objJson);
			param.put("sys_id", "EMRO");
			param.put("s_comp_cd", "SKB");
			param.put("comp_cd", "SKB");
			param.put("reg_id", param.get("charger_id"));
			param.put("usr_id", param.get("charger_id"));
			
			System.out.println("vendorJoin objJson >>> " + objJson.toString());
			
			resultVendorRegYN = jdbc.executeQuery("esourcing/vendor", "get.vd_sn.by.biz_reg_no", param);
			if(resultVendorRegYN != null) {
				param.put("rep_chr_yn", "N");
			}else {
				param.put("rep_chr_yn", "Y");
				jdbc.executeUpdate("esourcing/vendor", "insert.tb_vendor", param);
			}
			
			jdbc.executeUpdate("esourcing/vendor", "vendor.user.insert", param);
			jdbc.executeUpdate("esourcing/vendor", "vendor.user.role.insert", param);
			jdbc.executeUpdate("esourcing/vendor", "vendor.system.insert", param);
			
			resultSeq = jdbc.executeQuery("esourcing/vendor", "get.esmvdcp.chr.sn", null);
			param.put("chr_sn", resultSeq.get("seq"));
			jdbc.executeUpdate("esourcing/vendor", "charger.list.insert", param);
			
			String vendor_ip = request.getRemoteAddr().toString();
			param.put("vendor_term_no", "1");
			param.put("vendor_term_title", objJson.getString("vendor_term_title1"));
			param.put("vendor_ip", vendor_ip);
			jdbc.executeUpdate("esourcing/vendor", "insert.new.vendor.agreement", param);
			
			param.put("vendor_term_no", "2");
			param.put("vendor_term_title", objJson.getString("vendor_term_title2"));
			param.put("vendor_ip", vendor_ip);
			jdbc.executeUpdate("esourcing/vendor", "insert.new.vendor.agreement", param);
			
			if(objJson.getString("vd_cls").equals("A")) {
				
				param.put("vendor_term_no", "3");
				param.put("vendor_term_title", objJson.getString("vendor_term_title3"));
				param.put("vendor_ip", vendor_ip);
				jdbc.executeUpdate("esourcing/vendor", "insert.new.vendor.agreement", param);
				
				param.put("vendor_term_no", "4");
				param.put("vendor_term_title", objJson.getString("vendor_term_title4"));
				param.put("vendor_ip", vendor_ip);
				jdbc.executeUpdate("esourcing/vendor", "insert.new.vendor.agreement", param);
				
			}else if(objJson.getString("vd_cls").equals("B")) {
				param.put("vendor_term_no", "5");
				param.put("vendor_term_title", objJson.getString("vendor_term_title3"));
				param.put("vendor_ip", vendor_ip);
				jdbc.executeUpdate("esourcing/vendor", "insert.new.vendor.agreement", param);
				
				param.put("vendor_term_no", "6");
				param.put("vendor_term_title", objJson.getString("vendor_term_title4"));
				param.put("vendor_ip", vendor_ip);
				jdbc.executeUpdate("esourcing/vendor", "insert.new.vendor.agreement", param);
				
			}
			
			status = "INSERT";
			resultJson.put("status", status);
			resultJson.put("result", "S");
			
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write(resultJson.toString());
			
			TransactionManager.getInstance().accept();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("vendorJoinInfo error ====");
			status = "FAIL";
			errMsg = "저장시 에러가 발생 하였습니다.";
			
			e.printStackTrace();
			try {
				resultJson.put("status", status);
				resultJson.put("result", "F");
				resultJson.put("errMsg", errMsg);
				
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter writer = response.getWriter();
				writer.write(resultJson.toString());
				TransactionManager.getInstance().restore();
			} catch (FoundationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e2) {
				// TODO: handle exception
			}
		}finally {
			TransactionManager.getInstance().removeSession();
		}
		
	}

}
