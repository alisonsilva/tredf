package br.jus.tredf.justicanumeros.model.sao;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ArquivoExecucao {
	private CommonsMultipartFile file;
	private String token;
	
	
	public CommonsMultipartFile getFile() {
		return file;
	}
	public void setFile(CommonsMultipartFile arquivoExecucao) {
		this.file = arquivoExecucao;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}	
}
