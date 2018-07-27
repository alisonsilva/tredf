package br.jus.tredf.justicanumeros.service.email;

import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import br.jus.tredf.justicanumeros.model.email.MailMessage;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Component
public class JmsReceiveEmail {
	private static final Logger logger = Logger.getLogger(JmsReceiveEmail.class);
	private static final String QUEUE_EMAIL = "qEmail";

	@Autowired
	private ResourceBundle bundle;

	@Autowired
	private PropertiesServiceController properties;
	
	@Value("${smtp.autenticacao}")
	private String smtpAutenticacao;
	
	@Value("${smtp.server}")
	private String smtpServer;
	
	@Value("${smtp.port}")
	private String smtpPort;
	
	@Value("${usuario.smtp}")
	private String smtpUsuario;
	
	@Value("${senha.smtp}")
	private String smtpSenha;

	@JmsListener(destination = QUEUE_EMAIL)
	private void receiveMessage(final Message<MailMessage> messageQueue) {
		MailMessage message = messageQueue.getPayload();
		try {
			Properties prps = new Properties();
			SMTPAuthenticator auth = null;

			if (smtpAutenticacao.equalsIgnoreCase("true")) {
				prps.put("mail.smtp.auth", true);
				prps.put("mail.smtp.user", smtpUsuario);
				prps.put("mail.smtp.password", smtpSenha);
				auth = new SMTPAuthenticator();
			} else {
				prps.put("mail.smtp.auth", false);
			}
			prps.put("mail.smtp.host", smtpServer);
			prps.put("mail.smtp.port", smtpPort);

			Session session = Session.getDefaultInstance(prps, auth);

			MimeMessage mimeMessage = new MimeMessage(session);

			String from = message.getFrom();
			if (StringUtils.isEmpty(from)) {
				from = properties.getProperty("from");
			}

			mimeMessage.setFrom(new InternetAddress(from));
			mimeMessage.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(message.getTo()));
			if(StringUtils.isNotEmpty(message.getCopia())) {
				mimeMessage.setRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(message.getCopia()));
			}
			mimeMessage.setSubject(message.getSubject());
			mimeMessage.setContent(message.getText(), "text/html");

			Transport.send(mimeMessage);

		} catch (MessagingException e) {
			logger.error("Erro ao montar mensagem: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Erro ao montar relatorio: " + e.getMessage());
		}
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(smtpUsuario, smtpSenha);
		}
	}
}
