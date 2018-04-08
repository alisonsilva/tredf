package br.jus.tredf.justicanumeros.controller;

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

import br.jus.tredf.justicanumeros.model.Cartorio;
import br.jus.tredf.justicanumeros.model.Permissao;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.GrupoVO;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.AdministracaoGrupoService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


@RestController
@RequestMapping("/services/formulariostredf/admgrupos")
public class AdministracaoGruposController {
  @Autowired
  private AdministracaoGrupoService administracaoGruposService;
  
  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private ResourceBundle bundle;
  
  
  @RequestMapping(value = "/permissoesPorGrupo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> getPermissoesPorGrupo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      List<Permissao> permissoesGrupo = 
          administracaoGruposService.permissoesPorGrupo(chamada.idGrupo, chamada.token);
      List<Permissao> permissoesNaoAtribuidas = 
          administracaoGruposService.permissoesNaoAtribuidasAoGrupo(
              chamada.idGrupo, chamada.token);
      List<Cartorio> cartorios = administracaoGruposService.getCartorios();
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.permissoesDoGrupo = permissoesGrupo;
      retfor.permissoesNaoAtribuidasAoGrupo = permissoesNaoAtribuidas;
      retfor.cartorios = cartorios;
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  @RequestMapping(value = "/alteraGrupo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> alteraGrupo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      administracaoGruposService.alteraGrupo(chamada.idGrupo, 
          chamada.nomeGrupo, chamada.descricaoGrupo, chamada.idCartorio, chamada.token);      
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Grupo alterado com sucesso";
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  @RequestMapping(value = "/novoGrupo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> novoGrupo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      GrupoVO grupo = administracaoGruposService.novoGrupo(chamada.nomeGrupo, chamada.descricaoGrupo, chamada.token);      
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.grupo = grupo;
      retfor.mensagem = "Grupo criado com sucesso";
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }   

  
  @RequestMapping(value = "/removeGrupo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> removeGrupo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      administracaoGruposService.removeGrupo(chamada.idGrupo, chamada.token);
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Grupo removido com sucesso";
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }   

  @RequestMapping(value = "/incluirPermissaoNoGrupo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> incluirPermissaoNoGrupo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      administracaoGruposService.incluiPermissaoNoGrupo(chamada.idGrupo, 
          chamada.idPermissao, chamada.token);      
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Permissão inserida com sucesso";
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }   
  
  @RequestMapping(value = "/incluirUsuarioNoGrupo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> incluirUsuarioNoGrupo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      administracaoGruposService.incluiUsuarioNoGrupo(chamada.idGrupo, 
          chamada.loginUsuario, chamada.token);      
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Usuário inserido com sucesso";
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  @RequestMapping(value = "/removerPermissaoDoGrupo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> removerPermissaoDoGrupo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      administracaoGruposService.removePermissaoDoGrupo(chamada.idGrupo, 
          chamada.idPermissao, chamada.token);      
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Permissão removida com sucesso";
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  @RequestMapping(value = "/removerUsuarioDoGrupo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> removerUsuarioDoGrupo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      administracaoGruposService.removeUsuarioDoGrupo(chamada.idGrupo, 
          chamada.idUsuario, chamada.token);      
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.mensagem = "Usuário removido com sucesso";
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  } 
  
  @RequestMapping(value = "/getAllGrupos", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> getGrupos() {
    List<GrupoVO> grupos = null;
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);
    try {
      grupos = administracaoGruposService.getGrupos();
      AdmGruposOut out = new AdmGruposOut();
      out.codigo = 0;
      out.mensagem = "Listagem recuperada";
      out.grupos = grupos;
      ret = new ResponseEntity<AdmGruposOut>(out, HttpStatus.OK);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut out = new AdmGruposOut();
      out.codigo = 1;
      out.mensagem = e.getMessage();      
      ret = new ResponseEntity<AdmGruposOut>(out, responseHeaders, HttpStatus.EXPECTATION_FAILED);
    }
    return ret;
  }  

  @RequestMapping(value = "/usuariosDoGrupo/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> usuariosDoGrupo(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      List<UsuarioVO> usuarios = administracaoGruposService.usuariosPorGrupo(chamada.idGrupo, chamada.token);      
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.usuariosDoGrupo = usuarios;
      retfor.mensagem = "Usuários recuperados com sucesso";
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }   

  @RequestMapping(value = "/usuarios/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> usuarios(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      List<UsuarioVO> usuarios = administracaoGruposService.listagemUsuarios(chamada.token);      
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.usuarios = usuarios;
      retfor.mensagem = "Usuários recuperados com sucesso";
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }  
  
  @RequestMapping(value = "/usuariosPaginados/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdmGruposOut> usuariosPaginados(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<AdmGruposOut> ret = new ResponseEntity<AdmGruposOut>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      AdmGruposIn chamada = gson.fromJson(reader, AdmGruposIn.class);
      List<UsuarioVO> usuarios = administracaoGruposService.usuariosPaginados(chamada.pagina, chamada.limite, chamada.filtro, chamada.token);  
      int totalUsuarios = administracaoGruposService.totalUsuarios(chamada.filtro);
      HttpHeaders headers = new HttpHeaders();
      AdmGruposOut retfor = new AdmGruposOut();
      UsuarioVO usuario = authenticationService.getUsuarioFromToken(chamada.token);
      retfor.token = authenticationService.criaToken(usuario.lgn, usuario.sn);
      retfor.codigo = 0;
      retfor.usuarios = usuarios;
      retfor.totalUsuarios = totalUsuarios;
      retfor.mensagem = "Usuários recuperados com sucesso";
      ret = new ResponseEntity<AdmGruposOut>(retfor, headers, HttpStatus.OK);
    } catch(ParametroException pex) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", pex.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = pex.getCodigoErro();
      retf.mensagem = pex.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      AdmGruposOut retf = new AdmGruposOut();
      retf.codigo = HttpStatus.EXPECTATION_FAILED.value();
      retf.mensagem = e.getMessage();
      ret = new ResponseEntity<AdmGruposOut>(retf, responseHeaders, HttpStatus.NOT_ACCEPTABLE);
    }
    return ret;
  }    
}


class AdmGruposOut {
  public String mensagem;
  public int codigo;
  public int totalUsuarios;
  public String token;
  public GrupoVO grupo;
  public List<GrupoVO> grupos;
  public List<Permissao> permissoesDoGrupo;
  public List<Permissao> permissoesNaoAtribuidasAoGrupo;
  public List<Cartorio> cartorios;
  public List<UsuarioVO> usuariosDoGrupo;
  public List<UsuarioVO> usuarios;
}

class AdmGruposIn {
  public String token;
  public Long idGrupo;
  public Long idPermissao;
  public Long idUsuario;
  public int pagina;
  public String filtro;
  public int limite;
  public String nomeGrupo;
  public String loginUsuario;
  public String descricaoGrupo;
  public Long idCartorio;
}