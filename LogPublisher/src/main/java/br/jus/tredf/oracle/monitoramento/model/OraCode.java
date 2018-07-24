package br.jus.tredf.oracle.monitoramento.model;

public class OraCode {
	public String oraCode;
	public String message;
	
	public OraCode(String oraCode, String oraMessage) {
		this.oraCode = oraCode;
		this.message = oraMessage;
	}
	
	public static String getOraCode(String line) {
		if (line != null) {
			int pos = line.indexOf(":");
			if (pos < 0) {
				return null;
			}
			return line.substring(0, pos);
		}
		return null;
	}
	
	public static String getOraMessage(String line) {
		if (line != null) {
			int pos = line.indexOf(":");
			if (pos < 0) {
				return null;
			}
			return line.substring(pos);
		}
		return null;
	}
}
