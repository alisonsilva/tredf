package br.jus.tredf.justicanumeros.dao.sao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import br.jus.tredf.justicanumeros.dao.envioxml.DadosEnvioDao;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.sao.Dotacao;
import br.jus.tredf.justicanumeros.model.sao.FormularioDotacao;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

@Repository("DotacaoDao")
public class DotacaoDao {

  private static final Logger logger = Logger.getLogger(DadosEnvioDao.class);
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  
  /**
   * 
   * @param formulario
   * @return
   */
  public FormularioDotacao inserirFormularioDotacao(FormularioDotacao formulario) {
  	if (formulario == null || formulario.getDtReferencia() == null ||
  			formulario.getUsuario() == null) {
  		String msg = MessageFormat.format(bundle.getString("ExecucaoDao.erro.parametrosinvalidos"), 
  				"é necessário informar os dados da execução (data de referência e usuário)");
      throw new ParametroException(msg, 
          ICodigosErros.ERRO_SAO_EXECUCAO);  		
  	}
  	Connection con = null;
  	try {
			con = dataSource.getConnection();
    	con.setAutoCommit(false);

    	PreparedStatement pstmtDtRef = con.prepareStatement("SELECT * FROM JN_FORMULARIO_DOTACAO "
					+ "WHERE DT_REFERENCIA = ?");
			pstmtDtRef.setDate(1, new java.sql.Date(formulario.getDtReferencia().getTime()));
			ResultSet rsDtRef = pstmtDtRef.executeQuery();
			if(rsDtRef.next()) {
				String msg = MessageFormat.format(bundle.getString("DotacaoDao.erro.dotacaoxistente"), 
						formulario.getDtReferencia());
				rsDtRef.close();
	      throw new ParametroException(msg, 
	          ICodigosErros.ERRO_SAO_EXECUCAO);  		
			}
			pstmtDtRef.close();
			
			PreparedStatement pstmtIFormExec = con.prepareStatement("INSERT INTO JN_FORMULARIO_DOTACAO "
					+ "(DT_UPLOAD, USUARIO_ID, DT_REFERENCIA) "
					+ "VALUES (?,?,?)", new String[] {"ID"});
			pstmtIFormExec.setDate(1, new java.sql.Date(System.currentTimeMillis()));
			pstmtIFormExec.setLong(2, formulario.getUsuario().id);
			pstmtIFormExec.setDate(3, new java.sql.Date(formulario.getDtReferencia().getTime()));
			pstmtIFormExec.executeUpdate();
			
			ResultSet rs = pstmtIFormExec.getGeneratedKeys();
			if(rs.next()) {
				formulario.setId(rs.getLong(1));
				PreparedStatement pstmtExec = con.prepareStatement("INSERT INTO JN_DOTACAO (FORMULARIO_DOTACAOID, \r\n" + 
						"UNIDADE_ORC, UNIDADE_ORC_DESC, \r\n" + 
						"UG_EXECUTIVOS, UG_EXECUTIVOS_DESC, PT, \r\n" + 
						"ACAO_GOVERNO, ACAO_GOVERNO_DESC,\r\n" + 
						"PLANO_ORC, PLANO_ORC_DESC, PTRES, \r\n" + 
						"FONTE_SOF, FONTE_RECURSOS,\r\n" + 
						"GRUPO_DESP_ID, GRUPO_DESP, DOTACAO_INICIAL,\r\n" + 
						"DOTACAO_ATUALIZADA, PROVISAO_RECEBIDA, \r\n" + 
						"PROVISAO_CONCEDIDA, DESTAQUE_RECEBIDO,\r\n" + 
						"DESTAQUE_CONCEDIDO, CREDITO_DISPONIVEL,\r\n" + 
						"CREDITO_INDISPONIVEL, DESP_PRE_EMP_A_EMPENHAR,\r\n" + 
						"DESP_EMPENHADAS, DESP_LIQUIDAS, DESP_INSC_RPNP,\r\n" + 
						"DESP_PAGAS\r\n" + 
						") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				for (Dotacao dotacao : formulario.getDotacoes()) {
					int qidx = 1;
					pstmtExec.setLong(qidx++, formulario.getId());
					pstmtExec.setString(qidx++, dotacao.getUnidadeOrc());
					pstmtExec.setString(qidx++, dotacao.getUnidadeOrcDesc());
					pstmtExec.setString(qidx++, dotacao.getUgExecutivos());
					pstmtExec.setString(qidx++, dotacao.getUgExecutivosDesc());
					pstmtExec.setString(qidx++, dotacao.getPt());
					pstmtExec.setString(qidx++, dotacao.getAcaoGoverno());
					pstmtExec.setString(qidx++, dotacao.getAcaoGovernoDesc());
					pstmtExec.setString(qidx++, dotacao.getPlanoOrc());
					pstmtExec.setString(qidx++, dotacao.getPlanoOrcDesc());
					pstmtExec.setString(qidx++, dotacao.getPtres());
					pstmtExec.setString(qidx++, dotacao.getFonteSOF());
					pstmtExec.setString(qidx++, dotacao.getFonteRecursos());
					pstmtExec.setInt(qidx++, dotacao.getGrupoDespesasId());
					pstmtExec.setString(qidx++, dotacao.getGrupoDespesas());
					
					insertNullOrValue(pstmtExec, dotacao.getDotacaoInicial(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getDotacaoAtualizada(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getProvisaoRecebida(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getProvisaoConcedida(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getDestaqueRecebido(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getDestaqueConcedido(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getCreditoDisponivel(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getCreditoIndisponivel(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getDespPreEmpenhadaAEmpenhar(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getDespEmpenhadas(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getDespLiquidas(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getDespInscRpnp(), qidx++);
					insertNullOrValue(pstmtExec, dotacao.getDespPagas(), qidx++);

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
			logger.error("Erro ao executar comando de banco (inserirFormularioDotacao)", e);
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
	
	/**
	 * 
	 * @param dtReferencia
	 * @return
	 */
	public FormularioDotacao getFormularioDotacaoPorDtReferencia(Date dtReferencia) {
		if (dtReferencia == null) {
  		String msg = MessageFormat.format(bundle.getString("ExecucaoDao.erro.parametrosinvalidos"), 
  				"é necessário informar a data de referência");
      throw new ParametroException(msg, 
          ICodigosErros.ERRO_SAO_EXECUCAO);			
		}
		
		FormularioDotacao formulario = new FormularioDotacao();
		formulario.setDtReferencia(dtReferencia);
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtQForm = con.prepareStatement("SELECT * FROM JN_FORMULARIO_DOTACAO "
					+ "WHERE DT_REFERENCIA = ?");
			PreparedStatement pstmtQExec = con.prepareStatement("SELECT * FROM JN_DOTACAO WHERE FORMULARIO_DOTACAOID = ?");
			pstmtQForm.setDate(1, new java.sql.Date(formulario.getDtReferencia().getTime()));
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
					Dotacao exec = povoarDotacao(rsQExec);					
					formulario.getDotacoes().add(exec);
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
	

	/**
	 * 
	 * @param id
	 * @return
	 */
	public FormularioDotacao getFormularioDotacaoPorId(Long id) {
		if (id == null) {
  		String msg = MessageFormat.format(bundle.getString("ExecucaoDao.erro.parametrosinvalidos"), 
  				"é necessário informar o identificador");
      throw new ParametroException(msg, 
          ICodigosErros.ERRO_SAO_EXECUCAO);			
		}
		FormularioDotacao formulario = new FormularioDotacao();
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtQForm = con.prepareStatement("SELECT * FROM JN_FORMULARIO_DOTACAO "
					+ "WHERE ID = ?");
			PreparedStatement pstmtQExec = con.prepareStatement("SELECT * FROM JN_DOTACAO WHERE FORMULARIO_DOTACAOID = ?");
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
					Dotacao exec = povoarDotacao(rsQExec);					
					formulario.getDotacoes().add(exec);
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
	
	/**
	 * 
	 * @return
	 */
	public List<FormularioDotacao> getFormulariosDotacao() {
		List<FormularioDotacao> formularios = new ArrayList<FormularioDotacao>();
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtFE = con.prepareStatement("SELECT * FROM JN_FORMULARIO_DOTACAO ORDER BY DT_REFERENCIA");
			PreparedStatement pstmtE = con.prepareStatement("SELECT  * FROM JN_DOTACAO "
					+ " WHERE FORMULARIO_DOTACAOID = ? ORDER BY ID");
			ResultSet rs = pstmtFE.executeQuery();
			while(rs.next()) {
				FormularioDotacao form = new FormularioDotacao();
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
					Dotacao exec = povoarDotacao(rsExec);
					form.getDotacoes().add(exec);
				}
				formularios.add(form);
				rsExec.close();
			}
			pstmtFE.close();
			pstmtE.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (getFormulariosDoatacao)", e);
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
	
	/**
	 * 
	 * @param id
	 */
	public void apagarFormularioDotacao(Long id) {
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtDelFormExec = con.prepareStatement("DELETE FROM JN_FORMULARIO_DOTACAO WHERE ID = ?");
			PreparedStatement pstmtDelExec = con.prepareStatement("DELETE FROM JN_DOTACAO WHERE FORMULARIO_DOTACAOID = ?");
			pstmtDelExec.setLong(1, id);
			pstmtDelExec.executeUpdate();
			pstmtDelFormExec.setLong(1, id);
			pstmtDelFormExec.executeUpdate();
			pstmtDelExec.close();
			pstmtDelFormExec.close();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (apagarFormularioDotacao)", e);
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	private Dotacao povoarDotacao(ResultSet rsQExec) throws SQLException {
		Dotacao exec = new Dotacao();
		exec.setId(rsQExec.getLong("ID"));
		exec.setFormularioDotacaoId(rsQExec.getLong("FORMULARIO_DOTACAOID"));
		exec.setUnidadeOrc(rsQExec.getString("UNIDADE_ORC"));
		exec.setUnidadeOrcDesc(rsQExec.getString("UNIDADE_ORC_DESC"));
		exec.setUgExecutivos(rsQExec.getString("UG_EXECUTIVOS"));
		exec.setUgExecutivosDesc(rsQExec.getString("UG_EXECUTIVOS_DESC"));
		exec.setPt(rsQExec.getString("PT"));
		exec.setAcaoGoverno(rsQExec.getString("ACAO_GOVERNO"));
		exec.setAcaoGovernoDesc(rsQExec.getString("ACAO_GOVERNO_DESC"));
		exec.setPlanoOrc(rsQExec.getString("PLANO_ORC"));
		exec.setPlanoOrcDesc(rsQExec.getString("PLANO_ORC_DESC"));
		exec.setPtres(rsQExec.getString("PTRES"));
		exec.setFonteSOF(rsQExec.getString("FONTE_SOF"));
		exec.setFonteRecursos(rsQExec.getString("FONTE_RECURSOS"));
		exec.setGrupoDespesasId(rsQExec.getInt("GRUPO_DESP_ID"));
		exec.setGrupoDespesas(rsQExec.getString("GRUPO_DESP"));

		exec.setDotacaoInicial(rsQExec.getDouble("DOTACAO_INICIAL"));
		exec.setDotacaoAtualizada(rsQExec.getDouble("DOTACAO_ATUALIZADA"));
		exec.setProvisaoRecebida(rsQExec.getDouble("PROVISAO_RECEBIDA"));
		exec.setProvisaoConcedida(rsQExec.getDouble("PROVISAO_CONCEDIDA"));
		exec.setDestaqueRecebido(rsQExec.getDouble("DESTAQUE_RECEBIDO"));
		exec.setDestaqueConcedido(rsQExec.getDouble("DESTAQUE_CONCEDIDO"));
		exec.setCreditoDisponivel(rsQExec.getDouble("CREDITO_DISPONIVEL"));
		exec.setCreditoIndisponivel(rsQExec.getDouble("CREDITO_INDISPONIVEL"));
		exec.setDespPreEmpenhadaAEmpenhar(rsQExec.getDouble("DESP_PRE_EMP_A_EMPENHAR"));
		exec.setDespEmpenhadas(rsQExec.getDouble("DESP_EMPENHADAS"));
		exec.setDespLiquidas(rsQExec.getDouble("DESP_LIQUIDAS"));
		exec.setDespInscRpnp(rsQExec.getDouble("DESP_INSC_RPNP"));
		exec.setDespPagas(rsQExec.getDouble("DESP_PAGAS"));
		return exec;
	}
}
