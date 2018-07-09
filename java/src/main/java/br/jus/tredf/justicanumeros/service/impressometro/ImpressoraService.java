package br.jus.tredf.justicanumeros.service.impressometro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.jus.tredf.justicanumeros.dao.LogAcoesDao;
import br.jus.tredf.justicanumeros.dao.impressometro.ImpressoraDao;
import br.jus.tredf.justicanumeros.model.impressometro.Impressora;
import br.jus.tredf.justicanumeros.model.impressometro.ImpressoraDto;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Service("ImpressoraService")
public class ImpressoraService {
	
  @Autowired
  private ResourceBundle bundle;
  
  @Autowired
  private LogAcoesDao logAcoesDao;
  
  @Autowired
  private PropertiesServiceController properties;
  
  @Autowired
  private ImpressoraDao impressoraDao;
	
  @Autowired
  private SnmpService snmpService;
  
  public List<ImpressoraDto> coletaInformacoesImpressoras() {
  	List<Impressora> impressoras = impressoraDao.getAllImpressoras();
  	List<ImpressoraDto> impressorasNaRede = new ArrayList<ImpressoraDto>();
  	
  	for(Impressora imp : impressoras) {
  		ImpressoraDto impdto = snmpService.findPrinter(imp.getIpAddress());
  		imp.setImpDetail(impdto);
  		if (impdto != null) {
  			impdto.id = imp.getId();
  			imp.setDetalhes(impdto.modelo);
  			imp.setSerialNumber(impdto.serialNumber);
				impressorasNaRede.add(impdto);
			}
  	}
  	for(Impressora imp : impressoras) {
  		if(imp.getImpDetail() != null) {
  			impressoraDao.addHistImpressao(imp);
  		}
  		if((imp.getImpDetail() != null && !imp.isHabilitado()) || 
  				(imp.getImpDetail() == null && imp.isHabilitado())) {
  			impressoraDao.addHistoricoHabilitado(imp, new Date());
  		}
  	}
  	return impressorasNaRede;
  }
  
}
