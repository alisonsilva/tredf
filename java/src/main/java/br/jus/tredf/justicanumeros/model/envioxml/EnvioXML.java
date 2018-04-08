package br.jus.tredf.justicanumeros.model.envioxml;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="EnvioXML")
public class EnvioXML implements Serializable {
  private static final long serialVersionUID = 2798262303532312577L;

  @XmlAttribute private Long id;
  @XmlAttribute private Date dtComando;
  @XmlAttribute private String dtComandoStr;
  @XmlAttribute private boolean flEnviado;
  @XmlAttribute private Date dtEnvio;
  @XmlAttribute private String dtEnvioStr;
  @XmlAttribute private Date dtRefProc;
  @XmlAttribute private String dtRefProcStr;
  
  @XmlAttribute private int qtdProcessosEnviados;
  @XmlAttribute private int qtdProcessosNaoEnviados;
  
  private UsuarioVO usuario;
  private ControleCriacaoXml controleCriacao;
  private List<EnvioProcessoXML> processosEnviados = new ArrayList<EnvioProcessoXML>();
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public Date getDtComando() {
    return dtComando;
  }
  public void setDtComando(Date dtComando) {
    this.dtComando = dtComando;
    if(dtComando != null) {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	dtComandoStr = sdf.format(dtComando);
    }
  }
  public boolean isFlEnviado() {
    return flEnviado;
  }
  public void setFlEnviado(boolean flEnviado) {
    this.flEnviado = flEnviado;
  }
  public Date getDtEnvio() {
    return dtEnvio;
  }
  public void setDtEnvio(Date dtEnvio) {
    this.dtEnvio = dtEnvio;
    if(dtEnvio != null) {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	dtEnvioStr = sdf.format(dtEnvio);
    }
  }
  public Date getDtRefProc() {
    return dtRefProc;
  }
  public void setDtRefProc(Date dtRefProc) {
    this.dtRefProc = dtRefProc;
    if(dtRefProc != null) {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	dtRefProcStr = sdf.format(dtRefProc);
    }
  }  
  public List<EnvioProcessoXML> getProcessosEnviados() {
    return processosEnviados;
  }
  public void setProcessosEnviados(List<EnvioProcessoXML> processosEnviados) {
    this.processosEnviados = processosEnviados;
  }
  public UsuarioVO getUsuario() {
    return usuario;
  }
  public void setUsuario(UsuarioVO usuario) {
    this.usuario = usuario;
  } 
  
  public ControleCriacaoXml getControleCriacao() {
    return controleCriacao;
  }
  public void setControleCriacao(ControleCriacaoXml controleCriacao) {
    this.controleCriacao = controleCriacao;
  }
  
  public String getDtComandoStr() {
		return dtComandoStr;
	}
	public String getDtEnvioStr() {
		return dtEnvioStr;
	}
	public String getDtRefProcStr() {
		return dtRefProcStr;
	}	
	public int getQtdProcessosEnviados() {
		return qtdProcessosEnviados;
	}
	public void setQtdProcessosEnviados(int qtdProcessosEnviados) {
		this.qtdProcessosEnviados = qtdProcessosEnviados;
	}
	public int getQtdProcessosNaoEnviados() {
		return qtdProcessosNaoEnviados;
	}
	public void setQtdProcessosNaoEnviados(int qtdProcessosNaoEnviados) {
		this.qtdProcessosNaoEnviados = qtdProcessosNaoEnviados;
	}
	
	
	@Override
  public String toString() {
    return "{id: " + id + ", dtComando: '" + dtComando + "', flEnviado: " + flEnviado + 
        ", dtEnvio: '" + dtEnvio + "', dtRefProc: '" + dtRefProc + "' }";
  }
}
