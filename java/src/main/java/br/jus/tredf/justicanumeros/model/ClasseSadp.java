package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="CLASSE_SADP")
public class ClasseSadp implements Serializable{

  private static final long serialVersionUID = -2058851804495402724L;

  @XmlAttribute private String nome;
  @XmlAttribute private String sigla;
  
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
    return "{nome: '" + nome +"', sigla: '" + sigla +"'}";
  }
}
