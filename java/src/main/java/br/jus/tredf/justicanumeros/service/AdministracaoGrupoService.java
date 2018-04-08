package br.jus.tredf.justicanumeros.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.jus.tredf.justicanumeros.dao.ActiveDirectoryDao;
import br.jus.tredf.justicanumeros.dao.CartorioDao;
import br.jus.tredf.justicanumeros.dao.GrupoDao;
import br.jus.tredf.justicanumeros.dao.ICodigosAcoes;
import br.jus.tredf.justicanumeros.dao.PermissaoDao;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.model.Cartorio;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.GrupoVO;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.util.AuthenticationService;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Service(value = "administracaoGrupoService")
@Transactional
public class AdministracaoGrupoService extends FormulariosTREDFService {
  @Autowired
  private ResourceBundle bundle;
  
  @Autowired
  private PropertiesServiceController properties;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private GrupoDao grupoDao;
    
  @Autowired
  private UsuarioDao usuarioDao;  
  
  @Autowired
  private ActiveDirectoryDao activeDirectoryDao;
  
  @Autowired
  private PermissaoDao permissaoDao;
  
  @Autowired
  private CartorioDao cartorioDao;
  
  /**
   * Recupera todos os grupos cadastrados
   * @return Listagem com todos os grupos cadastrados
   */
  public List<GrupoVO> getGrupos() {
    return grupoDao.getTodosGrupos();
  }
  
  /**
   * Recupera todos os cart�rios dispon�veis
   * @return Listagem com os cart�rios dispon�veis
   */
  public List<Cartorio> getCartorios() {
    return cartorioDao.getCartorios();
  }
  
  /**
   * Cria um novo grupo. O usu�rio dever� ter as permiss�es para isso. 
   * @param nome Nome do grupo a ser criado
   * @param descricao Breve descri��o do grupo a ser criado
   * @param token Token de valida��o do usu�rio e da sess�o
   * @return O grupo criado
   */
  public GrupoVO novoGrupo(String nome, String descricao, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);

    GrupoVO grupo = new GrupoVO();
    grupo.nome = nome;
    grupo.descricao = descricao;
    
