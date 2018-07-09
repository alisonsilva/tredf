package br.jus.tredf.justicanumeros.model.impressometro;

import java.util.Date;

public class ImpressoraDto {
	public int id;
	public String ip;
	public String modelo;
	public String serialNumber;
	public Date dtColeta;
	public int qtd;
	
	public ImpressoraDto(String ip, String modelo, String serialNumber, Date dtColeta, int qtd) {
		this.ip = ip;
		this.modelo = modelo;
		this.serialNumber = serialNumber;
		this.dtColeta = dtColeta;
		this.qtd = qtd;
	}
	
	@Override
	public String toString() {
		String ret = "{ip: '" + ip + "', "
				+ "modelo: '" + modelo + "', "
				+ "serialNumber: '" + serialNumber + "', "
				+ "dtColeta: '" + dtColeta + "', "
				+ "qtd: " + qtd + ""
				+ "}";
		return ret;
	}
}
