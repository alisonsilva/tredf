package br.jus.tredf.justicanumeros.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.Cartorio;
import br.jus.tredf.justicanumeros.model.LogAcoesServentia;
import br.jus.tredf.justicanumeros.model.ProdutividadeMagistrado;
import br.jus.tredf.justicanumeros.model.ProdutividadeServentias;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("ProdutividadeMagistradoDao")
@SuppressWarnings("all")
public class ProdutividadeMagistradoDao implements Serializable {

  private static final long serialVersionUID = 927367593709664079L;
  
  @Autowired
  private ResourceBundle bundle;
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  public Long inserirProdutividade(ProdutividadeMagistrado prodMag) 
      throws ParametroException {
    if(prodMag == null || prodMag.getCartorio() == null || 
        prodMag.getCartorio().getId() == null || 
        prodMag.getCartorio().getId() == 0) {
      throw new ParametroException(bundle.getString("ProdutividadeMagistradoDao.inserirProdutividade.parametroInvalido"), 
          ICodigosErros.ERRO_SERVENTIAS_INCLUIR);
    }
    Long ret = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      String generatedColumns[] = { "ID" };
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO JN_PRODMAGISTRADO "
          + "(SENTCCMCRIM1, SENTCCMNCRIM1, SENTCSMCRIM1, SENTCSMNCRIM1, SENTH1, "
          + "SENTEXTFISC1, DT_PREENCHIMENTO, DT_COMPETENCIA, JN_CARTORIOID) "
          + "VALUES (?,?,?,?,?,?,?,?,?)", generatedColumns);
      
      int idx = 1;
      pstmt.setInt(idx++, prodMag.getSentCCMCrim1());
      pstmt.setInt(idx++, prodMag.getSentCCMNCrim1());
      pstmt.setInt(idx++, prodMag.getSentCSMCrim1());
      pstmt.setInt(idx++, prodMag.getSentCSMNCrim1());
      pstmt.setInt(idx++, prodMag.getSentH1());
      pstmt.setInt(idx++, prodMag.getSentExtFisc1());
      pstmt.setTimestamp(idx++, new Timestamp(System.currentTimeMillis()));
      pstmt.setDate(idx++, new java.sql.Date(prodMag.getDtCompetencia().getTime()));
      pstmt.executeUpdate();
      
      ResultSet rs = pstmt.getGeneratedKeys();
      ret = rs.getLong(1); 
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
    return ret;
  }  
  
  public ProdutividadeMagistrado getProducaoMagistradoPorCompetenciaCartorio(
      Date dtCompetencia, Long idCartorio) {
    if(dtCompetencia == null || idCartorio == null) {
      throw new ParametroException(
          bundle.getString("ProdutividadeMagistradoDao.getProducaoMagistradoPorCompetenciaCartorio.parametroInvalido"), 
          ICodigosErros.ERRO_SERVENTIAS_RECUPERAR);
    }
    ProdutividadeMagistrado prodMag = new ProdutividadeMagistrado();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(
          "SELECT * from JN_PRODMAGISTRADO WHERE DT_COMPETENCIA = ? AND JN_CARTORIOID = ?");
      pstmt.setDate(1, new java.sql.Date(dtCompetencia.getTime()));
      pstmt.setLong(2, idCartorio);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        int idx = 1;
        prodMag.setId(rs.getLong("ID"));
        prodMag.setSentCCMCrim1(rs.getInt("SENTCCMCRIM1"));
        prodMag.setSentCCMNCrim1(rs.getInt("SENTCCMNCRIM1"));
        prodMag.setSentCSMCrim1(rs.getInt("SENTCSMCRIM1"));
        prodMag.setSentCSMNCrim1(rs.getInt("SENTCSMNCRIM1"));
        prodMag.setSentH1(rs.getInt("SENTH1"));
        prodMag.setSentExtFisc1(rs.getInt("SENTEXTFISC1"));        
        prodMag.setDtPreenchimento(rs.getTimestamp("DT_PREENCHIMENTO"));
        prodMag.setDtCompetencia(rs.getDate("DT_COMPETENCIA"));
        
        Cartorio cartorio = new Cartorio();
        cartorio.setId(idCartorio);
        prodMag.setCartorio(cartorio);        
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
    return prodMag;
  }  
  
  public Long inserirLogProdutMag(LogAcoesServentia log) {
    if(log == null || StringUtils.isEmpty(log.getDescricao()) 
        || log.getUsuario() == null || log.getUsuario().id == 0) {
      throw new ParametroException(bundle.getString("ProdutividadeMagistradoDao.inserirLogProdutMag.parametroInvalido"), 
          ICodigosErros.ERRO_SERVENTIAS_INCLUIRLOG);
    }
    Long idLog = null;
    Connection con = null;
    try {
      String generatedColumns[] = { "ID" };
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO JN_LOG_ACOES_SERVENT "
          + "(DESCRICAO, DT_ACAO, JN_USUARIOID, JN_PRODSERVNTIASID, JN_PRODMAGISTRADOID) "
          + " VALUES (?,?,?,?,?)", generatedColumns);
      pstmt.setString(1, log.getDescricao());
      pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
      pstmt.setLong(3, log.getUsuario().id);
      if (log.getIdServentia() != null) {
        pstmt.setLong(4, log.getIdServentia());
      } else {
        pstmt.setNull(4, Types.INTEGER);
      }
      if(log.getIdProdMagistrado() != null) {
        pstmt.setLong(5, log.getIdProdMagistrado());
      } else {
        pstmt.setNull(5, Types.INTEGER);
      }
      pstmt.executeUpdate();
      ResultSet rs = pstmt.getGeneratedKeys();
      idLog = rs.getLong(1);
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
    
    return idLog;
  }  

}
