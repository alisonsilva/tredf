package br.jus.tredf.justicanumeros.service.sao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import br.jus.tredf.justicanumeros.dao.ICodigosAcoes;
import br.jus.tredf.justicanumeros.dao.LogAcoesDao;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.dao.sao.ExecucaoDao;
import br.jus.tredf.justicanumeros.model.LogAcoes;
import br.jus.tredf.justicanumeros.model.email.MailMessage;
import br.jus.tredf.justicanumeros.model.sao.Execucao;
import br.jus.tredf.justicanumeros.model.sao.FormularioExecucao;
import br.jus.tredf.justicanumeros.model.sao.SaoArquivoExecucao;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Component
public class JmsReceiveArquivoExecucao {
  private static final Logger logger = Logger.getLogger(JmsReceiveArquivoExecucao.class);  
  private static final String QUEUE_ARQUIVO_EXEC = "qSaoArquivoExecucao";	

  @Autowired
  private LogAcoesDao logAcoesDao;
  
  @Autowired
  private ExecucaoDao execucaoDao;
  
  @Autowired
  private UsuarioDao usuarioDao;
  
  @Autowired
  private ResourceBundle bundle;
  
	@Autowired
	private PropertiesServiceController properties;
  
  @Autowired
  private JmsTemplate jmsEmailTemplate;
  
  
  @JmsListener(destination=QUEUE_ARQUIVO_EXEC)
	public void receiveFile(final Message<SaoArquivoExecucao> arquivo) {
  	SaoArquivoExecucao payload = arquivo.getPayload();
  	BufferedReader br = new BufferedReader(new StringReader(new String(payload.getBytes())));
  	String linha = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");			
  	FormularioExecucao formulario = new FormularioExecucao();
		UsuarioVO usuario = usuarioDao.recuperarUsuarioPorLogin(payload.getLoginUsuario());
  	try {
    	formulario.setDtReferencia(sdf.parse(payload.getDataReferencia()));
    	formulario.setUsuario(usuario);
    	formulario.setDtUpload(new Date());
    	linha = br.readLine();
    	if(linha == null) {
  			LogAcoes log = new LogAcoes();
  			log.setCodAcao(ICodigosAcoes.ACAO_ATUALIZAR_ARQUIVO_EXECUCAO);
  			log.setDtAtualizacao(new Date());
  			log.setUsuario(usuario);
  			log.setDescricao(MessageFormat.format(bundle.getString("Execucao.recebimento.arquivo"), 
  					formulario.getDtReferenciaStr()));
  			logAcoesDao.inserirLogAcoes(log);
  			
  			MailMessage message = new MailMessage();
  			message.setFrom(properties.getProperty("smtp.email.from"));
  			message.setTo(usuario.email);
  			message.setCopia(properties.getProperty("smtp.email.from"));
  			message.setSubject(properties.getProperty("smtp.email.ArquivoExecucao.subject"));
  			message.setText(properties.getProperty("smtp.email.ArquivoExecucao.vazio"));
  			sendMailMessage(message);
    	} else {
				while((linha = br.readLine()) != null) {
					Execucao execucao = new Execucao();
					int i = 0;
					String[] strTok = StringUtils.splitPreserveAllTokens(linha, ';');
					execucao.setPt(strTok[i++]);
					execucao.setAcaoGoverno(strTok[i++]);
					execucao.setPtres(strTok[i++]);
					execucao.setGrupoDespesasId(StringUtils.isEmpty(strTok[i]) ? 0 : Integer.valueOf(strTok[i++]));
					execucao.setCategoriaEconomicaDespesa(strTok[i++]);
					execucao.setGrupoDespesasId(StringUtils.isEmpty(strTok[i]) ? 0 : Integer.valueOf(strTok[i++]));
					execucao.setGrupoDespesas(strTok[i++]);
					execucao.setFonteSOF(strTok[i++]);
					execucao.setPi(strTok[i++]);
					execucao.setElementoDespesaId(strTok[i++]);
					execucao.setElementoDespesa(strTok[i++]);
					execucao.setNaturezaDespesaId(strTok[i++]);
					execucao.setNaturezaDespesa(strTok[i++]);
					execucao.setNaturezaDespesaDetalhadaId(strTok[i++]);
					execucao.setNaturezaDespesaDetalhada(strTok[i++]);
					execucao.setTipoNeCCorId(strTok[i++]);
					execucao.setTipoNeCCor(strTok[i++]);
					execucao.setModalidadeLicitacaoNeCcorId(strTok[i++]);
					execucao.setModalidadeLicitacaoNeCcor(strTok[i++]);
					execucao.setNotaEmpenhoCcor(strTok[i++]);
					execucao.setNumeroProcessoNeCcor(strTok[i++]);
					i++;
					execucao.setFavorecidoNeCcor(strTok[i++]);
					execucao.setItemFormacao(strTok[i++]);
					i++;
					
					execucao.setDespesasEmpenhadas(formataValorNumerico(strTok[i++]));
					execucao.setDespesasLiquidadas(formataValorNumerico(strTok[i++]));
					execucao.setDespesasPagas(formataValorNumerico(strTok[i++]));
					execucao.setRestosPagarProcReinsc(formataValorNumerico(strTok[i++]));
					execucao.setRestosPagarProcPagar(formataValorNumerico(strTok[i++]));
					execucao.setRestosPagarNaoProcessadosInscritos(formataValorNumerico(strTok[i++]));
					execucao.setRestosPagarNaoProcessadosReinscritos(formataValorNumerico(strTok[i++]));
					execucao.setRestosPagarNaoProcessadosCancelados(formataValorNumerico(strTok[i++]));
					execucao.setRestosPagarNaoProcessadosLiquitados(formataValorNumerico(strTok[i++]));
					execucao.setRestosPagarNaoProcessadosPagos(formataValorNumerico(strTok[i++]));
					execucao.setRestosPagarNaoProcessadosAPagar(formataValorNumerico(strTok[i++]));
					
					formulario.getExecucao().add(execucao);
				}			
				formulario.getExecucao().remove(formulario.getExecucao().size() - 1);
				execucaoDao.inserirFormularioExecucao(formulario);
				LogAcoes log = new LogAcoes();
				log.setCodAcao(ICodigosAcoes.ACAO_ATUALIZAR_ARQUIVO_EXECUCAO);
				log.setDtAtualizacao(new Date());
				log.setUsuario(usuario);
				log.setDescricao(MessageFormat.format(bundle.getString("Execucao.recebimento.arquivo"), 
						formulario.getDtReferenciaStr()));
				logAcoesDao.inserirLogAcoes(log);
				
				MailMessage message = new MailMessage();
				message.setFrom(properties.getProperty("smtp.email.from"));
				message.setTo(usuario.email);
				message.setCopia(properties.getProperty("smtp.email.from"));
				message.setSubject(properties.getProperty("smtp.email.ArquivoExecucao.subject"));
				message.setText(properties.getProperty("smtp.email.ArquivoExecucao.text"));
				sendMailMessage(message);
    	}
		} catch (IOException e) {
			logger.error("Erro lendo arquivo enviado: " + e.getMessage(), e);
			sendErrorMailMessage("Erro lendo arquivo enviado: " + e.getMessage(), usuario.email);
		} catch (ParseException e) {
			logger.error("Erro fazendo parsing na data de referência da execução: " + e.getMessage(), e);
			sendErrorMailMessage("Erro lendo arquivo enviado: " + e.getMessage(), usuario.email);
		} catch (Exception e) {
			logger.error("Erro genérico armazenando arquivo: " + e.getMessage(), e);
			sendErrorMailMessage("Erro lendo arquivo enviado: " + e.getMessage(), usuario.email);
		}
	}
  
  private void sendErrorMailMessage(String errorMessage, String email) {
		MailMessage message = new MailMessage();
		message.setFrom(properties.getProperty("smtp.email.from"));
		message.setTo(email);
		message.setCopia(properties.getProperty("smtp.email.from"));
		message.setSubject(properties.getProperty("smtp.email.ArquivoExecucao.subject"));
		message.setText(errorMessage);
		sendMailMessage(message);
  }
  
  private void sendMailMessage(final MailMessage message) {
  	jmsEmailTemplate.send(new MessageCreator() {			
			@Override
			public javax.jms.Message createMessage(Session session) throws JMSException {
				ObjectMessage objectMessage = session.createObjectMessage(message);
				return objectMessage;
			}
		});  	
  }
  
  private Double formataValorNumerico(String valor) {
  	Double vl = 0d;
  	if(StringUtils.isEmpty(valor)) {
  		return vl;
  	}
  	if(valor.contains("(")) {
  		valor = valor.replace("(", "").replace(")", "");
  		valor = "-" + valor;  		
  	}
  	valor = valor.replace(".", "").replace(",", ".");
  	vl = Double.valueOf(valor);
  	return vl;
  }

}
