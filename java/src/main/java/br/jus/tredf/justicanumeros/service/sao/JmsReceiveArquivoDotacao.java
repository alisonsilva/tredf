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

import br.jus.tredf.justicanumeros.controller.config.FormulariosTREDFConfiguration;
import br.jus.tredf.justicanumeros.dao.ICodigosAcoes;
import br.jus.tredf.justicanumeros.dao.LogAcoesDao;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.dao.sao.DotacaoDao;
import br.jus.tredf.justicanumeros.model.LogAcoes;
import br.jus.tredf.justicanumeros.model.email.MailMessage;
import br.jus.tredf.justicanumeros.model.sao.Dotacao;
import br.jus.tredf.justicanumeros.model.sao.FormularioDotacao;
import br.jus.tredf.justicanumeros.model.sao.ItemExecucao;
import br.jus.tredf.justicanumeros.model.sao.ArquivoEnviadoSao;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Component
public class JmsReceiveArquivoDotacao {
  private static final Logger logger = Logger.getLogger(JmsReceiveArquivoDotacao.class);  
  private static final String QUEUE_ARQUIVO_EXEC = FormulariosTREDFConfiguration.DEFAULT_SAO_ARQUIVO_DOTACAO_QUEUE;	

  @Autowired
  private LogAcoesDao logAcoesDao;
  
  @Autowired
  private DotacaoDao dotacaoDao;
  
  @Autowired
  private UsuarioDao usuarioDao;
  
  @Autowired
  private ResourceBundle bundle;
  
	@Autowired
	private PropertiesServiceController properties;
  
  @Autowired
  private JmsTemplate jmsEmailTemplate;
  
  @Autowired
  private ResourceBundle bundleDotacao;
  
