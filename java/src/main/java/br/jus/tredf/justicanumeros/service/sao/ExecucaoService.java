package br.jus.tredf.justicanumeros.service.sao;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import br.jus.tredf.justicanumeros.dao.ICodigosAcoes;
import br.jus.tredf.justicanumeros.dao.LogAcoesDao;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.dao.sao.ExecucaoDao;
import br.jus.tredf.justicanumeros.model.LogAcoes;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.sao.FormularioExecucao;
import br.jus.tredf.justicanumeros.model.sao.ArquivoEnviadoSao;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.FormulariosTREDFService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Service(value="ExecucaoService")
public class ExecucaoService extends FormulariosTREDFService {
	
  @Autowired
  private ResourceBundle bundle;
  
  @Autowired
  private LogAcoesDao logAcoesDao;
  
  @Autowired
  private PropertiesServiceController properties;

  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private UsuarioDao usuarioDao;
  
  @Autowired
  private JmsTemplate jmsSaoArquivoExecucaoTemplate;
  
  @Autowired
  private ExecucaoDao execucaoDao;
  
  public void enviaArquivoExecucao(final ArquivoEnviadoSao arquivo, String token) {
  	if(arquivo == null || StringUtils.isEmpty(arquivo.getDataReferencia()) ) {
  		String msg = MessageFormat.format(bundle.getString("ExecucaoService.enviaarquivo.erro")
  				, "Arquivo inválido ou data de referência inválida");
  		throw new ParametroException(msg, ICodigosErros.ERRO_SAO_EXECUCAO);
  	}
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.upload.arquivo.execucao.sao"));    
    validaTokenUsuario(token, permissoes);
  	
  	jmsSaoArquivoExecucaoTemplate.send(new MessageCreator() {			
			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage objectMessage = session.createObjectMessage(arquivo);
				return objectMessage;
			}
		});
  }
  
  public List<FormularioExecucao> getFormulariosExecucao(String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.upload.arquivo.execucao.sao"));    
    validaTokenUsuario(token, permissoes);
  	return execucaoDao.getFormulariosExecucao();
  }
  
  public FormularioExecucao getFormularioExecucaoPorId(String token, Long id) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.upload.arquivo.execucao.sao"));    
    validaTokenUsuario(token, permissoes);
  	return execucaoDao.getFormularioExecucaoPorId(id);
  }
  
  public FormularioExecucao getFormularioExecucaoPorDtReferencia(String token, String dtReferencia) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.upload.arquivo.execucao.sao"));    
    validaTokenUsuario(token, permissoes);
  	return execucaoDao.getFormularioExecucaoPorDtReferencia(FormularioExecucao.getDtReferenciaFormatada(dtReferencia));
  }
  
  public void apagarFormularioExecucao(String token, Long id) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.upload.arquivo.execucao.sao"));    
    validaTokenUsuario(token, permissoes);
    execucaoDao.apagarFormularioExecucao(id);
    UsuarioVO usuario = authenticationService.getUsuarioFromToken(token);
		usuario = usuarioDao.recuperarUsuarioPorLogin(usuario.lgn);
		LogAcoes log = new LogAcoes();
		log.setCodAcao(ICodigosAcoes.ACAO_REMOVER_ARQUIVO_EXECUCAO);
		log.setDtAtualizacao(new Date());
		log.setUsuario(usuario);
		log.setDescricao(bundle.getString("Execucao.removendo.arquivo.execucao"));
    logAcoesDao.inserirLogAcoes(log);
  }
  
  public void testeEnvioArquivo(final String nomeArquivo) {
//  	jmsSaoArquivoExecucaoTemplate.send(new MessageCreator() {
//			
//			@Override
//			public Message createMessage(Session session) throws JMSException {
//				SaoArquivoExecucao arquivo = new SaoArquivoExecucao();
//				arquivo.setName(nomeArquivo);
//				ObjectMessage objectMessage = session.createObjectMessage(arquivo);
//				return objectMessage;
//			}
//		});
  }
  
}
