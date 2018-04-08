package br.jus.tredf.justicanumeros.dao.estagiario;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.stereotype.Repository;

import com.giaybac.traprange.PDFTableExtractor;
import com.giaybac.traprange.entity.Table;

import br.jus.tredf.justicanumeros.model.estagiario.DemonstrativoPagamento;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Repository("DemonstrativoPagamento")
public class DemonstrativoPagamentoDao {
	public static final int TAMANHO_CPF = 11;
	public static final int FINAL_SEQ_CODIGO = 11;
	
  private static final Logger logger = Logger.getLogger(DemonstrativoPagamentoDao.class);
	

	public List<DemonstrativoPagamento> getDemonstrativos() {
		List<DemonstrativoPagamento> demons = new ArrayList<>();

		PropertyConfigurator.configure(this.getClass().getResource("/log4j.properties"));

		File folder = new File(PropertiesServiceController.getInstance().getProperty("arquivo.demo.pagamento.estagiarios"));
		File[] listOfFiles = folder.listFiles();

		for (int idxf = 0; idxf < listOfFiles.length; idxf++) {		

			if (listOfFiles[idxf].isFile()) {
				PDFTableExtractor extractor = (new PDFTableExtractor()).setSource(listOfFiles[idxf].getAbsoluteFile());
				extractor.exceptLine(0, new int[] { 0, 1, 2, 4, 5, 6, 7, 8, 9, 10, -1 });
				List<Table> tables = extractor.extract();
				String competencia = "";
				for (int idx = 0; idx < tables.size(); idx++) {
					Table table = tables.get(idx);
					StringTokenizer strtoklinhas = new StringTokenizer(table.toString(), "\n");
					int numLinha = 0;
					while (strtoklinhas.hasMoreTokens()) {
						String linha = strtoklinhas.nextToken();
						linha = linha.replace(";", "");
						if (numLinha == 0 && idx == 0) {
							StringTokenizer tok = new StringTokenizer(linha, ":");
							tok.nextToken();
							competencia = tok.nextToken();
							competencia = competencia.replace(";", "");
							competencia = competencia.trim();
						} else if (!linha.startsWith("Seq.") && !linha.startsWith(";")) {
							try {
								DemonstrativoPagamento demonstrativo = new DemonstrativoPagamento();
								if (linha.startsWith("0")) {
									demonstrativo.setCodigo(linha.substring(4, 11));
									String rest = linha.substring(FINAL_SEQ_CODIGO, linha.length());
									rest = rest.replace(";", "");
									
									Matcher matcher = Pattern.compile("\\d+").matcher(rest);
									matcher.find();
									int posNome = matcher.start();
									String nome = rest.substring(0, posNome);
									String cpf = rest.substring(posNome, rest.length());
									demonstrativo.setCpf(cpf.substring(0, TAMANHO_CPF));
									demonstrativo.setNome(nome);
									
									linha = linha.replace(";", "");
									int virgposant = 0;
									int virgpos = linha.indexOf(",", virgposant);
									
									String jornada = linha.substring(virgpos - 5, virgpos - 3);
									String bolsaBase = linha.substring(virgpos - 3, virgpos + 3);
									bolsaBase = bolsaBase.replace(".", "");
									bolsaBase = bolsaBase.replace(",", ".");
									
									virgposant = virgpos;
									virgpos = linha.indexOf(",", virgposant+1);

									String ajuste = linha.substring(virgposant + 3, virgpos + 3);
									ajuste = ajuste.replace(".", "");
									ajuste = ajuste.replace(",", ".");
									
									virgposant = virgpos;
									virgpos = linha.indexOf(",", virgposant+1);
									
									String bolsaAuxilio = linha.substring(virgposant + 3, virgpos + 3);
									bolsaAuxilio = bolsaAuxilio.replace(".", "");
									bolsaAuxilio = bolsaAuxilio.replace(",", ".");
									
									virgposant = virgpos;
									virgpos = linha.indexOf(",", virgposant+1);
									
									String auxilioTransporte = linha.substring(virgposant + 3, virgpos + 3);
									auxilioTransporte = auxilioTransporte.replace(".", "");
									auxilioTransporte = auxilioTransporte.replace(",", ".");
									
									virgposant = virgpos;
									virgpos = linha.indexOf(",", virgposant+1);
									
									String valorPagar = linha.substring(virgposant + 3, virgpos + 3);
									valorPagar = valorPagar.replace(".", ""); 
									valorPagar = valorPagar.replace(",", ".");
									

									demonstrativo.setAuxilioTransporte(Float.parseFloat(auxilioTransporte));
									demonstrativo.setValorPagar(Float.parseFloat(valorPagar));
									demonstrativo.setJornadaEfetiva(Integer.parseInt(jornada));
									demonstrativo.setBolsaBase(Float.parseFloat(bolsaBase));
									demonstrativo.setAjuste(Float.parseFloat(ajuste));
									demonstrativo.setBolsaAuxilio(Float.parseFloat(bolsaAuxilio));
									demonstrativo.setCompetencia(competencia);
									demons.add(demonstrativo);
								} else {
									if (demons.size() > 0) {
										demonstrativo = demons.get(demons.size() - 1);
										if (competencia.equalsIgnoreCase(demonstrativo.getCompetencia()) && !linha.contains("Página")
										    && !linha.contains("Fale Con")
										    && !linha.contains("Empresas ")) {

											StringTokenizer strtok = new StringTokenizer(linha, ";");
											String restoDoNome = strtok.nextToken();

											Matcher matcher = Pattern.compile("\\d+").matcher(restoDoNome);
											matcher.find();
											try {
												int posNome = matcher.start();

												if (posNome > 0) {
													restoDoNome = restoDoNome.substring(0, posNome);
												}
											} catch (Exception e) {
											}

											demonstrativo.setNome(demonstrativo.getNome().trim() + " " + restoDoNome);
										} 
									}
								}
							} catch (Exception e) {
								 logger.error("Erro ao executar operação em arquivo (DemonstrativoPagamentoDao): linha {" + linha + "}", e);
							}
						}
						numLinha++;
					}

				} 
			}

		}
		return demons;
	}
}
