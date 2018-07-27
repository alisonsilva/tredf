package br.jus.tredf.oracle.monitoramento;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

import br.jus.tredf.oracle.monitoramento.model.EnumMachineState;
import br.jus.tredf.oracle.monitoramento.model.IMensagem;
import br.jus.tredf.oracle.monitoramento.model.filespace.FileSystemInfo;
import br.jus.tredf.oracle.monitoramento.model.filespace.InfoRow;
import br.jus.tredf.oracle.monitoramento.model.log.InfoLog;
import br.jus.tredf.oracle.monitoramento.model.log.InfoLogError;
import br.jus.tredf.oracle.monitoramento.model.log.OraCode;
import br.jus.tredf.oracle.monitoramento.producer.ProducerCreator;
import br.jus.tredf.oracle.monitoramento.producer.StreamGobbler;
import br.jus.tredf.oracle.monitoramento.util.LogPublisherProperties;
import br.jus.tredf.oracle.monitoramento.zookeeper.IZKManager;
import br.jus.tredf.oracle.monitoramento.zookeeper.ZKMangerImpl;

public class OracleLogPublisher {
  private static final Logger logger = Logger.getLogger(OracleLogPublisher.class);
	private static Producer<String, String> producer;
	private static IZKManager zkmanager;

	
	public static void main(String[] args) {
		try {
			zkmanager = new ZKMangerImpl(LogPublisherProperties.getInstance().getProperty("zookeeper.host"));
			producer = ProducerCreator.createProducer();
		} catch (Exception e) {
			logger.error("(OracleLogPublisher.main) Erro recuperando referência ao zookeeper ou ao kafka: " + e.getMessage());
			return;
		}
		runProducer();
		runFileMoving();
		runFileSpacingCheck();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
		}
		System.exit(0);
	}
	
	static void runFileSpacingCheck() {
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		final FileSystemInfo info = new FileSystemInfo();
		if(!isWindows) {
			try {
				Process process = Runtime.getRuntime().exec("sh -c df -h");
				StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), new Consumer<String>() {
					
					@Override
					public void accept(String t) {
						if(!t.startsWith("/")) {
							String[] row = t.split("\\s+");
							if(row.length < 7 && row[1] != null && row[1].trim().length() > 0) {
								info.rows.add(new InfoRow(row[1], row[2], row[3], row[4], row[5]));
							}
						}
					}
				});				
				Future ft = Executors.newSingleThreadExecutor().submit(streamGobbler);
				Executors.newSingleThreadExecutor().awaitTermination(5, TimeUnit.SECONDS);
				int exitCode = process.waitFor();
				if(exitCode > 0) {
					System.out.println("Erro executando pesquisa do arquivo");
					System.exit(1);
				} else {
					System.out.println("Bem sucedido");
					Executors.newSingleThreadExecutor().shutdownNow();
					if(ft.isDone()) {
						info.data = new Date();
						
						sendMessage(info, LogPublisherProperties.getInstance().getProperty("kafka.topic.filespace.name"));	
					}
				}
				
			} catch (Exception e) {
			}
		} else {
			System.out.println("Roada apenas em unix");
		}
	}
	
	static void runFileMoving() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat sdfAnoMesDia = new SimpleDateFormat("yyyyMMdd");
			if(zkmanager == null) {
				throw new Exception("Não foi estabelecida conexão com o zookeeper");
			}
			String ultimaData = zkmanager.getZNodeData(LogPublisherProperties.getInstance().getProperty("zookeeper.dir.ultimoarquivo"), true);
			if(ultimaData == null || ultimaData.trim().length() == 0 || ultimaData.trim().equalsIgnoreCase("-1")) {
				zkmanager.update(
						LogPublisherProperties.getInstance().getProperty("zookeeper.dir.ultimoarquivo"), 
						sdf.format(new Date()).getBytes());
				return;
			}
			Date dtUltimaData = sdf.parse(ultimaData);
			Date agora = new Date();
						
			if (sdfAnoMesDia.format(agora).compareTo(sdfAnoMesDia.format(dtUltimaData)) > 0) {
				boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
				String fileDestination = LogPublisherProperties.getInstance().getProperty("logfile.directory.destination");
				String fullFileName = LogPublisherProperties.getInstance().getProperty("logfile.name");
				File f = new File(fullFileName);
				String fileName = f.getName();
				final String newFileName = fileDestination + "/" + fileName + "_" + sdf.format(new Date());
				Process process = null; 
				if(isWindows) {
					String chNewFileName = newFileName.replace('/', '\\');
					fullFileName = fullFileName.replace('/', '\\');
					process = Runtime.getRuntime().exec(String.format("cmd.exe /c move /Y %s %s", fullFileName, chNewFileName));
				} else {
					process = Runtime.getRuntime().exec(String.format("sh -c mv %s %s", fullFileName, newFileName));
				}
				StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), new Consumer<String>() {
					
					@Override
					public void accept(String t) {
						logger.info("Copiando arquivo para destino dos arquivos de log: " + newFileName);
					}
				});
				Executors.newSingleThreadExecutor().submit(streamGobbler);
				int exitCode = process.waitFor();
				if(exitCode != 0) {
					logger.error("(OracleLogPublisher.runFileMoving) Erro movendo arquivo para destino de logs - " + exitCode);
				} else {
					zkmanager.update(
							LogPublisherProperties.getInstance().getProperty("zookeeper.dir.ponteiro"), 
							"-1".getBytes());
				}
			}
			zkmanager.update(LogPublisherProperties.getInstance().getProperty("zookeeper.dir.ultimoarquivo"),
			    sdf.format(new Date()).getBytes());
		} catch (IOException e) {
			logger.error("(OracleLogPublisher.runFileMoving) Erro lendo arquivo de propriedades: " + e.getMessage());
		} catch (Exception e) {
			logger.error("(OracleLogPublisher.runFileMoving) Erro executando operação: " + e.getMessage());
		}
	}
	
	static void runProducer() {
		String ultimaLinha = null;
		String fileName = null;
		int ultimaLn = -1;
		try {
			ultimaLinha = zkmanager.getZNodeData(LogPublisherProperties.getInstance().getProperty("zookeeper.dir.ponteiro"), true);
			fileName = LogPublisherProperties.getInstance().getProperty("logfile.name");
		} catch (Exception e1) {
			logger.error("(OracleLogPublisher.runProducer) Erro recuperando referência ao ZOOKEEPER: " + e1.getMessage());
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
					Date dt = InfoLog.checkDateFormat(line.length() >= InfoLog.CUMPRIMENTO_DATA ? line.substring(0, InfoLog.CUMPRIMENTO_DATA) : null);
					if (dt != null) {
						if (state == EnumMachineState.MESSAGE_GATHERING && log != null) {
							sendMessage(log, LogPublisherProperties.getInstance().getProperty("kafka.topic.name"));							
							state = EnumMachineState.COLLECTING;
						}
						log = new InfoLog();
						log.data = dt;
					} else if (InfoLog.checkError(line) != null) {
						state = EnumMachineState.MESSAGE_GATHERING;
						log.errors.add(new InfoLogError(line));
					} else if (InfoLog.checkOraMsg(line) != null && state == EnumMachineState.MESSAGE_GATHERING) {
						log.oraCodes.add(new OraCode(OraCode.getOraCode(line), OraCode.getOraMessage(line)));
					} else if (state == EnumMachineState.MESSAGE_GATHERING) {
						state = EnumMachineState.COLLECTING;
						sendMessage(log, LogPublisherProperties.getInstance().getProperty("kafka.topic.name"));
					}
				}
				qtdLn++;
			}
			buff.close();
			zkmanager.update(
					LogPublisherProperties.getInstance().getProperty("zookeeper.dir.ponteiro"), 
					String.valueOf(qtdLn).getBytes());
		} catch (FileNotFoundException e1) {
			logger.error("(OracleLogPublisher.runProducer)Erro abrindo arquivo: " + e1.getMessage());
		} catch(IOException e) {		
			logger.error("(OracleLogPublisher.runProducer)Erro escrevendo em arquivo: " + e.getMessage());
		} catch(Exception e) {	
			logger.error("(OracleLogPublisher.runProducer)Erro enviando mensagem: " + e.getMessage());
		}
	}
	
	private static void sendMessage(IMensagem log, final String topicName) {
		Gson gson = new Gson();
		String logJsonRepresentation = gson.toJson(log);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String dtId = sdf.format(log.getDate());
		ProducerRecord<String, String> metadata = 
				new ProducerRecord<String, String>(topicName,
		    dtId, logJsonRepresentation);
		producer.send(metadata, new Callback() {

			@Override
			public void onCompletion(RecordMetadata recordMetadata, Exception e) {
				if (e != null) {
					logger.error("Erro enviando mensagem para broker (topic: " + 
							topicName + "): " + e.getMessage());
				} 
			}
		});
	}
}
