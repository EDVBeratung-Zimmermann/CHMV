/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen für die Verwaltung des Carola Hartman Miles-Verlags bereit.
 *
 * Copyright (C) 2017 EDV-Beratung und Betrieb, Entwicklung von SOftware
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.Linie;
import static milesVerlagMain.Modulhelferlein.Trenner;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class briefBrief {

    public static void brief2PDF(String Adresse, Boolean Anrede, String Datum, String Betreff, String Bezug, String Text) {

        ResultSet resultAA = null; // Adresse Autor

        Statement SQLStatementAA = null;
        Connection conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Treiber nicht gefunden: " + exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Verbindung nicht moeglich: " + exept.getMessage());
            System.exit(1);
        } // try Verbindung zur Datenbank über die JDBC-Brücke

        if (conn != null) {
            try { // Create a document and add a page to it
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(A4);
                document.addPage(page);

                // Create a new font object selecting one of the PDF base fonts
                PDFont fontPlain = PDType1Font.HELVETICA;
                PDFont fontBold = PDType1Font.HELVETICA_BOLD;
                PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
                //PDFont fontMono = PDType1Font.COURIER;
                PDFont fontUniPlain = PDType0Font.load(document, new File("FreeSans.ttf"));
                PDFont fontUniBold = PDType0Font.load(document, new File("FreeSansBold.ttf"));
                PDFont fontUniOblique = PDType0Font.load(document, new File("FreeSansOblique.ttf"));
                PDFont fontUniBoldOblique = PDType0Font.load(document, new File("FreeSansBoldOblique.ttf"));

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
                    Modulhelferlein.Fehlermeldung("Brief Rezension", "File not found-Exception", "Keine Bild-Datei gefunden " + fnfex.getMessage());
                    System.out.println("No image for you");
                }
// Fu?zeile
                Ausgabe(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
                Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
                Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
                Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");

                Ausgabe(cos, fontBold, 10, Color.GRAY, 230, 35, Modulhelferlein.CheckStr("Alt Kladow 16d"));
                Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 25, "Telefon: +49 (0)30 36 28 86 77");
                Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 15, "e-Mail: miles-verlag@t-online.de");
                Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 5, "Internet: www.miles-verlag.jimdo.com");

                Ausgabe(cos, fontBold, 10, Color.GRAY, 400, 35, "14089 Berlin");
                Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 25, "Volksbank Berlin");
                Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 15, "IBAN: DE61 1009 0000 2233 8320 17");
                Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 5, "BIC: BEVODEBB");

// Faltmarke, Lochmarke, Faltmarke
                Linie(cos, 1, 0, 595, 15, 595);
                Linie(cos, 1, 0, 415, 25, 415);
                Linie(cos, 1, 0, 285, 15, 285);

                // Absenderzeile
                Linie(cos, 1, 50, 749, 297, 749);
                Ausgabe(cos, fontPlain, 8, Color.BLACK, 50, 751, Modulhelferlein.CheckStr("C. Hartmann Miles-Verlag - Alt Kladow 16d - 14089 Berlin"));

//helferlein.Infomeldung("schreibe adresse");

// Adresse
                // ADresse holen
                SQLStatementAA = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                resultAA = SQLStatementAA.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + Adresse + "'");
                resultAA.next();

                String[] AdressZeile = {"", "", "", "", "", "", ""};
                String[] args = {"", "", "", "", "", "", ""};

                args[0] = resultAA.getString("ADRESSEN_ZUSATZ_1");
                args[1] = Modulhelferlein.makeAnrede(resultAA.getString("ADRESSEN_NAMENSZUSATZ"),
                        resultAA.getString("ADRESSEN_VORNAME"),
                        resultAA.getString("ADRESSEN_NAME"));
                args[2] = resultAA.getString("ADRESSEN_ZUSATZ_2");
                args[3] = resultAA.getString("ADRESSEN_STRASSE");
                args[4] = resultAA.getString("ADRESSEN_PLZ") + " " + resultAA.getString("ADRESSEN_ORT");
                args[5] = resultAA.getString("ADRESSEN_ZUSATZ_3");

                Integer AdressZeilenNr = 1;
                for (int i = 0; i < 6; i++) {
                    if (!args[i].equals("")) {
                        AdressZeile[AdressZeilenNr] = args[i];
                        AdressZeilenNr = AdressZeilenNr + 1;
                    }
                }
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 730, AdressZeile[1]);
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 715, AdressZeile[2]);
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 700, AdressZeile[3]);
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 685, AdressZeile[4]);
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 670, AdressZeile[5]);
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 655, AdressZeile[6]);
                System.out.println("... Adresse geschrieben");

// Datum
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 354, 655, "Datum: " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                System.out.println("... Datum geschrieben");
// Betreff
                Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 625, Betreff);
                System.out.println("... Betreffzeile geschrieben");
