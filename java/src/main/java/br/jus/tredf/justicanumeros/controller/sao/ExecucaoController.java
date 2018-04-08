package br.jus.tredf.justicanumeros.controller.sao;

import java.io.StringReader;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.sao.FormularioExecucao;
import br.jus.tredf.justicanumeros.model.sao.SaoArquivoExecucao;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.sao.ExecucaoService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

@RestController
@RequestMapping("/services/formulariostredf/sao/execucao")
public class ExecucaoController {

  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private ResourceBundle bundle;
 
  @Autowired
  private ExecucaoService execucaoService;
	

	@RequestMapping(value = "/upload/", 
			method = RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EnvioXmlOut> upload(@RequestParam("arquivoExecucao") MultipartFile arquivoExecucao,
			@RequestParam("token") String token,
			@RequestParam("dtReferencia") String dtReferencia,
			ModelMap modelMap) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

		try {
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(token);
			SaoArquivoExecucao arquivo = new SaoArquivoExecucao();
			arquivo.setLength(arquivoExecucao.getSize());
			arquivo.setBytes(arquivoExecucao.getBytes());
			arquivo.setType(arquivoExecucao.getContentType());
			arquivo.setName(arquivoExecucao.getOriginalFilename());
			arquivo.setLoginUsuario(usuario.lgn);
			arquivo.setDataReferencia(dtReferencia);
			execucaoService.enviaArquivoExecucao(arquivo, token);

	    EnvioXmlOut retfor = new EnvioXmlOut();
	    retfor.token = token;
	    retfor.codigo = 0;
	    retfor.mensagem = bundle.getString("ExecucaoController.arquivoenviado.sucesso");
	    ret = new ResponseEntity<EnvioXmlOut>(retfor, new HttpHeaders(), HttpStatus.OK);
		} catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
		return ret;
	}
	
  @RequestMapping(value = "/formulariosExecucao/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlOut> formulariosExecucao(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
      List<FormularioExecucao> formularios = execucaoService.getFormulariosExecucao(prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlOut retfor = new EnvioXmlOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulários recuperados com sucesso";
      retfor.formularios = formularios;
      ret = new ResponseEntity<EnvioXmlOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  } 	
	
  @RequestMapping(value = "/formulariosExecucaoPorId/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlOut> formulariosExecucaoPorId(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
      FormularioExecucao formulario = execucaoService.getFormularioExecucaoPorId(prodObs.token, prodObs.idFormulario);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlOut retfor = new EnvioXmlOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulário recuperado com sucesso";
      retfor.formulario = formulario;
      ret = new ResponseEntity<EnvioXmlOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  } 	  
  
  @RequestMapping(value = "/formulariosExecucaoPorCompetencia/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlOut> formulariosExecucaoPorCompetencia(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
      FormularioExecucao formulario = 
      		execucaoService.getFormularioExecucaoPorDtReferencia(prodObs.token, prodObs.competencia);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlOut retfor = new EnvioXmlOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulário recuperado com sucesso";
      retfor.formulario = formulario;
      ret = new ResponseEntity<EnvioXmlOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }   
  
  @RequestMapping(value = "/testeEnvioArquivo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlOut> testeEnvioArquivo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
      execucaoService.testeEnvioArquivo(prodObs.nomeArquivo);
      HttpHeaders headers = new HttpHeaders();
      EnvioXmlOut retfor = new EnvioXmlOut();
      retfor.codigo = 0;
      retfor.mensagem = "Envios recuperados com sucesso";
      ret = new ResponseEntity<EnvioXmlOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  } 	

  
  @RequestMapping(value = "/apagarArquivoExecucao/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlOut> apagarArquivoExecucao(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
      execucaoService.apagarFormularioExecucao(prodObs.token, prodObs.idFormulario);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlOut retfor = new EnvioXmlOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulário apagado com sucesso";
      ret = new ResponseEntity<EnvioXmlOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlOut retf = new EnvioXmlOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }   
}


class EnvioXmlIn {
  public String token;
  public String competencia;
  public Long idFormulario;
  public String nomeArquivo;
}

class EnvioXmlOut {
  public int codigo;
  public String mensagem;
  public String token; 
  public FormularioExecucao formulario;
  public List<FormularioExecucao> formularios;
}
