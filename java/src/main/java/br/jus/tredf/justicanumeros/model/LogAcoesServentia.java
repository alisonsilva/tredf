package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="LOG_ACOES_SERVENTIA")
public class LogAcoesServentia implements Serializable {
  private static final long serialVersionUID = 785256566148840779L;

  @XmlAttribute private Long id;
  @XmlAttribute private String descricao;
  @XmlAttribute private Date dtAcao;
  @XmlAttribute private UsuarioVO usuario;
  @XmlAttribute private Long idServentia;
  @XmlAttribute private Long idProdMagistrado;
  
  
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
  public Date getDtAcao() {
    return dtAcao;
  }
  public void setDtAcao(Date dtAcao) {
    this.dtAcao = dtAcao;
  }
  public UsuarioVO getUsuario() {
    return usuario;
  }
  public void setUsuario(UsuarioVO usuario) {
    this.usuario = usuario;
  }
  
  public Long getIdServentia() {
    return idServentia;
  }
  public void setIdServentia(Long idServentia) {
    this.idServentia = idServentia;
  }  
  
  public Long getIdProdMagistrado() {
    return idProdMagistrado;
  }
  public void setIdProdMagistrado(Long idProdMagistrado) {
    this.idProdMagistrado = idProdMagistrado;
  }
  
  
  @Override
  public String toString() {
    return "{id: +" + id + ", descricao : '" + descricao + "', dtAcao: " + dtAcao + ", usuario : " + usuario +"}";
  }
}
