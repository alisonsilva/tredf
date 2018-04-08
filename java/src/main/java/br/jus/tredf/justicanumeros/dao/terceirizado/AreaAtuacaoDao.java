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

import br.jus.tredf.justicanumeros.model.terceirizado.AreaAtuacao;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("AreaAtuacaoDao")
public class AreaAtuacaoDao {
  private static final Logger logger = Logger.getLogger(TerceirizadoDao.class);
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;

  /**
   * 
   * @return
   */
  public List<AreaAtuacao> getTodos() {
    List<AreaAtuacao> areas = new ArrayList<AreaAtuacao>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM JN_AREA_ATUACAO");
      while(rs.next()) {
        AreaAtuacao area = new AreaAtuacao();
        area.setId(rs.getInt("ID"));
        area.setEstagiario(rs.getInt("ESTAGIARIO") == 1 ? true : false);
        area.setDescricao(rs.getString("DESCRICAO"));
        area.setNivelInstrucao(rs.getInt("NIVEL_INSTRUCAO"));
        area.setNome(rs.getString("NOME"));
        areas.add(area);
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
    
    return areas;
  }
}
