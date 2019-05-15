package br.com.renan.service;

import java.io.IOException;

import javax.mail.MessagingException;

public interface EmailSenderService {

	public void enviarEmail(String to, String assunto, String mensagem) throws IOException, MessagingException;

	void enviarEmailInformandoDataHora() throws MessagingException;
	
}
