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
   * Recupera todos os cartórios disponíveis
   * @return Listagem com os cartórios disponíveis
   */
  public List<Cartorio> getCartorios() {
    return cartorioDao.getCartorios();
  }
  
  /**
   * Cria um novo grupo. O usuário deverá ter as permissões para isso. 
   * @param nome Nome do grupo a ser criado
   * @param descricao Breve descrição do grupo a ser criado
   * @param token Token de validação do usuário e da sessão
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
   * Remove o grupo cujo identificador é passado como parâmetro
   * @param idGrupo O identificador do grupo a ser removido
   * @param token Token de validação do usuário que está executando a operação
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
   * Altera o grupo cujo identificador é passado como parâmetro. Os dados 
   * para alteração do grupo também são passados como parâmetro: nome e descricao
   * @param idGrupo Identificador único do grupo a ser alterado 
   * @param nome Novo nome do grupo
   * @param descricao Nova descrição do grupo
   * @param cartorioId O identificador do cartório que está selecionado
   * @param token Token de validação do usuário e sua sessão
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
   * Recupera as permissões relacionadas ao grupo desejado
   * @param idGrupo Identificador único do grupo para o qual se deseja
   * recuperar as permissões
   * @param token O token para validar o usuário e sua sessão
   * @return Listagem com as permissões do grupo
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
   * Recupera as permissões não atribuídas ao grupo.
   * @param idGrupo O identificador único do grupo para o qual se deseja
   * recuperar as permissões não atribuídas
   * @param token Token de validação do usuário
   * @return Listagem com as permissões não atribuídas ao grupo
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
   * Recupera os usuários que estão alocados no grupo
   * @param idGrupo Identificador único do grupo
   * @param token O token para validar o usuário e sua sessão
   * @return  Listagem com os usuários alocados ao grupo
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
   * Incluir uma nova permissão ao grupo. Caso a permissão já exista para o grupo,
   * uma excessão será lançada
   * @param idGrupo Identificador único do grupo ao qual será inserida a nova 
   * permissão
   * @param idPermissao Identificador da permissão a ser inserida no grupo
   * @param token Token de validação do usuário
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
   * Inclui um novo usuário ao grupo. Caso o usuário já esteja cadastrad no grupo, uma 
   * mensagem de erro será enviada
   * @param idGrupo Identificador do grupo para o qual será inserido o usuário
   * @param lgnUsuario Login do usuário que será inserido no grupo
   * @param token Token de validação do usuário
   */
  public void incluiUsuarioNoGrupo(Long idGrupo, String lgnUsuario, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);
    
    UsuarioVO usr = usuarioDao.recuperarUsuarioPorLogin(lgnUsuario);
    if(usr == null) {//usuário ainda não está no banco de dados
      //Recupera o usuário do AD e o insere na tabela de usuário
      usr = activeDirectoryDao.getDadosUsuario(lgnUsuario, "");
      if(usr == null) {// caso o usuário não exista no AD
        throw new ParametroException("Usuário inexistente", ICodigosErros.ERRO_GRUPO_INSUSUARIOGRUPO);
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
   * Recupera os usuários de uma página. A página conterá a quantidade
   * indicada em limite.
   * @param pagina A página de usuários a qual se deseja recuperar
   * @param limite O limite de usuários por página
   * @param filtro O filtro dos usuários pelos seus nomes
   * @param token O teken de validação do usuário
   * @return Listagem com os usuários recuperados para a página
   */
  public List<UsuarioVO> usuariosPaginados(int pagina, int limite, String filtro, String token) {
    authenticationService.validaToken(token);
    return usuarioDao.usuariosPaginados(pagina, limite, filtro);
  }
  
  /**
   * Recupera o total de usuários cadastrados no banco de dados
   * @param Campo para filtragem de usuário
   * @return Total de usuários no banco de dados
   */
  public int totalUsuarios(String filtro) {
    return usuarioDao.totalUsuarios(filtro);
  }
  
  /**
   * Remove uma permissão do grupo. 
   * @param idGrupo Identificador único do grupo do qual a permissão será removida 
   * permissão
   * @param idPermissao Identificador da permissão que será removida do grupo
   * @param token Token de validação do usuário
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
   * Remove um usuário do grupo. 
   * @param idGrupo Identificador único do grupo do qual a permissão será removida 
   * permissão
   * @param idUsuario Identificador do usuário que será removido do grupo
   * @param token Token de validação do usuário
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
   * Retorna uma listagem com todos os usuários disponíveis no active directory
   * @param token Token de validação do usuário
   * @return Listagem com todos os usuários presentes no Active Directory
   */
  public List<UsuarioVO> listagemUsuarios(String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);
    return activeDirectoryDao.getTodosUsuarios();
  }
  
  /**
   * Recupera uma listagem com todas as permissões cadastradas
   * @param token Token de validação do usuário e de sua seção
   * @return Listagem com todas as permissões cadastradas
   */
  public List<Permissao> listagemPermissoes(String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.grupo.ins.grupo"));
    addNovaPermissaoList(permissoes, properties.getProperty("perm.grupo.rem.grupo"));
    validaTokenUsuario(token, permissoes);
    return permissaoDao.getPermissoes();
  }
}
