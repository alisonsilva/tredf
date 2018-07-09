package br.jus.tredf.justicanumeros.service.impressometro;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.jus.tredf.justicanumeros.model.impressometro.ImpressoraDto;

@Service("SnmpService")
public class SnmpService {

	private static final Logger logger = Logger.getLogger(SnmpService.class);
	
	@Value("${printercounter.snmp.port}")
	private int snmpPort;

	@Value("${printercounter.snmp.timeout}")
	private int snmpTimeout;

	@Value("${printercounter.snmp.community}")
	private String snmpCommunity;

	@Value("${printercounter.snmp.version}")
	private int snmpVersion;

	@Value("${printercounter.snmp.oid_serialnumber}")
	private String oidSerialNumber;
	
	@Value("${printercounter.snmp.oid_model}")
	private String oidModel;

	@Value("${printercounter.snmp.oid_pagecount}")
	private String oidPageCount;

	public ImpressoraDto findPrinter(String ip) {

		ImpressoraDto printer = null;

		CommunityTarget comtarget = new CommunityTarget();
		comtarget.setCommunity(new OctetString(snmpCommunity));
		comtarget.setVersion(snmpVersion);
		comtarget.setAddress(new UdpAddress(ip + "/" + snmpPort));
		comtarget.setRetries(1);
		comtarget.setTimeout(snmpTimeout);

		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oidSerialNumber)));
		pdu.add(new VariableBinding(new OID(oidModel)));
		pdu.add(new VariableBinding(new OID(oidPageCount)));
		pdu.setType(PDU.GET);
		pdu.setRequestID(new Integer32(1));

		TransportMapping transport = null;
		Snmp snmp = null;

		try {
			transport = new DefaultUdpTransportMapping();
			transport.listen();

			snmp = new Snmp(transport);

			ResponseEvent response = snmp.get(pdu, comtarget);

			if (response != null && response.getResponse() != null) {

				PDU responsePDU = response.getResponse();

				int errorStatus = responsePDU.getErrorStatus();

				if (errorStatus == PDU.noError) {

					Vector<?> v = responsePDU.getVariableBindings();

					if (v != null && v.size() > 2) {

						String serialNumber = responsePDU.get(0).toValueString();
						String printerModel = responsePDU.get(1).toValueString();
						String counter = responsePDU.get(2).toValueString();

						if (StringUtils.isNotBlank(serialNumber) && StringUtils.isNotBlank(printerModel)
								&& StringUtils.isNotBlank(counter) && StringUtils.isNumeric(counter)) {

							printer = new ImpressoraDto(ip, printerModel.trim(), serialNumber.trim(), 
									new Date(), Integer.parseInt(counter));

						}
					}
				}
			}

		} catch (IOException e) {
			logger.error(e);

		} finally {
			close(snmp);
			close(transport);
		}

		return printer;
	}

	private void close(TransportMapping transport) {
		try {
			if (transport != null) {
				transport.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void close(Snmp snmp) {
		try {
			if (snmp != null) {
				snmp.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
