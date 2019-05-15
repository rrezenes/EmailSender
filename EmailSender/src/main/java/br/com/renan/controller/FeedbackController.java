package br.com.renan.controller;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.renan.model.Feedback;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

	@Autowired
	public JavaMailSender emailSender;

	@PostMapping
	public void sendFeedback(@RequestBody Feedback feedback, BindingResult bindingResult) throws IOException, MessagingException {
		if (bindingResult.hasErrors()) {
			throw new ValidationException("Feedback is not valid");
		}

		enviarEmail("rezeness@no-spam.ws", "New feedback from " + feedback.getName(), feedback.getFeedback());
		
	}

	public void enviarEmail(String to, String assunto, String mensagem) throws IOException, MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setTo(to);
		helper.setSubject(assunto);

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(mensagem, "text/html; charset=UTF-8");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		message.setContent(multipart);
		emailSender.send(message);
	}
}
