package br.jus.tredf.justicanumeros.service.terceirizado;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.jus.tredf.justicanumeros.dao.GrupoDao;
import br.jus.tredf.justicanumeros.dao.ICodigosAcoes;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.dao.terceirizado.AreaAtuacaoDao;
import br.jus.tredf.justicanumeros.dao.terceirizado.GrauInstrucaoDao;
import br.jus.tredf.justicanumeros.dao.terceirizado.LotacaoTerceirizadoDao;
import br.jus.tredf.justicanumeros.dao.terceirizado.TerceirizadoDao;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.terceirizado.AreaAtuacao;
import br.jus.tredf.justicanumeros.model.terceirizado.GrauInstrucao;
import br.jus.tredf.justicanumeros.model.terceirizado.LotacaoTerceirizado;
import br.jus.tredf.justicanumeros.model.terceirizado.Terceirizado;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.FormulariosTREDFService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Service(value = "terceirizadoService")
@Transactional
public class TerceirizadoTREDFService extends FormulariosTREDFService {
  @Autowired
  private ResourceBundle bundle;
  
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
  private LotacaoTerceirizadoDao lotacaoTerceirizadoDao;
  

  /**
   * Recupera uma listagem com todos os terceirizados 
   * (ativos e inativos / estagi�rios ou funcion�rios)
   * @param token O token de valida��o de usu�rio
   * @return Listagem com todos os terceirizados
   */
  public List<Terceirizado> todosTerceirizados(String token) {
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.forcatrabalho.auxiliar.usuario"));    
    validaTokenUsuario(token, permissoes);        
    return terceirizadoDao.todosTerceirizados();
  } 
  
  /**
   * Recupera todos os terceirizados onde aparece o filtro dentro ou do Nome do Terceirizado, 
   * ou do Nome do Grau de Instru��o, ou do nome da Area de Atuacao
   * @param token O token de valida��o do usu�rio
   * @param filtro O filtro a ser utilizado para recupera��o das informa��es
   * @return Listagem com os terceirizados recuperados
   */
  public List<Terceirizado> getTerceirizadosFiltrados(String token, String filtro) {
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.forcatrabalho.auxiliar.usuario"));    
    validaTokenUsuario(token, permissoes);        
    return terceirizadoDao.getTerceirizadosFiltrados(filtro);    
  }
  
  /**
   * 
   * @return
   */
  public List<GrauInstrucao> todosGrauInstrucao() {
    return grauInstrucaoDao.getTodos();
  }
  
  /**
   * 
   * @return
   */
  public List<AreaAtuacao> todasAreasAtuacao() {
    return areaAtuacaoDao.getTodos();
  }
  
  public List<LotacaoTerceirizado> todasLotacoesTerceirizados() {
  	return lotacaoTerceirizadoDao.getLotacoesTerceirizados();
  }
  
  /**
   * 
   * @param terceirizado
   * @param token
   * @return Terceirizado 
   */
  public Terceirizado insereTerceirizado(Terceirizado terceirizado, String token) {
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.forcatrabalho.auxiliar.usuario"));
    addNovaPermissaoList(permissoes, 
        properties.getProperty("perm.formulario.forcatrabalho.auxiliar.admlocal"));
    validaTokenUsuario(token, permissoes);
    
    terceirizado = terceirizadoDao.insereTerceirizado(terceirizado);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("TerceirizadoTREDFService.incluiterceirizado.sucesso.mensagemLog"), 
            terceirizado.toString());
    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    registraLogTerceirizado(logMessage, terceirizado.getId(), usuarioLog, 
        ICodigosAcoes.ACAO_TERCEIRIZADO_INSERCAO);   
    return terceirizado;
  }
  
  /**
   * 
   * @param terceirizado
   * @param token
   */
  public void alterarTerceirizado(Terceirizado terceirizado, String token) {
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.forcatrabalho.auxiliar.usuario"));
    addNovaPermissaoList(permissoes, 
        properties.getProperty("perm.formulario.forcatrabalho.auxiliar.admlocal"));
    validaTokenUsuario(token, permissoes);
    
    terceirizadoDao.alteraTerceirizado(terceirizado);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("TerceirizadoTREDFService.alteraterceirizado.sucesso.mensagemLog"), 
            terceirizado.toString());
    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    registraLogTerceirizado(logMessage, terceirizado.getId(), usuarioLog, 
        ICodigosAcoes.ACAO_TERCEIRIZADO_ALTERACAO);     
  }  
  
  /**
   * 
   * @param terceirizado
   * @param token
   */
  public void apagarTerceirizado(Terceirizado terceirizado, String token) {
    List<Permissao> permissoes = 
        addNovaPermissaoList(null, 
            properties.getProperty("perm.formulario.forcatrabalho.auxiliar.usuario"));
    addNovaPermissaoList(permissoes, 
        properties.getProperty("perm.formulario.forcatrabalho.auxiliar.admlocal"));
    validaTokenUsuario(token, permissoes);
    
    Terceirizado tercApagado = terceirizadoDao.apagaTerceirizado(terceirizado);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("TerceirizadoTREDFService.apagarterceirizado.sucesso.mensagemLog"), 
            tercApagado.toString());
    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    registraLogTerceirizado(logMessage, terceirizado.getId(), usuarioLog, 
        ICodigosAcoes.ACAO_TERCEIRIZADO_ALTERACAO);     
  }   
}
