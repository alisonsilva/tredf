package br.jus.tredf.justicanumeros.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class AtualizacaoImpressorasTask {

  @Autowired
  @Qualifier("atualizacaoImpressorasWorker")
  private Runnable atualizacaoImpressorasWorker;
  
  @Scheduled(cron="0 0 6-23 * * *")
  public void atualizaCache() {
    try {
      Thread.currentThread().sleep(20000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    atualizacaoImpressorasWorker.run();
  }

}
