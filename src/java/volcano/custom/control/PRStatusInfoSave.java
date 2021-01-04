package volcano.custom.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import emro.util.JsonUtil;
import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;
import eswf.util.ConfigUtils;

/**
 * Servlet implementation class PRStatusInfoSave
 * 선제적구매지원현황 저장
 */
@WebServlet("/bw/prStatusInfoSave")
public class PRStatusInfoSave extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PRStatusInfoSave() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html charset=UTF-8");
		Map<String, Object> param =  param = new HashMap<String, Object>();
		Map<String, Object> resultSeq = null;

		JSONObject resultJson = new JSONObject();
		String status= "";
		String errMsg = "";
		String message = "";
		String userId = "";

		TransactionManager.getInstance().transact();

		try {
			JSONArray arrJson = new JSONArray(request.getReader().readLine());
			JdbcAgency jdbc = new JdbcAgency();
			jdbc.setDefaults(ConfigUtils.getVirtualSessionUser());

			Helper helper = new Helper(request, new eswf.dataobject.Map());
			if(helper != null) {
				userId = (String) helper.getUser().get("usr_id");
			}

			if(arrJson != null) {
				for(int i = 0; i < arrJson.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					Map<String, Object> seq = null;
					JSONObject jsonObj = (JSONObject) arrJson.get(i);

					Iterator<String> keys = jsonObj.keys();
					seq = jdbc.executeQuery("skb/prstatus", "select.prstatus.idx", null);

					while (keys.hasNext()) {
						String key = (String) keys.next();
						String value = jsonObj.getString(key);
						map.put("seq", seq.get("seq"));
						map.put(key, value);

						if(jsonObj.getString("pr_status_cd").equals("PR_START")) {
							map.put("pr_status", "1.구매업무 착수");
						}else if(jsonObj.getString("pr_status_cd").equals("EP_SELECT")) {
							map.put("pr_status", "2-1.업체선정 중");
						}else if(jsonObj.getString("pr_status_cd").equals("PR_REVEW")) {
							map.put("pr_status", "2-2.가격검토 중");
						}else if(jsonObj.getString("pr_status_cd").equals("BIDDING_PROG")) {
							map.put("pr_status", "2-3.입찰진행 중");
						}else if(jsonObj.getString("pr_status_cd").equals("EP_PR_END")) {
							map.put("pr_status", "3.업체선정 및 가격검토 완료");
						}else if(jsonObj.getString("pr_status_cd").equals("PR_CONFER")) {
							map.put("pr_status", "4.구매품의 중");
						}else if(jsonObj.getString("pr_status_cd").equals("CONT_END")) {
							map.put("pr_status", "5.계약완료");
						}else if(jsonObj.getString("pr_status_cd").equals("DEL_PR")) {
							map.put("pr_status", "6.구매계약 아님");
						}

						if(userId != null) {
							map.put("create_user", userId);
						}
					}

					jdbc.executeUpdate("skb/prstatus", "insert.excel.valid.item", map);
				}
			}
			
			jdbc.executeUpdate("skb/prstatus", "delete.temp.excel.item", null);

			status = "SUCC";
			message = "저장 되었습니다.";
			resultJson.put("status", status);
			resultJson.put("message", message);

			response.setContentType("application/json; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write(resultJson.toString());
			writer.flush();
			TransactionManager.getInstance().accept();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			try {
				status = "FAIL";
				errMsg = "저장 시 에러가 발생 하였습니다.";
				resultJson.put("status", status);
				resultJson.put("errMsg", errMsg);
				TransactionManager.getInstance().restore();
			} catch (FoundationException | JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally {
			TransactionManager.getInstance().removeSession();
		}

	}

}
