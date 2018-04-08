package br.jus.tredf.justicanumeros.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.jus.tredf.justicanumeros.model.envioxml.EnvioProcessoXML;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;

public class ParsingXML {

  public static void main(String[] args) throws Exception {
    ParsingXML parsing = new ParsingXML();
    byte[] arqXml = new byte[1024];
    InputStream fio = ParsingXML.class.getResourceAsStream("/teste.xml");
    StringBuffer strBuff = new StringBuffer();
    while((fio.read(arqXml)) > 0) {
      strBuff.append(arqXml);
    }
    List<EnvioProcessoXML> processos = parsing.infoProcesso(strBuff.toString());
  }
  
  private List<EnvioProcessoXML> infoProcesso(String fullText) throws Exception {
    if(StringUtils.isEmpty(fullText)) {
      throw new ParametroException("Xml está vazio", 1);
    }
    List<EnvioProcessoXML> processos = new ArrayList<EnvioProcessoXML>();
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(new ByteArrayInputStream(fullText.getBytes()));
    NodeList nList = doc.getElementsByTagName("processo");
    
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
        procXml.setClasseProcessual(element.getAttribute("classeProcessual"));
        procXml.setNumero(element.getAttribute("numero"));
        procXml.setCodLocalidade(StringUtils.isEmpty(element.getAttribute("codigoLocalidade")) ? 
            0 : Integer.valueOf(element.getAttribute("codigoLocalidade")));
        procXml.setNivelSigilo(StringUtils.isEmpty(element.getAttribute("nivelSigilo")) ? 
            0 : Integer.valueOf(element.getAttribute("nivelSigilo")));
        procXml.setIntervencaoMp(StringUtils.isEmpty(element.getAttribute("intervencaoMP")) ? 
            0 : Boolean.parseBoolean(element.getAttribute("intervencaoMP")) ? 1 : 0);
        
        String dtAjuizamento = element.getAttribute("dataAjuizamento");
        if(!StringUtils.isEmpty(dtAjuizamento)) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
          procXml.setDtAjuizamento(sdf.parse(dtAjuizamento));
        }
        processos.add(procXml);
      }
    }
    return processos;
  }  
}
