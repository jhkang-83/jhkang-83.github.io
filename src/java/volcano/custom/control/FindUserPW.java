package volcano.custom.control;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import emro.util.EmailUtility;
import eswf.exception.FoundationException;
import eswf.jdbc.JdbcAgency;
import eswf.managers.TransactionManager;
import flex.messaging.util.URLEncoder;

/**
 * Servlet implementation class FindUserPW
 * 패스워드 찾기
 */

@WebServlet("/findUserPw")
public class FindUserPW extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    private static String usr_id = "";
    private static String usr_name = "";
    private static String biz_reg_no = "";
    private static String chr_email = "";
    private static String send_mail = "";
    private static String usr_randomPw = null;
    private static String returnShaPw = null;
    private static String result = "";

    private String host;
    private String port;
    private String user;
    private String pass;

    protected UserController uc = new UserController();

	public FindUserPW() throws FoundationException {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		usr_id = request.getParameter("pwCheck_chrId");
		usr_name = request.getParameter("pwCheck_chrName");
		biz_reg_no = request.getParameter("pwCheck_bizRegNo");

		uc.setPwdLength(12);
		usr_randomPw = uc.randomPwd();


		if(usr_randomPw == null)
		{
			return;
		}

		response.setContentType("text/html; charset=UTF-8");

		try {
			returnShaPw = makeSHA(usr_randomPw);

			try {
				loadUserInfo(returnShaPw);
			} catch (FoundationException e) {
				// TODO Auto-generated catch block
			}

			URLEncoder.encode(usr_randomPw, "UTF-8");
			response.getWriter().write(result);
			response.getWriter().flush();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
		}


	}

	public void loadUserInfo(String returnShaPw) throws FoundationException
	{
		boolean isUser = false;
		try{
			if(returnShaPw != null)
			{
				isUser = uc.loadUserInfo(usr_id, biz_reg_no, usr_name);
			}else
			{

				System.out.println("임시 비밀번호가 생성되지 않았습니다.");
				return;
			}

			System.out.println(isUser);

			if(isUser)
			{
				chr_email = uc.chr_email;
				send_mail = uc.send_email;

				savePw(returnShaPw);

			}else{
				result = "가입된 정보가 존재하지 않거나 일치하지 않습니다.";
				return;
			}


		}catch(Exception e)
		{
		}



	}

	public void savePw(String returnShaPw) throws FoundationException{

		String isSavePW = "";

		try{
			isSavePW = uc.savePw(returnShaPw, usr_id, biz_reg_no, usr_name);

			if(isSavePW == "sucess")
			{
				System.out.println("임시 비밀번호로 저장 완료 되었습니다.");
				sendMail();
			}else
			{
				result = "임시 비밀번호 저장 시 오류가 발생 되었습니다.";
				System.out.println("임시 비밀번호 저장 시 오류가 발생 되었습니다.");

			}

		}catch(Exception e){
		}


	}

	protected void sendMail() throws ServletException, IOException {

		String recipient = send_mail;
		String subject = "[SKB 구매포탈(Withpro)시스템] 패스워드 초기화 안내";
		String contentPw = usr_randomPw;
		String content1 = "안녕하세요. SK브로드밴드 구매포탈시스템 입니다."+"<br><br>";
		String content2 = "[" +usr_name + "]" + "님의 요청으로 아래와 같이 패스워드 초기화 하여 안내 드립니다." + "<br><br>" +
							"<b> " + contentPw + "</b>" + "<br><br>" + "초기화된 패스워드로 로그인 하신 뒤에 반드시" + "<br><br>" +
							 "HOME > 자사정보관리 페이지에서 패스워드 재설정을 해주시길 바랍니다." + "<br><br>";
		String content3 = "시스템 문의: jhkim2956@partner.sk.com" + "<br><br><br>" +
							"* 본 메일은 발신 전용 메일로, 회신되지 않습니다.";
		String content = content1 + content2 + content3;

		String text1 = "회원 가입시 등록된 이메일 ";
		String text2 = "임시 비밀번호를 메일 발송 하였습니다.";

		String resultMessage = "";

		try {
			EmailUtility.sendMail(host, port, user, pass, recipient, subject, content);
			resultMessage = "The e-mail was sent successfully";
			result = text1 + chr_email + "로" + "\n" + text2;
		}catch(Exception e) {
			result = "임시 비밀번호를 메일 발송 도중 오류가 발생 하였습니다.";
			e.printStackTrace();
		}finally {

		}

		System.out.println(resultMessage);
	}

	public static String makeSHA(String inputText) throws NoSuchAlgorithmException
	{
		String shaPw = inputText;
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(shaPw.getBytes());
		byte[] digest = md.digest();

		StringBuffer sb = new StringBuffer();
		for(byte b : digest)
		{
			sb.append(Integer.toHexString(b & 0xff));
		}
		String result = sb.toString().replaceAll("[0]", "");

		return result;
	}

}
