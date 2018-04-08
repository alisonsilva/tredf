package br.jus.tredf.justicanumeros.service.envioxml;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.jus.tredf.justicanumeros.dao.GrupoDao;
import br.jus.tredf.justicanumeros.dao.ICodigosAcoes;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.dao.envioxml.DadosEnvioDao;
import br.jus.tredf.justicanumeros.dao.envioxml.EnvioProcessoDao;
import br.jus.tredf.justicanumeros.dao.envioxml.EnvioXMLDao;
import br.jus.tredf.justicanumeros.dao.terceirizado.AreaAtuacaoDao;
import br.jus.tredf.justicanumeros.dao.terceirizado.GrauInstrucaoDao;
import br.jus.tredf.justicanumeros.dao.terceirizado.TerceirizadoDao;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.email.MailMessage;
import br.jus.tredf.justicanumeros.model.envioxml.ControleCriacaoXml;
import br.jus.tredf.justicanumeros.model.envioxml.EnvioProcessoXML;
import br.jus.tredf.justicanumeros.model.envioxml.EnvioXML;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.FormulariosTREDFService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Service(value="enviaProcessoService")
@Transactional
public class EnvioProcessoService extends FormulariosTREDFService {
  private static Boolean FLAG_SEMAFORO = false;
  
  private static final int PRIMEIRA_INSTANCIA = 1;
  private static final int SEGUNDA_INSTANCIA = 2;
  
  @Autowired
  private ResourceBundle bundle;
  
  private static final Logger logger = Logger.getLogger(DadosEnvioDao.class);
  
  @Autowired
  private PropertiesServiceController properties;

  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private UsuarioDao usuarioDao;
  
  @Autowired
  private GrupoDao grupoDao;
  
  @Autowired
  private TerceirizadoDao terceirizadoDao;
  
  @Autowired
  private GrauInstrucaoDao grauInstrucaoDao;
  
  @Autowired
  private AreaAtuacaoDao areaAtuacaoDao;

  @Autowired
  private EnvioXMLDao envioXmlDao;
  
  @Autowired
  private EnvioProcessoDao envioProcessoDao;
  
  @Autowired
  private DadosEnvioDao dadosEnvioDao;
  
  @Autowired
  private JmsTemplate jmsEmailTemplate;  
  
  public List<EnvioXML> getListaEnvios(String token) {
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.envioxml.usuario"));    
    validaTokenUsuario(token, permissoes);
    
    return envioXmlDao.getEnviosRealizados();
  }
  
