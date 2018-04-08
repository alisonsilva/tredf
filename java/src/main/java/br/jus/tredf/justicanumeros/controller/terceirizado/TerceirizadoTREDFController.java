package br.jus.tredf.justicanumeros.controller.terceirizado;

import java.io.StringReader;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.terceirizado.AreaAtuacao;
import br.jus.tredf.justicanumeros.model.terceirizado.GrauInstrucao;
import br.jus.tredf.justicanumeros.model.terceirizado.LotacaoTerceirizado;
import br.jus.tredf.justicanumeros.model.terceirizado.Terceirizado;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.AdministracaoFormulariosService;
import br.jus.tredf.justicanumeros.service.terceirizado.TerceirizadoTREDFService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

@RestController
@RequestMapping("/services/formulariostredf/terceirizados")
public class TerceirizadoTREDFController {
  @Autowired
  private AdministracaoFormulariosService administracaoFormulariosService;
  
  @Autowired
  private TerceirizadoTREDFService terceirizadoService;
  
  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private ResourceBundle bundle;
  

  @RequestMapping(value = "/getTodosTerceirizados/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TerceirizadoTREDFOut> getFormById(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<TerceirizadoTREDFOut> ret = new ResponseEntity<TerceirizadoTREDFOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      TerceirizadoTREDFIn user = gson.fromJson(reader, TerceirizadoTREDFIn.class);
      List<Terceirizado> terceirizados = terceirizadoService.todosTerceirizados(user.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(user.token);
      TerceirizadoTREDFOut retfor = new TerceirizadoTREDFOut();
      retfor.terceirizados = terceirizados;
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      TerceirizadoTREDFOut retf = new TerceirizadoTREDFOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      TerceirizadoTREDFOut retf = new TerceirizadoTREDFOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }

  @RequestMapping(value = "/getTodosTerceirizadosFiltrados/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TerceirizadoTREDFOut> getTerceirizadosFiltrados(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<TerceirizadoTREDFOut> ret = new ResponseEntity<TerceirizadoTREDFOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      TerceirizadoTREDFIn user = gson.fromJson(reader, TerceirizadoTREDFIn.class);
      List<Terceirizado> terceirizados = terceirizadoService.getTerceirizadosFiltrados(user.token, user.filtro);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(user.token);
      TerceirizadoTREDFOut retfor = new TerceirizadoTREDFOut();
      retfor.terceirizados = terceirizados;
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      TerceirizadoTREDFOut retf = new TerceirizadoTREDFOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      TerceirizadoTREDFOut retf = new TerceirizadoTREDFOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  /**
   * 
   * @return
   */
  @RequestMapping(value = "/getTodosGrauInstrucao", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GrauInstrucao>> getTodosGrauInstrucao() {
    List<GrauInstrucao> grausInstrucao = null;
    ResponseEntity<List<GrauInstrucao>> ret = 
        new ResponseEntity<List<GrauInstrucao>>(HttpStatus.OK);
    try {
      grausInstrucao = terceirizadoService.todosGrauInstrucao();
      if(grausInstrucao != null) {
        ret = new ResponseEntity<List<GrauInstrucao>>(grausInstrucao, HttpStatus.OK);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<List<GrauInstrucao>>(responseHeaders, HttpStatus.OK);
    }
    return ret;
  }  
  
  /**
   * 
   * @return
   */
  @RequestMapping(value = "/getTodasAreasAtuacao", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AreaAtuacao>> getTodasAreaAtuacao() {
    List<AreaAtuacao> areasAtuacao = null;
    ResponseEntity<List<AreaAtuacao>> ret = 
        new ResponseEntity<List<AreaAtuacao>>(HttpStatus.OK);
    try {
      areasAtuacao = terceirizadoService.todasAreasAtuacao();
      if(areasAtuacao != null) {
        ret = new ResponseEntity<List<AreaAtuacao>>(areasAtuacao, HttpStatus.OK);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<List<AreaAtuacao>>(responseHeaders, HttpStatus.OK);
    }
    return ret;
  }   

  /**
   * 
   * @return
   */
  @RequestMapping(value = "/getLotacoesTerceirizados", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<LotacaoTerceirizado>> getLotacoesTerceirizados() {
    List<LotacaoTerceirizado> areasAtuacao = null;
    ResponseEntity<List<LotacaoTerceirizado>> ret = 
        new ResponseEntity<List<LotacaoTerceirizado>>(HttpStatus.OK);
    try {
      areasAtuacao = terceirizadoService.todasLotacoesTerceirizados();
      if(areasAtuacao != null) {
        ret = new ResponseEntity<List<LotacaoTerceirizado>>(areasAtuacao, HttpStatus.OK);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<List<LotacaoTerceirizado>>(responseHeaders, HttpStatus.OK);
    }
    return ret;
  } 
  
  @RequestMapping(value = "/inserirTerceirizado/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TerceirizadoTREDFOut> inserirTerceirizado(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<TerceirizadoTREDFOut> ret = new ResponseEntity<TerceirizadoTREDFOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      TerceirizadoTREDFIn terceirizado = gson.fromJson(reader, TerceirizadoTREDFIn.class);
      Terceirizado retTerceirizado = terceirizadoService.insereTerceirizado(terceirizado.terceirizado, terceirizado.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(terceirizado.token);
      TerceirizadoTREDFOut retfor = new TerceirizadoTREDFOut();
      retfor.terceirizado = retTerceirizado;
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.mensagem = "Terceirizado inserido com sucesso";
      retfor.codigo = 0;
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      TerceirizadoTREDFOut retf = new TerceirizadoTREDFOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      TerceirizadoTREDFOut retf = new TerceirizadoTREDFOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  
  @RequestMapping(value = "/alterarTerceirizado/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TerceirizadoTREDFOut> alterarTerceirizado(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<TerceirizadoTREDFOut> ret = new ResponseEntity<TerceirizadoTREDFOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      TerceirizadoTREDFIn terceirizado = gson.fromJson(reader, TerceirizadoTREDFIn.class);
      terceirizadoService.alterarTerceirizado(terceirizado.terceirizado, terceirizado.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(terceirizado.token);
      TerceirizadoTREDFOut retfor = new TerceirizadoTREDFOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.mensagem = "Terceirizado alterado com sucesso";
      retfor.codigo = 0;
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      TerceirizadoTREDFOut retf = new TerceirizadoTREDFOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      TerceirizadoTREDFOut retf = new TerceirizadoTREDFOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  
  
  @RequestMapping(value = "/removerTerceirizado/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TerceirizadoTREDFOut> removerTerceirizado(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<TerceirizadoTREDFOut> ret = new ResponseEntity<TerceirizadoTREDFOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      TerceirizadoTREDFIn terceirizado = gson.fromJson(reader, TerceirizadoTREDFIn.class);
      terceirizadoService.apagarTerceirizado(terceirizado.terceirizado, terceirizado.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(terceirizado.token);
      TerceirizadoTREDFOut retfor = new TerceirizadoTREDFOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.mensagem = "Terceirizado removido com sucesso";
      retfor.codigo = 0;
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      TerceirizadoTREDFOut retf = new TerceirizadoTREDFOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      TerceirizadoTREDFOut retf = new TerceirizadoTREDFOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<TerceirizadoTREDFOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }

}

class TerceirizadoTREDFIn {
  public String token;
  public String filtro;
  public Terceirizado terceirizado;
}

class TerceirizadoTREDFOut {
  public int codigo;
  public String mensagem;
  public String token;
  public Terceirizado terceirizado;
  public List<Terceirizado> terceirizados;
}
