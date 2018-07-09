package br.jus.tredf.justicanumeros.dao.justificativa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.Cartorio;
import br.jus.tredf.justicanumeros.model.Indicador;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.justificativa.GrupoIndicador;
import br.jus.tredf.justicanumeros.model.justificativa.Observacao;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("JustificativaDao")
public class JustificativaDao {
  private static final Logger logger = Logger.getLogger(JustificativaDao.class);
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  private HashMap<String, byte[]> reportPorCompetencia = new HashMap<String, byte[]>();
  private HashMap<String, byte[]> reportPorCartorio = new HashMap<String, byte[]>();
  
  
  /**
   * Adiciona uma nova observa��o
   * @param observacao Observa��o a ser adicionada
   * @return O objeto obersava��o inserido j� com seu identificador �nico
   */
  public Observacao addNewObservation(Observacao observacao) {
    if(observacao == null ||
        observacao.getCodIndicador() <= 0 ||
        observacao.getDtReferencia() == null ||
        StringUtils.isEmpty(observacao.getProtocolo()) ||
        StringUtils.isEmpty(observacao.getJustificativa()) ||
        observacao.getCartorio() == null) {
      throw new ParametroException(bundle.getString("JustificativaDao.erro.parametrosinvalidos"), 
          ICodigosErros.ERRO_JUSTIFICATIVAS_PARAMETROINVALIDO);
    }
    Connection conn = null;
    try {
      String[] keys = new String[]{"ID"};
      conn = dataSource.getConnection();
      PreparedStatement pstmtI = conn.prepareStatement("INSERT INTO JN_PRODSERV_OBS "
          + "(COD_INDICADOR, DT_REFERENCIA, PROTOCOLO, JUSTIFICATIVA, FL_REG_NOVO, JN_CARTORIOID) "
          + "VALUES (?,?,?,?,?,?)", keys);
      pstmtI.setLong(1, observacao.getCodIndicador());
      pstmtI.setDate(2, new java.sql.Date(observacao.getDtReferencia().getTime()));
      pstmtI.setString(3, observacao.getProtocolo());
      pstmtI.setString(4, observacao.getJustificativa());
      pstmtI.setInt(5, observacao.isFlRegNovo() ? 1 : 0);
      pstmtI.setLong(6, observacao.getCartorio().getId());
      pstmtI.executeUpdate();
      ResultSet rs = pstmtI.getGeneratedKeys();
      if(rs.next()) {
        observacao.setId(rs.getLong(1));
      }
      rs.close();
      pstmtI.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
        }
      }
    }
    return observacao;
  }
  
  public List<Indicador> getIndicadores() {
    List<Indicador> indicadores = new ArrayList<Indicador>();
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      PreparedStatement pstmt = conn.prepareStatement("SELECT COD_INDICADOR, DES_INDICADOR, SGL_INDICADOR "
          +"FROM DW_CNJ.CNJ_INDICADORES INDIC "
          +"WHERE INDIC.COD_INDICADOR IN (20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, "  
                                                        +"33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44)");
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        Indicador indic = new Indicador();
        indic.setId(rs.getLong("COD_INDICADOR"));
        indic.setNome(rs.getString("DES_INDICADOR"));
        indic.setSigla(rs.getString("SGL_INDICADOR"));
        indicadores.add(indic);
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
        }
      }
    }
    return indicadores;    
  }
  
  public List<Observacao> getRegistrosProdutividadeComObservacoes(Date dataReferencia, 
  		String secao, int grauIndicador, int idGrupoObservacao) {
    if(dataReferencia == null || 
        StringUtils.isEmpty(secao)) {
      throw new ParametroException(bundle.getString("JustificativaDao.erro.parametrosinvalidos"), 
          ICodigosErros.ERRO_JUSTIFICATIVAS_PARAMETROINVALIDO);
    }
    List<Observacao> lst = new ArrayList<Observacao>();
    Connection conn = null;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
      conn = dataSource.getConnection();
      PreparedStatement pstmtQ2 = conn.prepareStatement("SELECT OBSERV.*, CARTORIO.SIGLA AS SIGLA_CARTORIO, INDIC.DES_INDICADOR AS INDICADOR, '' as CATEGORIA "
              +"FROM JN_PRODSERV_OBS OBSERV "
              +"INNER JOIN JN_CARTORIO CARTORIO ON CARTORIO.ID = OBSERV.JN_CARTORIOID "
              +"INNER JOIN DW_CNJ.CNJ_INDICADORES INDIC ON INDIC.COD_INDICADOR = OBSERV.COD_INDICADOR "
              +"WHERE CARTORIO.SIGLA = ? "
              +"AND FL_REG_NOVO = 1 "
              +"AND TO_CHAR(DT_REFERENCIA, 'YYYYMM') = ?");
      PreparedStatement pstmtQ1 = conn.prepareStatement("SELECT DADOS.COD_INDICADOR,\r\n" + 
      		"DADOS.DAT_REFERENCIA AS DT_REFERENCIA,\r\n" + 
      		"INDIC.DES_INDICADOR AS INDICADOR,\r\n" + 
      		"DADOS.NR_PROT AS PROTOCOLO,\r\n" + 
      		"DADOS.NR_PROCESSO AS PROCESSO,\r\n" + 
      		"DADOS.CD_CLASSE_CNJ AS CLASSE,\r\n" + 
      		"DADOS.DS_CLASSE AS DESC_CLASSE,\r\n" + 
      		"DADOS.SG_CLASSE AS SG_CLASSE,\r\n" + 
      		"DADOS.DS_ASSUNTO AS ASSUNTO,\r\n" + 
      		"DADOS.SG_SECAO AS SIGLA_CARTORIO,\r\n" + 
      		"CARTORIO.ID AS JN_CARTORIOID,\r\n" + 
      		"OBSERV.ID,\r\n" + 
      		"OBSERV.JUSTIFICATIVA,\r\n" + 
      		"OBSERV.COMENTARIO_SABAD,\r\n" + 
      		"CATEGORIAS.DES_CATEGORIA AS CATEGORIA,\r\n" + 
      		"CNJINDIC.SGL_INDICADOR AS SIGLA_INDICADOR,\r\n" + 
      		"0 AS FL_REG_NOVO  \r\n" + 
      		"FROM DW_CNJ.CNJ_DADOS DADOS  \r\n" + 
      		"INNER JOIN DW_CNJ.CNJ_INDICADORES INDIC ON INDIC.COD_INDICADOR = DADOS.COD_INDICADOR  \r\n" + 
      		"INNER JOIN JN_CARTORIO CARTORIO ON CARTORIO.SIGLA = DADOS.SG_SECAO  \r\n" + 
      		"INNER JOIN DW_CNJ.cnj_indicadores CNJINDIC ON CNJINDIC.COD_INDICADOR =  INDIC.COD_INDICADOR  \r\n" + 
      		"INNER JOIN DW_CNJ.CNJ_CATEGORIAS CATEGORIAS ON CATEGORIAS.COD_CATEGORIA = CNJINDIC.COD_CATEGORIA  \r\n" + 
      		"INNER JOIN dw_cnj.cnj_grupo GRP ON grp.cod_grupo = CATEGORIAS.COD_GRUPO \r\n" + 
      		"LEFT JOIN JN_PRODSERV_OBS OBSERV ON (OBSERV.COD_INDICADOR = DADOS.COD_INDICADOR   \r\n" + 
      		"                                     AND TO_CHAR(OBSERV.DT_REFERENCIA, 'YYYYMM') = TO_CHAR(DADOS.DAT_REFERENCIA, 'YYYYMM')   \r\n" + 
      		"                                     AND OBSERV.PROTOCOLO = DADOS.NR_PROT)  \r\n" + 
      		"WHERE TO_CHAR(DADOS.DAT_REFERENCIA, 'YYYYMM') = ?  \r\n" + 
      		"AND DADOS.SG_SECAO = ?  \r\n" + 
      		"AND CNJINDIC.COD_CATEGORIA IN (1, 2, 33, 34)  \r\n" + 
      		"AND INDIC.GRAU_INDICADOR = ?  \r\n" + 
      		"AND (OBSERV.FL_REG_NOVO = 0 OR OBSERV.FL_REG_NOVO IS NULL)  \r\n" + 
      		"AND GRP.COD_GRUPO = ? \r\n" + 
      		"ORDER BY INDIC.DES_INDICADOR");
      pstmtQ1.setString(1, sdf.format(dataReferencia));
      pstmtQ1.setString(2, secao);
      pstmtQ1.setInt(3, grauIndicador);
      pstmtQ1.setInt(4, idGrupoObservacao);
      ResultSet rsQ1 = pstmtQ1.executeQuery();
      while(rsQ1.next()) {
        Observacao obs = materializaObservacao(rsQ1);
        lst.add(obs);        
      }
      
      pstmtQ2.setString(1, secao);
      pstmtQ2.setString(2,sdf.format(dataReferencia));
      ResultSet rsQ2 = pstmtQ2.executeQuery();
      while(rsQ2.next()) {
        Observacao obs = materializaObservacao(rsQ2);
        lst.add(obs);
      }
      
      rsQ1.close();
      pstmtQ1.close();
      rsQ2.close();
      pstmtQ1.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
        }
      }
    }    
    return lst;
  }
  
  /**
   * 
   * @param obs
   * @return
   */
  public Observacao getObservacaoByLKey(Observacao obs) {
    if(obs == null ||
        obs.getCodIndicador() <= 0 ||
        obs.getDtReferencia() == null ||
        StringUtils.isEmpty(obs.getProtocolo())) {
      throw new ParametroException(bundle.getString("JustificativaDao.erro.parametrosinvalidos"), 
          ICodigosErros.ERRO_JUSTIFICATIVAS_PARAMETROINVALIDO);
    }
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      PreparedStatement pstmtQ = conn.prepareStatement("SELECT OBS.*, CARTORIO.SIGLA AS SIGLA_CARTORIO, INDIC.DES_INDICADOR AS INDICADOR "
          +"FROM JN_PRODSERV_OBS OBS "
          +"INNER JOIN JN_CARTORIO CARTORIO ON CARTORIO.ID = OBS.JN_CARTORIOID "
          +"INNER JOIN DW_CNJ.CNJ_INDICADORES INDIC ON INDIC.COD_INDICADOR = OBS.COD_INDICADOR "
          +"WHERE OBS.COD_INDICADOR = ? AND OBS.DT_REFERENCIA = ? AND OBS.PROTOCOLO = ?");
      pstmtQ.setInt(1, obs.getCodIndicador());
      pstmtQ.setDate(2, new java.sql.Date(obs.getDtReferencia().getTime()));
      pstmtQ.setString(3, obs.getProtocolo());
      ResultSet rs = pstmtQ.executeQuery();
      if (rs.next()) {
        obs = materializaObservacao(rs);
      }
      rs.close();
      pstmtQ.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
        }
      }
    }
    return obs;
  }
  
  public byte[] reportOrderPorCartorioIntoPDF(String competencia, String cartorio) {
    byte[] pdfFile = null;
    String chave = competencia + "_" + cartorio;
    if (reportPorCartorio.get(chave) == null) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("competencia", competencia);
      map.put("sg_secao", cartorio);
      Connection conn = null;
      try {
        conn = dataSource.getConnection();
        JasperReport report =
          (JasperReport) JRLoader.loadObject(this.getClass().getResourceAsStream(
              "/ObservacoesRespondidas/JustificativasRespostasCompCartorio.jasper"));
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
      reportPorCartorio.put(chave, pdfFile);
    } else {
      pdfFile = reportPorCartorio.get(chave);
    }
    return pdfFile;
  } 
  
  public byte[] reportOrderPorCompetenciaIntoPDF(String competencia) {
    byte[] pdfFile = null;
    if (reportPorCompetencia.get(competencia) == null) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("competencia", competencia);
      Connection conn = null;
      try {
        conn = dataSource.getConnection();
        JasperReport report =
          (JasperReport) JRLoader.loadObject(this.getClass().getResourceAsStream(
              "/RelObservacoesGerados/IndicadoresEObservacoes.jasper"));
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
      reportPorCompetencia.put(competencia, pdfFile);
    } else {
      pdfFile = reportPorCompetencia.get(competencia);
    }
    return pdfFile;
  } 

  
  public Observacao getObservacaoById(Observacao obs) {
    if(obs == null ||
        obs.getId() <= 0) {
      throw new ParametroException(bundle.getString("JustificativaDao.erro.parametrosinvalidos"), 
          ICodigosErros.ERRO_JUSTIFICATIVAS_PARAMETROINVALIDO);
    }
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      PreparedStatement pstmtQ = conn.prepareStatement("SELECT OBS.*, CARTORIO.SIGLA AS SIGLA_CARTORIO, "
      		+ "INDIC.DES_INDICADOR AS INDICADOR, '' as CATEGORIA,"
      		+ "INDIC.SGL_INDICADOR AS SIGLA_INDICADOR "
          + "FROM JN_PRODSERV_OBS OBS "
          +"INNER JOIN JN_CARTORIO CARTORIO ON CARTORIO.ID = OBS.JN_CARTORIOID "
          +"INNER JOIN DW_CNJ.CNJ_INDICADORES INDIC ON INDIC.COD_INDICADOR = OBS.COD_INDICADOR "
          + "WHERE OBS.ID = ?");
      pstmtQ.setLong(1, obs.getId());
      ResultSet rs = pstmtQ.executeQuery();
      if (rs.next()) {
        obs = materializaObservacao(rs);
      }
      rs.close();
      pstmtQ.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
        }
      }
    }
    return obs;
  }
  
  /**
   * 
   * @return
   */
  public List<GrupoIndicador> getGruposIndicador() {
  	List<GrupoIndicador> grupos = new ArrayList<GrupoIndicador>();
  	Connection conn = null;
  	try {
			conn = dataSource.getConnection();
			ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM DW_CNJ.CNJ_GRUPO "
					+ "WHERE COD_GRUPO IN (1, 4)");
			while(rs.next()) {
				GrupoIndicador grupo = new GrupoIndicador();
				grupo.setId(rs.getInt("COD_GRUPO"));
				grupo.setDesGrupo(rs.getString("DES_GRUPO"));
				grupos.add(grupo);
			}
		} catch (SQLException e) {
			logger.error("Erro executando banco de dados", e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
  	
  	return grupos;
  }
  
  private Observacao materializaObservacao(ResultSet rs) throws SQLException {
    Observacao obs = null;
    obs = new Observacao();
    obs.setId(rs.getLong("ID"));
    obs.setCodIndicador(rs.getInt("COD_INDICADOR"));
    obs.setNomeIndicador(rs.getString("INDICADOR"));
    obs.setDtReferencia(rs.getDate("DT_REFERENCIA"));
    obs.setProtocolo(rs.getString("PROTOCOLO"));
    obs.setJustificativa(rs.getString("JUSTIFICATIVA"));
    obs.setFlRegNovo(rs.getInt("FL_REG_NOVO"));
    obs.setResposta(rs.getString("COMENTARIO_SABAD"));
    obs.setCategoria(rs.getString("CATEGORIA"));    
    obs.setSiglaIndicador(rs.getString("SIGLA_INDICADOR"));
    
    if(rs.getMetaData().getColumnCount() == 17) {
    	obs.setProcesso(rs.getString("PROCESSO"));
    	obs.setClasse(rs.getString("CLASSE"));
    	obs.setDsClasse(rs.getString("DESC_CLASSE"));
    	obs.setSgClasse(rs.getString("SG_CLASSE"));
    	obs.setAssunto(rs.getString("ASSUNTO"));
    }
    
    Cartorio cart = new Cartorio();
    cart.setId(rs.getLong("JN_CARTORIOID"));
    cart.setSigla(rs.getString("SIGLA_CARTORIO"));
    obs.setCartorio(cart);
    return obs;
  }
  
  public void alterarObservacao(Observacao observacao) {
    if(observacao == null ||
        observacao.getId() <= 0 ||
        observacao.getCodIndicador() <= 0 ||
        observacao.getDtReferencia() == null ||
        StringUtils.isEmpty(observacao.getProtocolo()) ||
        StringUtils.isEmpty(observacao.getJustificativa()) ||
        observacao.getCartorio() == null ||
        observacao.getCartorio().getId() <= 0) {
      throw new ParametroException(bundle.getString("JustificativaDao.erro.parametrosinvalidos"), 
          ICodigosErros.ERRO_JUSTIFICATIVAS_PARAMETROINVALIDO);
    }
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      PreparedStatement pstmtU = conn.prepareStatement("UPDATE JN_PRODSERV_OBS SET COD_INDICADOR = ?, "
          + "DT_REFERENCIA = ?, "
          + "PROTOCOLO = ?, "
          + "JUSTIFICATIVA = ?, "
          + "FL_REG_NOVO = ?, "
          + "JN_CARTORIOID = ? "
          + "WHERE ID = ?");
      pstmtU.setInt(1, observacao.getCodIndicador());
      pstmtU.setDate(2, new java.sql.Date(observacao.getDtReferencia().getTime()));
      pstmtU.setString(3, observacao.getProtocolo());
      pstmtU.setString(4, observacao.getJustificativa());
      pstmtU.setInt(5, observacao.isFlRegNovo() ? 1 : 0);
      pstmtU.setLong(6, observacao.getCartorio().getId());
      pstmtU.setLong(7, observacao.getId());
      pstmtU.executeUpdate();
      pstmtU.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
        }
      }
    }
  }
  
  public void apagarObservacao(Observacao observacao) {
    if(observacao == null ||
        observacao.getId() <= 0) {
      throw new ParametroException(bundle.getString("JustificativaDao.erro.parametrosinvalidos"), 
          ICodigosErros.ERRO_JUSTIFICATIVAS_PARAMETROINVALIDO);
    }    
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      PreparedStatement pstmtD = conn.prepareStatement("DELETE FROM JN_PRODSERV_OBS WHERE ID = ?");
      pstmtD.setLong(1, observacao.getId());
      pstmtD.executeUpdate();
      pstmtD.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
        }
      }
    }
  }
}
