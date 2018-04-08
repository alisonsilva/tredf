package br.jus.tredf.justicanumeros.model.sao;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

public class FormularioExecucao implements Serializable {

	private static final long serialVersionUID = -8579618112160034417L;

	private long id;
	private Date dtUpload;
	private Date dtReferencia;
	private String dtUploadStr;
	private String dtReferenciaStr;
	
	private UsuarioVO usuario;
	private List<Execucao> execucao = new LinkedList<Execucao>();
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDtUpload() {
		return dtUpload;
	}
	public void setDtUpload(Date dtUpload) {
		this.dtUpload = dtUpload;
		if (dtUpload != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			this.dtUploadStr = sdf.format(dtUpload);
		}
	}
	public UsuarioVO getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioVO usuario) {
		this.usuario = usuario;
	}
	public List<Execucao> getExecucao() {
		return execucao;
	}
	public void setExecucao(List<Execucao> execucao) {
		this.execucao = execucao;
	}
	public Date getDtReferencia() {
		return dtReferencia;
	}
	public void setDtReferencia(Date dtReferencia) {
		this.dtReferencia = dtReferencia;
		if (dtReferencia != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			this.dtReferenciaStr = sdf.format(dtReferencia);
		}
	}
	
	public Date getDtReferenciaTransformada() {
		GregorianCalendar calOrig = new GregorianCalendar();
		calOrig.setTime(dtReferencia);
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, calOrig.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, calOrig.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}
	
	public String getDtUploadStr() {
		return dtUploadStr;
	}
	public String getDtReferenciaStr() {
		return dtReferenciaStr;
	}
	/**
	 * Formata a data de referência de acordo com o padrão dd/MM/yyyy. O retorno será uma data com o primeiro dia do 
	 * mês da data de referência.
	 * @param dtReferencia Data de referência em formato string de caracteres a ser transformado
	 * @return Um valor no formato Date com o dia sendo o primeiro dia do mês da data de entrada em formato string 
	 */
	public static Date getDtReferenciaFormatada(String dtReferencia) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dtref;
		try {
			dtref = sdf.parse(dtReferencia);
		} catch (ParseException e) {
			return null;
		}
		
		GregorianCalendar calOrig = new GregorianCalendar();
		calOrig.setTime(dtref);
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, calOrig.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, calOrig.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}
}
