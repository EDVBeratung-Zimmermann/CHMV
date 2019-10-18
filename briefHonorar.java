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

//~--- non-JDK imports --------------------------------------------------------
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;

//~--- JDK imports ------------------------------------------------------------
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.xml.transform.TransformerException;

import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.AusgabeDB;
import static milesVerlagMain.Modulhelferlein.AusgabeRB;
import static milesVerlagMain.Modulhelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;

/**
 *
 * @author Thomas Zimmermann
 */
public class briefHonorar {

    static Connection conn = null;

    public static void briefPDF(// Parameterliste
            Integer HONORAR_ZAHLEN, // 0
            //* 0 nichts, 1 Schwelle 1, 2 Schwelle 2
            String HONORAR_TITEL,           // 1
            String HONORAR_AUTOR,           // 2
            String HONORAR_ISBN_PB,         // 3
            Integer HONORAR_ANZAHL_PB,      // 4
            Integer HONORAR_ANZAHL_BOD_PB,  // 5
            Float HONORAR_PREIS_PB,         // 6
            String HONORAR_ISBN_HC,         // 7
            Integer HONORAR_ANZAHL_HC,      // 8
            Integer HONORAR_ANZAHL_BOD_HC,  // 9
            Float HONORAR_PREIS_HC,         // 10
            String HONORAR_ISBN_EB,         // 11
            Integer HONORAR_ANZAHL_EB,      // 12
            Integer HONORAR_ANZAHL_BOD_EB,  // 13
            Float HONORAR_PREIS_EB,         // 14
            Integer HONORAR_ANZAHL_1,       // 15
            Integer HONORAR_PROZENT_1,      // 16
            Integer HONORAR_ANZAHL_2,       // 17
            Integer HONORAR_PROZENT_2,      // 18
            Float HONORAR_MARGE,            // 19
            Float HONORAR_BODPROZENT,       // 20
            Integer HONORAR_BODFIX,          // 21
            Boolean HONORAR_GESAMTBETRACHTUNG, // 22
            Boolean HONORAR_VERTEILEN) {       // 23

        Statement SQLAdresse = null;
        ResultSet resultAdresse = null;
        Float Netto_VP_PB = 0F;
        Float Netto_VP_HC = 0F;
        Float Netto_VP_EB = 0F;
        Float Honorar_PB = 0F;
        Float Honorar_HC = 0F;
        Float Honorar_EB = 0F;
        Double Honorar = 0D;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Brief Rechnung", "ClassNotFound-Exception: Treiber nicht gefunden: ", exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank ?ber die JDBC-Br?cke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Brief Rechnung", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
            System.exit(1);
        } // try Verbindung zur Datenbank ?ber die JDBC-Br?cke

        if (conn != null) {
            try {
                SQLAdresse = conn.createStatement();
                resultAdresse = SQLAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID='" + HONORAR_AUTOR + "'");
                resultAdresse.next();

                System.out.println("Schreibe Honorarabrechnung für " + HONORAR_TITEL);
                String Titel = "";
                Integer Anzahl = 1;
                if (HONORAR_TITEL.contains(" ")) {
                    String[] Titelparts = HONORAR_TITEL.split(" ");
                    Titel = Titelparts[0];
                    while ((Anzahl < Titelparts.length) && (Anzahl < 8)) {
                        Titel = Titel + " " + Titelparts[Anzahl];
                        Anzahl = Anzahl + 1;
                    }
                } else {
                    Titel = HONORAR_TITEL;
                }
                
                // Create a document and add a page to it
                PDDocument document = new PDDocument();
                PDPage page1 = new PDPage(A4);

                document.addPage(page1);

//          PDFont fontMono = PDType1Font.COURIER;
                // Start a new content stream which will "hold" the to be created content
                PDPageContentStream cos;

                //try {
                // Create a new font object selecting one of the PDF base fonts
                // Create a new font object selecting one of the PDF base fonts
                // PDFont fontPlain = PDType1Font.HELVETICA;
                // PDFont fontBold = PDType1Font.HELVETICA_BOLD;
                // PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
                PDFont fontPlain = PDType0Font.load(document, new File("c:\\windows\\fonts\\arial.ttf"));
                PDFont fontBold = PDType0Font.load(document, new File("c:\\windows\\fonts\\arialbd.ttf"));
                PDFont fontItalic = PDType0Font.load(document, new File("c:\\windows\\fonts\\ariali.ttf"));
                PDFont fontBoldItalic = PDType0Font.load(document, new File("c:\\windows\\fonts\\arialbi.ttf"));

                cos = new PDPageContentStream(document, page1);

//              int line = 0;
                // Kopfzeile mit Bild
                BufferedImage awtImage = ImageIO.read(new File("header-brief.jpg"));
                //PDImageXObject  ximage = new PDPixelMap(document, awtImage);
                PDImageXObject pdImage = PDImageXObject.createFromFile("header-brief.jpg", document);
                float scaley = 0.5f; // alter this value to set the image size
                float scalex = 0.75f; // alter this value to set the image size
                cos.drawImage(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                //cos.drawXObject(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);

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
                Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 5, "BIC: BEV0DEBB");

// Faltmarke, Lochmarke, Faltmarke
                Linie(cos, 1, 0, 595, 15, 595);
                Linie(cos, 1, 0, 415, 25, 415);
                Linie(cos, 1, 0, 285, 15, 285);

                // Absenderzeile
                Linie(cos, 1, 50, 749, 297, 749);
                Ausgabe(cos, fontPlain, 8, Color.BLACK, 50, 751,
                        Modulhelferlein.CheckStr("C. Hartmann Miles-Verlag - Alt Kladow 16d - 14089 Berlin"));

                // Datum
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 354, 655,
                        "Datum: " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));

