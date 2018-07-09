package br.jus.tredf.justicanumeros.model.impressometro;

import java.io.Serializable;

public class Impressora implements Serializable {

	private static final long serialVersionUID = -680906776662048784L;

	
	private int id;
	private String ipAddress;
	private String localizacao;
	private String detalhes;
	private boolean habilitado;
	private String serialNumber;
	private ImpressoraDto impDetail;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getLocalizacao() {
		return localizacao;
	}
	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	public String getDetalhes() {
		return detalhes;
	}
	public void setDetalhes(String detalhes) {
		this.detalhes = detalhes;
	}
	public boolean isHabilitado() {
		return habilitado;
	}
	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}	
	public ImpressoraDto getImpDetail() {
		return impDetail;
	}
	public void setImpDetail(ImpressoraDto impDetail) {
		this.impDetail = impDetail;
	}
	@Override
	public String toString() {
		String info = "{id: " + id + 
				", ipAddress: '" + ipAddress + 
				"', localizacao: '" + localizacao + 
				"', detalhes: '" + detalhes + 
				"', habilitado: " + habilitado +
				", serialNumber: '" + serialNumber +
				"'}";
		
		return info;
	}
}
