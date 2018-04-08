package br.jus.tredf.justicanumeros.test.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.http.MediaType;

import br.jus.tredf.justicanumeros.model.User;
import br.jus.tredf.justicanumeros.test.TesteBase;

import com.sun.jersey.api.client.WebResource;

@SuppressWarnings(value = "all")
public class UserIntegrationTest extends TesteBase {
  private String addUserUrl = urlCrossover + "user/newuser/";
  private String queryUserUrl = urlCrossover + "user/userbyusername/{username}";
  private String authenticateUserUrl = urlCrossover + "user/authenticateuser";
  private String removeUserUrl = urlCrossover + "user/removeuser";

  @Test
  public void testA_InsertRemoveUser() throws Exception {

    
    this.mockMvc.perform(get(queryUserUrl,"alison"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$").isNotEmpty())
      .andExpect(jsonPath("$[0].userName").value("alison"));
  }

  
}
