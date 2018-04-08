package br.jus.tredf.justicanumeros.model.wrapper;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("all")
@XmlRootElement(name="usuario")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsuarioVO {
	@XmlAttribute public long id;
	
	@XmlAttribute public String lgn;
	@XmlAttribute public String sn;
	@XmlAttribute public String tm;
	
	@XmlAttribute public boolean ativo = true;
	@XmlAttribute public String cn;
	@XmlAttribute public String showName;
	@XmlAttribute public String email;
	@XmlAttribute public String canonicalName;
	@XmlAttribute public String distinguishedName;
	
	@XmlAttribute public String token;
	
	@XmlAttribute public String mensagem;
	
	public List<Long> deployments;
	public List<GrupoVO> grupos;
	
	@Override
	public String toString() {
	  return "{id: " + id + ", login: " + lgn + ", showName: " + showName + "}";
	}
}
