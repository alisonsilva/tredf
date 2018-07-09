package br.jus.tredf.justicanumeros.service.observacao;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.jus.tredf.justicanumeros.dao.CartorioDao;
import br.jus.tredf.justicanumeros.dao.ICodigosAcoes;
import br.jus.tredf.justicanumeros.dao.SadpDao;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.dao.justificativa.JustificativaDao;
import br.jus.tredf.justicanumeros.model.Cartorio;
import br.jus.tredf.justicanumeros.model.Indicador;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.justificativa.GrupoIndicador;
import br.jus.tredf.justicanumeros.model.justificativa.Observacao;
import br.jus.tredf.justicanumeros.service.FormulariosTREDFService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Service(value="ObservacaoProdutividadeService")
public class ObservacaoProdutividadeService extends FormulariosTREDFService {
	private static final int GRAU_INDICADOR_PRIMEIRA_INSTANCIA = 1;
	
  @Autowired
  private ResourceBundle bundle;
  
  @Autowired
  private PropertiesServiceController properties;

  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private JustificativaDao justificativaDao;
  
  @Autowired
  private CartorioDao cartorioDao;
  
  @Autowired
  private UsuarioDao usuarioDao;
  
  @Autowired 
  private SadpDao sadpDao;  
  
  public List<Observacao> getRegistrosProdutividadeComObservacao(Date dataReferencia, int idGrupoObservacao, 
  		String secao, int grauIndicador, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));    
    validaTokenUsuario(token, permissoes);    
    return justificativaDao.getRegistrosProdutividadeComObservacoes(dataReferencia, secao, grauIndicador, idGrupoObservacao);
  }

  public Observacao getObservacaoPorId(Observacao observacao, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));    
    validaTokenUsuario(token, permissoes);    
    return justificativaDao.getObservacaoById(observacao);
  }

  public Observacao getObservacaoPorLKey(Observacao observacao, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));    
    validaTokenUsuario(token, permissoes);    
    return justificativaDao.getObservacaoByLKey(observacao);
  }
  
  public void addObservacao(Observacao observacao, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));    
    validaTokenUsuario(token, permissoes);
    Cartorio cartorio = cartorioDao.getCartorioPorId(observacao.getCartorio().getId());
    if(cartorio.getGrauIndicador() == GRAU_INDICADOR_PRIMEIRA_INSTANCIA && 
    		!sadpDao.isProtocoloValidoSadp(Long.valueOf(observacao.getProtocolo()))) {
      throw new ParametroException(
          bundle.getString("ProdutividadeServentiaService.validacaonumeroprotocolo"), 
          ICodigosErros.ERRO_SADP_PROTOCOLOINVALIDO);
    }
    
    
    if(observacao.getId() > 0 ) {// observação existe
      justificativaDao.alterarObservacao(observacao);
      String logMessage = 
          MessageFormat.format(
              bundle.getString("ObservacaoProdutividadeService.alterarObservacao.sucesso.mensagemLog"), 
              observacao.toString());
      registraLogAcoes(logMessage, ICodigosAcoes.ACAO_OBSERVACAO_ALTERACAO, token);   
    } else {
      observacao = justificativaDao.addNewObservation(observacao);
      String logMessage = 
          MessageFormat.format(
              bundle.getString("ObservacaoProdutividadeService.novaObservacao.sucesso.mensagemLog"), 
              observacao.toString());
      registraLogAcoes(logMessage, ICodigosAcoes.ACAO_OBSERVACAO_INSERCAO, token);   
    }

  }
  
  public byte[] reportOrderIntoPDF(String competencia, String idCartorio) { 
    if(StringUtils.isEmpty(idCartorio)) {
      throw new ParametroException("Cart�rio inv�lido", ICodigosErros.ERRO_SERVENTIAS_PARAMETROSINVALIDOS);
    }
    Cartorio cartorio = cartorioDao.getCartorioPorId(Long.valueOf(idCartorio));
    return justificativaDao.reportOrderPorCartorioIntoPDF(competencia, cartorio.getSigla());
  }

  public byte[] reportOrderIntoPDFPorCompetencia(String competencia, Integer idCartorio) {
    byte[] ret = null;
    if(idCartorio == 0) {
      ret = justificativaDao.reportOrderPorCompetenciaIntoPDF(competencia);
    } else {
      Cartorio cartorio = cartorioDao.getCartorioPorId(Long.valueOf(idCartorio));
      ret = justificativaDao.reportOrderPorCartorioIntoPDF(competencia, cartorio.getSigla());
    }
    return ret;
  }  
  
  public void apagarObservacao(Observacao observacao, String token) {
    List<Permissao> permissoes = addNovaPermissaoList(null, 
        properties.getProperty("perm.formulario.alterar.campo.admlocal.usuario"));    
    validaTokenUsuario(token, permissoes);
    observacao = this.getObservacaoPorId(observacao, token);
    justificativaDao.apagarObservacao(observacao);
    String logMessage = 
        MessageFormat.format(
            bundle.getString("ObservacaoProdutividadeService.removerObservacao.sucesso.mensagemLog"), 
            observacao.toString());
    registraLogAcoes(logMessage, ICodigosAcoes.ACAO_OBSERVACAO_REMOCAO, token);   
  }
  
  public List<Cartorio> getCartorios() {
    return cartorioDao.getCartorios();
  }
  
  public List<GrupoIndicador> getGruposIndicadores() {
  	return justificativaDao.getGruposIndicador();
  }
  
  public List<Indicador> getIndicadores() {
    return justificativaDao.getIndicadores();
  }
}
