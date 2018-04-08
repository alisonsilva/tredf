package br.jus.tredf.justicanumeros.dao.terceirizado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.terceirizado.AreaAtuacao;
import br.jus.tredf.justicanumeros.model.terceirizado.GrauInstrucao;
import br.jus.tredf.justicanumeros.model.terceirizado.LotacaoTerceirizado;
import br.jus.tredf.justicanumeros.model.terceirizado.Terceirizado;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("TerceirizadoDao")
public class TerceirizadoDao {
  private static final Logger logger = Logger.getLogger(TerceirizadoDao.class);
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  
  
  /**
   * Recupera todos os terceirizados (ativos e inativos estagi�rios ou n�o)
   * @return Listagem com todos os terceirizados
   */
  public List<Terceirizado> todosTerceirizados() {
    List<Terceirizado> tercs = new ArrayList<Terceirizado>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      ResultSet rs = con.createStatement().executeQuery("SELECT TER.ID AS ID_TER, "
          +     "TER.NOME AS NOME_TER, TER.ATIVO AS ATIVO_TER, "
          + "GI.ID AS ID_GRAU, GI.NOME AS NOME_GRAU, GI.DESCRICAO AS DESC_GRAU, "
          + "AA.ID AS ID_AREA, AA.NOME AS NOME_AREA, AA.DESCRICAO AS DESC_AREA, "
          +     "AA.ESTAGIARIO AS ESTAG_AREA, AA.NIVEL_INSTRUCAO AS NINST_AREA, "
          +     "TL.ID as ID_LOTACAO, TL.NOME AS NOME_LOTACAO "
          + "FROM JN_TERCEIRIZADO TER "
          + "INNER JOIN JN_GRAU_INSTRUCAO GI ON GI.ID = TER.JN_GRAU_INSTRUCAOID "
          + "LEFT JOIN JN_AREA_ATUACAO AA ON AA.ID = TER.JN_AREA_ATUACAOID "
          + "LEFT JOIN JN_TERCEIRIZADO_LOTACAO TL ON TL.ID = TER.JN_TERCEIRIZADO_LOTACAOID "
          + "WHERE TER.APAGADO <> 1 "
          + "ORDER BY TER.ID");
      while(rs.next()) {
        Terceirizado ter = new Terceirizado();
        GrauInstrucao grau = new GrauInstrucao();
        AreaAtuacao area = new AreaAtuacao();
        LotacaoTerceirizado lotacao = new LotacaoTerceirizado();
        
        int intAtivo = rs.getInt("ATIVO_TER");
        ter.setId(rs.getLong("ID_TER"));
        ter.setNome(rs.getString("NOME_TER"));
        ter.setAtivo(intAtivo > 0 ? true : false);
        
        grau.setId(rs.getInt("ID_GRAU"));
        grau.setNome(rs.getString("NOME_GRAU"));
        grau.setDescricao(rs.getString("DESC_GRAU"));
                
        Integer idArea = rs.getInt("ID_AREA");
        if (idArea != null) {
          int intEstag = rs.getInt("ESTAG_AREA");
          area.setId(idArea);
          area.setNome(rs.getString("NOME_AREA"));
          area.setDescricao(rs.getString("DESC_AREA"));
          area.setEstagiario(intEstag > 0 ? true : false);
          area.setNivelInstrucao(rs.getInt("NINST_AREA"));
        }
        Integer idLotacao = rs.getInt("ID_LOTACAO");
        if(idLotacao != null) {
        	lotacao = new LotacaoTerceirizado();
        	lotacao.setId(idLotacao);
        	lotacao.setNome(rs.getString("NOME_LOTACAO"));
          ter.setLotacao(lotacao);
        }
        ter.setAreaAtuacao(area);
        ter.setGrauInstrucao(grau);     
        tercs.add(ter);
      }
      rs.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return tercs;
  }
  
