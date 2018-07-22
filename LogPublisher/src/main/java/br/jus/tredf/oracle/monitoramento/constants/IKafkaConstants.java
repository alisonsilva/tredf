package br.jus.tredf.oracle.monitoramento.constants;

public class IKafkaConstants {
	public static String KAFKA_BROKERS = "srv-bi.tre-df.gov.br:6667";	
	public static Integer MESSAGE_COUNT=1000;	
	public static String CLIENT_ID="client_hml";	
	public static String TOPIC_NAME="oracle_homologacao";	
	public static String GROUP_ID_CONFIG="consumerGroup10";	
	public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;	
	public static String OFFSET_RESET_LATEST="latest";	
	public static String OFFSET_RESET_EARLIER="earliest";	
	public static Integer MAX_POLL_RECORDS=1;
}
