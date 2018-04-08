package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="protocolo_processo")
public class ProtocoloProcesso implements Serializable {

  private static final long serialVersionUID = 6375470424420363117L;

  @XmlAttribute private Long id;
  @XmlAttribute private String protocolo;
  @XmlAttribute private Campo campo;
  private ClasseSadp classe;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getProtocolo() {
    return protocolo;
  }
  public void setProtocolo(String protocolo) {
    this.protocolo = protocolo;
  }
  public Campo getCampo() {
    return campo;
  }
  public void setCampo(Campo campo) {
    this.campo = campo;
  }  

  public ClasseSadp getClasse() {
    return classe;
  }
  public void setClasse(ClasseSadp classe) {
    this.classe = classe;
  }
  @Override
  public String toString() {
    return "{id: " + id + ", protocolo: '" + protocolo +"'}";
  }
}