  public List<Terceirizado> getTerceirizadosFiltrados(String filtro) {
    List<Terceirizado> terceirizados = new ArrayList<Terceirizado>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT TER.ID AS ID_TER, "
          +     "TER.NOME AS NOME_TER, TER.ATIVO AS ATIVO_TER, "
          + "GI.ID AS ID_GRAU, GI.NOME AS NOME_GRAU, GI.DESCRICAO AS DESC_GRAU, "
          + "AA.ID AS ID_AREA, AA.NOME AS NOME_AREA, AA.DESCRICAO AS DESC_AREA, "
          +     "AA.ESTAGIARIO AS ESTAG_AREA, AA.NIVEL_INSTRUCAO AS NINST_AREA, "
          +     "TL.ID as ID_LOTACAO, TL.NOME AS NOME_LOTACAO "
          + "FROM JN_TERCEIRIZADO TER "
          + "INNER JOIN JN_GRAU_INSTRUCAO GI ON GI.ID = TER.JN_GRAU_INSTRUCAOID "
          + "LEFT JOIN JN_AREA_ATUACAO AA ON AA.ID = TER.JN_AREA_ATUACAOID "
          + "LEFT JOIN JN_TERCEIRIZADO_LOTACAO TL ON TL.ID = TER.JN_TERCEIRIZADO_LOTACAOID "
          + "WHERE (TER.NOME LIKE ? "
          + "OR GI.NOME LIKE ? "
          + "OR AA.NOME LIKE ?) "
          + "AND TER.APAGADO <> 1 "
          + "ORDER BY TER.ID");
      pstmt.setString(1, "%" + filtro.toUpperCase() + "%");
      pstmt.setString(2, "%" + filtro.toUpperCase() + "%");
      pstmt.setString(3, "%" + filtro.toUpperCase() + "%");
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        Terceirizado ter = montaTerceirizado(rs);
        terceirizados.add(ter);
      }
      rs.close();
      pstmt.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return terceirizados;
  }
  
  /**
   * Recupera informa��es de um terceirizado a partir do seu identificador �nico
   * @param idTerceirizado 
   * @return Terceirizado recuperado para o identificador �nico
   */
  public Terceirizado getTerceirizadoPorId(Long idTerceirizado) {
    Terceirizado ter = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      ter = getTerceirizadoPorId(idTerceirizado, con);
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return ter;
  }
  
  private Terceirizado getTerceirizadoPorId(Long idTerceirizado, Connection con) throws SQLException {
    Terceirizado ter = null;
    PreparedStatement pstmt = con.prepareStatement("SELECT TER.ID AS ID_TER, "
        +     "TER.NOME AS NOME_TER, TER.ATIVO AS ATIVO_TER, "
        + "GI.ID AS ID_GRAU, GI.NOME AS NOME_GRAU, GI.DESCRICAO AS DESC_GRAU, "
        + "AA.ID AS ID_AREA, AA.NOME AS NOME_AREA, AA.DESCRICAO AS DESC_AREA, "
        +     "AA.ESTAGIARIO AS ESTAG_AREA, AA.NIVEL_INSTRUCAO AS NINST_AREA, "
        +     "TL.ID as ID_LOTACAO, TL.NOME AS NOME_LOTACAO "
        + "FROM JN_TERCEIRIZADO TER "
        + "INNER JOIN JN_GRAU_INSTRUCAO GI ON GI.ID = TER.JN_GRAU_INSTRUCAOID "
        + "LEFT JOIN JN_AREA_ATUACAO AA ON AA.ID = TER.JN_AREA_ATUACAOID "
        + "LEFT JOIN JN_TERCEIRIZADO_LOTACAO TL ON TL.ID = TER.JN_TERCEIRIZADO_LOTACAOID "
        + "WHERE TER.ID = ?");
    pstmt.setLong(1, idTerceirizado);
    ResultSet rs = pstmt.executeQuery();
    if(rs.next()) {
      ter = montaTerceirizado(rs);        
    }
    return ter;
  }

  private Terceirizado montaTerceirizado(ResultSet rs) throws SQLException {
    Terceirizado ter = new Terceirizado();
    GrauInstrucao grau = new GrauInstrucao();
    AreaAtuacao area = new AreaAtuacao();
    LotacaoTerceirizado lotacao = new LotacaoTerceirizado();
    
    int intAtivo = rs.getInt("ATIVO_TER");
    ter.setId(rs.getLong("ID_TER"));
    ter.setNome(rs.getString("NOME_TER"));
    ter.setAtivo(intAtivo > 0 ? true : false);
    
    grau.setId(rs.getInt("ID_GRAU"));
    grau.setNome(rs.getString("NOME_GRAU"));
    grau.setDescricao(rs.getString("DESC_GRAU"));
            
    Integer idArea = rs.getInt("ID_AREA");
    if (idArea != null) {
      int intEstag = rs.getInt("ESTAG_AREA");
      area.setId(idArea);
      area.setNome(rs.getString("NOME_AREA"));
      area.setDescricao(rs.getString("DESC_AREA"));
      area.setEstagiario(intEstag > 0 ? true : false);
      area.setNivelInstrucao(rs.getInt("NINST_AREA"));
    }
    Integer idLotacao = rs.getInt("ID_LOTACAO");
    if(idLotacao != null) {
    	lotacao = new LotacaoTerceirizado();
    	lotacao.setId(idLotacao);
    	lotacao.setNome(rs.getString("NOME_LOTACAO"));
      ter.setLotacao(lotacao);
    }    
    ter.setAreaAtuacao(area);
    ter.setGrauInstrucao(grau);
    return ter;
  }  
  
  /**
   * Insere novo terceirizado no reposit�rio de armazenamento
   * @param terceirizado O terceirizado a ser inserido
   * @return O terceirizado inserido com seu identificador �nico
   */
  public Terceirizado insereTerceirizado(Terceirizado terceirizado) { 
    if(terceirizado == null || StringUtils.isEmpty(terceirizado.getNome()) || 
        terceirizado.getGrauInstrucao() == null || 
        terceirizado.getGrauInstrucao().getId() <= 0) {
      throw new ParametroException(
          bundle.getString("TerceirizadoDao.erro.parametrosinvalidos"), 
          ICodigosErros.ERRO_TERCEIRIZADO_PARAMETROINVALIDO); 
    } else if(terceirizado.getAreaAtuacao() != null && terceirizado.getAreaAtuacao().getId() <= 0) {
      throw new ParametroException(
          bundle.getString("TerceirizadoDao.erro.parametrosinvalidos"), 
          ICodigosErros.ERRO_TERCEIRIZADO_PARAMETROINVALIDO); 
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_TERCEIRIZADO WHERE UPPER(NOME) = ?");
      pstmt.setString(1, terceirizado.getNome().toUpperCase().trim());
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        throw new ParametroException(
            bundle.getString("TerceirizadoDao.erro.terceirizadoexistente"), 
            ICodigosErros.ERRO_TERCEIRIZADO_JAEXISTENTE);
      }
      
      String[] columnNames = {"ID"};
      PreparedStatement pstmtIns = con.prepareStatement("INSERT INTO JN_TERCEIRIZADO "
          + "(NOME, ATIVO, JN_GRAU_INSTRUCAOID, JN_AREA_ATUACAOID, JN_TERCEIRIZADO_LOTACAOID) "
          + "VALUES (?,?,?,?,?)", columnNames);
      int updtIdx = 1;
      pstmtIns.setString(updtIdx++, terceirizado.getNome().trim().toUpperCase());
      pstmtIns.setInt(updtIdx++, terceirizado.isAtivo() ? 1 : 0);
      pstmtIns.setInt(updtIdx++, terceirizado.getGrauInstrucao().getId());
      if(terceirizado.getAreaAtuacao() != null) {
        pstmtIns.setInt(updtIdx++, terceirizado.getAreaAtuacao().getId());
      } else {
        pstmtIns.setNull(updtIdx++, Types.INTEGER);
      }
      if(terceirizado.getLotacao() != null) {
      	pstmtIns.setInt(updtIdx++, terceirizado.getLotacao().getId());
      } else {
      	pstmtIns.setNull(updtIdx++, Types.INTEGER);
      }
      pstmtIns.executeUpdate();
      ResultSet rsKey = pstmtIns.getGeneratedKeys();
      if(rsKey.next()) {
        terceirizado.setId(rsKey.getLong(1));
      }
      rsKey.close();
      pstmtIns.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    
    return terceirizado;
  }
  
  /**
   * Altera informa��es de um determinado terceirizado 
   * @param terceirizado As informa��es do terceirizado a serem consideradas
   * para a altera��o
   */
  public void alteraTerceirizado(Terceirizado terceirizado) {
    if(terceirizado.getId() <= 0 || terceirizado.getGrauInstrucao() == null || 
        terceirizado.getGrauInstrucao().getId() <= 0 || 
        (terceirizado.getAreaAtuacao() != null && terceirizado.getAreaAtuacao().getId() <= 0)) {
      throw new ParametroException(
          bundle.getString("TerceirizadoDao.erro.parametrosinvalidos"), 
          ICodigosErros.ERRO_TERCEIRIZADO_PARAMETROINVALIDO); 
    }
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmtu = con.prepareStatement("UPDATE JN_TERCEIRIZADO SET NOME = ?, "
          + "JN_GRAU_INSTRUCAOID = ?, JN_AREA_ATUACAOID = ?, JN_TERCEIRIZADO_LOTACAOID = ?, ATIVO = ? WHERE ID = ?");
      int updtIdx = 1;
      pstmtu.setString(updtIdx++, terceirizado.getNome().trim().toUpperCase());
      pstmtu.setInt(updtIdx++, terceirizado.getGrauInstrucao().getId());
      if(terceirizado.getAreaAtuacao() != null) {
        pstmtu.setInt(updtIdx++, terceirizado.getAreaAtuacao().getId());
      } else {
        pstmtu.setNull(updtIdx++, Types.INTEGER);
      }
      if(terceirizado.getLotacao() != null) {
      	pstmtu.setInt(updtIdx++, terceirizado.getLotacao().getId());
      } else {
      	pstmtu.setNull(updtIdx++, Types.INTEGER);
      }
      pstmtu.setInt(updtIdx++, terceirizado.isAtivo() ? 1 : 0);
      pstmtu.setLong(updtIdx++, terceirizado.getId());
      pstmtu.executeUpdate();
      pstmtu.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
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
   * Apaga permanentemente o terceirizado passado como par�metro
   * @param terceirizado O terceirizado a ser passado
   * @return Terceirizado O terceirizado que acabou de ser apagado
   */
  public Terceirizado apagaTerceirizado(Terceirizado terceirizado) {
    if(terceirizado == null || terceirizado.getId() <= 0) {
      throw new ParametroException(
          bundle.getString("TerceirizadoDao.erro.parametrosinvalidos"), 
          ICodigosErros.ERRO_TERCEIRIZADO_PARAMETROINVALIDO); 
    }
    Connection con = null;
    Terceirizado ret = null;
    try {      
      con = dataSource.getConnection();
      ret = getTerceirizadoPorId(terceirizado.getId(), con);      
      PreparedStatement pstmtd = con.prepareStatement("UPDATE JN_TERCEIRIZADO SET APAGADO = 1 WHERE ID = ?");
      pstmtd.setLong(1, terceirizado.getId());
      pstmtd.executeUpdate();
      pstmtd.close();
    } catch (SQLException e) {
      logger.error("Erro executando banco de dados", e);
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
}
