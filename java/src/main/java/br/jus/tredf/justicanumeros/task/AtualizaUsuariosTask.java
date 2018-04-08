package br.jus.tredf.justicanumeros.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class AtualizaUsuariosTask {

  @Autowired
  @Qualifier("atualizaUsuariosWorker")
  private Runnable atualizaUsuariosWorker;
  
  @Scheduled(fixedDelay=21600000)
  public void atualizaCache() {
    try {
      Thread.currentThread().sleep(20000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    atualizaUsuariosWorker.run();
  }

}
