package br.jus.tredf.justicanumeros.dao.terceirizado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.terceirizado.LogAcoesTerceirizado;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("LogAcoesTerceirizadoDao")
public class LogAcoesTerceirizadoDao {
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;

  /**
   * Insere mensagem de log para ações de administração de terceirizados
   * @param log Informações a serem gravadas para registro de ação
   */
  public void insereLog(LogAcoesTerceirizado log) {
    if(log == null || StringUtils.isEmpty(log.getDescricao()) || 
        log.getAcao() == 0 || log.getDtAcao() == null || 
        log.getUsuario() == null || log.getUsuario().id == 0) {
      throw new ParametroException(
          bundle.getString("LogAcoesTerceirizadosDao.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_LOGACOES_TERCEIRIZADO);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO JN_LOG_ACOES_TERC "
          + "(CD_ACAO, DESCRICAO, DT_ACAO, JN_TERCEIRIZADOID, JN_USUARIOID) VALUES (?,?,?,?,?)");
      pstmt.setInt(1, log.getAcao());
      pstmt.setString(2, log.getDescricao());
      pstmt.setTimestamp(3, new Timestamp(log.getDtAcao().getTime()));
      if(log.getTerceirizado() != null) {
        pstmt.setLong(4, log.getTerceirizado().getId());
      } else {
        pstmt.setNull(4, Types.INTEGER);
      }
      pstmt.setLong(5, log.getUsuario().id);
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
}