  @JmsListener(destination=QUEUE_ARQUIVO_EXEC)
	public void receiveFile(final Message<ArquivoEnviadoSao> arquivo) {
  	ArquivoEnviadoSao payload = arquivo.getPayload();
  	BufferedReader br = new BufferedReader(new StringReader(new String(payload.getBytes())));
  	String linha = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");			
  	FormularioDotacao formulario = new FormularioDotacao();
		UsuarioVO usuario = usuarioDao.recuperarUsuarioPorLogin(payload.getLoginUsuario());
		String colunas = bundleDotacao.getString("colunas");
		String[] cols = StringUtils.splitPreserveAllTokens(colunas, ";");
		ItemExecucao itemExecucao = new ItemExecucao(0);
		int idx = 1;
		for(String col : cols) {
			 itemExecucao.addColuna(col, false, idx++);
		}
		
  	try {
    	formulario.setDtReferencia(sdf.parse(payload.getDataReferencia()));
    	formulario.setUsuario(usuario);
    	formulario.setDtUpload(new Date());
    	linha = br.readLine();
    	cols = StringUtils.splitPreserveAllTokens(linha, ";");
    	idx = 0;
    	for(String col : cols) {
    		itemExecucao.alteraColuna(col, true, idx++);
    	}
    	if(linha == null) {
  			LogAcoes log = new LogAcoes();
  			log.setCodAcao(ICodigosAcoes.ACAO_ATUALIZAR_ARQUIVO_DOTACAO);
  			log.setDtAtualizacao(new Date());
  			log.setUsuario(usuario);
  			log.setDescricao(MessageFormat.format(bundle.getString("Execucao.recebimento.arquivo"), 
  					formulario.getDtReferenciaStr()));
  			logAcoesDao.inserirLogAcoes(log);
  			
  			MailMessage message = new MailMessage();
  			message.setFrom(properties.getProperty("smtp.email.from"));
  			message.setTo(usuario.email);
  			message.setCopia(properties.getProperty("smtp.email.from"));
  			message.setSubject(properties.getProperty("smtp.email.ArquivoDotacao.subject"));
  			message.setText(properties.getProperty("smtp.email.ArquivoDotacao.vazio"));
  			sendMailMessage(message);
    	} else {
				while((linha = br.readLine()) != null) {
					Dotacao dotacao = new Dotacao();
					int i = 0;
					String[] strTok = StringUtils.splitPreserveAllTokens(linha, ';');
					
					String prim = strTok[i++];					
					if(prim.equalsIgnoreCase("total")) {
						continue;
					}
					
					dotacao.setUnidadeOrc(prim);
					dotacao.setUnidadeOrcDesc(strTok[i++]);
					dotacao.setUgExecutivos(strTok[i++]);
					dotacao.setUgExecutivosDesc(strTok[i++]);
					dotacao.setPt(strTok[i++]);
					dotacao.setAcaoGoverno(strTok[i++]);
					dotacao.setAcaoGovernoDesc(strTok[i++]);
					dotacao.setPlanoOrc(strTok[i++]);
					dotacao.setPlanoOrcDesc(strTok[i++]);
					dotacao.setPtres(strTok[i++]);
					dotacao.setFonteSOF(strTok[i++]);
					dotacao.setFonteRecursos(strTok[i++]);
					dotacao.setGrupoDespesasId(StringUtils.isEmpty(strTok[i]) ? 0 : Integer.valueOf(strTok[i++]));
					dotacao.setGrupoDespesas(strTok[i++]);
					
					for(int idxTmp = i; idxTmp < strTok.length; idxTmp++) {
						itemExecucao.insereValorPorIndice(formataValorNumerico(strTok[idxTmp]), idxTmp);
					}
					
					dotacao.preencheCampos(itemExecucao, bundleDotacao);
					
					formulario.getDotacoes().add(dotacao);
				}			
				formulario.getDotacoes().remove(formulario.getDotacoes().size() - 1);
				dotacaoDao.inserirFormularioDotacao(formulario);
				LogAcoes log = new LogAcoes();
				log.setCodAcao(ICodigosAcoes.ACAO_ATUALIZAR_ARQUIVO_DOTACAO);
				log.setDtAtualizacao(new Date());
				log.setUsuario(usuario);
				log.setDescricao(MessageFormat.format(bundle.getString("Execucao.recebimento.arquivo"), 
						formulario.getDtReferenciaStr()));
				logAcoesDao.inserirLogAcoes(log);
				
				String corpoMensagem = bundleDotacao.getString("mensagem.upload.sucesso");
				corpoMensagem = corpoMensagem.replace("&subs1;", payload.getName());
				MailMessage message = new MailMessage();
				message.setFrom(properties.getProperty("smtp.email.from"));
				message.setTo(usuario.email);
				message.setCopia(properties.getProperty("smtp.email.from"));
				message.setSubject(properties.getProperty("smtp.email.ArquivoDotacao.subject"));
				message.setText(corpoMensagem);
				sendMailMessage(message);
    	}
		} catch (IOException e) {
			String msg = "Erro lendo arquivo enviado: " + e.getMessage();
			String msgErro = bundleDotacao.getString("mensagem.upload.error");
			msgErro = msgErro.replace("&subs1", payload.getName());
			msgErro = msgErro.replace("&subs2", msg);
			logger.error(msg, e);
			sendErrorMailMessage(msgErro, usuario.email);
		} catch (ParseException e) {
			String msg = "Erro fazendo parsing na data de referência da dotação: " + e.getMessage();
			String msgErro = bundleDotacao.getString("mensagem.upload.error");
			msgErro = msgErro.replace("&subs1", payload.getName());
			msgErro = msgErro.replace("&subs2", msg);
			logger.error(msg, e);
			sendErrorMailMessage(msgErro, usuario.email);
		} catch (Exception e) {
			String msgErro = bundleDotacao.getString("mensagem.upload.error");
			msgErro = msgErro.replace("&subs1", payload.getName());
			msgErro = msgErro.replace("&subs2", e.getMessage());
			logger.error("Erro genérico armazenando arquivo: " + e.getMessage(), e);
			sendErrorMailMessage(msgErro, usuario.email);
		}
	}
  
  private void sendErrorMailMessage(String errorMessage, String email) {
		MailMessage message = new MailMessage();
		message.setFrom(properties.getProperty("smtp.email.from"));
		message.setTo(email);
		message.setCopia(properties.getProperty("smtp.email.from"));
		message.setSubject(properties.getProperty("smtp.email.ArquivoDotacao.subject"));
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
