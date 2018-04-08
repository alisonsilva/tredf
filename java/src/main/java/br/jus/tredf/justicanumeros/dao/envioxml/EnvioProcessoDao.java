package br.jus.tredf.justicanumeros.dao.envioxml;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.dao.terceirizado.TerceirizadoDao;
import br.jus.tredf.justicanumeros.model.envioxml.EnvioProcessoXML;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.dbms.OracleUtils;

@Repository("EnvioProcessoDao")
public class EnvioProcessoDao {
  private static final Logger logger = Logger.getLogger(TerceirizadoDao.class);
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;

  
  public EnvioProcessoXML getEnvioProcessoPorID(Long id) {
    EnvioProcessoXML envioProcesso = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = 
          con.prepareStatement("SELECT * FROM JN_ENVIO_PROCESSO EP WHERE EP.ID = ?");
      pstmt.setLong(1, id);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {        
        envioProcesso = montaEnvioProcesso(rs);        
      }
      rs.close();
      pstmt.close();
    } catch (Exception e) {
      logger.error("Erro ao executar comando de banco (EnvioProcessoDao)", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return envioProcesso;
  }
  
  public List<EnvioProcessoXML> getEnvioProcessoPorEnvio(Long envioId) {
    if(envioId == null || envioId < 0) {
      throw new ParametroException(
          bundle.getString("envioxml.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_ENVIOXML_PARAMETROINVALIDO);
    }
    List<EnvioProcessoXML> envios = new ArrayList<EnvioProcessoXML>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = 
          con.prepareStatement(
              "SELECT * FROM JN_ENVIO_PROCESSO EP WHERE EP.JN_ENVIO_XMLID = ?");
      pstmt.setLong(1, envioId);
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        envios.add(montaEnvioProcesso(rs));
      }
      rs.close();
      pstmt.close();
    } catch (Exception e) {
      logger.error("Erro ao executar comando de banco (EnvioProcessoDao)", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
     
    return envios;
  }

  public List<EnvioProcessoXML> getEnvioProcessoPorEnvioParseOk(Long envioId) {
    if(envioId == null || envioId < 0) {
      throw new ParametroException(
          bundle.getString("envioxml.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_ENVIOXML_PARAMETROINVALIDO);
    }
    List<EnvioProcessoXML> envios = new ArrayList<EnvioProcessoXML>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = 
          con.prepareStatement(
              "SELECT * FROM JN_ENVIO_PROCESSO EP WHERE EP.JN_ENVIO_XMLID = ? AND FL_PARSE_OK = 1 AND FL_ENVIADO = 0");
      pstmt.setLong(1, envioId);
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        envios.add(montaEnvioProcesso(rs));
      }
      rs.close();
      pstmt.close();
    } catch (Exception e) {
      logger.error("Erro ao executar comando de banco (EnvioProcessoDao)", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
     
    return envios;
  }  
  
  public EnvioProcessoXML inserirEnvioProcesso(EnvioProcessoXML envio) {
    if(envio == null ||
        StringUtils.isEmpty(envio.getNumero()) ||
        envio.getEnvioXml() == null || envio.getEnvioXml().getId() == null ) {
      throw new ParametroException(
          bundle.getString("envioxml.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_ENVIOXML_PARAMETROINVALIDO);
    }
    Connection con = null;
    
    try {
      int i = 1;
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO JN_ENVIO_PROCESSO "
          + "(JN_ENVIO_XMLID, NUMERO, COMPETENCIA, CLASSE_PROCESSUAL, "
          + "CD_LOCALIDADE, NIVEL_SIGILO, INTERVENCAO_MP, TAMANHO_PROCESSO, "
          + "DT_AJUIZAMENTO, FL_ENVIADO, INFO_ENVIO, DADO_ENVIO) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",          
          new String[]{"ID"});
      pstmt.setLong(i++, envio.getEnvioXml().getId());
      pstmt.setString(i++, envio.getNumero());
      if(envio.getCompetencia() == null) {
        pstmt.setNull(i++, Types.INTEGER);
      } else {
        pstmt.setInt(i++, envio.getCompetencia());
      }
      if(envio.getClasseProcessual() == null) {
        pstmt.setNull(i++, Types.VARCHAR);
      } else {
        pstmt.setString(i++, envio.getClasseProcessual());
      }
      if(envio.getCodLocalidade() == null) {
        pstmt.setNull(i++, Types.INTEGER);
      } else {
        pstmt.setInt(i++, envio.getCodLocalidade());
      }
      if(envio.getNivelSigilo() == null) {
        pstmt.setNull(i++, Types.INTEGER);
      } else {
        pstmt.setInt(i++, envio.getNivelSigilo());
      }
      if(envio.getIntervencaoMp() == null) {
        pstmt.setNull(i++, Types.INTEGER);
      } else {
        pstmt.setInt(i++, envio.getIntervencaoMp());
      }
      if(envio.getTamanhoProcesso() == null) {
        pstmt.setNull(i++, Types.INTEGER);
      } else {
        pstmt.setInt(i++, envio.getTamanhoProcesso());
      }
      if(envio.getDtAjuizamento() == null) {
        pstmt.setNull(i++, Types.TIMESTAMP);
      } else {
        pstmt.setTimestamp(i++, new Timestamp(envio.getDtAjuizamento().getTime()));
      }
      pstmt.setInt(i++, envio.isFlEnviado() ? 1 : 0);
      pstmt.setString(i++, envio.getInfoEnvio());
      
      Clob clb = OracleUtils.createTemporaryCLOB(con, true, 10);
      String stUtf = new String(envio.getElementoXml().getBytes(Charset.forName("ISO-8859-1")));
      clb.setString(1, stUtf);      
      pstmt.setClob(i++, clb);
      pstmt.executeUpdate();
      
      ResultSet rs = pstmt.getGeneratedKeys();
      if(rs.next()) {
        envio.setId(rs.getLong(1));
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
      logger.error("Erro ao executar comando de banco (EnvioProcessoDao)", e);
    } finally {
      if(con != null ) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    
    return envio;
  }
  
  public void alteraEnvioProcessoEnviado(Long idEnvioProcesso, String infoEnvio, boolean flParseOk) {
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("UPDATE JN_ENVIO_PROCESSO SET INFO_ENVIO = ?, FL_PARSE_OK = ? "
          + "WHERE ID = ?");
      pstmt.setString(1, infoEnvio);
      pstmt.setInt(2, flParseOk ? 1 : 0);
      pstmt.setLong(3, idEnvioProcesso);
      pstmt.executeUpdate();
      pstmt.close();
    } catch (SQLException e) {
      logger.error("Erro ao executar comando de banco (EnvioProcessoDao)", e);
    } finally {
      if(con != null ) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
  }
  
  private EnvioProcessoXML montaEnvioProcesso(ResultSet rs) throws Exception {
    EnvioProcessoXML envioProcesso = new EnvioProcessoXML();    
    envioProcesso.setId(rs.getLong("ID"));
    envioProcesso.setNumero(rs.getString("NUMERO"));
    envioProcesso.setCompetencia(rs.getInt("COMPETENCIA"));        
    envioProcesso.setClasseProcessual(rs.getString("CLASSE_PROCESSUAL"));
    envioProcesso.setCodLocalidade(rs.getInt("CD_LOCALIDADE"));
    envioProcesso.setNivelSigilo(rs.getInt("NIVEL_SIGILO"));
    envioProcesso.setIntervencaoMp(rs.getInt("INTERVENCAO_MP"));
    envioProcesso.setTamanhoProcesso(rs.getInt("TAMANHO_PROCESSO"));
    envioProcesso.setDtAjuizamento(rs.getTimestamp("DT_AJUIZAMENTO"));
    envioProcesso.setFlEnviado(rs.getInt("FL_ENVIADO") > 0 ? true : false);
    envioProcesso.setInfoEnvio(rs.getString("INFO_ENVIO"));
    envioProcesso.setElementoXml(readClob(rs.getClob("DADO_ENVIO")));
    return envioProcesso;
  }

  private String readClob(Clob clob) throws Exception {
    //IOUtils.copy(clob.getAsciiStream(), sb);
    StringBuffer sb = new StringBuffer();
    
    Reader reader = new InputStreamReader(clob.getAsciiStream(),Charset.forName("UTF-8"));
    BufferedReader br = new BufferedReader(reader);

    String line;
    while(null != (line = br.readLine())) {
        sb.append(line);
    }
    br.close();

    return sb.toString();    
  }
}
