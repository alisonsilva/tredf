package br.jus.tredf.justicanumeros.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.Cartorio;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("UsuarioDao")
@SuppressWarnings("all")
public class UsuarioDao implements Serializable {

  private static final long serialVersionUID = 6794289114286948231L;

  @Autowired
  private ComboPooledDataSource dataSource;
  
  /**
   * Recupera o usu�rio cujo login � passado como par�metro
   * @param login Login do usu�rio para o qual se deseja recuperar as informa��es
   * @return O usu�rio, caso exista no banco de dados; caso n�o exista, nulo
   */
  public UsuarioVO recuperarUsuarioPorLogin(String login) {
    if(StringUtils.isEmpty(login)) {
      throw new ParametroException("Login vazio", ICodigosErros.ERRO_USUARIO_RECUPERAR);
    }
    UsuarioVO ret = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_USUARIO WHERE LOWER(LOGIN) = ?");
      pstmt.setString(1, login.toLowerCase());
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        ret = new UsuarioVO();
        ret.id = rs.getLong("ID");
        ret.lgn = rs.getString("LOGIN");
        ret.email = ret.lgn + "@tre-df.jus.br";
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
      throw new ParametroException("Erro ao realizar operacao: " + e.getMessage(), 
          ICodigosErros.ERRO_USUARIO_RECUPERAR);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return ret;
  }
  
