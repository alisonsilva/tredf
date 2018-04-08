package br.jus.tredf.justicanumeros.dao;

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

import br.jus.tredf.justicanumeros.model.Campo;
import br.jus.tredf.justicanumeros.model.ProtocoloProcesso;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository(value="ProtocoloProcessoDao")
public class ProtocoloProcessoDao {


  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  /**
   * Recupera os números de protocolo dos processos referentes ao campo de preenchimento
   * de estatística cujo id é passado como parâmetro
   * @param idCampo Id do campo para o qual se está inserindo os protocolos
   * @return Listagem com os protocolos recuperados, caso existam. Caso não existam, a listagem
   * volta vazia
   */
  public List<ProtocoloProcesso> getProtocolosPorCampo(Long idCampo) {
    if(idCampo == null || idCampo <= 0) {
      throw new ParametroException(
          bundle.getString("ProtocoloProcessoDao.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    List<ProtocoloProcesso> protocolos = new ArrayList<ProtocoloProcesso>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(
          "SELECT * FROM JN_PROTOCOLO_PROC WHERE JN_CAMPOID = ?");
      pstmt.setLong(1, idCampo);
      ResultSet rs = pstmt.executeQuery();
      Campo campo = new Campo();
      campo.setId(idCampo);
      while(rs.next()) {
        ProtocoloProcesso proto = new ProtocoloProcesso();
        proto.setId(rs.getLong("ID"));
        proto.setProtocolo(rs.getString("PROTOCOLO"));
        proto.setCampo(campo);
        protocolos.add(proto);
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
    return protocolos;
  }
  
  
  /**
   * Recupera um protocolo de processo pelo seu identificador único.
   * @param idProtocolo Identificador do protocolo a ser recuperado.
   * @return ProtocoloProcesso Objeto com informações a respeito
   * do protocolo a ser recuperado
   */
  public ProtocoloProcesso getProtocoloById(Long idProtocolo) {
    if(idProtocolo == null || idProtocolo <= 0) {
      throw new ParametroException(
          bundle.getString("ProtocoloProcessoDao.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    ProtocoloProcesso protocolo = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(
          "SELECT * FROM JN_PROTOCOLO_PROC WHERE ID = ?");
      pstmt.setLong(1, idProtocolo);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        protocolo = new ProtocoloProcesso();
        protocolo.setId(rs.getLong("ID"));
        protocolo.setProtocolo(rs.getString("PROTOCOLO"));
        
        Campo campo = new Campo();
        campo.setId(rs.getLong("JN_CAMPOID"));
        protocolo.setCampo(campo);
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
    return protocolo;
  }
  
  /**
   * Remove o protocolo do processo cujo id é passado como parâmetro
   * @param idProtocolo Identificador do protocolo a ser removido
   */
  public void removerProtocolo(Long idProtocolo) {
    if(idProtocolo == null || idProtocolo <= 0) {
      throw new ParametroException(
          bundle.getString("ProtocoloProcessoDao.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(
          "DELETE FROM JN_PROTOCOLO_PROC WHERE ID = ?");
      pstmt.setLong(1, idProtocolo);
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
   * Insere um novo protocolo para um campo de uma determinada estatística 
   * @param protocolo Os dados do protocolo a serem inseridos
   * @return O novo protocolo inserido com seu campo identificador único 
   * preenchido
   * @throws ParametroException Quando o protocolo já está cadastrado
   * para o campo em questão, será lançada uma exceção
   */
  public ProtocoloProcesso inserirProtocolo(ProtocoloProcesso protocolo) {
    if(protocolo == null || StringUtils.isEmpty(protocolo.getProtocolo()) 
        || protocolo.getCampo() == null || protocolo.getCampo().getId() == null ) {
      throw new ParametroException(
          bundle.getString("ProtocoloProcessoDao.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_PROTOCOLO_PARAMETROSINVALIDOS);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = 
          con.prepareStatement("INSERT INTO JN_PROTOCOLO_PROC "
              + "(PROTOCOLO, JN_CAMPOID) VALUES (?, ?)", new String[]{"ID"});
      PreparedStatement pstmtS = con.prepareStatement("SELECT * FROM JN_PROTOCOLO_PROC WHERE JN_CAMPOID = ? AND PROTOCOLO = ?");
      pstmtS.setLong(1, protocolo.getCampo().getId());
      pstmtS.setString(2, protocolo.getProtocolo());
      ResultSet rsS = pstmtS.executeQuery();
      if(rsS.next()) {
        rsS.close();
        throw new ParametroException(
            bundle.getString("ProtocoloProcessoDao.inserirProtocolo.erro.protocoloDuplicado"), 
            ICodigosErros.ERRO_PROTOCOLO_PROTOCOLODUPLICADO);
      }      
      pstmt.setString(1, protocolo.getProtocolo());
      pstmt.setLong(2, protocolo.getCampo().getId());
      pstmt.executeUpdate();
      ResultSet rs = pstmt.getGeneratedKeys();
      if(rs.next()) {
        protocolo.setId(rs.getLong(1));
      }
      rs.close();
      pstmt.close();
      rsS.close();
      pstmtS.close();
    } catch (SQLException e) {
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }    
    return protocolo;
  }
  
  
  /**
   * Altera o valor do protocolo para o protocolo passado
   * @param protocolo O protocolo que terá seu valor alterado
   */
  public void alterarProtocolo(ProtocoloProcesso protocolo) {
    if(protocolo == null || protocolo.getId() == null 
        || StringUtils.isEmpty(protocolo.getProtocolo())) {
      throw new ParametroException(
          bundle.getString("ProtocoloProcessoDao.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_PROTOCOLO_PARAMETROSINVALIDOS);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(
          "UPDATE JN_PROTOCOLO_PROC SET PROTOCOLO = ? WHERE ID = ?");
      pstmt.setString(1, protocolo.getProtocolo());
      pstmt.setLong(2, protocolo.getId());
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
