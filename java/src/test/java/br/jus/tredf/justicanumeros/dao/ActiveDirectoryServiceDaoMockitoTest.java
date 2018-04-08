package br.jus.tredf.justicanumeros.dao;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.test.MockitoTesteBase;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;


public class ActiveDirectoryServiceDaoMockitoTest extends MockitoTesteBase {
  @Mock
  PropertiesServiceController propertiesServiceController;
  
  @InjectMocks
  ActiveDirectoryDao activeDirectory = new ActiveDirectoryDao();
  
  @Test
  public void testGetUser() {
    
//    when(propertiesServiceController.getProperty("ldap.activedirectory.ativo")).thenReturn("true");
//    when(propertiesServiceController.getProperty("ldap.activedirectory.host")).thenReturn("dfdc10.tre-df.gov.br");
//    when(propertiesServiceController.getProperty("ldap.activedirectory.dominio")).thenReturn("tre-df.gov.br");
//    when(propertiesServiceController.getProperty("ldap.activedirectory.porta")).thenReturn("389");
//    when(propertiesServiceController.getProperty("ldap.activedirectory.sldapjks")).thenReturn("");
//    when(propertiesServiceController.getProperty("ldap.activedirectory.sldapporta")).thenReturn("389");
//    when(propertiesServiceController.getProperty("ldap.activedirectory.usuario")).thenReturn("alison.silva");
//    when(propertiesServiceController.getProperty("ldap.activedirectory.senha")).thenReturn("");
    
    UsuarioVO usuario = activeDirectory.getDadosUsuario("alison.silva", "AliSil12");
    Assert.assertTrue(usuario.cn.equals("alison.silva"));
  }
}
