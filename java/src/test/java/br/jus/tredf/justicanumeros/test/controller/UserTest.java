package br.jus.tredf.justicanumeros.test.controller;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.jus.tredf.justicanumeros.model.User;
import br.jus.tredf.justicanumeros.test.TesteBase;

import com.sun.jersey.api.client.WebResource;

@SuppressWarnings(value = "all")
public class UserTest extends TesteBase {

 
  
  @Test
  public void testA_InsertRemoveUser() {

    String addUserUrl = urlBase + "user/newuser/";
    String queryUserUrl = urlBase + "user/userbyusername/";
    String authenticateUserUrl = urlBase + "user/authenticateuser";
    String removeUserUrl = urlBase + "user/removeuser";
    
    try {
      // insertion
      System.out.println("--------> User Test Cases <-------- "
          + "Testing user insertion: ");
      String reqStr = "{userName: 'peter', password: '1234567'}";
      WebResource webResource =
        client.resource(addUserUrl);
      webResource.accept("application/json");
      String info = webResource.post(String.class, reqStr);
      Assert.assertNotNull(info);
      System.out.println("-------->  User Test Cases <-------- "
          + "Received message (user id): " + info);
      System.out.println("-------->  User Test Cases <-------- "
          + "Testing user insertion: Ok");
      
      //querying user
      System.out.println("-------->  User Test Cases <-------- "
          + "Testing user query: ");
      webResource =
          client.resource(queryUserUrl + "peter");
      webResource.accept("application/json");
      List<User> usuarios = webResource.get(List.class);
      Assert.assertNotNull(usuarios);
      Assert.assertFalse(usuarios.size() == 0);
      System.out.println("Users received:");
      for(Object user : usuarios) {
        System.out.println("-------->  User Test Cases <-------- : " + user);
      }
      
      // authenticating user
      System.out.println("-------->  User Test Cases <-------- "
          + "Testing user authentication: ");
      reqStr = "{userName: 'peter', password: '1234567'}";
      webResource =
        client.resource(authenticateUserUrl);
      webResource.accept("application/json");
      info = webResource.post(String.class, reqStr);
      Assert.assertNotNull(info);
      System.out.println("-------->  User Test Cases <-------- "
          + "Received message: " + info);
      System.out.println("-------->  User Test Cases <-------- "
          + "Testing user insertion: Ok");
      
      //removing user
      System.out.println("-------->  User Test Cases <-------- "
          + "Testing removing user: ");
      reqStr = "{id: '" + info + "', userName: 'peter', password: '1234567'}";
      webResource =
        client.resource(removeUserUrl);
      webResource.accept("application/json");
      info = webResource.post(String.class, reqStr);
      Assert.assertNotNull(info);
      System.out.println("-------->  User Test Cases <-------- "
          + "Received message: " + info);
      System.out.println("-------->  User Test Cases <-------- "
          + "Testing removing user: Ok");
      
    } catch (Exception except) {
      if (except.getMessage().contains("401")) {
        except = new Exception("N�oo autorizado a realizar essa operação");
        throw new RuntimeException(except);
      } else if (except.getMessage().contains("Connection refused: connect")) {
        System.out.println("-------->  User Test Cases <-------- "
            + "Serve out of reach");
      }
    }
  }

}
