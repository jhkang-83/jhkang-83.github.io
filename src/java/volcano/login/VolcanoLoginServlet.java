package volcano.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;
import eswf.util.ConfigUtils;
import volcano.custom.control.Helper;

/**
 * Servlet implementation class VolcanoLoginServlet
 * 로그인 서블릿
 */
@WebServlet("/volcanoLogin")
public class VolcanoLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public VolcanoLoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("text/html; charset=UTF-8");
		RequestDispatcher rd = request.getRequestDispatcher("login_supplier.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
    	request.setCharacterEncoding("UTF-8");
    	response.setContentType("text/html; charset=UTF-8");

    	Map<String, Object> user;
    	Map<String, Object> param = new HashMap<String, Object>();
    	Map<String, Object> map = null;

    	final String id = request.getParameter("principal");
    	final String pwd = request.getParameter("credential");
    	final String isNewVendor = request.getParameter("isNewVendor");
    	final String system = "EMRO";
    	final String company = "SKB";
    	final String locale = "ko_KR";

    	param.put("usr_id", id);
    	param.put("sys_id", system);
    	param.put("pwd", pwd);
    	param.put("comp_cd", company);

    	TransactionManager.getInstance().transact();

    	System.out.println("request >>>>>" + request);
    	System.out.println("principal >>>>>" + request.getParameter("principal"));
    	System.out.println("id >>>>>" + id);
    	System.out.println("pwd >>>>>" + pwd);

    	try {
			JdbcAgency jdbc = new JdbcAgency();
			jdbc.setDefaults(ConfigUtils.getVirtualSessionUser());

			if(!"dummy".equals(id) && "vendorlogin".equals(isNewVendor))
			{
				map = jdbc.executeQuery("login", "login.check.select.usrid", param);
			}else {
				map = jdbc.executeQuery("login", "login.check.select", param);
			}

			Helper helper = new Helper(request, new eswf.dataobject.Map());

			if(map == null)
			{
				System.out.println("No Such User");
				response.sendRedirect("/loginSupplier");
			}
			else if(!pwd.equals(map.get("pwd")))
			{
				System.out.println("Bad Credential");
				response.sendRedirect("/loginSupplier");

			}else {
				map.put("comp_cd", company);
				user = jdbc.executeQuery("login", "login.session.data.select", map);
				user.put("locale", locale);

				if(!company.equals("company"))
				{
					user.put("comp_cd", company);
					user.put("s_comp_cd", company);
				}

				request.getSession().setAttribute("user_menus", jdbc.executeQueryList("login", "login.session.menu.select", user));

				helper.login((eswf.dataobject.Map)user);

				System.out.println("login Sucess");
				response.sendRedirect("/Main");

			}

			TransactionManager.getInstance().accept();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
