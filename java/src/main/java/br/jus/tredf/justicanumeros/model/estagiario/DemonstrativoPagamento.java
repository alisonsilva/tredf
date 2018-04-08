package br.jus.tredf.justicanumeros.model.estagiario;

import java.io.Serializable;

public class DemonstrativoPagamento implements Serializable {
	private static final long serialVersionUID = 7579977423371718611L;
	
	private String codigo;
	private String nome;
	private String cpf;
	private String competencia;
	private int jornadaEfetiva;
	private float bolsaBase;
	private float ajuste;
	private float bolsaAuxilio;
	private float auxilioTransporte;
	private float valorPagar;
	
	
	@Override
	public String toString() {
	  return "{codigo: '" + codigo + "', " +
	         "nome: '" + nome + "', " +
	         "cpf: '" + cpf + "', " +
	         "competencia: '" + competencia + "', " +
	         "jornadaEfetiva: " + jornadaEfetiva + 
	         ", bolsaBase: " + bolsaBase + 
	         ", ajuste: " + ajuste + 
	         ", bolsaAuxilio: " + bolsaAuxilio +
	         ", auxilioTransporte: " + auxilioTransporte +
	         ", valorPagar: " + valorPagar + "}";
	}
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public int getJornadaEfetiva() {
		return jornadaEfetiva;
	}
	public void setJornadaEfetiva(int jornadaEfetiva) {
		this.jornadaEfetiva = jornadaEfetiva;
	}
	public float getBolsaBase() {
		return bolsaBase;
	}
	public void setBolsaBase(float bolsaBase) {
		this.bolsaBase = bolsaBase;
	}
	public float getAjuste() {
		return ajuste;
	}
	public void setAjuste(float ajuste) {
		this.ajuste = ajuste;
	}
	public float getBolsaAuxilio() {
		return bolsaAuxilio;
	}
	public void setBolsaAuxilio(float bolsaAuxilio) {
		this.bolsaAuxilio = bolsaAuxilio;
	}
	public float getAuxilioTransporte() {
		return auxilioTransporte;
	}
	public void setAuxilioTransporte(float auxilioTransporte) {
		this.auxilioTransporte = auxilioTransporte;
	}
	public float getValorPagar() {
		return valorPagar;
	}
	public void setValorPagar(float valorPagar) {
		this.valorPagar = valorPagar;
	}
	public String getCompetencia() {
		return competencia;
	}
	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}

	
}
