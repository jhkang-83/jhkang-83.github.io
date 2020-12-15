package volcano.custom.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import emro.util.JsonUtil;
import eswf.exception.FoundationException;
import eswf.managers.TransactionManager;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import volcano.custom.control.SearchCodeController;

/**
 * Servlet implementation class PRStatusWrite
 */
@WebServlet("/bw/emPRStatusWrite")
public class PRStatusWrite extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected SearchCodeController searchCode = new SearchCodeController();
	protected List<Map> prStatusDetailCD = null;
	/**
     * @see HttpServlet#HttpServlet()
     */
    public PRStatusWrite() {
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
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html charset=UTF-8");
		
		TransactionManager.getInstance().transact();
		
		try {
			request.setAttribute("prStatusCD", prStatusDetailCD);
			RequestDispatcher rd = request.getRequestDispatcher("/modules/bp/skb/esPRStatusWrite.jsp");
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
		doGet(request, response);
	}

}
