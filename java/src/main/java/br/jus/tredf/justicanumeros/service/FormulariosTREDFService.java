package br.jus.tredf.justicanumeros.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.jus.tredf.justicanumeros.dao.GrupoDao;
import br.jus.tredf.justicanumeros.dao.LogAcoesDao;
import br.jus.tredf.justicanumeros.dao.PermissaoDao;
import br.jus.tredf.justicanumeros.dao.ProdutividadeMagistradoDao;
import br.jus.tredf.justicanumeros.dao.ServentiasDao;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.dao.terceirizado.LogAcoesTerceirizadoDao;
import br.jus.tredf.justicanumeros.model.LogAcoes;
import br.jus.tredf.justicanumeros.model.LogAcoesServentia;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.ProdutividadeMagistrado;
import br.jus.tredf.justicanumeros.model.ProdutividadeServentias;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.terceirizado.LogAcoesTerceirizado;
import br.jus.tredf.justicanumeros.model.terceirizado.Terceirizado;
import br.jus.tredf.justicanumeros.model.wrapper.GrupoVO;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.util.AuthenticationService;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Service(value = "formulariosTredfService")
@Transactional
public class FormulariosTREDFService {
  
  @Autowired
  private ServentiasDao serventiasDao;
  
  @Autowired
  private ProdutividadeMagistradoDao produtividadeMagistradoDao;
  
  @Autowired
  private GrupoDao grupoDao;
  
  @Autowired
  private UsuarioDao usuarioDao;
  
  @Autowired
  private LogAcoesDao logAcoesDao;
  
  @Autowired
  private LogAcoesTerceirizadoDao logAcoesTerceirizadosDao;
  
  @Autowired
  private PermissaoDao permissaoDao;
  
  @Autowired
  private ResourceBundle bundle;
  
  @Autowired
  private PropertiesServiceController properties;

  @Autowired
  private AuthenticationService authenticationService;
  
  public void inserirServentia(ProdutividadeServentias serventia, UsuarioVO usuario) {
    if(usuario == null || StringUtils.isEmpty(usuario.lgn)) {
      throw new ParametroException(
          bundle.getString("FormulariosTREDFService.inserirServentia.usuario_invalido"), 
          ICodigosErros.ERRO_SERVENTIAS_INCLUIR);
    }
    List<Permissao> permissoes = new ArrayList<Permissao>();
    Permissao permissao = new Permissao();
    permissao.setDescricao(properties.getProperty("perm.grupo.ins.produtividadeserventias").trim());
    
    validarPermissaoUsuario(usuario, permissoes);
    
    ProdutividadeServentias srvt = serventiasDao.getServentiaPorCompetenciaCartorio(
        serventia.getDtCompetencia(), serventia.getCartorio().getId());
    if(srvt == null) {
      Long idServentia = serventiasDao.inserirServentia(serventia);
      UsuarioVO usr = usuarioDao.recuperarUsuarioPorLogin(usuario.lgn);
      LogAcoesServentia log = new LogAcoesServentia();
      log.setDescricao(bundle.getString("FormulariosTREDFService.inserirServentia.log_nova_serventia"));
      log.setIdServentia(idServentia);
      log.setUsuario(usr);
      serventiasDao.inserirLogServentias(log);
    } else {
      throw new ParametroException(
          bundle.getString("FormulariosTREDFService.inserirServentia.erro.serventia_existe_competencia"), 
          ICodigosErros.ERRO_SERVENTIAS_INCLUIR);
    }
  }

