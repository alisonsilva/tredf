package br.jus.tredf.oracle.monitoramento.model.log;

public class InfoLogError {
	public String message;
	public InfoLogError(String errorLine) {
		this.message = errorLine;
	}
}
