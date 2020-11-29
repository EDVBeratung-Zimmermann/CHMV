/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen für die Verwaltung des Carola Hartman Miles-Verlags bereit.
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import static milesVerlagMain.ModulHelferlein.AusgabeBS;
import static milesVerlagMain.ModulHelferlein.AusgabeZ;
import static milesVerlagMain.ModulHelferlein.Linie;
import static milesVerlagMain.ModulHelferlein.Trenner;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;

/**
 *
 * @author spielen
 */
public class briefFlyer {

    private static Connection conn;
    private static Statement SQLAnfrageBuch;
    private static Statement SQLAnfrageAutor;
    private static ResultSet resultBuch;
    private static ResultSet resultAutor;

    private static void FlyerDOC(String buchISBN) {
        ModulHelferlein.Infomeldung("DOC noch nicht implementiert");
    }

    private static void FlyerXLS(String buchISBN) {
        ModulHelferlein.Infomeldung("XLS noch nicht implementiert");
    }

    private static void FlyerPDF(String buchISBN) {
        String outputFileName = ModulHelferlein.pathBuchprojekte + "/" + buchISBN + "/Flyer";

        if (ModulHelferlein.checkDir(outputFileName)) {
            outputFileName = outputFileName + "/Flyer-"
                    + buchISBN
                    + "-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                    + ".pdf";

            try {
                //Modulhelferlein.Infomeldung("PDF noch nicht implementiert");

                // Create a document and add a page to it
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(A4);
                document.addPage(page);

                // Create a new font object selecting one of the PDF base fonts
                PDFont fontPlain = PDType1Font.HELVETICA;
                PDFont fontBold = PDType1Font.HELVETICA_BOLD;
                PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
                PDFont fontUniPlain = PDType0Font.load(document, new File("FreeSans.ttf"));
                PDFont fontUniBold = PDType0Font.load(document, new File("FreeSansBold.ttf"));
                PDFont fontUniOblique = PDType0Font.load(document, new File("FreeSansOblique.ttf"));
                PDFont fontUniBoldOblique = PDType0Font.load(document, new File("FreeSansBoldOblique.ttf"));
                //PDFont fontUniBold = PDType0Font.load(document, new File("LucidaSansUnicode.ttf"));

//        PDFont fontMono = PDType1Font.COURIER;
// Start a new content stream which will "hold" the to be created content
                PDPageContentStream cos;
                cos = new PDPageContentStream(document, page);

                // Set a Font and its Size
                // We cannot use the standard fonts provided.
                // pdPageContentStream.setFont(PDType1Font.HELVETICA, 12);

// Kopfzeile mit Bild
                //try {
                  //  BufferedImage awtImage = ImageIO.read(new File("header-brief.jpg"));
                  //  //PDImageXObject  ximage = new PDPixelMap(document, awtImage);
                  //  PDImageXObject pdImage = PDImageXObject.createFromFile("header-brief.jpg", document);
                  //  float scaley = 0.5f; // alter this value to set the image size
                  //  float scalex = 0.75f; // alter this value to set the image size
                  //  cos.drawImage(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                  //  cos.drawImage(pdImage, 57, 770, 481, pdImage.getHeight() * scaley);
                    //cos.drawXObject(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                // } catch (FileNotFoundException fnfex) {
                  //  System.out.println("No image for you");
                // }

// Fu?zeile
                //Ausgabe(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
                //Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
                //Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
                //Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");

                //Ausgabe(cos, fontBold, 10, Color.GRAY, 230, 35, ModulHelferlein.CheckStr("George-Caylay-Straße 38"));
                //Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 25, "Telefon: +49 (0)30 36 28 86 77");
                //Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 15, "e-Mail: miles-verlag@t-online.de");
                //Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 5, "Internet: www.miles-verlag.jimdo.com");

                //Ausgabe(cos, fontBold, 10, Color.GRAY, 400, 35, "14089 Berlin");
                //Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 25, "Volksbank Berlin");
                //Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 15, "IBAN: DE61 1009 0000 2233 8320 17");
                //Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 5, "BIC: BEVODEBB");

// Kopfzeile
                // Autor
                try {
                    AusgabeZ(cos, fontBold, 16, Color.RED, 57, 740, "NEUERSCHEINUNG " + resultBuch.getString("BUCH_JAHR"), 220);
                    // Autor holen
                    String[] col_Autorliste = resultBuch.getString("BUCH_AUTOR").split(",");
                    String AutorEintrag = "";
                    for (String strAutor : col_Autorliste) {
                        resultAutor = SQLAnfrageAutor.executeQuery(
                                "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                        resultAutor.next();
                        AutorEintrag = AutorEintrag
                                + resultAutor.getString("ADRESSEN_Name") + ", "
                                + resultAutor.getString("ADRESSEN_Vorname") + "; ";
                    }
                    AutorEintrag = AutorEintrag.substring(0, AutorEintrag.length() - 2);
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 57, 700, AutorEintrag, 220);
                    // Titel
                    //String Titel = ModulSilbentrennung.formatText(resultBuch.getString("BUCH_TITEL"), 40);
                    String Titel = ModulSilbentrennung.formatText(resultBuch.getString("BUCH_TITEL"), 220, cos, fontUniBold, 12);
                    String[] SplitTitel = Titel.split("~#!#~");
                    Titel = resultBuch.getString("BUCH_TITEL");
                    int zeile = 670;
                    for (int i = 0; i < SplitTitel.length; i++) {
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 57, zeile - i * 15, SplitTitel[i]);
                    }
                    // Buchdaten
                    // ISBN, Seitenzahl
                    zeile = zeile - 35;
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 57, zeile, "ISBN: " + ModulHelferlein.makeISBN13(resultBuch.getString("BUCH_ISBN"))
                            + ", " + resultBuch.getString("BUCH_SEITEN") + " Seiten");
                    zeile = zeile - 20;
                    // Buchtyp, Preis
                    switch (resultBuch.getInt("Buch_HC")) {
                        case 0:
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 57, 620, resultBuch.getString("BUCH_PREIS") + " Euro, Paperback");
                            break;
                        case 1:
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 57, 620, resultBuch.getString("BUCH_PREIS") + " Euro, Hardcover");
                            break;
                        case 2:
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 57, 620, resultBuch.getString("BUCH_PREIS") + " Euro, E-Book");
                            break;
                    }

