/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen f�r die Verwaltung des Carola Hartman Miles-Verlags bereit.
 *
 * Copyright (C) 2017 EDV-Beratung und Betrieb, Entwicklung von Software
 *                    Dipl.Inform Thomas Zimmermann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package milesVerlagMain;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import static milesVerlagMain.ModulHelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;

/**
 *
 * @author thoma
 */
public class ModulBriefpapierDrucken {

    public static void PDF() {
        try {
            String outputFileName = ModulHelferlein.pathBerichte + "/Briefpapier"
                    + "-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                    + ".pdf";

            // Create a document and add a page to it
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(A4);
            document.addPage(page);

            // Create a new font object selecting one of the PDF base fonts
            PDFont fontPlain = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;
            PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
//        PDFont fontMono = PDType1Font.COURIER;

// Start a new content stream which will "hold" the to be created content
            PDPageContentStream cos = new PDPageContentStream(document, page);

// Kopfzeile mit Bild
            try {
                            BufferedImage awtImage = ImageIO.read(new File("header-brief.jpg"));
                            //PDImageXObject  ximage = new PDPixelMap(document, awtImage);
                            PDImageXObject pdImage = PDImageXObject.createFromFile("header-brief.jpg", document);
                            float scaley = 0.5f; // alter this value to set the image size
                            float scalex = 0.75f; // alter this value to set the image size
                            cos.drawImage(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                            //cos.drawXObject(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
            } catch (FileNotFoundException fnfex) {
                System.out.println("No image for you");
            }

// Fu?zeile
            AusgabeLB(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");

            AusgabeLB(cos, fontBold, 10, Color.GRAY, 230, 35, ModulHelferlein.CheckStr("Alt Kladow 16d"));
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 25, "Telefon: +49 (0)30 36 28 86 77");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 15, "e-Mail: miles-verlag@t-online.de");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 5, "Internet: www.miles-verlag.jimdo.de");

            AusgabeLB(cos, fontBold, 10, Color.GRAY, 400, 35, "14089 Berlin");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 25, "Volksbank Berlin");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 15, "IBAN: DE61 1009 0000 2233 8320 17");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 5, "BIC: BEV0DEBB");

// Faltmarke, Lochmarke, Faltmarke
            Linie(cos,1,0, 595, 15, 595);
            Linie(cos,1,0, 415, 25, 415);
            Linie(cos,1,0, 285, 15, 285);
            
// Absenderzeile
            Linie(cos,1,50, 749, 297, 749);
            AusgabeLB(cos, fontPlain, 8, Color.BLACK, 50, 751, ModulHelferlein.CheckStr("C. Hartmann Miles-Verlag - Alt Kladow 16d - 14089 Berlin"));

// Datum
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 354, 655, "Datum: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));

// Make sure that the content stream is closed:
            cos.close();

// Save the results and ensure that the document is properly closed:
            document.save(outputFileName);
            document.close();

            try {
                Runtime.getRuntime().exec("cmd.exe /c " + "\""  + outputFileName );
            } catch (IOException exept) {
                ModulHelferlein.Fehlermeldung("Ausgabe Blanko-Brief","IO-Exception: " , exept.getMessage());
            }// try Brief ausgeben
        } // void brief PDF // void brief PDF
        catch (IOException ex) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " + ex.getMessage());
        }
    }
}
