package br.jus.tredf.justicanumeros.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.Cartorio;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("CartorioDao")
public class CartorioDao implements Serializable {

  private static final long serialVersionUID = -3036083359156266992L;

  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;  


  /**
   * Recupera a listagem com todos os cart�rios cadastrados
   * @return Listagem com os cart�rios cadastrados
   */
  public List<Cartorio> getCartorios() {
    List<Cartorio> cartorios = new ArrayList<Cartorio>();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_CARTORIO");
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        Cartorio cartorio = new Cartorio();
        cartorio.setId(rs.getLong("ID"));
        cartorio.setNome(rs.getString("NOME"));
        cartorio.setSigla(rs.getString("SIGLA"));
        cartorio.setGrauIndicador(rs.getInt("GRAU_INDICADOR"));
        cartorios.add(cartorio);
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
  
  public Cartorio getCartorioPorId(Long id) {
    Cartorio cartorio = null;
    Connection con = null;
    
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmtQ = con.prepareStatement("SELECT * FROM JN_CARTORIO WHERE ID = ?");
      pstmtQ.setLong(1, id);
      ResultSet rs = pstmtQ.executeQuery();
      if(rs.next()) {
        cartorio = new Cartorio();
        cartorio.setId(new Long(id));
        cartorio.setNome(rs.getString("NOME"));
        cartorio.setSigla(rs.getString("SIGLA"));       
        cartorio.setGrauIndicador(rs.getInt("GRAU_INDICADOR"));
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
    return cartorio;
  }
}
