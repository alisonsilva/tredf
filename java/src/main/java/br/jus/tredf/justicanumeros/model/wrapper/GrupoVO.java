package br.jus.tredf.justicanumeros.model.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import br.jus.tredf.justicanumeros.model.Permissao;

@SuppressWarnings("all")
@XmlRootElement(name="grupo")
@XmlAccessorType(XmlAccessType.FIELD)
public class GrupoVO {
	@XmlAttribute public long id;
	@XmlAttribute public Long cartorioId;
	@XmlAttribute public String nome;
	@XmlAttribute public String dn;
	@XmlAttribute public String cn;
	
	@XmlAttribute public String descricao;
	
	public List<Long> deployments;
	public List<Permissao> permissoes = new ArrayList<Permissao>();
	
	@Override
	public String toString() {
	  return "{id: " + id + ", descricao: '" + descricao + "', nome: '" + nome + "' }";
	}
}
