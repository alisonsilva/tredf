package br.jus.tredf.justicanumeros.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.ClasseSadp;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository("SadpDao")
@SuppressWarnings("all")
public class SadpDao {

  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  
  /**
   * Verifica se o protocolo passado como parâmetro está definido 
   * no sistema SADP. Caso esteja, retorna true; caso não esteja,
   * retorna false
   * @param nrProtocolo Protocolo a ser analisado
   * @return True, caso o protocolo esteja definido no SADP, false 
   * caso contrário
   */
  public boolean isProtocoloValidoSadp(Long nrProtocolo) {
    if(nrProtocolo == null || nrProtocolo <= 0) {
      throw new ParametroException("Parâmetro inválido", ICodigosErros.ERRO_SADP_PARAMETROINVALIDO);
    }
    boolean valido = false;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM admsadp.protocolo WHERE NR_PROT = ?");
      pstmt.setLong(1, nrProtocolo);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        valido = true;
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
    return valido;
  }
  
  /**
   * Recupera as informações da classe processual do processo cujo protocolo está sendo passado
   * @param nrProtocolo Númoro do protocolo do processo para o qual se deseja recuperar a classe
   * @return Informações da classe processual do processo desejado 
   */
  public ClasseSadp getClasseProcesso(Long nrProtocolo) {
    if(nrProtocolo == null || nrProtocolo <= 0) {
      throw new ParametroException("Parâmetro inválido", ICodigosErros.ERRO_SADP_PARAMETROINVALIDO);
    }
    ClasseSadp classe = new ClasseSadp();
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT c.sg_classe, "
                 +" c.ds_classe "
          +" FROM   admsadp.processo_zona p, "
                 +" admsadp.classe_zona   c "
          +" WHERE  p.nr_prot = ? "
          +" AND    p.sq_classe = c.sq_classe");
      pstmt.setLong(1, nrProtocolo);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        classe.setNome(rs.getString("ds_classe"));
        classe.setSigla(rs.getString("sg_classe"));        
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
    return classe;
  }
}
