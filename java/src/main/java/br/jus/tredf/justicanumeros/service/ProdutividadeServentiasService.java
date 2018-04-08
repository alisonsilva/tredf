package br.jus.tredf.justicanumeros.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.jus.tredf.justicanumeros.dao.CartorioDao;
import br.jus.tredf.justicanumeros.dao.FormularioDao;
import br.jus.tredf.justicanumeros.dao.GrupoDao;
import br.jus.tredf.justicanumeros.dao.ProtocoloProcessoDao;
import br.jus.tredf.justicanumeros.dao.SadpDao;
import br.jus.tredf.justicanumeros.dao.ServentiasDao;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.model.Campo;
import br.jus.tredf.justicanumeros.model.Cartorio;
import br.jus.tredf.justicanumeros.model.ClasseSadp;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.ProdutividadeServentias;
import br.jus.tredf.justicanumeros.model.ProtocoloProcesso;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.GrupoVO;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.util.AuthenticationService;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Service(value = "produtividadeServentiasService")
@Transactional
public class ProdutividadeServentiasService extends FormulariosTREDFService {
  @Autowired
  private ResourceBundle bundle;
  
  @Autowired
  private PropertiesServiceController properties;

  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private FormularioDao formularioDao;
  
  @Autowired
  private CartorioDao cartorioDao;
  
  @Autowired
  private ServentiasDao serventiasDao;
  
  @Autowired
  private UsuarioDao usuarioDao;
  
  @Autowired
  private GrupoDao grupoDao;
  
  @Autowired
  private ProtocoloProcessoDao protocoloProcessoDao;
  
  @Autowired 
  private SadpDao sadpDao; 
  
