package br.jus.tredf.justicanumeros.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.jus.tredf.justicanumeros.model.Cartorio;
import br.jus.tredf.justicanumeros.model.Formulario;
import br.jus.tredf.justicanumeros.model.ProdutividadeServentias;
import br.jus.tredf.justicanumeros.model.ProtocoloProcesso;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.AdministracaoFormulariosService;
import br.jus.tredf.justicanumeros.service.ProdutividadeServentiasService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

@RestController
@RequestMapping("/services/formulariostredf/produtividadeserventias")
public class ProdutividadeServentiasController {
  @Autowired
  private AdministracaoFormulariosService administracaoFormulariosService;
  
  @Autowired
  private ProdutividadeServentiasService produtividadeServentiasService;
  
  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private ResourceBundle bundle;
  
  /**
   * Recupera dados de um formulário pelo seu ID
   * @param req Parâmetros de pesquisa. Deve conter objeto do tipo ChamadaFormById
   * no formato {token: 'token do usuario', idFormulario: iddoformulario}
   * @param ucBuilder
   * @return Formulário encontrado ou vazio
   */
  @RequestMapping(value = "/getFormById/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> getFormById(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn user = gson.fromJson(reader, AdmFormulariosIn.class);
      Formulario formulario = administracaoFormulariosService.getFormularioById(user.token, user.idFormulario);
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(ucBuilder.path("/user/authuser/{idFormulario}").buildAndExpand(user.idFormulario).toUri());
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(user.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.formulario = formulario;
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      ret = new ResponseEntity<AdmFormulariosOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }

  /**
   * 
   * @param req
   * @param ucBuilder
   * @return
   */
  @RequestMapping(value = "/buscarProdServ/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> buscarProdServ(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn form = gson.fromJson(reader, AdmFormulariosIn.class);
      HttpHeaders headers = new HttpHeaders();
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");      
      Date dtCompetencia = sdf.parse(form.dtCompetencia);
      ProdutividadeServentias prodServentias =
          administracaoFormulariosService.getServentiaPorCompetenciaCartorio(dtCompetencia, form.idCartorio);
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(form.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = bundle.getString("AdministracaoFormulariosController.alteraFormulario.sucesso");
      retfor.prodServentias = prodServentias;
      ret = new ResponseEntity<AdmFormulariosOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      if (ICodigosErros.ERRO_SERVENTIAS_NOVASERVENTIA == pex.getCodigoErro()) {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.FOUND);
      } else {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }

    return ret;
  }   

  @RequestMapping(value = "/alteraValorCampo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> alteraValorCampo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn form = gson.fromJson(reader, AdmFormulariosIn.class);
      HttpHeaders headers = new HttpHeaders();
      produtividadeServentiasService.alteraValorCampo(form.idCampo, form.valor, form.token);
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(form.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = bundle.getString("AdministracaoFormulariosController.alteraFormulario.sucesso");
      ret = new ResponseEntity<AdmFormulariosOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      if (ICodigosErros.ERRO_SERVENTIAS_NOVASERVENTIA == pex.getCodigoErro()) {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.FOUND);
      } else {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }

    return ret;
  }    
  
  @RequestMapping(value = "/protocolosPorCampo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> protocolosPorCampo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn form = gson.fromJson(reader, AdmFormulariosIn.class);
      HttpHeaders headers = new HttpHeaders();
      List<ProtocoloProcesso> protocolos = 
          administracaoFormulariosService.protocolosPorCampo(form.idCampo, form.token);
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(form.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = bundle.getString("AdministracaoFormulariosController.alteraFormulario.sucesso");
      retfor.protocolos = protocolos;
      
      ret = new ResponseEntity<AdmFormulariosOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      if (ICodigosErros.ERRO_SERVENTIAS_NOVASERVENTIA == pex.getCodigoErro()) {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.FOUND);
      } else {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  @RequestMapping(value = "/alterarProtocolo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> alterarProtocolo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn form = gson.fromJson(reader, AdmFormulariosIn.class);
      HttpHeaders headers = new HttpHeaders();
      produtividadeServentiasService.alteraProtocolo(form.idProtocolo, form.vlrProtocolo, form.token);
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(form.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = bundle.getString("AdministracaoFormulariosController.alteraProtocolo.sucesso");
      
      ret = new ResponseEntity<AdmFormulariosOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      if (ICodigosErros.ERRO_SERVENTIAS_NOVASERVENTIA == pex.getCodigoErro()) {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.FOUND);
      } else {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }
  
  @RequestMapping(value = "/removerProtocolo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> removeProtocolo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn form = gson.fromJson(reader, AdmFormulariosIn.class);
      HttpHeaders headers = new HttpHeaders();
      produtividadeServentiasService.removeProtocolo(form.idProtocolo, form.token);
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(form.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = bundle.getString("AdministracaoFormulariosController.removeProtocolo.sucesso");
      
      ret = new ResponseEntity<AdmFormulariosOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      if (ICodigosErros.ERRO_SERVENTIAS_NOVASERVENTIA == pex.getCodigoErro()) {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.FOUND);
      } else {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  
  @RequestMapping(value = "/inserirProtocolo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> inserirProtocolo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn form = gson.fromJson(reader, AdmFormulariosIn.class);
      HttpHeaders headers = new HttpHeaders();
      ProtocoloProcesso prot = produtividadeServentiasService.inserirProtocolo(
          form.vlrProtocolo, form.idCampo, form.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(form.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = bundle.getString("AdministracaoFormulariosController.inserirProtocolo.sucesso");
      retfor.protocolo = prot;      
      ret = new ResponseEntity<AdmFormulariosOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      if (ICodigosErros.ERRO_PROTOCOLO_PROTOCOLODUPLICADO == pex.getCodigoErro()) {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.CONFLICT);
      } else {
        ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }
  

  @RequestMapping(value = "/cartoriosUsuario/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> cartoriosUsuario(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn user = gson.fromJson(reader, AdmFormulariosIn.class);
      List<Cartorio> cartorios = produtividadeServentiasService.cartoriosUsuario(user.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(user.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.cartorios = cartorios;
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Cartórios recuperados com sucesso";
      ret = new ResponseEntity<AdmFormulariosOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  

  @RequestMapping(value = "/fecharProdutividadeServico/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> fecharProdutividadeServico(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn user = gson.fromJson(reader, AdmFormulariosIn.class);
      produtividadeServentiasService.fechaProdutividadeServentias(user.idFormulario, user.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(user.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulário fechado com sucesso";
      ret = new ResponseEntity<AdmFormulariosOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }
  
  @RequestMapping(value = "/preencheProtocolosCompetenciaAnterior/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> preencheProtocolosCompetenciaAnterior(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn user = gson.fromJson(reader, AdmFormulariosIn.class);
      produtividadeServentiasService.preencheProtocolosCompetenciaAnterior(user.idFormulario, user.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(user.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulário preenchido com sucesso";
      ret = new ResponseEntity<AdmFormulariosOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmFormulariosOut retf = new AdmFormulariosOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmFormulariosOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }
  
  /**
   * Chamada que retorna uma listagem com todos os cartórios disponíveis
   * @param publisher
   * @return Listagem com os cartórios encontrados
   */
  @RequestMapping(value = "/getTodosCartorios", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Cartorio>> getCartorios() {
    List<Cartorio> cartorios = null;
    ResponseEntity<List<Cartorio>> ret = 
        new ResponseEntity<List<Cartorio>>(HttpStatus.OK);
    try {
      cartorios = administracaoFormulariosService.getCartorios();
      if(cartorios != null) {
        ret = new ResponseEntity<List<Cartorio>>(cartorios, HttpStatus.OK);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<List<Cartorio>>(responseHeaders, HttpStatus.OK);
    }
    return ret;
  }  
  
}
