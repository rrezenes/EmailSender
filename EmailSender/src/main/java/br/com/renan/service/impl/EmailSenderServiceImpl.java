package br.com.renan.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import br.com.renan.service.EmailSenderService;

@Service
@EnableScheduling
public class EmailSenderServiceImpl implements EmailSenderService {

    private static final String EMAIL_EDITABLE_TEMPLATE_CLASSPATH_RES = "classpath:mail/editablehtml/email-editable.html";

    private static final String BACKGROUND_IMAGE = "mail/editablehtml/images/background.png";
    private static final String LOGO_BACKGROUND_IMAGE = "mail/editablehtml/images/logo-background.png";
    private static final String THYMELEAF_BANNER_IMAGE = "mail/editablehtml/images/thymeleaf-banner.png";
    private static final String THYMELEAF_LOGO_IMAGE = "mail/editablehtml/images/thymeleaf-logo.png";

	private static final String PNG_MIME = "image/png";

	@Autowired
	private TemplateEngine stringTemplateEngine;

    @Autowired
    private ApplicationContext applicationContext;
    
	@Autowired
	public JavaMailSender mailSender;

	@Autowired
	private TemplateEngine htmlTemplateEngine;

	@Async("emailSender")
	@Override
	public void enviarEmail(String to, String assunto, String mensagem) throws IOException, MessagingException {
		MimeMessage message = montaEmail(to, assunto, mensagem);
		mailSender.send(message);
	}

	@Async("emailSender")
	@Scheduled(fixedDelay = 10000)
	@Override
	public void enviarEmailInformandoDataHora() throws MessagingException, IOException {

		String to = "rezeness@gmail.com";
		String assunto = "Data e Hora!";
		String mensagem = "Agora são: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

		sendEditableMail("Renan Rezenes", to, this.getEditableMailTemplate(), new Locale("pt", "BR"));
		
//		MimeMessage message = montaEmail(to, assunto, mensagem);
//		mailSender.send(message);
	}

	private MimeMessage montaEmail(String to, String assunto, String mensagem) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
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
	
	/*
	 * Send HTML mail with inline image
	 */
	public void sendEditableMail(final String recipientName, final String recipientEmail, final String htmlContent,
			final Locale locale) throws MessagingException {

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
		message.setSubject("Example editable HTML email");
		message.setFrom("icarwash.br@gmail.com");
		message.setTo(recipientEmail);

		// Prepare the evaluation context
		final Context ctx = new Context(locale);
		ctx.setVariable("name", recipientName);
		ctx.setVariable("subscriptionDate", new Date());
		ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));

		// Create the HTML body using Thymeleaf
		final String output = stringTemplateEngine.process(htmlContent, ctx);
		message.setText(output, true /* isHtml */);

		// Add the inline images, referenced from the HTML code as "cid:image-name"
		message.addInline("background", new ClassPathResource(BACKGROUND_IMAGE), PNG_MIME);
		message.addInline("logo-background", new ClassPathResource(LOGO_BACKGROUND_IMAGE), PNG_MIME);
		message.addInline("thymeleaf-banner", new ClassPathResource(THYMELEAF_BANNER_IMAGE), PNG_MIME);
		message.addInline("thymeleaf-logo", new ClassPathResource(THYMELEAF_LOGO_IMAGE), PNG_MIME);

		// Send mail
		this.mailSender.send(mimeMessage);
	}

    public final String EMAIL_TEMPLATE_ENCODING = "UTF-8";
    
    public String getEditableMailTemplate() throws IOException {
        final Resource templateResource = this.applicationContext.getResource(EMAIL_EDITABLE_TEMPLATE_CLASSPATH_RES);
        final InputStream inputStream = templateResource.getInputStream();
        return IOUtils.toString(inputStream, this.EMAIL_TEMPLATE_ENCODING);
    }

}
