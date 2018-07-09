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
import br.jus.tredf.justicanumeros.model.sao.FormularioDotacao;
import br.jus.tredf.justicanumeros.model.sao.ArquivoEnviadoSao;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.sao.DotacaoService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

@RestController
@RequestMapping("/services/formulariostredf/sao/dotacao")
public class DotacaoController {

  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private ResourceBundle bundle;
 
  @Autowired
  private DotacaoService dotacaoService;
	

	@RequestMapping(value = "/upload/", 
			method = RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EnvioXmlDotacaoOut> upload(@RequestParam("arquivoDotacao") MultipartFile arquivoExecucao,
			@RequestParam("token") String token,
			@RequestParam("dtReferencia") String dtReferencia,
			ModelMap modelMap) {
    ResponseEntity<EnvioXmlDotacaoOut> ret = new ResponseEntity<EnvioXmlDotacaoOut>(HttpStatus.OK);

		try {
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(token);
			ArquivoEnviadoSao arquivo = new ArquivoEnviadoSao();
			arquivo.setLength(arquivoExecucao.getSize());
			arquivo.setBytes(arquivoExecucao.getBytes());
			arquivo.setType(arquivoExecucao.getContentType());
			arquivo.setName(arquivoExecucao.getOriginalFilename());
			arquivo.setLoginUsuario(usuario.lgn);
			arquivo.setDataReferencia(dtReferencia);
			dotacaoService.enviaArquivoDotacao(arquivo, token);

	    EnvioXmlDotacaoOut retfor = new EnvioXmlDotacaoOut();
	    retfor.token = token;
	    retfor.codigo = 0;
	    retfor.mensagem = bundle.getString("ExecucaoController.arquivoenviado.sucesso");
	    ret = new ResponseEntity<EnvioXmlDotacaoOut>(retfor, new HttpHeaders(), HttpStatus.OK);
		} catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
		return ret;
	}
	
  @RequestMapping(value = "/formulariosDotacao/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlDotacaoOut> formulariosDotacao(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlDotacaoOut> ret = new ResponseEntity<EnvioXmlDotacaoOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlDotacaoIn prodObs = gson.fromJson(reader, EnvioXmlDotacaoIn.class);
      List<FormularioDotacao> formularios = dotacaoService.getFormulariosDotacao(prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlDotacaoOut retfor = new EnvioXmlDotacaoOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulários recuperados com sucesso";
      retfor.formularios = formularios;
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  } 	
	
  @RequestMapping(value = "/formulariosDotacaoPorId/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlDotacaoOut> formulariosDotacaoPorId(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlDotacaoOut> ret = new ResponseEntity<EnvioXmlDotacaoOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlDotacaoIn prodObs = gson.fromJson(reader, EnvioXmlDotacaoIn.class);
      FormularioDotacao formulario = dotacaoService.getFormularioDotacaoPorId(prodObs.token, prodObs.idFormulario);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlDotacaoOut retfor = new EnvioXmlDotacaoOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulário recuperado com sucesso";
      retfor.formulario = formulario;
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  } 	  
  
  @RequestMapping(value = "/formulariosDotacaoPorCompetencia/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlDotacaoOut> formulariosExecucaoPorCompetencia(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlDotacaoOut> ret = new ResponseEntity<EnvioXmlDotacaoOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlDotacaoIn prodObs = gson.fromJson(reader, EnvioXmlDotacaoIn.class);
      FormularioDotacao formulario = 
      		dotacaoService.getFormularioDotacaoPorDtReferencia(prodObs.token, prodObs.competencia);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlDotacaoOut retfor = new EnvioXmlDotacaoOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulário recuperado com sucesso";
      retfor.formulario = formulario;
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }   
  
  @RequestMapping(value = "/testeEnvioArquivo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlDotacaoOut> testeEnvioArquivo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlDotacaoOut> ret = new ResponseEntity<EnvioXmlDotacaoOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlDotacaoIn prodObs = gson.fromJson(reader, EnvioXmlDotacaoIn.class);
      dotacaoService.testeEnvioArquivo(prodObs.nomeArquivo);
      HttpHeaders headers = new HttpHeaders();
      EnvioXmlDotacaoOut retfor = new EnvioXmlDotacaoOut();
      retfor.codigo = 0;
      retfor.mensagem = "Envios recuperados com sucesso";
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  } 	

  
  @RequestMapping(value = "/apagarArquivoDotacao/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlDotacaoOut> apagarArquivoDotacao(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlDotacaoOut> ret = new ResponseEntity<EnvioXmlDotacaoOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlDotacaoIn prodObs = gson.fromJson(reader, EnvioXmlDotacaoIn.class);
      dotacaoService.apagarFormularioDotacao(prodObs.token, prodObs.idFormulario);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlDotacaoOut retfor = new EnvioXmlDotacaoOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulário apagado com sucesso";
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlDotacaoOut retf = new EnvioXmlDotacaoOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlDotacaoOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }   
}

class EnvioXmlDotacaoIn {
  public String token;
  public String competencia;
  public Long idFormulario;
  public String nomeArquivo;
}

class EnvioXmlDotacaoOut {
  public int codigo;
  public String mensagem;
  public String token; 
  public FormularioDotacao formulario;
  public List<FormularioDotacao> formularios;
}


