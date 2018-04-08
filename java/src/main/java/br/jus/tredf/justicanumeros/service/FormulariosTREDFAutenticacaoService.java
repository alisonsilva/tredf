package br.jus.tredf.justicanumeros.service;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.naming.ldap.InitialLdapContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.jus.tredf.justicanumeros.dao.ActiveDirectoryDao;
import br.jus.tredf.justicanumeros.dao.GrupoDao;
import br.jus.tredf.justicanumeros.dao.ICodigosAcoes;
import br.jus.tredf.justicanumeros.dao.LogAcoesDao;
import br.jus.tredf.justicanumeros.dao.ServentiasDao;
import br.jus.tredf.justicanumeros.dao.UsuarioDao;
import br.jus.tredf.justicanumeros.model.LogAcoes;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.GrupoVO;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

@Service(value = "formulariosTredfAutenticacaoService")
@Transactional
@SuppressWarnings("all") 
public class FormulariosTREDFAutenticacaoService {

  
  @Autowired
  private  ActiveDirectoryDao activeDirectoryService;
  
  @Autowired
  private UsuarioDao usuarioDao;
  
  @Autowired
  private GrupoDao grupoDao;
    
  @Autowired
  private ResourceBundle bundle;  
  
  @Autowired
  private ServentiasDao serventiasDao;
  
  @Autowired
  private LogAcoesDao logAcoesDao;
  
  public UsuarioVO getDadosUsuario(String login, String senha) {
    InitialLdapContext context = activeDirectoryService.loginDominio(login, senha);
    if(context == null) {
      throw new ParametroException(
          bundle.getString("FormulariosTREDFAutenticacaoService.getDadosUsuario.usuarioInvalido"), 
          ICodigosErros.ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO);
    }
    UsuarioVO usr = activeDirectoryService.getDadosUsuario(login.toLowerCase(), senha);
    UsuarioVO usrLocal = usuarioDao.recuperarUsuarioPorLogin(usr.lgn.toLowerCase());
    if(usrLocal == null) {
      usr = usuarioDao.inserirUsuario(usr);
    } else {
      usr.id = usrLocal.id;
    }
    List<GrupoVO> grupos = grupoDao.getGruposPorLogin(usr.lgn);
    usr.grupos = grupos;
    return usr;
  }
  
  public void registrarAcessoUsuario(String login) {
    UsuarioVO usuario = usuarioDao.recuperarUsuarioPorLogin(login);
		LogAcoes log = new LogAcoes();
		log.setCodAcao(ICodigosAcoes.ACAO_USUARIO_ACESSANDO_SISTEMA);
		log.setDtAtualizacao(new Date());
		log.setUsuario(usuario);
		log.setDescricao(bundle.getString("AuthenticationService.logUsuarioAcessando"));
    logAcoesDao.inserirLogAcoes(log);  	
  }
}
