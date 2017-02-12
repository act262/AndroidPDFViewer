package io.micro.itextpdf.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * PDF 工具类
 */
public class PdfUtils {

    public static void writeWaterMark(InputStream inputStream, OutputStream outputStream, String text) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(inputStream);
        PdfStamper stamper = new PdfStamper(reader, outputStream);

        manipulatePdf(reader, stamper, text);
    }

    public static void manipulatePdf(PdfReader reader, PdfStamper stamper, String text) throws IOException, DocumentException {
        int pages = reader.getNumberOfPages();
        // text watermark
        Font f = new Font(Font.FontFamily.HELVETICA, 40, Font.BOLD);
        Phrase p = new Phrase(text, f);
        // transparency
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(0.3f);
        // properties
        PdfContentByte over;
        Rectangle pagesize;
        float x, y;
        // loop over every page
        for (int i = 1; i <= pages; i++) {
            pagesize = reader.getPageSizeWithRotation(i);
            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
            y = (pagesize.getTop() + pagesize.getBottom()) / 2;
            over = stamper.getOverContent(i);
            over.saveState();
            over.setGState(gs1);
            ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 30f);
            over.restoreState();
        }
        stamper.close();
        reader.close();
    }
}