  public void recuperaEnvioProcessosCNJ(String competencia, String token) {
  	if(StringUtils.isEmpty(competencia)) {
  		throw new ParametroException("Dados inválidos", ICodigosErros.ERRO_ENVIOXML_PARAMETROINVALIDO);
  	}
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.envioxml.usuario"));    
    validaTokenUsuario(token, permissoes);
    if(FLAG_SEMAFORO) {
    	throw new ParametroException(bundle.getString("envioxml.reenvioprocessos.aindaprocessando"), 
    			ICodigosAcoes.ACAO_ENVIOXML_PRODUCAOPROCESSOSENVIO);
    }
    
    final EnvioProcessoService me = this;
    
    Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
		    synchronized(FLAG_SEMAFORO) {
		    	FLAG_SEMAFORO = true;
		      UsuarioVO usuario = authenticationService.getUsuarioFromToken(token);
		      UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usuario.lgn);
			    try {
						me.geraDadosEnvioXML(competencia, PRIMEIRA_INSTANCIA, token);
						me.geraDadosEnvioXML(competencia, SEGUNDA_INSTANCIA, token);
						
						me.recuperaProcessosASeremEnviados(competencia, PRIMEIRA_INSTANCIA, token);
						me.recuperaProcessosASeremEnviados(competencia, SEGUNDA_INSTANCIA, token);
						
						me.preparaEnvioDadosXML(competencia, PRIMEIRA_INSTANCIA, token);
						me.preparaEnvioDadosXML(competencia, SEGUNDA_INSTANCIA, token);
						
						me.enviaProcessosCNJ(competencia, PRIMEIRA_INSTANCIA, token);
						me.enviaProcessosCNJ(competencia, SEGUNDA_INSTANCIA, token);
					} catch (Exception e) {
						logger.error("Erro ao gerar xml para envio: " + e.getMessage());
						enviaEmail(e.getMessage(), usuarioLog.email);
					} finally {
						FLAG_SEMAFORO = false;
					}
		    }
			}
		});
    
    t.start();
    
  }
  
  public void enviaProcessosNaoEnviadosPorEnvio(long envioId, String token) {
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.envioxml.usuario"));    
    validaTokenUsuario(token, permissoes);
    
  	EnvioXML envio = envioXmlDao.getEnvioXML(envioId);
    if (!FLAG_SEMAFORO || !envio.isFlEnviado()) {
    	Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
		    	synchronized(FLAG_SEMAFORO) {
		    		FLAG_SEMAFORO = true;
		        try {
							List<EnvioProcessoXML> processos = envioProcessoDao.getEnvioProcessoPorEnvioParseOk(envioId);
							if (processos != null && processos.size() > 0) {
								dadosEnvioDao.enviaProcessos(processos);
								String logMessage = MessageFormat.format(bundle.getString("envioxml.envioprocessosxml"), envioId);
								registraLogAcoes(logMessage, ICodigosAcoes.ACAO_ENVIOXML_ENVIOPROCESSOS, token);
							}
							if(processos != null && processos.size() == 0) {
								FLAG_SEMAFORO = false;
								throw new ParametroException(bundle.getString("envioxml.erro.naohadados.parareenvio"), 
										ICodigosAcoes.ACAO_ENVIOXML_PRODUCAOPROCESSOSENVIO);
							}
						} catch (Exception e) {
						} finally {
			    		FLAG_SEMAFORO = false;
			    	}
		    	} 
				}
			});
    	t.start();
    } else {
    	throw new ParametroException(bundle.getString("envioxml.reenvioprocessos.aindaprocessando"), 
    			ICodigosAcoes.ACAO_ENVIOXML_PRODUCAOPROCESSOSENVIO);
    }
    
  }
  
  public void enviaProcessosCNJ(String competencia, String token) {
    List<Permissao> permissoes = 
            addNovaPermissaoList(null, 
                properties.getProperty("perm.formulario.envioxml.usuario"));    
        validaTokenUsuario(token, permissoes);
	  
    this.enviaProcessosCNJ(competencia, PRIMEIRA_INSTANCIA, token);
    this.enviaProcessosCNJ(competencia, SEGUNDA_INSTANCIA, token);
  }
  
  private void recuperaProcessosASeremEnviados(String competencia, int instancia, String token) {
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.envioxml.usuario"));    
    validaTokenUsuario(token, permissoes);
    
    EnvioXML envioXml = dadosEnvioDao.getProcessosEnvio(competencia, instancia);
    if (envioXml == null) {
      throw new ParametroException(bundle.getString("envioxml.erro.naohadados"), 
          ICodigosErros.ERRO_ENVIOXML_NAOHADADOS);
    }      
    UsuarioVO usuario = authenticationService.getUsuarioFromToken(token);
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usuario.lgn);
    
    ControleCriacaoXml controle = dadosEnvioDao.getControleCriacaoXml(competencia, instancia);
    envioXml.setUsuario(usuarioLog);
    envioXml.setControleCriacao(controle);
    envioXml = envioXmlDao.inserirEnvioXML(envioXml);
    for(EnvioProcessoXML procEnvio : envioXml.getProcessosEnviados()) {
      procEnvio.setEnvioXml(envioXml);
      envioProcessoDao.inserirEnvioProcesso(procEnvio);
    }
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("envioxml.novoarquivoxml.geracaoprocessosenvio"), 
            envioXml.getId());
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_ENVIOXML_PRODUCAOPROCESSOSENVIO, token);          
  }
  
  public void enviaProcessosCNJPorProcesso(Long idProcesso, String token) {
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.envioxml.usuario"));    
    validaTokenUsuario(token, permissoes);

    EnvioProcessoXML processo = envioProcessoDao.getEnvioProcessoPorID(idProcesso);
    if(processo == null || processo.getId() == null) {
      throw new ParametroException("Processo não encontrado", ICodigosErros.ERRO_ENVIOXML_PARAMETROINVALIDO);
    }
    dadosEnvioDao.enviaProcesso(processo);
    String logMessage = 
        MessageFormat.format(
            bundle.getString("envioxml.envioprocessoxml"), 
            idProcesso);
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_ENVIOXML_ENVIOPROCESSO, token);          
  }
  
  /*
   * 
   */
  public void removeEnvio(Long idEnvio, String token) {
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.envioxml.usuario"));    
    validaTokenUsuario(token, permissoes);

    if(FLAG_SEMAFORO) {
    	throw new ParametroException(bundle.getString("envioxml.reenvioprocessos.aindaprocessando"), 
    			ICodigosAcoes.ACAO_ENVIOXML_PRODUCAOPROCESSOSENVIO);
    }
    
    envioXmlDao.removeEnvio(idEnvio);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("envioxml.removeenvio"), 
            idEnvio);
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_ENVIOXML_REMOVEENVIO, token);          
  }
  
  public byte[] relatorioProcessosEnviadosCnj(Long idEnvioXml) {
  	return envioXmlDao.relatorioProcessosEnviadosCNJ(idEnvioXml);
  }
  
  /**
   * Recupera os processos por competência e instância que sofreram validação do seus respectivos xml e
   * que ainda não foram enviados. Esses processos então serão preparados para envio e serão enviados.
   * @param competencia A competência de referência (MM/yyyy)
   * @param instancia A instância de referência: 1 = primeira instância; 2 = segunda instância
   * @param token
   */
  protected void enviaProcessosCNJ(String competencia, int instancia, String token) {
		ControleCriacaoXml controle = dadosEnvioDao.getControleCriacaoXml(competencia, instancia);
		EnvioXML envio = envioXmlDao.getEnvioPorControleXml(controle.getId());
		List<EnvioProcessoXML> processos = envioProcessoDao.getEnvioProcessoPorEnvioParseOk(envio.getId());
		if (processos != null && processos.size() > 0) {
			dadosEnvioDao.enviaProcessos(processos);
			envioXmlDao.setEnviado(envio.getId());
			String logMessage = MessageFormat.format(bundle.getString("envioxml.envioprocessosxml"), envio.getId());
			registraLogAcoes(logMessage, ICodigosAcoes.ACAO_ENVIOXML_ENVIOPROCESSOS, token);
		}        
  }

  /**
   * Recupera os processos a serem enviados por instância e realiza um parsing nos mesmos
   * para validação da formatação do xml dos processos
   * @param competencia Competência de referência formato MM/yyyy
   * @param instancia A instância de referência: 1 = primeira instância; 2 = segunda instância
   * @param token
   */
  protected void preparaEnvioDadosXML(String competencia, int instancia, String token) {
  
    ControleCriacaoXml controle = dadosEnvioDao.getControleCriacaoXml(competencia, instancia);
    if (controle != null) {
      EnvioXML envio = envioXmlDao.getEnvioPorControleXml(controle.getId());
      List<EnvioProcessoXML> processos = envioProcessoDao.getEnvioProcessoPorEnvio(envio.getId());
      for(EnvioProcessoXML processo : processos) {
        String xml = processo.getElementoXml();
        InputStream xsd = this.getClass().getResourceAsStream("/envio_xml/modelo_intercomunicacao_base.xsd");
        try {
          envioXmlDao.validateXsd(IOUtils.toInputStream(xml), xsd);
          envioProcessoDao.alteraEnvioProcessoEnviado(processo.getId(), "", true);
        } catch (Exception e) {
          envioProcessoDao.alteraEnvioProcessoEnviado(processo.getId(), e.getMessage(), false);
        }
      }          
      
    } else {
      throw new ParametroException(bundle.getString("envioxml.erro.processamentoxml.inexistente"), 
          ICodigosErros.ERRO_ENVIOXML_ARQUIVOJACRIADO);
    }
  }
  
  
  protected void geraDadosEnvioXML(String competencia, int instancia, String token) {
    
    List<Permissao> permissoes =
      addNovaPermissaoList(null, properties.getProperty("perm.formulario.envioxml.usuario"));
    validaTokenUsuario(token, permissoes);
    ControleCriacaoXml controle = dadosEnvioDao.getControleCriacaoXml(competencia, instancia);
    if (controle == null) {
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(token);
      UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usuario.lgn);
      controle = dadosEnvioDao.newControleCriacaoXml(competencia, instancia, usuarioLog.id);

      try {
				dadosEnvioDao.geraXml(competencia, instancia);
			} catch (ParametroException e) {
				logger.error("Erro ao gerar xml para envio: " + e.getMessage());
				dadosEnvioDao.removeControleCriacaoXml(controle.getId());
				throw e;
			}
      
      String logMessage = 
          MessageFormat.format(
              bundle.getString("envioxml.novoarquivoxml.mensagemlog"), 
              competencia, instancia);
      registraLogAcoes(logMessage, ICodigosAcoes.ACAO_ENVIOXML_CRIACAOARQUIVO, token);        
    } else {
    	logger.error(bundle.getString("envioxml.erro.processamentoxml.existente"));    	
      throw new ParametroException(bundle.getString("envioxml.erro.processamentoxml.existente"), 
          ICodigosErros.ERRO_ENVIOXML_ARQUIVOJACRIADO);
    }
  }
  
  private void enviaEmail(String mensagem, String emailDestino) {
		MailMessage message = new MailMessage();
		message.setFrom(properties.getProperty("smtp.email.from"));
		message.setTo(emailDestino);
		message.setCopia(properties.getProperty("smtp.email.from"));
		message.setSubject(properties.getProperty("smtp.email.EnvioDadosCNJ.subject"));
		message.setText(mensagem);
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
}
