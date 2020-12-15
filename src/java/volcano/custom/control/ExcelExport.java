package volcano.custom.control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eswf.exception.FoundationException;
import eswf.managers.TransactionManager;

public class ExcelExport extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String pageNm = request.getParameter("fileNm");
		String path = "/modules/bp/skb/";
		TransactionManager.getInstance().transact();
		try {
			
			RequestDispatcher rd = request.getRequestDispatcher(path + pageNm +".jsp");
			rd.forward(request, response);
			TransactionManager.getInstance().accept();
		}catch(Exception e) {
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
