package br.jus.tredf.justicanumeros.model.envioxml;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="EnvioProcessoXML")
public class EnvioProcessoXML implements Serializable {
  private static final long serialVersionUID = -9036437886991481498L;

  private Long id;
  private String numero;
  private Integer competencia;
  private String classeProcessual;
  private Integer codLocalidade;
  private Integer nivelSigilo;
  private Integer intervencaoMp;
  private Integer tamanhoProcesso;
  private Date dtAjuizamento;
  private boolean flEnviado;
  private String infoEnvio;
  private EnvioXML envioXml;
  private String elementoXml;
  
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getNumero() {
    return numero;
  }
  public void setNumero(String numero) {
    this.numero = numero;
  }
  public Integer getCompetencia() {
    return competencia;
  }
  public void setCompetencia(Integer competencia) {
    this.competencia = competencia;
  }
  public String getClasseProcessual() {
    return classeProcessual;
  }
  public void setClasseProcessual(String classeProcessual) {
    this.classeProcessual = classeProcessual;
  }
  public Integer getCodLocalidade() {
    return codLocalidade;
  }
  public void setCodLocalidade(Integer codLocalidade) {
    this.codLocalidade = codLocalidade;
  }
  public Integer getNivelSigilo() {
    return nivelSigilo;
  }
  public void setNivelSigilo(Integer nivelSigilo) {
    this.nivelSigilo = nivelSigilo;
  }
  public Integer getIntervencaoMp() {
    return intervencaoMp;
  }
  public void setIntervencaoMp(Integer intervencaoMp) {
    this.intervencaoMp = intervencaoMp;
  }
  public Integer getTamanhoProcesso() {
    return tamanhoProcesso;
  }
  public void setTamanhoProcesso(Integer tamanhoProcesso) {
    this.tamanhoProcesso = tamanhoProcesso;
  }
  public Date getDtAjuizamento() {
    return dtAjuizamento;
  }
  public void setDtAjuizamento(Date dtAjuizamento) {
    this.dtAjuizamento = dtAjuizamento;
  }
  public boolean isFlEnviado() {
    return flEnviado;
  }
  public void setFlEnviado(boolean flEnviado) {
    this.flEnviado = flEnviado;
  }
  public String getInfoEnvio() {
    return infoEnvio;
  }
  public void setInfoEnvio(String infoEnvio) {
    this.infoEnvio = infoEnvio;
  }
  public EnvioXML getEnvioXml() {
    return envioXml;
  }
  public void setEnvioXml(EnvioXML envio) {
    this.envioXml = envio;
  }
  
  public String getElementoXml() {
    return elementoXml;
  }
  public void setElementoXml(String elementoXml) {
    this.elementoXml = elementoXml;
  }
  @Override
  public String toString() {
    return "{id: " + id + ", numero: '" + numero + "' }";
  }
}
