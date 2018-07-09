package br.jus.tredf.justicanumeros.controller.config;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.jms.ConnectionFactory;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Configuration
@EnableWebMvc
@EnableJms
@EnableScheduling
@ComponentScan(basePackages="br.jus.tredf.justicanumeros")
@PropertySource("classpath:formstredf.properties")
public class FormulariosTREDFConfiguration extends WebMvcConfigurerAdapter {
	public static final String DEFAULT_SAO_ARQUIVO_EXECUCAO_QUEUE = "qSaoArquivoExecucao";
	public static final String DEFAULT_SAO_ARQUIVO_DOTACAO_QUEUE = "qSaoArquivoDotacao";
	public static final String DEFAULT_SAO_ARQUIVO_PROP_ORCAMENTARIA = "qPropOrcamentaria";
	public static final String DEFAULT_EMAIL = "qEmail";
	private static final String DEFAULT_BROKER_URL = "tcp://10.20.1.23:61616"; //srv-bi.tre-df.gov.br
	
  @Autowired
  ConnectionFactory connectionFactory;
	
	
  public @Bean ResourceBundle bundle() {
    Locale locale = new Locale("pt", "BR");
    ResourceBundle bundle = ResourceBundle.getBundle("negocio_msgs", locale);
    return bundle;
  }

  public @Bean ResourceBundle bundleXml() {
    Locale locale = new Locale("pt", "BR");
    ResourceBundle bundle = ResourceBundle.getBundle("envio_xml/info_envio", locale);
    return bundle;
  }
  
  public @Bean ResourceBundle bundleExecucao() {
    Locale locale = new Locale("pt", "BR");
    ResourceBundle bundle = ResourceBundle.getBundle("execucao/info_execucao", locale);
    return bundle;
  }  
  
  public @Bean ResourceBundle bundlePropOrcamentaria() {
    Locale locale = new Locale("pt", "BR");
    ResourceBundle bundle = ResourceBundle.getBundle("execucao/info_proporcamentaria", locale);
    return bundle;
  }  
  
  public @Bean ResourceBundle bundleDotacao() {
    Locale locale = new Locale("pt", "BR");
    ResourceBundle bundle = ResourceBundle.getBundle("execucao/info_dotacao", locale);
    return bundle;
  }   
  
  @Bean(name = "multipartResolver")
  public CommonsMultipartResolver multipartResolver() {
      CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
      multipartResolver.setMaxUploadSize(-1);// no limits (in bytes)
      return multipartResolver;
  }  
  
  
  public @Bean ComboPooledDataSource dataSource() throws Exception {
    ComboPooledDataSource combo = new ComboPooledDataSource();
    combo.setDriverClass("oracle.jdbc.driver.OracleDriver");
    combo.setJdbcUrl(PropertiesServiceController.getInstance().getProperty("jdbc.url"));
    combo.setUser(PropertiesServiceController.getInstance().getProperty("jdbc.usuario"));
    combo.setPassword(PropertiesServiceController.getInstance().getProperty("jdbc.senha"));
    
    combo.setMinPoolSize(5);
    combo.setMaxPoolSize(10);
    combo.setAcquireIncrement(5);
    combo.setPreferredTestQuery("SELECT 1 FROM DUAL");
    combo.setTestConnectionOnCheckout(true);
    
    return combo;
  }
  
  @Bean
  public ActiveMQConnectionFactory connectionFactory(){
      ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
      connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
      connectionFactory.setTrustedPackages(Arrays.asList("br.jus.tredf.justicanumeros"));
      return connectionFactory;
  }
  
  @Bean
  public JmsTemplate jmsSaoArquivoExecucaoTemplate(){
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(DEFAULT_SAO_ARQUIVO_EXECUCAO_QUEUE);
      return template;
  }

  @Bean
  public JmsTemplate jmsSaoArquivoDotacaoTemplate(){
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(DEFAULT_SAO_ARQUIVO_DOTACAO_QUEUE);
      return template;
  }
  
  @Bean
  public JmsTemplate jmsSaoArquivoPropOrcamentaria(){
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(DEFAULT_SAO_ARQUIVO_PROP_ORCAMENTARIA);
      return template;
  }  
  
  @Bean
  public JmsTemplate jmsEmailTemplate(){
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(connectionFactory());
      template.setDefaultDestinationName(DEFAULT_EMAIL);
      return template;
  }  
  
  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
      DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
      factory.setConnectionFactory(connectionFactory);
      factory.setConcurrency("1-1");
      return factory;
  }  


  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/estatico/**").addResourceLocations("/WEB-INF/estatico/");
  }
}
