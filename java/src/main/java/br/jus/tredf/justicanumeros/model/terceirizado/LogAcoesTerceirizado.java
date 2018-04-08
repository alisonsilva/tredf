package br.jus.tredf.justicanumeros.model.terceirizado;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="LOG_ACOES_TERCEIRIZADO")
public class LogAcoesTerceirizado implements Serializable {

  private static final long serialVersionUID = 8375263774438100797L;  

  @XmlAttribute private Long id;
  @XmlAttribute private String descricao;
  @XmlAttribute private Date dtAcao;
  @XmlAttribute private int acao;
  @XmlAttribute private Terceirizado terceirizado;
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
  public Date getDtAcao() {
    return dtAcao;
  }
  public void setDtAcao(Date dtAcao) {
    this.dtAcao = dtAcao;
  }
  public Terceirizado getTerceirizado() {
    return terceirizado;
  }
  public void setTerceirizado(Terceirizado terceirizado) {
    this.terceirizado = terceirizado;
  }
  public UsuarioVO getUsuario() {
    return usuario;
  }
  public void setUsuario(UsuarioVO usuario) {
    this.usuario = usuario;
  }  
  public int getAcao() {
    return acao;
  }
  public void setAcao(int acao) {
    this.acao = acao;
  }
  
  @Override
  public String toString() {
    return "{id: " + id + ", descricao: '" + descricao + "', acao: " + acao + ", dtAcao: " + dtAcao +
        ", usuario_id: " + usuario.id + ", terceirizado_id: " + terceirizado.getId() + "}";
  }
}