  /**
   * Insere o usu�rio passado no banco de dados local.
   * @param usuario O usu�rio a ser inserido no banco de dados local
   * @return O usu�rio inserido com seu novo identificador.
   */
  public UsuarioVO inserirUsuario(UsuarioVO usuario) {
    if(usuario == null || StringUtils.isEmpty(usuario.cn)) {
      throw new ParametroException("Usu�rio vazio", ICodigosErros.ERRO_USUARIO_INCLUIR);
    }
    Long id = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      String generatedColumns[] = { "ID" };
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO JN_USUARIO (LOGIN, DT_INSERCAO, NOME) VALUES (?, ?, ?)", 
          generatedColumns);
      pstmt.setString(1, usuario.cn.toLowerCase());
      pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
      pstmt.setString(3, (usuario.showName == null ? usuario.showName : usuario.showName.toUpperCase()));
      pstmt.executeUpdate();
      ResultSet rs = pstmt.getGeneratedKeys();
      if(rs.next()) {
        id = rs.getLong(1);
        usuario.id = id;
      }
    } catch (SQLException e) {
      throw new ParametroException("Erro ao realizar operacao: " + e.getMessage(), 
          ICodigosErros.ERRO_USUARIO_INCLUIR);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return usuario;
  }
  
  
  /**
   * Recupera os usu�rios de acordo com pagina��es, cada p�gina tendo um limite de
   * quantidade de usu�rios.
   * @param pagina P�gina � qual se deseja recuperar. Deve conter um valor positivo
   * maior que zero.
   * @param limite Limite de usu�rios por p�gina
   * @param filtro Par�metro de filtragem dos nomes dos usu�rios
   * @return Listagem com os usu�rios recuperados para a p�gina
   */
  public List<UsuarioVO> usuariosPaginados(int pagina, int limite, String filtro) {
    if(pagina <= 0) {
      throw new ParametroException(
          "A p�gina deve ter valor positivo maior que zero", 
          ICodigosErros.ERRO_USUARIO_RECUPERAR);
    }
    List<UsuarioVO> usuarios = new ArrayList<UsuarioVO>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      int pinicio = (pagina - 1) * limite + 1;
      int pfinal = pagina*limite;
      PreparedStatement pstmt = null;
      if(StringUtils.isNotEmpty(filtro)) {
        pstmt = con.prepareStatement("select * from "
            +"( select a.*, ROWNUM rnum from " 
            +"  ( select * from JN_USUARIO WHERE NOME LIKE ? order by nome ) a " 
            +"  where ROWNUM <= " + pfinal + " ) "
            +" where rnum  >= " + pinicio   );
        pstmt.setString(1, "%" + filtro.toUpperCase() + "%");
      } else {
        pstmt = con.prepareStatement("select * from "
            +"( select a.*, ROWNUM rnum from " 
            +"  ( select * from JN_USUARIO  order by nome ) a " 
            +"  where ROWNUM <= " + pfinal + " ) "
            +" where rnum  >= " + pinicio   );        
      }
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        UsuarioVO usuario = new UsuarioVO();
        usuario.lgn = rs.getString("LOGIN");
        usuario.id = rs.getLong("ID");
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
   * Retorna a quantidade de usu�rios cadastrados no banco de dados
   * @param Par�metro para filtragem dos usu�rios
   * @return Quantidade de usu�rios
   */
  public int totalUsuarios(String filtro) {
    int qtd = 0;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      if (StringUtils.isEmpty(filtro)) {
        ResultSet rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM JN_USUARIO");
        rs.next();
        qtd = rs.getInt(1);
        rs.close();
      } else {
        PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) FROM JN_USUARIO WHERE NOME LIKE ?");
        pstmt.setString(1, "%" + filtro + "%");
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        qtd = rs.getInt(1);
        rs.close();
        pstmt.close();
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
    return qtd;
  }
  
  /**
   * Recupera os cart�rios relacionados a grupos dos quais o usu�rio
   * em quest�o participa
   * @param login Login do usu�rio para se encontrar os cart�rios
   * dos grupos do qual esse usu�rio participa
   * @return Listagem com os cart�rios encontrados para o usu�rio
   */
  public List<Cartorio> cartoriosPorUsuario(String login) {
    if(StringUtils.isEmpty(login)) {
      throw new ParametroException("Par�metro inv�lido", 
          ICodigosErros.ERRO_USUARIO_PARAMETROINVALIDO);
    }
    List<Cartorio> cartorios = new ArrayList<Cartorio>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt1 = con.prepareStatement("SELECT GRUPO.* FROM JN_USUARIO USUARIO "
        +"INNER JOIN RL_USUARIO_GRUPO UG ON UG.JN_USUARIOID = USUARIO.ID "
        +"INNER JOIN JN_GRUPO GRUPO ON GRUPO.ID = UG.JN_GRUPOID "
        +"WHERE USUARIO.LOGIN = ? "
        +"AND GRUPO.JN_CARTORIOID IS NOT NULL");
      PreparedStatement pstmt2 = con.prepareStatement("SELECT * FROM JN_CARTORIO WHERE ID = ?");
      pstmt1.setString(1, login);
      ResultSet rs1 = pstmt1.executeQuery();
      while(rs1.next()) {
        Long cartorioId = rs1.getLong("JN_CARTORIOID");
        pstmt2.clearParameters();
        pstmt2.setLong(1, cartorioId);
        ResultSet rs2 = pstmt2.executeQuery();
        if(rs2.next()) {
          Cartorio c = new Cartorio();
          c.setId(cartorioId);
          c.setNome(rs2.getString("NOME"));
          c.setSigla(rs2.getString("SIGLA"));
          c.setGrauIndicador(rs2.getInt("GRAU_INDICADOR"));
          cartorios.add(c);
          rs2.close();
        }
      }
      rs1.close();
      pstmt1.close();
      pstmt2.close();
    } catch (SQLException e) {
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    
    return cartorios;
  }
  
  /**
   * Insere os usuarios passados na listagem que j� n�o estejam no banco de dados.
   * Os que j� est�o, ter�o seus nomes alterados.
   * @param usuarios Lista com os usu�rios a serem inseridos ou alterados
   */
  public void insereUsuariosNaoExistentes(List<UsuarioVO> usuarios) {
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmtC = con.prepareStatement("SELECT * FROM JN_USUARIO WHERE LOGIN = ?");
      PreparedStatement pstmtU = con.prepareStatement("UPDATE JN_USUARIO SET NOME = ? WHERE LOGIN = ?");
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO JN_USUARIO (LOGIN, NOME, DT_INSERCAO) VALUES (?,?,?)");
      for(UsuarioVO usuario : usuarios) {
        pstmtC.clearParameters();
        pstmtC.setString(1, usuario.cn.toLowerCase());
        ResultSet rs1 = pstmtC.executeQuery();
        if(!rs1.next()) {
          pstmt.clearParameters();
          pstmt.setString(1, usuario.cn.toLowerCase());
          if (StringUtils.isNotEmpty(usuario.showName)) {
            pstmt.setString(2, usuario.showName.toUpperCase());
          } else {
            pstmt.setString(2, usuario.showName);
          }
          pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
          pstmt.executeUpdate();
        } else {
          pstmtU.clearParameters();
          if (StringUtils.isNotEmpty(usuario.showName)) {
            pstmtU.setString(1, usuario.showName.toUpperCase());
          } else {
            pstmtU.setString(1, usuario.showName);
          }
          pstmtU.setString(2, usuario.cn.toLowerCase());
          pstmtU.executeUpdate();
        }
        rs1.close();
      }
      pstmtC.close();
      pstmt.close();
      pstmtU.close();
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
