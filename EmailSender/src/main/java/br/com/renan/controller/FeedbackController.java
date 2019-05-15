package br.com.renan.controller;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.renan.model.Feedback;
import br.com.renan.service.EmailSenderService;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

	@Autowired
	EmailSenderService emailService;

	@PostMapping
	public void sendFeedback(@RequestBody Feedback feedback, BindingResult bindingResult) throws IOException, MessagingException {
		if (bindingResult.hasErrors()) {
			throw new ValidationException("Feedback is not valid");
		}

		emailService.enviarEmail("rezeness@gmail.com", "New feedback from " + feedback.getName(), feedback.getFeedback());
		
	}

	

}
