package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;

/**
 * 
 * @author alison
 *
 */
public class VisualizacaoCartoriosInfo implements Serializable {

  private static final long serialVersionUID = -5865427624038152079L;

  private String nomeCartorio;
  private String sigla;
  private Long idCartorio;
  private Long idRegistro;
  private String dtCompetencia;
  private String dtFechamento;
  private int quantidade;
  private boolean expirado;
  private boolean fechado;
  
  
  @Override
  public String toString() {
    return "{idCartorio: " + idCartorio + ", nomeCartorio: '" + nomeCartorio + "'}";
  }


  public String getNomeCartorio() {
    return nomeCartorio;
  }


  public void setNomeCartorio(String nomeCartorio) {
    this.nomeCartorio = nomeCartorio;
  }


  public Long getIdCartorio() {
    return idCartorio;
  }


  public void setIdCartorio(Long idCartorio) {
    this.idCartorio = idCartorio;
  }


  public String getDtCompetencia() {
    return dtCompetencia;
  }


  public void setDtCompetencia(String dtCompetencia) {
    this.dtCompetencia = dtCompetencia;
  }


  public int getQuantidade() {
    return quantidade;
  }


  public void setQuantidade(int quantidade) {
    this.quantidade = quantidade;
  }


  public boolean isExpirado() {
    return expirado;
  }


  public void setExpirado(boolean expirado) {
    this.expirado = expirado;
  }


  public boolean isFechado() {
    return fechado;
  }


  public void setFechado(boolean fechado) {
    this.fechado = fechado;
  }


  public String getSiglaCartorio() {
    return sigla;
  }


  public void setSiglaCartorio(String sigla) {
    this.sigla = sigla;
  }


  public Long getIdRegistro() {
    return idRegistro;
  }


  public void setIdRegistro(Long idRegistro) {
    this.idRegistro = idRegistro;
  }


  public String getDtFechamento() {
    return dtFechamento;
  }


  public void setDtFechamento(String dtFechamento) {
    this.dtFechamento = dtFechamento;
  }


  
}
