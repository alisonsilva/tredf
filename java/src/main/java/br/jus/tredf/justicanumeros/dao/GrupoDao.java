package br.jus.tredf.justicanumeros.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.GrupoVO;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("GrupoDao")
@SuppressWarnings("all")
public class GrupoDao implements Serializable {

  private static final long serialVersionUID = -757285475780600677L;

  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  /**
   * Recupera todos os grupos constantes na tabela de grupos.
   * Os grupos n�o ser�o preenchidos com seus usu�rios cadastrados e nem 
   * com suas permiss�es.
   * @return Grupos constantes na tabela de grupos
   */
  public List<GrupoVO> getTodosGrupos() {
    List<GrupoVO> grupos = new ArrayList<GrupoVO>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM JN_GRUPO");
      while(rs.next()) {
        GrupoVO grupo = new GrupoVO();
        grupo.id = rs.getLong("ID");
        grupo.nome = rs.getString("NOME");
        grupo.descricao = rs.getString("DESCRICAO");
        grupo.cartorioId = rs.getLong("JN_CARTORIOID");
        grupos.add(grupo);
      }
    } catch (SQLException e) {
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return grupos;
  }
  
  /**
   * Recupera as informa��es referentes a um determinado grupo
   * a partir de seu identificador �nico
   * @param idGrupo Identificador �nico do grupo
   * @return O grupo recuperado
   */
  public GrupoVO getGrupoPorID(Long idGrupo) {
    GrupoVO grupo = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_GRUPO WHERE ID = ?");
      pstmt.setLong(1, idGrupo);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        grupo = new GrupoVO();
        grupo.id = rs.getLong("ID");
        grupo.nome = rs.getString("NOME");
        grupo.descricao = rs.getString("DESCRICAO");
      }
    } catch (SQLException e) {
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return grupo;
  }
  
  /**
   * Recupera todos os grupos releacionados a um login
   * @param login O login para o qual deseja-se recuperar os grupos
   * @return Uma listagem com os grupos recuperados para o login
   */
  public List<GrupoVO> getGruposPorLogin(String login) {
    if(StringUtils.isEmpty(login)) {
      throw new ParametroException(bundle.getString("GrupoDao.getGruposPorLogin.erro.login_invalido"), 
          ICodigosErros.ERRO_GRUPO_RECUPERARPORLOGIN);
    }
    List<GrupoVO> grupos = new ArrayList<GrupoVO>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT GRP.* "
          +"FROM JN_GRUPO GRP "
          +"INNER JOIN RL_USUARIO_GRUPO USRGRP ON USRGRP.JN_GRUPOID = GRP.ID "
          +"INNER JOIN JN_USUARIO USR ON USR.ID = USRGRP.JN_USUARIOID "
          +"WHERE USR.LOGIN = ?");
      PreparedStatement pstmtPerm = con.prepareStatement("SELECT PERM.* FROM JN_PERMISSAO PERM "
          + "INNER JOIN RL_GRUPO_PERMISSAO GP ON GP.JN_PERMISSAOID = PERM.ID "
          + "WHERE GP.JN_GRUPOID = ?");
      
      pstmt.setString(1, login.toLowerCase());
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        GrupoVO grp = new GrupoVO();
        grp.id = rs.getLong("ID");
        grp.nome = rs.getString("NOME");
        grp.descricao = rs.getString("DESCRICAO");
        grp.cartorioId = rs.getBigDecimal("JN_CARTORIOID") == null ? null : rs.getLong("JN_CARTORIOID");
        
        pstmtPerm.clearParameters();
        pstmtPerm.setLong(1, grp.id);
        ResultSet rsPerm = pstmtPerm.executeQuery();
        while(rsPerm.next()) {
          Permissao permissao = new Permissao();
          permissao.setId(rsPerm.getLong("ID"));
          permissao.setDescricao(rsPerm.getString("DESCRICAO"));
          grp.permissoes.add(permissao);          
        }
        rsPerm.close();        
        grupos.add(grp);
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
      throw new ParametroException("Erro ao recuperar grupos: " + e.getMessage(), 
          ICodigosErros.ERRO_GRUPO_RECUPERARPORLOGIN);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }    
    return grupos;
  }
  
  /**
   * Insere informa��es sobre um novo grupo
   * @param novoGrupo O grupo com suas informa��es a serem inseridas
   * @return O grupo criado com seu identificador �nico
   */
  public GrupoVO insereGrupo(GrupoVO novoGrupo) {    
    Long grupoId = null;
    if(novoGrupo == null || StringUtils.isEmpty(novoGrupo.nome) ) {
      throw new ParametroException(bundle.getString("GrupoDao.inserirGrupo.erro.grupo_invalido"),
          ICodigosErros.ERRO_GRUPO_INSERIRGRUPO);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmtQGrupo = con.prepareStatement("SELECT * FROM JN_GRUPO WHERE NOME = UPPER(?)");
      pstmtQGrupo.setString(1, novoGrupo.nome.trim().toUpperCase());
      ResultSet rs1 = pstmtQGrupo.executeQuery();
      if(rs1.next()) {
        pstmtQGrupo.close();
        rs1.close();
        throw new ParametroException(
            MessageFormat.format(bundle.getString("GrupoDao.inserirGrupo.erro.generico"), "Grupo j� existente"), 
            ICodigosErros.ERRO_GRUPO_INSERIRGRUPO);
      }
      
      pstmtQGrupo.close();
      rs1.close();
      String generatedColumns[] = { "ID" };

      PreparedStatement pstmtIGrupo = con.prepareStatement(
          "INSERT INTO JN_GRUPO (NOME, DESCRICAO) VALUES (?,?)", 
          generatedColumns);
      pstmtIGrupo.setString(1, novoGrupo.nome);
      if (StringUtils.isEmpty(novoGrupo.descricao)) {
        pstmtIGrupo.setNull(2, java.sql.Types.VARCHAR);
      } else {
        pstmtIGrupo.setString(2, novoGrupo.descricao);
      }
      pstmtIGrupo.executeUpdate();
      ResultSet rs = pstmtIGrupo.getGeneratedKeys();
      if(rs.next()) {
        grupoId = rs.getLong(1);
        novoGrupo.id = grupoId;
      }
      pstmtIGrupo.close();
      rs.close();
    } catch (SQLException e) {
      throw new ParametroException(
          MessageFormat.format(bundle.getString("GrupoDao.inserirGrupo.erro.generico"), e.getMessage()), 
          ICodigosErros.ERRO_GRUPO_INSERIRGRUPO);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return novoGrupo;
  }
  
  /**
   * Altera as informa��es b�sicas do grupo: nome e descricao
   * Os valores s�o passados no par�metro de entrade bem como
   * o idenficador �nico do grupo que ir� sofrer as altera��es
   * @param grupo Informa��es do grupo que ir� sofrer as altera��es
   * identificador �nico do grupo, novo nome, nova descri��o
   */
  public void alteraGrupo(GrupoVO grupo) {
    if(grupo == null || grupo.id <= 0 || StringUtils.isEmpty(grupo.nome)) {
      throw new ParametroException(bundle.getString("GrupoDao.removerGrupo.erro.grupo_invalido"), 
          ICodigosErros.ERRO_GRUPO_PARAMETROINVALIDO);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("UPDATE JN_GRUPO SET NOME = ?, DESCRICAO = ?, JN_CARTORIOID = ? WHERE ID = ?");
      pstmt.setString(1, grupo.nome);
      if(StringUtils.isEmpty(grupo.descricao)) {
        pstmt.setNull(2, java.sql.Types.VARCHAR);
      } else {
        pstmt.setString(2, grupo.descricao);
      }
      if(grupo.cartorioId == null || grupo.cartorioId <= 0) {
        pstmt.setNull(3, java.sql.Types.INTEGER);
      } else {
        pstmt.setLong(3, grupo.cartorioId);
      }
      pstmt.setLong(4, grupo.id);
      pstmt.executeUpdate();
      pstmt.close();
    } catch (SQLException e) {
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
  }
  
  /**
   * Remove o grupo em quest�o
   * @param grupo O grupo a ser removido
   */
  public void removeGrupo(GrupoVO grupo) {
    if(grupo == null || grupo.id == 0) {
      throw new ParametroException(bundle.getString("GrupoDao.removerGrupo.erro.grupo_invalido"), 
          ICodigosErros.ERRO_GRUPO_REMOVERGRUPO);      
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmtRL = con.prepareStatement("DELETE FROM RL_GRUPO_PERMISSAO WHERE JN_GRUPOID = ?");
      pstmtRL.setLong(1, grupo.id);
      pstmtRL.executeUpdate();
      pstmtRL.close();
      
      pstmtRL = con.prepareStatement("DELETE FROM RL_USUARIO_GRUPO WHERE JN_GRUPOID = ?");
      pstmtRL.setLong(1, grupo.id);
      pstmtRL.executeUpdate();
      pstmtRL.close();
      
      PreparedStatement pstmtD = con.prepareStatement("DELETE FROM JN_GRUPO WHERE ID = ?");
      pstmtD.setLong(1, grupo.id);
      pstmtD.executeUpdate();
      pstmtD.close();
    } catch (SQLException e) {
      throw new ParametroException(
          MessageFormat.format(
              bundle.getString("GrupoDao.removerGrupo.erro.generico"), 
              e.getMessage()), 
              ICodigosErros.ERRO_GRUPO_REMOVERGRUPO);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
  }
  
  /**
   * Recupera todoas as permiss�es para o grupo em quest�o
   * @param grupo O grupo para o qual se deseja recuperar as permiss�es
   * @return Lista com as permiss�es do grupo
   */
  public List<Permissao> permissoesPorGrupo(GrupoVO grupo) {
    if(grupo == null || grupo.id <= 0) {
      throw new ParametroException(bundle.getString("GrupoDao.removerGrupo.erro.grupo_invalido"), 
          ICodigosErros.ERRO_GRUPO_PARAMETROINVALIDO);
    }
    List<Permissao> permissoes = new ArrayList<Permissao>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT PERM.ID, PERM.DESCRICAO FROM JN_PERMISSAO PERM "
          + "INNER JOIN RL_GRUPO_PERMISSAO RLPERM ON RLPERM.JN_PERMISSAOID = PERM.ID "
          + "WHERE RLPERM.JN_GRUPOID = ?");
      pstmt.setLong(1, grupo.id);
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        Permissao perm = new Permissao();
        perm.setId(rs.getLong("ID"));
        perm.setDescricao(rs.getString("DESCRICAO"));
        permissoes.add(perm);
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return permissoes;
  }
  
  /**
   * Recupera as permiss�es que n�o foram atribu�das ao grupo.
   * @param grupo O grupo de refer�ncia para checar as permiss�es
   * que ele n�o tem
   * @return Listagem com as permiss�es n�o atribu�das ao grupo
   */
  public List<Permissao> permissoesNaoAtribuidasAoGrupo(GrupoVO grupo) {
    if(grupo == null || grupo.id <= 0) {
      throw new ParametroException(
          bundle.getString("GrupoDao.removerGrupo.erro.grupo_invalido"), 
          ICodigosErros.ERRO_GRUPO_PARAMETROINVALIDO);
    }
    List<Permissao> permissoes = new ArrayList<Permissao>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT P.ID, P.DESCRICAO " 
          +"FROM JN_PERMISSAO P "
          +"WHERE P.ID NOT IN ( "
          +" SELECT R.JN_PERMISSAOID " 
          +" FROM RL_GRUPO_PERMISSAO R "
          +" WHERE R.JN_GRUPOID = ? "
          +")");
      pstmt.setLong(1, grupo.id);
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        Permissao per = new Permissao();
        per.setId(rs.getLong("ID"));
        per.setDescricao(rs.getString("DESCRICAO"));
        permissoes.add(per);
      }
      rs.close();
      pstmt.close();
    } catch (Exception e) {      
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return permissoes;
  }
  
  /**
   * Recupera os usu�rios cadastrados no grupo.
   * @param grupo O grupo para o qual deseja-se recuperar seus usu�rios
   * @return Listagem com os usu�rios pertencentes ao grupo
   */
  public List<UsuarioVO> usuariosPorGrupo(GrupoVO grupo) {
    if(grupo == null || grupo.id <= 0) {
      throw new ParametroException(bundle.getString("GrupoDao.removerGrupo.erro.grupo_invalido"), 
          ICodigosErros.ERRO_GRUPO_PARAMETROINVALIDO);
    }
    List<UsuarioVO> usuarios = new ArrayList<UsuarioVO>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT U.ID, U.LOGIN, U.NOME "
          + "FROM JN_USUARIO U "
          + "INNER JOIN RL_USUARIO_GRUPO UG ON UG.JN_USUARIOID = U.ID "
          + "WHERE UG.JN_GRUPOID = ?");
      pstmt.setLong(1, grupo.id);
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        UsuarioVO usuario = new UsuarioVO();
        usuario.id = rs.getLong("ID");
        usuario.lgn = rs.getString("LOGIN");
        usuario.showName = rs.getString("NOME");
        usuarios.add(usuario);
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return usuarios;
  }
  
  /**
   * Insere uma permiss�o ao grupo
   * @param grupo Grupo para o qual ser� inserida a permiss�o
   * @param permissao A permiss�o a ser inserida ao grupo
   */
  public void incluiPermissaoNoGrupo(GrupoVO grupo, Permissao permissao) {
    if(grupo == null || grupo.id <= 0 ||
        permissao == null || permissao.getId() == null ) {
      throw new ParametroException(bundle.getString("GrupoDao.removerGrupo.erro.grupo_invalido"), 
          ICodigosErros.ERRO_GRUPO_PARAMETROINVALIDO);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmtS = con.prepareStatement("SELECT * FROM RL_GRUPO_PERMISSAO "
          + "WHERE JN_PERMISSAOID = ? AND JN_GRUPOID = ?");
      pstmtS.setLong(1, permissao.getId());
      pstmtS.setLong(2, grupo.id);
      ResultSet rs = pstmtS.executeQuery();
      if(rs.next()) {
        rs.close();
        pstmtS.close();
        throw new ParametroException(
            bundle.getString("GrupoDao.incluirPermissaoGrupo.erro.permissaojapertence"), 
            ICodigosErros.ERRO_GRUPO_INSPERMGRUPO);
      }
      
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO RL_GRUPO_PERMISSAO "
          + "(JN_PERMISSAOID, JN_GRUPOID) values (?,?)");
      pstmt.setLong(1, permissao.getId());
      pstmt.setLong(2, grupo.id);
      pstmt.executeUpdate();
      pstmt.close();
      pstmtS.close();
      rs.close();
    } catch (SQLException e) {
      throw new ParametroException("Erro ao incluir permiss�o", 
          ICodigosErros.ERRO_GRUPO_PERMISSAOGRUPO);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
  }
  
  /**
   * Remove uma permiss�o relacionada ao grupo
   * @param grupo O grupo do qual a permiss�o ser� removida 
   * @param permissao A permiss�o a ser removida do grupo
   */
  public void removePermissaoDoGrupo(GrupoVO grupo, Permissao permissao) {
    if(grupo == null || grupo.id <= 0 ||
        permissao == null || permissao.getId() == null ) {
      throw new ParametroException(bundle.getString("GrupoDao.removerGrupo.erro.grupo_invalido"), 
          ICodigosErros.ERRO_GRUPO_PARAMETROINVALIDO);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("DELETE FROM RL_GRUPO_PERMISSAO "
          + "WHERE JN_PERMISSAOID = ? AND JN_GRUPOID = ?");
      pstmt.setLong(1, permissao.getId());
      pstmt.setLong(2, grupo.id);
      pstmt.executeUpdate();
      pstmt.close();
    } catch (SQLException e) {
      throw new ParametroException("Erro ao remover permiss�o", 
          ICodigosErros.ERRO_GRUPO_PERMISSAOGRUPO);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
  }
  
  /**
   * Insere um usu�rio ao grupo
   * @param grupo O grupo para o qual ser� inserido o usu�rio
   * @param usuario O usu�rio a ser inserido no grupo
   */
  public void incluiUsuarioNoGrupo(GrupoVO grupo, UsuarioVO usuario) {
    if(grupo == null || grupo.id <= 0 ||
        usuario == null || usuario.id <= 0 ) {
      throw new ParametroException(bundle.getString("GrupoDao.removerGrupo.erro.grupo_invalido"), 
          ICodigosErros.ERRO_GRUPO_PARAMETROINVALIDO);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();      
      PreparedStatement pstmtS = con.prepareStatement("SELECT * FROM RL_USUARIO_GRUPO "
          + "WHERE JN_USUARIOID = ? AND JN_GRUPOID = ?");
      pstmtS.setLong(1, usuario.id);
      pstmtS.setLong(2, grupo.id);
      ResultSet rs = pstmtS.executeQuery();
      if(rs.next()) {
        pstmtS.close();
        rs.close();
        throw new ParametroException(
            bundle.getString("GrupoDao.incluirUsuarioGrupo.erro.usuariojapertence"), 
            ICodigosErros.ERRO_GRUPO_INSUSUARIOGRUPO);
      }
      
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO RL_USUARIO_GRUPO "
          + "(JN_USUARIOID, JN_GRUPOID) values (?,?)");
      pstmt.setLong(1, usuario.id);
      pstmt.setLong(2, grupo.id);
      pstmt.executeUpdate();
      pstmt.close();
      rs.close();
      pstmtS.close();
    } catch (SQLException e) {
      throw new ParametroException("Erro ao incluir usu�rio", 
          ICodigosErros.ERRO_GRUPO_PERMISSAOGRUPO);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }    
  }
  
  /**
   * Remove o usu�rio do grupo
   * @param grupo Grupo do qual o usu�rio ser� removido
   * @param usuario O usu�rio que ser� removido do grupo
   */
  public void removeUsuarioDoGrupo(GrupoVO grupo, UsuarioVO usuario) {
    if(grupo == null || grupo.id <= 0 ||
        usuario == null || usuario.id <= 0 ) {
      throw new ParametroException(bundle.getString("GrupoDao.removerGrupo.erro.grupo_invalido"), 
          ICodigosErros.ERRO_GRUPO_PARAMETROINVALIDO);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();      
      PreparedStatement pstmt = con.prepareStatement("DELETE FROM RL_USUARIO_GRUPO "
          + "WHERE JN_USUARIOID = ? AND JN_GRUPOID = ?");
      pstmt.setLong(1, usuario.id);
      pstmt.setLong(2, grupo.id);
      pstmt.executeUpdate();
      pstmt.close();
    } catch (SQLException e) {
      throw new ParametroException("Erro ao remover usu�rio do grupo", 
          ICodigosErros.ERRO_GRUPO_PERMISSAOGRUPO);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }        
  }
}
