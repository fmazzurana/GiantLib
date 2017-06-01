package net;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import commons.MyException;

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

		// quitwait = true: waits the response to the QUIT command; false: don't
	    props.put("mail.smtps.quitwait", "false");
		// Get the default Session object.
		session = Session.getDefaultInstance(props);
	}
	
	/**
	 * Combinations on the basic method to send an email
	 * 
	 * @param toAddrs/toAddr List of destination addresses or single destination address
	 * @param ccAddrs/ccAddr List of carbon-copy destination addresses or single cc destination address (null for none)
	 * @param subject Subject of the message
	 * @param text/lines Body of the message
	 * @param attachments/attachment List of the filenames to be attached or single attach filename (null for none)
	 * @throws MyException
	 */
	
	public void Send(String toAddr, String ccAddr, String subject, String text, String attachment) throws MyException {
		sendEmail(toAddr, ccAddr, subject, text, attachment);
	}
	public void Send(String toAddr, String ccAddr, String subject, String text) throws MyException {
		sendEmail(toAddr, ccAddr, subject, text, null);
	}
	public void Send(String toAddr, String subject, String text) throws MyException {
		sendEmail(toAddr, null, subject, text, null);
	}
	
	// --------------------------------------------------------------------------------------------
	// Privates
	// --------------------------------------------------------------------------------------------
	/**
	 * Basic method to send an email
	 * 
	 * @param toAddr Destination address; could be a list separated by a semicolon
	 * @param ccAddr Carbon-copy destination; could be empty or a list separated by a semicolon
	 * @param subject Subject of the message
	 * @param text Body of the message
	 * @param attachment Filename to be attached; could be empty or a list separated by a semicolon
	 * @throws MyException
	 */
	private void sendEmail(String toAddr, String ccAddr, String subject, String text, String attachment) throws MyException {
		if (toAddr == null || toAddr.isEmpty())
			throw new MyException("GMail.Send: null or empty destination address");
		if (subject == null || subject.isEmpty())
			throw new MyException("GMail.Send: null or empty subject");
		if (text == null || text.isEmpty())
			throw new MyException("GMail.Send: null or empty text");
		
		try {
			// Creates a MimeMessage object
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderUsr));
			for (String addr : toAddr.split(";"))
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(addr));
			if (ccAddr != null && !ccAddr.isEmpty())
				for (String addr : ccAddr.split(";"))
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(addr));
			message.setSubject(subject);
			
			// Creates the message part
			BodyPart messageBodyPart = new MimeBodyPart();
        	messageBodyPart.setContent(message, "text/html; charset=utf-8");
			messageBodyPart.setText(text);
			
			// Creates a multipart message
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			// inserts the attachments (if any)
			if (attachment != null && !attachment.isEmpty()) {
				for (String attach : attachment.split(";")) {
					messageBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(attach);
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(attach);
					multipart.addBodyPart(messageBodyPart);
				}
			}
			message.setContent(multipart);
			
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
