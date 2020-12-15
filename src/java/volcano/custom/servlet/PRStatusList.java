package volcano.custom.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import emro.util.StringUtil;
import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;
import eswf.util.ConfigUtils;
import volcano.custom.control.Helper;
import volcano.custom.control.SearchCodeController;

/**
 * Servlet implementation class PRStatusList
 */
@WebServlet("/bw/emPRStatusList")
public class PRStatusList extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected SearchCodeController searchCode = new SearchCodeController();
	protected List<Map> prStatusCode = null;
	protected List<Map> prStatusDetailCD = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PRStatusList() {
        super();
        // TODO Auto-generated constructor stub
        
        try {
			prStatusCode = searchCode.getCode("skb/common", "select.code", "grp_cd", "PR_STATUS");
			prStatusDetailCD = searchCode.getCode("skb/prstatus", "select.prstatus.code", "grp_cd", "PR_STATUS");
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html charset=UTF-8");
		
		Map<String, Object> param = new eswf.dataobject.Map();
		String userId = "";
		Map<String, Object> userInfo = null;
		List<Map> listMap = null;
		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		
		int begin = 1;
		int end = 15;
		int currentPage = 1;
		String selectedPRStatusCD = "";
		String mode = "";
		String changeWriteMode = "view";
		
		TransactionManager.getInstance().transact();
		
		try {
			JdbcAgency jdbc = new JdbcAgency();
			jdbc.setDefaults(ConfigUtils.getVirtualSessionUser());
			
			Helper helper = new Helper(request, new eswf.dataobject.Map());
			if(helper != null) {
				userId = (String) helper.getUser().get("usr_id");
				param.put("usr_id", userId);
			}
			
			userInfo = jdbc.executeQuery("skb/common", "select.hr_user.info", param);
			
			if(request.getParameter("approval_title") != null) {
				param.put("approval_title", request.getParameter("approval_title"));
			}
			if(request.getParameter("charge_name") != null) {
				param.put("charge_name", request.getParameter("charge_name"));
			}
			if(request.getParameter("purc_grp_nm") != null) {
				param.put("purc_grp_nm", request.getParameter("purc_grp_nm"));
			}
			if(request.getParameter("doc_no") != null) {
				param.put("doc_no", request.getParameter("doc_no"));
			}
			if(request.getParameter("prsts_gb") != null) {
				param.put("prsts_gb", request.getParameter("prsts_gb"));
				selectedPRStatusCD = request.getParameter("prsts_gb");
			}
			
			System.out.println("role_lis >>>" + helper.getUser().get("role_list"));
			if(helper.getUser().get("role_list").equals("WORKER") || helper.getUser().get("role_list").equals("WORKER2") ||  helper.getUser().get("role_list").equals("DO") || helper.getUser().get("role_list").equals("CONSCADMIN,WORKER")) {
				param.put("dept_cd", userInfo.get("dept_cd"));
				param.put("grp_gb_cd", "CONT_END");
				param.put("del_pr_cd", "DEL_PR");
			}
			
			if(request.getParameter("startRow") != null && !"0".equals(request.getParameter("startRow")))
			{
				begin = Integer.parseInt(request.getParameter("startRow"));
			}
			
			if(request.getParameter("endRow") != null && !"0".equals(request.getParameter("endRow")))
			{
				end = Integer.parseInt(request.getParameter("endRow"));
			}
			
			if(request.getParameter("currentPage") != null && !"0".equals(request.getParameter("currentPage")))
			{
				currentPage = Integer.parseInt(request.getParameter("currentPage"));
			}
			
			if(request.getParameter("changeWriteMode") != null) {
				changeWriteMode = request.getParameter("changeWriteMode");
			}
			
			param.put("begin", begin);
			param.put("end", end);
			
			listMap = jdbc.executeQueryList("skb/prstatus", "select.prstatus.list", param);
			totalMap = jdbc.executeQuery("skb/prstatus", "prlist.select.total", param);
			
			System.out.println("changeWriteMode >>>" + changeWriteMode);
			request.setAttribute("changeWriteMode", changeWriteMode);
			request.setAttribute("selectedPRStatusCD", selectedPRStatusCD);
			request.setAttribute("resultList", listMap);
			request.setAttribute("resultTotal", totalMap);
			request.setAttribute("begin", begin);
			request.setAttribute("end", end);
			request.setAttribute("currentPage", currentPage);
			request.setAttribute("prStatusCode", prStatusCode);
			request.setAttribute("prStatusDetailCD", prStatusDetailCD);
			
			RequestDispatcher rd = request.getRequestDispatcher("/modules/bp/skb/esPRStatusList.jsp");
			rd.forward(request, response);
			
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		if(request.getParameter("mode").equals("save")) {
			doUpdate(request, response);
		}else {
			doGet(request, response);
		}
	}
	
	protected void doUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String no[] = request.getParameterValues("no");
		String approval_date[] = request.getParameterValues("approval_date");
		String doc_no[] = request.getParameterValues("doc_no");
		String approval_title[] = request.getParameterValues("approval_title");
		String amount[] = request.getParameterValues("amount");
		String pr_status[] = request.getParameterValues("pr_status");
		String pr_status_cd[] = request.getParameterValues("col_prsts_gb");
		String purc_grp_nm[] = request.getParameterValues("purc_grp_nm");
		String dept_nm[] = request.getParameterValues("dept_nm");
		String dept_cd[] = request.getParameterValues("dept_cd");
		String charge_name[] = request.getParameterValues("charge_name");
		String approval_name[] = request.getParameterValues("approval_name");
		String budget_cls_nm[] = request.getParameterValues("budget_cls_nm");
		String attach_yn[] = request.getParameterValues("attach_yn");
		String message = "";
		
		TransactionManager.getInstance().transact();
		response.setContentType("text/html; charset=UTF-8");
		
		try {
			JdbcAgency jdbc = new JdbcAgency();
			jdbc.setDefaults(ConfigUtils.getVirtualSessionUser());
			jdbc.executeUpdate("skb/prstatus", "delete.temp.excel.item", null);
			
			for(int i = 0; i < no.length; i++) {
				Map<String, Object> param = new eswf.dataobject.Map();
				param.put("no", no[i]);
				param.put("doc_no", doc_no[i]);
				param.put("approval_date", approval_date[i]);
				param.put("approval_title", approval_title[i]);
				param.put("amount", StringUtil.removeComma(amount[i]));
				param.put("pr_status", pr_status[i]);
				param.put("pr_status_cd", pr_status_cd[i]);
				param.put("purc_grp_nm", purc_grp_nm[i]);
				param.put("dept_nm", dept_nm[i]);
				param.put("dept_cd", dept_cd[i]);
				param.put("charge_name", charge_name[i]);
				param.put("approval_name", approval_name[i]);
				param.put("budget_cls_nm", budget_cls_nm[i]);
				param.put("attach_yn", attach_yn[i]);
				
				jdbc.executeUpdate("skb/prstatus", "update.approv.list", param);
			}
			
			message = "수정 되었습니다.";
					
			response.getWriter().write(message);
			response.getWriter().flush();
			
			System.out.println(no[0]);
			TransactionManager.getInstance().accept();
		} catch (Exception e) {
			e.printStackTrace();
			message = "수정시 에러가 발생하였습니다.";
			// TODO: handle exception
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
