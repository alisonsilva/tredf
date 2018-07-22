package br.jus.tredf.oracle.monitoramento;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import br.jus.tredf.oracle.monitoramento.constants.IKafkaConstants;
import br.jus.tredf.oracle.monitoramento.producer.ProducerCreator;

public class OracleLogPublisher {
	public static void main(String[] args) {
		runProducer();
	}
	
	static void runProducer() {
		Producer<String, String> producer = ProducerCreator.createProducer();

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
}
