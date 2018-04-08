package br.jus.tredf.justicanumeros.controller;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
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
import br.jus.tredf.justicanumeros.model.VisualizacaoCartoriosInfo;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.AdministracaoFormulariosService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


@RestController
@RequestMapping("/services/formulariostredf/admformularios")
public class AdministracaoFormulariosController {

  @Autowired
  private AdministracaoFormulariosService administracaoFormulariosService;
  
  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private ResourceBundle bundle;
  
  
  /**
   * Recupera uma listagem com todos os formulários disponíveis
   * @param req Parâmetros de pesquisa. Deve conter um objeto do tipo UsuarioVO com no 
   * mínimo login e token: {lgn: login_usuario, token: token_usuario}
   * @param ucBuilder
   * @return Listagem com os formulários encontrados
   */
  @RequestMapping(value = "/getAllForms/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> getAllForms(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      UsuarioVO user = gson.fromJson(reader, UsuarioVO.class);
      List<Formulario> formularios = administracaoFormulariosService.getTodosFormularios(user);
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(ucBuilder.path("/user/authuser/{lgn}").buildAndExpand(user.lgn).toUri());
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.formularios = formularios;
      retfor.token = user.token;
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
  @RequestMapping(value = "/formularioFechado/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> isFormularioFechado(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn params = gson.fromJson(reader, AdmFormulariosIn.class);
      boolean formularioFechado = administracaoFormulariosService.isFormularioFechado(params.idFormulario, params.token);
      HttpHeaders headers = new HttpHeaders();
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.codigo = 0;
      retfor.mensagem = "Status do formulário recuperado";
      retfor.fechado = formularioFechado;
      retfor.token = params.token;
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
  
  @RequestMapping(value = "/valorCampo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> valorCampo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn params = gson.fromJson(reader, AdmFormulariosIn.class);
      int valorCampo = administracaoFormulariosService.getValorcampo(params.idCampo);
      HttpHeaders headers = new HttpHeaders();
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.codigo = 0;
      retfor.mensagem = "Valor para o campo recuperado";
      retfor.valorCampo = valorCampo;
      retfor.token = params.token;
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
  @RequestMapping(value = "/alteraStatusFechado/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> alteraStatusFechado(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn params = gson.fromJson(reader, AdmFormulariosIn.class);
      administracaoFormulariosService.alterarFechamentoFormulario(params.dtCompetencia, 
          params.idCartorio, params.fechado, params.token);
      HttpHeaders headers = new HttpHeaders();
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.codigo = 0;
      retfor.mensagem = "Status do formulário alterado";
      retfor.token = params.token;
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
   * Altera um formulário
   * @param req Parâmetros de pesquisação. Deve conter os dados do formulário a ser alterado
   * e um token de validação de permissões
   * @param ucBuilder
   * @return Mensagem de sucesso/erro para a atualização do formulário
   */
  @RequestMapping(value = "/alteraFormulario/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> alteraFormulario(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn form = gson.fromJson(reader, AdmFormulariosIn.class);
      HttpHeaders headers = new HttpHeaders();
      administracaoFormulariosService.alterarFormulario(form.token, form.formulario);
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
  @RequestMapping(value = "/novaProdServForm/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> novaProdServForm(@RequestBody String req, 
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
      Date dtLimite = null;
      if(StringUtils.isNotEmpty(form.dtLimite)) {
        dtLimite = sdf.parse(form.dtLimite);
      }
      ProdutividadeServentias prodServentias =
          administracaoFormulariosService.novaProdutividadeServentias(
              dtCompetencia, form.idCartorio, form.todosCartorios, dtLimite, form.token);
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
      administracaoFormulariosService.alteraValorCampo(form.idCampo, form.valor, form.token);
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
  
  @RequestMapping(value = "/alteraValorDataLimite/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> alteraDataLimite(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn form = gson.fromJson(reader, AdmFormulariosIn.class);
      HttpHeaders headers = new HttpHeaders();
      administracaoFormulariosService.alterarDataLimitePreenchimento(
          form.dtCompetencia, form.idCartorio, form.dtLimite, form.token);
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

  @RequestMapping(value = "/statusCartoriosCompetencia/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmFormulariosOut> statusCartoriosCompetencia(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmFormulariosOut> ret = new ResponseEntity<AdmFormulariosOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmFormulariosIn form = gson.fromJson(reader, AdmFormulariosIn.class);
      HttpHeaders headers = new HttpHeaders();
      List<VisualizacaoCartoriosInfo> statusCartorios = 
          administracaoFormulariosService.relatorioVisualizacaoCartoriosCompetencia(form.dtCompetencia, form.token);
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(form.token);
      AdmFormulariosOut retfor = new AdmFormulariosOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Status dos cartórios recuperados com sucesso";
      retfor.resumoCartorios = statusCartorios;
      
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
      List<ProtocoloProcesso> protocolos = administracaoFormulariosService.protocolosPorCampo(form.idCampo, form.token);
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
      administracaoFormulariosService.alteraProtocolo(form.idProtocolo, form.vlrProtocolo, form.token);
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
      administracaoFormulariosService.removeProtocolo(form.idProtocolo, form.token);
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
      ProtocoloProcesso prot = administracaoFormulariosService.inserirProtocolo(
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

class AdmFormulariosIn {
  public String token;
  public String dtCompetencia;
  public Long idCartorio;
  public boolean todosCartorios;
  public boolean fechado;
  public String dtLimite;
  public Long idFormulario;  
  public Formulario formulario;
  public Long idCampo;
  public Double valor;
  public Long idProtocolo;
  public String vlrProtocolo;
}

class AdmFormulariosOut {
  public int codigo;
  public boolean fechado;
  public String mensagem;
  public String token;
  public List<Formulario> formularios;
  public Formulario formulario;
  public ProdutividadeServentias prodServentias;
  public List<ProtocoloProcesso> protocolos;
  public ProtocoloProcesso protocolo;
  public List<Cartorio> cartorios;
  public List<VisualizacaoCartoriosInfo> resumoCartorios;
  public Cartorio cartorio;
  public int valorCampo;
}
