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
import br.jus.tredf.justicanumeros.dao.sao.PropOrcamentariaDao;
import br.jus.tredf.justicanumeros.model.LogAcoes;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.sao.ArquivoEnviadoSao;
import br.jus.tredf.justicanumeros.model.sao.FormularioExecucao;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.FormulariosTREDFService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Service(value="PropostaOrcamentariaService")
public class PropOrcamentariaService extends FormulariosTREDFService {
	
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
  private JmsTemplate jmsSaoArquivoPropOrcamentaria;
  
  @Autowired
  private PropOrcamentariaDao dotacaoDao;
  
  public void enviaPropostaOrcamentaria(final ArquivoEnviadoSao arquivo, String token) {
  	if(arquivo == null || StringUtils.isEmpty(arquivo.getDataReferencia()) ) {
  		String msg = MessageFormat.format(bundle.getString("PropOrcamentaria.enviaarquivo.erro")
  				, "Arquivo inválido ou data de referência inválida");
  		throw new ParametroException(msg, ICodigosErros.ERRO_SAO_PROPOSTA_ORC);
  	}
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.upload.arquivo.proporcamentaria.sao"));    
    validaTokenUsuario(token, permissoes);
  	
    jmsSaoArquivoPropOrcamentaria.send(new MessageCreator() {			
			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage objectMessage = session.createObjectMessage(arquivo);
				return objectMessage;
			}
		});
  }
  
  public List<FormularioExecucao> getFormulariosPropostaOrcamentaria(String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.upload.arquivo.proporcamentaria.sao"));    
    validaTokenUsuario(token, permissoes);
  	return dotacaoDao.getFormulariosPropostaOrcamentaria();
  }
  
  public FormularioExecucao getFormularioPropostaOrcamentariaPorId(String token, Long id) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.upload.arquivo.proporcamentaria.sao"));    
    validaTokenUsuario(token, permissoes);
  	return dotacaoDao.getFormularioPropOrcamentariaPorId(id);
  }
  
  public void apagarFormularioPropostaOrcamentaria(String token, Long id) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.upload.arquivo.proporcamentaria.sao"));    
    validaTokenUsuario(token, permissoes);
    dotacaoDao.apagarFormularioPropostaOrcamentaria(id);
    UsuarioVO usuario = authenticationService.getUsuarioFromToken(token);
		usuario = usuarioDao.recuperarUsuarioPorLogin(usuario.lgn);
		LogAcoes log = new LogAcoes();
		log.setCodAcao(ICodigosAcoes.ACAO_REMOVER_ARQUIVO_PROPORC);
		log.setDtAtualizacao(new Date());
		log.setUsuario(usuario);
		log.setDescricao(bundle.getString("Execucao.removendo.arquivo.propostaorcamentaria"));
    logAcoesDao.inserirLogAcoes(log);
  }
  
  
}
