package br.jus.tredf.justicanumeros.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.jus.tredf.justicanumeros.dao.ActiveDirectoryDao;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

@Component("atualizaUsuariosWorker")
public class AtualizaUsuariosWorker implements Runnable {

  @Autowired
  private ActiveDirectoryDao activeDirectoryDao;
  
  @Autowired
  private UsuarioDao usuarioDao;
  
  @Override
  public void run() {
    List<UsuarioVO> usuarios = activeDirectoryDao.getTodosUsuarios();
    usuarioDao.insereUsuariosNaoExistentes(usuarios);
  }

}
