package br.jus.tredf.justicanumeros.controller;

import java.io.StringReader;

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

import br.jus.tredf.justicanumeros.model.User;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.service.FormulariosTREDFAutenticacaoService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

@RestController
@RequestMapping("/services/formulariostredf/auth")
public class FormulariosTREDFAutenticacaoController {
  
  @Autowired
  private FormulariosTREDFAutenticacaoService formulariosService;
  
  @Autowired
  private AuthenticationService authenticationService;

  @RequestMapping(value = "/getUsr/", 
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UsuarioVO> getUser(@RequestBody String req, 
      UriComponentsBuilder ucBuilder) {
    ResponseEntity<UsuarioVO> ret = new ResponseEntity<UsuarioVO>(HttpStatus.OK);

    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new StringReader(req));
    reader.setLenient(true);
    try {
      User user = gson.fromJson(reader, User.class);
      UsuarioVO usuarioVo = formulariosService.getDadosUsuario(user.getUserName(), user.getPassword());
      usuarioVo.token = authenticationService.criaToken(user.getUserName().toLowerCase(), user.getPassword());
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(ucBuilder.path("/user/authuser/{id}")
          .buildAndExpand(user.getId()).toUri());
      formulariosService.registrarAcessoUsuario(user.getUserName());
      ret = new ResponseEntity<UsuarioVO>(usuarioVo, headers, HttpStatus.OK);      
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      UsuarioVO usuarioVo = new UsuarioVO();
      usuarioVo.mensagem = e.getMessage();
      ret = new ResponseEntity<UsuarioVO>(usuarioVo, responseHeaders, 
          HttpStatus.UNAUTHORIZED);
    }
    return ret;
  }
  
}
