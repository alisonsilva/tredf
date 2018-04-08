package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="CARTORIO")
public class Cartorio implements Serializable {

  private static final long serialVersionUID = 397775369604032763L;

  @XmlAttribute private Long id;
  @XmlAttribute private String nome;
  @XmlAttribute private String sigla;
  
  
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
  public String getSigla() {
    return sigla;
  }
  public void setSigla(String sigla) {
    this.sigla = sigla;
  }
  
  @Override
  public String toString() {
    return "{id: " + id + ", nome : " + nome + ", sigla : " + sigla + "}";
  }
}
