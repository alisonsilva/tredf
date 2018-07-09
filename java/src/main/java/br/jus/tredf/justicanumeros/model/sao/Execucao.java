package br.jus.tredf.justicanumeros.model.sao;

import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;


public class Execucao implements Serializable, IArquivo {

	private static final long serialVersionUID = 80632741772691097L;

	public static final String DESP_EMPENHADAS = "DESPESAS EMPENHADAS (CONTROLE EMPENHO)";
	public static final String DESP_LIQUIDADAS = "DESPESAS LIQUIDADAS (CONTROLE EMPENHO)";
	public static final String DESP_INSCR_RPNP = "DESPESAS INSCRITAS EM RPNP (CONTROLE EMPENHO)";
	public static final String DESP_PAGAS = "DESPESAS PAGAS (CONTROLE EMPENHO)";
	public static final String RESTOS_PAGAR_PROC_INSCRITOS = "RESTOS A PAGAR PROCESSADOS INSCRITOS";
	public static final String RESTOS_PAGAR_PROC_REINSC = "RESTOS A PAGAR PROCESSADOS REINSCRITOS";
	public static final String RESTOS_PAGAR_PROC_CANCEL = "RESTOS A PAGAR PROCESSADOS CANCELADOS";
	public static final String RESTOS_PAGAR_PROC_PAGOS = "RESTOS A PAGAR PROCESSADOS PAGOS";
	public static final String RESTOS_PAGAR_PROC_PAGAR = "RESTOS A PAGAR PROCESSADOS A PAGAR";
	public static final String RESTOS_PAGAR_NAO_PROC_INSCR = "RESTOS A PAGAR NAO PROCESSADOS INSCRITOS";
	public static final String RESTOS_PAGAR_NAO_PROC_REINSCR = "RESTOS A PAGAR NAO PROCESSADOS REINSCRITOS";
	public static final String RESTOS_PAGAR_NAO_PROC_CANCELADOS = "RESTOS A PAGAR NAO PROCESSADOS CANCELADOS";
	public static final String RESTOS_PAGAR_NAO_PROC_LIQUIDADOS = "RESTOS A PAGAR NAO PROCESSADOS LIQUIDADOS";
	public static final String RESTOS_PAGAR_NAO_PROC_PAGOS = "RESTOS A PAGAR NAO PROCESSADOS PAGOS";
	public static final String RESTOS_PAGAR_NAO_PROC_A_PAGAR = "RESTOS A PAGAR NAO PROCESSADOS A PAGAR";	
	public static final String DOTACAO_INICIAL = "DOTACAO INICIAL";
	public static final String DOTACAO_ATUALIZADA = "DOTACAO ATUALIZADA";
	public static final String PROVISAO_RECEBIDA = "PROVISAO RECEBIDA";
	public static final String PROVISAO_CONCEDIDA = "PROVISAO CONCEDIDA";
	public static final String CREDITO_DISPONIVEL = "CREDITO DISPONIVEL";
	public static final String CREDITO_INDISPONIVEL = "CREDITO INDISPONIVEL";
	public static final String DESTAQUE_RECEBIDO = "DESTAQUE RECEBIDO";
	public static final String DESTAQUE_CONCEDIDO = "DESTAQUE CONCEDIDO";
	public static final String DESP_PRE_EMP_A_EMPENHAR = "DESPESAS PRE-EMPENHADAS A EMPENHAR";
	public static final String LIQUIDACOES_TOTAIS = "LIQUIDACOES TOTAIS (EXERCICIO E RPNP)";
	public static final String PAGAMENTOS_TOTAIS = "PAGAMENTOS TOTAIS (EXERCICIO E RAP)";
	
	
	private long id;
	private long formularioExecucaoId;
	private String unidadeOrc;
	private String unidadeOrcDesc;
	private String ugExecutivos;
	private String ugExecutivosDesc;
	private String pt;
	private String acaoGoverno;
	private String acaoGovernoDesc;
	private String planoOrc;
	private String planoOrcDesc;
	private String ptres;
	private String pi;
	private String piDesc;
	private String fonteSOF;
	private String fonteRecursos;
	private String esferaOrc;
	private String esferaOrcDesc;
	private int categoriaEconomicaDespesaId;
	private String categoriaEconomicaDespesa;
	private int grupoDespesasId;
	private String grupoDespesas;
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
	private String diaEmissaoNeCcor;
	private String notaEmpenhoCcor;
	private String numeroProcessoNeCcor;
	private String docObservacao;
	private String favorecidoNeCcorId;
	private String favorecidoNeCcor;
	private String itemFormacao;
	private Double despesasEmpenhadas = 0.0;
	private Double despesasLiquidadas = 0.0;
	private Double despesasInscRpnp = 0.0;
	private Double despesasPagas = 0.0;
	private Double restosPagarProcReinsc = 0.0;
	private Double restosPagarProcPagar = 0.0;
	private Double restosPagarNaoProcessadosInscritos = 0.0;
	private Double restosPagarNaoProcessadosReinscritos = 0.0;
	private Double restosPagarNaoProcessadosCancelados = 0.0;
	private Double restosPagarNaoProcessadosLiquitados = 0.0;
	private Double restosPagarNaoProcessadosPagos = 0.0;
	private Double restosPagarNaoprocessadosAPagar = 0.0;	
	private Double restosPagarProcCancelados = 0.0;
	private Double restosPagarProcPagos = 0.0; 
	private Double restosPagarProcInscritos = 0.0;
	private Double dotacaoInicial = 0.0;
	private Double dotacaoAtualizada = 0.0;
	private Double provisaoRecebida = 0.0;
	private Double provisaoConcedida = 0.0;
	private Double destaqueRecebido = 0.0;
	private Double destaqueConcedido = 0.0;
	private Double creditoDisponivel = 0.0;
	private Double creditoIndisponivel = 0.0;
	private Double despPreEmpAEmpenhar = 0.0;
	private Double liquidacoesTotais = 0.0;
	private Double pagamentosTotais = 0.0;

	
	@Override
	public String toString() {
		return "{dotacaoInicial: " + dotacaoInicial + 
				", dotacaoAtualizada: " + dotacaoAtualizada +
				", provisaoRecebida: " + provisaoRecebida +
				", provisaoConcedida: " + provisaoConcedida +
				", destaqueRecebido: " + destaqueRecebido +
				", destaqueConcedido: " + destaqueConcedido +
				", creditoDisponivel: " + creditoDisponivel +
				", creditoIndisponivel: " + creditoIndisponivel +
				", despPreEmpAEmpenhar: " + despPreEmpAEmpenhar +
				", liquidacoesTotais: " + liquidacoesTotais +
				", pagamentosTotais: " + pagamentosTotais +
				", despesasEmpenhadas: " + despesasEmpenhadas +
				", despesasLiquidadas: " + despesasLiquidadas +
				", despesasInscRpnp: " + despesasInscRpnp +
				", despesasPagas: " + despesasPagas +
				", restosPagarProcReinsc: " + restosPagarProcReinsc +
				", restosPagarProcPagar: " + restosPagarProcPagar +
				", restosPagarNaoProcessadosInscritos: " + restosPagarNaoProcessadosInscritos +
				", restosPagarNaoProcessadosReinscritos: " + restosPagarNaoProcessadosReinscritos +
				", restosPagarNaoProcessadosCancelados: " + restosPagarNaoProcessadosCancelados +
				", restosPagarNaoProcessadosLiquitados: " + restosPagarNaoProcessadosLiquitados +
				", restosPagarNaoProcessadosPagos: " + restosPagarNaoProcessadosPagos +
				", restosPagarNaoprocessadosAPagar: " + restosPagarNaoprocessadosAPagar +
				", restosPagarProcCancelados: " + restosPagarProcCancelados +
				", restosPagarProcPagos: " + restosPagarProcPagos +
				", restosPagarProcInscritos: " + restosPagarProcInscritos + "}";
	}
	
	
	public void preencheCampos(ItemExecucao itemExecucao, ResourceBundle bundleExecucao) {
		String[] colunas =  StringUtils.splitPreserveAllTokens(bundleExecucao.getString("colunas"), ";");
		Map<String, ItemExecucao> campos = itemExecucao.getCampos();
		
		for(String coluna : colunas) {
			if(campos.containsKey(coluna)) {
				ItemExecucao item = campos.get(coluna);
				if(item.presente) {
					switch(coluna) {
						case DESP_EMPENHADAS:
							this.setDespesasEmpenhadas(item.valor);	break;
						case DESP_LIQUIDADAS:
							this.setDespesasLiquidadas(item.valor);	break;
						case DESP_INSCR_RPNP:
							this.setDespesasInscRpnp(item.valor);	break;
						case DESP_PAGAS:
							this.setDespesasPagas(item.valor); break;
						case RESTOS_PAGAR_PROC_INSCRITOS:
							this.setRestosPagarProcInscritos(item.valor); break;
						case RESTOS_PAGAR_PROC_REINSC:
							this.setRestosPagarProcReinsc(item.valor); break;
						case RESTOS_PAGAR_PROC_CANCEL:
							this.setRestosPagarProcCancelados(item.valor); break;
						case RESTOS_PAGAR_PROC_PAGOS:
							this.setRestosPagarProcPagos(item.valor); break;
						case RESTOS_PAGAR_PROC_PAGAR:
							this.setRestosPagarProcPagar(item.valor); break;
						case RESTOS_PAGAR_NAO_PROC_INSCR:
							this.setRestosPagarNaoProcessadosInscritos(item.valor); break;
						case RESTOS_PAGAR_NAO_PROC_REINSCR:
							this.setRestosPagarNaoProcessadosReinscritos(item.valor); break;
						case RESTOS_PAGAR_NAO_PROC_CANCELADOS:
							this.setRestosPagarNaoProcessadosCancelados(item.valor); break;
						case RESTOS_PAGAR_NAO_PROC_LIQUIDADOS:
							this.setRestosPagarNaoProcessadosLiquitados(item.valor); break;
						case RESTOS_PAGAR_NAO_PROC_PAGOS:
							this.setRestosPagarNaoProcessadosPagos(item.valor); break;
						case RESTOS_PAGAR_NAO_PROC_A_PAGAR:
							this.setRestosPagarNaoProcessadosAPagar(item.valor); break;
						case DOTACAO_INICIAL:
							this.setDotacaoInicial(item.valor); break;
						case DOTACAO_ATUALIZADA:
							this.setDotacaoAtualizada(item.valor); break;
						case PROVISAO_RECEBIDA:
							this.setProvisaoRecebida(item.valor); break;
						case PROVISAO_CONCEDIDA:
							this.setProvisaoConcedida(item.valor); break;
						case DESTAQUE_RECEBIDO:
							this.setDestaqueRecebido(item.valor); break;
						case DESTAQUE_CONCEDIDO:
							this.setDestaqueConcedido(item.valor); break;
						case CREDITO_DISPONIVEL:
							this.setCreditoDisponivel(item.valor); break;
						case CREDITO_INDISPONIVEL:
							this.setCreditoIndisponivel(item.valor); break;
						case DESP_PRE_EMP_A_EMPENHAR:
							this.setDespPreEmpAEmpenhar(item.valor); break;
						case LIQUIDACOES_TOTAIS:
							this.setLiquidacoesTotais(item.valor); break;
						case PAGAMENTOS_TOTAIS:
							this.setPagamentosTotais(item.valor); break;
					}
				}
			}
		}
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
	public String getUnidadeOrc() {
		return unidadeOrc;
	}
	public void setUnidadeOrc(String unidadeOrc) {
		this.unidadeOrc = unidadeOrc;
	}
	public String getUnidadeOrcDesc() {
		return unidadeOrcDesc;
	}
	public void setUnidadeOrcDesc(String unidadeOrcDesc) {
		this.unidadeOrcDesc = unidadeOrcDesc;
	}
	public String getUgExecutivos() {
		return ugExecutivos;
	}
	public void setUgExecutivos(String ugExecutivos) {
		this.ugExecutivos = ugExecutivos;
	}
	public String getUgExecutivosDesc() {
		return ugExecutivosDesc;
	}
	public void setUgExecutivosDesc(String ugExecutivosDesc) {
		this.ugExecutivosDesc = ugExecutivosDesc;
	}
	public String getAcaoGovernoDesc() {
		return acaoGovernoDesc;
	}
	public void setAcaoGovernoDesc(String acaoGovernoDesc) {
		this.acaoGovernoDesc = acaoGovernoDesc;
	}
	public String getPlanoOrc() {
		return planoOrc;
	}
	public void setPlanoOrc(String planoOrc) {
		this.planoOrc = planoOrc;
	}
	public String getPlanoOrcDesc() {
		return planoOrcDesc;
	}
	public void setPlanoOrcDesc(String planoOrcDesc) {
		this.planoOrcDesc = planoOrcDesc;
	}
	public String getPiDesc() {
		return piDesc;
	}
	public void setPiDesc(String piDesc) {
		this.piDesc = piDesc;
	}
	public String getFonteRecursos() {
		return fonteRecursos;
	}
	public void setFonteRecursos(String fonteRecursos) {
		this.fonteRecursos = fonteRecursos;
	}
	public String getEsferaOrc() {
		return esferaOrc;
	}
	public void setEsferaOrc(String esferaOrc) {
		this.esferaOrc = esferaOrc;
	}
	public String getEsferaOrcDesc() {
		return esferaOrcDesc;
	}
	public void setEsferaOrcDesc(String esferaOrcDesc) {
		this.esferaOrcDesc = esferaOrcDesc;
	}
	public String getDiaEmissaoNeCcor() {
		return diaEmissaoNeCcor;
	}
	public void setDiaEmissaoNeCcor(String diaEmissaoNeCcor) {
		this.diaEmissaoNeCcor = diaEmissaoNeCcor;
	}
	public String getDocObservacao() {
		return docObservacao;
	}
	public void setDocObservacao(String docObservacao) {
		this.docObservacao = docObservacao;
	}
	public String getFavorecidoNeCcorId() {
		return favorecidoNeCcorId;
	}
	public void setFavorecidoNeCcorId(String favorecidoNeCcorId) {
		this.favorecidoNeCcorId = favorecidoNeCcorId;
	}
	public Double getDespesasInscRpnp() {
		return despesasInscRpnp;
	}
	public void setDespesasInscRpnp(Double despesasInscRpnp) {
		this.despesasInscRpnp = despesasInscRpnp;
	}
	public Double getRestosPagarNaoprocessadosAPagar() {
		return restosPagarNaoprocessadosAPagar;
	}
	public void setRestosPagarNaoprocessadosAPagar(Double restosPagarNaoprocessadosAPagar) {
		this.restosPagarNaoprocessadosAPagar = restosPagarNaoprocessadosAPagar;
	}
	public Double getRestosPagarProcCancelados() {
		return restosPagarProcCancelados;
	}
	public void setRestosPagarProcCancelados(Double restosPagarProcCancelados) {
		this.restosPagarProcCancelados = restosPagarProcCancelados;
	}
	public Double getRestosPagarProcPagos() {
		return restosPagarProcPagos;
	}
	public void setRestosPagarProcPagos(Double restosPagarProcPagos) {
		this.restosPagarProcPagos = restosPagarProcPagos;
	}
	public Double getRestosPagarProcInscritos() {
		return restosPagarProcInscritos;
	}
	public void setRestosPagarProcInscritos(Double restosPagarProcInscritos) {
		this.restosPagarProcInscritos = restosPagarProcInscritos;
	}


	public Double getDotacaoInicial() {
		return dotacaoInicial;
	}
	public void setDotacaoInicial(Double dotacaoInicial) {
		this.dotacaoInicial = dotacaoInicial;
	}
	public Double getDotacaoAtualizada() {
		return dotacaoAtualizada;
	}
	public void setDotacaoAtualizada(Double dotacaoAtualizada) {
		this.dotacaoAtualizada = dotacaoAtualizada;
	}
	public Double getProvisaoRecebida() {
		return provisaoRecebida;
	}
	public void setProvisaoRecebida(Double provisaoRecebida) {
		this.provisaoRecebida = provisaoRecebida;
	}
	public Double getProvisaoConcedida() {
		return provisaoConcedida;
	}
	public void setProvisaoConcedida(Double provisaoConcedida) {
		this.provisaoConcedida = provisaoConcedida;
	}
	public Double getDestaqueRecebido() {
		return destaqueRecebido;
	}
	public void setDestaqueRecebido(Double destaqueRecebido) {
		this.destaqueRecebido = destaqueRecebido;
	}
	public Double getDestaqueConcedido() {
		return destaqueConcedido;
	}
	public void setDestaqueConcedido(Double destaqueConcedido) {
		this.destaqueConcedido = destaqueConcedido;
	}
	public Double getCreditoDisponivel() {
		return creditoDisponivel;
	}
	public void setCreditoDisponivel(Double creditoDisponivel) {
		this.creditoDisponivel = creditoDisponivel;
	}
	public Double getCreditoIndisponivel() {
		return creditoIndisponivel;
	}
	public void setCreditoIndisponivel(Double creditoIndisponivel) {
		this.creditoIndisponivel = creditoIndisponivel;
	}
	public Double getDespPreEmpAEmpenhar() {
		return despPreEmpAEmpenhar;
	}
	public void setDespPreEmpAEmpenhar(Double despPreEmpAEmpenhar) {
		this.despPreEmpAEmpenhar = despPreEmpAEmpenhar;
	}


	public Double getLiquidacoesTotais() {
		return liquidacoesTotais;
	}


	public void setLiquidacoesTotais(Double liquidacoesTotais) {
		this.liquidacoesTotais = liquidacoesTotais;
	}


	public Double getPagamentosTotais() {
		return pagamentosTotais;
	}


	public void setPagamentosTotais(Double pagamentosTotais) {
		this.pagamentosTotais = pagamentosTotais;
	}	
}

