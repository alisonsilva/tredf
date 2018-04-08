package br.jus.tredf.justicanumeros.model.sao;

import java.io.Serializable;

public class Execucao implements Serializable {

	private static final long serialVersionUID = 80632741772691097L;

	private long id;
	private long formularioExecucaoId;
	private String pt;
	private String acaoGoverno;
	private String ptres;
	private int categoriaEconomicaDespesaId;
	private String categoriaEconomicaDespesa;
	private int grupoDespesasId;
	private String grupoDespesas;
	private String fonteSOF;
	private String pi;
	private String elementoDespesaId;
	private String elementoDespesa;
	private String naturezaDespesaId;
	private String naturezaDespesa;
	private String naturezaDespesaDetalhadaId;
	private String naturezaDespesaDetalhada;
	private String tipoNeCCorId;
	private String tipoNeCCor;
	private String modalidadeLicitacaoNeCcorId;
	private String modalidadeLicitacaoNeCcor;
	private String notaEmpenhoCcor;
	private String numeroProcessoNeCcor;
	private String favorecidoNeCcor;
	private String itemFormacao;
	private Double despesasEmpenhadas;
	private Double despesasLiquidadas;
	private Double despesasPagas;
	private Double restosPagarProcReinsc;
	private Double restosPagarProcPagar;
	private Double restosPagarNaoProcessadosInscritos;
	private Double restosPagarNaoProcessadosReinscritos;
	private Double restosPagarNaoProcessadosCancelados;
	private Double restosPagarNaoProcessadosLiquitados;
	private Double restosPagarNaoProcessadosPagos;
	private Double restosPagarNaoprocessadosAPagar;
	
