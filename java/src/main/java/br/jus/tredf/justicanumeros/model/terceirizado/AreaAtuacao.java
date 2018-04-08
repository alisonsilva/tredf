package br.jus.tredf.justicanumeros.model.terceirizado;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="AREA_ATUACAO")
public class AreaAtuacao implements Serializable {

  private static final long serialVersionUID = 3191935435814352464L;

  @XmlAttribute private int id;
  @XmlAttribute private String nome;
  @XmlAttribute private String descricao;
  @XmlAttribute private boolean estagiario;
  @XmlAttribute private int nivelInstrucao;
  public int getId() {
    return id;
  }
  public void setId(int id) {
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
  public boolean isEstagiario() {
    return estagiario;
  }
  public void setEstagiario(boolean estagiario) {
    this.estagiario = estagiario;
  }
  public int getNivelInstrucao() {
    return nivelInstrucao;
  }
  public void setNivelInstrucao(int nivelInstrucao) {
    this.nivelInstrucao = nivelInstrucao;
  }
  
  @Override
  public String toString() {
    return "{id: " + id +", nome: '" + nome + "', estagiario: " + estagiario +" }";
  }
}
