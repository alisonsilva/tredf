package br.jus.tredf.justicanumeros.controller.envioxml;

import java.io.StringReader;
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

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import br.jus.tredf.justicanumeros.model.envioxml.EnvioXML;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.envioxml.EnvioProcessoService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

@RestController
@RequestMapping("/services/formulariostredf/envioProcesso")
public class EnvioXmlController {
  
  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private ResourceBundle bundle;
 
  @Autowired
  private EnvioProcessoService envioProcessoService;
  
  @RequestMapping(value = "/listagemEnvios/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlOut> getListagemEnvios(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
      List<EnvioXML> envios = envioProcessoService.getListaEnvios(prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlOut retfor = new EnvioXmlOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Envios recuperados com sucesso";
      retfor.listaEnvios = envios;
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
  
  @RequestMapping(value = "/recuperaEenviaProcessosXML/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlOut> recuperaEnviaProcessosXML(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
      envioProcessoService.recuperaEnvioProcessosCNJ(prodObs.competencia, prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlOut retfor = new EnvioXmlOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Procedimento de recuperação dos processos e envio foi iniciado";
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
  
  @RequestMapping(value = "/enviaProcessosNaoEnviadosPor/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlOut> enviaProcessosNaoEnviadosPorXML(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
      envioProcessoService.enviaProcessosNaoEnviadosPorEnvio(prodObs.envioProcessosCnjId, prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlOut retfor = new EnvioXmlOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Processos enviados com sucesso";
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
  
	@RequestMapping(value = "/enviaProcessosXML/", 
			method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EnvioXmlOut> enviaProcessosXML(@RequestBody String req, UriComponentsBuilder ucBuilder) {
		ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new StringReader(req));
		reader.setLenient(true);
		try {
			EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
			envioProcessoService.enviaProcessosCNJ(prodObs.competencia, prodObs.token);
			HttpHeaders headers = new HttpHeaders();
			UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
			EnvioXmlOut retfor = new EnvioXmlOut();
			retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
			retfor.codigo = 0;
			retfor.mensagem = "Processos enviados com sucesso";
			ret = new ResponseEntity<EnvioXmlOut>(retfor, headers, HttpStatus.OK);
		} catch (ParametroException pex) {
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

  @RequestMapping(value = "/enviaProcessoXML/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EnvioXmlOut> enviaProcessoXML(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
      envioProcessoService.enviaProcessosCNJPorProcesso(prodObs.processoId, prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlOut retfor = new EnvioXmlOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Processo enviado com sucesso";
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
  
  @RequestMapping(value = "/removeEnvioProcessos/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)  
  public ResponseEntity<EnvioXmlOut> removeEnvioProcessos(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<EnvioXmlOut> ret = new ResponseEntity<EnvioXmlOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      EnvioXmlIn prodObs = gson.fromJson(reader, EnvioXmlIn.class);
      envioProcessoService.removeEnvio(prodObs.envioProcessosCnjId, prodObs.token);
      HttpHeaders headers = new HttpHeaders();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(prodObs.token);
      EnvioXmlOut retfor = new EnvioXmlOut();
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Processo removido com sucesso";
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
  

  /**
   * Recupera relatório dos processos enviados para o CNJ por identificador do envio
   * @param idEnvioXml Identificador do envio
   * @return Relatório PDF com o status dos processos enviados
   */
  @RequestMapping(value = "/report/{idEnvioXml}", 
      method = RequestMethod.GET)
  public ResponseEntity<byte[]> report(
      @PathVariable("idEnvioXml") Long idEnvioXml) {
    ResponseEntity<byte[]> ret = 
        new ResponseEntity<byte[]>(HttpStatus.OK);
   try{
      byte[] report = envioProcessoService.relatorioProcessosEnviadosCnj(idEnvioXml);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.add("Content-disposition", "attachment; filename=processos_enviados_cnj.pdf");
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

class EnvioXmlIn {
  public String token;
  public String competencia;
  public long envioProcessosCnjId;
  public long processoId;
  public int instancia;
}

class EnvioXmlOut {
  public int codigo;
  public String mensagem;
  public String token;  
  public List<EnvioXML> listaEnvios;
}