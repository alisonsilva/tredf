package br.jus.tredf.justicanumeros.controller.observacao;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.jus.tredf.justicanumeros.model.Cartorio;
import br.jus.tredf.justicanumeros.model.Indicador;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.justificativa.GrupoIndicador;
import br.jus.tredf.justicanumeros.model.justificativa.Observacao;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.observacao.ObservacaoProdutividadeService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

@RestController
@RequestMapping("/services/formulariostredf/observacao")
public class ObservacaoProdutividadeController {
  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private ResourceBundle bundle;

  @Autowired
  private ObservacaoProdutividadeService observacaoService;
  
  
  @RequestMapping(value = "/produtividadesComObservacao/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ObservacaoProdOut> getRegistrosProdutividadeComObservacao(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<ObservacaoProdOut> ret = new ResponseEntity<ObservacaoProdOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      ObservacaoProdIn prodObs = gson.fromJson(reader, ObservacaoProdIn.class);
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      Date dtRef = sdf.parse(prodObs.observacao.getDtReferenciaStr());
      int grauIndicador = prodObs.observacao.getCartorio().getGrauIndicador();
      List<Observacao> observacoes = observacaoService.getRegistrosProdutividadeComObservacao(
          dtRef, 
          prodObs.observacao.getGrupoObservacao(),
          prodObs.observacao.getCartorio().getSigla(),
          grauIndicador,
          prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      ObservacaoProdOut retfor = new ObservacaoProdOut();
      retfor.observacoes = observacoes;
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      ret = new ResponseEntity<ObservacaoProdOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      ObservacaoProdOut retf = new ObservacaoProdOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<ObservacaoProdOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ObservacaoProdOut retf = new ObservacaoProdOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<ObservacaoProdOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }

  @RequestMapping(value = "/observacaoPorId/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ObservacaoProdOut> observacaoPorId(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<ObservacaoProdOut> ret = new ResponseEntity<ObservacaoProdOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      ObservacaoProdIn prodObs = gson.fromJson(reader, ObservacaoProdIn.class);
      Observacao observacao = observacaoService.getObservacaoPorId(
          prodObs.observacao, prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      ObservacaoProdOut retfor = new ObservacaoProdOut();
      retfor.observacao = observacao;
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      ret = new ResponseEntity<ObservacaoProdOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      ObservacaoProdOut retf = new ObservacaoProdOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<ObservacaoProdOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ObservacaoProdOut retf = new ObservacaoProdOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<ObservacaoProdOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  @RequestMapping(value = "/observacaoPorLKey/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ObservacaoProdOut> observacaoPorLKey(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<ObservacaoProdOut> ret = new ResponseEntity<ObservacaoProdOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      ObservacaoProdIn prodObs = gson.fromJson(reader, ObservacaoProdIn.class);
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      prodObs.observacao.setDtReferencia(sdf.parse(prodObs.observacao.getDtReferenciaStr()));
      Observacao observacao = observacaoService.getObservacaoPorLKey(
          prodObs.observacao, prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      ObservacaoProdOut retfor = new ObservacaoProdOut();
      retfor.observacao = observacao;
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      ret = new ResponseEntity<ObservacaoProdOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      ObservacaoProdOut retf = new ObservacaoProdOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<ObservacaoProdOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ObservacaoProdOut retf = new ObservacaoProdOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<ObservacaoProdOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  @RequestMapping(value = "/addObservacao/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ObservacaoProdOut> addObservacao(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<ObservacaoProdOut> ret = new ResponseEntity<ObservacaoProdOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      ObservacaoProdIn prodObs = gson.fromJson(reader, ObservacaoProdIn.class);
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      prodObs.observacao.setDtReferencia(sdf.parse(prodObs.observacao.getDtReferenciaStr()));
      observacaoService.addObservacao(prodObs.observacao, prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      ObservacaoProdOut retfor = new ObservacaoProdOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      ret = new ResponseEntity<ObservacaoProdOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      ObservacaoProdOut retf = new ObservacaoProdOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<ObservacaoProdOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ObservacaoProdOut retf = new ObservacaoProdOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<ObservacaoProdOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  @RequestMapping(value = "/removeObservacao/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ObservacaoProdOut> removeObservacao(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<ObservacaoProdOut> ret = new ResponseEntity<ObservacaoProdOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      ObservacaoProdIn prodObs = gson.fromJson(reader, ObservacaoProdIn.class);
      observacaoService.apagarObservacao(prodObs.observacao, prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      ObservacaoProdOut retfor = new ObservacaoProdOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      ret = new ResponseEntity<ObservacaoProdOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      ObservacaoProdOut retf = new ObservacaoProdOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<ObservacaoProdOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ObservacaoProdOut retf = new ObservacaoProdOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<ObservacaoProdOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }   
  
  @RequestMapping(value = "/getCartorios", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Cartorio>> getCartorios() {
    List<Cartorio> areasAtuacao = null;
    ResponseEntity<List<Cartorio>> ret = 
        new ResponseEntity<List<Cartorio>>(HttpStatus.OK);
    try {
      areasAtuacao = observacaoService.getCartorios();
      if(areasAtuacao != null) {
        ret = new ResponseEntity<List<Cartorio>>(areasAtuacao, HttpStatus.OK);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<List<Cartorio>>(responseHeaders, HttpStatus.OK);
    }
    return ret;
  }    
  
  @RequestMapping(value = "/getGruposIndicadores", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GrupoIndicador>> getGrupoIndicadores() {
    List<GrupoIndicador> areasAtuacao = null;
    ResponseEntity<List<GrupoIndicador>> ret = 
        new ResponseEntity<List<GrupoIndicador>>(HttpStatus.OK);
    try {
      areasAtuacao = observacaoService.getGruposIndicadores();
      if(areasAtuacao != null) {
        ret = new ResponseEntity<List<GrupoIndicador>>(areasAtuacao, HttpStatus.OK);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<List<GrupoIndicador>>(responseHeaders, HttpStatus.OK);
    }
    return ret;
  }    
  
  @RequestMapping(value = "/getIndicadores", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Indicador>> getIndicadores() {
    List<Indicador> areasAtuacao = null;
    ResponseEntity<List<Indicador>> ret = 
        new ResponseEntity<List<Indicador>>(HttpStatus.OK);
    try {
      areasAtuacao = observacaoService.getIndicadores();
      if(areasAtuacao != null) {
        ret = new ResponseEntity<List<Indicador>>(areasAtuacao, HttpStatus.OK);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<List<Indicador>>(responseHeaders, HttpStatus.OK);
    }
    return ret;
  }   
  
  /**
   * Generates a pdf report with the orders of the user
   * @param userId The user identification
   * @return Success or failure message.
   */ 
  @RequestMapping(value = "/report/{competencia}/{cartorio}", 
      method = RequestMethod.GET)
  public ResponseEntity<byte[]> report(
      @PathVariable("competencia") String competencia,
      @PathVariable("cartorio") String cartorio) {
    ResponseEntity<byte[]> ret = 
        new ResponseEntity<byte[]>(HttpStatus.OK);
   try{
      byte[] report = observacaoService.reportOrderIntoPDF(competencia, cartorio);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.add("Content-disposition", "attachment; filename=justica_numeros.pdf");
      headers.setContentLength(report.length);
      ret = new ResponseEntity<byte[]>(report, headers, HttpStatus.OK);
    } catch(ParametroException e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<byte[]>(null, responseHeaders, 
          HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<byte[]>(null, responseHeaders, 
          HttpStatus.BAD_REQUEST);
    }
    return ret;
  }    

  @RequestMapping(value = "/report/porcompetencia/{competencia}/{cartorio}", 
      method = RequestMethod.GET)
  public ResponseEntity<byte[]> reportPorCompetencia(
      @PathVariable("competencia") String competencia,
      @PathVariable("cartorio") Integer cartorio) {
    ResponseEntity<byte[]> ret = 
        new ResponseEntity<byte[]>(HttpStatus.OK);
   try{
      byte[] report = observacaoService.reportOrderIntoPDFPorCompetencia(competencia, cartorio);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.add("Content-disposition", "attachment; filename=justica_numeros.pdf");
      headers.setContentLength(report.length);
      ret = new ResponseEntity<byte[]>(report, headers, HttpStatus.OK);
    } catch(ParametroException e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<byte[]>(null, responseHeaders, 
          HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<byte[]>(null, responseHeaders, 
          HttpStatus.BAD_REQUEST);
    }
    return ret;
  }    

}



class ObservacaoProdIn {
  public String token;
  public String filtro;
  public Observacao observacao;
}

class ObservacaoProdOut {
  public int codigo;
  public String mensagem;
  public String token;
  public Observacao observacao;
  public List<Observacao> observacoes;
}
