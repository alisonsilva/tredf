package br.jus.tredf.justicanumeros.dao.sao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import br.jus.tredf.justicanumeros.dao.envioxml.DadosEnvioDao;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.sao.FormularioExecucao;
import br.jus.tredf.justicanumeros.model.sao.IArquivo;
import br.jus.tredf.justicanumeros.model.sao.PropostaOrcamentaria;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

@Repository("PropOrcamentariaDao")
public class PropOrcamentariaDao {

  private static final Logger logger = Logger.getLogger(DadosEnvioDao.class);
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
	
	public FormularioExecucao inserirFormularioPropOrcamentaria(FormularioExecucao formulario) {
  	if (formulario == null || formulario.getDtReferencia() == null ||
  			formulario.getUsuario() == null) {
  		String msg = MessageFormat.format(bundle.getString("ExecucaoDao.erro.parametrosinvalidos"), 
  				"é necessário informar os dados da proposta orçamentária (data de referência e usuário)");
      throw new ParametroException(msg, 
          ICodigosErros.ERRO_SAO_PROPOSTA_ORC);  		
  	}
  	Connection con = null;
  	try {
			con = dataSource.getConnection();
    	con.setAutoCommit(false);

//    	PreparedStatement pstmtDtRef = con.prepareStatement("SELECT * FROM JN_FORMULARIO_PROP_ORC "
//					+ "WHERE DT_REFERENCIA = ?");
//			pstmtDtRef.setDate(1, new java.sql.Date(formulario.getDtReferencia().getTime()));
//			ResultSet rsDtRef = pstmtDtRef.executeQuery();
//			if(rsDtRef.next()) {
//				String msg = MessageFormat.format(bundle.getString("PropostaOrcamentaria.erro.propostaexistente"), 
//						formulario.getDtReferencia());
//				rsDtRef.close();
//	      throw new ParametroException(msg, 
//	          ICodigosErros.ERRO_SAO_PROPOSTA_ORC);  		
//			}
//			pstmtDtRef.close();
			
			PreparedStatement pstmtIFormExec = con.prepareStatement("INSERT INTO JN_FORMULARIO_PROP_ORC "
					+ "(DT_UPLOAD, USUARIO_ID, DT_REFERENCIA, PLEITOS) "
					+ "VALUES (?,?,?,?)", new String[] {"ID"});
			pstmtIFormExec.setDate(1, new java.sql.Date(System.currentTimeMillis()));
			pstmtIFormExec.setLong(2, formulario.getUsuario().id);
			pstmtIFormExec.setDate(3, new java.sql.Date(formulario.getDtReferencia().getTime()));
			pstmtIFormExec.setInt(4, formulario.getFlag());
			pstmtIFormExec.executeUpdate();
			
			ResultSet rs = pstmtIFormExec.getGeneratedKeys();
			if(rs.next()) {
				formulario.setId(rs.getLong(1));
				PreparedStatement pstmtExec = con.prepareStatement("INSERT INTO JN_PROP_ORCAMENTARIA (FORMULARIO_PROP_ORCID, "
						+ "ACAO_ORCAMENT, CATEGORIA_PROGRAMACAO, UNIDADE_ADMINISTRATIVA, DESPESA_AGENDADA, PLANO_INTERNO, "
						+ "GND, ELEMENTO_ID, ELEMENTO_DESC, SUBELEMENTO_ID, SUBELEMENTO_DESC, ITEM_DESPESA, PROPOSTA_UA1, "
						+ "PROPOSTA_FINAL, PLEITOS) "
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				for (IArquivo arquivo : formulario.getExecucao()) {
					PropostaOrcamentaria propOrcamentaria = (PropostaOrcamentaria)arquivo;					
					int qidx = 1;
					pstmtExec.setLong(qidx++, formulario.getId());
					pstmtExec.setString(qidx++, propOrcamentaria.getAcaoOrcamentaria());
					pstmtExec.setString(qidx++, propOrcamentaria.getCategoriaProgramacao());
					pstmtExec.setString(qidx++, propOrcamentaria.getUnidadeAdministrativa());
					pstmtExec.setString(qidx++, propOrcamentaria.getDespesaAgendada());
					pstmtExec.setString(qidx++, propOrcamentaria.getPlanoInterno());
					pstmtExec.setString(qidx++, propOrcamentaria.getGnd());
					pstmtExec.setString(qidx++, propOrcamentaria.getElementoId());
					pstmtExec.setString(qidx++, propOrcamentaria.getElementoDesc());
					pstmtExec.setString(qidx++, propOrcamentaria.getSubElementoId());
					pstmtExec.setString(qidx++, propOrcamentaria.getSubElementoDesc());
					pstmtExec.setString(qidx++, propOrcamentaria.getItemDespesa());
					
					insertNullOrValue(pstmtExec, propOrcamentaria.getPropostaUA1(), qidx++);
					insertNullOrValue(pstmtExec, propOrcamentaria.getPropostaFinal(), qidx++);

					pstmtExec.setInt(qidx++, formulario.getFlag());

					pstmtExec.executeUpdate();
					pstmtExec.clearParameters();
				}
				pstmtExec.close();
			}
			con.commit();
			rs.close();
			pstmtIFormExec.close();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {}
			logger.error("Erro ao executar comando de banco (inserirFormularioExecucao)", e);
			throw new ParametroException("Erro ao inserir arquivo de execução: " + e.getMessage());
		}  finally {
			if(con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (SQLException e) {
				}
			}
		}
  	
  	return formulario;		
	}
	
	private void insertNullOrValue(PreparedStatement pstmtExec, Double value, int qidx) throws SQLException {
		if(value != null) {
			pstmtExec.setBigDecimal(qidx, new BigDecimal(value));
		} else {
			pstmtExec.setNull(qidx, Types.NUMERIC);
		}
	}	
	
	
	public FormularioExecucao getFormularioPropOrcamentariaPorId(Long id) {
		if (id == null) {
  		String msg = MessageFormat.format(bundle.getString("PropostaOrcamentaria.erro.parametrosinvalidos"), 
  				"é necessário informar o identificador");
      throw new ParametroException(msg, 
          ICodigosErros.ERRO_SAO_PROPOSTA_ORC);			
		}
		FormularioExecucao formulario = new FormularioExecucao();
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtQForm = con.prepareStatement("SELECT * FROM JN_FORMULARIO_PROP_ORC "
					+ "WHERE ID = ?");
			PreparedStatement pstmtQExec = con.prepareStatement("SELECT * FROM JN_PROP_ORCAMENTARIA WHERE FORMULARIO_PROP_ORCID = ?");
			pstmtQForm.setLong(1, id);
			ResultSet rsQForm = pstmtQForm.executeQuery();
			if(rsQForm.next()) {
				formulario.setId(rsQForm.getLong("ID"));
				formulario.setDtUpload(rsQForm.getDate("DT_UPLOAD"));
				UsuarioVO usr = new UsuarioVO();
				usr.id = rsQForm.getLong("USUARIO_ID");
				formulario.setUsuario(usr);
				pstmtQExec.clearParameters();
				pstmtQExec.setLong(1, formulario.getId());
				ResultSet rsQExec = pstmtQExec.executeQuery();
				while(rsQExec.next()) {
					PropostaOrcamentaria exec = povoarPropostaOrcamentaria(rsQExec);					
					formulario.getExecucao().add(exec);
				}
				rsQExec.close();
			} 
			rsQForm.close();
			pstmtQForm.close();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (getFormularioDotacaoPorDtReferencia)", e);
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return formulario;
	}		
	
	public List<FormularioExecucao> getFormulariosPropostaOrcamentaria() {
		List<FormularioExecucao> formularios = new ArrayList<FormularioExecucao>();
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtFE = con.prepareStatement("SELECT * FROM JN_FORMULARIO_PROP_ORC ORDER BY DT_REFERENCIA");
			PreparedStatement pstmtE = con.prepareStatement("SELECT  * FROM JN_PROP_ORCAMENTARIA "
					+ " WHERE FORMULARIO_PROP_ORCID = ? ORDER BY ID");
			ResultSet rs = pstmtFE.executeQuery();
			while(rs.next()) {
				FormularioExecucao form = new FormularioExecucao();
				form.setId(rs.getLong("ID"));
				form.setDtUpload(rs.getDate("DT_UPLOAD"));
				form.setDtReferencia(rs.getDate("DT_REFERENCIA"));
				UsuarioVO usuario = new UsuarioVO();
				usuario.id = rs.getLong("USUARIO_ID");
				form.setUsuario(usuario);
				
				pstmtE.clearParameters();
				pstmtE.setLong(1, rs.getLong("ID"));
				ResultSet rsExec = pstmtE.executeQuery();
				while(rsExec.next()) {
					PropostaOrcamentaria exec = povoarPropostaOrcamentaria(rsExec);
					form.getExecucao().add(exec);
				}
				formularios.add(form);
				rsExec.close();
			}
			pstmtFE.close();
			pstmtE.close();
			rs.close();
		} catch (Exception e) {
			logger.error("Erro ao executar comando de banco (getFormulariosPropostaOrcamentaria)", e);
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		return formularios;
	}	
	
	public void apagarFormularioPropostaOrcamentaria(Long id) {
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtDelFormExec = con.prepareStatement("DELETE FROM JN_FORMULARIO_PROP_ORC WHERE ID = ?");
			PreparedStatement pstmtDelExec = con.prepareStatement("DELETE FROM JN_PROP_ORCAMENTARIA WHERE FORMULARIO_PROP_ORCID = ?");
			pstmtDelExec.setLong(1, id);
			pstmtDelExec.executeUpdate();
			pstmtDelFormExec.setLong(1, id);
			pstmtDelFormExec.executeUpdate();
			pstmtDelExec.close();
			pstmtDelFormExec.close();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (apagarFormularioPropostaOrcamentaria)", e);
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}	
	
	private PropostaOrcamentaria povoarPropostaOrcamentaria(ResultSet rsQExec) throws SQLException {
		PropostaOrcamentaria exec = new PropostaOrcamentaria();
		exec.setId(rsQExec.getLong("ID"));
		exec.setFormularioPropOrcamentariaId(rsQExec.getLong("FORMULARIO_PROP_ORCID"));
		exec.setAcaoOrcamentaria(rsQExec.getString("ACAO_ORCAMENT"));
		exec.setCategoriaProgramacao(rsQExec.getString("CATEGORIA_PROGRAMACAO"));
		exec.setUnidadeAdministrativa(rsQExec.getString("UNIDADE_ADMINISTRATIVA"));
		exec.setDespesaAgendada(rsQExec.getString("DESPESA_AGENDADA"));
		exec.setPlanoInterno(rsQExec.getString("PLANO_INTERNO"));
		exec.setGnd(rsQExec.getString("GND"));
		exec.setElementoId(rsQExec.getString("ELEMENTO_ID"));
		exec.setElementoDesc(rsQExec.getString("ELEMENTO_DESC"));
		exec.setSubElementoId(rsQExec.getString("SUBELEMENTO_ID"));
		exec.setSubElementoDesc(rsQExec.getString("SUBELEMENTO_DESC"));
		exec.setItemDespesa(rsQExec.getString("ITEM_DESPESA"));
		exec.setPropostaUA1(rsQExec.getDouble("PROPOSTA_UA1"));
		exec.setPropostaFinal(rsQExec.getDouble("PROPOSTA_FINAL"));
		return exec;
	}	
}
