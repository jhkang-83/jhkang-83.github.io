package volcano.lock;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;
import flex.messaging.util.URLEncoder;

/**
 * Servlet implementation class ReleaseLockManager
 */

@WebServlet("/ReleaseLockManager")
public class ReleaseLockManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String usr_id = "";
    private static String usr_name = "";
    private static String biz_reg_no = "";
    private static String chr_email = "";

    private JdbcAgency jdbc = new JdbcAgency();


    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReleaseLockManager() throws FoundationException {
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

		usr_id = request.getParameter("chrId");
		usr_name = request.getParameter("chrName");
		biz_reg_no = request.getParameter("bizRegNo");
		chr_email = request.getParameter("chrEmail");

		Map<String, Object> param = new HashMap<String, Object>();
		Map result = null;

		String isRelease = "";

		param.put("usr_id", usr_id);
		param.put("usr_nm", usr_name);
		param.put("vd_sn", biz_reg_no);
		param.put("email", chr_email);

		TransactionManager.getInstance().transact();

		response.setContentType("text/html; charset=UTF-8");

		try {

			result = jdbc.executeQuery("lock", "retrieve.lock.user", param);

			if(result != null){
				jdbc.executeUpdate("lock", "release.lock", param);
				isRelease = "계정잠금이 해제 되었습니다.";
			}else{
				isRelease = "가입된 정보가 존재하지 않거나 일치하지 않습니다. \n자세한 문의는 시스템 담당자에게 문의 주시기 바랍니다.";
				System.out.println("가입된 정보가 없거나 일치하지 않습니다. \n자세한 문의는 시스템 담당자에게 문의 주시기 바랍니다.");
			}

			URLEncoder.encode(isRelease, "UTF-8");
			response.getWriter().write(isRelease);
			response.getWriter().flush();
			TransactionManager.getInstance().accept();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				TransactionManager.getInstance().restore();
			} catch (FoundationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally{
			TransactionManager.getInstance().removeSession();
		}

	}
}
