package br.jus.tredf.justicanumeros.model.sao;

import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class PropostaOrcamentaria implements Serializable, IArquivo {

	private static final String PROPOSTA_UA1 = "Proposta UA-1";
	private static final String PROPOSTA_FINAL = "Proposta Final";
	
	private static final long serialVersionUID = -4514095816617037037L;

	private Long id;
	private Long formularioPropOrcamentariaId;
	private String acaoOrcamentaria;
	private String categoriaProgramacao;
	private String unidadeAdministrativa;
	private String despesaAgendada;
	private String planoInterno;
	private String gnd;
	private String elementoId;
	private String elementoDesc;
	private String subElementoId;
	private String subElementoDesc;
	private String itemDespesa;
	private double propostaUA1;
	private double propostaFinal;
	
	public void preencheCampos(ItemExecucao itemExecucao, ResourceBundle bundleExecucao) {
		String[] colunas =  StringUtils.splitPreserveAllTokens(bundleExecucao.getString("colunas"), ";");
		Map<String, ItemExecucao> campos = itemExecucao.getCampos();
		
		for(String coluna : colunas) {
			if(campos.containsKey(coluna)) {
				ItemExecucao item = campos.get(coluna);
				if(item.presente) {
					switch(coluna) {
						case PROPOSTA_UA1:
							this.setPropostaUA1(item.valor);	break;
						case PROPOSTA_FINAL:
							this.setPropostaFinal(item.valor);	break;

					}
				}
			}
		}
	}	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFormularioPropOrcamentariaId() {
		return formularioPropOrcamentariaId;
	}
	public void setFormularioPropOrcamentariaId(Long formularioPropOrcamentariaId) {
		this.formularioPropOrcamentariaId = formularioPropOrcamentariaId;
	}
	public String getAcaoOrcamentaria() {
		return acaoOrcamentaria;
	}
	public void setAcaoOrcamentaria(String acaoOrcamentaria) {
		this.acaoOrcamentaria = acaoOrcamentaria;
	}
	public String getCategoriaProgramacao() {
		return categoriaProgramacao;
	}
	public void setCategoriaProgramacao(String categoriaProgramacao) {
		this.categoriaProgramacao = categoriaProgramacao;
	}
	public String getUnidadeAdministrativa() {
		return unidadeAdministrativa;
	}
	public void setUnidadeAdministrativa(String unidadeAdministrativa) {
		this.unidadeAdministrativa = unidadeAdministrativa;
	}
	public String getDespesaAgendada() {
		return despesaAgendada;
	}
	public void setDespesaAgendada(String despesaAgendada) {
		this.despesaAgendada = despesaAgendada;
	}
	public String getPlanoInterno() {
		return planoInterno;
	}
	public void setPlanoInterno(String planoInterno) {
		this.planoInterno = planoInterno;
	}
	public String getGnd() {
		return gnd;
	}
	public void setGnd(String gnd) {
		this.gnd = gnd;
	}
	public String getElementoId() {
		return elementoId;
	}
	public void setElementoId(String elementoId) {
		this.elementoId = elementoId;
	}
	public String getElementoDesc() {
		return elementoDesc;
	}
	public void setElementoDesc(String elementoDesc) {
		if(StringUtils.isNotEmpty(elementoDesc)) {
			StringTokenizer strTok = new StringTokenizer(elementoDesc, "-");
			if (strTok.countTokens() <= 1) {
				this.elementoDesc = strTok.nextToken();
			} else {
				this.elementoId = strTok.nextToken();
				this.elementoDesc = strTok.nextToken();
				this.elementoId = this.elementoId.replace(".", "");
			}
		} else {
			this.elementoDesc = elementoDesc;
		}
	}
	public String getSubElementoId() {
		return subElementoId;
	}
	public void setSubElementoId(String subElementoId) {
		this.subElementoId = subElementoId;
	}
	public String getSubElementoDesc() {		
		return subElementoDesc;
	}
	public void setSubElementoDesc(String subElementoDesc) {
		if(StringUtils.isNotEmpty(subElementoDesc)) {
			StringTokenizer strTok = new StringTokenizer(subElementoDesc, "-");
			if (strTok.countTokens() <= 1) {
				this.subElementoDesc = strTok.nextToken();
			} else {
				this.subElementoId = strTok.nextToken();
				this.subElementoDesc = strTok.nextToken();
				this.subElementoId = this.subElementoId.replace(".", "");
			}
		} else {		
			this.subElementoDesc = subElementoDesc;
		}
	}
	public String getItemDespesa() {
		return itemDespesa;
	}
	public void setItemDespesa(String itemDespesa) {
		this.itemDespesa = itemDespesa;
	}
	public double getPropostaUA1() {
		return propostaUA1;
	}
	public void setPropostaUA1(double propostaUA1) {
		this.propostaUA1 = propostaUA1;
	}
	public double getPropostaFinal() {
		return propostaFinal;
	}
	public void setPropostaFinal(double propostaFinal) {
		this.propostaFinal = propostaFinal;
	}
	
	@Override
	public String toString() {
		String result = "";
		result = "{id: " + id + ", formularioPropOrcId: " + formularioPropOrcamentariaId + 
				", acaoOrcamentaria: '" + acaoOrcamentaria + "', " +
				", categoriaProgramacao: '" + categoriaProgramacao + "', " +
				", unidadeAdministrativa: '" + unidadeAdministrativa + "', " +
				", despesaAgendada: '" + despesaAgendada + "', " +
				", planoInterno: '" + planoInterno + "', " +
				", gnd: '" + gnd + "', " +
				", elementoId: '" + elementoId + "', " +
				", elementoDesc: '" + elementoDesc + "', " +
				", subElementoId: '" + subElementoId + "', " +
				", subElementoDesc: '" + subElementoDesc + "', " +
				", itemDespesa: '" + itemDespesa + "', " +
				", propostaUA1: " + propostaUA1 + ", " +
				", propostaFinal: " + propostaFinal + "}";
		return result;
	}
	
}
