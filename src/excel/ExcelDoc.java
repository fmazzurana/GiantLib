package excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelDoc {

	private XSSFWorkbook wb = null;
	
	public ExcelDoc() {

		// creates workbook
		wb = new XSSFWorkbook();
	}
	
	public ExcelSheet addSheet(String sheetName, Boolean gridLines) {
		XSSFSheet s = wb.createSheet(sheetName); 
		s.setDisplayGridlines(gridLines);
		//wb.setSheetName(wb.getNumberOfSheets()-1, sheetName);
		return new ExcelSheet(wb, s);
	}
	
	public void write(String filename) {
		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(new File(filename));
			wb.write(outStream);
			outStream.close();
			wb.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
