package br.jus.tredf.justicanumeros.model.terceirizado;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="TERCEIRIZADO")
public class Terceirizado implements Serializable {

  private static final long serialVersionUID = -7646248613016840497L;

  @XmlAttribute private Long id;
  @XmlAttribute private String nome;
  @XmlAttribute private boolean ativo;
  
  private LotacaoTerceirizado lotacao;  
  private GrauInstrucao grauInstrucao;
  private AreaAtuacao areaAtuacao;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getNome() {
    return nome;
  }
  public void setNome(String nome) {
    this.nome = nome;
  }
  public boolean isAtivo() {
    return ativo;
  }
  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }  
  public GrauInstrucao getGrauInstrucao() {
    return grauInstrucao;
  }
  public void setGrauInstrucao(GrauInstrucao grauInstrucao) {
    this.grauInstrucao = grauInstrucao;
  }
  public AreaAtuacao getAreaAtuacao() {
    return areaAtuacao;
  }
  public void setAreaAtuacao(AreaAtuacao areaAtuacao) {
    this.areaAtuacao = areaAtuacao;
  }
  public LotacaoTerceirizado getLotacao() {
		return lotacao;
	}
	public void setLotacao(LotacaoTerceirizado lotacao) {
		this.lotacao = lotacao;
	}
	
	
	@Override
  public String toString() {
    return "{id: " + id +", nome: '" + nome + "', ativo: " + ativo 
        + ", grauInstrucao: " + grauInstrucao.toString() + ", areaAtuacao: " + areaAtuacao.getDescricao() 
        + (lotacao != null ? ", lotacao: " + lotacao.toString() : "") + "}";
  }
}
