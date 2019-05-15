package br.com.renan.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.renan.service.EmailSenderService;

@Service
@EnableScheduling
public class EmailSenderServiceImpl implements EmailSenderService {

	@Autowired
	public JavaMailSender emailSender;

	@Async("emailSender")
	@Override
	public void enviarEmail(String to, String assunto, String mensagem) throws IOException, MessagingException {
		MimeMessage message = montaEmail(to, assunto, mensagem);
		emailSender.send(message);
	}

	@Async("emailSender")
	@Scheduled(fixedDelay = 120000)
	@Override
	public void enviarEmailInformandoDataHora() throws MessagingException {

		String to = "rsxweer@gmail.com";
		String assunto = "Data e Hora!";
		String mensagem = "Agora são: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

		MimeMessage message = montaEmail(to, assunto, mensagem);
		emailSender.send(message);
	}

	private MimeMessage montaEmail(String to, String assunto, String mensagem) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		
		helper.setTo(to);
		helper.setSubject(assunto);

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(mensagem, "text/html; charset=UTF-8");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		message.setContent(multipart);
		return message;
	}

}
