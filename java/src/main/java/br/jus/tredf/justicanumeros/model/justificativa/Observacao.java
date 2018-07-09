package br.jus.tredf.justicanumeros.model.justificativa;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import br.jus.tredf.justicanumeros.model.Cartorio;

@XmlRootElement(name="OBSERVACAO")
@XmlAccessorType(XmlAccessType.FIELD)
public class Observacao implements Serializable {
  private static final long serialVersionUID = 1280033176347507121L;

  private Long id;
  private int codIndicador;
  private int grupoObservacao;
  private String nomeIndicador;
  private String processo;
  private String classe;
  private String dsClasse;
  private String sgClasse;
  private String assunto;
  private Date dtReferencia;
  private String dtReferenciaStr;
  private String protocolo;
  private String justificativa;
  private String resposta;
  private String categoria;
  private String siglaIndicador;
  private boolean flRegNovo;
  private Cartorio cartorio;
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public int getCodIndicador() {
    return codIndicador;
  }
  public void setCodIndicador(int codIndicador) {
    this.codIndicador = codIndicador;
  }
  public Date getDtReferencia() {
    return dtReferencia;
  }
  public void setDtReferencia(Date dtReferencia) {
    this.dtReferencia = dtReferencia;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    if (dtReferencia != null) {
      dtReferenciaStr = sdf.format(dtReferencia);
    }
  }
  public String getProtocolo() {
    return protocolo;
  }
  public void setProtocolo(String protocolo) {
    this.protocolo = protocolo;
  }
  public String getJustificativa() {
    return justificativa;
  }
  public void setJustificativa(String justificativa) {
    this.justificativa = justificativa;
  }  
  public String getNomeIndicador() {
    return nomeIndicador;
  }
  public void setNomeIndicador(String nomeIndicador) {
    this.nomeIndicador = nomeIndicador;
  }
  public boolean isFlRegNovo() {
    return flRegNovo;
  }
  public void setFlRegNovo(boolean flRegNovo) {
    this.flRegNovo = flRegNovo;
  }
  public String getSiglaIndicador() {
		return siglaIndicador;
	}
	public void setSiglaIndicador(String siglaIndicador) {
		this.siglaIndicador = siglaIndicador;
	}
	public Cartorio getCartorio() {
    return cartorio;
  }
  public void setCartorio(Cartorio cartorio) {
    this.cartorio = cartorio;
  }  
  
  public String getProcesso() {
		return processo;
	}
	public void setProcesso(String processo) {
		this.processo = processo;
	}
	public String getClasse() {
		return classe;
	}
	public void setClasse(String classe) {
		this.classe = classe;
	}
	public String getDsClasse() {
		return dsClasse;
	}
	public void setDsClasse(String dsClasse) {
		this.dsClasse = dsClasse;
	}
	public String getSgClasse() {
		return sgClasse;
	}
	public void setSgClasse(String sgClasse) {
		this.sgClasse = sgClasse;
	}
	public String getAssunto() {
		return assunto;
	}
	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}
	public String getResposta() {
		return resposta;
	}
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
	public String getDtReferenciaStr() {
    return dtReferenciaStr;
  }
  public void setDtReferenciaStr(String dtReferenciaStr) {
    this.dtReferenciaStr = dtReferenciaStr;
  }  
  public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}	
	public int getGrupoObservacao() {
		return grupoObservacao;
	}
	public void setGrupoObservacao(int grupoObservacao) {
		this.grupoObservacao = grupoObservacao;
	}
	public void setFlRegNovo(int fl) {
    if(fl == 1) {
      this.flRegNovo = true;
    } else {
      this.flRegNovo = false;
    }
  }
  
  @Override
  public String toString() {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    return "{id: " + id +", codIndicador: " + codIndicador + 
        ", dtReferencia: " + sdf.format(dtReferencia) + ", protocolo: '" + protocolo + 
        "', flRegNovo: " + flRegNovo + ", cartorio: " + cartorio.toString() + " }";
  }
}
