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
import br.jus.tredf.justicanumeros.model.sao.ArquivoEnviadoSao;
import br.jus.tredf.justicanumeros.model.sao.FormularioExecucao;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.sao.PropOrcamentariaService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

@RestController
@RequestMapping("/services/formulariostredf/sao/proporcamentaria")
public class PropOrcamentariaController {

  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private ResourceBundle bundle;
 
  @Autowired
  private PropOrcamentariaService propostaOrcamentariaService;
	

	@RequestMapping(value = "/upload/", 
			method = RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EnvioXmlPropOrcamentariaOut> upload(@RequestParam("arquivoPropOrcamentaria") MultipartFile arquivoExecucao,
			@RequestParam("arquivoPleitos") boolean arquivoPleitos,
			@RequestParam("token") String token,
			@RequestParam("dtReferencia") String dtReferencia,
			ModelMap modelMap) {
    ResponseEntity<EnvioXmlPropOrcamentariaOut> ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(HttpStatus.OK);

		try {
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(token);
			ArquivoEnviadoSao arquivo = new ArquivoEnviadoSao();
			arquivo.setLength(arquivoExecucao.getSize());
			arquivo.setBytes(arquivoExecucao.getBytes());
			arquivo.setType(arquivoExecucao.getContentType());
			arquivo.setName(arquivoExecucao.getOriginalFilename());
			arquivo.setLoginUsuario(usuario.lgn);
			arquivo.setDataReferencia(dtReferencia);
			arquivo.setFlag(arquivoPleitos);
			propostaOrcamentariaService.enviaPropostaOrcamentaria(arquivo, token);

	    EnvioXmlPropOrcamentariaOut retfor = new EnvioXmlPropOrcamentariaOut();
	    retfor.token = token;
	    retfor.codigo = 0;
	    retfor.mensagem = bundle.getString("ExecucaoController.arquivoenviado.sucesso");
	    ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retfor, new HttpHeaders(), HttpStatus.OK);
		} catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlPropOrcamentariaOut retf = new EnvioXmlPropOrcamentariaOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlPropOrcamentariaOut retf = new EnvioXmlPropOrcamentariaOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
		return ret;
	}
	
  @RequestMapping(value = "/formulariosPropostaOrcamentaria/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlPropOrcamentariaOut> formulariosPropostaOrcamentaria(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlPropOrcamentariaOut> ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlPropOrcamentariaIn prodObs = gson.fromJson(reader, EnvioXmlPropOrcamentariaIn.class);
      List<FormularioExecucao> formularios = propostaOrcamentariaService.getFormulariosPropostaOrcamentaria(prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlPropOrcamentariaOut retfor = new EnvioXmlPropOrcamentariaOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulários recuperados com sucesso";
      retfor.formularios = formularios;
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlPropOrcamentariaOut retf = new EnvioXmlPropOrcamentariaOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlPropOrcamentariaOut retf = new EnvioXmlPropOrcamentariaOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  } 	
	
  @RequestMapping(value = "/formulariosPropostaOrcamentariaPorId/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlPropOrcamentariaOut> formulariosPropostaOrcamentariaPorId(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlPropOrcamentariaOut> ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlPropOrcamentariaIn prodObs = gson.fromJson(reader, EnvioXmlPropOrcamentariaIn.class);
      FormularioExecucao formulario = propostaOrcamentariaService.getFormularioPropostaOrcamentariaPorId(prodObs.token, prodObs.idFormulario);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlPropOrcamentariaOut retfor = new EnvioXmlPropOrcamentariaOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulário recuperado com sucesso";
      retfor.formulario = formulario;
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlPropOrcamentariaOut retf = new EnvioXmlPropOrcamentariaOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlPropOrcamentariaOut retf = new EnvioXmlPropOrcamentariaOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  } 	  
  

  
  @RequestMapping(value = "/apagarArquivoPropostaOrcamentaria/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlPropOrcamentariaOut> apagarArquivoPropostaOrcamentaria(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlPropOrcamentariaOut> ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlPropOrcamentariaIn prodObs = gson.fromJson(reader, EnvioXmlPropOrcamentariaIn.class);
      propostaOrcamentariaService.apagarFormularioPropostaOrcamentaria(prodObs.token, prodObs.idFormulario);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlPropOrcamentariaOut retfor = new EnvioXmlPropOrcamentariaOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Formulário apagado com sucesso";
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      EnvioXmlPropOrcamentariaOut retf = new EnvioXmlPropOrcamentariaOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      EnvioXmlPropOrcamentariaOut retf = new EnvioXmlPropOrcamentariaOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<EnvioXmlPropOrcamentariaOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }   
}

class EnvioXmlPropOrcamentariaIn {
  public String token;
  public String competencia;
  public Long idFormulario;
  public String nomeArquivo;
}

class EnvioXmlPropOrcamentariaOut {
  public int codigo;
  public String mensagem;
  public String token; 
  public FormularioExecucao formulario;
  public List<FormularioExecucao> formularios;
}


