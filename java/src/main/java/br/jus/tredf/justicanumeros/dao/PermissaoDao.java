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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("PermissaoDao")
public class PermissaoDao implements Serializable {
  private static final long serialVersionUID = 1355993118936709887L;

  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  /**
   * 
   * @param permissao
   * @return
   */
  public Long inserirPermissao(Permissao permissao) {
    Long permissaoId = null;
    if(permissao == null || StringUtils.isEmpty(permissao.getDescricao())) {
      throw new ParametroException(bundle.getString(
          "PermissaoDao.inserirPermissao.erro.permissao_invalida"), 
          ICodigosErros.ERRO_PERMISSAO_INSERIRPERMISSAO);
    }
    
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmtQPerm = con.prepareStatement("SELECT * FROM JN_PERMISSAO WHERE DESCRICAO = UPPER(?)");
      pstmtQPerm.setString(1, permissao.getDescricao().toUpperCase().trim());
      ResultSet rs1 = pstmtQPerm.executeQuery();
      if(rs1.next()) {
        pstmtQPerm.close();
        rs1.close();
        throw new ParametroException(
            MessageFormat.format(bundle.getString("PermissaoDao.inserirPermissao.erro.generico"), 
                "permissão existente"), 
            ICodigosErros.ERRO_PERMISSAO_INSERIRPERMISSAO);    
      }
      pstmtQPerm.close();
      rs1.close();
      String generatedColumns[] = { "ID" };
      PreparedStatement pstmtIPerm = con.prepareStatement("INSERT INTO JN_PERMISSAO (DESCRICAO) VALUES (?)", 
          generatedColumns);
      pstmtIPerm.setString(1, permissao.getDescricao());
      pstmtIPerm.executeUpdate();
      ResultSet rs = pstmtIPerm.getGeneratedKeys();
      if(rs.next()) {
        permissaoId = rs.getLong(1);
      }
      pstmtIPerm.close();
      rs.close();
    } catch (SQLException e) {
      throw new ParametroException(
          MessageFormat.format(bundle.getString("PermissaoDao.inserirPermissao.erro.generico"), 
              e.getMessage()), 
          ICodigosErros.ERRO_PERMISSAO_INSERIRPERMISSAO);    
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return permissaoId;
  }
  
  /**
   * 
   * @param permissao
   */
  public void removerPermissao(Permissao permissao) {
    if (permissao == null || permissao.getId() == null || permissao.getId() == 0) {
      throw new ParametroException(bundle.getString("PermissaoDao.removerPermissao.parametroInvalido"), 
          ICodigosErros.ERRO_PERMISSAO_REMOVERPERMISSAO);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("DELETE FROM JN_PERMISSAO WHERE ID = ?");
      pstmt.setLong(1, permissao.getId());
      pstmt.executeUpdate();
      pstmt.close();
    } catch (SQLException e) {
      throw new ParametroException(
          MessageFormat.format(bundle.getString("PermissaoDao.removerPermissao.erro.generico"), 
              e.getMessage()), ICodigosErros.ERRO_PERMISSAO_REMOVERPERMISSAO);
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
   * Recupera todas as permissões constantes no banco de dados 
   * @return Listagem com todas as permissões constantes no banco de dadoss
   */
  public List<Permissao> getPermissoes() {
    List<Permissao> permissoes = new ArrayList<Permissao>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_PERMISSAO");
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
}