                // Adresse
                String[] AdressZeile = {"", "", "", "", "", "", ""};
                String[] adresse = {"", "", "", "", "", "", ""};
                System.out.println("erzeuge Adresse aus Kundendatenbank");
                adresse[0] = resultAdresse.getString("ADRESSEN_ZUSATZ_1");
                adresse[1] = Modulhelferlein.makeAnrede(resultAdresse.getString("ADRESSEN_NAMENSZUSATZ"),
                        resultAdresse.getString("ADRESSEN_VORNAME"),
                        resultAdresse.getString("ADRESSEN_NAME"));
                adresse[2] = resultAdresse.getString("ADRESSEN_ZUSATZ_2");
                adresse[3] = resultAdresse.getString("ADRESSEN_STRASSE");
                adresse[4] = resultAdresse.getString("ADRESSEN_PLZ") + " " + resultAdresse.getString("ADRESSEN_ORT");
                adresse[5] = resultAdresse.getString("ADRESSEN_ZUSATZ_3");
                Integer ZeilenNr = 1;
                for (int i = 0; i < 6; i++) {
                    if (!adresse[i].equals("")) {
                        AdressZeile[ZeilenNr] = adresse[i];
                        ZeilenNr = ZeilenNr + 1;
                    }
                }
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 730, AdressZeile[1]);
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 715, AdressZeile[2]);
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 700, AdressZeile[3]);
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 685, AdressZeile[4]);
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 670, AdressZeile[5]);
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 655, AdressZeile[6]);

                // Betreff
                Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 575, "Honorarabrechnung des Carola Hartmann Miles-Verlag");
                if (HONORAR_TITEL.length() > 70) {
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 560, "hier: " + HONORAR_TITEL.substring(0, 70) + "...");
                } else {
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 560, "hier: " + HONORAR_TITEL);
                }

                // Anrede
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 515, resultAdresse.getString("ADRESSEN_ANREDE")
                        + " "
                        + Modulhelferlein.makeAnrede(resultAdresse.getString("ADRESSEN_NAMENSZUSATZ"),
                                "",
                                resultAdresse.getString("ADRESSEN_NAME"))
                        + ",");

                // Text
                Integer Startzeile = 0;
                Anzahl =  HONORAR_ANZAHL_PB     + HONORAR_ANZAHL_HC     + HONORAR_ANZAHL_EB
                        + HONORAR_ANZAHL_BOD_PB + HONORAR_ANZAHL_BOD_HC + HONORAR_ANZAHL_BOD_EB;
                Integer Schwelle = 0;
                if (HONORAR_ZAHLEN == 0) { // keine Zahlung - Schwelle 1 nicht erreicht
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 485, "gemäß §4 des Verlagsvertrages erhalten Sie ein Honorar in Höhe " + Integer.toString(HONORAR_PROZENT_1) + " Prozent auf der Basis");
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 470, "des Netto-Ladenverkaufspreises, sofern mehr als " + Integer.toString(HONORAR_ANZAHL_1) + " Exemplare verkauft wurden.");

                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 445, "Leider haben wir im vergangenen Jahr lediglich " + Anzahl.toString() + " Exemplar(e) verkauft. ");
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 430, "Daher kann ich Ihnen in diesem Jahr leider kein Honorar vergüten.");
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 410, "Wir hoffen, dass die Verkaufszahlen dieses Jahr wieder steigen werden.");
                } else {
                    Netto_VP_PB = HONORAR_PREIS_PB / 107 * 100;
                    Netto_VP_HC = HONORAR_PREIS_HC / 107 * 100;
                    Netto_VP_EB = HONORAR_PREIS_EB / 107 * 100;
                    if (HONORAR_ZAHLEN == 1) {
                        Honorar_PB = HONORAR_PREIS_PB / 107 * HONORAR_PROZENT_1;
                        Honorar_HC = HONORAR_PREIS_HC / 107 * HONORAR_PROZENT_1;
                        Honorar_EB = HONORAR_PREIS_EB / 107 * HONORAR_PROZENT_1;
                        Honorar = (HONORAR_ANZAHL_PB * Honorar_PB + HONORAR_ANZAHL_HC * Honorar_HC + HONORAR_ANZAHL_EB * Honorar_EB) * 1D;
                        
                        if (HONORAR_ANZAHL_2 > 0) { // gestaffelt - 2 Schwellen
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 485, "gemäß §4 des Verlagsvertrages erhalten Sie ein gestaffeltes Honorar auf der Basis des");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 470, "Netto-Ladenverkaufspreises in Abhängigkeit der Verkaufserfolge:");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 455, " - " + HONORAR_PROZENT_1.toString() + "% bei mehr als " + HONORAR_ANZAHL_1.toString() + " verkauften Exemplaren");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 440, " - " + HONORAR_PROZENT_2.toString() + "% bei mehr als " + HONORAR_ANZAHL_2.toString() + " verkauften Exemplaren");
                            Startzeile = 420;
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 485, "gemäß §4 des Verlagsvertrages erhalten Sie ein Honorar in Höhe " + HONORAR_PROZENT_1.toString() + " Prozent auf der Basis");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 470, "des Netto-Ladenverkaufspreises, sofern mehr als " + HONORAR_ANZAHL_1.toString() + " Exemplare verkauft wurden.");
                            Startzeile = 450;
                        }
                    } else {
                        Honorar_PB = HONORAR_PREIS_PB / 107 * HONORAR_PROZENT_2;
                        Honorar_HC = HONORAR_PREIS_HC / 107 * HONORAR_PROZENT_2;
                        Honorar_EB = HONORAR_PREIS_EB / 107 * HONORAR_PROZENT_2;
                        Honorar = (HONORAR_ANZAHL_PB * Honorar_PB + HONORAR_ANZAHL_HC * Honorar_HC + HONORAR_ANZAHL_EB * Honorar_EB)*1D;

                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 485, "gemäß §4 des Verlagsvertrages erhalten Sie ein gestaffeltes Honorar auf der Basis des");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 470, "Netto-Ladenverkaufspreises in Abhängigkeit der Verkaufserfolge:");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 455, " - " + HONORAR_PROZENT_1.toString() + "% bei mehr als " + HONORAR_ANZAHL_1.toString() + " verkauften Exemplaren");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 440, " - " + HONORAR_PROZENT_2.toString() + "% bei mehr als " + HONORAR_ANZAHL_2.toString() + " verkauften Exemplaren");
                        Startzeile = 420;
                    }
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, Startzeile, "Eigenbestellungen sind hiervon ausgenommen.");
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, Startzeile-20, "Im vergangenen Jahr wurden insgesamt " + Anzahl.toString() + " Exemplare verkauft.");

                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, Startzeile-45, "Ich freue mich daher, Ihnen ein Honorar in Höhe von " + Modulhelferlein.str2dec(Honorar) + " Euro vergüten zu können.");

                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, Startzeile-70, "Die Abrechnung lautet wie folgt:");
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, Startzeile-90, "ISBN");
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 155, Startzeile-90, "Typ");
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 230, Startzeile-90, "Anzahl");
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 300, Startzeile-90, "Netto-VK");
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 400, Startzeile-90, "Honorar");
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, Startzeile-90, "Gesamt");

                    Linie(cos, 1, 55, Startzeile-93, 540, Startzeile-93);

                    String[] ZeilenInhalt = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
                    Integer Zeile = 0;
                    if (HONORAR_ISBN_PB.length() > 0) { // PB
                        Zeile = Zeile + 1;
                        ZeilenInhalt[(Zeile - 1) * 6 + 0] = HONORAR_ISBN_PB;
                        ZeilenInhalt[(Zeile - 1) * 6 + 1] = "Paperback";
                        ZeilenInhalt[(Zeile - 1) * 6 + 2] = HONORAR_ANZAHL_PB.toString();
                        ZeilenInhalt[(Zeile - 1) * 6 + 3] = Modulhelferlein.str2dec(Netto_VP_PB*1D);
                        ZeilenInhalt[(Zeile - 1) * 6 + 4] = Modulhelferlein.str2dec(Honorar_PB*1D);
                        ZeilenInhalt[(Zeile - 1) * 6 + 5] = Modulhelferlein.str2dec(Honorar_PB*1D*HONORAR_ANZAHL_PB);
                        System.out.println("Zeile " + Zeile.toString() + " - PB");
                    }
                    if (HONORAR_ISBN_HC.length() > 0) { // HC
                        Zeile = Zeile + 1;
                        ZeilenInhalt[(Zeile - 1) * 6 + 0] = HONORAR_ISBN_HC;
                        ZeilenInhalt[(Zeile - 1) * 6 + 1] = "Hardcover";
                        ZeilenInhalt[(Zeile - 1) * 6 + 2] = HONORAR_ANZAHL_HC.toString();
                        ZeilenInhalt[(Zeile - 1) * 6 + 3] = Modulhelferlein.str2dec(Netto_VP_HC*1D);
                        ZeilenInhalt[(Zeile - 1) * 6 + 4] = Modulhelferlein.str2dec(Honorar_HC*1D);
                        ZeilenInhalt[(Zeile - 1) * 6 + 5] = Modulhelferlein.str2dec(Honorar_HC*1D*HONORAR_ANZAHL_HC);
                        System.out.println("Zeile " + Zeile.toString() + " - HC");
                    }
                    if (HONORAR_ISBN_EB.length() > 0) { // EB
                        Zeile = Zeile + 1;
                        ZeilenInhalt[(Zeile - 1) * 6 + 0] = HONORAR_ISBN_EB;
                        ZeilenInhalt[(Zeile - 1) * 6 + 1] = "E-Book";
                        ZeilenInhalt[(Zeile - 1) * 6 + 2] = HONORAR_ANZAHL_EB.toString();
                        ZeilenInhalt[(Zeile - 1) * 6 + 3] = Modulhelferlein.str2dec(Netto_VP_EB*1D);
                        ZeilenInhalt[(Zeile - 1) * 6 + 4] = Modulhelferlein.str2dec(Honorar_EB*1D);
                        ZeilenInhalt[(Zeile - 1) * 6 + 5] = Modulhelferlein.str2dec(Honorar_EB*1D*HONORAR_ANZAHL_EB);
                        System.out.println("Zeile " + Zeile.toString() + " - EB");
                    }

                    for (int i = 1; i <= Zeile; i++) {
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, Startzeile-95 - i * 15, ZeilenInhalt[(i - 1) * 6 + 0]);
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 155, Startzeile-95 - i * 15, ZeilenInhalt[(i - 1) * 6 + 1]);
                        AusgabeRB(cos, fontPlain, 12, Color.BLACK, 260, Startzeile-95 - i * 15, ZeilenInhalt[(i - 1) * 6 + 2]);
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 320, Startzeile-95 - i * 15, ZeilenInhalt[(i - 1) * 6 + 3]);
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 410, Startzeile-95 - i * 15, ZeilenInhalt[(i - 1) * 6 + 4]);
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 500, Startzeile-95 - i * 15, ZeilenInhalt[(i - 1) * 6 + 5]);
                        
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 340, Startzeile-95 - i * 15, "€");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 430, Startzeile-95 - i * 15, "€");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 520, Startzeile-95 - i * 15, "€");
                    }

                    if (HONORAR_VERTEILEN) {
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 210, "Das Ihnen zustehende hälftige Honorar in Höhe "+ Modulhelferlein.str2dec(Honorar/2) + " EURO werden wir auf Ihr Konto");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 195, "mit der IBAN " + resultAdresse.getString("ADRESSEN_IBAN") + ", " + resultAdresse.getString("ADRESSEN_BANK") + " überweisen.");
                    } else {
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 210, "Das Ihnen zustehende fällige Honorar in Höhe "+ Modulhelferlein.str2dec(Honorar) + " EURO werden wir auf Ihr Konto mit der ");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 195, "IBAN " + resultAdresse.getString("ADRESSEN_IBAN") + ", " + resultAdresse.getString("ADRESSEN_BANK") + " überweisen.");
                    }
                }
                // Schlussformel
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 150, Modulhelferlein.CheckStr("Mit freundlichen Grüßen"));
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 85, "Carola Hartmann");
                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 70, "Diplom Kauffrau");

                // Make sure that the content stream is closed:
                cos.close();
                System.out.println("   .. Inhalt geschrieben");

                String outputFileName = Modulhelferlein.pathBerichte + "\\Honorare\\"
                        + "Honorar"
                        + "-"
                        + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                        + "-"
                        + HONORAR_ZAHLEN.toString()
                        + "-"
                        + resultAdresse.getString("ADRESSEN_NAME")
                        + "-"
                        + Titel
                        + ".pdf";
                System.out.println(".. " + outputFileName);

