package domain;

import java.io.File;
import java.io.IOException;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.szlabsun.wqimc.web.common.utils.PdfUtil;

@SuppressWarnings("all")
public class PDFSample {
	 public static final String DEST = "F://simpleTable.pdf";
	 
	    public static void main(String[] args) throws Exception {
	        File file = new File(DEST);
	        file.getParentFile().mkdirs();
	        myPdf(DEST);
	    }
	 
	    
	    @SuppressWarnings("resource")
		static void myPdf(String dest) throws IOException {
	    	PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
	    	PdfFont sysFont = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H",true);//设置字体、编码
	    	Document doc = new Document(pdf);
	    	doc.setFont(sysFont);
	    	Paragraph title = new Paragraph("XXXXX");
	    	title.setFontSize(20);
	    	title.setTextAlignment(TextAlignment.CENTER);
	    	doc.add(title);
	    	Table table = new Table(6);
	    	table.addCell(getCell(2,3,"XXX：XXXXXXXXXXXXXXX",VerticalAlignment.MIDDLE,TextAlignment.LEFT)); 
	    	table.addCell(getCell(1,3,"XXX：XXXXXXXXXXXXXXX",null,TextAlignment.LEFT)); 
	    	table.addCell(getCell(1,3,"XXX：XXXX-XX-XX",null,TextAlignment.LEFT)); 
	    	
	    	table.addCell(getCell(1, 1, "XXX", null, TextAlignment.CENTER)); 
	    	table.addCell(getCell(1, 2, "XXX", null, TextAlignment.LEFT)); 
	    	table.addCell(getCell(1, 1, "XXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, "XXXXXXXXXXX", null, TextAlignment.LEFT)); 
	    	
	    	table.addCell(getCell(1, 1, "XXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, "XXX", null, TextAlignment.LEFT));
	    	table.addCell(getCell(1, 1, "XXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 4, "XXXXXXXXXXXXXXXXXXXXXX", null, TextAlignment.LEFT));
	    	
	    	//XXXXXX
	    	table.addCell(getCell(1, 2, "XXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, "XXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, "XXXXXXX", null, TextAlignment.CENTER));
	    	
	    	//XXXXXX
	    	table.addCell(getCell(1, 1, "XXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 5, "XX、XX", null, TextAlignment.LEFT)); 
	    	
	    	//XXXXXX
	    	table.addCell(getCell(1, 1, "XXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 5, "X", null, TextAlignment.LEFT));
	    	
	    	//XXXXXX
	    	table.addCell(getCell(1, 1, "XXXX", VerticalAlignment.MIDDLE, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, "X \n XX √", VerticalAlignment.MIDDLE, TextAlignment.LEFT)); 
	    	table.addCell(getCell(1, 1, "XX \n XX √", VerticalAlignment.MIDDLE, TextAlignment.LEFT)); 
	    	table.addCell(getCell(1, 1, "XX \n XX √", VerticalAlignment.MIDDLE, TextAlignment.LEFT));
	    	table.addCell(getCell(1, 2, "XXXXX：X", VerticalAlignment.MIDDLE, TextAlignment.LEFT)); 
	    	
	    	//XXXXXX
	    	table.addCell(getCell(1, 2, "XXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, "XXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, "XXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, "XXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, "XXXX", null, TextAlignment.CENTER));
	    	//XXXXXX
	    	table.addCell(getCell(1, 2, "X", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, "X", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, "X", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, " X", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, " X", null, TextAlignment.CENTER));
	    	
	    	
	    	//XXXXXX
	    	table.addCell(getCell(1, 2, "XXXXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, "XXXXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, "XX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, "XX", null, TextAlignment.CENTER));
	    	//XXXXXX
	    	table.addCell(getCell(1, 2, " X", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, " X", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, " X", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 1, " X", null, TextAlignment.CENTER));
	    	
	    	//XXXXXX
	    	table.addCell(getCell(1, 1, "XXXX(X)", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, "XXXX.XX", null, TextAlignment.LEFT)); 
	    	//XXXXXX
	    	table.addCell(getCell(1, 1, "XXXX(X)", null, TextAlignment.CENTER)); 
	    	table.addCell(getCell(1, 2, "XXX", null, TextAlignment.LEFT)); 
	    	//XXXXXX
	    	table.addCell(getCell(1, 1, "XXXXX", null, TextAlignment.CENTER)); 
	    	table.addCell(getCell(1, 2, "XXX", null, TextAlignment.LEFT)); 
	    	//XXXXXX
	    	table.addCell(getCell(1, 1, "XXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, "XXXX-XX-XX", null, TextAlignment.LEFT)); 
	    	//XXXXXX
	    	table.addCell(getCell(1, 1, "XXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, "XXX", null, TextAlignment.LEFT)); 
	    	//XXXXXX
	    	table.addCell(getCell(1, 1, "XXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, "XXXX-XXXXXXX", null, TextAlignment.LEFT)); 
	    	
	    	//XXXXXX
	    	table.addCell(getCell(1, 1, "XXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, "XX", null, TextAlignment.LEFT)); 
	    	table.addCell(getCell(1, 1, "XXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 2, "XXXX-XX-XX", null, TextAlignment.LEFT)); 
	    	table.addCell(getCell(1, 1, "XXXXX", null, TextAlignment.CENTER));
	    	table.addCell(getCell(1, 5, "XX", null, TextAlignment.LEFT)); 
	    	doc.add(table);
	    	
	    	doc.close();
	    }
	    
	    /**
		* 生成普通单元格
		* @param row 单元格所占行数
		* @param col 单元格所占列数
		* @param text 单元格文本
 		* @param verticalAlign 单元格文本垂直布局
		* @param horizontalAlign 单元格文本水平布局
		* @return cell
		*/
	    private static Cell getCell(int row,int col,String text,VerticalAlignment verticalAlign,TextAlignment textAlign) {
	    	Cell c = new Cell(row,col).add(text); //TODO 合同号
			
	    	if(verticalAlign != null) {
	    		c = c.setVerticalAlignment(verticalAlign);
	    	}
	    	if(textAlign != null) {
	    		c = c.setTextAlignment(textAlign);
	    	}
	    	return c;
	    }
}
