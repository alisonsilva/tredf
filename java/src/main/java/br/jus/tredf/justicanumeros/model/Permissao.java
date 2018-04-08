package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="PERMISSAO")
public class Permissao implements Serializable {

  private static final long serialVersionUID = -430459198340036445L;

  @XmlAttribute private Long id;
  @XmlAttribute private String descricao;
  private boolean avaliada = false;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getDescricao() {
    return descricao;
  }
  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }  
  public boolean isAvaliada() {
    return avaliada;
  }
  public void setAvaliada(boolean avaliada) {
    this.avaliada = avaliada;
  }
  
  @Override
  public boolean equals(Object permissao) {
    boolean ret = false;
    if(permissao instanceof Permissao) {
      Permissao perm = (Permissao)permissao;
      ret = perm.descricao.equals(this.descricao);
    }
    return ret;
  }
  
  @Override
  public int hashCode() {
    return this.descricao.hashCode();
  }
  
  @Override
  public String toString() {
    return "{id: " + id + ", descricao: '" + descricao + "' }"; 
  }
}