  /**
   * Fecha o formul�rio de produtividade de serventias para preenchimento
   * @param idProdutividade Identificador �nico da produtividade serventias
   * @param token Token de valida��o do usu�rio
   */
  public void fechaProdutividadeServentias(Long idProdutividade, String token) {
    if(idProdutividade == null || idProdutividade <= 0) {
      throw new ParametroException("� preciso identificar o formul�rio", 
          ICodigosErros.ERRO_PROTOCOLO_PARAMETROSINVALIDOS);
    }
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    List<GrupoVO> grupos = grupoDao.getGruposPorLogin(usr.lgn);
    ProdutividadeServentias prodServ = serventiasDao.getServentiaPorId(idProdutividade);
    for(Permissao permissao : permissoes) {
      if(permissao.getDescricao().equals(
          properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario")) 
            && permissao.isAvaliada()) {        
        boolean grupoRefereACartorio = false;
        for(GrupoVO grupo : grupos) {
          for(Permissao perm : grupo.permissoes){
            if(perm.getDescricao().equals(permissao.getDescricao()) &&
                prodServ.getCartorio() != null && 
                grupo.cartorioId == prodServ.getCartorio().getId()) {
              grupoRefereACartorio = true;
            }
          }
        }
        if(!grupoRefereACartorio) {
          throw new ParametroException(
              bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
              ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
        }
      } else {
        throw new ParametroException(
            bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
            ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
      }
    }
    
    //caso o formul�rios esteja fechado para preenchimento
    if(serventiasDao.isFechadoOuExpirado(idProdutividade)) {
      throw new ParametroException(
          bundle.getString("ProdutividadeServentiaService.validacaofechamentoprodserv"), 
          ICodigosErros.ERRO_SERVENTIAS_FECHADOPREENCHIMENTO);
    }  
    
    serventiasDao.alteraFechamentoProdutividadeServentia(idProdutividade, true);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("ProdutividadeServentiaService.fechaProdutividadeServentias.mensagemlog"), 
            idProdutividade);
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    registraLogEvento(logMessage, prodServ.getId(), usuarioLog); 
  }
  

  /**
   * Duplica os dados de protocolos referentes � compet�ncia anterior na
   * compet�ncia da produtividade passada
   * @param idProdutividade Identificador �nico da produtividade a ser 
   * utilizada como refer�ncia para que os dados de protocolos sejam
   * duplicados na mesma
   * @param token Token de valida��o do usu�rio
   */
  public void preencheProtocolosCompetenciaAnterior(Long idProdutividade, String token) {
    if(idProdutividade == null || idProdutividade <= 0) {
      throw new ParametroException("� preciso identificar o formul�rio", 
          ICodigosErros.ERRO_PROTOCOLO_PARAMETROSINVALIDOS);
    }
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    List<GrupoVO> grupos = grupoDao.getGruposPorLogin(usr.lgn);
    ProdutividadeServentias prodServ = serventiasDao.getServentiaPorId(idProdutividade);
    for(Permissao permissao : permissoes) {
      if(permissao.getDescricao().equals(
          properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario")) 
            && permissao.isAvaliada()) {        
        boolean grupoRefereACartorio = false;
        for(GrupoVO grupo : grupos) {
          for(Permissao perm : grupo.permissoes){
            if(perm.getDescricao().equals(permissao.getDescricao()) &&
                prodServ.getCartorio() != null && 
                grupo.cartorioId == prodServ.getCartorio().getId()) {
              grupoRefereACartorio = true;
            }
          }
        }
        if(!grupoRefereACartorio) {
          throw new ParametroException(
              bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
              ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
        }
      } else {
        throw new ParametroException(
            bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
            ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
      }
    }
    
    //caso o formul�rios esteja fechado para preenchimento
    if(serventiasDao.isFechadoOuExpirado(idProdutividade)) {
      throw new ParametroException(
          bundle.getString("ProdutividadeServentiaService.validacaofechamentoprodserv"), 
          ICodigosErros.ERRO_SERVENTIAS_FECHADOPREENCHIMENTO);
    }  
    
    serventiasDao.preencheProtocolosCompetenciaAnterior(idProdutividade);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("ProdutividadeServentiaService.preencheProtocolosCompetenciaAnterior.mensagemlog"), 
            idProdutividade);
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    registraLogEvento(logMessage, prodServ.getId(), usuarioLog); 
  }
  
  
  /**
   * Altera o nome do protocolo para o protocolo passado
   * @param idProtocolo Identificador do protocolo para o qual se deseja alterar o valor
   * @param vlrProtocolo Novo valor para o protocolo
   * @param token O token para valida��o das permiss�es do usu�rio
   */
  public List<Cartorio> cartoriosUsuario(String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    List<GrupoVO> grupos = grupoDao.getGruposPorLogin(usr.lgn);
    List<Cartorio> cartorios = usuarioDao.cartoriosPorUsuario(usr.lgn);
    for(Permissao permissao : permissoes) {
      if(permissao.getDescricao().equals(
          properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario")) 
            && permissao.isAvaliada()) {
        boolean grupoRefereACartorio = false;
        for(GrupoVO grupo : grupos) {
          for(Permissao perm : grupo.permissoes){
            if(perm.getDescricao().equals(permissao.getDescricao())) {
              grupoRefereACartorio = true;
            }
          }
        }
        if(!grupoRefereACartorio) {
          throw new ParametroException(
              bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
              ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
        }
      } else {
        throw new ParametroException(
            bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
            ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
      }
    }
    
    return cartorios;
  }   
  
  /**
   * Altera o nome do protocolo para o protocolo passado
   * @param idProtocolo Identificador do protocolo para o qual se deseja alterar o valor
   * @param vlrProtocolo Novo valor para o protocolo
   * @param token O token para valida��o das permiss�es do usu�rio
   */
  public void alteraProtocolo(Long idProtocolo, String vlrProtocolo, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    List<GrupoVO> grupos = grupoDao.getGruposPorLogin(usr.lgn);
    ProtocoloProcesso protocolo = protocoloProcessoDao.getProtocoloById(idProtocolo);
    for(Permissao permissao : permissoes) {
      if(permissao.getDescricao().equals(
          properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario")) 
            && permissao.isAvaliada()) {
        ProdutividadeServentias produtividade = 
            serventiasDao.getProdutividadeServentiaPorCampo(protocolo.getCampo().getId());
        boolean grupoRefereACartorio = false;
        for(GrupoVO grupo : grupos) {
          for(Permissao perm : grupo.permissoes){
            if(perm.getDescricao().equals(permissao.getDescricao())
                && grupo.cartorioId == produtividade.getCartorio().getId()) {
              grupoRefereACartorio = true;
            }
          }
        }
        if(!grupoRefereACartorio) {
          throw new ParametroException(
              bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
              ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
        }
      } else {
        throw new ParametroException(
            bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
            ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
      }
    }
    
    //caso o formul�rios esteja fechado para preenchimento
    if(serventiasDao.isFechadoOuExpiradoPorProtocolo(idProtocolo)) {
      throw new ParametroException(
          bundle.getString("ProdutividadeServentiaService.validacaofechamentoprodserv"), 
          ICodigosErros.ERRO_SERVENTIAS_FECHADOPREENCHIMENTO);
    }
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoFormularioService.alterarProtocolo.mensagemlog"), 
            protocolo.getCampo().getId(), protocolo.getId(), protocolo.getProtocolo());
    Long idProdServentia = serventiasDao.getServentiaPorCampo(protocolo.getCampo().getId()).getId();
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    
    ProtocoloProcesso prot = new ProtocoloProcesso();
    prot.setProtocolo(vlrProtocolo);
    prot.setId(idProtocolo);
    
    protocoloProcessoDao.alterarProtocolo(prot);
    registraLogEvento(logMessage, idProdServentia, usuarioLog);
  } 
  
  /**
   * Insere um novo protocolo para um determinado campo.
   * @param protocolo O n�mero do protocolo a ser inserido
   * @param idCampo Identificador do campo para o qual ser� inserido o protocolo
   * @param token O token para valida��o das permiss�es do usu�rio
   * @return
   */
  public ProtocoloProcesso inserirProtocolo(String protocolo, Long idCampo, String token) {
    if(StringUtils.isEmpty(protocolo) || !StringUtils.isNumeric(protocolo)) {
      throw new ParametroException("Protocolo inv�lido", ICodigosErros.ERRO_SADP_PARAMETROINVALIDO);
    }
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    List<GrupoVO> grupos = grupoDao.getGruposPorLogin(usr.lgn);
    for(Permissao permissao : permissoes) {
      if(permissao.getDescricao().equals(
          properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario")) 
            && permissao.isAvaliada()) {
        ProdutividadeServentias produtividade = 
            serventiasDao.getProdutividadeServentiaPorCampo(idCampo);
        boolean grupoRefereACartorio = false;
        for(GrupoVO grupo : grupos) {
          for(Permissao perm : grupo.permissoes){
            if(perm.getDescricao().equals(permissao.getDescricao())
                && grupo.cartorioId == produtividade.getCartorio().getId()) {
              grupoRefereACartorio = true;
            }
          }
        }
        if(!grupoRefereACartorio) {
          throw new ParametroException(
              bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
              ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
        }
      } else {
        throw new ParametroException(
            bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
            ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
      }
    }
    
    //caso o formul�rios esteja fechado para preenchimento
    if(serventiasDao.isFechadoOuExpiradoPorCampo(idCampo)) {
      throw new ParametroException(
          bundle.getString("ProdutividadeServentiaService.validacaofechamentoprodserv"), 
          ICodigosErros.ERRO_SERVENTIAS_FECHADOPREENCHIMENTO);
    }    
    
    if(!sadpDao.isProtocoloValidoSadp(Long.valueOf(protocolo))) {
      throw new ParametroException(
          bundle.getString("ProdutividadeServentiaService.validacaonumeroprotocolo"), 
          ICodigosErros.ERRO_SADP_PROTOCOLOINVALIDO);
    }
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoFormularioService.inserirProtocolo.mensagemlog"), 
            idCampo, protocolo);
    Long idProdServentia = serventiasDao.getServentiaPorCampo(idCampo).getId();
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    
    ProtocoloProcesso prot = new ProtocoloProcesso();
    prot.setProtocolo(protocolo);
    Campo campo = new Campo();
    campo.setId(idCampo);
    prot.setCampo(campo);
    
    prot = protocoloProcessoDao.inserirProtocolo(prot);
    ClasseSadp classe = sadpDao.getClasseProcesso(Long.valueOf(protocolo));
    prot.setClasse(classe);

    registraLogEvento(logMessage, idProdServentia, usuarioLog);
    
    return prot;
  }   
  
  /**
   * Removo o protocolo de um processo referente � um item de 
   * estat�stica.
   * @param idProtocolo Identificador do protocolo a ser removido.
   * @param token O token para valida��o das permiss�es do usu�rio
   */
  public void removeProtocolo(Long idProtocolo, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    List<GrupoVO> grupos = grupoDao.getGruposPorLogin(usr.lgn);
    ProtocoloProcesso protocolo = protocoloProcessoDao.getProtocoloById(idProtocolo);
    for(Permissao permissao : permissoes) {
      if(permissao.getDescricao().equals(
          properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario")) 
            && permissao.isAvaliada()) {
        ProdutividadeServentias produtividade = serventiasDao.getProdutividadeServentiaPorCampo(protocolo.getCampo().getId());
        boolean grupoRefereACartorio = false;
        for(GrupoVO grupo : grupos) {
          for(Permissao perm : grupo.permissoes){
            if(perm.getDescricao().equals(permissao.getDescricao())
                && grupo.cartorioId == produtividade.getCartorio().getId()) {
              grupoRefereACartorio = true;
            }
          }
        }
        if(!grupoRefereACartorio) {
          throw new ParametroException(
              bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
              ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
        }
      } else {
        throw new ParametroException(
            bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
            ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
      }
    }
    
    //caso o formul�rios esteja fechado para preenchimento
    if(serventiasDao.isFechadoOuExpiradoPorProtocolo(idProtocolo)) {
      throw new ParametroException(
          bundle.getString("ProdutividadeServentiaService.validacaofechamentoprodserv"), 
          ICodigosErros.ERRO_SERVENTIAS_FECHADOPREENCHIMENTO);
    }    
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoFormularioService.removeProtocolo.mensagemlog"), 
            protocolo.getCampo().getId(), protocolo.getId(), protocolo.getProtocolo());
    Long idProdServentia = serventiasDao.getServentiaPorCampo(protocolo.getCampo().getId()).getId();
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    protocoloProcessoDao.removerProtocolo(idProtocolo);
    registraLogEvento(logMessage, idProdServentia, usuarioLog);
  }   
  
  /**
   * Altera o valor de um campo em espec�fico para um formul�rio de Produtividade de Serventias.
   * Esse campo se refere a um indicador para uma compet�ncia e um cart�rio em espec�fico.
   * @param idCampo Identificador �nico do campo a ser alterado.
   * @param valor Novo valor a ser atribu�do ao campo.
   * @param token O token para valida��o das permiss�es do usu�rio
   */
  public void alteraValorCampo(Long idCampo, Double valor, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    List<GrupoVO> grupos = grupoDao.getGruposPorLogin(usr.lgn);
    for(Permissao permissao : permissoes) {
      if(permissao.getDescricao().equals(
          properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario")) 
            && permissao.isAvaliada()) {
        ProdutividadeServentias produtividade = serventiasDao.getProdutividadeServentiaPorCampo(idCampo);
        boolean grupoRefereACartorio = false;
        for(GrupoVO grupo : grupos) {
          for(Permissao perm : grupo.permissoes){
            if(perm.getDescricao().equals(permissao.getDescricao())
                && grupo.cartorioId == produtividade.getCartorio().getId()) {
              grupoRefereACartorio = true;
            }
          }
        }
        if(!grupoRefereACartorio) {
          throw new ParametroException(
              bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
              ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
        }
      } else {
        throw new ParametroException(
            bundle.getString("FormulariosTREDFService.validarPermissaoUsuario.usuario_sem_permissao"), 
            ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO);
      }
    }

    //caso o formul�rios esteja fechado para preenchimento
    if(serventiasDao.isFechadoOuExpiradoPorCampo(idCampo)) {
      throw new ParametroException(
          bundle.getString("ProdutividadeServentiaService.validacaofechamentoprodserv"), 
          ICodigosErros.ERRO_SERVENTIAS_FECHADOPREENCHIMENTO);
    }  
    
    serventiasDao.alteraValorCampo(idCampo, valor);

    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoFormularioService.alteraValorCampo.mensagemlog"), 
            idCampo);
    Long idProdServentia = serventiasDao.getServentiaPorCampo(idCampo).getId();
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    registraLogEvento(logMessage, idProdServentia, usuarioLog);
  }  
}
