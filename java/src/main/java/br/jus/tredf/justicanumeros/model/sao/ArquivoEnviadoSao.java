package br.jus.tredf.justicanumeros.model.sao;

import java.io.Serializable;

public class ArquivoEnviadoSao implements Serializable {

	private static final long serialVersionUID = -5718732962985879523L;
	private long length;
	private byte[] bytes;
	private String name;
	private String type;
	private String loginUsuario;
	private String dataReferencia;
	private boolean flag = false;
	
	
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLoginUsuario() {
		return loginUsuario;
	}
	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = loginUsuario;
	}
	public String getDataReferencia() {
		return dataReferencia;
	}
	public void setDataReferencia(String dataReferencia) {
		this.dataReferencia = dataReferencia;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}	
	
}
