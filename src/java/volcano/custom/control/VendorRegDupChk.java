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

import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;
import eswf.util.ConfigUtils;


/**
 * Servlet implementation class VendorRegDupChk
 * 회원가입 화면
 */
@WebServlet("/sp/selectVndRegDupChk")
public class VendorRegDupChk extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected JSONObject json = new JSONObject();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VendorRegDupChk() {
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
		try {
			json = new JSONObject(request.getReader().readLine());

			if(json.getString("chk_mode").equals("bizRegNo")) {
				chkBizRegNo(request, response);
			}else if(json.getString("chk_mode").equals("ID")) {
				chkId(request, response);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	protected void chkBizRegNo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> resultMap = null;

		String status= "";
		String errMsg = "";
		String member_cnt = "";

		TransactionManager.getInstance().transact();
		response.setContentType("text/html; charset=UTF-8");

		try {
			JdbcAgency jdbc = new JdbcAgency();
			jdbc.setDefaults(ConfigUtils.getVirtualSessionUser());


			if(json != null) {
				param.put("biz_reg_no", json.getString("biz_reg_no"));
				param.put("comp_cd", json.getString("comp_cd"));
			}

			resultMap = jdbc.executeQuery("esourcing/vendor", "select.vendor.member.filtering", param);

			if(resultMap != null) {
				member_cnt = (String) resultMap.get("member_cnt");
			}

			status = "SUCC";
			for(Map.Entry<String, Object> entry:resultMap.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				json.put(key, value);
			}
			json.put("status", status);
			json.put("member_cnt", member_cnt);

			System.out.println("json >> " + json.toString());
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write(json.toString());
			writer.flush();
			TransactionManager.getInstance().accept();


		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			try {
				TransactionManager.getInstance().restore();
			} catch (FoundationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally {
			TransactionManager.getInstance().removeSession();
		}

	}

	protected void chkId(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> resultMap = null;
		Map<String, Object> user = null;

		String status= "";
		String errMsg = "";
		String dupid_yn = "";

		TransactionManager.getInstance().transact();
		response.setContentType("text/html; charset=UTF-8");

		try {
			JdbcAgency jdbc = new JdbcAgency();
			jdbc.setDefaults(ConfigUtils.getVirtualSessionUser());

			param.put("sys_id", "EMRO");
			param.put("comp_cd", "SKB");
			param.put("s_comp_cd", "SKB");

			if(json != null) {
				param.put("usr_id", json.getString("charger_id"));
			}

			resultMap = jdbc.executeQuery("esourcing/vendor", "select.dupId.yn", param);

			status = "SUCC";
			if(resultMap != null) {
				dupid_yn = (String) resultMap.get("dupid_yn");
			}

			json.put("dupid_yn", dupid_yn);
			json.put("status", status);
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write(json.toString());
			writer.flush();
			TransactionManager.getInstance().accept();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			try {
				TransactionManager.getInstance().restore();
			} catch (FoundationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}finally {
			TransactionManager.getInstance().removeSession();
		}
	}

}
