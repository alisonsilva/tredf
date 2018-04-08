package br.jus.tredf.justicanumeros.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.Formulario;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("FormularioDao")
@SuppressWarnings("all")
public class FormularioDao implements Serializable {

  private static final long serialVersionUID = 3992196338517620150L;

  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;  

  public List<Formulario> getTodosFormularios() {
    List<Formulario> ret = new ArrayList<Formulario>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_RELATORIO");
      PreparedStatement pstmtPerm = con.prepareStatement("SELECT PERM.* FROM JN_PERMISSAO PERM "
          + "INNER JOIN RL_RELATORIO_PERM RLPERM ON RLPERM.JN_PERMISSAOID = PERM.ID "
          + "WHERE RLPERM.JN_RELATORIOID  = ? ");
      ResultSet rs = pstmt.executeQuery();      
      while(rs.next()) {
        Formulario form = new Formulario();
        form.setDescricao(rs.getString("DESCRICAO"));
        form.setNomeRelatorio(rs.getString("NOME_RELATORIO"));
        form.setNome(rs.getString("NOME"));
        form.setId(rs.getLong("ID"));
        form.setDtInsercao(rs.getTimestamp("DT_INSERCAO"));
        
        pstmtPerm.clearParameters();
        pstmtPerm.setLong(1, form.getId());
        ResultSet rsPerm = pstmtPerm.executeQuery();
        while(rsPerm.next()) {
          Permissao perm = new Permissao();
          perm.setDescricao(rsPerm.getString("DESCRICAO"));
          perm.setId(rsPerm.getLong("ID"));
          form.getPermissoes().add(perm);
        }
        rsPerm.close();
        ret.add(form);
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e1) {
        }
      }
    }
    return ret;
  }
  
  public Formulario getFormularioById(Long formularioId) {
    Formulario formulario = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_RELATORIO rel where rel.id = ?");
      PreparedStatement pstmtPerm = con.prepareStatement("SELECT PERM.* FROM JN_PERMISSAO PERM "
          + "INNER JOIN RL_RELATORIO_PERM RLPERM ON RLPERM.JN_PERMISSAOID = PERM.ID "
          + "WHERE RLPERM.JN_RELATORIOID  = ? ");
      pstmt.setLong(1, formularioId);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        formulario = new Formulario();
        formulario.setId(rs.getLong("ID"));
        formulario.setDescricao(rs.getString("DESCRICAO"));
        formulario.setNome(rs.getString("NOME"));
        formulario.setNomeRelatorio(rs.getString("NOME_RELATORIO"));
        formulario.setDtInsercao(rs.getTimestamp("DT_INSERCAO"));
                
        pstmtPerm.clearParameters();
        pstmtPerm.setLong(1, formulario.getId());
        ResultSet rsPerm = pstmtPerm.executeQuery();
        while(rsPerm.next()) {
          Permissao perm = new Permissao();
          perm.setDescricao(rsPerm.getString("DESCRICAO"));
          perm.setId(rsPerm.getLong("ID"));
          formulario.getPermissoes().add(perm);
        }
        rsPerm.close();
      }
      rs.close();
      pstmt.close();
      pstmtPerm.close();
    } catch (SQLException e) {      
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return formulario;
  }
  
  public void alteraFormulario(Formulario formulario) {
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(
          "UPDATE JN_RELATORIO SET NOME = ?, DESCRICAO = ? WHERE ID = ?");
      pstmt.setString(1, formulario.getNome());
      pstmt.setString(2, formulario.getDescricao());
      pstmt.setLong(3, formulario.getId());
      pstmt.executeUpdate();
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
  
  
}
