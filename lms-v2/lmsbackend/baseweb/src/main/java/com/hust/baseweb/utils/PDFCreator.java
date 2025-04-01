package com.hust.baseweb.utils;
/*
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.FileInputStream;
*/
class ExamAccount{
    public String examUserLogin;
    public String examPassword;
    String email;
    public String fullName;
    public String studentCode;

    public ExamAccount(String examUserLogin, String examPassword, String email, String fullName, String studentCode) {
        this.examUserLogin = examUserLogin;
        this.examPassword = examPassword;
        this.email = email;
        this.fullName = fullName;
        this.studentCode = studentCode;
    }
}
public class PDFCreator {
    public static void main(String[] args){
        /*
        try {
            String dir = "C:\\DungPQ\\teaching\\Applied-algorithms\\HK-2024-2025-1\\thi-cuoi-ky-IT3170E\\";
            InputStream inp = new FileInputStream(dir + "map-tai-khoan-thi-K4.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(inp);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            ArrayList<ExamAccount> lst= new ArrayList<>();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = sheet.getRow(i);
                XSSFCell cell = row.getCell(0);
                String examUserLogin = cell.getStringCellValue();
                cell = row.getCell(1);
                String examPassword = cell.getStringCellValue();
                cell = row.getCell(2);
                String email = cell.getStringCellValue();
                cell = row.getCell(3);
                String fullName = cell.getStringCellValue();
                System.out.println(i + " Fullname " + fullName);
                cell = row.getCell(4);
                String studentCode = cell.getStringCellValue();
                System.out.println(i + " MSSV " + studentCode);
                lst.add(new ExamAccount(examUserLogin,examPassword,email,fullName,studentCode));

            }
            Document pdfdoc = new Document();
            PdfWriter.getInstance(pdfdoc,
                                  new FileOutputStream(dir + "file.pdf"));

            pdfdoc.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Chunk chunk = new Chunk("Hello World", font);
            pdfdoc.add(chunk);

            Chunk chunk1 = new Chunk("Hello World", font);
            pdfdoc.add(chunk);
            pdfdoc.close();

            PDDocument document = new PDDocument();
            //for(int i = 1; i <= 2; i++) {
            for(int i = 0; i < lst.size(); i++){
                ExamAccount ea = lst.get(i);
                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                //PDFont pdfFont = PDType1Font.HELVETICA;
                PDFont pdfFont = PDType1Font.TIMES_ROMAN;
                float fontSize = 18;
                float leading = 1.5f * fontSize;

                PDRectangle mediabox = page.getMediaBox();
                float margin = 72;
                float width = mediabox.getWidth() - 2*margin;
                float startX = mediabox.getLowerLeftX() + margin;
                float startY = mediabox.getUpperRightY() - margin;

                String text = "I am trying to create a PDF file with a lot of text contents in the document. I am using PDFBox";
                ArrayList<String> lines = new ArrayList<String>();
                int lastSpace = -1;
                while (text.length() > 0)
                {
                    int spaceIndex = text.indexOf(' ', lastSpace + 1);
                    if (spaceIndex < 0)
                        spaceIndex = text.length();
                    String subString = text.substring(0, spaceIndex);
                    float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
                    //System.out.printf("'%s' - %f of %f\n", subString, size, width);
                    if (size > width)
                    {
                        if (lastSpace < 0)
                            lastSpace = spaceIndex;
                        subString = text.substring(0, lastSpace);
                        lines.add(subString);
                        text = text.substring(lastSpace).trim();
                        //System.out.printf("'%s' is line\n", subString);
                        lastSpace = -1;
                    }
                    else if (spaceIndex == text.length())
                    {
                        lines.add(text);
                        //System.out.printf("'%s' is line\n", text);
                        text = "";
                    }
                    else
                    {
                        lastSpace = spaceIndex;
                    }
                }

                contentStream.beginText();
                contentStream.setFont(pdfFont, fontSize);
                contentStream.moveTextPositionByAmount(startX, startY);
                contentStream.drawString("Login: " + ea.examUserLogin);
                contentStream.moveTextPositionByAmount(0, -leading);
                contentStream.drawString("Password: " + ea.examPassword);
                contentStream.moveTextPositionByAmount(0, -leading);
                contentStream.drawString("Email: " + ea.email);
                contentStream.moveTextPositionByAmount(0, -leading);
                //String fullName = ea.fullName.replace("\n", "").replace("\r", "");
                //System.out.println("FULLNAME = " + fullName);
                //contentStream.drawString(fullName);
                contentStream.drawString("MSSV: " + ea.studentCode);
                contentStream.moveTextPositionByAmount(0, -leading);

                contentStream.endText();
                contentStream.close();

            }
            document.save(dir + "tai-khoan-exam.pdf");
            document.close();
        }catch (Exception e){
            e.printStackTrace();
        }

         */
    }
}
