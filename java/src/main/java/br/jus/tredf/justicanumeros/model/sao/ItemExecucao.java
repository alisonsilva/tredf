package br.jus.tredf.justicanumeros.model.sao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ItemExecucao implements Comparable<ItemExecucao>{
	public String nomeColuna;
	public int posicao;
	public boolean presente;
	public Double valor = 0.0;

	private Map<String, ItemExecucao> campos = new HashMap<String, ItemExecucao>();
	
	public ItemExecucao(int posicao) {
		this.posicao = posicao;
	}
	
	public ItemExecucao(String nomeColuna, boolean presente, int posicao) {
		this.nomeColuna = nomeColuna;
		this.presente = presente;
		this.posicao = posicao;
		campos.put(nomeColuna, this);
	}
	
	public boolean addColuna(String nomeColuna, boolean presente, int posicao) {
		if(campos.containsKey(nomeColuna)) {
			return false;
		}
		campos.put(nomeColuna, new ItemExecucao(nomeColuna, presente, posicao));
		return true;
	}
	
	public boolean alteraColuna(String nomeColuna, boolean presente, int posicao) {
		if(!campos.containsKey(nomeColuna)) {
			return false;
		}
		campos.put(nomeColuna, new ItemExecucao(nomeColuna, presente, posicao));
		return true;
	}
	
	public Map<String, ItemExecucao> getCampos() {
		return campos;
	}
	
	@Override
	public String toString() {
		return "{nomeColuna: '" + nomeColuna + "', posicao: " + posicao + ", presente: " + presente + ", valor: " + valor +"}";
	}
	
	public boolean insereValorPorIndice(Double valor, int index) {
		boolean ret = false;
		Collection<ItemExecucao> itens = campos.values();
		for(ItemExecucao itExec : itens) {
			if(itExec.posicao == index) {
				ret = true;
				itExec.valor = valor;
				break;
			}
		}
		return ret;
	}

	@Override
	public int compareTo(ItemExecucao o) {
		int comp = 0;
		if (o.posicao < this.posicao) {
			comp = -1;
		} else if(o.posicao > this.posicao) {
			comp = 1;
		} else {
			comp = 0;
		}		
		return comp;
	}
	
	
	
}
