package br.jus.tredf.justicanumeros.model.envioxml;

import java.io.Serializable;


public class ControleCriacaoXml implements Serializable {
  private static final long serialVersionUID = 2166881359320409654L;

  private Long id;
  private String competencia;
  private int instancia;
  private long usuarioId;
  
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getCompetencia() {
    return competencia;
  }
  public void setCompetencia(String competencia) {
    this.competencia = competencia;
  }
  public int getInstancia() {
    return instancia;
  }
  public void setInstancia(int instancia) {
    this.instancia = instancia;
  }
  public long getUsuarioId() {
    return usuarioId;
  }
  public void setUsuarioId(long usuarioId) {
    this.usuarioId = usuarioId;
  }
  
  @Override
  public String toString(){
    return "{id: " + id +", competencia: '" + competencia +"', instancia: " + instancia +", usuarioId: " +usuarioId + "}";
  }
}
