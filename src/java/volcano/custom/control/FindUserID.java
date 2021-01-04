package volcano.custom.control;

import java.awt.Font;
import java.awt.Label;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import emro.util.EmailUtility;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;
import flex.messaging.util.URLEncoder;

/**
 * Servlet implementation class FindUserID
 * 아이디 찾기
 */

@WebServlet("/findUserId")
public class FindUserID extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
   protected String usr_name;
   protected String biz_reg_no;
   protected String chr_email;
   protected String send_email;
   private static String result = "";

   private String host;
   private String port;
   private String user;
   private String pass;

	public FindUserID() {
        super();
        // TODO Auto-generated constructor stub
    }

	public void init(){
		ServletContext context = getServletContext();
		host = context.getInitParameter("host");
		port = context.getInitParameter("port");
		user = context.getInitParameter("user");
		pass = context.getInitParameter("pass");
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
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		this.usr_name = request.getParameter("chrName");
		this.biz_reg_no = request.getParameter("bizRegNo");
//		this.chr_email = request.getParameter("chrEmail");

		String usr_id = null;
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> map;

		System.out.println("usr_name >>>>>" + this.usr_name);

		param.put("usr_nm", usr_name);
		param.put("vd_sn", biz_reg_no);
//		param.put("email", chr_email);

		TransactionManager.getInstance().transact();
		response.setContentType("text/html; charset=UTF-8");

		try{
			JdbcAgency jdbc = new JdbcAgency();

			map = jdbc.executeQuery("admin/user", "select.user.findId", param);

			if(map == null)
			{
				result = "가입된 정보가 존재하지 않거나 일치하지 않습니다.";

			}else
			{
				usr_id = (String)map.get("usr_id");
				chr_email = (String)map.get("usr_email");
				send_email = (String)map.get("send_email");
				sendMail(usr_id);
			}

			URLEncoder.encode(result, "UTF-8");
			response.getWriter().write(result);
			response.getWriter().flush();
		}catch(Exception e){
//			e.printStackTrace();
		}

		System.out.println("result >>>>>" + result);
	}

	protected void sendMail(String usr_id) throws ServletException, IOException {
		String subject = "[SKB 구매포탈(Withpro)시스템] 회원 아이디 안내";
		String text1 = "회원 가입시 등록된 이메일 "+"\n";
		String text2 ="로 아이디를 메일 발송 하였습니다.";
		String content1 = "안녕하세요. SK브로드밴드 구매포탈시스템 입니다."+"<br><br>";
		String content2 = "[" +usr_name + "]"+ "님의 요청으로 아래와 같이 ID 조회하여 안내드립니다."+"<br><br>";
		String content3 = "시스템 문의: jhkim2956@partner.sk.com" + "<br><br><br>" +
							"* 본 메일은 발신 전용 메일로, 회신되지 않습니다.";

		String content = content1 + content2 + "<b>" + usr_id + "</b><br><br>" + content3;

		try {
			EmailUtility.sendMail(host, port, user, pass, send_email, subject, content);
			result = text1 + chr_email + text2;
		} catch (Exception e) {
			// TODO: handle exception
			result = "아이디를 메일 발송 도중 오류가 발생 하였습니다.";
			e.printStackTrace();
		}
	}


}
