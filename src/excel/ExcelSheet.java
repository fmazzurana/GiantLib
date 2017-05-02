package excel;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelSheet {

	public enum cellStyle { NONE, HEADER, TEXT, DATA, CURRENCY, PERCENTAGE };
	
	// Properties
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	
	/**
	 * Constructor
	 * 
	 * @param sheet
	 */
	public ExcelSheet(XSSFWorkbook workbook, XSSFSheet sheet) {
		this.workbook = workbook;
		this.sheet = sheet;
	}
	
	// ------------------------------------------------------------------------
	//  PUBLICS
	// ------------------------------------------------------------------------

	public void addCell(int row, int col, String value, Boolean border, cellStyle style) {
		DataFormat df = workbook.createDataFormat();
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		if (border) {
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);	
			
		}
		switch (style) {
		case HEADER: {
			XSSFFont fBold = workbook.createFont();
			fBold.setBold(true);
			cellStyle.setFont(fBold);
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			cellStyle.setAlignment(HorizontalAlignment.CENTER);		
			}
			break;
		case TEXT:
			cellStyle.setAlignment(HorizontalAlignment.LEFT);
			break;
		case DATA:
			cellStyle.setDataFormat(df.getFormat("dd/MM/yyyy"));
			break;
		case CURRENCY:
			cellStyle.setDataFormat(df.getFormat("+#,##0.00;[Red]-#,##0.00"));
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			break;
		case PERCENTAGE:
			cellStyle.setDataFormat(df.getFormat("0.00%"));
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			break;
		default:
			break;
		}
		XSSFCell cell = getRow(row).createCell(col);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(value);
	}
	
	public void addCellrange(int row, int col, int nrow, int ncol, String value, Boolean border, cellStyle style) {
		addCell(row, col, value, border, style);
		CellRangeAddress cra = new CellRangeAddress(row, row+nrow-1, col, col+ncol-1);
		sheet.addMergedRegion(cra);
	}
	
	// ------------------------------------------------------------------------
	//  PRIVATES
	// ------------------------------------------------------------------------
	private XSSFRow getRow(int rowIdx) {
		XSSFRow row = sheet.getRow(rowIdx);
		if (row == null)
			row = sheet.createRow(rowIdx);
		return row;
	}
	
//	private void setRegionBorder(HSSFSheet sheet, CellRangeAddress cra) {
//		HSSFRegionUtil.setBorderTop(1, cra, sheet, wb);
//		HSSFRegionUtil.setBorderLeft(BorderStyle.THIN.ordinal(), cra, sheet, wb);
//		HSSFRegionUtil.setBorderRight(BorderStyle.THIN.ordinal(), cra, sheet, wb);
//		HSSFRegionUtil.setBorderBottom(BorderStyle.THIN.ordinal(), cra, sheet, wb);
//		HSSFRegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(), cra, sheet, wb); 
//		HSSFRegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(), cra, sheet, wb); 
//		HSSFRegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(), cra, sheet, wb); 
//		HSSFRegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(), cra, sheet, wb); 
//	}
}
