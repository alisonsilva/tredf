package br.jus.tredf.justicanumeros.controller.impressometro;

import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.jus.tredf.justicanumeros.model.impressometro.ImpressoraDto;
import br.jus.tredf.justicanumeros.model.justificativa.Observacao;
import br.jus.tredf.justicanumeros.service.impressometro.ImpressoraService;
import br.jus.tredf.justicanumeros.util.AuthenticationService;

@RestController
@RequestMapping("/services/formulariostredf/impressora")
public class ImpressoraController {
  @Autowired
  private AuthenticationService authenticationService;
  
  @Autowired
  private ResourceBundle bundle;

  @Autowired
  private ImpressoraService impressoraService;
  
  
  @RequestMapping(value = "/getImpressorasRede", 
      method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ImpressoraDto>> getCartorios() {
    List<ImpressoraDto> areasAtuacao = null;
    ResponseEntity<List<ImpressoraDto>> ret = 
        new ResponseEntity<List<ImpressoraDto>>(HttpStatus.OK);
    try {
      areasAtuacao = impressoraService.coletaInformacoesImpressoras();
      if(areasAtuacao != null) {
        ret = new ResponseEntity<List<ImpressoraDto>>(areasAtuacao, HttpStatus.OK);
      }
    } catch (Exception e) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add("ExceptionCause", e.getMessage());
      ret = new ResponseEntity<List<ImpressoraDto>>(responseHeaders, HttpStatus.OK);
    }
    return ret;
  }    
  

}



class ObservacaoProdIn {
  public String token;
  public String filtro;
  public Observacao observacao;
}

class ObservacaoProdOut {
  public int codigo;
  public String mensagem;
  public String token;
  public Observacao observacao;
  public List<ImpressoraDto> observacoes;
}
