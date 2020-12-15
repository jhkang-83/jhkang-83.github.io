package volcano.custom.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;
import eswf.util.ConfigUtils;

/**
 * Servlet implementation class RegisterVendor
 */
@WebServlet("/bp/emRegisterVendor")
public class RegisterVendor extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterVendor() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html charset=UTF-8");
		
		Map<String, Object> param = new eswf.dataobject.Map();
		Map<String, Object> c_terms_agree = new HashMap<String, Object>(); //가입약관
		Map<String, Object> c_info_agree = new HashMap<String, Object>(); //개인정보 취급방침 및 정보공유 동의 
		Map<String, Object> c_agree_1 = new HashMap<>(); //개인정보 수집 및 이용에 대한 동의
		Map<String, Object> c_agree_2 = new HashMap<>(); //개인정보 수집 및 이용에 대한 동의
		Map<String, Object> r_terma3_agree_1 = new HashMap<>(); //개인정보 제3자 제공에 대한 동의
		Map<String, Object> r_terma3_agree_2 = new HashMap<>(); //개인정보 제3자 제공에 대한 동의
		
 		TransactionManager.getInstance().transact();
		
		try {
			JdbcAgency jdbc = new JdbcAgency();
			jdbc.setDefaults(ConfigUtils.getVirtualSessionUser());
			
			param.put("sys_id", "EMRO");
			param.put("comp_cd", "SKB");
			
			param.put("term_no", 1);
			c_terms_agree = jdbc.executeQuery("esourcing/vendor", "select.vendor.term.info", param);
			
			param.put("term_no", 2);
			c_info_agree = jdbc.executeQuery("esourcing/vendor", "select.vendor.term.info", param);
			
			param.put("term_no", 3);
			c_agree_1 = jdbc.executeQuery("esourcing/vendor", "select.vendor.term.info", param);

			param.put("term_no", 5);
			c_agree_2 = jdbc.executeQuery("esourcing/vendor", "select.vendor.term.info", param);
			
			param.put("term_no", 4);
			r_terma3_agree_1 = jdbc.executeQuery("esourcing/vendor", "select.vendor.term.info", param);
			
			param.put("term_no", 6);
			r_terma3_agree_2 = jdbc.executeQuery("esourcing/vendor", "select.vendor.term.info", param);
			
			request.setAttribute("c_terms_agree", c_terms_agree);
			request.setAttribute("c_info_agree", c_info_agree);
			request.setAttribute("c_agree_1", c_agree_1);
			request.setAttribute("c_agree_2", c_agree_2);
			request.setAttribute("r_terma3_agree_1", r_terma3_agree_1);
			request.setAttribute("r_terma3_agree_2", r_terma3_agree_2);
			
			RequestDispatcher rd = request.getRequestDispatcher("/modules/bp/esourcing/vendorMgt/spRegisterVendor.jsp");
			rd.forward(request, response);
			TransactionManager.getInstance().accept();
		} catch (Exception e) {
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		doGet(request, response);
	}

}
