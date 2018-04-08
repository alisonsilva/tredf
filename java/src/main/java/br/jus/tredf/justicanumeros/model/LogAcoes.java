package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="LOG_ACOES")
public class LogAcoes implements Serializable {
  private static final long serialVersionUID = 5989643524613496763L;

  @XmlAttribute private Long id;
  @XmlAttribute private String descricao;
  @XmlAttribute private Date dtAtualizacao;
  @XmlAttribute private Integer codAcao;
  private UsuarioVO usuario;
  
  
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
  public Date getDtAtualizacao() {
    return dtAtualizacao;
  }
  public void setDtAtualizacao(Date dtAtualizacao) {
    this.dtAtualizacao = dtAtualizacao;
  }
  public UsuarioVO getUsuario() {
    return usuario;
  }
  public void setUsuario(UsuarioVO usuario) {
    this.usuario = usuario;
  }
  
  public Integer getCodAcao() {
    return codAcao;
  }
  public void setCodAcao(Integer codAcao) {
    this.codAcao = codAcao;
  }
  
  @Override
  public String toString() {
    return "{id: +" + id + ", descricao : '" + descricao + "', dtAtualizacao: " + dtAtualizacao + ", usuario : " + usuario +"}";
  }
}
