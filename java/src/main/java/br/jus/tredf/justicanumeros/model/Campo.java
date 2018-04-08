package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="CAMPO")
public class Campo implements Serializable {
  private static final long serialVersionUID = 527880044281260511L;

  @XmlAttribute private Long id;
  @XmlAttribute private String nome;
  @XmlAttribute private String descricao;
  @XmlAttribute private Double valor;
  @XmlAttribute private boolean flProtocolo;
  @XmlAttribute private Integer indicador;
  
  private String ajuda;

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
  public String getDescricao() {
    return descricao;
  }
  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }
  public Double getValor() {
    return valor;
  }
  public void setValor(Double valor) {
    this.valor = valor;
  }
  
  public boolean isFlProtocolo() {
    return flProtocolo;
  }
  public void setFlProtocolo(boolean flProtocolo) {
    this.flProtocolo = flProtocolo;
  }
  public String getAjuda() {
    return ajuda;
  }
  public void setAjuda(String ajuda) {
    this.ajuda = ajuda;
  }
  public Integer getIndicador() {
    return indicador;
  }
  public void setIndicador(Integer indicador) {
    this.indicador = indicador;
  }
  @Override
  public String toString() {
    return "{id: " + id + ", nome: '" + nome + "', descricao: '" + descricao + "', valor: " + valor + "}";
  }
}
