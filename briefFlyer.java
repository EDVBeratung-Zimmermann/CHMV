/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.AusgabeZ;
import static milesVerlagMain.Modulhelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

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
        Modulhelferlein.Infomeldung("DOC noch nicht implementiert");
    }

    private static void FlyerXLS(String buchISBN) {
        Modulhelferlein.Infomeldung("XLS noch nicht implementiert");
    }

    private static void FlyerPDF(String buchISBN) {
        try {
            //Modulhelferlein.Infomeldung("PDF noch nicht implementiert");

            String outputFileName = Modulhelferlein.pathBuchprojekte + "/" + buchISBN + "/Flyer/";

            boolean checkDir = Modulhelferlein.checkDir(outputFileName);

            outputFileName = outputFileName + "Flyer-"
                    + buchISBN
                    + "-"
                    + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
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
            PDPageContentStream cos;
            cos = new PDPageContentStream(document, page);

// Kopfzeile mit Bild
            try {
                BufferedImage awtImage = ImageIO.read(new File("header-brief.jpg"));
                //PDImageXObject  ximage = new PDPixelMap(document, awtImage);
                PDImageXObject pdImage = PDImageXObject.createFromFile("header-brief.jpg", document);
                float scaley = 0.5f; // alter this value to set the image size
                float scalex = 0.75f; // alter this value to set the image size
                cos.drawImage(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                cos.drawImage(pdImage, 57, 770, 481, pdImage.getHeight() * scaley);
                //cos.drawXObject(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
            } catch (FileNotFoundException fnfex) {
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

// Kopfzeile
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
                Ausgabe(cos, fontBold, 12, Color.BLACK, 57, 670, resultBuch.getString("BUCH_TITEL"));
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 57, 640, "ISBN: " + resultBuch.getString("BUCH_ISBN")
                        + ", " + resultBuch.getString("BUCH_SEITEN") + " Seiten");
                switch (resultBuch.getInt("Buch_HC")) {
                    case 0:
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 57, 620, resultBuch.getString("BUCH_PREIS") + " Euro, Paperback");
                        break;
                    case 1:
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 57, 620, resultBuch.getString("BUCH_PREIS") + " Euro, Hardcover");
                        break;
                    case 2:
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 57, 620, resultBuch.getString("BUCH_PREIS") + " Euro, E-Book");
                        break;
                }

// Beschreibung ab Zeile 580  bis 57 => 600 => 35 Zeilen á 40 Zeichen
                String Beschreibung = spell.formatText(resultBuch.getString("BUCH_BESCHREIBUNG"), 40);
                String[] Flyertext = Beschreibung.split("~#!#~");

                int zeile = 580;
                for (int i = 0; i < Flyertext.length; i++) {
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 57, 580 - i * 16, Flyertext[i]);
                }

// Cover
                try {
                    BufferedImage awtImage = ImageIO.read(new File(resultBuch.getString("BUCH_COVER")));
                    //PDImageXObject  ximage = new PDPixelMap(document, awtImage);
                    PDImageXObject pdImage = PDImageXObject.createFromFile(resultBuch.getString("BUCH_COVER"), document);
                    //float scaley = 0.5f; // alter this value to set the image size
                    //float scalex = 0.75f; // alter this value to set the image size
                    //cos.drawImage(pdImage, 312, 450, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                    cos.drawImage(pdImage, 312, 450, 226, 300);
                    //cos.drawXObject(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                } catch (FileNotFoundException fnfex) {
                    System.out.println("No image for you");
                }
// Bestellung
                Ausgabe(cos, fontBold, 11, Color.BLACK, 315, 410, "Bestellcoupon");
                Ausgabe(cos, fontPlain, 11, Color.BLACK, 315, 395, "Hiermit bestelle ich ..... Exemplar(e) des Buches");
                Ausgabe(cos, fontPlain, 11, Color.BLACK, 315, 380, "zum Preis von " + resultBuch.getString("BUCH_PREIS") + " Euro je Exemplar.");

                Ausgabe(cos, fontBold, 11, Color.BLACK, 315, 350, "Bitte senden Sie Ihre Bestellung an");
                Ausgabe(cos, fontPlain, 11, Color.BLACK, 315, 330, "Carola Hartmann Miles Verlag");
                Ausgabe(cos, fontPlain, 11, Color.BLACK, 315, 315, "Alt-Kladow 16D");
                Ausgabe(cos, fontPlain, 11, Color.BLACK, 315, 300, "14089 Berlin");
                Ausgabe(cos, fontBold, 11, Color.BLACK, 315, 280, "oder per E-Mail an");
                Ausgabe(cos, fontPlain, 11, Color.BLACK, 315, 260, "miles-verlag@t-online.de");

// Absender
                Linie(cos, 2, 312, 57, 538, 57);
                Linie(cos, 2, 312, 240, 538, 240);
                Linie(cos, 2, 312, 57, 312, 240);
                Linie(cos, 2, 538, 57, 538, 240);

                Ausgabe(cos, fontBold, 9, Color.BLACK, 315, 230, "Absender");
                Linie(cos, 1, 315, 200, 535, 200);
                Ausgabe(cos, fontBold, 9, Color.BLACK, 315, 190, "Name");
                Linie(cos, 1, 315, 160, 535, 160);
                Ausgabe(cos, fontBold, 9, Color.BLACK, 315, 150, "Straße");
                Linie(cos, 1, 315, 120, 535, 120);
                Ausgabe(cos, fontBold, 9, Color.BLACK, 315, 110, "Postleitzahl, Ort");
                Linie(cos, 1, 315, 80, 535, 80);
                Ausgabe(cos, fontBold, 9, Color.BLACK, 315, 70, "Datum, Unterschrift");

// Make sure that the content stream is closed:
                cos.close();

// Save the results and ensure that the document is properly closed:
                document.save(outputFileName);
                document.close();

                Modulhelferlein.Infomeldung("Flyer ist als PDF gespeichert!");
                resultBuch.updateString("BUCH_FLYER", outputFileName);
                resultBuch.updateRow();
            } catch (SQLException ex) {
                Modulhelferlein.Fehlermeldung("Flyer erstellen", "SQL-Exception", ex.getMessage());
            }

            try {
                Runtime.getRuntime().exec("cmd.exe /c " + outputFileName);
            } catch (IOException exept) {
                Modulhelferlein.Fehlermeldung("IO-Exception: " + exept.getMessage());
            }// try Brief ausgeben

        } catch (IOException ex) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + ex.getMessage());
        }

    }

    public static void bericht(String buchISBN, String Format) {

        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Flyer erstellen", "Datenbanktreiber nicht gefunden: ", exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Flyer erstellen", "Verbindung zur Datenbank nicht moeglich: ", exept.getMessage());
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

            } catch (SQLException ex) {
                Modulhelferlein.Fehlermeldung("Flyer erstellen", "SQL-Exception", ex.getMessage());
            }
        };

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
    } // void

}
