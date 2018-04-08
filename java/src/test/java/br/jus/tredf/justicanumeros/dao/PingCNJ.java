package br.jus.tredf.justicanumeros.dao;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class PingCNJ {

  public static void main(String[] args) {
    testarConexao();
  }
  
  public static void testarConexao() {
    try {
      final Client client = Client.create();
      client.addFilter(new HTTPBasicAuthFilter("TRE-DF Tribunal Regional Eleitoral do Distrito Federal", 
          "0e2c11d7b21e504d141079c09b3d07e8"));

      final WebResource resource = client.resource("https://wwwh.cnj.jus.br/selo-integracao-web/v1/processos");
      ClientResponse response = resource.get(ClientResponse.class);
      System.out.println(response.getEntity(String.class));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
