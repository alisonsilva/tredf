package br.jus.tredf.justicanumeros.model.justificativa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="GRUPO_INDICADOR")
@XmlAccessorType(XmlAccessType.FIELD)
public class GrupoIndicador {
	private int id;
	private String desGrupo;
	public int getId() {
		return id;
	}
	
	
	public void setId(int id) {
		this.id = id;
	}
	public String getDesGrupo() {
		return desGrupo;
	}
	public void setDesGrupo(String desGrupo) {
		this.desGrupo = desGrupo;
	}
	
	@Override
	public String toString() {
		return "{id: " + id + ", descricao: '" + desGrupo + "'}"; 
	}
}
