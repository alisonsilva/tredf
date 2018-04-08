package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="PRODUTIVIDADE_SERVENTIAS")
public class ProdutividadeServentias implements Serializable {
  private static final long serialVersionUID = 8165662994091749771L;

  @XmlAttribute private Long id;
  @XmlAttribute private Date dtPreenchimento;
  @XmlAttribute private Date dtCompetencia;  
  @XmlAttribute private String dtCompetenciaStr;
  @XmlAttribute private String dtPreenchimentoStr;
  @XmlAttribute private boolean fechado;
  
  private List<LogAcoesServentia> logAcoes = new ArrayList<LogAcoesServentia>();
  private List<Campo> campos = new ArrayList<Campo>();
  private Cartorio cartorio;
  
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public Date getDtPreenchimento() {
    return dtPreenchimento;
  }
  public void setDtPreenchimento(Date dtPreenchimento) {
    this.dtPreenchimento = dtPreenchimento;
    if (dtPreenchimento != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      this.dtPreenchimentoStr = sdf.format(dtPreenchimento);
    }
  }
  public Date getDtCompetencia() {
    return dtCompetencia;
  }
  public void setDtCompetencia(Date dtCompetencia) {
    this.dtCompetencia = dtCompetencia;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    this.dtCompetenciaStr = sdf.format(this.dtCompetencia);
  }
  public List<LogAcoesServentia> getLogAcoes() {
    return logAcoes;
  }
  public void setLogAcoes(List<LogAcoesServentia> logAcoes) {
    this.logAcoes = logAcoes;
  } 
  
  public List<Campo> getCampos() {
    return campos;
  }
  public void setCampos(List<Campo> campos) {
    this.campos = campos;
  }
  public Cartorio getCartorio() {
    return cartorio;
  }
  public void setCartorio(Cartorio cartorio) {
    this.cartorio = cartorio;
  }
  
  public String getDtCompetenciaStr() {
    return dtCompetenciaStr;
  }
  public void setDtCompetenciaStr(String dtCompetenciaStr) {
    this.dtCompetenciaStr = dtCompetenciaStr;
  } 
  
  public String getDtPreenchimentoStr() {
    return dtPreenchimentoStr;
  }
  public void setDtPreenchimentoStr(String dtPreenchimentoStr) {
    this.dtPreenchimentoStr = dtPreenchimentoStr;
  }
  public boolean isFechado() {
    return fechado;
  }
  public void setFechado(boolean fechado) {
    this.fechado = fechado;
  }
  @Override
  public String toString() {
    return "{id: " + id + " }";
  }
}
