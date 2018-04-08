package br.jus.tredf.justicanumeros.model.email;

import java.io.Serializable;

public class MailMessage implements Serializable {

	private static final long serialVersionUID = 825525660900870636L;

	private String to;
	private String copia;
	private String from;
	private String subject;
	private String text;
	
	
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	public String getCopia() {
		return copia;
	}
	public void setCopia(String copia) {
		this.copia = copia;
	}
	@Override
	public String toString() {
		return "{to: '" + to + "',\n from: '" + from + "',\n subject: '" + subject + "',\n text: '" + text + "'}";
	}
}
