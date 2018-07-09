package br.jus.tredf.justicanumeros.model.sao;

import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;


public class Dotacao implements Serializable {

	private static final long serialVersionUID = 80632741772691097L;

	public static final String DOTACAO_INICIAL = "DOTACAO INICIAL";
	public static final String DOTACAO_ATUALIZADA = "DOTACAO ATUALIZADA";
	public static final String PROVISAO_RECEBIDA = "PROVISAO RECEBIDA";
	public static final String PROVISAO_CONCEDIDA = "PROVISAO CONCEDIDA";
	public static final String CREDITO_DISPONIVEL = "CREDITO DISPONIVEL";
	public static final String CREDITO_INDISPONIVEL = "CREDITO INDISPONIVEL";
	public static final String DESPESAS_PREEMPENHADAS_A_EMPENHAR = "DESPESAS PRE-EMPENHADAS A EMPENHAR";
	public static final String DESPESAS_EMPENHADAS = "DESPESAS EMPENHADAS (CONTROLE EMPENHO)";
	public static final String DESPESAS_LIQUIDADAS = "DESPESAS LIQUIDADAS (CONTROLE EMPENHO)";
	public static final String DESPESAS_PAGAS = "DESPESAS PAGAS (CONTROLE EMPENHO)";
	
	private long id;
	private long formularioDotacaoId;
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
	private String fonteSOF;
	private String fonteRecursos;
	private int grupoDespesasId;
	private String grupoDespesas;

	private Double dotacaoInicial = 0.0;
	private Double dotacaoAtualizada = 0.0;
	private Double provisaoRecebida = 0.0;
	private Double provisaoConcedida = 0.0;
	private Double destaqueRecebido = 0.0;
	private Double destaqueConcedido = 0.0;
	private Double creditoDisponivel = 0.0;
	private Double creditoIndisponivel = 0.0;
	private Double despPreEmpenhadaAEmpenhar = 0.0;
	private Double despEmpenhadas = 0.0;
	private Double despLiquidas = 0.0;
	private Double despInscRpnp = 0.0;	
	private Double despPagas = 0.0;
	

	
	@Override
	public String toString() {
		return "{id: '" + id + "', \n"
				+ "formularioExecucaoId: '" + formularioDotacaoId + "', \n"
				+ "unidadeOrc: '" + unidadeOrc + "' "
				+ "unidadeOrcDesc: '" + unidadeOrcDesc + "' "
				+ "ugExecutivos: '" + ugExecutivos + "' "
				+ "ugExecutivosDesc: '" + ugExecutivosDesc + "' "
				+ "pt: '" + pt  + "', \n"
				+ "acaoGoverno: '" + acaoGoverno  + "', \n"
				+ "acaoGovernoDesc: '" + acaoGovernoDesc  + "', \n"
				+ "planoOrc: '" + planoOrc  + "', \n"
				+ "planoOrcDesc: '" + planoOrcDesc  + "', \n"
				+ "ptres: '" + ptres  + "', \n"
				+ "grupoDespesaId: '" + grupoDespesasId  + "', \n"
				+ "grupoDespesas: '" + grupoDespesas + "', \n"
				+ "fonteSOF: '" + fonteSOF  + "', \n"
				+ "dotacaoInicial: '" + dotacaoInicial  + "', \n"
				+ "dotacaoAtualizada: '" + dotacaoAtualizada  + "', \n"
				+ "provisaoRecebida: '" + provisaoRecebida  + "', \n"
				+ "provisaoConcedida: '" + provisaoConcedida + "', \n"
				+ "destaqueRecebido: '" + destaqueRecebido + "', \n"
				+ "destaqueConcedido: '" + destaqueConcedido + "', \n"
				+ "creditoDisponivel: '" + creditoDisponivel + "', \n"
				+ "creditoIndisponivel: '" + creditoIndisponivel + "', \n"
				+ "despPreEmpenhadaAEmpenhar: '" + despPreEmpenhadaAEmpenhar + "', \n"
				+ "despEmpenhadas: '" + despEmpenhadas + "', \n"
				+ "despLiquidas: '" + despLiquidas + "', \n"
				+ "despInscRpnp: '" + despInscRpnp + "', \n"
				+ "despPagas: '" + despPagas + "', \n"
				+ "}";
	}
	
	
	public void preencheCampos(ItemExecucao itemExecucao, ResourceBundle bundleExecucao) {
		String[] colunas =  StringUtils.splitPreserveAllTokens(bundleExecucao.getString("colunas"), ";");
		Map<String, ItemExecucao> campos = itemExecucao.getCampos();
		
		for(String coluna : colunas) {
			if(campos.containsKey(coluna)) {
				ItemExecucao item = campos.get(coluna);
				if(item.presente) {
					switch(coluna) {
						case DOTACAO_INICIAL:
							this.setDotacaoInicial(item.valor);	break;
						case DOTACAO_ATUALIZADA:
							this.setDotacaoAtualizada(item.valor);	break;
						case PROVISAO_RECEBIDA:
							this.setProvisaoRecebida(item.valor);	break;
						case PROVISAO_CONCEDIDA:
							this.setProvisaoConcedida(item.valor); break;
						case CREDITO_DISPONIVEL:
							this.setCreditoDisponivel(item.valor); break;
						case CREDITO_INDISPONIVEL:
							this.setCreditoIndisponivel(item.valor); break;
						case DESPESAS_PREEMPENHADAS_A_EMPENHAR:
							this.setDespPreEmpenhadaAEmpenhar(item.valor); break;
						case DESPESAS_EMPENHADAS:
							this.setDespEmpenhadas(item.valor); break;
						case DESPESAS_LIQUIDADAS:
							this.setDespLiquidas(item.valor); break;
						case DESPESAS_PAGAS:
							this.setDespPagas(item.valor);
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


	public long getFormularioDotacaoId() {
		return formularioDotacaoId;
	}


	public void setFormularioDotacaoId(long formularioDotacaoId) {
		this.formularioDotacaoId = formularioDotacaoId;
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


	public String getPtres() {
		return ptres;
	}


	public void setPtres(String ptres) {
		this.ptres = ptres;
	}


	public String getFonteSOF() {
		return fonteSOF;
	}


	public void setFonteSOF(String fonteSOF) {
		this.fonteSOF = fonteSOF;
	}


	public String getFonteRecursos() {
		return fonteRecursos;
	}


	public void setFonteRecursos(String fonteRecursos) {
		this.fonteRecursos = fonteRecursos;
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


	public Double getDespPreEmpenhadaAEmpenhar() {
		return despPreEmpenhadaAEmpenhar;
	}


	public void setDespPreEmpenhadaAEmpenhar(Double despPreEmpenhadaAEmpenhar) {
		this.despPreEmpenhadaAEmpenhar = despPreEmpenhadaAEmpenhar;
	}


	public Double getDespEmpenhadas() {
		return despEmpenhadas;
	}


	public void setDespEmpenhadas(Double despEmpenhadas) {
		this.despEmpenhadas = despEmpenhadas;
	}


	public Double getDespLiquidas() {
		return despLiquidas;
	}


	public void setDespLiquidas(Double despLiquidas) {
		this.despLiquidas = despLiquidas;
	}


	public Double getDespInscRpnp() {
		return despInscRpnp;
	}


	public void setDespInscRpnp(Double despInscRpnp) {
		this.despInscRpnp = despInscRpnp;
	}


	public Double getDespPagas() {
		return despPagas;
	}


	public void setDespPagas(Double despPagas) {
		this.despPagas = despPagas;
	}
	
	

}