	@Override
	public String toString() {
		return "{id: '" + id + "', \n"
				+ "formularioExecucaoId: '" + formularioExecucaoId + "', \n"
				+ "pt: '" + pt  + "', \n"
				+ "acaoGoverno: '" + acaoGoverno  + "', \n"
				+ "ptres: '" + ptres  + "', \n"
				+ "categoriaEconomicaDespesaId: '" + categoriaEconomicaDespesaId  + "', \n"
				+ "categoriaEconomicaDespesa: '" + categoriaEconomicaDespesa  + "', \n"
				+ "grupoDespesaId: '" + grupoDespesasId  + "', \n"
				+ "grupoDespesas: '" + grupoDespesas + "', \n"
				+ "fonteSOF: '" + fonteSOF  + "', \n"
				+ "pi: '" + pi  + "', \n"
				+ "elementoDespesaId: '" + elementoDespesaId + "', \n"
				+ "elementoDespesa: '" + elementoDespesa  + "', \n"
				+ "naturezaDespesaId: '" + naturezaDespesaId  + "', \n"
				+ "naturezaDespesa: '" + naturezaDespesa  + "', \n"
				+ "naturezaDespesaDetalhadaId: '" + naturezaDespesaDetalhadaId  + "', \n"
				+ "naturezaDespesaDetalhada: '" + naturezaDespesaDetalhada  + "', \n"
				+ "tipoNeCCorId: '" + tipoNeCCorId  + "', \n"
				+ "tipoNeCCor: '" + tipoNeCCor + "', \n"
				+ "modalidadeLicitacaoNeCcorId: '" + modalidadeLicitacaoNeCcorId  + "', \n"
				+ "modalidadeLicitacaoNeCcor: '" + modalidadeLicitacaoNeCcor  + "', \n"
				+ "notaEmpenhoCcor: '" + notaEmpenhoCcor  + "', \n"
				+ "numeroProcessoNeCcor: '" + numeroProcessoNeCcor  + "', \n"
				+ "favorecidoNeCcor: '" + favorecidoNeCcor  + "', \n"
				+ "itemFormacao: '" + itemFormacao  + "', \n"
				+ "despesasEmpenhadas: '" + despesasEmpenhadas  + "', \n"
				+ "despesasLiquidadas: '" + despesasLiquidadas  + "', \n"
				+ "despesasPagas: '" + despesasPagas  + "', \n"
				+ "restosPagarProcReinsc: '" + restosPagarProcReinsc  + "', \n"
				+ "restosPagarProcPagar: '" + restosPagarProcPagar  + "', \n"
				+ "restosPagarNaoProcessadosInscritos: '" + restosPagarNaoProcessadosInscritos  + "', \n"
				+ "restosPagarNaoProcessadosReinscritos: '" + restosPagarNaoProcessadosReinscritos  + "', \n"
				+ "restosPagarNaoProcessadosCancelados: '" + restosPagarNaoProcessadosCancelados  + "', \n"
				+ "restosPagarNaoProcessadosLiquidados: '" + restosPagarNaoProcessadosLiquitados  + "', \n"
				+ "restosPagarNaoProcessadosPagos: '" + restosPagarNaoProcessadosPagos  + "', \n"
				+ "restosPagarNaoPrcessadosAPagar: '" + restosPagarNaoprocessadosAPagar + "', \n"
				+ "}";
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getFormularioExecucaoId() {
		return formularioExecucaoId;
	}
	public void setFormularioExecucaoId(long formularioExecucaoId) {
		this.formularioExecucaoId = formularioExecucaoId;
	}
	public String getPt() {
		return pt;
	}
	public void setPt(String pt) {
		this.pt = pt;
	}
	public String getAcaoGoverno() {
		return acaoGoverno;
	}
	public void setAcaoGoverno(String acaoGoverno) {
		this.acaoGoverno = acaoGoverno;
	}
	public String getPtres() {
		return ptres;
	}
	public void setPtres(String ptres) {
		this.ptres = ptres;
	}
	public int getCategoriaEconomicaDespesaId() {
		return categoriaEconomicaDespesaId;
	}
	public void setCategoriaEconomicaDespesaId(int categoriaEconomicaDespesaId) {
		this.categoriaEconomicaDespesaId = categoriaEconomicaDespesaId;
	}
	public String getCategoriaEconomicaDespesa() {
		return categoriaEconomicaDespesa;
	}
	public void setCategoriaEconomicaDespesa(String categoriaEconomicaDespesa) {
		this.categoriaEconomicaDespesa = categoriaEconomicaDespesa;
	}
	public int getGrupoDespesasId() {
		return grupoDespesasId;
	}
	public void setGrupoDespesasId(int grupoDespesasId) {
		this.grupoDespesasId = grupoDespesasId;
	}
	public String getGrupoDespesas() {
		return grupoDespesas;
	}
	public void setGrupoDespesas(String grupoDespesas) {
		this.grupoDespesas = grupoDespesas;
	}
	public String getFonteSOF() {
		return fonteSOF;
	}
	public void setFonteSOF(String fonteSOF) {
		this.fonteSOF = fonteSOF;
	}
	public String getPi() {
		return pi;
	}
	public void setPi(String pi) {
		this.pi = pi;
	}
	public String getElementoDespesaId() {
		return elementoDespesaId;
	}
	public void setElementoDespesaId(String elementoDespesaId) {
		this.elementoDespesaId = elementoDespesaId;
	}
	public String getElementoDespesa() {
		return elementoDespesa;
	}
	public void setElementoDespesa(String elementoDespesa) {
		this.elementoDespesa = elementoDespesa;
	}
	public String getNaturezaDespesaId() {
		return naturezaDespesaId;
	}
	public void setNaturezaDespesaId(String naturezaDespesaId) {
		this.naturezaDespesaId = naturezaDespesaId;
	}
	public String getNaturezaDespesa() {
		return naturezaDespesa;
	}
	public void setNaturezaDespesa(String naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}
	public String getNaturezaDespesaDetalhadaId() {
		return naturezaDespesaDetalhadaId;
	}
	public void setNaturezaDespesaDetalhadaId(String naturezaDespesaDetalhadaId) {
		this.naturezaDespesaDetalhadaId = naturezaDespesaDetalhadaId;
	}
	public String getNaturezaDespesaDetalhada() {
		return naturezaDespesaDetalhada;
	}
	public void setNaturezaDespesaDetalhada(String naturezaDespesaDetalhada) {
		this.naturezaDespesaDetalhada = naturezaDespesaDetalhada;
	}
	public String getTipoNeCCorId() {
		return tipoNeCCorId;
	}
	public void setTipoNeCCorId(String tipoNeCCorId) {
		this.tipoNeCCorId = tipoNeCCorId;
	}
	public String getTipoNeCCor() {
		return tipoNeCCor;
	}
	public void setTipoNeCCor(String tipoNeCCor) {
		this.tipoNeCCor = tipoNeCCor;
	}
	public String getModalidadeLicitacaoNeCcorId() {
		return modalidadeLicitacaoNeCcorId;
	}
	public void setModalidadeLicitacaoNeCcorId(String modalidadeLicitacaoNeCcorId) {
		this.modalidadeLicitacaoNeCcorId = modalidadeLicitacaoNeCcorId;
	}
	public String getModalidadeLicitacaoNeCcor() {
		return modalidadeLicitacaoNeCcor;
	}
	public void setModalidadeLicitacaoNeCcor(String modalidadeLicitacaoNeCcor) {
		this.modalidadeLicitacaoNeCcor = modalidadeLicitacaoNeCcor;
	}
	public String getNotaEmpenhoCcor() {
		return notaEmpenhoCcor;
	}
	public void setNotaEmpenhoCcor(String notaEmpenhoCcor) {
		this.notaEmpenhoCcor = notaEmpenhoCcor;
	}
	public String getNumeroProcessoNeCcor() {
		return numeroProcessoNeCcor;
	}
	public void setNumeroProcessoNeCcor(String numeroProcessoNeCcor) {
		this.numeroProcessoNeCcor = numeroProcessoNeCcor;
	}
	public String getFavorecidoNeCcor() {
		return favorecidoNeCcor;
	}
	public void setFavorecidoNeCcor(String favorecidoNeCcor) {
		this.favorecidoNeCcor = favorecidoNeCcor;
	}
	public String getItemFormacao() {
		return itemFormacao;
	}
	public void setItemFormacao(String itemFormacao) {
		this.itemFormacao = itemFormacao;
	}
	public Double getDespesasEmpenhadas() {
		return despesasEmpenhadas;
	}
	public void setDespesasEmpenhadas(Double despesasEmpenhadas) {
		this.despesasEmpenhadas = despesasEmpenhadas;
	}
	public Double getDespesasLiquidadas() {
		return despesasLiquidadas;
	}
	public void setDespesasLiquidadas(Double despesasLiquidadas) {
		this.despesasLiquidadas = despesasLiquidadas;
	}
	public Double getDespesasPagas() {
		return despesasPagas;
	}
	public void setDespesasPagas(Double despesasPagas) {
		this.despesasPagas = despesasPagas;
	}
	public Double getRestosPagarProcReinsc() {
		return restosPagarProcReinsc;
	}
	public void setRestosPagarProcReinsc(Double restosPagarProcReinsc) {
		this.restosPagarProcReinsc = restosPagarProcReinsc;
	}
	public Double getRestosPagarProcPagar() {
		return restosPagarProcPagar;
	}
	public void setRestosPagarProcPagar(Double restosPagarProcPagar) {
		this.restosPagarProcPagar = restosPagarProcPagar;
	}
	public Double getRestosPagarNaoProcessadosInscritos() {
		return restosPagarNaoProcessadosInscritos;
	}
	public void setRestosPagarNaoProcessadosInscritos(Double restosPagarNaoProcessadosInscritos) {
		this.restosPagarNaoProcessadosInscritos = restosPagarNaoProcessadosInscritos;
	}
	public Double getRestosPagarNaoProcessadosReinscritos() {
		return restosPagarNaoProcessadosReinscritos;
	}
	public void setRestosPagarNaoProcessadosReinscritos(Double restosPagarNaoProcessadosReinscritos) {
		this.restosPagarNaoProcessadosReinscritos = restosPagarNaoProcessadosReinscritos;
	}
	public Double getRestosPagarNaoProcessadosCancelados() {
		return restosPagarNaoProcessadosCancelados;
	}
	public void setRestosPagarNaoProcessadosCancelados(Double restosPagarNaoProcessadosCancelados) {
		this.restosPagarNaoProcessadosCancelados = restosPagarNaoProcessadosCancelados;
	}
	public Double getRestosPagarNaoProcessadosLiquitados() {
		return restosPagarNaoProcessadosLiquitados;
	}
	public void setRestosPagarNaoProcessadosLiquitados(Double restosPagarNaoProcessadosLiquitados) {
		this.restosPagarNaoProcessadosLiquitados = restosPagarNaoProcessadosLiquitados;
	}
	public Double getRestosPagarNaoProcessadosPagos() {
		return restosPagarNaoProcessadosPagos;
	}
	public void setRestosPagarNaoProcessadosPagos(Double restosPagarNaoProcessadosPagos) {
		this.restosPagarNaoProcessadosPagos = restosPagarNaoProcessadosPagos;
	}
	public Double getRestosPagarNaoProcessadosAPagar() {
		return restosPagarNaoprocessadosAPagar;
	}
	public void setRestosPagarNaoProcessadosAPagar(Double restosPagarNaoprocessadosAPagar) {
		this.restosPagarNaoprocessadosAPagar = restosPagarNaoprocessadosAPagar;
	}
	
	
}
