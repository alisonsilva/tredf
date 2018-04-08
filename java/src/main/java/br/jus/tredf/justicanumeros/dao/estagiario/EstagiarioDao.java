package br.jus.tredf.justicanumeros.dao.estagiario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.model.estagiario.Estagiario;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

@Repository("EstagiarioDao")
public class EstagiarioDao {
  private static final Logger logger = Logger.getLogger(EstagiarioDao.class);

	
	public List<Estagiario> lerEstagiarioExcel() {
		List<Estagiario> estagiarios = new ArrayList<Estagiario>();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		
		String nomeArquivo = PropertiesServiceController.getInstance().getProperty("arquivo.controle.estagiarios");		
		nomeArquivo = MessageFormat.format(nomeArquivo, (cal.get(Calendar.YEAR) + "").replace(".", ""));
		File file = new File(nomeArquivo);
		Workbook workbook = null;
		if (file.exists()) {
			try {
				
				FileInputStream excelFile = new FileInputStream(file);
				workbook = new XSSFWorkbook(excelFile);

				Sheet datatypeSheet = workbook.getSheet("Dados Funcionais");
				Iterator<Row> iterator = datatypeSheet.iterator();
				iterator.next();
				while (iterator.hasNext()) {
					Estagiario estagiario = new Estagiario();
					Row currentRow = iterator.next();
					Iterator<Cell> cellIterator = currentRow.iterator();
					Cell currentCell = cellIterator.next();
					estagiario.setTipo(currentCell.getStringCellValue()); 
					currentCell = cellIterator.next();
					if(currentCell.getCellType() == Cell.CELL_TYPE_BLANK) {
						continue;
					}
					if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
						estagiario.setCodigo(currentCell.getStringCellValue());
					} else {
						estagiario.setCodigo(String.valueOf(currentCell.getNumericCellValue()));
					}
					currentCell = cellIterator.next();
					estagiario.setNome(new String(currentCell.getStringCellValue().getBytes(Charset.forName("UTF-8")))); 
					currentCell = cellIterator.next();
					if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						estagiario.setInicio(currentCell.getDateCellValue());
					} else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
						estagiario.setInicio(currentCell.getStringCellValue());
					}
					currentCell = cellIterator.next();
					if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						estagiario.setTermino(currentCell.getDateCellValue());
					} else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
						estagiario.setTermino(currentCell.getStringCellValue());
					}
					currentCell = cellIterator.next();
					if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
						estagiario.setTelefone(currentCell.getStringCellValue());
					}
					cellIterator.next();cellIterator.next();  
					currentCell = cellIterator.next();
					
					estagiario.setNomeSupervisor(new String(currentCell.getStringCellValue().getBytes(Charset.forName("UTF-8")), Charset.forName("Windows-1252"))); 
					currentCell = cellIterator.next();
					estagiario.setEmailSupervisor(currentCell.getStringCellValue()); 
					currentCell = cellIterator.next();
					estagiario.setInstituicaoEnsino(currentCell.getStringCellValue()); 
					cellIterator.next();
					currentCell = cellIterator.next();
					estagiario.setCurso(currentCell.getStringCellValue()); 
					currentCell = cellIterator.next();
					if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
						estagiario.setTelefoneSupervisor(currentCell.getStringCellValue());
					} else {
						estagiario.setTelefoneSupervisor(String.valueOf(currentCell.getNumericCellValue()));
					}
					currentCell = cellIterator.next();
					if(currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
						estagiario.setEmailEstagiario(currentCell.getStringCellValue());
					}
					currentCell = cellIterator.next();
					estagiario.setLotacao(currentCell.getStringCellValue());
					estagiarios.add(estagiario);
				}
				excelFile.close();
			} catch (FileNotFoundException e) {
				logger.error("Erro abrindo arquivo Excel", e);
			} catch (IOException e) {
				logger.error("Erro abrindo arquivo Excel", e);
			} finally {
				if(workbook != null) {
					try {
						workbook.close();
					} catch (IOException e) {
					}
					
				}
			}
		} else {
			logger.error("Arquivo com relação de estagiários não existe: " + nomeArquivo);
		}
		return estagiarios;
	}
}
