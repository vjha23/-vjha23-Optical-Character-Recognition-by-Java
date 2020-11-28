package com.pdfscan.Ocrscan;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ExtractTextPdf {

    public static void main(String[] args) throws Exception {
	ExtractTextPdf demo = new ExtractTextPdf();
	demo.run();

    }

    private void run() throws Exception {
	PDDocument document = PDDocument.load(new File("/home/pericent/Downloads/aadhar4.pdf"));
	String text = extractTextFromScannedDocument(document);
	 FileReader reader=new FileReader("db.properties");  
	 Properties p=new Properties();  
	 p.load(reader);  
	 String invoice=p.getProperty("invoice");
	 String email=p.getProperty("email");
	 String aadhar=p.getProperty("aadhar");
	 String dateFormat=p.getProperty("dateFormat");
	 
	 String replacedemail=email.replace('/', '\\');
	 String replaceAadhar=aadhar.replace('/','\\');

	
	 
	 // for checking wether the document is scannable or not
	Boolean typePdf=hasText(document);
	if(typePdf==true) {
		System.out.println("This is Non scannned pdf");
	}else {
		System.out.println("This is Scanned Pdf");
	}
	

	System.out.println("\n");
	String emailMatched=patternMatching(replacedemail,text);
	String invoiceMatched=patternMatching(invoice,text);
	String aadharMatched=patternMatching(replaceAadhar, text);
	String dateMatched=patternMatching(dateFormat, text);
	
	System.out.println(text);
	System.out.println("\n");
	System.out.println("\n");
	
	System.out.println(emailMatched);
	System.out.println("\n");
	System.out.println(invoiceMatched);
	System.out.println("\n");
	System.out.println(aadharMatched);
	System.out.println("\n");
	System.out.println(dateMatched);

	
	
	
	
    }

    private String extractTextFromScannedDocument(PDDocument document) throws IOException, TesseractException {

	// Extract images from file
	PDFRenderer pdfRenderer = new PDFRenderer(document);
	StringBuilder out = new StringBuilder();

	ITesseract _tesseract = new Tesseract();
	_tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
	_tesseract.setLanguage("eng+hin");

	

	for (int page = 0; page < document.getNumberOfPages(); page++) {
	    BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

	    // Create a temp image file
	    File tempFile = File.createTempFile("tempfile_" + page, ".png");
	    ImageIO.write(bufferedImage, "png", tempFile);
	    
	    String result = _tesseract.doOCR(tempFile);
	    out.append(result);

	    // Delete temp file
	    tempFile.delete();

	}

	return out.toString();

    }
    
    
    
    // Method to find document is searachble or not
private boolean hasText(PDDocument doc) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(doc).trim().length() != 0;
    }

// For extracting the substring from the text
private String patternMatching(String str1,String str2) {
	String res="";
	 Pattern pattern = Pattern.compile(str1);
	 Matcher matcher = pattern.matcher(str2);
	 while (matcher.find()) {
         res="find() found substring \"" + matcher.group()
               + "\" starting at index " + matcher.start()
               + " and ending at index " + matcher.end();
      }
	 return res;

}

}