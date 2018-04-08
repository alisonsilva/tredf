package br.jus.tredf.justicanumeros.model.exception;

@SuppressWarnings("all")
public class ParametroException extends RuntimeException {
	private int codigoErro;
	
	public ParametroException() {
		super ("Erro na camada de persistência");
	}
	
	public ParametroException(String msg) {
		super(msg);
	}
	
	public ParametroException(String msg, int codigoErro) {
		super(msg);
		this.codigoErro = codigoErro;
	}
	
	public ParametroException(String msg, Throwable excp) {
		super(msg, excp);
	}

	public int getCodigoErro() {
		return codigoErro;
	}

	public void setCodigoErro(int codigoErro) {
		this.codigoErro = codigoErro;
	}

}