// Beschreibung 
                    String Beschreibung = resultBuch.getString("BUCH_BESCHREIBUNG");
                    //Beschreibung = Beschreibung.replace("\\u000A", Trenner);
                    Beschreibung = Beschreibung.replace("\r\n", Trenner);
                    Beschreibung = Beschreibung.replace("\n\r", Trenner);
                    Beschreibung = Beschreibung.replace("\n", Trenner);
                    Beschreibung = Beschreibung.replace("\r", Trenner);
                    
                    //Beschreibung = ModulSilbentrennung.formatText(Beschreibung, 40);
                    Beschreibung = ModulSilbentrennung.formatText(Beschreibung, 220, cos, fontUniPlain, 11);
                    
                    String[] Flyertext = Beschreibung.split(Trenner);
                    
                    zeile = zeile - 40;
                    for (int i = 0; i < Flyertext.length; i++) {
                        //System.out.println("Brief: "+Flyertext[i]);
                        AusgabeBS(cos, fontPlain, 11, Color.BLACK, 57, zeile - i * 16, Flyertext[i], 230);
                    }

// Cover
                    try {
                        BufferedImage awtImage = ImageIO.read(new File(resultBuch.getString("BUCH_COVER_GROSS")));
                        //PDImageXObject  ximage = new PDPixelMap(document, awtImage);
                        PDImageXObject pdImage = PDImageXObject.createFromFile(resultBuch.getString("BUCH_COVER_GROSS"), document);
                        //float scaley = 0.5f; // alter this value to set the image size
                        //float scalex = 0.75f; // alter this value to set the image size
                        //cos.drawImage(pdImage, 312, 450, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                        cos.drawImage(pdImage, 312, 450, 226, 300);
                        //cos.drawXObject(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                    } catch (FileNotFoundException fnfex) {
                        System.out.println("No image for you");
                    }
