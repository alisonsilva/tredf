package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="FORMULARIO")
public class Formulario implements Serializable {

  private static final long serialVersionUID = -4561758910172747704L;

  @XmlAttribute private Long id;
  @XmlAttribute private String nome;
  @XmlAttribute private String descricao;
  @XmlAttribute private Date dtInsercao;
  @XmlAttribute private String nomeRelatorio;
  
  private List<Permissao> permissoes = new ArrayList<Permissao>();
  
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
  public Date getDtInsercao() {
    return dtInsercao;
  }
  public void setDtInsercao(Date dtInsercao) {
    this.dtInsercao = dtInsercao;
  }
  public String getNomeRelatorio() {
    return nomeRelatorio;
  }
  public void setNomeRelatorio(String nomeRelatorio) {
    this.nomeRelatorio = nomeRelatorio;
  }  
  public List<Permissao> getPermissoes() {
    return permissoes;
  }  
  public void setPermissoes(List<Permissao> permissoes) {
    this.permissoes = permissoes;
  }
  
  @Override
  public String toString() {
    return "{id: " + id + ", descricao: '" + descricao +"'}";
  }
}