// add XMP metadata
                System.out.println("   .. XMP metaddata schreiben");
                XMPMetadata xmp = XMPMetadata.createXMPMetadata();

                DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
                dc.setTitle(outputFileName);

                PDFAIdentificationSchema id = xmp.createAndAddPFAIdentificationSchema();
                id.setPart(1);
                id.setConformance("B");

                XmpSerializer serializer = new XmpSerializer();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                serializer.serialize(xmp, baos, true);

                PDMetadata metadata = new PDMetadata(document);
                metadata.importXMPMetadata(baos.toByteArray());
                document.getDocumentCatalog().setMetadata(metadata);
                System.out.println("   -> XMP metadata geschrieben");

// sRGB output intent
                System.out.println("   .. sRGB output schreiben");
                //InputStream colorProfile = briefRechnungMahnung.class.getResourceAsStream("/org/apache/pdfbox/resources/pdfa/sRGB.icc");
                InputStream colorProfile = briefRechnungMahnung.class.getResourceAsStream("sRGB.icc");
                PDOutputIntent intent = new PDOutputIntent(document, colorProfile);
                intent.setInfo("sRGB IEC61966-2.1");
                intent.setOutputCondition("sRGB IEC61966-2.1");
                intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
                intent.setRegistryName("http://www.color.org");
                document.getDocumentCatalog().addOutputIntent(intent);
                System.out.println("   -> sRGB output geschrieben");

