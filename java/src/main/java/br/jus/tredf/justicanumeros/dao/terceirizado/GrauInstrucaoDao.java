package br.jus.tredf.justicanumeros.dao.terceirizado;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.terceirizado.GrauInstrucao;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("GrauInstrucaoDao")
public class GrauInstrucaoDao {
  private static final Logger logger = Logger.getLogger(TerceirizadoDao.class);
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  

  /**
   * 
   * @return
   */
  public List<GrauInstrucao> getTodos() {
    List<GrauInstrucao> graus = new ArrayList<GrauInstrucao>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM JN_GRAU_INSTRUCAO");
      while(rs.next()) {
        GrauInstrucao grau = new GrauInstrucao();
        grau.setId(rs.getInt("ID"));
        grau.setNome(rs.getString("NOME"));
        grau.setDescricao(rs.getString("DESCRICAO"));
        graus.add(grau);
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
    return graus;
  }
}
