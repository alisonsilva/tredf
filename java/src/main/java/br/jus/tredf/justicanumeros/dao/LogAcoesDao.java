package br.jus.tredf.justicanumeros.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.LogAcoes;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("LogAcoesDao")
public class LogAcoesDao implements Serializable {
  private static final long serialVersionUID = 4253552806549547136L;

  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  /**
   * Insere novo log de ações
   * @param log O log de ações a ser inserido
   * @return O objeto log com o seu id
   */
  public Long inserirLogAcoes(LogAcoes log) {
    if(log == null || StringUtils.isEmpty(log.getDescricao()) || log.getDtAtualizacao() == null) {
      throw new ParametroException(
          MessageFormat.format(
              bundle.getString("LogAcoesDao.inserirLogAcoes.parametroInvalido"), "Informações de log ausentes"), 
          ICodigosErros.ERRO_LOGACOES_INSERIRLOG);
    }
    if(log.getUsuario() == null || StringUtils.isEmpty(log.getDescricao()) || log.getDtAtualizacao() == null) {
      throw new ParametroException(
          MessageFormat.format(
              bundle.getString("LogAcoesDao.inserirLogAcoes.parametroInvalido"), "Informações de usuário ausentes"), 
          ICodigosErros.ERRO_LOGACOES_INSERIRLOG);
    }
    Long idLog = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      String generatedColumns[] = { "ID" };
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO JN_LOG_ACOES "
          + "(DESCRICAO, DT_ATUALIZACAO, JN_USUARIOID, CD_ACAO) VALUES (?,?,?,?)", generatedColumns);
      pstmt.setString(1, log.getDescricao());
      pstmt.setTimestamp(2, new Timestamp(log.getDtAtualizacao().getTime()));
      pstmt.setLong(3, log.getUsuario().id);
      pstmt.setInt(4, log.getCodAcao());
      pstmt.executeUpdate();
      
      ResultSet rs = pstmt.getGeneratedKeys();
      if(rs.next()) {
        idLog = rs.getLong(1);
        log.setId(idLog);
      }    
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
      throw new ParametroException(
          MessageFormat.format(
              bundle.getString("LogAcoesDao.inserirLogAcoes.erro.generico"), e.getMessage()), 
          ICodigosErros.ERRO_LOGACOES_INSERIRLOG);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    
    return idLog;
  }
}
