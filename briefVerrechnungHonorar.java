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

import static milesVerlagMain.ModulHelferlein.AusgabeDB;
import static milesVerlagMain.ModulHelferlein.AusgabeRB;
import static milesVerlagMain.ModulHelferlein.Linie;
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
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;

/**
 *
 * @author Thomas Zimmermann
 */
public class briefVerrechnungHonorar {

    private static Connection conn = null;
    private static Statement SQLAdresse = null;
    private static Statement SQLVerrechnung = null;
    private static Statement SQLHonorar = null;
    private static ResultSet resultAdresse = null;
    private static ResultSet resultVerrechnung = null;
    private static ResultSet resultHonorar = null;
    private static Float Netto_VP_PB = 0F;
    private static Float Netto_VP_HC = 0F;
    private static Float Netto_VP_EB = 0F;
    private static Float Honorar_PB = 0F;
    private static Float Honorar_HC = 0F;
    private static Float Honorar_EB = 0F;
    private static Float Honorar = 0F;

    private static Double GesamtBetrag = 0D;
    private static Double GesamtHonorar = 0D;

    private static Integer HONORAR_ZAHLEN; // 0
    //* 0 nichts, 1 Schwelle 1, 2 Schwelle 2
    private static String HONORAR_TITEL; // 1
    private static String HONORAR_ISBN_PB; // 3
    private static Integer HONORAR_ANZAHL_PB; // 4
    private static Float HONORAR_PREIS_PB; // 5
    private static String HONORAR_ISBN_HC; // 6
    private static Integer HONORAR_ANZAHL_HC; // 7
    private static Float HONORAR_PREIS_HC; // 8
    private static String HONORAR_ISBN_EB; // 9
    private static Integer HONORAR_ANZAHL_EB; // 10
    private static Float HONORAR_PREIS_EB; // 11
    private static Integer HONORAR_ANZAHL_1; // 12
    private static Integer HONORAR_PROZENT_1; // 13
    private static Integer HONORAR_ANZAHL_2; // 14
    private static Integer HONORAR_PROZENT_2; // 15

    private static Integer Startzeile = 0;
    private static Integer Honorarzeile = 0;

    public static void briefPDF(String HONORAR_AUTOR) { // 16

        try { // Datenbank-Treiber laden
            Class.forName(ModulHelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            ModulHelferlein.Fehlermeldung("Brief Rechnung", "ClassNotFound-Exception: Treiber nicht gefunden: ", exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank ?ber die JDBC-Br?cke
            conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Brief Honorarverrechnung", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
            System.exit(1);
        } // try Verbindung zur Datenbank ?ber die JDBC-Br?cke

        if (conn != null) {
            try {
                SQLAdresse = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLHonorar = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLVerrechnung = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                resultVerrechnung = SQLVerrechnung.executeQuery("SELECT * FROM TBL_VERRECHNUNG");
                resultHonorar = SQLHonorar.executeQuery("SELECT * FROM TBL_HONORAR WHERE HONORAR_AUTOR_1 = '" + HONORAR_AUTOR + "'");
                resultAdresse = SQLAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID='" + HONORAR_AUTOR + "'");
                resultAdresse.next();

                System.out.println("      => briefVerrechnung.java für " + HONORAR_AUTOR);
                String Titel = "";
                Integer Anzahl = 1;
                //if (HONORAR_TITEL.contains(" ")) {
                //    String[] Titelparts = HONORAR_TITEL.split(" ");
                //    Titel = Titelparts[0];
                //    while ((Anzahl < Titelparts.length) && (Anzahl < 8)) {
                //        Titel = Titel + " " + Titelparts[Anzahl];
                //        Anzahl = Anzahl + 1;
                //    }
                //} else {
                //    Titel = HONORAR_TITEL;
                //}

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
                AusgabeLB(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
                AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
                AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
                AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");
                AusgabeLB(cos, fontBold, 10, Color.GRAY, 230, 35, ModulHelferlein.CheckStr("George-Caylay-Straße 38"));
                AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 25, "Telefon: +49 (0)30 36 28 86 77");
                AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 15, "e-Mail: miles-verlag@t-online.de");
                AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 5, "Internet: www.miles-verlag.jimdo.com");
                AusgabeLB(cos, fontBold, 10, Color.GRAY, 400, 35, "14089 Berlin");
                AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 25, "Volksbank Berlin");
                AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 15, "IBAN: DE61 1009 0000 2233 8320 17");
                AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 5, "BIC: BEVODEBB");

// Faltmarke, Lochmarke, Faltmarke
                Linie(cos, 1, 0, 595, 15, 595);
                Linie(cos, 1, 0, 415, 25, 415);
                Linie(cos, 1, 0, 285, 15, 285);

                // Absenderzeile
                Linie(cos, 1, 50, 749, 297, 749);
                AusgabeLB(cos, fontPlain, 8, Color.BLACK, 50, 751,
                        ModulHelferlein.CheckStr("C. Hartmann Miles-Verlag - George-Caylay-Straße 38 - 14089 Berlin"));

