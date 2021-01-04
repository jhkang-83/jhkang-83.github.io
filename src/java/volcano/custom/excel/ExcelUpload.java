package volcano.custom.excel;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import emro.util.JsonUtil;
import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;
import volcano.custom.control.SearchCodeController;

/**
 * Servlet implementation class ExcelUpload
 * 선제적구매지원현황 엑셀 업로드
 */
@WebServlet("/bw/excelUpload")
public class ExcelUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected SearchCodeController searchCode = new SearchCodeController();
	protected List<Map> prStatusDetailCD = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExcelUpload() {
        super();
        // TODO Auto-generated constructor stub

        try {
			prStatusDetailCD = searchCode.getCode("skb/prstatus", "select.prstatus.code", "grp_cd", "PR_STATUS");
		} catch (FoundationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html charset=UTF-8");
		List<Map> resultList = null;
		String status = "";
		TransactionManager.getInstance().transact();
		try {
			JSONArray arrJson = new JSONArray(request.getReader().readLine());
			net.minidev.json.JSONArray excelDataJson = null;
			net.minidev.json.JSONArray prStatusCdJson = null;
			net.minidev.json.JSONObject resultJson = new net.minidev.json.JSONObject();
			JdbcAgency jdbc = new JdbcAgency();

			if(arrJson != null) {
				for(int i = 0; i < arrJson.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					JSONObject jsonObj = (JSONObject) arrJson.get(i);
					Iterator<String> keys = jsonObj.keys();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						String value = jsonObj.getString(key);
						map.put(key, value);
						map.put("seq", i+1);
					}
					jdbc.executeUpdate("skb/prstatus", "insert.temp.excel.item", map);

				}

				resultList = jdbc.executeQueryList("skb/prstatus", "select.temp.excel.item", null);
				excelDataJson = JsonUtil.getJsonArrayFromList(resultList);

				resultJson.put("excelData", excelDataJson);
			}

			prStatusCdJson = JsonUtil.getJsonArrayFromList(prStatusDetailCD);
			status = "SUCC";

			resultJson.put("prStatusCD", prStatusCdJson);
			resultJson.put("status", status);

			response.setContentType("application/json; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write(resultJson.toJSONString());
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
