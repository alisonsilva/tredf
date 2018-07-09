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
import br.jus.tredf.justicanumeros.model.sao.Execucao;
import br.jus.tredf.justicanumeros.model.sao.FormularioExecucao;
import br.jus.tredf.justicanumeros.model.sao.IArquivo;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

@Repository("ExecucaoDao")
public class ExecucaoDao {

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
  public FormularioExecucao inserirFormularioExecucao(FormularioExecucao formulario) {
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

    	PreparedStatement pstmtDtRef = con.prepareStatement("SELECT * FROM JN_FORMULARIO_EXECUCAO "
					+ "WHERE DT_REFERENCIA = ?");
			pstmtDtRef.setDate(1, new java.sql.Date(formulario.getDtReferencia().getTime()));
			ResultSet rsDtRef = pstmtDtRef.executeQuery();
			if(rsDtRef.next()) {
				String msg = MessageFormat.format(bundle.getString("ExecucaoDao.erro.execucaoexistente"), 
						formulario.getDtReferencia());
				rsDtRef.close();
	      throw new ParametroException(msg, 
	          ICodigosErros.ERRO_SAO_EXECUCAO);  		
			}
			pstmtDtRef.close();
			
			PreparedStatement pstmtIFormExec = con.prepareStatement("INSERT INTO JN_FORMULARIO_EXECUCAO "
					+ "(DT_UPLOAD, USUARIO_ID, DT_REFERENCIA) "
					+ "VALUES (?,?,?)", new String[] {"ID"});
			pstmtIFormExec.setDate(1, new java.sql.Date(System.currentTimeMillis()));
			pstmtIFormExec.setLong(2, formulario.getUsuario().id);
			pstmtIFormExec.setDate(3, new java.sql.Date(formulario.getDtReferencia().getTime()));
			pstmtIFormExec.executeUpdate();
			
			ResultSet rs = pstmtIFormExec.getGeneratedKeys();
			if(rs.next()) {
				formulario.setId(rs.getLong(1));
				PreparedStatement pstmtExec = con.prepareStatement("INSERT INTO JN_EXECUCAO (FORMULARIO_EXECUCAOID,\r\n" + 
				    "           UNIDADE_ORC, UNIDADE_ORC_DESC, UG_EXECUTIVOS, UG_EXECUTIVOS_DESC, " +
						"						PT, ACAO_GOVERNO, ACAO_GOVERNO_DESC, PLANO_ORC, PLANO_ORC_DESC, PTRES, CAT_ECO_DESP_ID, "	+ 
				    "           CAT_ECO_DESP, GRUPO_DESP_ID, GRUPO_DESP, \r\n" + 
						"						FONTE_SOF, PI, PI_DESC, FONTE_RECURSOS, ESFERA_ORC, ESFERA_ORC_DESC, ELEMENTO_DESPESA_ID, ELEMENTO_DESPESA, NATUREZA_DESPESA_ID, NATUREZA_DESPESA, \r\n" + 
						"						NATUREZA_DESP_DET_ID,NATUREZA_DESP_DET, TIPO_NE_CCOR_ID,\r\n" + 
						"						TIPO_NE_CCOR, MOD_LICITACAO_NE_CCOR_ID, MOD_LICITACAO_NE_CCOR, DIA_EMISSAO_NE_CCOR, NOTA_EMPENHO_CCOR,\r\n" + 
						"						NUM_PROCESSO_NE_CCOR, DOC_OBSERVACAO, FAVORECIDO_NE_CCOR, DESPESAS_EMPENHADAS, DESPESAS_LIQUIDADAS, DESPESAS_INSC_RPNP, \r\n" + 
						"						DESPESAS_PAGAS, RESTOS_PAGAR_PROC_REINSC, RESTOS_PAGAR_PROC_PAGAR, \r\n" + 
						"						RESTOS_PAGAR_NAO_PRC_INSC, RESTOS_PAGAR_NAO_PRC_REINS, RESTOS_PAGAR_NAO_PRC_CAN,\r\n" + 
						"						RESTOS_PAGAR_NAO_PRC_LIQ, RESTOS_PAGAR_NAO_PRC_PG, RESTOS_PAGAR_NAO_PRC_A_PG, "	+ 
						"           RESTOS_PAGAR_PROC_CAN, RESTOS_PAGAR_PROC_PAGOS, RESTOS_PAGAR_PROC_INSC, "
						+ "         DOTACAO_INICIAL, DOTACAO_ATUALIZADA, PROVISAO_RECEBIDA, PROVISAO_CONCEDIDA, DESTAQUE_RECEBIDO, "
						+ "         DESTAQUE_CONCEDIDO, CREDITO_DISPONIVEL, CREDITO_INDISPONIVEL, DESP_PRE_EMP_A_EMPENHAR, "
						+ "         LIQUIDACOES_TOTAIS, PAGAMENTOS_TOTAIS) "
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				for (IArquivo arquivo : formulario.getExecucao()) {
					Execucao execucao = (Execucao)arquivo;
					int qidx = 1;
					pstmtExec.setLong(qidx++, formulario.getId());
					pstmtExec.setString(qidx++, execucao.getUnidadeOrc());
					pstmtExec.setString(qidx++, execucao.getUnidadeOrcDesc());
					pstmtExec.setString(qidx++, execucao.getUgExecutivos());
					pstmtExec.setString(qidx++, execucao.getUgExecutivosDesc());
					pstmtExec.setString(qidx++, execucao.getPt());
					pstmtExec.setString(qidx++, execucao.getAcaoGoverno());
					pstmtExec.setString(qidx++, execucao.getAcaoGovernoDesc());
					pstmtExec.setString(qidx++, execucao.getPlanoOrc());
					pstmtExec.setString(qidx++, execucao.getPlanoOrcDesc());
					pstmtExec.setString(qidx++, execucao.getPtres());
					pstmtExec.setInt(qidx++, execucao.getCategoriaEconomicaDespesaId());
					pstmtExec.setString(qidx++, execucao.getCategoriaEconomicaDespesa());
					pstmtExec.setInt(qidx++, execucao.getGrupoDespesasId());
					pstmtExec.setString(qidx++, execucao.getGrupoDespesas());
					pstmtExec.setString(qidx++, execucao.getFonteSOF());
					pstmtExec.setString(qidx++, execucao.getPi());
					pstmtExec.setString(qidx++, execucao.getPiDesc());
					pstmtExec.setString(qidx++, execucao.getFonteRecursos());
					pstmtExec.setString(qidx++, execucao.getEsferaOrc());
					pstmtExec.setString(qidx++, execucao.getEsferaOrcDesc());
					pstmtExec.setString(qidx++, execucao.getElementoDespesaId());
					pstmtExec.setString(qidx++, execucao.getElementoDespesa());
					pstmtExec.setString(qidx++, execucao.getNaturezaDespesaId());
					pstmtExec.setString(qidx++, execucao.getNaturezaDespesa());
					pstmtExec.setString(qidx++, execucao.getNaturezaDespesaDetalhadaId());
					pstmtExec.setString(qidx++, execucao.getNaturezaDespesaDetalhada());
					pstmtExec.setString(qidx++, execucao.getTipoNeCCorId());
					pstmtExec.setString(qidx++, execucao.getTipoNeCCor());
					pstmtExec.setString(qidx++, execucao.getModalidadeLicitacaoNeCcorId());
					pstmtExec.setString(qidx++, execucao.getModalidadeLicitacaoNeCcor());
					pstmtExec.setString(qidx++, execucao.getDiaEmissaoNeCcor());
					pstmtExec.setString(qidx++, execucao.getNotaEmpenhoCcor());
					pstmtExec.setString(qidx++, execucao.getNumeroProcessoNeCcor());
					pstmtExec.setString(qidx++, execucao.getDocObservacao());
					pstmtExec.setString(qidx++, execucao.getFavorecidoNeCcor());
					
					insertNullOrValue(pstmtExec, execucao.getDespesasEmpenhadas(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getDespesasLiquidadas(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getDespesasInscRpnp(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getDespesasPagas(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarProcReinsc(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarProcPagar(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarNaoProcessadosInscritos(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarNaoProcessadosReinscritos(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarNaoProcessadosCancelados(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarNaoProcessadosLiquitados(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarNaoProcessadosPagos(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarNaoProcessadosAPagar(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarProcCancelados(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarProcPagos(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getRestosPagarProcInscritos(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getDotacaoInicial(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getDotacaoAtualizada(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getProvisaoRecebida(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getProvisaoConcedida(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getDestaqueRecebido(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getDestaqueConcedido(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getCreditoDisponivel(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getCreditoIndisponivel(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getDespPreEmpAEmpenhar(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getLiquidacoesTotais(), qidx++);
					insertNullOrValue(pstmtExec, execucao.getPagamentosTotais(), qidx++);

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
	
	/**
	 * 
	 * @param dtReferencia
	 * @return
	 */
	public FormularioExecucao getFormularioExecucaoPorDtReferencia(Date dtReferencia) {
		if (dtReferencia == null) {
  		String msg = MessageFormat.format(bundle.getString("ExecucaoDao.erro.parametrosinvalidos"), 
  				"é necessário informar a data de referência");
      throw new ParametroException(msg, 
          ICodigosErros.ERRO_SAO_EXECUCAO);			
		}
		
		FormularioExecucao formulario = new FormularioExecucao();
		formulario.setDtReferencia(dtReferencia);
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtQForm = con.prepareStatement("SELECT * FROM JN_FORMULARIO_EXECUCAO "
					+ "WHERE DT_REFERENCIA = ?");
			PreparedStatement pstmtQExec = con.prepareStatement("SELECT * FROM JN_EXECUCAO WHERE FORMULARIO_EXECUCAOID = ?");
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
					Execucao exec = povoarExecucao(rsQExec);					
					formulario.getExecucao().add(exec);
				}
				rsQExec.close();
			}
			rsQForm.close();
			pstmtQForm.close();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (getFormularioExecucaoPorDtReferencia)", e);
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
	public FormularioExecucao getFormularioExecucaoPorId(Long id) {
		if (id == null) {
  		String msg = MessageFormat.format(bundle.getString("ExecucaoDao.erro.parametrosinvalidos"), 
  				"é necessário informar o identificador");
      throw new ParametroException(msg, 
          ICodigosErros.ERRO_SAO_EXECUCAO);			
		}
		FormularioExecucao formulario = new FormularioExecucao();
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtQForm = con.prepareStatement("SELECT * FROM JN_FORMULARIO_EXECUCAO "
					+ "WHERE ID = ?");
			PreparedStatement pstmtQExec = con.prepareStatement("SELECT * FROM JN_EXECUCAO WHERE FORMULARIO_EXECUCAOID = ?");
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
					Execucao exec = povoarExecucao(rsQExec);					
					formulario.getExecucao().add(exec);
				}
				rsQExec.close();
			} 
			rsQForm.close();
			pstmtQForm.close();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (getFormularioExecucaoPorDtReferencia)", e);
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
	public List<FormularioExecucao> getFormulariosExecucao() {
		List<FormularioExecucao> formularios = new ArrayList<FormularioExecucao>();
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtFE = con.prepareStatement("SELECT * FROM JN_FORMULARIO_EXECUCAO ORDER BY DT_REFERENCIA");
			ResultSet rs = pstmtFE.executeQuery();
			while(rs.next()) {
				FormularioExecucao form = new FormularioExecucao();
				form.setId(rs.getLong("ID"));
				form.setDtUpload(rs.getDate("DT_UPLOAD"));
				form.setDtReferencia(rs.getDate("DT_REFERENCIA"));
				UsuarioVO usuario = new UsuarioVO();
				usuario.id = rs.getLong("USUARIO_ID");
				form.setUsuario(usuario);
				
				formularios.add(form);
			}
			pstmtFE.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (getFormulariosExecucao)", e);
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
	public void apagarFormularioExecucao(Long id) {
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmtDelFormExec = con.prepareStatement("DELETE FROM JN_FORMULARIO_EXECUCAO WHERE ID = ?");
			PreparedStatement pstmtDelExec = con.prepareStatement("DELETE FROM JN_EXECUCAO WHERE FORMULARIO_EXECUCAOID = ?");
			pstmtDelExec.setLong(1, id);
			pstmtDelExec.executeUpdate();
			pstmtDelFormExec.setLong(1, id);
			pstmtDelFormExec.executeUpdate();
			pstmtDelExec.close();
			pstmtDelFormExec.close();
		} catch (SQLException e) {
			logger.error("Erro ao executar comando de banco (apagarFormularioExecucao)", e);
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	private Execucao povoarExecucao(ResultSet rsQExec) throws SQLException {
		Execucao exec = new Execucao();
		exec.setId(rsQExec.getLong("ID"));
		exec.setFormularioExecucaoId(rsQExec.getLong("FORMULARIO_EXECUCAOID"));
		exec.setPt(rsQExec.getString("PT"));
		exec.setAcaoGoverno(rsQExec.getString("ACAO_GOVERNO"));
		exec.setPtres(rsQExec.getString("PTRES"));
		exec.setCategoriaEconomicaDespesaId(rsQExec.getInt("CAT_ECO_DESP_ID"));
		exec.setCategoriaEconomicaDespesa(rsQExec.getString("CAT_ECO_DESP"));
		exec.setGrupoDespesasId(rsQExec.getInt("GRUPO_DESP_ID"));
		exec.setGrupoDespesas(rsQExec.getString("GRUPO_DESP"));
		exec.setFonteSOF(rsQExec.getString("FONTE_SOF"));
		exec.setPi(rsQExec.getString("PI"));
		exec.setElementoDespesaId(rsQExec.getString("ELEMENTO_DESPESA_ID"));
		exec.setElementoDespesa(rsQExec.getString("ELEMENTO_DESPESA"));
		exec.setNaturezaDespesaId(rsQExec.getString("ELEMENTO_DESPESA_ID"));
		exec.setNaturezaDespesa(rsQExec.getString("NATUREZA_DESPESA"));
		exec.setNaturezaDespesaDetalhadaId(rsQExec.getString("NATUREZA_DESP_DET_ID"));
		exec.setNaturezaDespesaDetalhada(rsQExec.getString("NATUREZA_DESP_DET_ID"));
		exec.setTipoNeCCorId(rsQExec.getString("TIPO_NE_CCOR_ID"));
		exec.setTipoNeCCor(rsQExec.getString("TIPO_NE_CCOR"));
		exec.setModalidadeLicitacaoNeCcorId(rsQExec.getString("MOD_LICITACAO_NE_CCOR_ID"));
		exec.setModalidadeLicitacaoNeCcor(rsQExec.getString("MOD_LICITACAO_NE_CCOR"));
		exec.setNotaEmpenhoCcor(rsQExec.getString("NOTA_EMPENHO_CCOR"));
		exec.setNumeroProcessoNeCcor(rsQExec.getString("NUM_PROCESSO_NE_CCOR"));
		exec.setFavorecidoNeCcor(rsQExec.getString("FAVORECIDO_NE_CCOR"));
		//exec.setItemFormacao(rsQExec.getString("ITEM_FORMACAO"));
		exec.setDespesasEmpenhadas(rsQExec.getDouble("DESPESAS_EMPENHADAS"));
		exec.setDespesasLiquidadas(rsQExec.getDouble("DESPESAS_LIQUIDADAS"));
		exec.setDespesasPagas(rsQExec.getDouble("DESPESAS_PAGAS"));
		exec.setRestosPagarProcReinsc(rsQExec.getDouble("RESTOS_PAGAR_PROC_REINSC"));
		exec.setRestosPagarProcPagar(rsQExec.getDouble("RESTOS_PAGAR_PROC_PAGAR"));
		exec.setRestosPagarNaoProcessadosInscritos(rsQExec.getDouble("RESTOS_PAGAR_NAO_PRC_INSC"));
		exec.setRestosPagarNaoProcessadosReinscritos(rsQExec.getDouble("RESTOS_PAGAR_NAO_PRC_REINS"));
		exec.setRestosPagarNaoProcessadosCancelados(rsQExec.getDouble("RESTOS_PAGAR_NAO_PRC_CAN"));
		exec.setRestosPagarNaoProcessadosLiquitados(rsQExec.getDouble("RESTOS_PAGAR_NAO_PRC_LIQ"));
		exec.setRestosPagarNaoProcessadosPagos(rsQExec.getDouble("RESTOS_PAGAR_NAO_PRC_PG"));
		exec.setRestosPagarNaoProcessadosAPagar(rsQExec.getDouble("RESTOS_PAGAR_NAO_PRC_A_PG"));
		return exec;
	}
}
