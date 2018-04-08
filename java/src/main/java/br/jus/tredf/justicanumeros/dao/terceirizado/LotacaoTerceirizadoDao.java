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

import com.mchange.v2.c3p0.ComboPooledDataSource;

import br.jus.tredf.justicanumeros.model.terceirizado.LotacaoTerceirizado;

@Repository("LotacaoTerceirizadoDao")
public class LotacaoTerceirizadoDao {
  private static final Logger logger = Logger.getLogger(TerceirizadoDao.class);

  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  public List<LotacaoTerceirizado> getLotacoesTerceirizados() {
  	List<LotacaoTerceirizado> lotacoes = new ArrayList<LotacaoTerceirizado>();
  	Connection con = null;
  	try {
			con = dataSource.getConnection();
			String q = "SELECT * FROM JN_TERCEIRIZADO_LOTACAO";
			ResultSet rs = con.createStatement().executeQuery(q);
			while(rs.next()) {
				Integer id = rs.getInt("id");
				String nome = rs.getString("nome");
				String descricao = rs.getString("descricao");
				LotacaoTerceirizado lot = new LotacaoTerceirizado();
				lot.setId(id);
				lot.setNome(nome);
				lot.setDescricao(descricao);
				lotacoes.add(lot);
			}
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
  	return lotacoes;
  }
}
