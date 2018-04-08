package br.jus.tredf.justicanumeros.test;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import br.jus.tredf.justicanumeros.service.envioxml.EnvioProcessoService;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Ignore
public class TesteBase {


  @Autowired
  protected WebApplicationContext ctx;
  
  @Autowired
  protected EnvioProcessoService enviaProcessoService;  

  protected PropertiesServiceController properties;
 
  protected MockMvc mockMvc;
  protected Client client;
  protected String urlBase;
  protected String urlCrossover;
  
  
  /**
   * Initiates the test case.
   */
  @Before
  public void setUp() {
    properties = PropertiesServiceController.getInstance();
    this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    ClientConfig clientConfig = new DefaultClientConfig();
    clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, 
        Boolean.TRUE);

    client = Client.create(clientConfig);
    client.setConnectTimeout(120 * 1000);
    client.setReadTimeout(120 * 1000);
    
    String host = properties.getProperty("web.host");
    String port = properties.getProperty("web.port");
    urlCrossover = "/services/crossover/"; 
    urlBase = "http://"+host;
    if(!StringUtils.isEmpty(port)) {
      urlBase += ":"+port;
    }
    urlBase += urlCrossover; 
    
  }

}
