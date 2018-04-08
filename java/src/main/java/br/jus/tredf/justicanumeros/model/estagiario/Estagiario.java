package br.jus.tredf.justicanumeros.model.estagiario;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Estagiario implements Serializable {

	private static final long serialVersionUID = -7366085436246507050L;
	public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	private String tipo;
	private String codigo;
	private String nome;
	private Date inicio;
	private Date termino;
	private String telefone;
	private String nomeSupervisor;
	private String emailSupervisor;
	private String instituicaoEnsino;
	private String curso;
	private String telefoneSupervisor;
	private String emailEstagiario;
	private String lotacao;
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		if (codigo.contains(".")) {
			codigo = codigo.substring(0, codigo.indexOf("."));
		}
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getInicio() {
		return inicio;
	}
	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}
	public void setInicio(String inicio) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			this.inicio = sdf.parse(inicio);
		} catch (ParseException e) {
		}
	}

	public Date getTermino() {
		return termino;
	}
	public void setTermino(Date termino) {
		this.termino = termino;
	}
	public void setTermino(String termino) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			this.termino = sdf.parse(termino);
		} catch (ParseException e) {
		}
	}
	
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getNomeSupervisor() {
		return nomeSupervisor;
	}
	public void setNomeSupervisor(String nomeSupervisor) {
		byte[] ptext = nomeSupervisor.getBytes(ISO_8859_1); 
		String value = new String(ptext, UTF_8); 		
		this.nomeSupervisor = value;
	}

	public String getEmailSupervisor() {
		return emailSupervisor;
	}
	public void setEmailSupervisor(String emailSupervisor) {
		this.emailSupervisor = emailSupervisor;
	}

	public String getInstituicaoEnsino() {
		return instituicaoEnsino;
	}
	public void setInstituicaoEnsino(String instituicaoEnsino) {
		this.instituicaoEnsino = instituicaoEnsino;
	}

	public String getCurso() {
		return curso;
	}
	public void setCurso(String curso) {
		this.curso = curso;
	}

	public String getTelefoneSubstituto() {
		return telefoneSupervisor;
	}
	public void setTelefoneSupervisor(String telefoneSupervisor) {
		if(telefoneSupervisor.contains(".")) {
			telefoneSupervisor = telefoneSupervisor.substring(0, telefoneSupervisor.indexOf("."));
		}
		this.telefoneSupervisor = telefoneSupervisor;
	}

	public String getEmailEstagiario() {
		return emailEstagiario;
	}
	public void setEmailEstagiario(String emailEstagiario) {
		this.emailEstagiario = emailEstagiario;
	}

	public String getLotacao() {
		return lotacao;
	}
	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return "{tipo: '" + tipo + "', "
				+ "codigo: '" + codigo + "', "
				+ "nome: '" + nome + "', "
				+ "inicio: '" + sdf.format(inicio) + "', "
				+ "termino: '" + sdf.format(termino) + "', "
				+ "telefone: '" + telefone + "', "
				+ "nomeSupervisor: '" + nomeSupervisor + "', "
				+ "emailSupervisor: '" + emailSupervisor + "', "
				+ "instituicaoEnsino: '" + instituicaoEnsino + "', "
				+ "curso: '" + curso + "', " 
				+ "telefoneSupervisor: '" + telefoneSupervisor + "', "
				+ "emailEstagiario: '" + emailEstagiario + "', "
				+ "lotacao: '" + lotacao + "'}";
	}
}
