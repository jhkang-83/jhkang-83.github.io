package emro.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailUtility {
	
	public static void sendMail(String host, String port, 
								final String userName, final String password, String toAddress,
								String subject, String message) throws AddressException, MessagingException
	{
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.put("mail.smtp.sendpartial", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "false");
//		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");
//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//      props.put("mail.smtp.socketFactory.fallback", "false");
//      props.setProperty("mail.smtp.quitwait", "false");
		
		
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};
		
		Session session = Session.getInstance(props, auth);
		
		Message msg = new MimeMessage(session);
		
		msg.setFrom(new InternetAddress(userName));
		InternetAddress[] toAddresses = { new InternetAddress(toAddress)};
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setHeader("content-type", "text/html; charset=UTF-8");
		msg.setContent(message, "text/html; charset=UTF-8");
//		msg.setText(message1);
		
		Transport.send(msg);
	}

}
