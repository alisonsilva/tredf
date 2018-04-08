package br.jus.tredf.justicanumeros.service;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import br.jus.tredf.justicanumeros.model.Formulario;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.ProdutividadeServentias;
import br.jus.tredf.justicanumeros.model.ProtocoloProcesso;
import br.jus.tredf.justicanumeros.model.VisualizacaoCartoriosInfo;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.util.AuthenticationService;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Service(value = "administracaoFormulariosService")
@Transactional
public class AdministracaoFormulariosService extends FormulariosTREDFService {

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
   * Recupera uma listagem com todos os formulários cadastrados. 
   * O usuário deverá ter permissão para executar tal procedimento. 
   * @param usuario Usuário recuperado com as informações do token
   * @return Lista com os formularios encontrados
   */
  public List<Formulario> getTodosFormularios(UsuarioVO usuario) {
    authenticationService.validaToken(usuario.token);
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.formulario.listar.formularios"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.ins.formulario.local"));
    
    validarPermissaoUsuario(usuario, permissoes);
    List<Formulario> formularios = formularioDao.getTodosFormularios();
    UsuarioVO usr = authenticationService.getUsuarioFromToken(usuario.token);
    usuario.token = authenticationService.criaToken(usr.lgn, usr.sn);
    return formularios;    
  }  
  
  /**
   * Verifica se o formulário está fechado para edição
   * @param idProdutividade Identificador único do formulário a ser analisado se está 
   * fechado ou não
   * @param token Token de validação do usuário
   * @return True, caso o formulário esteja fechado para edição; False, caso contrário
   */
  public boolean isFormularioFechado(Long idProdutividade, String token) {
    authenticationService.validaToken(token);
    return serventiasDao.isFechado(idProdutividade);
  }
  
  /**
   * Recupera um formulário com suas permissões a partir de seu identificador único
   * @param idFormulario Identificador único do formulário sendo recuperado  
   * @param token token para validação da operação
   * @return O formulário recuperado
   */
  public Formulario getFormularioById(String token, Long idFormulario) {
    authenticationService.validaToken(token);
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.formulario.listar.formularios"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.ins.formulario.local"));
    
    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    validarPermissaoUsuario(usr, permissoes);
    return formularioDao.getFormularioById(idFormulario);
  }
  
  /**
   * Altera o formulário passado 
   * @param token token para validação das permissões do usuário
   * @param formulario o formulário a ser alterado
   */
  public void alterarFormulario(String token, Formulario formulario) {
    if(formulario == null || formulario.getId() == null || 
        StringUtils.isEmpty(formulario.getNome()) || 
        StringUtils.isEmpty(formulario.getDescricao())) {
      throw new ParametroException(
          bundle.getString("FormularioDao.alteraFormulario.erro.dadosInvalidos"), 
          ICodigosErros.ERRO_FORMULARIO_ALTERARFORMULARIO);
    }
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.formulario.alterar.formularios"));
    validaTokenUsuario(token, permissoes);
    formularioDao.alteraFormulario(formulario);
  }

  /**
   * Recupera a listagem com os cartórios disponíveis. Não é necessário ter 
   * permissão especial para executar essa operação.
   * @return Listagem com os cartórios disponíveis.
   */
  public List<Cartorio> getCartorios() {
    return cartorioDao.getCartorios();
  }
  
  /**
   * Cria novo preenchimento para Produtividade de Serventia para uma determinada
   * competência e cartório passados como parâmetro. Pode ser criado também para
   * todos os cartórios, dependendo se o campo todosCartorios está TRUE.
   * @param dtCompetencia A competência a ser utilizada para criação de novo preenchimento
   * @param idCartorio O cartório (serventia) de referência para o preenchimento
   * @param todosCartorios Booleano que indica se vai criar esse formulário de preenchimento para todos os cartórios
   * @param dtLimite A data limite de preenchimento desse formulário
   * @param token O token para validação das permissões do usuário
   * @return Registro de preenchimento para produtividade da serventias criada
   */
  public ProdutividadeServentias novaProdutividadeServentias(Date dtCompetencia, 
      Long idCartorio, 
      boolean todosCartorios,
      Date dtLimite,
      String token) {
    if (dtCompetencia == null || (idCartorio == null && !todosCartorios)) {
      throw new ParametroException(
          bundle.getString("AdministracaoFormularioService.novaProdutividadeServentias.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.grupo.ins.produtividadeserventias"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.ins.formulario"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.ins.formulario.local"));
    validaTokenUsuario(token, permissoes);
    

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);

    ProdutividadeServentias prodServ = new ProdutividadeServentias();

    if (todosCartorios) {
      List<Cartorio> cartorios = cartorioDao.getCartorios();
      for(Cartorio cartorio : cartorios) {
        prodServ = new ProdutividadeServentias();
        prodServ.setDtCompetencia(dtCompetencia);
        if(dtLimite != null) {
          prodServ.setDtPreenchimento(dtLimite);
        }
        prodServ.setCartorio(cartorio);
        serventiasDao.novoPreenchimento(prodServ);
        prodServ = serventiasDao.getServentiaPorCompetenciaCartorio(dtCompetencia, cartorio.getId());
      }
    } else {
      Cartorio cartorio = new Cartorio();
      cartorio.setId(idCartorio);
      prodServ.setDtCompetencia(dtCompetencia);
      if(dtLimite != null) {
        prodServ.setDtPreenchimento(dtLimite);
      }
      prodServ.setCartorio(cartorio);
      serventiasDao.novoPreenchimento(prodServ);
      prodServ = serventiasDao.getServentiaPorCompetenciaCartorio(dtCompetencia, idCartorio);
    }
    
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoFormularioService.novaProdutividadeServentias.mensagemlog"), 
            sdf.format(dtCompetencia), idCartorio, todosCartorios, dtLimite!= null ? sdf.format(dtLimite) : "");
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    registraLogEvento(logMessage, prodServ.getId(), usuarioLog);
    
    return prodServ;
  }
  
  /**
   * Recupera a quantidade de protocolos ou valor referente ao campo passado.
   * Como um campo pode armazenar protocolos ou um valor fixo, é necessário
   * fazer essa crítica.
   * @param idCampo Identificador do campo para realizar essa pesquisa
   * @return Quantidade identificada para o campo: quantidade de protocolos, ou valor fixo
   */
  public int getValorcampo(Long idCampo) {
    return serventiasDao.getValorCampo(idCampo);
  }
  
  /**
   * Recupera informações sobre produtividade de serventia para o cartório passado
   * dentro da competência
   * @param dtCompetencia Competência para a qual se deseja realizar a consulta
   * @param idCartorio Cartório para o qual se deseja realizar a consulta
   * @return Produtividade da serventia encontrada de acordo com os parâmetros
   */
  public ProdutividadeServentias getServentiaPorCompetenciaCartorio(
      Date dtCompetencia, Long idCartorio) {
    if (dtCompetencia == null || idCartorio == null) {
      throw new ParametroException(
          bundle.getString("AdministracaoFormularioService.novaProdutividadeServentias.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    return serventiasDao.getServentiaPorCompetenciaCartorio(dtCompetencia, idCartorio);
  }
  

  /**
   * Altera a data de preenchimento limite para o formulário identificado
   * por sua data de competência e seu cartório
   * @param dtCompetencia Data de competência do formulário
   * @param idCartorio Identificador único do cartório para o qual será realizada a mudança 
   * na data limite
   * @param dtLimite Data limite nova
   * @param token Token de validação do usuário
   */
  public void alterarDataLimitePreenchimento(String dtCompetencia, 
      Long idCartorio, 
      String dtLimite, 
      String token) {
    if (dtCompetencia == null || idCartorio == null) {
      throw new ParametroException(
          bundle.getString("AdministracaoFormularioService.novaProdutividadeServentias.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.grupo.ins.produtividadeserventias"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.ins.formulario"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.ins.formulario.local"));
    validaTokenUsuario(token, permissoes);
    

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
   
    ProdutividadeServentias prodServ;
    try {
      prodServ = serventiasDao.getServentiaPorCompetenciaCartorio(sdf.parse(dtCompetencia), idCartorio);
      if(prodServ == null) {
        throw new ParametroException("Não foi encontrado formulário para o período informado", 
            ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
      }
      serventiasDao.alteraDataLimite(prodServ.getId(), StringUtils.isNotEmpty(dtLimite) ? sdf.parse(dtLimite) : null);
    } catch (ParseException e) {
      throw new ParametroException("Data com formatação errada: " + e.getMessage(), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoFormularioService.alteraDataLimite.mensagemlog"), 
            dtCompetencia, idCartorio, dtLimite);
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    registraLogEvento(logMessage, prodServ.getId(), usuarioLog);    
  }

  /**
   * Altera o valor de um campo em específico para um formulário de Produtividade de Serventias.
   * Esse campo se refere a um indicador para uma competência e um cartório em específico.
   * @param idCampo Identificador único do campo a ser alterado.
   * @param valor Novo valor a ser atribuído ao campo.
   * @param token O token para validação das permissões do usuário
   */
  public void alteraValorCampo(Long idCampo, Double valor, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("prem.formulario.alterar.campo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.alterar.campo.admlocal"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);

    serventiasDao.alteraValorCampo(idCampo, valor);

    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoFormularioService.alteraValorCampo.mensagemlog"), 
            idCampo);
    Long idProdServentia = serventiasDao.getServentiaPorCampo(idCampo).getId();
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    registraLogEvento(logMessage, idProdServentia, usuarioLog);
  }
  
  /**
   * Recupera todos os protocolos dos processos referentes a um determinado campo
   * de estatística.
   * @param idCampo Identificador único do campo a ser alterado.
   * @param token O token para validação das permissões do usuário
   */
  public List<ProtocoloProcesso> protocolosPorCampo(Long idCampo, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("prem.formulario.alterar.campo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.alterar.campo.admlocal"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));
    
    validaTokenUsuario(token, permissoes);
    
    List<ProtocoloProcesso> protocolos = protocoloProcessoDao.getProtocolosPorCampo(idCampo);
    for(ProtocoloProcesso protocolo : protocolos) {
      ClasseSadp classe = sadpDao.getClasseProcesso(Long.valueOf(protocolo.getProtocolo()));
      protocolo.setClasse(classe);
    }
    return protocolos;
  }  
  
  /**
   * Removo o protocolo de um processo referente à um item de 
   * estatística.
   * @param idProtocolo Identificador do protocolo a ser removido.
   * @param token O token para validação das permissões do usuário
   */
  public void removeProtocolo(Long idProtocolo, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("prem.formulario.alterar.campo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.alterar.campo.admlocal"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    ProtocoloProcesso protocolo = protocoloProcessoDao.getProtocoloById(idProtocolo);
    
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
   * Insere um novo protocolo para um determinado campo.
   * @param protocolo O número do protocolo a ser inserido
   * @param idCampo Identificador do campo para o qual será inserido o protocolo
   * @param token O token para validação das permissões do usuário
   * @return
   */
  public ProtocoloProcesso inserirProtocolo(String protocolo, Long idCampo, String token) {
    if(StringUtils.isEmpty(protocolo) || !StringUtils.isNumeric(protocolo)) {
      throw new ParametroException("Protocolo inválido", ICodigosErros.ERRO_SADP_PARAMETROINVALIDO);
    }
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("prem.formulario.alterar.campo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.alterar.campo.admlocal"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    
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
   * Modifica o status de fechamento do formulário de preenchimento de produtividade
   * da serventia.
   * @param dtCompetencia Data da competência. String no formato dd/MM/yyyy
   * @param idCartorio Identificador único da serventia (cartório).
   * @param fechado Status do fenchamento podendo ser true (fechar) ou false (abrir)
   * @param token Token de validação do usuário
   */
  public void alterarFechamentoFormulario(String dtCompetencia, Long idCartorio, boolean fechado, String token) {
    if(StringUtils.isEmpty(dtCompetencia) || idCartorio <= 0) {
      throw new ParametroException("Campos inválidos", 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("prem.formulario.alterar.campo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.alterar.campo.admlocal"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date dtCompetenciaConv = null;
    try {
      dtCompetenciaConv = sdf.parse(dtCompetencia);
    } catch (ParseException e) {
      throw new ParametroException("Formatação da data está incorreta", 
          ICodigosErros.ERRO_FORMULARIO_ALTERARFORMULARIO);
    }
    ProdutividadeServentias produtividadeServ = 
        serventiasDao.getServentiaPorCompetenciaCartorio(dtCompetenciaConv, idCartorio);
    
    serventiasDao.alteraFechamentoProdutividadeServentia(produtividadeServ.getId(), fechado);

    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoFormularioService.alterarFechamentoFormulario.mensagemlog"), 
            produtividadeServ.getId(), fechado);
    UsuarioVO usuarioLog = usuarioDao.recuperarUsuarioPorLogin(usr.lgn);
    
    
    registraLogEvento(logMessage, produtividadeServ.getId(), usuarioLog);
    
  }   
  
  /**
   * Altera o nome do protocolo para o protocolo passado
   * @param idProtocolo Identificador do protocolo para o qual se deseja alterar o valor
   * @param vlrProtocolo Novo valor para o protocolo
   * @param token O token para validação das permissões do usuário
   */
  public void alteraProtocolo(Long idProtocolo, String vlrProtocolo, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("prem.formulario.alterar.campo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.alterar.campo.admlocal"));
    
    validaTokenUsuario(token, permissoes);

    UsuarioVO usr = authenticationService.getUsuarioFromToken(token);
    ProtocoloProcesso protocolo = protocoloProcessoDao.getProtocoloById(idProtocolo);
    
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
   * Recupera informações consolidadas sobre o status de cada preenchimento, 
   * cartório a cartório, na competência
   * @param competencia A competência do preenchimento
   * @param token Token de validação do permissionamento do usuário
   * @return Listagem com os valores com informações sobre o preenchimento dos cartórios 
   * na competência
   */
  public List<VisualizacaoCartoriosInfo> relatorioVisualizacaoCartoriosCompetencia(String competencia, String token) {
    if(StringUtils.isEmpty(competencia)) {
      throw new ParametroException("Campos inválidos", 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date dtCompetencia = null;
    try {
      dtCompetencia = sdf.parse(competencia);
    } catch (ParseException e) {
      throw new ParametroException("Formatação da data está inválida", 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("prem.formulario.alterar.campo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.formulario.alterar.campo.admlocal"));
    
    validaTokenUsuario(token, permissoes);
    return serventiasDao.relatorioVisualizacaoCartoriosCompetencia(dtCompetencia);
  }

}
