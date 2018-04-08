package br.jus.tredf.justicanumeros.dao.envioxml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;

import br.jus.tredf.justicanumeros.dao.terceirizado.TerceirizadoDao;
import br.jus.tredf.justicanumeros.model.envioxml.ControleCriacaoXml;
import br.jus.tredf.justicanumeros.model.envioxml.EnvioProcessoXML;
import br.jus.tredf.justicanumeros.model.envioxml.EnvioXML;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;

@Repository("DadosEnvioDao")
public class DadosEnvioDao {
  private static final int PRIMEIRA_INSTANCIA = 1;
  private static final int SEGUNDA_INSTANCIA = 2;
  
  private static final Logger logger = Logger.getLogger(DadosEnvioDao.class);
  
  @Autowired
  private ComboPooledDataSource dataSource;
  
  @Autowired
  private ResourceBundle bundle;
  
  @Autowired
  private ResourceBundle bundleXml;
  
  
  public void montaArquivoEnvio(List<EnvioProcessoXML> processos) {
    Connection con = null;
    
    final ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(MultiPartWriter.class);

    Client client = null;
    try {

      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("UPDATE JN_ENVIO_PROCESSO SET INFO_ENVIO = ?, "
          + "FL_ENVIADO = ? "
          + "WHERE ID = ?");
      
      URL openStream = this.getClass().getClassLoader().getResource("envio_xml/processo.xml");
      Writer fos = new FileWriter(new File(openStream.getFile()));
      fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns2:processos xmlns=\"http://www.cnj.jus.br/intercomunicacao-2.2.2\" xmlns:ns2=\"http://www.cnj.jus.br/replicacao-nacional\">");
      
      for (EnvioProcessoXML processo : processos) {
        processo.setElementoXml(processo.getElementoXml().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns2:processos xmlns=\"http://www.cnj.jus.br/intercomunicacao-2.2.2\" xmlns:ns2=\"http://www.cnj.jus.br/replicacao-nacional\">", ""));
        processo.setElementoXml(processo.getElementoXml().replace("</ns2:processos>", ""));
        fos.write(processo.getElementoXml());
        
        pstmt.clearParameters();
        pstmt.setString(1, "Arquivo montado");
        pstmt.setInt(2, 1);
        pstmt.setLong(3, processo.getId());
        pstmt.executeUpdate();
      }
      fos.write("</ns2:processos>");
      fos.flush();
      fos.close();
      pstmt.close();
    } catch (Exception e) {
      logger.error("Erro ao executar comando de banco (enviaProcessos)", e);
    } finally {
      if (client != null) {
        client.destroy();
      }
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }    
  }
  
  public void enviaProcesso(EnvioProcessoXML processo) {
    List<EnvioProcessoXML> processos = new ArrayList<EnvioProcessoXML>();
    processos.add(processo);
    enviaProcessos(processos);
  }

  public void enviaProcessos(List<EnvioProcessoXML> processos) {
    String usuario = bundleXml.getString("envio_xml.usuario");
    String senha = bundleXml.getString("envio_xml.senha");
    String url = bundleXml.getString("envio_xml.url");

    Connection con = null;
    
    final ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(MultiPartWriter.class);

    Client client = null;
    try {
      client = Client.create(config);
      client.addFilter(new HTTPBasicAuthFilter(usuario, senha));
      WebResource resource = client.resource(url);

      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("UPDATE JN_ENVIO_PROCESSO SET INFO_ENVIO = ?, "
          + "FL_ENVIADO = ?, "
      		+ "DT_PROC_ENVIO = ? "
          + "WHERE ID = ?");

      boolean resolved = true;
      for (EnvioProcessoXML processo : processos) {
        resolved = true;
        montaArquivo(processo);
        ClientResponse response = null;
        try {
          File fileToUpload = new File(bundleXml.getString("envio_xml.caminho_arquivo_envio"));
          if(fileToUpload != null) {
            FileDataBodyPart filePart = new FileDataBodyPart("file", fileToUpload,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);

            MultiPart multiPart = new FormDataMultiPart();
            multiPart.bodyPart(filePart);

            response = resource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, multiPart);
          }
        } catch (Exception e) {
        	logger.error("Erro ao executar comando de envioxml (envioArquivoXml)", e);
          resolved = false;
        }
        
        pstmt.clearParameters();
        if (resolved == true && response != null && ((response.getStatus() == HttpStatus.SC_CREATED) ||
            (response.getStatus() == HttpStatus.SC_OK) ||
            (response.getStatus() == HttpStatus.SC_MOVED_TEMPORARILY))) {
          pstmt.setString(1, response.toString());
          pstmt.setInt(2, 1);
        } else {
          pstmt.setString(1, response == null ? "Erro generico enviando arquivo XML" : response.toString());
          pstmt.setInt(2, 0);
        }
        pstmt.setDate(3, new java.sql.Date((new Date()).getTime()));
        pstmt.setLong(4, processo.getId());
        pstmt.executeUpdate();
      }
      pstmt.close();
    } catch (Exception e) {
      logger.error("Erro ao executar comando de banco (enviaProcessos): ", e);
      throw new ParametroException("Erro executando envio de processos ao CNJ: " + e.getMessage());
    } finally {
      if (client != null) {
        client.destroy();
      }
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    
  }

  private void montaArquivo(EnvioProcessoXML processo) throws IOException, InterruptedException {
    Writer fos = new FileWriter(new File(bundleXml.getString("envio_xml.caminho_arquivo_envio")), false);
    fos.write(processo.getElementoXml());
    fos.flush();
    fos.close();
  }
  
  /**
   * Gera o arquivo XML para a competência (formato mm/yyyy) e instância (1 ou 2)
   * @param competencia A competência de referência. Formato mm/yyyy
   * @param instancia A instância para qual se deseja gerar o XML. Formato númerico valores 1(primeira instância) 
   * ou 2 (segunda instância)
   */
  public void geraXml(String competencia, int instancia) {
    Connection con = null;
    try {
      con = dataSource.getConnection();

      CallableStatement cStmt = null;
      if (instancia == SEGUNDA_INSTANCIA) {
        cStmt = con.prepareCall("{call DW_CNJ.pc_cnj_selo_xml.arq_selo_just(3, ?)}");
        cStmt.setString(1, competencia);
        cStmt.execute();
      } else if (instancia == PRIMEIRA_INSTANCIA) {
        cStmt = con.prepareCall("{call DW_CNJ.pc_cnj_selo_xml.arq_selo_just_zona(3, ?)}");
        cStmt.setString(1, competencia);
        cStmt.execute();
      }
      if(cStmt != null) {
        cStmt.close();
      }
    } catch (SQLException e) {
      logger.error("Erro ao executar comando de banco (EnvioProcessoDao)", e);
      throw new ParametroException("Erro ao gerar XML no lado do servidor: " + e.getMessage());
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
  }  
  
  /**
   * Recupera listagem com os processos a serem enviados para o CNJ de acordo com a 
   * campet�ncia e o grau.
   * @param competencia Compet�ncia a ser considerada para recupera��o dos processos.
   * Formato: mm/yyyy
   * @param instancia Grau da inst�ncia cujos processos est�o sendo recuperados. 
   * Podendo ser: 1 - primeiro grau; 2 - segundo grau
   * @return Listagem com os processos encontrados
   */
  public EnvioXML getProcessosEnvio(String competencia, int instancia) {
    EnvioXML processos = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * "
            +"FROM DW_CNJ.CNJ_DADOS_XML WHERE DAT_PROCESSAMENTO = " 
            +"(SELECT MAX(DAT_PROCESSAMENTO) FROM DW_CNJ.CNJ_DADOS_XML "
            +"WHERE TO_CHAR(DAT_REFERENCIA, 'MM/YYYY') = ? "
            +"AND GRAU = ?)"); 
      pstmt.setString(1, competencia);
      pstmt.setInt(2, instancia);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        String st = readClob(rs.getClob("VL_CLOB"));
        processos = new EnvioXML();
        processos.setDtRefProc(rs.getDate("DAT_REFERENCIA"));
        processos.setDtComando(rs.getDate("DAT_PROCESSAMENTO"));
        processos.setFlEnviado(false);
        processos.setProcessosEnviados(infoProcesso(st));
      }
      rs.close();
      pstmt.close();
    } catch (Exception e) {
      logger.error("Erro ao executar opera��o em banco de dados (DadosEnvioDao)", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return processos;
  }
  
  private String readClob(Clob clob) throws Exception {
    //IOUtils.copy(clob.getAsciiStream(), sb);
    StringBuffer sb = new StringBuffer();
    
    Reader reader = new InputStreamReader(clob.getAsciiStream(),Charset.forName("UTF-8"));
    BufferedReader br = new BufferedReader(reader);

    String line;
    while(null != (line = br.readLine())) {
        sb.append(line);
    }
    br.close();

    return sb.toString();    
  }
  
  private List<EnvioProcessoXML> infoProcesso(String fullText) throws Exception {
    if(StringUtils.isEmpty(fullText)) {
      throw new ParametroException("Xml está vazio", 1);
    }
    List<EnvioProcessoXML> processos = new ArrayList<EnvioProcessoXML>();
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(new ByteArrayInputStream(fullText.getBytes()));
    NodeList nList = doc.getElementsByTagName("ns2:processo");
    
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    for(int idx = 0; idx < nList.getLength(); idx++){
      EnvioProcessoXML procXml = new EnvioProcessoXML();
      Node nNode = nList.item(idx);
      if(nNode.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element)nNode;
        DOMSource source = new DOMSource(element);
        StreamResult result = new StreamResult(new StringWriter());
        transformer.transform(source, result);
        String textoXmlProcesso = result.getWriter().toString();
        textoXmlProcesso = textoXmlProcesso.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
        
        String textoXml = MessageFormat.format(
            bundleXml.getString("envio_xml.processo.basico"), 
            textoXmlProcesso);
        procXml.setElementoXml(textoXml);
        Element dadosBasicos = (Element)element.getElementsByTagName("dadosBasicos").item(0);        
        procXml.setClasseProcessual(dadosBasicos.getAttribute("classeProcessual"));
        procXml.setNumero(dadosBasicos.getAttribute("numero"));
        procXml.setCodLocalidade(StringUtils.isEmpty(dadosBasicos.getAttribute("codigoLocalidade")) ? 
            0 : Integer.valueOf(dadosBasicos.getAttribute("codigoLocalidade")));
        procXml.setNivelSigilo(StringUtils.isEmpty(dadosBasicos.getAttribute("nivelSigilo")) ? 
            0 : Integer.valueOf(dadosBasicos.getAttribute("nivelSigilo")));
        procXml.setIntervencaoMp(StringUtils.isEmpty(dadosBasicos.getAttribute("intervencaoMP")) ? 
            0 : Boolean.parseBoolean(dadosBasicos.getAttribute("intervencaoMP")) ? 1 : 0);
        
        String dtAjuizamento = dadosBasicos.getAttribute("dataAjuizamento");
        if(!StringUtils.isEmpty(dtAjuizamento)) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
          procXml.setDtAjuizamento(sdf.parse(dtAjuizamento));
        }
        processos.add(procXml);
      }
    }
    return processos;
  } 
  
  /**
   * Recupera do banco de dados o registro da geração do arquivo XML, caso exista um.
   * @param competencia Competência para a verificação do registro. Formato mm/YYYY
   * @param instancia Instância. Valores 1 ou 2
   * @return
   */
  public ControleCriacaoXml getControleCriacaoXml(String competencia, int instancia) {
    ControleCriacaoXml cntrl = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement("SELECT * FROM JN_CNTRL_CRIACAO_XML "
          + "WHERE COMPETENCIA = ? AND INSTANCIA = ?");
      pstmt.setString(1, competencia);
      pstmt.setInt(2, instancia);
      ResultSet rs = pstmt.executeQuery();
      if(rs.next()) {
        cntrl = new ControleCriacaoXml();
        cntrl.setId(rs.getLong("ID"));
        cntrl.setCompetencia(rs.getString("COMPETENCIA"));
        cntrl.setInstancia(rs.getInt("INSTANCIA"));
        cntrl.setUsuarioId(rs.getLong("JN_USUARIOID"));
      }
      rs.close();
    } catch (SQLException e) {
      logger.error("Erro ao executar operação em banco de dados (DadosEnvioDao)", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return cntrl;
  }
  
  /**
   * 
   * @param competencia
   * @param instancia
   * @param usuarioId
   */
  public ControleCriacaoXml newControleCriacaoXml(String competencia, int instancia, Long usuarioId) {
  	if (StringUtils.isEmpty(competencia) || instancia < 1 || instancia > 2 || usuarioId == null || usuarioId < 1) {
  		throw new ParametroException("Dados inválidos", ICodigosErros.ERRO_ENVIOXML_PARAMETROINVALIDO);
  	}
    ControleCriacaoXml controle = null;
    Connection con = null;
    try {
      con = dataSource.getConnection();
      PreparedStatement ins = con.prepareStatement("INSERT INTO JN_CNTRL_CRIACAO_XML "
      		+ "(COMPETENCIA, INSTANCIA, JN_USUARIOID) "
          + "VALUES (?,?,?)", new String[]{"ID"});
      ins.setString(1, competencia);
      ins.setInt(2, instancia);
      ins.setLong(3, usuarioId);
      ins.executeUpdate();
      
      ResultSet rs = ins.getGeneratedKeys();
      if(rs.next()) {
        controle = new ControleCriacaoXml();
        controle.setId(rs.getLong(1));
        controle.setCompetencia(competencia);
        controle.setInstancia(instancia);
        controle.setUsuarioId(usuarioId);
      }
      rs.close();
      ins.close();
    } catch (SQLException e) {
      logger.error("Erro ao executar operação em banco de dados (DadosEnvioDao)", e);
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
        }
      }
    }
    return controle;
  }
  
  public void removeControleCriacaoXml(Long id) {
  	Connection con = null;
  	
  	try {
			con = dataSource.getConnection();
			PreparedStatement pstmt = con.prepareStatement("DELETE FROM JN_CNTRL_CRIACAO_XML WHERE ID = ?");
			pstmt.setLong(1, id);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("Erro apagando controle de criação: " + e.getMessage());
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
  }
}
