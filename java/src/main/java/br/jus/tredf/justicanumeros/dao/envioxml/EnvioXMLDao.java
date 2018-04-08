package br.jus.tredf.justicanumeros.dao.envioxml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import br.jus.tredf.justicanumeros.dao.terceirizado.TerceirizadoDao;
import br.jus.tredf.justicanumeros.model.envioxml.ControleCriacaoXml;
import br.jus.tredf.justicanumeros.model.envioxml.EnvioXML;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Repository("EnvioXMLDao")
public class EnvioXMLDao {
  private static final Logger logger = Logger.getLogger(TerceirizadoDao.class);
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  
  public List<EnvioXML> getEnviosRealizados() {
  	List<EnvioXML> enviosRealizados = new ArrayList<EnvioXML>();
  	Connection con = null;
  	try {
			con = dataSource.getConnection();
			ResultSet rs = con.createStatement().executeQuery("select envio.id, " + 
					"    envio.dt_comando, " + 
					"    envio.fl_enviado, " + 
					"    envio.dt_ref_proc, " + 
					"    envio.jn_usuarioid, " + 
					"    cntrl.instancia, " + 
					"    cntrl.id as cntrl_id, " + 
					"    (select count(*) from jn_envio_processo proc " + 
					"     where proc.jn_envio_xmlid = envio.id " + 
					"     and proc.fl_enviado = 0) as qtd_nao_enviados, " + 
					"    (select count(*) from jn_envio_processo proc " + 
					"     where proc.jn_envio_xmlid = envio.id " + 
					"     and proc.fl_enviado = 1) as qtd_enviados " + 
					"from jn_envio_xml envio " + 
					"inner join JN_CNTRL_CRIACAO_XML cntrl on cntrl.id = envio.JN_CNTRL_CRIACAO_XMLID " + 
					"order by envio.id");
			while(rs.next()) {
				EnvioXML envio = new EnvioXML();
				envio.setDtComando(rs.getDate("dt_comando"));
				envio.setDtEnvio(rs.getDate("dt_ref_proc"));
				envio.setDtRefProc(rs.getDate("dt_ref_proc"));
				envio.setFlEnviado(rs.getInt("FL_ENVIADO") > 0 ? true : false);
				envio.setId(rs.getLong("id"));
				envio.setQtdProcessosEnviados(rs.getInt("qtd_enviados"));
				envio.setQtdProcessosNaoEnviados(rs.getInt("qtd_nao_enviados"));
				
				UsuarioVO usuario = new UsuarioVO();				
				usuario.id = rs.getLong("jn_usuarioid");
				envio.setUsuario(usuario);
				
				ControleCriacaoXml cntrlXml = new ControleCriacaoXml();
				cntrlXml.setId(rs.getLong("cntrl_id"));
				cntrlXml.setInstancia(rs.getInt("instancia"));
				envio.setControleCriacao(cntrlXml);
				
				enviosRealizados.add(envio);
			}			
		} catch (SQLException e) {
			logger.error("Erro executando consulta/alteração ao banco de dados: (EnvioXMLDao) ", e);
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
  	return enviosRealizados;
  }
  
  public EnvioXML getEnvioPorControleXml(Long controleXmlId) {
    EnvioXML envio = new EnvioXML();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_ENVIO_XML "
          + "WHERE jn_cntrl_criacao_xmlid = ?");
      pstmt.setLong(1, controleXmlId);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        envio = montarEnvioXML(rs);
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
    return envio;
  }
  
