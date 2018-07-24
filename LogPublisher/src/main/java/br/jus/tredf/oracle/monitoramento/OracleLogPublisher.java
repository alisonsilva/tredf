package br.jus.tredf.oracle.monitoramento;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import br.jus.tredf.oracle.monitoramento.constants.IKafkaConstants;
import br.jus.tredf.oracle.monitoramento.model.EnumMachineState;
import br.jus.tredf.oracle.monitoramento.model.InfoLog;
import br.jus.tredf.oracle.monitoramento.model.InfoLogError;
import br.jus.tredf.oracle.monitoramento.model.OraCode;
import br.jus.tredf.oracle.monitoramento.producer.ProducerCreator;
import br.jus.tredf.oracle.monitoramento.util.LogPublisherProperties;
import br.jus.tredf.oracle.monitoramento.zookeeper.IZKManager;
import br.jus.tredf.oracle.monitoramento.zookeeper.ZKMangerImpl;

public class OracleLogPublisher {
  private static final Logger logger = Logger.getLogger(OracleLogPublisher.class);
	
	public static void main(String[] args) {
		runProducer();
	}
	
	static void runProducer() {
		Producer<String, String> producer = ProducerCreator.createProducer();
		IZKManager zkmanager = null;
		String ultimaLinha = null;
		String fileName = null;
	  int ultimaLn = -1;
		try {
			zkmanager = new ZKMangerImpl(LogPublisherProperties.getInstance().getProperty("zookeeper.host"));
			ultimaLinha = zkmanager.getZNodeData(LogPublisherProperties.getInstance().getProperty("zookeeper.dir"), true);
			fileName = LogPublisherProperties.getInstance().getProperty("logfile.name");
		} catch (Exception e1) {
			logger.error("Erro recuperando referência ao ZOOKEEPER: " + e1.getMessage());
			return;
		}
		
		if(ultimaLinha != null) {
			ultimaLn = Integer.parseInt(ultimaLinha);
		}

		try {
			BufferedReader buff = new BufferedReader(new FileReader(new File(fileName)));
			String line = null;
			InfoLog log = null;
			int qtdLn = 0;
			EnumMachineState state = EnumMachineState.START;
			while( (line = buff.readLine()) != null) {				
				if (qtdLn > ultimaLn) {
					Date dt = InfoLog.checkDateFormat(line.substring(0, InfoLog.CUMPRIMENTO_DATA));
					if (dt != null) {
						if (state == EnumMachineState.MESSAGE_GATHERING && log != null) {
							//enviar mensagem
							
							state = EnumMachineState.COLLECTING;
						}
						log = new InfoLog();
						log.data = dt;
					} else if (InfoLog.checkError(line) != null) {
						state = EnumMachineState.MESSAGE_GATHERING;
						log.errors.add(new InfoLogError(line));
					} else if (InfoLog.checkOraMsg(line) != null && state == EnumMachineState.MESSAGE_GATHERING) {
						log.oraCodes.add(new OraCode(OraCode.getOraCode(line), OraCode.getOraMessage(line)));
					} else {
						
					}
				}
				qtdLn++;
			}
			buff.close();
		} catch (FileNotFoundException e1) {
		} catch(IOException e) {			
		}
		
		for (int index = 0; index < IKafkaConstants.MESSAGE_COUNT; index++) {
			final ProducerRecord<String, String> metadata = new ProducerRecord<String, String>(IKafkaConstants.TOPIC_NAME,
			    "key-" + index, "record " + index);
			producer.send(metadata, new Callback() {

				@Override
				public void onCompletion(RecordMetadata recordMetadata, Exception e) {
					if (e != null) {
						System.out.println("Error while producing message to topic :" + recordMetadata);
						e.printStackTrace();
					} else {
						String message = String.format("sent message to topic:%s partition:%s  offset:%s", recordMetadata.topic(),
						    recordMetadata.partition(), recordMetadata.offset());
						System.out.println(message);
					}
				}
			});
		}
	}
	
	private static void readDate(String linha) {
		
	}
}
