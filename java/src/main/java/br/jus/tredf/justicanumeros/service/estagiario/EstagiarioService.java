package br.jus.tredf.justicanumeros.service.estagiario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.jus.tredf.justicanumeros.dao.estagiario.DemonstrativoPagamentoDao;
import br.jus.tredf.justicanumeros.dao.estagiario.EstagiarioDao;
import br.jus.tredf.justicanumeros.model.estagiario.DemonstrativoPagamento;
import br.jus.tredf.justicanumeros.model.estagiario.Estagiario;

@Service(value="EstagiarioService")
public class EstagiarioService {

	@Autowired
	private DemonstrativoPagamentoDao demonstrativoPagamentoDao;
	
	@Autowired
	private EstagiarioDao estagiarioDao;
	
	public List<DemonstrativoPagamento> getDemonstrativos() {
		return demonstrativoPagamentoDao.getDemonstrativos();
	}
	
	public List<Estagiario> getEstagiarios() {
		return estagiarioDao.lerEstagiarioExcel();
	}
}