  public void setEnviado(Long envioId) {
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("UPDATE JN_ENVIO_XML SET FL_ENVIADO = 1, DT_ENVIO = ? WHERE ID = ?");
      pstmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
      pstmt.setLong(2, envioId);
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
  
  public EnvioXML getEnvioXML(Long id) {
    EnvioXML envio = new EnvioXML();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(
          "SELECT * FROM JN_ENVIO_XML WHERE ID = ?");
      pstmt.setLong(1, id);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        envio = montarEnvioXML( rs);        
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados: (EnvioXMLDao) ", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }    
    return envio;
  }
  
  
  public List<EnvioXML> getEnvioXMLPorDtEnvio(Date dataEnvio) {
    List<EnvioXML> lstRetorno = new ArrayList<EnvioXML>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_ENVIO_XML WHERE DT_ENVIO = ?");
      pstmt.setDate(1, new java.sql.Date(dataEnvio.getTime()));
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        lstRetorno.add(montarEnvioXML(rs));
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados: (EnvioXMLDao) ", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    
    return lstRetorno;
  }
  
  public EnvioXML inserirEnvioXML(EnvioXML envio) {
    if(envio == null || 
        envio.getDtComando() == null ||
        envio.getDtRefProc() == null ||
        envio.getUsuario() == null ||
        envio.getUsuario().id <= 0) {
      throw new ParametroException(bundle.getString("envioxml.erro.parametrosInvalidos"), 
          ICodigosErros.ERRO_ENVIOXML_PARAMETROINVALIDO);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO JN_ENVIO_XML "
          + "(DT_COMANDO, FL_ENVIADO, DT_ENVIO, DT_REF_PROC, JN_USUARIOID, JN_CNTRL_CRIACAO_XMLID) "
          + "VALUES (?,?,?,?,?,?)", new String[]{"ID"});
      pstmt.setTimestamp(1, new Timestamp(envio.getDtComando().getTime()));
      pstmt.setInt(2, envio.isFlEnviado() ? 1 : 0);
      if (envio.getDtEnvio() != null) {
        pstmt.setTimestamp(3, new Timestamp(envio.getDtEnvio().getTime()));
      } else {
        pstmt.setNull(3, Types.DATE);
      }
      pstmt.setDate(4, new java.sql.Date(envio.getDtRefProc().getTime()));
      pstmt.setLong(5, envio.getUsuario().id);
      pstmt.setLong(6, envio.getControleCriacao().getId());
      pstmt.executeUpdate();
      ResultSet rs = pstmt.getGeneratedKeys();
      if(rs.next()) {
        envio.setId(rs.getLong(1));
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados: (EnvioXMLDao) ", e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return envio;
  }
  
  public void removeEnvio(Long idEnvio) {
  	if(idEnvio == null || idEnvio <= 0) {
  		throw new ParametroException("Identificador inválido", ICodigosErros.ERRO_ENVIOXML_PARAMETROINVALIDO);
  	}
  	Connection con = null;
  	try {
			con = dataSource.getConnection();
			String selectCntrlId = "SELECT ENVIO.JN_CNTRL_CRIACAO_XMLID FROM JN_ENVIO_XML ENVIO WHERE ENVIO.ID = ?";
			String selectCntrl = "SELECT * FROM JN_CNTRL_CRIACAO_XML WHERE ID = ?";
			
			String delCntrl = "DELETE FROM JN_CNTRL_CRIACAO_XML WHERE ID = ?";
			String delEnvio = "DELETE FROM JN_ENVIO_XML WHERE ID = ?";
			String delEnvioProcesso = "DELETE FROM JN_ENVIO_PROCESSO WHERE JN_ENVIO_XMLID = ?";
			String delXmlGerado = "DELETE FROM DW_CNJ.CNJ_DADOS_XML " + 
					"WHERE TO_CHAR(DAT_REFERENCIA, 'MM/YYYY') = ? " + 
					"AND GRAU = ?";
			
			PreparedStatement pstmtSql = con.prepareStatement(selectCntrlId);
			pstmtSql.setLong(1, idEnvio);
			ResultSet rs = pstmtSql.executeQuery();
			Long cntrlId = 0l;
			if(rs.next()) {
				cntrlId = rs.getLong(1);
			} else {				
	  		throw new ParametroException("Identificador inválido", ICodigosErros.ERRO_ENVIOXML_PARAMETROINVALIDO);
			}
			rs.close();
			pstmtSql.close();
			
			PreparedStatement pstmtSqlCntrl = con.prepareStatement(selectCntrl);
			pstmtSqlCntrl.setLong(1, cntrlId);
			rs = pstmtSqlCntrl.executeQuery();
			if(rs.next()) {
				String competencia = rs.getString("COMPETENCIA");
				int instancia = rs.getInt("INSTANCIA");
				PreparedStatement pstmtDelXml = con.prepareStatement(delXmlGerado);
				pstmtDelXml.setString(1, competencia);
				pstmtDelXml.setInt(2, instancia);
				pstmtDelXml.executeUpdate();
				pstmtDelXml.close();
			} else {
				throw new ParametroException("Identificador inválido", ICodigosErros.ERRO_ENVIOXML_PARAMETROINVALIDO);
			}
			rs.close();
			pstmtSqlCntrl.close();
			
			PreparedStatement pstmtDelEnvioProcesso = con.prepareStatement(delEnvioProcesso);
			pstmtDelEnvioProcesso.setLong(1, idEnvio);
			pstmtDelEnvioProcesso.executeUpdate();
			pstmtDelEnvioProcesso.close();

			PreparedStatement pstmtDelEnvio = con.prepareStatement(delEnvio);
			pstmtDelEnvio.setLong(1, idEnvio);
			pstmtDelEnvio.executeUpdate();
			pstmtDelEnvio.close();
			
			PreparedStatement pstmtDelCntrl = con.prepareStatement(delCntrl);
			pstmtDelCntrl.setLong(1, cntrlId);
			pstmtDelCntrl.executeUpdate();
			pstmtDelCntrl.close();
			
		} catch (SQLException e) {
			logger.error("Erro executando banco de dados: (EnvioXMLDao) ", e);
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
  }

  public byte[] relatorioProcessosEnviadosCNJ(Long idEnvioXml) {
    byte[] pdfFile = null;
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("idEnvioXml", idEnvioXml);
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      JasperReport report =
        (JasperReport) JRLoader.loadObject(this.getClass().getResourceAsStream(
            "/RelEnvio/EnvioInfoProcessos.jasper"));
      JasperPrint jasperPrint = JasperFillManager.fillReport(report, map, conn);
      pdfFile = JasperExportManager.exportReportToPdf(jasperPrint);
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } catch (JRException e) {
      throw new ParametroException(e.getMessage(), ICodigosErros.REPORT_ERROR_GENERATION);
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
        }
      }
    }
    return pdfFile;
  }   
  
  public void validateXsd(InputStream xml, InputStream xsd) throws Exception {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = factory.newSchema(new StreamSource(xsd));
    Validator validator = schema.newValidator();
    validator.validate(new StreamSource(xml));
  }
  
  private EnvioXML montarEnvioXML(ResultSet rs) throws SQLException {
    EnvioXML envio = new EnvioXML();
    envio.setId(rs.getLong("ID"));
    envio.setDtComando(rs.getDate("DT_COMANDO"));
    envio.setFlEnviado(rs.getInt("FL_ENVIADO") == 1 ? true : false);
    envio.setDtEnvio(rs.getDate("DT_ENVIO"));
    envio.setDtRefProc(rs.getDate("DT_REF_PROC"));
    return envio;
  }
  
}
