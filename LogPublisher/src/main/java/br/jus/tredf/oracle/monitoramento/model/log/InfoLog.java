package br.jus.tredf.oracle.monitoramento.model.log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.jus.tredf.oracle.monitoramento.model.IMensagem;

public class InfoLog implements IMensagem {
	public static final int CUMPRIMENTO_DATA = 19;
	
	public Date data;
	public List<InfoLogError> errors = new ArrayList<>();
	public List<OraCode> oraCodes = new ArrayList<>();
	
	/**
	 * 2018-07-20T14:38:26.025354-03:00
	 * @param date
	 * @return
	 */
	public static Date checkDateFormat(String date) {
		if(date != null && date.length() >= CUMPRIMENTO_DATA) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date dt = null;
			try {
				dt = sdf.parse(date);
			} catch (ParseException e) {
			}
			return dt;
		}
		return null;
	}
	
	public static String checkError(String linha) {
		if(linha != null) {
			if (linha.startsWith("Error")) {
				return linha;
			}
		}
		return null;
	}
	
	public static String checkOraMsg(String linha) {
		if(linha != null) {
			if(linha.startsWith("ORA-")) {
				return linha;
			}
		}
		return null;
	}

	@Override
	public Date getDate() {
		return data;
	}
	
}
