package br.jus.tredf.justicanumeros.model.terceirizado;

import java.io.Serializable;

public class LotacaoTerceirizado implements Serializable {

	private static final long serialVersionUID = 1172531436725676282L;

	private Integer id;
	private String nome;
	private String descricao;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Override
	public String toString() {
		return "{id: " + id + ", nome: '" + nome + "', descricao: '" + descricao +"'}"; 
	}
}
