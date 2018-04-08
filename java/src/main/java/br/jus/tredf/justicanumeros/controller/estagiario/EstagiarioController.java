package br.jus.tredf.justicanumeros.controller.estagiario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.jus.tredf.justicanumeros.model.estagiario.DemonstrativoPagamento;
import br.jus.tredf.justicanumeros.model.estagiario.Estagiario;
import br.jus.tredf.justicanumeros.service.estagiario.EstagiarioService;

@RestController
@RequestMapping("/services/formulariostredf/estagiario")
public class EstagiarioController {
  
  @Autowired
  private EstagiarioService estagiarioService;
  
  
  /**
   * 
   * @return
   */
  @RequestMapping(value = "/getDemonstrativoPagamento", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getTodosGrauInstrucao() {
    List<DemonstrativoPagamento> grausInstrucao = null;
    ResponseEntity<String> ret = 
        new ResponseEntity<String>(HttpStatus.OK);
    try {
      grausInstrucao = estagiarioService.getDemonstrativos();
      if(grausInstrucao != null && grausInstrucao.size() > 0) {
        String resposta = "{resposta: [";
        int element = 0;
        for(DemonstrativoPagamento dem : grausInstrucao) {
          if(element > 0) {
            resposta += ",";
          }
          resposta += dem.toString();
          element++;
        }
        resposta += "]}";
        ret = new ResponseEntity<String>(resposta, HttpStatus.ACCEPTED);
      } else {
        ret = new ResponseEntity<String>("{resposta: []}", HttpStatus.NO_CONTENT);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<String>("{resposta: [{erro: 'Erro generico', mensagem: '" + 
          e.getMessage() + "'}]}", HttpStatus.UNAUTHORIZED);
    }
    return ret;
  }  
 

  /**
   * 
   * @return
   */
  @RequestMapping(value = "/getEstagiarios", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getEstagiarios() {
    List<Estagiario> estagiarios = null;
    ResponseEntity<String> ret = 
        new ResponseEntity<String>(HttpStatus.OK);
    try {
    	estagiarios = estagiarioService.getEstagiarios();
      if(estagiarios != null && estagiarios.size() > 0) {
        String resposta = "{resposta: [";
        int element = 0;
        for(Estagiario estagiario : estagiarios) {
          if(element > 0) {
            resposta += ",";
          }
          resposta += estagiario.toString();
          element++;
        }
        resposta += "]}";
        ret = new ResponseEntity<String>(resposta, HttpStatus.ACCEPTED);
      } else {
        ret = new ResponseEntity<String>("{resposta: []}", HttpStatus.NO_CONTENT);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<String>("{resposta: [{erro: 'Erro generico', mensagem: '" + 
          e.getMessage() + "'}]}", HttpStatus.UNAUTHORIZED);
    }
    return ret;
  }   
}

