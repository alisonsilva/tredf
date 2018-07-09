package br.jus.tredf.justicanumeros.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.jus.tredf.justicanumeros.service.impressometro.ImpressoraService;

@Component("atualizacaoImpressorasWorker")
public class AtualizacaoImpressorasWorker implements Runnable {

  @Autowired
  private ImpressoraService impressoraService;
  
  @Override
  public void run() {
  	impressoraService.coletaInformacoesImpressoras();
  }

}
