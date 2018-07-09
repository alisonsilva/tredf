package br.jus.tredf.justicanumeros.model.impressometro;

import java.io.Serializable;
import java.util.Date;

public class HistoricoHabilitado implements Serializable {
	private static final long serialVersionUID = 1755691829102091416L;
	
	private long id;
	private Date dtMudanca;
	private int codMudanca;
	private Impressora impressora;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDtMudanca() {
		return dtMudanca;
	}
	public void setDtMudanca(Date dtMudanca) {
		this.dtMudanca = dtMudanca;
	}
	public int getCodMudanca() {
		return codMudanca;
	}
	public void setCodMudanca(int codMudanca) {
		this.codMudanca = codMudanca;
	}
	public Impressora getImpressora() {
		return impressora;
	}
	public void setImpressora(Impressora impressora) {
		this.impressora = impressora;
	}
	
	@Override
	public String toString() {
		String ret = "{id: " + id + 
				", dtMudanca: "  + dtMudanca +
				", codMudanca: " + codMudanca +
				", impressora: " + (impressora != null ? impressora.toString() : "") + 
				"}";
		return ret;
	}

}