// Bestellung
                    AusgabeLB(cos, fontBold, 11, Color.BLACK, 315, 410, "Bestellcoupon");
                    AusgabeLB(cos, fontPlain, 11, Color.BLACK, 315, 395, "Hiermit bestelle ich ..... Exemplar(e) des Buches");
                    AusgabeLB(cos, fontPlain, 11, Color.BLACK, 315, 380, "zum Preis von " + resultBuch.getString("BUCH_PREIS") + " Euro je Exemplar.");

                    AusgabeLB(cos, fontBold, 11, Color.BLACK, 315, 350, "Bitte senden Sie Ihre Bestellung an");
                    AusgabeLB(cos, fontPlain, 11, Color.BLACK, 315, 330, "Carola Hartmann Miles Verlag");
                    AusgabeLB(cos, fontPlain, 11, Color.BLACK, 315, 315, "George-Caylay-Straße 38");
                    AusgabeLB(cos, fontPlain, 11, Color.BLACK, 315, 300, "14089 Berlin");
                    AusgabeLB(cos, fontBold, 11, Color.BLACK, 315, 280, "oder per E-Mail an");
                    AusgabeLB(cos, fontPlain, 11, Color.BLACK, 315, 260, "miles-verlag@t-online.de");

// Absender
                    Linie(cos, 2, 312, 57, 538, 57);
                    Linie(cos, 2, 312, 240, 538, 240);
                    Linie(cos, 2, 312, 57, 312, 240);
                    Linie(cos, 2, 538, 57, 538, 240);

                    AusgabeLB(cos, fontBold, 9, Color.BLACK, 315, 230, "Absender");
                    Linie(cos, 1, 315, 200, 535, 200);
                    AusgabeLB(cos, fontBold, 9, Color.BLACK, 315, 190, "Name");
                    Linie(cos, 1, 315, 160, 535, 160);
                    AusgabeLB(cos, fontBold, 9, Color.BLACK, 315, 150, "Straße");
                    Linie(cos, 1, 315, 120, 535, 120);
                    AusgabeLB(cos, fontBold, 9, Color.BLACK, 315, 110, "Postleitzahl, Ort");
                    Linie(cos, 1, 315, 80, 535, 80);
                    AusgabeLB(cos, fontBold, 9, Color.BLACK, 315, 70, "Datum, Unterschrift");

// Make sure that the content stream is closed:
                    cos.close();

// Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    ModulHelferlein.Infomeldung("Flyer ist als PDF gespeichert!");
                    resultBuch.updateString("BUCH_FLYER", outputFileName);
                    resultBuch.updateString("BUCH_TITEL", Titel);
                    System.out.println("Update Link zum Flyer: " + outputFileName);
                    resultBuch.updateRow();
                } catch (SQLException ex) {
                    ModulHelferlein.Fehlermeldung("Flyer erstellen", "SQL-Exception", ex.getMessage());
                }

                try {
                    Runtime.getRuntime().exec("cmd.exe /c " + outputFileName);
                } catch (IOException exept) {
                    ModulHelferlein.Fehlermeldung("IO-Exception: " + exept.getMessage());
                }// try Brief ausgeben

            } catch (IOException ex) {
                ModulHelferlein.Fehlermeldung("IO-Exception: " + ex.getMessage());
            }
        } else {
            System.out.println("Verzeichnis " + outputFileName + " konnte nicht angelegt werden");
        }
    }

    public static void bericht(String buchISBN, String Format) {

        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(ModulHelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            ModulHelferlein.Fehlermeldung("Flyer erstellen", "Datenbanktreiber nicht gefunden: ", exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Flyer erstellen", "Verbindung zur Datenbank nicht moeglich: ", exept.getMessage());
            System.exit(1);
        } // try Verbindung zur Datenbank über die JDBC-Brücke

        if (conn != null) {

            SQLAnfrageBuch = null; // Anfrage erzeugen für resultBuch => Aufbau Bücherliste
            SQLAnfrageAutor = null;

            try { // SQL-Anfragen an die Datenbank
                SQLAnfrageBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLAnfrageAutor = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ISBN='" + buchISBN + "'");
                resultBuch.next();

                switch (Format) {
                    case "PDF":
                        FlyerPDF(buchISBN);
                        break;
                    case "XLS":
                        FlyerXLS(buchISBN);
                        break;
                    default:
                        FlyerDOC(buchISBN);
                        break;
                }
            } catch (SQLException ex) {
                ModulHelferlein.Fehlermeldung("Flyer erstellen", "SQL-Exception", ex.getMessage());
            }
        }
    } // void
}
