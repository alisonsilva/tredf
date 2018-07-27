package br.jus.tredf.oracle.monitoramento.model.filespace;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.jus.tredf.oracle.monitoramento.model.IMensagem;

public class FileSystemInfo implements IMensagem {
	public Date data;
	public List<InfoRow> rows = new ArrayList<>();
	
	@Override
	public String toString() {
		String ret = "{data: '" + data + "', " 
				+ "rows: [";
		
		for(InfoRow row : rows) {
			ret += row.toString() + ",";
		}
		
		ret += "]}";
		
		return ret;
	}
	
	@Override
	public Date getDate() {
		return data;
	}
}
