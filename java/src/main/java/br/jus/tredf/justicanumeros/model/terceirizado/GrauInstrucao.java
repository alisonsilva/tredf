package br.jus.tredf.justicanumeros.model.terceirizado;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="GRAU_INSTRUCAO")
@XmlAccessorType(XmlAccessType.FIELD)
public class GrauInstrucao implements Serializable {

  private static final long serialVersionUID = 1397138076476579576L;

  @XmlAttribute private int id;
  @XmlAttribute private String nome;
  @XmlAttribute private String descricao;
  
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
  
  @Override
  public String toString() {
    return "{id: " + id + ", nome: '" + nome + "', descricao: '" + descricao +"'}";
  }
}
