package br.jus.tredf.justicanumeros.dao.impressometro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.impressometro.Impressora;

@Repository("ImpressoraDao")
public class ImpressoraDao {
  private static final Logger logger = Logger.getLogger(ImpressoraDao.class);
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  public void addImpressora(Impressora impressora) {
  	if(impressora == null || impressora.getId() == 0 || StringUtils.isEmpty(impressora.getIpAddress())) {
  		String msg = MessageFormat.format(bundle.getString("ImpressoraDao.erro.parametrosinvalidos"), 
  				"é necessário informar os dados da impressora (endereço ip)");
      throw new ParametroException(msg, 
          ICodigosErros.ERRO_IMPRESSOMETRO_GENERICO);    		
  	}
  	Connection con = null;
  	try {
			con = dataSource.getConnection();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (addImpressora)", e);
			throw new ParametroException("Erro ao adicionar impressora: " + e.getMessage());
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {}
			}
		}
  }
  
  public List<Impressora> getAllImpressoras() {
  	List<Impressora> impressoras = new ArrayList<Impressora>();
  	Connection con = null;
  	try {
			con = dataSource.getConnection();
			String qImp = "SELECT * FROM IMP_IMPRESSORA";
			ResultSet rs = con.createStatement().executeQuery(qImp);
			while(rs.next()) {
				Impressora imp = materializaImpressora(rs);
				impressoras.add(imp);
			}
			rs.close();
		} catch (SQLException e) {
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {}
			}
		}
  	
  	return impressoras;
  }

	private Impressora materializaImpressora(ResultSet rs) throws SQLException {
		Impressora imp = new Impressora();
		imp.setId(rs.getInt("ID"));
		imp.setIpAddress(rs.getString("IP_ADDRESS"));
		imp.setLocalizacao(rs.getString("LOCALIZACAO"));
		imp.setDetalhes(rs.getString("DETALHES"));
		imp.setHabilitado(rs.getInt("HABILITADO") > 0 ? true : false);
		imp.setSerialNumber(rs.getString("SERIAL_NUMBER"));
		return imp;
	}
	
	public void addHistImpressao(Impressora impressora) {
		if (impressora == null || impressora.getId() == 0 || impressora.getImpDetail() == null) {
  		String msg = MessageFormat.format(bundle.getString("ImpressoraDao.erro.parametrosinvalidos"), 
  				"é necessário informar os dados da impressora");
      throw new ParametroException(msg, 
          ICodigosErros.ERRO_IMPRESSOMETRO_GENERICO);    		
		}
		
		Connection con = null;
		try {
			con = dataSource.getConnection();
			String insHist = "INSERT INTO IMP_HIST_IMPRESSAO (IMP_IMPRESSORAID, DT_INFO, QTD_IMPRESSAO) VALUES (?,?,?)";
			PreparedStatement pstmt = con.prepareStatement(insHist);
			pstmt.setInt(1, impressora.getId());
			pstmt.setDate(2, new java.sql.Date(impressora.getImpDetail().dtColeta.getTime()));
			pstmt.setInt(3, impressora.getImpDetail().qtd);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (addHistImpressao)", e);
			throw new ParametroException("Erro ao adicionar historico de impressao: " + e.getMessage());
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {}
			}
		}
	}
  
  public void addHistoricoHabilitado(Impressora imp, Date dtMudanca) {
  	if (imp == null || dtMudanca == null) {
  		String msg = MessageFormat.format(bundle.getString("ImpressoraDao.erro.parametrosinvalidos"), 
  				"é necessário informar os dados da impressora e a data de mudança");
      throw new ParametroException(msg, 
          ICodigosErros.ERRO_IMPRESSOMETRO_GENERICO);
  	}
  	
  	Connection con = null;
  	try {
			con = dataSource.getConnection();
			String insc = "INSERT INTO IMP_HIST_HABILITADO (IMP_IMPRESSORAID, DT_MUDANCA, COD_MUDANCA) VALUES (?,?,?)";
			String updt = "UPDATE IMP_IMPRESSORA SET HABILITADO = ?, DETALHES = ?, SERIAL_NUMBER = ?, LOCALIZACAO = ? WHERE ID = ?";
			PreparedStatement pstmt = con.prepareStatement(insc);
			pstmt.setInt(1, imp.getId());
			pstmt.setDate(2, new java.sql.Date(dtMudanca.getTime()));
			pstmt.setInt(3, imp.isHabilitado() ? 1 : 0);
			pstmt.executeUpdate();
			pstmt.close();
			
			pstmt = con.prepareStatement(updt);
			pstmt.setInt(1, imp.isHabilitado() ? 0 : 1);
			pstmt.setString(2, imp.getDetalhes());
			pstmt.setString(3, imp.getSerialNumber());
			pstmt.setString(4, imp.getLocalizacao());
			pstmt.setInt(5, imp.getId());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (addHistoricoHabilitado)", e);
			throw new ParametroException("Erro ao adicionar Historico de habilitado: " + e.getMessage());
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {}
			}
		}
  	
  }

}