// Save the results and ensure that the document is properly closed:
                document.save(outputFileName);
                document.close();

                SQLAdresse.close();
                resultAdresse.close();
                System.out.println("-> gespeichert: " + outputFileName);
//                Modulhelferlein.Infomeldung("Honorarabrechnung " + args[1], "ist als PDF gespeichert unter ", outputFileName);
//                try {
//                    Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
//                } catch (IOException exept) {
//                    Modulhelferlein.Fehlermeldung("Honorarabrechnung", "Ausgabe Brief: IO-Exception: ", exept.getMessage());
//                } // try Brief ausgeben
            } catch (IOException ex) {
                Modulhelferlein.Fehlermeldung("Honorar: Brief erstellen", "IO-Exception: ", ex.getMessage());
            } catch (SQLException ex) {
                Modulhelferlein.Fehlermeldung("Honorar: Brief erstellen", "SQL-Exception: ", ex.getMessage());
            } catch (BadFieldValueException e) {
                // won't happen here, as the provided value is valid
                throw new IllegalArgumentException(e);
            } catch (TransformerException ex) {
                Modulhelferlein.Fehlermeldung("PDF/A-Erstellung Honorabrechung", "TransformerException-Exception: " + ex.getMessage());
            }
        } // if conn!= null
    }
}    // class


//~ Formatted by Jindent --- http://www.jindent.com
