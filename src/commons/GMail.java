package commons;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMail {

	// --------------------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------------------
	private static final String host = "smtp.gmail.com";
	private static final String port = "587";
	
	// --------------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------------
	private String senderUsr;
	private String senderPwd;
	private Properties props;
	private Session session;
	
	// --------------------------------------------------------------------------------------------
	// Construct
	// --------------------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param usr Sender user (ie test@gmail.com)
	 * @param pwd Sender password
	 */
	public GMail(String usr, String pwd) {
		senderUsr = usr;
		senderPwd = pwd;
		// Setup mail server
		props = System.getProperties();		// or new Properties() ???
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.ssl.trust", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.user", senderUsr);
		props.put("mail.smtp.password", senderPwd);
		// Get the default Session object.
		session = Session.getDefaultInstance(props);
	}
	
	/**
	 * Sends an email
	 * 
	 * @param toAddr The destination email address
	 * @param subject The amail subject
	 * @param lines/text The amail body
	 * @throws MyException
	 */
	public void Send(String toAddr, String subject, List<String> lines) throws MyException {
		String text = "";
		for (String line : lines)
			text += line + "\n";
		Send(toAddr, subject, text);
	}
	public void Send(String toAddr, String subject, String text) throws MyException {
		try {
			// Creates a MimeMessage object
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderUsr));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
			message.setSubject(subject);
			message.setText(text);
//			Multipart mp = new MimeMultipart();
//			message.setContent(mp);

			// Send message
			Transport transport = session.getTransport("smtp");
			transport.connect(host, senderUsr, senderPwd);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (MessagingException mex) {
			throw new MyException(mex.getMessage());
		}
	}
}
