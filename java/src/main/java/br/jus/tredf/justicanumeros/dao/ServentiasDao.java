package br.jus.tredf.justicanumeros.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.Campo;
import br.jus.tredf.justicanumeros.model.Cartorio;
import br.jus.tredf.justicanumeros.model.LogAcoesServentia;
import br.jus.tredf.justicanumeros.model.ProdutividadeServentias;
import br.jus.tredf.justicanumeros.model.VisualizacaoCartoriosInfo;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("ServentiasDao")
@SuppressWarnings("all")
public class ServentiasDao implements Serializable {
  private static final long serialVersionUID = -7978546686402814360L;

  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  /**
   * Cria um novo registro para preenchimento de um cart�rio em uma dada compet�ncia, caso 
   * o registro ainda n�o exista
   * @param prodServentias Produtividade com o cart�rio e a compet�ncia para cria��o
   * @return Identificador do registro de produtividade de serventia criado
   * @throws ParametroException Principalmente quando j� existe um registro para 
   * a compet�ncia e cart�rio requeridos
   */
  public Long novoPreenchimento(ProdutividadeServentias prodServentias) {
    if(prodServentias == null || prodServentias.getDtCompetencia() == null || 
        prodServentias.getCartorio() == null || prodServentias.getCartorio().getId() == null) {
      throw new ParametroException(bundle.getString("ServentiasDao.novoPreenchimento.erro.parametros"), 
          ICodigosErros.ERRO_SERVENTIAS_NOVASERVENTIA);
    }
    Connection con = null;
    Long produtoServentiaId = null;
    try {
      String generatedColumns[] = { "ID" };
      con = dataSource.getConnection();
      PreparedStatement pstmt1 = con.prepareStatement(
          "SELECT * FROM JN_PRODSERVENTIAS WHERE DT_COMPETENCIA = ? AND JN_CARTORIOID = ?");
      if(isProdServExistenteCompetencia(
          prodServentias.getDtCompetencia(), 
          prodServentias.getCartorio().getId(), con)) {
        throw new ParametroException(MessageFormat.format(
            bundle.getString("ServentiasDao.novoPreenchimento.erro.nova"), 
            "existem registros para essa compet�ncia e esse cart�rio"), 
          ICodigosErros.ERRO_SERVENTIAS_NOVASERVENTIA);
      } else {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(prodServentias.getDtCompetencia());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        PreparedStatement pstmtIns = con.prepareStatement(
            "INSERT INTO JN_PRODSERVENTIAS (DT_COMPETENCIA, JN_CARTORIOID, DT_PREENCHIMENTO) VALUES (?, ?, ?)", 
            generatedColumns);
        pstmtIns.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
        pstmtIns.setLong(2, prodServentias.getCartorio().getId());
        if (prodServentias.getDtPreenchimento() != null) {
          cal = new GregorianCalendar();
          cal.setTime(prodServentias.getDtPreenchimento());
          cal.set(Calendar.HOUR_OF_DAY, 23);
          cal.set(Calendar.MINUTE, 59);
          cal.set(Calendar.SECOND, 59);          
          pstmtIns.setDate(3, new java.sql.Date(cal.getTimeInMillis()));
        } else {
          pstmtIns.setNull(3, Types.TIMESTAMP);
        }
        pstmtIns.executeUpdate();
        ResultSet rsIns = pstmtIns.getGeneratedKeys();
        if(rsIns.next()) {
          produtoServentiaId = rsIns.getLong(1);
          PreparedStatement pstmtCmpo = con.prepareStatement(
              "INSERT INTO JN_CAMPO (NOME, DESCRICAO, JN_PRODSERVID, FL_PROTOCOLO, AJUDA, COD_INDICADOR) VALUES (?, ?, ?, ?, ?, ?)");
          BufferedReader buffreader = new BufferedReader(
              new InputStreamReader(
                  this.getClass().getResourceAsStream("/ProdutividadeServentias.txt")));
          String linha = null;
          while((linha = buffreader.readLine()) != null) {
            StringTokenizer strtok = new StringTokenizer(linha, "|");
            String sigla = strtok.nextToken().trim();
            String desc = strtok.nextToken().trim();
            String strFlProtocolo = strtok.nextToken().trim();
            String ajuda = strtok.nextToken().trim();
            String indicador = strtok.nextToken().trim();
            pstmtCmpo.clearParameters();
            pstmtCmpo.setString(1, sigla);
            pstmtCmpo.setString(2, desc);
            pstmtCmpo.setLong(3, produtoServentiaId);
            pstmtCmpo.setInt(4, new Integer(strFlProtocolo));
            pstmtCmpo.setString(5, ajuda);
            pstmtCmpo.setInt(6, new Integer(indicador));
            pstmtCmpo.executeUpdate();            
          }
          buffreader.close();
          pstmtCmpo.close();
        }
        pstmtIns.close();
        rsIns.close();
      }
      pstmt1.close();
    } catch (ParametroException e) {
      throw e;
    } catch (Exception e) {      
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return produtoServentiaId;
  }
  
  /**
   * Inserir dados de preenchimento para uma determinada serventia
   * e compet�ncia
   * @param serventia Dados de preenchimento com a serventia e sua compet�ncia
   * @return Identificador �nico do registro que foi inserido
   * @throws ParametroException
   */
  public Long inserirServentia(ProdutividadeServentias serventia) 
      throws ParametroException {
    if(serventia == null || serventia.getCartorio() == null || 
        serventia.getCartorio().getId() == null ||
        serventia.getId() == null || serventia.getId() == 0) {
      throw new ParametroException("Valores da serventia est�o inv�lidos", 
          ICodigosErros.ERRO_SERVENTIAS_INCLUIR);
    }
    Long ret = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      if(isProdServExistenteCompetencia(
          serventia.getDtCompetencia(), 
          serventia.getCartorio().getId(), con)) {
        throw new ParametroException(MessageFormat.format(
            bundle.getString("ServentiasDao.novoPreenchimento.erro.nova"), 
            "existe registros para essa compet�ncia e esse cart�rio"), 
          ICodigosErros.ERRO_SERVENTIAS_NOVASERVENTIA);
      } else {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(serventia.getDtCompetencia());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        PreparedStatement pstmt = con.prepareStatement("UPDATE JN_PRODSERVENTIAS SET DT_PREENCHIMENTO = ?, "
            + "DT_COMPETENCIA = ?, JN_CARTORIO = ? WHERE ID = ?");
        pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        pstmt.setDate(2, new java.sql.Date(cal.getTimeInMillis()));
        pstmt.setLong(3, serventia.getCartorio().getId());
        pstmt.setLong(4, serventia.getId());
        pstmt.executeUpdate();
        
        PreparedStatement pstmtDCampos = con.prepareStatement("DELETE FORM JN_CAMPO WHERE JN_PRODSERVID = ?");
        pstmtDCampos.setLong(1, serventia.getId());
        pstmtDCampos.executeUpdate();
        pstmtDCampos.close();
        
        PreparedStatement pstmtICampo = con.prepareStatement("INSERT INTO JN_CAMPO "
            + "(NOME, DESCRICAO, VALOR, JN_PRODSERVID, FL_PROTOCOLO, AJUDA, COD_INDICADOR) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?) ");
        for(Campo campo : serventia.getCampos()) {
          pstmtICampo.clearParameters();
          pstmtICampo.setString(1, campo.getNome());
          pstmtICampo.setString(2, campo.getDescricao());
          pstmtICampo.setDouble(3, campo.getValor());
          pstmtICampo.setLong(4, serventia.getId());
          pstmtICampo.setInt(5, campo.isFlProtocolo() ? 1 : 0);
          pstmtICampo.setString(6, campo.getAjuda());
          pstmtICampo.setInt(7, campo.getIndicador() != null ? campo.getIndicador() : 0);
          pstmtICampo.executeUpdate();
        }
        pstmtICampo.close();
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
    return ret;
  }
  
  /**
   * Verifica se j� existe um registro de produtividade de serventia
   * para a competencia e cart�rio requisitados. Caso exista, retorna true.
   * Caso contr�rio, retorna false.
   * @param competencia A compet�ncia (m�s de refer�ncia) 
   * @param cartorio O cart�rio para o qual est� sendo preenchido
   * @param con Conex�o com banco de dados a ser utilizada
   * @return true, caso j� exista; false, caso contr�rio
   * @throws SQLException
   */
  private boolean isProdServExistenteCompetencia(Date competencia, 
      Long cartorio, 
      Connection con) throws SQLException {
    boolean ret = false;
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(competencia);
    cal.set(Calendar.DAY_OF_MONTH, 1);

    PreparedStatement pstmt1 = con.prepareStatement(
        "SELECT * FROM JN_PRODSERVENTIAS WHERE DT_COMPETENCIA = ? AND JN_CARTORIOID = ?");      
    pstmt1.setDate(1, new java.sql.Date(cal.getTime().getTime()));
    pstmt1.setLong(2, cartorio);
    ResultSet rs1 = pstmt1.executeQuery();
    if(rs1.next()) {
      ret = true;
    }
    pstmt1.close();
    rs1.close();
    return ret;
  }
  
  /**
   * Altera a data limite de preenchimento para um determinado formul�rio 
   * @param idProdServentia Identificador �nico da produtividade para a qual
   * se deseja alterar a data limite
   * @param dtFechamento Data limite de preenchimento nova para o formul�rio 
   */
  public void alteraDataLimite(Long idProdServentia,
      Date dtFechamento) {
    if(idProdServentia == null) {
      throw new ParametroException("Competencia ou cart�rio errado", 
          ICodigosErros.ERRO_SERVENTIAS_RECUPERAR);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();

      PreparedStatement pstmt = con.prepareStatement("UPDATE JN_PRODSERVENTIAS SET DT_PREENCHIMENTO = ? "
          + "WHERE ID = ?");
      if (dtFechamento != null) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dtFechamento);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        pstmt.setTimestamp(1, new java.sql.Timestamp(cal.getTimeInMillis()));
      } else {
        pstmt.setNull(1, Types.TIMESTAMP);
      }
      pstmt.setLong(2, idProdServentia);
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
   * Recupera a quantidade de protocolos ou valor referente ao campo passado.
   * Como um campo pode armazenar protocolos ou um valor fixo, � necess�rio
   * fazer essa cr�tica.
   * @param idCampo Identificador do campo para realizar essa pesquisa
   * @return Quantidade identificada para o campo: quantidade de protocolos, ou valor fixo
   */
  public int getValorCampo(Long idCampo) {
    if(idCampo == null || idCampo == null) {
      throw new ParametroException("Identificador inv�lido", 
          ICodigosErros.ERRO_SERVENTIAS_RECUPERAR);
    }
    int valor = 0;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_CAMPO CAMPO WHERE CAMPO.ID = ?");
      PreparedStatement pstmtQProt = con.prepareStatement("SELECT COUNT(*) FROM JN_PROTOCOLO_PROC WHERE JN_CAMPOID = ?");
      pstmt.setLong(1, idCampo);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        if(rs.getInt("FL_PROTOCOLO") == 1) {
          pstmtQProt.clearParameters();
          pstmtQProt.setLong(1, idCampo);
          ResultSet rs1 = pstmtQProt.executeQuery();
          if(rs1.next()) {
            valor = rs1.getInt(1);
          }
        } else {
          valor = (int) rs.getDouble("VALOR");
        }
      } else {
        throw new ParametroException("Identificador inv�lido", 
            ICodigosErros.ERRO_SERVENTIAS_RECUPERAR);
      }
      rs.close();
      pstmt.close();
      pstmtQProt.close();
    } catch (SQLException e) {
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return valor;
  }
  
  /**
   * Recupera o formul�rio preenchido da serventia por competencia e cart�rio
   * @param dtCompetencia Data da compet�ncia do preenchimento
   * @param idCartorio Identificador do cart�rio para o qual foi feito o preenchimento
   * @return Produtividade preenchida para o cart�rio e compet�ncia desejados
   */
  public ProdutividadeServentias getServentiaPorCompetenciaCartorio(
      Date dtCompetencia, Long idCartorio) {
    if(dtCompetencia == null || idCartorio == null) {
      throw new ParametroException("Competencia ou cart�rio errado", 
          ICodigosErros.ERRO_SERVENTIAS_RECUPERAR);
    }
    ProdutividadeServentias serventia = null;
    Connection con = null;
    try {
      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(dtCompetencia);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(
          "SELECT PRODSERV.*, CAR.NOME AS NOME_CAR, CAR.SIGLA AS SIGLA_CAR "
          + "FROM JN_PRODSERVENTIAS PRODSERV "
          + "INNER JOIN JN_CARTORIO CAR ON CAR.ID = PRODSERV.JN_CARTORIOID "
          + "WHERE DT_COMPETENCIA = ? "
          + "AND JN_CARTORIOID = ?");
      pstmt.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
      pstmt.setLong(2, idCartorio);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        serventia = new ProdutividadeServentias();
        int idx = 1;
        serventia.setId(rs.getLong("ID"));
        serventia.setDtPreenchimento(rs.getTimestamp("DT_PREENCHIMENTO"));
        serventia.setDtCompetencia(rs.getDate("DT_COMPETENCIA"));
        serventia.setFechado(rs.getInt("FECHADO") > 0 ? true : false);
        
        Cartorio cartorio = new Cartorio();
        cartorio.setId(idCartorio);
        cartorio.setNome(rs.getString("NOME_CAR"));
        cartorio.setSigla(rs.getString("SIGLA_CAR"));
        serventia.setCartorio(cartorio);        
        
        PreparedStatement pstmtQCampo = con.prepareStatement(
            "SELECT CAMPO.*  FROM JN_CAMPO CAMPO "
            + "WHERE CAMPO.JN_PRODSERVID = ?"
            + "ORDER BY CAMPO.ID");
        PreparedStatement pstmtQProt = con.prepareStatement("SELECT COUNT(*) FROM JN_PROTOCOLO_PROC "
            + "WHERE JN_CAMPOID = ?"); 
        pstmtQCampo.setLong(1, serventia.getId());
        ResultSet rsQCampo = pstmtQCampo.executeQuery();
        while(rsQCampo.next()) {
          pstmtQProt.clearParameters();
          Campo campo = new Campo();
          campo.setNome(rsQCampo.getString("NOME"));
          campo.setDescricao(rsQCampo.getString("DESCRICAO"));
          campo.setId(rsQCampo.getLong("ID"));
          campo.setAjuda(rsQCampo.getString("AJUDA"));
          campo.setFlProtocolo(rsQCampo.getInt("FL_PROTOCOLO") == 0 ? false : true);
          campo.setIndicador(rsQCampo.getInt("COD_INDICADOR"));
          
          pstmtQProt.setLong(1, campo.getId());
          ResultSet rsProt = pstmtQProt.executeQuery();
          int quantidade = 0;
          if(rsProt.next()) {
            quantidade = rsProt.getInt(1);
          }  
          //SE FOR COMPOSTO POR PROTOCOLOS, CONTAR OS PROTOCOLOS
          //SE N�O, PEGAR O VALOR
          if(campo.isFlProtocolo()) {            
            campo.setValor(new Double(quantidade > 0 ? quantidade : 0));            
          } else {
            campo.setValor(rsQCampo.getDouble("VALOR"));
          }
          
          serventia.getCampos().add(campo);
          rsProt.close();
        }
        rsQCampo.close();
        pstmtQCampo.close();
        pstmtQProt.close();
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
    return serventia;
  }
  
  /**
   * Recupera o formul�rio preenchido da serventia por seu identificador �nico
   * @param idProdServ Identificador �nico da produtividade da serventia de interesse
   * @return Produtividade preenchida para o cart�rio e compet�ncia desejados
   */
  public ProdutividadeServentias getServentiaPorId(Long idProdServ) {
    if(idProdServ == null || idProdServ <= 0) {
      throw new ParametroException("Competencia ou cart�rio errado", 
          ICodigosErros.ERRO_SERVENTIAS_RECUPERAR);
    }
    ProdutividadeServentias serventia = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(
          "SELECT PRODSERV.*, CAR.NOME AS NOME_CAR, CAR.SIGLA AS SIGLA_CAR "
          + "FROM JN_PRODSERVENTIAS PRODSERV "
          + "INNER JOIN JN_CARTORIO CAR ON CAR.ID = PRODSERV.JN_CARTORIOID "
          + "WHERE PRODSERV.ID = ?");
      pstmt.setLong(1, idProdServ);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        serventia = new ProdutividadeServentias();
        int idx = 1;
        serventia.setId(rs.getLong("ID"));
        serventia.setDtPreenchimento(rs.getTimestamp("DT_PREENCHIMENTO"));
        serventia.setDtCompetencia(rs.getDate("DT_COMPETENCIA"));
        Long idCartorio = rs.getLong("JN_CARTORIOID");
        if (idCartorio != null && idCartorio > 0) {
          Cartorio cartorio = new Cartorio();
          cartorio.setId(rs.getLong("JN_CARTORIOID"));
          cartorio.setNome(rs.getString("NOME_CAR"));
          cartorio.setSigla(rs.getString("SIGLA_CAR"));
          serventia.setCartorio(cartorio);
        }
        PreparedStatement pstmtQCampo = con.prepareStatement(
            "SELECT CAMPO.*  FROM JN_CAMPO CAMPO "
            + "WHERE CAMPO.JN_PRODSERVID = ? "
            + "ORDER BY CAMPO.ID");
        PreparedStatement pstmtQProt = con.prepareStatement("SELECT COUNT(*) FROM JN_PROTOCOLO_PROC "
            + "WHERE JN_CAMPOID = ?"); 
        pstmtQCampo.setLong(1, serventia.getId());
        ResultSet rsQCampo = pstmtQCampo.executeQuery();
        while(rsQCampo.next()) {
          pstmtQProt.clearParameters();
          Campo campo = new Campo();
          campo.setNome(rsQCampo.getString("NOME"));
          campo.setDescricao(rsQCampo.getString("DESCRICAO"));
          campo.setId(rsQCampo.getLong("ID"));
          campo.setAjuda(rsQCampo.getString("AJUDA"));
          campo.setFlProtocolo(rsQCampo.getInt("FL_PROTOCOLO") == 0 ? false : true);
          campo.setIndicador(rsQCampo.getInt("COD_INDICADOR"));
          
          pstmtQProt.setLong(1, campo.getId());
          ResultSet rsProt = pstmtQProt.executeQuery();
          int quantidade = 0;
          if(rsProt.next()) {
            quantidade = rsProt.getInt(1);
          }          
          //SE FOR COMPOSTO POR PROTOCOLOS, CONTAR OS PROTOCOLOS
          //SE N�O, PEGAR O VALOR
          if(campo.isFlProtocolo()) {            
            campo.setValor(new Double(quantidade > 0 ? quantidade : 0));            
          } else {
            campo.setValor(rsQCampo.getDouble("VALOR"));
          }          
          serventia.getCampos().add(campo);
          rsProt.close();
        }
        rsQCampo.close();
        pstmtQCampo.close();
        pstmtQProt.close();
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
    return serventia;
  }  
  
  /**
   * Recupera um formul�rio de preenchimento de Produtividade de Serventias a partir
   * do id de um de seus campos
   * @param idCampo Identificador �nico de um dos campos do formul�rio sendo recuperado
   * @return Formul�rio para o qual um de seus campos � o campo passado 
   */
  public ProdutividadeServentias getServentiaPorCampo(Long idCampo) {
    ProdutividadeServentias prodServ = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT pserv.* FROM JN_PRODSERVENTIAS PSERV "
          + "INNER JOIN JN_CAMPO CAMPO ON CAMPO.JN_PRODSERVID = PSERV.ID "
          + "WHERE CAMPO.ID = ?");
      pstmt.setLong(1, idCampo);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        prodServ = new ProdutividadeServentias();
        prodServ.setId(rs.getLong("ID"));
        prodServ.setDtPreenchimento(rs.getTimestamp("DT_PREENCHIMENTO"));
        prodServ.setDtCompetencia(rs.getTimestamp("DT_COMPETENCIA"));        
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
    return prodServ;
  }
  
  /**
   * Recupera um objeto ProdutividadeServentias a partir de um de seus campos
   * @param idCampo O campo a partir do qual ser� recuperada a Produtividade
   * da serventia (cart�rio)
   * @return Objeto ProdutividadeServentias com os dados dessa produtividade.
   */
  public ProdutividadeServentias getProdutividadeServentiaPorCampo(Long idCampo) {
    if(idCampo == null || idCampo == 0) {
      throw new ParametroException(
          bundle.getString("ServentiasDao.novoPreenchimento.erro.parametros"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    ProdutividadeServentias prodServ = null;
    Connection con = null; 
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT PROD.* FROM JN_PRODSERVENTIAS PROD "
          + "INNER JOIN JN_CAMPO CAMPO ON CAMPO.JN_PRODSERVID = PROD.ID "
          + "WHERE CAMPO.ID = ?");
      pstmt.setLong(1, idCampo);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        prodServ = new ProdutividadeServentias();
        prodServ.setId(rs.getLong(1));
        prodServ.setDtCompetencia(rs.getDate("DT_COMPETENCIA"));
        prodServ.setDtPreenchimento(rs.getDate("DT_PREENCHIMENTO"));
        Cartorio cartorio = new Cartorio();
        cartorio.setId(rs.getLong("JN_CARTORIOID"));
        prodServ.setCartorio(cartorio);
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
    return prodServ;
  }  
  
  /**
   * Insere um registro de log espec�fico para inclus�o de dados de serventia
   * @param log Informa��es espec�ficas a serem registradas em log
   * @return Identificador do registro inserido
   */
  public Long inserirLogServentias(LogAcoesServentia log) {
    if(log == null || StringUtils.isEmpty(log.getDescricao()) 
        || log.getUsuario() == null || log.getUsuario().id == 0) {
      throw new ParametroException("Erro ao inserir log", 
          ICodigosErros.ERRO_SERVENTIAS_INCLUIRLOG);
    }
    Long idLog = null;
    Connection con = null;
    try {
      String generatedColumns[] = { "ID" };
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO JN_LOG_ACOES_SERVENT "
          + "(DESCRICAO, DT_ACAO, JN_USUARIOID, JN_PRODSERVENTIASID) "
          + " VALUES (?,?,?,?)", generatedColumns);
      pstmt.setString(1, log.getDescricao());
      pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
      pstmt.setLong(3, log.getUsuario().id);
      if (log.getIdServentia() != null) {
        pstmt.setLong(4, log.getIdServentia());
      } else {
        pstmt.setNull(4, Types.INTEGER);
      }
      pstmt.executeUpdate();
      ResultSet rs = pstmt.getGeneratedKeys();
      if (rs.next()) {
        idLog = rs.getLong(1);
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e1) {
        }
      }
    }
    
    return idLog;
  }
  
  /**
   * Altera o valor de um campo em espec�fico para um formul�rio de Produtividade de Serventias.
   * Esse campo se refere a um indicador para uma compet�ncia e um cart�rio em espec�fico.
   * @param idCampo Identificador �nico do campo a ser alterado.
   * @param valor Novo valor a ser atribu�do ao campo.
   */
  public void alteraValorCampo(Long idCampo, Double valor) {
    if(idCampo == null || valor == null) {
      throw new ParametroException(bundle.getString("ServentiasDao.novoPreenchimento.erro.parametros"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("UPDATE JN_CAMPO SET VALOR = ? WHERE ID = ?");
      pstmt.setDouble(1, valor);
      pstmt.setLong(2, idCampo);
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
  
  /**
   * Altera a flag de fechamento do relat�rio de produtividade da serventia para preenchimento 
   * @param idProdutividade Formul�rio de preenchimento que ser� afetado pela mudan�a da 
   * flag.
   * @param valor Novo valo da flag podendo ser true ou false. True fecha o formul�rio
   * para edi��o
   */
  public void alteraFechamentoProdutividadeServentia(Long idProdutividade, boolean valor) {
    if(idProdutividade == null || idProdutividade <= 0) {
      throw new ParametroException(bundle.getString("ServentiasDao.novoPreenchimento.erro.parametros"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = 
          con.prepareStatement("UPDATE JN_PRODSERVENTIAS SET FECHADO = ? WHERE ID = ?");
      pstmt.setInt(1, (valor ? 1 : 0));
      pstmt.setLong(2, idProdutividade);
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
   * Duplica os dados de protocolos referentes � compet�ncia anterior na
   * compet�ncia da produtividade passada
   * @param idProdutividade Identificador �nico da produtividade a ser 
   * utilizada como refer�ncia para que os dados de protocolos sejam
   * duplicados na mesma
   */
  public void preencheProtocolosCompetenciaAnterior(Long idProdutividade) {
    if(idProdutividade == null || idProdutividade <= 0) {
      throw new ParametroException(bundle.getString("ServentiasDao.novoPreenchimento.erro.parametros"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmtQ1 = con.prepareStatement("SELECT * FROM JN_PRODSERVENTIAS WHERE ID = ?");
      pstmtQ1.setLong(1, idProdutividade);
      ResultSet rs1 = pstmtQ1.executeQuery();
      if(rs1.next()) {
        Date dtCompetencia = rs1.getDate("DT_COMPETENCIA");
        Long cartorioId = rs1.getLong("JN_CARTORIOID");
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dtCompetencia);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
        PreparedStatement pstmtQ3 = con.prepareStatement("SELECT PROC.*, (SELECT COD_INDICADOR FROM JN_CAMPO CAMPO "
            + " WHERE CAMPO.ID = PROC.JN_CAMPOID) AS COD_INDICADOR FROM JN_PROTOCOLO_PROC PROC WHERE PROC.JN_CAMPOID = ?");
        PreparedStatement pstmtD1 = con.prepareStatement("DELETE FROM JN_PROTOCOLO_PROC WHERE JN_CAMPOID IN "
            + "(SELECT ID FROM JN_CAMPO WHERE JN_PRODSERVID = ?)");
        PreparedStatement pstmtI1 = con.prepareStatement("INSERT INTO JN_PROTOCOLO_PROC (PROTOCOLO, JN_CAMPOID) "
            + "VALUES (?,(SELECT ID FROM JN_CAMPO WHERE JN_PRODSERVID = ? AND COD_INDICADOR = ?))");
        PreparedStatement pstmtQ2 = con.prepareStatement("SELECT * FROM JN_CAMPO WHERE JN_PRODSERVID = "
            + "(SELECT ID FROM JN_PRODSERVENTIAS WHERE DT_COMPETENCIA = ? AND JN_CARTORIOID = ?)");
        pstmtQ2.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
        pstmtQ2.setLong(2, cartorioId);
        ResultSet rs2 = pstmtQ2.executeQuery();
        pstmtD1.setLong(1, idProdutividade);
        pstmtD1.executeUpdate();
        while(rs2.next()) {
          pstmtQ3.clearParameters();
          pstmtQ3.setLong(1, rs2.getLong("ID"));
          ResultSet rs3 = pstmtQ3.executeQuery();
          while(rs3.next()) {
            pstmtI1.clearParameters();
            pstmtI1.setString(1, rs3.getString("PROTOCOLO"));
            pstmtI1.setLong(2, idProdutividade);
            pstmtI1.setInt(3, rs3.getInt("COD_INDICADOR"));
            pstmtI1.executeUpdate();
          }
          rs3.close();
        }
        pstmtQ2.close();
        pstmtQ3.close();
        pstmtD1.close();
        pstmtI1.close();
        rs2.close();        
      }
      pstmtQ1.close();
      rs1.close();
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
   * Verifica se a produtividade da serventia est� fechada para altera��o pelo usu�rio
   * @param idProtocolo Identificador �nico do protocolo para o qual se deseja 
   * realizar altera��es
   * @return False, caso a produtividade serventia esteja aberta para edi��o; false, caso
   * esteja fechada para edi��o
   */
  public boolean isFechadoOuExpiradoPorProtocolo(Long idProtocolo) {
    if(idProtocolo == null || idProtocolo <= 0) {
      throw new ParametroException(bundle.getString("ServentiasDao.novoPreenchimento.erro.parametros"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    boolean ret = false;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT PROD.* FROM JN_PROTOCOLO_PROC PROT "
          + "INNER JOIN JN_CAMPO CAMPO ON CAMPO.ID = PROT.JN_CAMPOID "
          + "INNER JOIN JN_PRODSERVENTIAS PROD ON PROD.ID = CAMPO.JN_PRODSERVID "
          + "WHERE PROT.ID = ?");
      pstmt.setLong(1, idProtocolo);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        int fechado = rs.getInt("FECHADO");
        Date dataAtual = new Date();
        Date dataFechamento = rs.getDate("DT_PREENCHIMENTO");
        if(fechado > 0) {
          ret = true;
        } else if(dataAtual.after(dataFechamento)) {
          ret = true;
        }
      } else {
        rs.close();
        pstmt.close();
        throw new ParametroException("Protocolo n�o encontrado", 
            ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
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
    
    return ret;
  }
  
  /**
   * Verifica se a produtividade da serventia est� fechada para altera��o pelo usu�rio
   * @param idCampo Identificador �nico do campo para o qual se deseja 
   * incluir novo protocolo
   * @return False, caso a produtividade serventia esteja aberta para edi��o; false, caso
   * esteja fechada para edi��o
   */
  public boolean isFechadoOuExpiradoPorCampo(Long idCampo) {
    if(idCampo == null || idCampo <= 0) {
      throw new ParametroException(bundle.getString("ServentiasDao.novoPreenchimento.erro.parametros"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    boolean ret = false;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT PROD.* FROM JN_CAMPO CAMPO "
          + "INNER JOIN JN_PRODSERVENTIAS PROD ON PROD.ID = CAMPO.JN_PRODSERVID "
          + "WHERE CAMPO.ID = ?");
      pstmt.setLong(1, idCampo);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        int fechado = rs.getInt("FECHADO");
        Date dataAtual = new Date();
        Date dataFechamento = rs.getDate("DT_PREENCHIMENTO");
        if(fechado > 0) {
          ret = true;
        } else if(dataAtual.after(dataFechamento)) {
          ret = true;
        }
      } else {
        rs.close();
        pstmt.close();
        throw new ParametroException("Protocolo n�o encontrado", 
            ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
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
    
    return ret;
  }  
  
  /**
   * Verifica se o formul�rio de produtividade est� fechado ou n�o para edi��o
   * @param idProdutividade Identificador do formul�rio de preenchimento
   * a se verificar se est� fechado
   * @return True, caso o formul�rio esteja fechado para altera��es; 
   * False, caso contr�rio
   */
  public boolean isFechadoOuExpirado(Long idProdutividade) {
    if(idProdutividade == null || idProdutividade <= 0) {
      throw new ParametroException(bundle.getString("ServentiasDao.novoPreenchimento.erro.parametros"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    boolean ret = false;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT PROD.* FROM JN_PRODSERVENTIAS PROD "
          + "WHERE PROD.ID = ?");
      pstmt.setLong(1, idProdutividade);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        int fechado = rs.getInt("FECHADO");
        Date dataAtual = new Date();
        Date dataFechamento = rs.getDate("DT_PREENCHIMENTO");
        if(fechado > 0) {
          ret = true;
        } else if(dataAtual.after(dataFechamento)) {
          ret = true;
        }
      } else {
        rs.close();
        pstmt.close();
        throw new ParametroException("Protocolo n�o encontrado", 
            ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
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
    
    return ret;
  }
  
  /**
   * Verifica se o formul�rio de produtividade est� fechado ou n�o para edi��o
   * @param idProdutividade Identificador do formul�rio de preenchimento
   * a se verificar se est� fechado
   * @return True, caso o formul�rio esteja fechado para altera��es; 
   * False, caso contr�rio
   */
  public boolean isFechado(Long idProdutividade) {
    if(idProdutividade == null || idProdutividade <= 0) {
      throw new ParametroException(bundle.getString("ServentiasDao.novoPreenchimento.erro.parametros"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    boolean ret = false;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT PROD.* FROM JN_PRODSERVENTIAS PROD "
          + "WHERE PROD.ID = ?");
      pstmt.setLong(1, idProdutividade);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        int fechado = rs.getInt("FECHADO");
        if(fechado > 0) {
          ret = true;
        }
      } else {
        rs.close();
        pstmt.close();
        throw new ParametroException("Protocolo n�o encontrado", 
            ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
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
    
    return ret;
  }  
  
  /**
   * Recupera informa��es consolidadas sobre o status de cada preenchimento, 
   * cart�rio a cart�rio, na compet�ncia
   * @param competencia A compet�ncia do preenchimento
   * @return Listagem com as informa��es consolidadas cart�rio a cart�rio
   */
  public List<VisualizacaoCartoriosInfo> relatorioVisualizacaoCartoriosCompetencia(Date competencia) {
    if(competencia == null) {
      throw new ParametroException(
          bundle.getString("ServentiasDao.novoPreenchimento.erro.parametros"), 
          ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    List<VisualizacaoCartoriosInfo> cartorios = 
        new ArrayList<VisualizacaoCartoriosInfo>();
    Connection con = null;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      con = dataSource.getConnection();
      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(competencia);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      PreparedStatement pstmt = con.prepareStatement("SELECT PROD.ID AS ID_PRODSERV, PROD.DT_COMPETENCIA, PROD.FECHADO, "
               + "PROD.ID AS ID_PRODUTIVIDADE, PROD.DT_PREENCHIMENTO, " 
               +" (CASE WHEN SYSTIMESTAMP > PROD.DT_PREENCHIMENTO THEN 1 ELSE 0 END) AS EXPIRADO, "
               +" CARTORIO.NOME as NOME_CARTORIO, CARTORIO.SIGLA, CARTORIO.ID AS ID_CARTORIO, "
               +" (SELECT COUNT(*) FROM JN_PROTOCOLO_PROC PROT " 
               +" INNER JOIN JN_CAMPO CAMPO ON CAMPO.ID = PROT.JN_CAMPOID "
               +" WHERE CAMPO.JN_PRODSERVID = PROD.ID) AS QTD_PROCESSOS "
        +" FROM JN_PRODSERVENTIAS PROD "
        +" INNER JOIN JN_CARTORIO CARTORIO ON CARTORIO.ID = PROD.JN_CARTORIOID "
        +" WHERE PROD.DT_COMPETENCIA = ?");
      pstmt.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        VisualizacaoCartoriosInfo info = new VisualizacaoCartoriosInfo();
        Date dtCompetencia = rs.getDate("DT_COMPETENCIA");
        Date dtFechamento = rs.getDate("DT_PREENCHIMENTO");
        info.setDtCompetencia(sdf.format(dtCompetencia));
        info.setExpirado(rs.getInt("EXPIRADO") == 1 ? true : false);
        info.setFechado(rs.getInt("FECHADO") == 1 ? true : false);
        info.setIdCartorio(rs.getLong("ID_CARTORIO"));
        info.setNomeCartorio(rs.getString("NOME_CARTORIO"));
        info.setSiglaCartorio(rs.getString("SIGLA"));
        info.setQuantidade(rs.getInt("QTD_PROCESSOS"));
        info.setDtFechamento(dtFechamento == null ? "" : sdf.format(dtFechamento));
        info.setIdRegistro(rs.getLong("ID_PRODSERV"));
        cartorios.add(info);
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
    return cartorios;
  }
}
