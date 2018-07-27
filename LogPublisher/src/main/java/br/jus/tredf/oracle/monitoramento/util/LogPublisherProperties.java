package br.jus.tredf.oracle.monitoramento.util;

import java.io.IOException;
import java.util.Properties;

public class LogPublisherProperties {
	private static LogPublisherProperties LOG_PROPERTIES;
	private Properties props;
	
	private LogPublisherProperties() throws IOException {
		props = new Properties();
		props.load(this.getClass().getResourceAsStream("/configuration.properties"));
	}
	
	public static LogPublisherProperties getInstance() throws IOException {
		if(LOG_PROPERTIES == null) {
			LOG_PROPERTIES = new LogPublisherProperties();
		}
		return LOG_PROPERTIES;
	}
	
	public String getProperty(String property) {
		return props.getProperty(property);
	}
}