                // Datum
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 354, 655,
                        "Datum: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));

                // Adresse
                String[] AdressZeile = {"", "", "", "", "", "", ""};
                String[] adresse = {"", "", "", "", "", "", ""};
                System.out.println("         .. erzeuge Adresse aus Kundendatenbank");
                adresse[0] = resultAdresse.getString("ADRESSEN_ZUSATZ_1");
                adresse[1] = ModulHelferlein.makeAnrede(resultAdresse.getString("ADRESSEN_NAMENSZUSATZ"),
                        resultAdresse.getString("ADRESSEN_VORNAME"),
                        resultAdresse.getString("ADRESSEN_NAME"));
                adresse[2] = resultAdresse.getString("ADRESSEN_ZUSATZ_2");
                adresse[3] = resultAdresse.getString("ADRESSEN_STRASSE");
                adresse[4] = resultAdresse.getString("ADRESSEN_PLZ") + " " + resultAdresse.getString("ADRESSEN_ORT");
                adresse[5] = resultAdresse.getString("ADRESSEN_ZUSATZ_3");
                System.out.println("            .. " + adresse[1] + ", " + adresse[4]);
                Integer ZeilenNr = 1;
                for (int i = 0; i < 6; i++) {
                    if (!adresse[i].equals("")) {
                        AdressZeile[ZeilenNr] = adresse[i];
                        ZeilenNr = ZeilenNr + 1;
                    }
                }
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 730, AdressZeile[1]);
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 715, AdressZeile[2]);
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 700, AdressZeile[3]);
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 685, AdressZeile[4]);
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 670, AdressZeile[5]);
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 655, AdressZeile[6]);

                // Betreff
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 575, "Honorarabrechnung des Carola Hartmann Miles-Verlag");
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 560, "hier: Verrechnung Buchkäufe und Honorar");

                // Anrede
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 515, resultAdresse.getString("ADRESSEN_ANREDE")
                        + " "
                        + ModulHelferlein.makeAnrede(resultAdresse.getString("ADRESSEN_NAMENSZUSATZ"),
                                "",
                                resultAdresse.getString("ADRESSEN_NAME"))
                        + ",");

                // Text
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "Sie baten um eine Verrechnung mit den von Ihnen getätigten Buchkäufe. ");
                Float Betrag = 0F;
                while (resultVerrechnung.next()) {
                    Betrag = Betrag + resultVerrechnung.getFloat("VERRECHNUNG_BETRAG");
                }
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 455, "Im vergangenen Jahr haben Sie Bücher im Gesamtwert von " + Float.toString(Betrag) + " Euro erworben.");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 440, "Die detaillierte Übersicht der Rechnungen entnehmen Sie bitte der Anlage.");

                GesamtHonorar = 0D;
                SQLHonorar = conn.createStatement();
                resultHonorar = SQLHonorar.executeQuery("SELECT * FROM TBL_HONORAR WHERE HONORAR_AUTOR_1='" + HONORAR_AUTOR + "'");
                while (resultHonorar.next()) {
                    GesamtHonorar = GesamtHonorar + resultHonorar.getFloat("HONORAR_HONORAR") * 1D;
                    HONORAR_ZAHLEN = resultHonorar.getInt("HONORAR_ZAHLEN");
                }
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 410, "Dem gegenüber stehen Honoraransprüche in toto in Höhe von " + ModulHelferlein.str2dec(GesamtHonorar) + " Euro.");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 395, "Die detaillierte Übersicht der Abrechnungen entnehmen Sie bitte der Anlage.");
                
                // Abrechnung
                if (GesamtHonorar - Betrag < 0) {
                    GesamtBetrag = -1D * (GesamtHonorar - Betrag);  // Gesamtbetrag negativ = einzahlen
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 350, "Den Differenzbetrag in Höhe von");
                    AusgabeLB(cos, fontBold, 12, Color.RED, 235, 350, ModulHelferlein.str2dec(GesamtBetrag) + " Euro ");
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 310, 350, "bitten wir auf unser Konto zu überweisen");
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 350 - 15, "bei der     Volksbank Berlin,    IBAN: DE61 1009 0000 2233 8320 17,    BIC: BEVODEBB");
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 350 - 30, "innerhalb der nächsten 14 Tage.");
                } else {
                    GesamtBetrag = 1D * (GesamtHonorar - Betrag);  // Gesamtbetrag positiv = auszahlen  
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 350, "Den Differenzbetrag in Höhe von");
                    AusgabeLB(cos, fontBold, 12, Color.GREEN, 235, 350, ModulHelferlein.str2dec(GesamtBetrag) + " Euro ");
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 310, 350, "überweisen wir auf Ihr Konto bei der");
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 350 - 20, resultAdresse.getString("ADRESSEN_BANK") + " IBAN: " + resultAdresse.getString("ADRESSEN_IBAN"));
                }

                // Schlussformel
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 150, ModulHelferlein.CheckStr("Mit freundlichen Grüßen"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 85, "Carola Hartmann");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 70, "Diplom Kauffrau");

                // Neue Seite mit der detaillierten Aufstellung
                cos.close();

                PDPage page = new PDPage(A4);

                document.addPage(page);
                cos = new PDPageContentStream(document, page);

                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 785, "Anlage");
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 750, "Detaillierte Aufstellung der Rechnungen");

                ZeilenNr = 1;
                resultVerrechnung = SQLVerrechnung.executeQuery("SELECT * FROM TBL_VERRECHNUNG");
                resultVerrechnung.last();
                System.out.println("         .. verrechne " + resultVerrechnung.getRow() + " Rechnungen");
                resultVerrechnung.first();
                while (resultVerrechnung.next()) {
                    System.out.println("            .. Rechnung Nr. " + resultVerrechnung.getString("VERRECHNUNG_ISBN"));
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 740 - 15 * ZeilenNr, "Rechnung Nr. " + resultVerrechnung.getString("VERRECHNUNG_ISBN"));
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 337, 740 - 15 * ZeilenNr, "Betrag:");
                    AusgabeDB(cos, fontPlain, 12, Color.BLACK, 397, 740 - 15 * ZeilenNr, resultVerrechnung.getString("VERRECHNUNG_BETRAG"));
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 425, 740 - 15 * ZeilenNr, "€");
                    Betrag = Betrag + resultVerrechnung.getFloat("VERRECHNUNG_BETRAG");
                    ZeilenNr = ZeilenNr + 1;
                }

                // Make sure that the content stream is closed:
                cos.close();
                System.out.println("      -> Inhalt geschrieben, Content Stream closed");

                String outputFileName = ModulHelferlein.pathBerichte + "\\Honorare\\"
                        + "Verrechnung-Honorar"
                        + "-"
                        + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                        + "-"
                        + HONORAR_ZAHLEN.toString()
                        + "-"
                        + resultAdresse.getString("ADRESSEN_NAME")
                        + "-"
                        + Titel
                        + ".pdf";
                //System.out.println("   -> " + outputFileName);
                System.out.println("      -> PDF/A erzeugen ...");