    grupo = grupoDao.insereGrupo(grupo);

    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoGrupoService.novogrupo.mensagemlog"), 
            grupo.nome);
    
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_INSERIR_GRUPO, token);
    
    return grupo;
  } 
  
  /**
   * Remove o grupo cujo identificador � passado como par�metro
   * @param idGrupo O identificador do grupo a ser removido
   * @param token Token de valida��o do usu�rio que est� executando a opera��o
   */
  public void removeGrupo(Long idGrupo, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);
    
    GrupoVO grpTemp = grupoDao.getGrupoPorID(idGrupo);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoGrupoService.removegrupo.mensagemlog"), 
            grpTemp.nome, grpTemp.id);
    
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_REMOVER_GRUPO, token);
    grupoDao.removeGrupo(grpTemp);    
  }
  
  /**
   * Altera o grupo cujo identificador � passado como par�metro. Os dados 
   * para altera��o do grupo tamb�m s�o passados como par�metro: nome e descricao
   * @param idGrupo Identificador �nico do grupo a ser alterado 
   * @param nome Novo nome do grupo
   * @param descricao Nova descri��o do grupo
   * @param cartorioId O identificador do cart�rio que est� selecionado
   * @param token Token de valida��o do usu�rio e sua sess�o
   */
  public void alteraGrupo(Long idGrupo, String nome, String descricao, Long cartorioId, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);
    
    GrupoVO grupo = new GrupoVO();
    grupo.id = idGrupo;
    grupo.nome = nome;
    grupo.descricao = descricao;
    grupo.cartorioId = cartorioId;
    
    grupoDao.alteraGrupo(grupo);    
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoGrupoService.alteragrupo.mensagemlog"), 
            idGrupo, nome, descricao);    
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_ALTERAR_GRUPO, token);
  }
  
  /**
   * Recupera as permiss�es relacionadas ao grupo desejado
   * @param idGrupo Identificador �nico do grupo para o qual se deseja
   * recuperar as permiss�es
   * @param token O token para validar o usu�rio e sua sess�o
   * @return Listagem com as permiss�es do grupo
   */
  public List<Permissao> permissoesPorGrupo(Long idGrupo, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);

    GrupoVO grupo = new GrupoVO();
    grupo.id = idGrupo;
    return grupoDao.permissoesPorGrupo(grupo);
  }
  
  /**
   * Recupera as permiss�es n�o atribu�das ao grupo.
   * @param idGrupo O identificador �nico do grupo para o qual se deseja
   * recuperar as permiss�es n�o atribu�das
   * @param token Token de valida��o do usu�rio
   * @return Listagem com as permiss�es n�o atribu�das ao grupo
   */
  public List<Permissao> permissoesNaoAtribuidasAoGrupo(Long idGrupo, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);

    GrupoVO grupo = new GrupoVO();
    grupo.id = idGrupo;
    return grupoDao.permissoesNaoAtribuidasAoGrupo(grupo);
  }
  
  /**
   * Recupera os usu�rios que est�o alocados no grupo
   * @param idGrupo Identificador �nico do grupo
   * @param token O token para validar o usu�rio e sua sess�o
   * @return  Listagem com os usu�rios alocados ao grupo
   */
  public List<UsuarioVO> usuariosPorGrupo(Long idGrupo, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);

    GrupoVO grupo = new GrupoVO();
    grupo.id = idGrupo; 
    return grupoDao.usuariosPorGrupo(grupo);
  }
  
  /**
   * Incluir uma nova permiss�o ao grupo. Caso a permiss�o j� exista para o grupo,
   * uma excess�o ser� lan�ada
   * @param idGrupo Identificador �nico do grupo ao qual ser� inserida a nova 
   * permiss�o
   * @param idPermissao Identificador da permiss�o a ser inserida no grupo
   * @param token Token de valida��o do usu�rio
   */
  public void incluiPermissaoNoGrupo(Long idGrupo, Long idPermissao, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);

    GrupoVO grupo = new GrupoVO();
    grupo.id = idGrupo; 

    Permissao permissao = new Permissao();
    permissao.setId(idPermissao);
    
    grupoDao.incluiPermissaoNoGrupo(grupo, permissao);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoGrupoService.novapermissaonogrupo.mensagemlog"), 
            idGrupo, idPermissao);    
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_NOVAPERMISSAO_GRUPO, token);    
  }
  
  /**
   * Inclui um novo usu�rio ao grupo. Caso o usu�rio j� esteja cadastrad no grupo, uma 
   * mensagem de erro ser� enviada
   * @param idGrupo Identificador do grupo para o qual ser� inserido o usu�rio
   * @param lgnUsuario Login do usu�rio que ser� inserido no grupo
   * @param token Token de valida��o do usu�rio
   */
  public void incluiUsuarioNoGrupo(Long idGrupo, String lgnUsuario, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);
    
    UsuarioVO usr = usuarioDao.recuperarUsuarioPorLogin(lgnUsuario);
    if(usr == null) {//usu�rio ainda n�o est� no banco de dados
      //Recupera o usu�rio do AD e o insere na tabela de usu�rio
      usr = activeDirectoryDao.getDadosUsuario(lgnUsuario, "");
      if(usr == null) {// caso o usu�rio n�o exista no AD
        throw new ParametroException("Usu�rio inexistente", ICodigosErros.ERRO_GRUPO_INSUSUARIOGRUPO);
      }
      usr = usuarioDao.inserirUsuario(usr);
    }
    GrupoVO grp = new GrupoVO();
    grp.id = idGrupo;
    
    grupoDao.incluiUsuarioNoGrupo(grp, usr);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoGrupoService.novousuarionogrupo.mensagemlog"), 
            idGrupo, usr.id);    
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_NOVOUSUARIO_GRUPO, token);    
  }
  
  /**
   * Recupera os usu�rios de uma p�gina. A p�gina conter� a quantidade
   * indicada em limite.
   * @param pagina A p�gina de usu�rios a qual se deseja recuperar
   * @param limite O limite de usu�rios por p�gina
   * @param filtro O filtro dos usu�rios pelos seus nomes
   * @param token O teken de valida��o do usu�rio
   * @return Listagem com os usu�rios recuperados para a p�gina
   */
  public List<UsuarioVO> usuariosPaginados(int pagina, int limite, String filtro, String token) {
    authenticationService.validaToken(token);
    return usuarioDao.usuariosPaginados(pagina, limite, filtro);
  }
  
  /**
   * Recupera o total de usu�rios cadastrados no banco de dados
   * @param Campo para filtragem de usu�rio
   * @return Total de usu�rios no banco de dados
   */
  public int totalUsuarios(String filtro) {
    return usuarioDao.totalUsuarios(filtro);
  }
  
  /**
   * Remove uma permiss�o do grupo. 
   * @param idGrupo Identificador �nico do grupo do qual a permiss�o ser� removida 
   * permiss�o
   * @param idPermissao Identificador da permiss�o que ser� removida do grupo
   * @param token Token de valida��o do usu�rio
   */
  public void removePermissaoDoGrupo(Long idGrupo, Long idPermissao, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);

    GrupoVO grupo = new GrupoVO();
    grupo.id = idGrupo; 

    Permissao permissao = new Permissao();
    permissao.setId(idPermissao);
    
    grupoDao.removePermissaoDoGrupo(grupo, permissao);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoGrupoService.removepermissaodogrupo.mensagemlog"), 
            idGrupo, idPermissao);    
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_REMOVEPERMISSAO_GRUPO, token);    
  }  
  
  /**
   * Remove um usu�rio do grupo. 
   * @param idGrupo Identificador �nico do grupo do qual a permiss�o ser� removida 
   * permiss�o
   * @param idUsuario Identificador do usu�rio que ser� removido do grupo
   * @param token Token de valida��o do usu�rio
   */
  public void removeUsuarioDoGrupo(Long idGrupo, Long idUsuario, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);

    GrupoVO grupo = new GrupoVO();
    grupo.id = idGrupo; 

    UsuarioVO usuario = new UsuarioVO();
    usuario.id = idUsuario;
    
    grupoDao.removeUsuarioDoGrupo(grupo, usuario);
    
    String logMessage = 
        MessageFormat.format(
            bundle.getString("AdministracaoGrupoService.removeusuariodogrupo.mensagemlog"), 
            idGrupo, idUsuario);    
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_REMOVEUSUARIO_GRUPO, token);    
  }  
  
  
  /**
   * Retorna uma listagem com todos os usu�rios dispon�veis no active directory
   * @param token Token de valida��o do usu�rio
   * @return Listagem com todos os usu�rios presentes no Active Directory
   */
  public List<UsuarioVO> listagemUsuarios(String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);
    return activeDirectoryDao.getTodosUsuarios();
  }
  
  /**
   * Recupera uma listagem com todas as permiss�es cadastradas
   * @param token Token de valida��o do usu�rio e de sua se��o
   * @return Listagem com todas as permiss�es cadastradas
   */
  public List<Permissao> listagemPermissoes(String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);
    return permissaoDao.getPermissoes();
  }
}