  public void inserirProducaoMagistrado(ProdutividadeMagistrado prodMagistrado, UsuarioVO usuario) {
    if(usuario == null || StringUtils.isEmpty(usuario.lgn)) {
      throw new ParametroException(
          bundle.getString("FormulariosTREDFService.inserirProducaoMagistrado.usuario_invalido"), 
          ICodigosErros.ERRO_FORMULARIOSSERVICE_INCPRODUCAOMAGISTRADO);
    }
    List<Permissao> permissoes = new ArrayList<Permissao>();
    Permissao permissao = new Permissao();
    permissao.setDescricao(properties.getProperty("perm.grupo.ins.produtividademagistrados").trim());
    
    validarPermissaoUsuario(usuario, permissoes);
    
    ProdutividadeServentias srvt = serventiasDao.getServentiaPorCompetenciaCartorio(
        prodMagistrado.getDtCompetencia(), prodMagistrado.getCartorio().getId());
    if(srvt == null) {
      Long idProducaoMag = produtividadeMagistradoDao.inserirProdutividade(prodMagistrado);
      UsuarioVO usr = usuarioDao.recuperarUsuarioPorLogin(usuario.lgn);
      LogAcoesServentia log = new LogAcoesServentia();
      log.setDescricao(bundle.getString("FormulariosTREDFService.inserirProducaoMagistrado.log_nova_producao"));
      log.setIdServentia(idProducaoMag);
      log.setUsuario(usr);
      produtividadeMagistradoDao.inserirLogProdutMag(log);
    } else {
      throw new ParametroException(
          bundle.getString("FormulariosTREDFService.inserirProducaoMagistrado.erro.produtividade_existe_competencia"), 
          ICodigosErros.ERRO_SERVENTIAS_INCLUIR);
    }
  }  
  
 
  /**
   * Valida se o usuário tem a permissão requerida. Caso não tenha, 
   * será lançada uma excessão indicando a falta de permissão.
   * @param usuario Usuário objeto de validação de permissão
   * @param permissoesNaoAtribuidasAoGrupo Permissões que o usuário deverá ter.
   * False, caso contrário.
   */
  protected void validarPermissaoUsuario(UsuarioVO usuario, List<Permissao> permissoes) {
    if(usuario == null || StringUtils.isEmpty(usuario.lgn)) {
      throw new ParametroException(
          bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_invalido"), 
          ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO);
    }
    List<GrupoVO> grupos = grupoDao.getGruposPorLogin(usuario.lgn);
    for (GrupoVO grupo : grupos) {
      List<Permissao> lstPermissoes = grupoDao.permissoesPorGrupo(grupo);
      for(Permissao permissao : lstPermissoes) {
        for(Permissao prm : permissoes) {
          if(permissao.getDescricao().equalsIgnoreCase(prm.getDescricao())){
            prm.setAvaliada(true);
          }
        }
      }
    }
    boolean ret = false;
    for(Permissao permi : permissoes) {
      if(permi.isAvaliada()) {
        ret = true;
        break;
      }
    }
    if (!ret) {
      throw new ParametroException(
          bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
          ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
    }
  }
 
  /**
   * Valida o token e as permissões do usuário responsável
   * @param token Token para ser utilizado na validação da requisição e permissões do usuário
   * @param permissoesNaoAtribuidasAoGrupo As permissões que o usuário deverá ter
   */
  protected void validaTokenUsuario(String token, List<Permissao> permissoes) {
    authenticationService.validaToken(token);
    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    validarPermissaoUsuario(usr, permissoes);
  }
  
  /**
   * Registra informações de log para a operação desejada
   * @param logMessage Mensagem a ser registrada
   * @param idProdServentia Identificador do formulário Produtividade da Serventia para o qual
   * houve registro
   * @param usuario Usuário que está realizando a operação
   */
  protected void registraLogEvento(String logMessage, Long idProdServentia, UsuarioVO usuario) {
    LogAcoesServentia log = new LogAcoesServentia();
    log.setDescricao(logMessage);
    log.setDtAcao(new Date());
    log.setIdServentia(idProdServentia);
    log.setUsuario(usuario);
    serventiasDao.inserirLogServentias(log);
  }
  
  /**
   * Registra inforamções de log para ações referentes a terceirizados
   * @param mensagem Mensagem a ser registrada para log de ações realizadas
   * @param idTerceirizacao Identificador único do terceirizado sendo afetado.
   * Poderá ser nulo em caso de remoção de terceirizado
   * @param usuario O usuário realizando a operação
   * @param codigoAcao Código da ação sendo realizada
   */
  protected void registraLogTerceirizado(String mensagem, Long idTerceirizacao, 
      UsuarioVO usuario, Integer codigoAcao) {
    LogAcoesTerceirizado log = new LogAcoesTerceirizado();
    log.setDescricao(mensagem);
    log.setAcao(codigoAcao);
    log.setDtAcao(new Date());

    if (idTerceirizacao != null && idTerceirizacao > 0) {
      Terceirizado terceirizado = new Terceirizado();
      terceirizado.setId(idTerceirizacao);
      log.setTerceirizado(terceirizado);
    }
    log.setUsuario(usuario);
    logAcoesTerceirizadosDao.insereLog(log);
  }
  
  /**
   * Insere um registro de log de ações.
   * @param logMessage A mensagem de log a ser registrada
   * @param codigoAcao O código da ação sendo realizada
   * @param usuario O usuário que está realizando a ação
   */
  protected void registraLogAcoes(String logMessage, int codigoAcao, String token) {
    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    usr = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    LogAcoes lg = new LogAcoes();
    lg.setCodAcao(codigoAcao);
    lg.setDescricao(logMessage);
    lg.setDtAtualizacao(new Date());
    lg.setUsuario(usr);
    logAcoesDao.inserirLogAcoes(lg);
  }
  
  /**
   * 
   * @param permissoesNaoAtribuidasAoGrupo
   * @param permissao
   * @return
   */
  protected List<Permissao> addNovaPermissaoList(List<Permissao> permissoes, String permissao) {
    if(permissoes == null) {
      permissoes = new ArrayList<Permissao>();
    }
    Permissao perm = new Permissao();
    perm.setDescricao(permissao);
    permissoes.add(perm);
    return permissoes;
  }  
}