// add XMP metadata
                System.out.println("         .. XMP metaddata schreiben");
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
                System.out.println("            -> XMP metadata geschrieben");

// sRGB output intent
                System.out.println("         .. sRGB output schreiben");
                //InputStream colorProfile = briefRechnungMahnung.class.getResourceAsStream("/org/apache/pdfbox/resources/pdfa/sRGB.icc");
                InputStream colorProfile = briefRechnungMahnung.class.getResourceAsStream("sRGB.icc");
                PDOutputIntent intent = new PDOutputIntent(document, colorProfile);
                intent.setInfo("sRGB IEC61966-2.1");
                intent.setOutputCondition("sRGB IEC61966-2.1");
                intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
                intent.setRegistryName("http://www.color.org");
                document.getDocumentCatalog().addOutputIntent(intent);
                System.out.println("            -> sRGB output geschrieben");

// Save the results and ensure that the document is properly closed:
                document.save(outputFileName);
                document.close();

                resultAdresse.close();
                resultVerrechnung.close();
                resultHonorar.close();
                SQLAdresse.close();
                SQLHonorar.close();
                System.out.println("      -> PDF gespeichert: ");
                System.out.println("         -> " + outputFileName);
                System.out.println("");
//                ModulHelferlein.Infomeldung("Honorarabrechnung " + args[1], "ist als PDF gespeichert unter ", outputFileName);
//                try {
//                    Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
//                } catch (IOException exept) {
//                    ModulHelferlein.Fehlermeldung("Honorarabrechnung", "AusgabeLB Brief: IO-Exception: ", exept.getMessage());
//                } // try Brief ausgeben
            } catch (IOException ex) {
                System.out.println("      -> IO-Exception: " + ex.getMessage());
                ModulHelferlein.Fehlermeldung("Honorarverrechnung: Brief erstellen", "IO-Exception: ", ex.getMessage());
            } catch (SQLException ex) {
                System.out.println("      -> SQL-Exception: " + ex.getMessage());
                ModulHelferlein.Fehlermeldung("Honorarverrechnung: Brief erstellen", "SQL-Exception: ", ex.getMessage());
            } catch (BadFieldValueException e) {
                // won't happen here, as the provided value is valid
                throw new IllegalArgumentException(e);
            } catch (TransformerException ex) {
                System.out.println("      -> Transformer Exception PDF/A: " + ex.getMessage());
                ModulHelferlein.Fehlermeldung("PDF/A-Erstellung Honorverrechnung", "TransformerException-Exception: " + ex.getMessage());
            }
        } // if conn!= null
    }
}    // class


//~ Formatted by Jindent --- http://www.jindent.com
