package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="PRODUTIVIDADE_MAGISTRADO")
public class ProdutividadeMagistrado implements Serializable {

  private static final long serialVersionUID = 6513469303440592913L;

  @XmlAttribute private Long id;
  @XmlAttribute private int sentCCMCrim1;
  @XmlAttribute private int sentCCMNCrim1;
  @XmlAttribute private int sentCSMCrim1;
  @XmlAttribute private int sentCSMNCrim1;
  @XmlAttribute private int sentH1;
  @XmlAttribute private int sentExtFisc1;
  @XmlAttribute private Date dtPreenchimento;
  @XmlAttribute private Date dtCompetencia;
  @XmlAttribute private Cartorio cartorio;
  @XmlAttribute private List<LogAcoesServentia> logAcoes;
  
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public int getSentCCMCrim1() {
    return sentCCMCrim1;
  }
  public void setSentCCMCrim1(int sentCCMCrim1) {
    this.sentCCMCrim1 = sentCCMCrim1;
  }
  public int getSentCCMNCrim1() {
    return sentCCMNCrim1;
  }
  public void setSentCCMNCrim1(int sentCCMNCrim1) {
    this.sentCCMNCrim1 = sentCCMNCrim1;
  }
  public int getSentCSMCrim1() {
    return sentCSMCrim1;
  }
  public void setSentCSMCrim1(int sentCSMCrim1) {
    this.sentCSMCrim1 = sentCSMCrim1;
  }
  public int getSentCSMNCrim1() {
    return sentCSMNCrim1;
  }
  public void setSentCSMNCrim1(int sentCSMNCrim1) {
    this.sentCSMNCrim1 = sentCSMNCrim1;
  }
  public int getSentH1() {
    return sentH1;
  }
  public void setSentH1(int sentH1) {
    this.sentH1 = sentH1;
  }
  public int getSentExtFisc1() {
    return sentExtFisc1;
  }
  public void setSentExtFisc1(int sentExtFisc1) {
    this.sentExtFisc1 = sentExtFisc1;
  }
  public Date getDtPreenchimento() {
    return dtPreenchimento;
  }
  public void setDtPreenchimento(Date dtPreenchimento) {
    this.dtPreenchimento = dtPreenchimento;
  }
  public Date getDtCompetencia() {
    return dtCompetencia;
  }
  public void setDtCompetencia(Date dtCompetencia) {
    this.dtCompetencia = dtCompetencia;
  }
  public Cartorio getCartorio() {
    return cartorio;
  }
  public void setCartorio(Cartorio cartorio) {
    this.cartorio = cartorio;
  }
  public List<LogAcoesServentia> getLogAcoes() {
    return logAcoes;
  }
  public void setLogAcoes(List<LogAcoesServentia> logAcoes) {
    this.logAcoes = logAcoes;
  }  
  
  public String toString() {
    return "{ id : " + id + ", sentCCMCrim1: " + sentCCMCrim1 
        + ", sentCCMNCrim1: " + sentCCMNCrim1 + ", sentCSMCrim1: " + sentCSMCrim1 + "}";
  }
}
