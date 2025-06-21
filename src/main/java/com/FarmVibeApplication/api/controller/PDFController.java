package com.FarmVibeApplication.api.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.IOException;

public class PDFController extends PdfPageEventHelper {

    private Image backgroundImage;

    // ✅ Constructor with image path
    public PDFController(String imagePath) throws IOException, BadElementException {
        backgroundImage = Image.getInstance(imagePath);

        // Adjust size and position as needed
        backgroundImage.scaleToFit(300, 300);  // scale image to 300x300
        backgroundImage.setAbsolutePosition(250, 200); // X and Y position
        backgroundImage.setTransparency(new int[]{0x0F, 0x10}); // light transparency
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.addImage(backgroundImage);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
