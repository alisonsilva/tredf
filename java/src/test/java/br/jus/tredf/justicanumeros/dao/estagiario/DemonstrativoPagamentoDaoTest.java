package br.jus.tredf.justicanumeros.dao.estagiario;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.jus.tredf.justicanumeros.model.estagiario.DemonstrativoPagamento;

public class DemonstrativoPagamentoDaoTest {
	
	@Test
	public void getDemonstrativosTest() {
		DemonstrativoPagamentoDao dao = new DemonstrativoPagamentoDao();
		List<DemonstrativoPagamento> demonstrativos = dao.getDemonstrativos();
		Assert.assertNotNull(demonstrativos);
	}
}