// Bezug
                Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 605, Betreff);
                System.out.println("... Bezugzeile geschrieben");

// Anrede
                if (Anrede) {
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 540, Modulhelferlein.makeAnrede(resultAA.getString("ADRESSEN_ANREDE"), resultAA.getString("ADRESSEN_NAMENSZUSATZ"), resultAA.getString("ADRESSEN_NAME")) + ",");
                }
// Text
                Integer Startzeile = 500;
                Integer ZeilenNr = 1;

                String Beschreibung = Text;
                Beschreibung = Beschreibung.replace("\r\n", Trenner);
                Beschreibung = Beschreibung.replace("\n\r", Trenner);
                Beschreibung = Beschreibung.replace("\r \n", Trenner);
                Beschreibung = Beschreibung.replace("\n \r", Trenner);
                Beschreibung = Beschreibung.replace("\n", Trenner);
                Beschreibung = Beschreibung.replace("\r", Trenner);
                Beschreibung = spell.formatText(Beschreibung, 483, cos, fontUniPlain, 12);

                String[] splitBeschreibung = Beschreibung.split(Trenner);
                for (int i = 0; i < splitBeschreibung.length; i++) {
                    Ausgabe(cos, fontUniPlain, 12, Color.BLACK, 57, Startzeile - 15 * (ZeilenNr - 1), splitBeschreibung[i]);
                    ZeilenNr = ZeilenNr + 1;
                    if (Startzeile - 15 * (ZeilenNr - 1) <= 130) { //neue Seite
                        cos.close();
                        page = new PDPage(A4);
                        document.addPage(page);
                        cos = new PDPageContentStream(document, page);

                        // Fu?zeile
                        Ausgabe(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");

                        Ausgabe(cos, fontBold, 10, Color.GRAY, 230, 35, Modulhelferlein.CheckStr("Alt Kladow 16d"));
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 25, "Telefon: +49 (0)30 36 28 86 77");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 15, "e-Mail: miles-verlag@t-online.de");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 5, "Internet: www.miles-verlag.jimdo.com");

                        Ausgabe(cos, fontBold, 10, Color.GRAY, 400, 35, "14089 Berlin");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 25, "Volksbank Berlin");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 15, "IBAN: DE61 1009 0000 2233 8320 17");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 5, "BIC: BEVODEBB");

// Faltmarke, Lochmarke, Faltmarke
                        Linie(cos, 1, 0, 595, 15, 595);
                        Linie(cos, 1, 0, 415, 25, 415);
                        Linie(cos, 1, 0, 285, 15, 285);

                        Startzeile = 700;
                        ZeilenNr = 0;
                    }
                }

                System.out.println("... Brief geschrieben ");

// Schlussformel
                ZeilenNr = ZeilenNr + 1;
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 5), Modulhelferlein.CheckStr("Mit freundlichen Grüßen"));
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 9), "Carola Hartmann");
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 10), "Diplom Kauffrau");

                String outputFileName = Modulhelferlein.pathUserDir + "\\Briefe"
                        + "\\"
                        + "Brief-"
                        + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                        + "-"
                        + resultAA.getString("ADRESSEN_NAME")
                        + ".pdf";

                // Make sure that the content stream is closed:
//helferlein.Infomeldung(outputFileName);                    
                cos.close();
                document.save(outputFileName);
                document.close();

                Modulhelferlein.Infomeldung("Brief an " + resultAA.getString("ADRESSEN_NAME") + " ist als PDF gespeichert!");
                try {
                    Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                } catch (IOException exept) {
                    Modulhelferlein.Fehlermeldung(
                            "Exception: " + exept.getMessage());
                }// try Brief ausgeben

                // Adressetikett drucken
                String Zeile1 = resultAA.getString("ADRESSEN_ZUSATZ_1");
                String Zeile2 = Modulhelferlein.makeAnrede(resultAA.getString("ADRESSEN_NAMENSZUSATZ"), resultAA.getString("ADRESSEN_VORNAME"), resultAA.getString("ADRESSEN_NAME"));
                String Zeile3 = resultAA.getString("ADRESSEN_ZUSATZ_2");
                String Zeile4 = resultAA.getString("ADRESSEN_STRASSE");
                String Zeile5 = resultAA.getString("ADRESSEN_PLZ") + " " + resultAA.getString("ADRESSEN_ORT");
                String Zeile6 = resultAA.getString("ADRESSEN_ZUSATZ_3");
                String[] argumente = {Zeile1, Zeile2, Zeile3, Zeile4, Zeile5, Zeile6};
                _DlgAdresseDrucken.main(argumente);

            } catch (IOException exept) {
                Modulhelferlein.Fehlermeldung("IO-Exception: " + exept.getMessage());
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            } // try Erstelle Dokument
        } //if conn != null
    }
;

} // class
