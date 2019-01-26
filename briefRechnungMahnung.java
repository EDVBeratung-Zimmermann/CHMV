/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen f�r die Verwaltung des Carola Hartman Miles-Verlags bereit.
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.xml.transform.TransformerException;
import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.AusgabeDB;
import static milesVerlagMain.Modulhelferlein.AusgabeLeistung;
import static milesVerlagMain.Modulhelferlein.Linie;
import static milesVerlagMain.Modulhelferlein.SpracheAnrede;
import static milesVerlagMain.Modulhelferlein.SpracheAnzahl;
import static milesVerlagMain.Modulhelferlein.SpracheAutor;
import static milesVerlagMain.Modulhelferlein.SpracheBelegexemplar;
import static milesVerlagMain.Modulhelferlein.SpracheBeruf;
import static milesVerlagMain.Modulhelferlein.SpracheBestelldatum;
import static milesVerlagMain.Modulhelferlein.SpracheBestellzeichen;
import static milesVerlagMain.Modulhelferlein.SpracheEULieferung;
import static milesVerlagMain.Modulhelferlein.SpracheEinMahnung1;
import static milesVerlagMain.Modulhelferlein.SpracheEinMahnung2;
import static milesVerlagMain.Modulhelferlein.SpracheEinleitung1;
import static milesVerlagMain.Modulhelferlein.SpracheEinleitung2;
import static milesVerlagMain.Modulhelferlein.SpracheEinzelpreis;
import static milesVerlagMain.Modulhelferlein.SpracheGesamtVersand;
import static milesVerlagMain.Modulhelferlein.SpracheGesamtpreis;
import static milesVerlagMain.Modulhelferlein.SpracheGeschenk;
import static milesVerlagMain.Modulhelferlein.SpracheGruss;
import static milesVerlagMain.Modulhelferlein.SpracheMahnung;
import static milesVerlagMain.Modulhelferlein.SpracheNetto;
import static milesVerlagMain.Modulhelferlein.SprachePflichtexempar;
import static milesVerlagMain.Modulhelferlein.SpracheRechnung;
import static milesVerlagMain.Modulhelferlein.SpracheRechnungsdatum;
import static milesVerlagMain.Modulhelferlein.SpracheRechnungsnummer;
import static milesVerlagMain.Modulhelferlein.SpracheReverseCharge;
import static milesVerlagMain.Modulhelferlein.SpracheRezensionsexemplar;
import static milesVerlagMain.Modulhelferlein.SpracheSchluss1;
import static milesVerlagMain.Modulhelferlein.SpracheSchluss21;
import static milesVerlagMain.Modulhelferlein.SpracheSchluss22;
import static milesVerlagMain.Modulhelferlein.SpracheSchluss3;
import static milesVerlagMain.Modulhelferlein.SpracheSteuerfrei1;
import static milesVerlagMain.Modulhelferlein.SpracheSteuerfrei2;
import static milesVerlagMain.Modulhelferlein.SpracheStorniert;
import static milesVerlagMain.Modulhelferlein.SpracheRemittende;
import static milesVerlagMain.Modulhelferlein.SpracheTitel;
import static milesVerlagMain.Modulhelferlein.SpracheUmsatzsteuer;
import static milesVerlagMain.Modulhelferlein.SpracheUstrID;
import static milesVerlagMain.Modulhelferlein.SpracheVerrechnung1;
import static milesVerlagMain.Modulhelferlein.SpracheVerrechnung2;
import static milesVerlagMain.Modulhelferlein.SpracheVerrechnung3;
import static milesVerlagMain.Modulhelferlein.SpracheVersanddatum;
import static milesVerlagMain.Modulhelferlein.SpracheVersandkosten;
import static milesVerlagMain.Modulhelferlein.SpracheAnlage1;
import static milesVerlagMain.Modulhelferlein.SpracheAnlage2;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
//import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
//import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
//import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;

public class briefRechnungMahnung {

    private static Connection conn;
    private static Statement SQLAnfrage;
    private static Statement SQLAnfrageK;
    private static Statement SQLAnfrageA;
    private static Statement SQLAnfrageBD;
    private static Statement SQLAnfrageBuch;
    private static ResultSet result;
    private static ResultSet resultK;
    private static ResultSet resultA;
    private static ResultSet resultBD;
    private static ResultSet resultBuch;

    private static JFrame Dialog = new JFrame("Carola Hartmann Miles Verlag");
    private static JLabel dlgLabel = new JLabel("");
    private static JPanel dlgPanel;
    private static JScrollPane dlgScrollpane;
    private static JTextArea dlgTextarea;

    private static String Zeile1 = "";
    private static String Zeile2 = "";
    private static String Zeile3 = "";
    private static String Zeile4 = "";
    private static String Zeile5 = "";
    private static String Zeile6 = "";

    private static void Bildschirmausgabe(String zeile) {
        dlgTextarea.append(zeile + "\r\n");
        dlgTextarea.update(dlgTextarea.getGraphics());
    }

    public static void briefDOC(String RechNr, Integer Typ, String MahnNr) throws Exception {

        String outputFileName = Modulhelferlein.pathRechnungen + "\\Einnahmen"
                + "\\Rechnung"
                + "-"
                + "CHMV"
                + "-"
                + RechNr
                + "-"
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                + "-";

        String ISBN = "";

        Dialog.setSize(600, 500);
        Dialog.setLocationRelativeTo(null);
        Dialog.setResizable(false);

        dlgPanel = new JPanel();
        dlgPanel.setBounds(10, 40, 580, 440);

        dlgLabel.setBounds(10, 10, 580, 25);
        dlgPanel.add(dlgLabel);

        dlgTextarea = new JTextArea(5, 50);
        dlgTextarea.setBounds(10, 40, 580, 440);
        dlgTextarea.setLineWrap(true);
        dlgTextarea.setWrapStyleWord(true);
        dlgTextarea.setFont(new Font("Monospaced", Font.BOLD, 12));
        dlgTextarea.setForeground(Color.WHITE);
        dlgTextarea.setBackground(Color.BLACK);

        dlgScrollpane = new JScrollPane(dlgTextarea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dlgScrollpane.setPreferredSize(new Dimension(580, 440));
        dlgPanel.add(dlgScrollpane);

        Dialog.add(dlgPanel);
        dlgLabel.setText("Erzeuge " + outputFileName);
        Dialog.setVisible(true);

        Integer twipsPerInch = 1440;

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
            SQLAnfrage = null; // Anfrage erzeugen    Bestellungen
            SQLAnfrageK = null; // Anfrage erzeugen    Kundenadresse
            SQLAnfrageA = null; // Anfrage erzeugen    Adresse
            SQLAnfrageBD = null; // Anfrage erzeugen    Bestellung Details
            SQLAnfrageBuch = null; // Anfrage erzeugen   Buch

            try { // SQL-Anfragen an die Datenbank
                SQLAnfrage = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageK = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageA = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageBD = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen

                // Bestellung lesen
                result = SQLAnfrage.executeQuery("SELECT * FROM TBL_BESTELLUNG WHERE BESTELLUNG_RECHNR = '" + RechNr + "'");
                result.next();

                // Kunde lesen
                if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                    resultK = SQLAnfrageK.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + Integer.toString(result.getInt("BESTELLUNG_KUNDE")) + "'");
                    resultK.next();
                }

                // Create a document and add a page to it
                try ( //Blank Document
                        XWPFDocument document = new XWPFDocument()) {
                    // create header-footer
                    XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
                    if (headerFooterPolicy == null) {
                        headerFooterPolicy = document.createHeaderFooterPolicy();
                    }
                    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
                    CTPageMar pageMar = sectPr.addNewPgMar();
                    pageMar.setLeft(BigInteger.valueOf(1152));
                    pageMar.setTop(BigInteger.valueOf(288));
                    pageMar.setRight(BigInteger.valueOf(1152));
                    pageMar.setBottom(BigInteger.valueOf(0));
                    pageMar.setFooter(BigInteger.valueOf(144));

                    // create header start
                    Bildschirmausgabe("... erzeuge Textblock Kopfzeile ...");
                    XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
                    XWPFParagraph paragraphHeader = document.createParagraph();
                    XWPFRun runHeader = paragraphHeader.createRun();
                    paragraphHeader = header.createParagraph();
                    paragraphHeader.setAlignment(ParagraphAlignment.CENTER);
                    Path imagePath = Paths.get("header-brief.jpg");
                    runHeader = paragraphHeader.createRun();
                    runHeader.addPicture(Files.newInputStream(imagePath),
                            XWPFDocument.PICTURE_TYPE_PNG,
                            imagePath.getFileName().toString(), Units.toEMU(500), Units.toEMU(50));

                    // Adresse
                    Bildschirmausgabe("... erzeuge Textblock Adresse ...");
                    XWPFTable table = document.createTable(1, 4);
                    CTTblLayoutType type = table.getCTTbl().getTblPr().addNewTblLayout();
                    type.setType(STTblLayoutType.FIXED);
                    table.getCTTbl().getTblPr().unsetTblBorders();
                    table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(5000));
                    table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(576));
                    table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(2400));
                    table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(2400));

                    XWPFTableRow rows = null;
                    XWPFTableCell cell = null;
                    XWPFParagraph paragraph;
                    XWPFRun run;

                    rows = table.getRow(0);
                    cell = rows.getCell(0);
                    paragraph = cell.getParagraphs().get(0);
                    run = paragraph.createRun();
                    run.setFontSize(9);
                    run.setUnderline(UnderlinePatterns.SINGLE);
                    run.setText("Carola Hartmann Miles Verlag - George-Cylay Str. 38 - 14089 Berlin     ");

                    //Adressblock Zeile 1 - 6
                    paragraph = cell.addParagraph();
                    run = paragraph.createRun();
                    run.setFontFamily("Arial");
                    run.setFontSize(12);
                    if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                        run.setText(resultK.getString("ADRESSEN_ZUSATZ_1"));
                        run.addBreak();
                        if (resultK.getString("ADRESSEN_NAMENSZUSATZ").equals("")) {
                            run.setText(resultK.getString("ADRESSEN_VORNAME") + " " + resultK.getString("ADRESSEN_NAME"));
                        } else {
                            run.setText(resultK.getString("ADRESSEN_NAMENSZUSATZ") + " " + resultK.getString("ADRESSEN_VORNAME") + " " + resultK.getString("ADRESSEN_NAME"));
                        }

                        run.addBreak();
                        run.setText(resultK.getString("ADRESSEN_ZUSATZ_2"));
                        run.addBreak();
                        run.setText(resultK.getString("ADRESSEN_ZUSATZ_3"));
                        run.addBreak();
                        run.setText(resultK.getString("ADRESSEN_STRASSE"));
                        run.addBreak();
                        run.setText(resultK.getString("ADRESSEN_PLZ") + " " + resultK.getString("ADRESSEN_ORT"));
                        run.addBreak();
                    } else {
                        run.setText(result.getString("BESTELLUNG_ZEILE_1"));
                        run.addBreak();
                        run.setText(result.getString("BESTELLUNG_ZEILE_2"));
                        run.addBreak();
                        run.setText(result.getString("BESTELLUNG_ZEILE_3"));
                        run.addBreak();
                        run.setText(result.getString("BESTELLUNG_ZEILE_4"));
                        run.addBreak();
                        run.setText(result.getString("BESTELLUNG_ZEILE_5"));
                        run.addBreak();
                        run.setText(result.getString("BESTELLUNG_ZEILE_6"));
                        run.addBreak();
                    } // if

                    cell = rows.getCell(2);
                    paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);
                    run = paragraph.createRun();
                    run.setFontFamily("Arial");
                    run.setFontSize(12);
                    run.setBold(true);
                    run.setText("  ");
                    run.addBreak();
                    run.setBold(true);
                    if ((result.getInt("BESTELLUNG_LAND") == 20) || (result.getInt("BESTELLUNG_LAND") == 10) || (result.getInt("BESTELLUNG_LAND") == 0)) {
                        switch (Typ) {
                            case 0:
                                run.setText("Rechnung");
                                break;
                            case 1:
                                run.setText("Rezensionsexemplar");
                                break;
                            case 2:
                                run.setText("Pflichtexemplar");
                                break;
                            case 3:
                                run.setText("Geschenkexemplar");
                                break;
                        }
                    } else {
                        run.setBold(true);
                        switch (Typ) {
                            case 0:
                                run.setText("Order");
                                break;
                            case 1:
                                run.setText("Review");
                                break;
                            case 2:
                                run.setText("");
                                break;
                            case 3:
                                run.setText("Gift");
                                break;
                        }
                    }
                    run.addBreak();
                    run.setText(" ");
                    run.addBreak();
                    if ((result.getInt("BESTELLUNG_LAND") == 20) || (result.getInt("BESTELLUNG_LAND") == 10) || (result.getInt("BESTELLUNG_LAND") == 0)) {
                        run.setText("RechnNr: ");
                        run.addBreak();
                        run.setText("RechnDat: ");
                        run.addBreak();
                        run.addBreak();
                        run.setBold(false);
                        run.setText("Rechnungsdatum = ");
                    } else {
                        run.setText("Invoice No.: ");
                        run.addBreak();
                        run.setText("Date of Invoice: ");
                        run.addBreak();
                        run.addBreak();
                        run.setBold(false);
                        run.setText("Date of Shipment =");
                    }

                    cell = rows.getCell(3);
                    paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.LEFT);
                    run = paragraph.createRun();
                    run.setFontFamily("Arial");
                    run.setFontSize(12);
                    run.setText("  ");
                    run.addBreak();
                    run.setText(" ");
                    run.addBreak();
                    run.addBreak();
                    run.setText(" ");
                    run.setText(RechNr);
                    run.addBreak();
                    run.setText(Modulhelferlein.printDateFormat("dd.MM.yyyy", Modulhelferlein.SQLDate2Date(result.getDate("BESTELLUNG_RECHDAT"))));
                    run.addBreak();
                    run.addBreak();
                    run.setBold(false);
                    if ((result.getInt("BESTELLUNG_LAND") == 20) || (result.getInt("BESTELLUNG_LAND") == 10) || (result.getInt("BESTELLUNG_LAND") == 0)) {
                        run.setText("Versanddatum");
                    } else {
                        run.setText("Date of Invoice");
                    }
                    run.addBreak();
                    run.setText(" ");

                    // Betreffzeile
                    Bildschirmausgabe("... erzeuge Textblock Betreffzeile ...");
                    XWPFTable tableBetreff = document.createTable(1, 2);
                    type = tableBetreff.getCTTbl().getTblPr().addNewTblLayout();
                    type.setType(STTblLayoutType.FIXED);
                    tableBetreff.getCTTbl().getTblPr().unsetTblBorders();
                    tableBetreff.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(5000));
                    tableBetreff.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(5000));

                    rows = tableBetreff.getRow(0);
                    cell = rows.getCell(0);
                    XWPFParagraph paragraphBetreff = cell.getParagraphs().get(0);
                    paragraphBetreff.setAlignment(ParagraphAlignment.LEFT);
                    XWPFRun runBetreff = paragraphBetreff.createRun();
                    runBetreff.setFontFamily("Arial");
                    runBetreff.setFontSize(12);
                    runBetreff.setBold(true);

                    // Betreff
                    if ((result.getInt("BESTELLUNG_LAND") == 20) || (result.getInt("BESTELLUNG_LAND") == 10) || (result.getInt("BESTELLUNG_LAND") == 0)) {
                        runBetreff.setText("Ihre Buchbestellung vom "
                                + Modulhelferlein.printDateFormat("dd.MM.yyyy",
                                        Modulhelferlein.SQLDate2Date(result.getDate("BESTELLUNG_DATUM"))));
                    } else {
                        runBetreff.setText("Your order from "
                                + Modulhelferlein.printDateFormat("dd.MM.yyyy",
                                        Modulhelferlein.SQLDate2Date(result.getDate("BESTELLUNG_DATUM"))));
                    }

                    cell = rows.getCell(1);
                    paragraphBetreff = cell.getParagraphs().get(0);
                    paragraphBetreff.setAlignment(ParagraphAlignment.LEFT);
                    runBetreff = paragraphBetreff.createRun();
                    runBetreff.setFontFamily("Arial");
                    runBetreff.setFontSize(12);
                    runBetreff.setBold(true);

                    if ((result.getInt("BESTELLUNG_LAND") == 20) || (result.getInt("BESTELLUNG_LAND") == 10) || (result.getInt("BESTELLUNG_LAND") == 0)) {
                        runBetreff.setText("Ihr Bestellzeichen " + result.getString("BESTELLUNG_BESTNR"));
                        runBetreff.addBreak();
                        if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                            runBetreff.setText("Ihre UStr-IDNr " + resultK.getString("ADRESSEN_USTR_ID"));
                        } else {
                            runBetreff.setText("Ihre UStr-IDNr " + result.getString("BESTELLUNG_USTR_ID"));
                        }
                    } else {
                        runBetreff.setText("Your Sign of order " + result.getString("BESTELLUNG_BESTNR"));
                        runBetreff.addBreak();
                        if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                            runBetreff.setText("Your VAT-TaxNo " + resultK.getString("ADRESSEN_USTR_ID"));
                        } else {
                            runBetreff.setText("Your VAT-TaxNo " + result.getString("BESTELLUNG_USTR_ID"));
                        }
                    }

                    //Anrede
                    Bildschirmausgabe("... erzeuge Textblock Anrede ...");
                    XWPFParagraph paragraphAnrede = document.createParagraph();
                    paragraphAnrede.setAlignment(ParagraphAlignment.LEFT);
                    XWPFRun runAnrede = paragraphAnrede.createRun();
                    runAnrede.addBreak();
                    runAnrede.addBreak();
                    if ((result.getInt("BESTELLUNG_LAND") == 20) || (result.getInt("BESTELLUNG_LAND") == 10) || (result.getInt("BESTELLUNG_LAND") == 0)) {
                        if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                            if (resultK.getString("ADRESSEN_ANREDE").equals("")) {
                                runAnrede.setText("Sehr geehrte Damen und Herren,");
                            } else {
                                // String POC[] = resultK.getString("ADRESSEN_POC").split(" ");
                                // Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 540, resultK.getString("ADRESSEN_ANREDE") + " " + POC[1] + ",");
                                runAnrede.setText(resultK.getString("ADRESSEN_ANREDE") + " " + resultK.getString("ADRESSEN_NAMENSZUSATZ") + " " + resultK.getString("ADRESSEN_NAME") + ",");
                            } // if
                        } else {
                        }
                    } else {
                        if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                            if (resultK.getString("ADRESSEN_ANREDE").equals("")) {
                                runAnrede.setText("Dear Madam, dear Sir,");
                            } else {
                                // String POC[] = resultK.getString("ADRESSEN_POC").split(" ");
                                // Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 540, resultK.getString("ADRESSEN_ANREDE") + " " + POC[1] + ",");
                                runAnrede.setText(resultK.getString("ADRESSEN_ANREDE") + " " + resultK.getString("ADRESSEN_NAMENSZUSATZ") + " " + resultK.getString("ADRESSEN_NAME") + ",");
                            } // if
                        } else {
                        }
                    }

                    // Text
                    //create paragraph
                    XWPFParagraph paragraphText = document.createParagraph();
                    paragraphText.setAlignment(ParagraphAlignment.LEFT);
                    XWPFRun runText = paragraphText.createRun();
                    runText.setFontSize(12);
                    if ((result.getInt("BESTELLUNG_LAND") == 20) || (result.getInt("BESTELLUNG_LAND") == 10) || (result.getInt("BESTELLUNG_LAND") == 0)) {
                        runText.setText("f�r Ihre Bestellung danken wir Ihnen sehr herzlich und w�rden uns freuen, wenn Sie uns einmal auf unserer Homepage www.miles-verlag.jimdo.com besuchen.");
                    } else {
                        runText.setText("thank you very much for your order.");
                        runBetreff.addBreak();
                    }

                    // Bestellung
                    // Tabelle mit 1 Zeile, 4 Spalten
                    XWPFTable tableBestellung = document.createTable(1, 4);
                    //Spaltenbreite festlegen
                    type = tableBestellung.getCTTbl().getTblPr().addNewTblLayout();
                    type.setType(STTblLayoutType.FIXED);
                    tableBestellung.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(710));
                    tableBestellung.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6300));
                    tableBestellung.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(1200));
                    tableBestellung.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(1100));

                    // Reihe Tabellenkopf
                    rows = tableBestellung.getRow(0);
                    cell = rows.getCell(0);
                    XWPFParagraph paragraphBestellung = cell.getParagraphs().get(0);
                    paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun runFooter = paragraphBestellung.createRun();
                    runFooter.setFontSize(10);
                    runFooter.setBold(true);
                    runFooter.setText("Anzahl");

                    cell = rows.getCell(1);
                    paragraphBestellung = cell.getParagraphs().get(0);
                    paragraphBestellung.setAlignment(ParagraphAlignment.LEFT);
                    runFooter = paragraphBestellung.createRun();
                    runFooter.setFontSize(10);
                    runFooter.setBold(true);
                    runFooter.setText(" ISBN, Autor/Herausgeber");
                    runFooter.addBreak();
                    runFooter.setText(" Titel");

                    cell = rows.getCell(2);
                    paragraphBestellung = cell.getParagraphs().get(0);
                    paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                    runFooter = paragraphBestellung.createRun();
                    runFooter.setFontSize(10);
                    runFooter.setBold(true);
                    runFooter.setText("Einzelpreis");
                    runFooter.addBreak();
                    runFooter.setText("in Euro");

                    cell = rows.getCell(3);
                    paragraphBestellung = cell.getParagraphs().get(0);
                    paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                    runFooter = paragraphBestellung.createRun();
                    runFooter.setFontSize(10);
                    runFooter.setBold(true);
                    runFooter.setText("Gesamtpreis");
                    runFooter.addBreak();
                    runFooter.setText("in Euro");

                    // Bestellungdetails holen und ausgeben
                    Double Betrag = 0.0;    // Gesamtbetrag
                    Double ZBetrag = 0.0;   // Gesamtpreis pro Zeile
                    Double ZPreis = 0.0;    // Einzelpreis

                    Integer Buch = 0;
                    Integer Autor = 0;

                    String Sql = "";
                    Sql = "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + result.getString("BESTELLUNG_RECHNR") + "'";
                    resultBD = SQLAnfrageBD.executeQuery(Sql);
                    while (resultBD.next()) {
                        // Buchdetails holen
                        if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                            ZBetrag = (double) resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS");
                            ZBetrag = Modulhelferlein.round2dec(ZBetrag);
                            rows = tableBestellung.createRow();

                            cell = rows.getCell(0);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("1");

                            cell = rows.getCell(1);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));

                            cell = rows.getCell(3);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag)));
                        } else {
                            Buch = resultBD.getInt("BESTELLUNG_DETAIL_BUCH");
                            Sql = "SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + Integer.toString(Buch) + "'";
                            //helferlein.Infomeldung("Brief Rechnung: hole Bestellungsdetails - jetzt Buch");
                            resultBuch = SQLAnfrageBuch.executeQuery(Sql);
                            resultBuch.next();

                            if (ISBN.equals("")) {
                                ISBN = resultBuch.getString("BUCH_ISBN");
                            } else {
                                if (ISBN.equals("Multi")) {

                                } else {
                                    ISBN = "Multi";
                                }
                            }
                            rows = tableBestellung.createRow();

                            cell = rows.getCell(0);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));

                            // Autor holen
                            String[] col_Autorliste = resultBuch.getString("BUCH_AUTOR").split(",");
                            String AutorEintrag = "";
                            for (String strAutor : col_Autorliste) {
                                resultA = SQLAnfrageA.executeQuery(
                                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                                resultA.next();
                                AutorEintrag = AutorEintrag
                                        + resultA.getString("ADRESSEN_Name") + ", "
                                        + resultA.getString("ADRESSEN_Vorname") + "; ";
                            }
                            AutorEintrag = AutorEintrag.substring(0, AutorEintrag.length() - 2);
                            if (resultBuch.getBoolean("BUCH_HERAUSGEBER")) {
                                AutorEintrag = AutorEintrag + " (Hrsg.)";
                            }

                            ZPreis = (double) resultBuch.getFloat("BUCH_PREIS");
                            ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");
                            ZPreis = Modulhelferlein.round2dec(ZPreis);
                            ZBetrag = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                            ZBetrag = Modulhelferlein.round2dec(ZBetrag);

                            cell = rows.getCell(1);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.LEFT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText(resultBuch.getString("BUCH_ISBN") + ", " + AutorEintrag);
                            runFooter.addBreak();
                            String strTitel = resultBuch.getString("BUCH_TITEL");
                            if (strTitel.length() > 70) {
                                runFooter.setText(strTitel.substring(0, 70) + "...");
                            } else {
                                runFooter.setText(strTitel);
                            }

                            cell = rows.getCell(2);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(resultBuch.getFloat("BUCH_PREIS") * 1.0)));
                            runFooter.addBreak();
                            if (resultBD.getFloat("BESTELLUNG_DETAIL_RABATT") != 0) {
                                runFooter.setText("- " + Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT"))
                                        + "% = " + Modulhelferlein.str2dec(ZPreis));
                            }

                            cell = rows.getCell(3);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag)));
                            runFooter.addBreak();
                            runFooter.setText("");
                        }

                        Betrag = Betrag + ZBetrag;
                    }

                    switch (result.getInt("BESTELLUNG_LAND")) {
                        case 0: //Inland - Deutsch : Privatkunde = Gesch�ftskunde
                            rows = tableBestellung.createRow();

                            cell = rows.getCell(0);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(1);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.LEFT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(true);
                            runFooter.setText("Gesamtpreis");

                            cell = rows.getCell(2);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(3); // Gesamtbetrag
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(true);
                            runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                            rows = tableBestellung.createRow();

                            cell = rows.getCell(0);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(1);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.LEFT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("Nettogesamtbetrag");
                            runFooter.addBreak();
                            runFooter.setText("7% Mehrwertsteuer");
                            if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
//                                Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                runFooter.addBreak();
                                runFooter.setText("Versandkosten");
                                runFooter.addBreak();
                                runFooter.setText("Gesamtpreis mit Versand");
                            }

                            cell = rows.getCell(2);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(3); // Netto und UStr
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag / 107 * 100)));
                            runFooter.addBreak();
                            runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag - Betrag / 107 * 100)));
                            if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                runFooter.addBreak();
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                runFooter.addBreak();
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                            }
                            break;
                        case 10: // EU-Ausland - Deutsch
                            rows = tableBestellung.createRow();

                            cell = rows.getCell(0);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(1);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.LEFT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(true);
                            runFooter.setText("Gesamtpreis");

                            cell = rows.getCell(2);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(3);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(true);
                            runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                            rows = tableBestellung.createRow();

                            cell = rows.getCell(0);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(1);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.LEFT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            if (result.getBoolean("BESTELLUNG_PRIVAT")) { // Privatkunde
                                runFooter.setText("Nettogesamtbetrag");
                                runFooter.addBreak();
                                runFooter.setText("7% Mehrwertsteuer");
                            } else {
                                runFooter.setText("Innergemeinschaftliche Lieferung");
                                runFooter.addBreak();
                                runFooter.setText("Reverse Charge - Steuerschuldnerschaft des Leistungsempf�ngers");
                            }
                            if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                runFooter.addBreak();
                                runFooter.setText("Versandkosten");
                                runFooter.addBreak();
                                runFooter.setText("Gesamtpreis mit Versand");
                            }

                            cell = rows.getCell(2);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(3);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            if (result.getBoolean("BESTELLUNG_PRIVAT")) { // Privatkunde
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag / 107 * 100)));
                                runFooter.addBreak();
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag - Betrag / 107 * 100)));
                            } else { //Gesch�ftskunde
                                runFooter.addBreak();
                            }
                            if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                runFooter.addBreak();
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                runFooter.addBreak();
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                            }
                            break;
                        case 11: // EU-Ausland - Englisch
                            rows = tableBestellung.createRow();

                            cell = rows.getCell(0);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(1);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.LEFT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(true);
                            runFooter.setText("Total Price");

                            cell = rows.getCell(3);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(3);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(true);
                            runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                            rows = tableBestellung.createRow();

                            cell = rows.getCell(0);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(1);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.LEFT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            if (result.getBoolean("BESTELLUNG_PRIVAT")) { // Privatkunde
                                runFooter.setText("Total price without Tax");
                                runFooter.addBreak();
                                runFooter.setText("7% Value added Tax");
                            } else {
                                runFooter.setText("Intra-EU Export");
                                runFooter.addBreak();
                                runFooter.setText("Reverse Charge");
                            }
                            if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                runFooter.addBreak();
                                runFooter.setText("Shipping costs");
                                runFooter.addBreak();
                                runFooter.setText("Total price inl. Shipping costs");
                            }

                            cell = rows.getCell(2);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(3);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            if (result.getBoolean("BESTELLUNG_PRIVAT")) { // Privatkunde
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag / 107 * 100)));
                                runFooter.addBreak();
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag - Betrag / 107 * 100)));
                                if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                    Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                    runFooter.addBreak();
                                    runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                    runFooter.addBreak();
                                    runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                }
                            } else { //Gesch�ftskunde
                                runFooter.setText("Innergemeinscahftliche Lieferung");
                                runFooter.addBreak();
                                runFooter.setText("Reverse Charge - Steuerschuldnerschaft des Leistungsempf�ngers");
                                if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                    Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                    runFooter.addBreak();
                                    runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                    runFooter.addBreak();
                                    runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                }
                            }
                            break;
                        case 21: // Nicht-EU-Ausland - Englisch
                            rows = tableBestellung.createRow();

                            cell = rows.getCell(0);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(1);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.LEFT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("Total Price");
                            runFooter.addBreak();
                            runFooter.setText("Tax free export");
                            if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                runFooter.addBreak();
                                runFooter.setText("Shipping costs");
                                runFooter.addBreak();
                                runFooter.setText("Total Price incl. shipping");
                            }

                            cell = rows.getCell(2);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(3);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                            if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                runFooter.addBreak();
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                runFooter.addBreak();
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                            }
                            break;
                        case 20: // Nicht-EU-Ausland - Deutsch
                            rows = tableBestellung.createRow();

                            cell = rows.getCell(0);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(1);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.LEFT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("Gesamtpreis");
                            runFooter.addBreak();
                            runFooter.setText("Steuerfreie Ausfuhrlieferung, da es sich um eine nicht im Inland steuerbare Leistung handelt.");
                            if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                runFooter.addBreak();
                                runFooter.setText("Versandkosten");
                                runFooter.addBreak();
                                runFooter.setText("Gesamtpreis mit Versand");
                            }

                            cell = rows.getCell(2);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.CENTER);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText("");

                            cell = rows.getCell(3);
                            paragraphBestellung = cell.getParagraphs().get(0);
                            paragraphBestellung.setAlignment(ParagraphAlignment.RIGHT);
                            runFooter = paragraphBestellung.createRun();
                            runFooter.setFontSize(10);
                            runFooter.setBold(false);
                            runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                            if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                runFooter.addBreak();
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                runFooter.addBreak();
                                runFooter.setText(Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                            }
                            break;
                    }

                    Betrag = Modulhelferlein.round2dec(Betrag);

                    // Bezahlung
                    Bildschirmausgabe("... erzeuge Textblock Bezahlung ...");
                    //create paragraph
                    XWPFParagraph paragraphBezahlung = document.createParagraph();
                    paragraphBezahlung.setAlignment(ParagraphAlignment.LEFT);
                    XWPFRun runBezahlung = paragraphBezahlung.createRun();
                    runBezahlung.setFontSize(12);
                    if ((result.getInt("BESTELLUNG_LAND") == 20) || (result.getInt("BESTELLUNG_LAND") == 10) || (result.getInt("BESTELLUNG_LAND") == 0)) {
                        runBezahlung.addBreak();
                        runBezahlung.setText("Den Rechnungsbetrag von " + Modulhelferlein.str2dec(Betrag) + " EURO bitten wir innerhalb von 14 Tagen auf unser Konto bei der Berliner Volksbank zu �berweisen.");
                        runBezahlung.addBreak();
                        runBezahlung.addBreak();

                        // Schlussformel
                        Bildschirmausgabe("... erzeuge Schlussformel ...");
                        runBezahlung.setText("Mit freundlichen Gr��en");
                        runBezahlung.addBreak();
                        runBezahlung.addBreak();
                        runBezahlung.addBreak();
                        runBezahlung.setText("Carola Hartmann");
                        runBezahlung.addBreak();
                        runBezahlung.setText("Diplom Kauffrau");
                    } else {
                        runBezahlung.setText("Please transfer the amount of " + Modulhelferlein.str2dec(Betrag) + " EURO on the Invoice within 21 days to our bank account at");
                        runBezahlung.setText("the Berliner Volksbank.");
                        runBezahlung.addBreak();
                        runBezahlung.addBreak();

                        // Schlussformel
                        Bildschirmausgabe("... erzeuge Schlussformel ...");
                        runBezahlung.setText("Kind regards");
                        runBezahlung.addBreak();
                        runBezahlung.addBreak();
                        runBezahlung.addBreak();
                        runBezahlung.setText("Carola Hartmann");
                        runBezahlung.addBreak();
                        runBezahlung.setText("Master of Science in Business Administration");
                    }

                    // create footer start
                    Bildschirmausgabe("... erzeuge Fu�zeile ...");
                    XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

                    XWPFParagraph paragraphFooter = footer.createParagraph();
                    paragraphFooter.setAlignment(ParagraphAlignment.CENTER);
                    XmlCursor cursor = paragraphFooter.getCTP().newCursor();
                    XWPFTable tableFooter = footer.insertNewTbl(cursor);

                    String[] strFooter1 = {"Carola Hartmann Miles Verlag", "George Caylay Stra�e 38", "14089 Berlin"};
                    String[] strFooter2 = {"Dipl.Kff. Carola Hartmann", "Telefon +49 (0)30 36 28 86 77", "Volksbank Berlin"};
                    String[] strFooter3 = {"StrNr 19 332 6006 5", "E-Mail miles-verlag@t-online.de", "IBAN DE61 1009 0000 2233 8320 17"};
                    String[] strFooter4 = {"UStrID DE 269 369 5", "www.miles-verlag.jimdo.com", "BIC BEV0DEBB"};

                    XWPFTableRow tableFooterrow1 = tableFooter.createRow();
                    for (int i = 0; i < 3; i++) {
                        cell = tableFooterrow1.createCell();
                        CTTblWidth tblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
                        tblWidth.setW(BigInteger.valueOf(3666));
                        tblWidth.setType(STTblWidth.DXA);
                        paragraphFooter = cell.getParagraphs().get(0);
                        paragraphFooter.setAlignment(ParagraphAlignment.CENTER);
                        runFooter = paragraphFooter.createRun();
                        runFooter.setBold(false);
                        runFooter.setFontSize(10);
                        runFooter.setColor("c0c0c0");
                        runFooter.setText(strFooter1[i]);
                        runFooter.addBreak();
                        runFooter.setText(strFooter2[i]);
                        runFooter.addBreak();
                        runFooter.setText(strFooter3[i]);
                        runFooter.addBreak();
                        runFooter.setText(strFooter4[i]);
                    }
                    Bildschirmausgabe("... Fertig! Dokument ist erzeugt");
                    Bildschirmausgabe("... Dokument wird gespeichert");
                    outputFileName = outputFileName + ISBN + ".docx";

                    try ( // speichern
                            FileOutputStream out = new FileOutputStream(new File(outputFileName))) {
                        document.write(out);
                    }
                    Modulhelferlein.Infomeldung("Rechnung Nr. " + RechNr, "ist als DOC gespeichert unter ", outputFileName);
                    Dialog.setVisible(false);
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        Modulhelferlein.Fehlermeldung("Brief Rechnung", "Ausgabe Brief: IO-Exception: ", exept.getMessage());
                    }// try Brief ausgeben

                    // Adressetikett drucken
                    if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                        Zeile1 = resultK.getString("ADRESSEN_ZUSATZ_1");
                        if (resultK.getString("ADRESSEN_NAMENSZUSATZ").equals("")) {
                            Zeile2 = resultK.getString("ADRESSEN_VORNAME") + " " + resultK.getString("ADRESSEN_NAME");
                        } else {
                            Zeile2 = resultK.getString("ADRESSEN_NAMENSZUSATZ") + " " + resultK.getString("ADRESSEN_VORNAME") + " " + resultK.getString("ADRESSEN_NAME");
                        }
                        Zeile3 = resultK.getString("ADRESSEN_ZUSATZ_2");
                        Zeile2 = resultK.getString("ADRESSEN_STRASSE");
                        Zeile5 = "";
                        Zeile6 = resultK.getString("ADRESSEN_PLZ") + " " + resultK.getString("ADRESSEN_ORT");
                    } else {
                        Zeile1 = result.getString("BESTELLUNG_ZEILE_1");
                        Zeile2 = result.getString("BESTELLUNG_ZEILE_2");
                        Zeile3 = result.getString("BESTELLUNG_ZEILE_3");
                        Zeile4 = result.getString("BESTELLUNG_ZEILE_4");
                        Zeile5 = result.getString("BESTELLUNG_ZEILE_5");
                        Zeile6 = result.getString("BESTELLUNG_ZEILE_6");
                    } // if
                    String[] args = {Zeile1, Zeile2, Zeile3, Zeile4, Zeile5, Zeile6};
                    _DlgAdresseDrucken.main(args);

                }
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Brief Rechnung", "SQL-Exception: SQL-Anfrage nicht moeglich: ",
                        exept.getMessage());
                // System.exit(1);
            } // try SQL-Anfragen an die Datenbank

        } // if conn != null
    }

    /**
     *
     * @param RechNr
     * @param Typ
     * @param MahnNr
     * @param ERechnung
     * @throws Exception
     */
    public static void briefPDF(String RechNr, Integer Typ, String MahnNr, Integer ERechnung) throws Exception {
        System.out.println("Starte Erstellung Rechnung");
        String InvoiceNumber = RechNr;
        String InvoiceLanguage = "de";
        String InvoiceDate = "";
        String InvoiceName = "";
        String BuyerName = "";
        String BuyerPLZ = "";
        String BuyerStrasse = "";
        String BuyerOrt = "";
        String BuyerLand = "DE";
        String BuyerUID = "";
        String BuyerBestellzeichen = "";
        String[] Produkte; // = {"7", "Buch", "107", "100", "1"}; 
        // Produkte[i]   = Steuer
        // Produkte[i+1] = Produkt
        // Produkte[i+2] = Brutto
        // Produkte[i+3] = Netto
        // Produkte[i+4] = Anzahl
        Boolean ReverseCharge = false;
        String setLineTotal = "0";       //.setLineTotal(new Amount(100, EUR))
        String setChargeTotal = "0";     //.setChargeTotal(new Amount(0, EUR))
        String setAllowanceTotal = "0";  //.setAllowanceTotal(new Amount(0, EUR))
        String setTaxBasisTotal = "0";   //.setTaxBasisTotal(new Amount(100, EUR))
        String setTaxTotal = "0";        //.setTaxTotal(new Amount(19, EUR))
        String setDuePayable = "0";      //.setDuePayable(new Amount(119, EUR))
        String setTotalPrepaid = "0";    //.setTotalPrepaid(new Amount(0, EUR))
        String setGrandTotal = "0";      //.setGrandTotal(new Amount(119, EUR))));                                                               

        String outputFileName = Modulhelferlein.pathRechnungen + "\\Einnahmen"
                + "\\Rechnung"
                + "-"
                + "CHMV"
                + "-"
                + RechNr
                + "-"
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                + "-";

        String ISBN = "";
        System.out.println("Dateiname ist " + outputFileName);
//helferlein.Infomeldung("Dateiname " + outputFileName);
        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Brief Rechnung", "ClassNotFound-Exception: Treiber nicht gefunden: ", exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden
        System.out.println("Datenbanktreiber geladen");
        try { // Verbindung zur Datenbank ?ber die JDBC-Br?cke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Brief Rechnung", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
            System.exit(1);
        } // try Verbindung zur Datenbank ?ber die JDBC-Br?cke

        if (conn != null) {
            System.out.println("Datenbankverbindung steht");
            SQLAnfrage = null; // Anfrage erzeugen    Bestellungen
            SQLAnfrageK = null; // Anfrage erzeugen    Kundenadresse
            SQLAnfrageA = null; // Anfrage erzeugen    Adresse
            SQLAnfrageBD = null; // Anfrage erzeugen    Bestellung Details
            SQLAnfrageBuch = null; // Anfrage erzeugen   Buch

            try { // SQL-Anfragen an die Datenbank
                SQLAnfrage = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageK = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageA = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageBD = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen

                // Bestellung lesen
                result = SQLAnfrage.executeQuery("SELECT * FROM TBL_BESTELLUNG WHERE BESTELLUNG_RECHNR = '" + RechNr + "'");
                result.next();
                System.out.println("Bestellung ist eingelesen");
                // Kunde lesen
                if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                    resultK = SQLAnfrageK.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + Integer.toString(result.getInt("BESTELLUNG_KUNDE")) + "'");
                    resultK.next();
                    System.out.println("Kundendaten sind eingelesen");
                }

                try ( // Create a document and add a page to it
                        PDDocument document = new PDDocument()) {
                    PDPage page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    // Create a new font object selecting one of the PDF base fonts
                    // PDFont fontPlain = PDType1Font.HELVETICA;
                    // PDFont fontBold = PDType1Font.HELVETICA_BOLD;
                    // PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;

                    PDFont fontPlain = PDType0Font.load(document, new File("c:\\windows\\fonts\\arial.ttf"));
                    PDFont fontBold = PDType0Font.load(document, new File("c:\\windows\\fonts\\arialbd.ttf"));
                    PDFont fontItalic = PDType0Font.load(document, new File("c:\\windows\\fonts\\ariali.ttf"));
                    PDFont fontBoldItalic = PDType0Font.load(document, new File("c:\\windows\\fonts\\arialbi.ttf"));

                    PDPageContentStream cos = new PDPageContentStream(document, page);
//                    try ( PDPageContentStream cos = new PDPageContentStream(document, page)){      
                    // Start a new content stream which will "hold" the to be created content

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
                        Modulhelferlein.Fehlermeldung("Rechnung erzeugen - Header", "FileNotFound-Exception", fnfex.getMessage());
                    }
                    System.out.println("Kopfzeile erzeugt");
// Fu?zeile
                    Ausgabe(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
                    Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
                    Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
                    Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");
                    Ausgabe(cos, fontBold, 10, Color.GRAY, 230, 35, "George Caylay Stra�e 38");
                    Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 25, "Telefon: +49 (0)30 36 28 86 77");
                    Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 15, "e-Mail: miles-verlag@t-online.de");
                    Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 5, "Internet: www.miles-verlag.jimdo.com");
                    Ausgabe(cos, fontBold, 10, Color.GRAY, 400, 35, "14089 Berlin");
                    Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 25, "Volksbank Berlin");
                    Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 15, "IBAN: DE61 1009 0000 2233 8320 17");
                    Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 5, "BIC: BEV0DEBB");
                    System.out.println("Fu�zeile erzeugt");
// Faltmarke, Lochmarke, Faltmarke
                    Linie(cos, 1, 0, 595, 15, 595);
                    Linie(cos, 1, 0, 415, 25, 415);
                    Linie(cos, 1, 0, 285, 15, 285);

                    System.out.println("Faltmarke erzeugt");
// Absenderzeile
                    Ausgabe(cos, fontPlain, 8, Color.BLACK, 50, 751, "C. Hartmann Miles-Verlag - George Caylay Stra�e 38 - 14089 Berlin");
                    Linie(cos, 1, 50, 749, 297, 749);

                    System.out.println("Absenderzeile erzeugt");
// Datum, Rechnungsnummer, Bestell-Typ
                    InvoiceDate = Modulhelferlein.printDateFormat("dd.MM.yyyy", Modulhelferlein.SQLDate2Date(result.getDate("BESTELLUNG_RECHDAT")));
                    Integer Sprache = 0;
                    if ((result.getInt("BESTELLUNG_LAND") == 20) || (result.getInt("BESTELLUNG_LAND") == 10) || (result.getInt("BESTELLUNG_LAND") == 0)) {
                        Sprache = 0; // Deutsch
                        InvoiceLanguage = "de";
                    } else {
                        Sprache = 1; // Englisch
                        InvoiceLanguage = "en";
                    }
                    InvoiceName = SpracheRechnung[Sprache];
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 354, 655, SpracheRechnungsdatum[Sprache] + " " + Modulhelferlein.printDateFormat("dd.MM.yyyy", Modulhelferlein.SQLDate2Date(result.getDate("BESTELLUNG_RECHDAT"))));
                    Ausgabe(cos, fontPlain, 10, Color.BLACK, 354, 640, SpracheVersanddatum[Sprache]);
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 354, 685, SpracheRechnungsnummer[Sprache] + " " + RechNr);
                    switch (Typ) {
                        case 0:
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 354, 720, SpracheRechnung[Sprache]);
                            break;
                        case 1:
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 354, 720, SpracheRechnung[Sprache]);
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 354, 705, SpracheRezensionsexemplar[Sprache]);
                            break;
                        case 2:
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 354, 720, SpracheRechnung[Sprache]);
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 354, 705, SprachePflichtexempar[Sprache]);
                            break;
                        case 3:
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 354, 720, SpracheRechnung[Sprache]);
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 354, 705, SpracheBelegexemplar[Sprache]);
                            break;
                        case 4:
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 354, 720, SpracheRechnung[Sprache]);
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 354, 705, SpracheGeschenk[Sprache]);
                            break;
                        case 5:
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 354, 720, SpracheRechnung[Sprache]);
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 354, 705, SpracheRemittende[Sprache]);
                            break;
                        case 99:
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 354, 720, MahnNr + ". " + SpracheMahnung[Sprache]);
                            break;
                    }

                    System.out.println("Datum, Rechnungsnummer geschrieben");
// Adresse
                    String[] AdressZeile = {"", "", "", "", "", "", ""};
                    String[] args = {"", "", "", "", "", "", ""};
                    if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                        System.out.println("erzeuge Adresse aus Kundendatenbank");
                        args[0] = resultK.getString("ADRESSEN_ZUSATZ_1");
                        args[1] = Modulhelferlein.makeAnrede(resultK.getString("ADRESSEN_NAMENSZUSATZ"),
                                resultK.getString("ADRESSEN_VORNAME"),
                                resultK.getString("ADRESSEN_NAME"));
                        args[2] = resultK.getString("ADRESSEN_ZUSATZ_2");
                        args[3] = resultK.getString("ADRESSEN_STRASSE");
                        args[4] = resultK.getString("ADRESSEN_PLZ") + " " + resultK.getString("ADRESSEN_ORT");
                        args[5] = resultK.getString("ADRESSEN_ZUSATZ_3");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 410, 560, resultK.getString("ADRESSEN_USTR_ID"));
                        BuyerName = Modulhelferlein.makeAnrede(resultK.getString("ADRESSEN_NAMENSZUSATZ"),
                                resultK.getString("ADRESSEN_VORNAME"),
                                resultK.getString("ADRESSEN_NAME"));
                        BuyerPLZ = resultK.getString("ADRESSEN_PLZ");
                        BuyerStrasse = resultK.getString("ADRESSEN_STRASSE");
                        BuyerOrt = resultK.getString("ADRESSEN_ORT");
                        BuyerUID = resultK.getString("ADRESSEN_USTR_ID");
                        if (!args[5].equals("")) {
                            BuyerLand = args[5];
                        }
                    } else {
                        System.out.println("erzeuge Adresse aus Bestellung");
                        args[0] = result.getString("BESTELLUNG_ZEILE_1");
                        args[1] = result.getString("BESTELLUNG_ZEILE_2");
                        args[2] = result.getString("BESTELLUNG_ZEILE_3");
                        args[3] = result.getString("BESTELLUNG_ZEILE_4");
                        args[4] = result.getString("BESTELLUNG_ZEILE_5");
                        args[5] = result.getString("BESTELLUNG_ZEILE_6");
                        if (!args[5].equals("")) {
                            BuyerLand = args[5];
                        }
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 410, 560, result.getString("BESTELLUNG_USTR_ID"));
                        BuyerName = result.getString("BESTELLUNG_ZEILE_2");
                        if (result.getString("BESTELLUNG_ZEILE_5").contains(" ")) {
                            String[] PLZOrt = result.getString("BESTELLUNG_ZEILE_5").split(" ");
                            BuyerPLZ = PLZOrt[0];
                            BuyerOrt = PLZOrt[1];
                        } else {
                            BuyerOrt = result.getString("BESTELLUNG_ZEILE_5");
                            BuyerPLZ = "";
                        }
                        BuyerStrasse = result.getString("BESTELLUNG_ZEILE_4");
                        BuyerUID = result.getString("BESTELLUNG_USTR_ID");
                    } // if
                    System.out.println("komprimiere Adresse - l�sche Leerzeilen");
                    if (BuyerLand.equals("DEUTSCHLAND")) {
                        args[5] = "";
                    }
                    Integer ZeilenNr = 1;
                    for (int i = 0; i < 6; i++) {
                        if (!args[i].equals("")) {
                            AdressZeile[ZeilenNr] = args[i];
                            ZeilenNr = ZeilenNr + 1;
                        }
                    }
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 730, AdressZeile[1]);
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 715, AdressZeile[2]);
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 700, AdressZeile[3]);
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 685, AdressZeile[4]);
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 670, AdressZeile[5]);
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 655, AdressZeile[6]);
                    System.out.println("Adresse erzeugt");
// Betreff, Anrede
                    BuyerBestellzeichen = result.getString("BESTELLUNG_BESTNR");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 575, SpracheBestelldatum[Sprache] + " "
                            + Modulhelferlein.printDateFormat("dd.MM.yyyy", Modulhelferlein.SQLDate2Date(result.getDate("BESTELLUNG_DATUM"))));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 300, 575, SpracheBestellzeichen[Sprache]);
                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 410, 575, result.getString("BESTELLUNG_BESTNR"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 300, 560, SpracheUstrID[Sprache]);
                    // Anrede
                    if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                        if (resultK.getString("ADRESSEN_ANREDE").equals("")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 540, SpracheAnrede[Sprache]);
                        } else {
                            // String POC[] = resultK.getString("ADRESSEN_POC").split(" ");
                            // Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 540, resultK.getString("ADRESSEN_ANREDE") + " " + POC[1] + ",");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 540, Modulhelferlein.makeAnrede(resultK.getString("ADRESSEN_ANREDE"), resultK.getString("ADRESSEN_NAMENSZUSATZ"), resultK.getString("ADRESSEN_NAME")) + ",");
                        } // if
                    } else {
                    }
                    System.out.println("Anrede erzeugt");
// Text
                    switch (Typ) {
                        case 99:  // Mahnung
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 510, SpracheEinMahnung1[Sprache]);
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 495, SpracheEinMahnung2[Sprache]);
                            break;
                        default:
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 510, SpracheEinleitung1[Sprache]);
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 495, SpracheEinleitung2[Sprache]);
                    }
// Buchbestellungen
                    Double Betrag = 0.0;
                    Double ZBetrag = 0.0;
                    Double ZPreis = 0.0;
                    Double Gesamt7 = 0.0;
                    Double Gesamt19 = 0.0;
                    Integer zeilenNr = 0;
                    Integer Buch = 0;
                    Integer Autor = 0;
// Bestellungdetails holen und ausgeben
                    String Sql = "";
                    Sql = "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + result.getString("BESTELLUNG_RECHNR") + "'";
                    System.out.println("Brief Rechnung: hole Bestellungsdetails");

                    resultBD = SQLAnfrageBD.executeQuery(Sql);
                    resultBD.last();
                    Integer itemcount = resultBD.getRow();
                    Boolean Einseitig = (itemcount <= 8);
                    System.out.println("Die Bestellung enth�lt " + Integer.toString(itemcount) + " Positionen");
                    if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                        itemcount = itemcount + 1;
                        System.out.println("Die Bestellung enth�lt Versandkosten");
                        itemcount = itemcount * 6;
                        Produkte = new String[itemcount];
                        itemcount = 6;
                        Produkte[0] = "0";
                        Produkte[2] = Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND")));
                        Produkte[3] = Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND")));
                        Produkte[4] = "1";
                        switch (Sprache) {
                            case 0:
                                Produkte[1] = "Versandkosten";
                                Produkte[5] = "ohne";
                                break;
                            case 1:
                                Produkte[1] = "Shipping costs";
                                Produkte[5] = "not applicable";
                                break;
                        }
                    } else {
                        itemcount = itemcount * 6;
                        Produkte = new String[itemcount];
                        itemcount = 0;
                    }
                    Integer Basiszeile = 450;

                    if (Einseitig) { // es passt alles auf 1 Seite
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 55, 470, SpracheAnzahl[Sprache]);
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, 470, SpracheAutor[Sprache]);
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 420, 470, SpracheEinzelpreis[Sprache]);
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 485, 470, SpracheGesamtpreis[Sprache]);
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, 460, SpracheTitel[Sprache]);
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 435, 460, "in Euro");
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 500, 460, "in Euro");
                        Linie(cos, 1, 55, 455, 540, 455);

                        resultBD.beforeFirst();
                        while (resultBD.next()) {
                            // Buchdetails holen
                            if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                                ZBetrag = (double) resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS");
                                ZBetrag = Modulhelferlein.round2dec(ZBetrag);
                                Gesamt19 = Gesamt19 + ZBetrag;
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 70, Basiszeile - (zeilenNr + 1) * 11, "1");
                                AusgabeLeistung(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"), 300);
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 450, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag)));
                                AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag)));
                                Produkte[itemcount] = "19";
                                Produkte[itemcount + 1] = resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT");
                                Produkte[itemcount + 2] = Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag));
                                Produkte[itemcount + 3] = Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag / 119 * 100));
                                Produkte[itemcount + 4] = "1";
                                Produkte[itemcount + 5] = "ohne";
                                zeilenNr = zeilenNr + 1;
                            } else {
                                Buch = resultBD.getInt("BESTELLUNG_DETAIL_BUCH");
                                Sql = "SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + Integer.toString(Buch) + "'";
                                //helferlein.Infomeldung("Brief Rechnung: hole Bestellungsdetails - jetzt Buch");
                                resultBuch = SQLAnfrageBuch.executeQuery(Sql);
                                resultBuch.next();

                                if (ISBN.equals("")) {
                                    ISBN = resultBuch.getString("BUCH_ISBN");
                                } else {
                                    if (ISBN.equals("Multi")) {

                                    } else {
                                        ISBN = "Multi";
                                    }
                                }
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 70, Basiszeile - (zeilenNr + 1) * 11, Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));

                                // Autor holen
                                String[] col_Autorliste = resultBuch.getString("BUCH_AUTOR").split(",");
                                String AutorEintrag = "";
                                for (String strAutor : col_Autorliste) {
                                    resultA = SQLAnfrageA.executeQuery(
                                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                                    resultA.next();
                                    AutorEintrag = AutorEintrag
                                            + resultA.getString("ADRESSEN_Name") + ", "
                                            + resultA.getString("ADRESSEN_Vorname") + "; ";
                                }
                                AutorEintrag = AutorEintrag.substring(0, AutorEintrag.length() - 2);
                                if (resultBuch.getBoolean("BUCH_HERAUSGEBER")) {
                                    AutorEintrag = AutorEintrag + " (Hrsg.)";
                                }

                                ZPreis = (double) resultBuch.getFloat("BUCH_PREIS");
                                ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");
                                ZPreis = Modulhelferlein.round2dec(ZPreis);
                                ZBetrag = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                                Gesamt7 = Gesamt7 + ZBetrag;
                                ZBetrag = Modulhelferlein.round2dec(ZBetrag);

                                Ausgabe(cos, fontItalic, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, resultBuch.getString("BUCH_ISBN") + ", " + AutorEintrag);
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 450, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(resultBuch.getFloat("BUCH_PREIS") * 1.0)));
                                AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag)));

                                zeilenNr = zeilenNr + 1;

                                String strTitel = resultBuch.getString("BUCH_TITEL");
                                if (strTitel.length() > 70) {
                                    Ausgabe(cos, fontItalic, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, strTitel.substring(0, 70) + "...");
                                } else {
                                    Ausgabe(cos, fontItalic, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, strTitel);
                                }

                                if (resultBD.getFloat("BESTELLUNG_DETAIL_RABATT") != 0) {
                                    Ausgabe(cos, fontPlain, 9, Color.BLACK, 420, Basiszeile - (zeilenNr + 1) * 11, "- " + Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")) + "% = " + Modulhelferlein.str2dec(ZPreis));
                                }
                                Produkte[itemcount] = "7";
                                Produkte[itemcount + 1] = resultBuch.getString("BUCH_TITEL");
                                Produkte[itemcount + 2] = Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag));
                                Produkte[itemcount + 3] = Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag / 107 * 100));
                                Produkte[itemcount + 4] = resultBD.getString("BESTELLUNG_DETAIL_ANZAHL");
                                Produkte[itemcount + 5] = resultBuch.getString("BUCH_ISBN");
                            }
                            System.out.println("Schreibe Position " + itemcount.toString() + ": " + Produkte[itemcount] + ", " + Produkte[itemcount + 1] + ", " + Produkte[itemcount + 2] + ", " + Produkte[itemcount + 3] + ", " + Produkte[itemcount + 4] + ", " + Produkte[itemcount + 5]);
                            Betrag = Betrag + ZBetrag;
                            zeilenNr = zeilenNr + 1;
                            Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                            itemcount = itemcount + 6;
                        } // while
// Schreibe Summen
                        switch (result.getInt("BESTELLUNG_LAND")) {
                            case 0: //Inland - Deutsch : Privatkunde = Gesch�ftskunde
                                // Umsatzsteuer ist auszuweisen
                                zeilenNr = zeilenNr + 1;
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Gesamtpreis");
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                zeilenNr = zeilenNr + 1;
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Nettogesamtbetrag");
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec((Gesamt19 / 119 * 100) + (Gesamt7 / 107 * 100))));

                                zeilenNr = zeilenNr + 1;
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, " 7% Mehrwertsteuer");
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt7 - Gesamt7 / 107 * 100)));

                                if (Gesamt19 > 0) {
                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "19% Mehrwertsteuer");
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt19 - Gesamt19 / 119 * 100)));
                                }

                                zeilenNr = zeilenNr + 1;
                                Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                    zeilenNr = zeilenNr + 1;
                                    Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                    Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Versandkosten");
                                    AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Gesamtpreis mit Versand");
                                    AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                }
                                break;
                            case 10: // EU-Ausland - Deutsch
                            case 11: // EU-Asuland - Englisch
                                if (result.getBoolean("BESTELLUNG_PRIVAT")) { // Privatkunde
                                    // Umsatzsteuer ist auszuweisen

                                    // Ausgabe Gesamtsumme Brutto => Betrag
                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtpreis[Sprache]);
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                    // Ausgabe Nettobetrag => Betrag - UStr 7% - UStr 19%
                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheNetto[Sprache]);
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec((Betrag - (Gesamt19 - Gesamt19 / 119 * 100) - (Gesamt7 - Gesamt7 / 107 * 100)))));

//                                    zeilenNr = zeilenNr + 1;
//                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, " 7% " + SpracheUmsatzsteuer[Sprache]);
//                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt7 - Gesamt7 / 107 * 100)));
                                    // Ausgabe Umsatzsteuer 7%
                                    if (Gesamt7 > 0) {
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "7% " + SpracheUmsatzsteuer[Sprache]);
                                        AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt7 - Gesamt7 / 107 * 100)));
                                    }

                                    // Ausgabe Umsatzsteuer 19%
                                    if (Gesamt19 > 0) {
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "19% " + SpracheUmsatzsteuer[Sprache]);
                                        AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt19 - Gesamt19 / 119 * 100)));
                                    }
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                    if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                        Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                        zeilenNr = zeilenNr + 1;
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheVersandkosten[Sprache]);
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtVersand[Sprache]);
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                    }
                                } else { //Gesch�ftskunde
                                    //
                                    ReverseCharge = true;
                                    setDuePayable = setTaxBasisTotal;
                                    setGrandTotal = setTaxBasisTotal;

                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtpreis[Sprache]);
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheNetto[Sprache]);
                                    Betrag = Gesamt7 / 107 * 100 + Gesamt19 / 119 * 100;
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheEULieferung[Sprache]);
                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheReverseCharge[Sprache]);

                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                    if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                        Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                        zeilenNr = zeilenNr + 1;
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheVersandkosten[Sprache]);
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtVersand[Sprache]);
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                    }
                                }
                                break;
                            case 20: // Nicht-EU-Ausland - Deutsch
                            case 21: // Nicht-EU-Ausland - Englisch
                                // Privatkunde = Gesch�ftskunde
                                // keine Steuer
                                setDuePayable = setTaxBasisTotal;
                                setGrandTotal = setTaxBasisTotal;

                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, SpracheGesamtpreis[Sprache]);
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 1) * 11, 540, Basiszeile - 2 - (zeilenNr + 1) * 11);

                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 2) * 11, SpracheSteuerfrei1[Sprache]);
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 3) * 11, SpracheSteuerfrei2[Sprache]);

                                Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 3) * 11, 540, Basiszeile - 2 - (zeilenNr + 3) * 11);

                                if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                    Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 4) * 11, 540, Basiszeile - 2 - (zeilenNr + 4) * 11);
                                    Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 4) * 11, SpracheVersandkosten[Sprache]);
                                    AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 4) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                    Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 5) * 11, SpracheGesamtVersand[Sprache]);
                                    AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 5) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 5) * 11, 540, Basiszeile - 2 - (zeilenNr + 5) * 11);

                                }
                                break;
                        } // switch

                        Betrag = Modulhelferlein.round2dec(Betrag);
                        System.out.println("Gesamtsumme/Steuer geschrieben");

// Schlusstext Bezahlung
                        if (result.getBoolean("BESTELLUNG_BEZAHLUNG")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 215, SpracheVerrechnung1[Sprache]);
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 55 + Math.round(fontPlain.getStringWidth(SpracheVerrechnung1[Sprache] + " ") / 1000 * 12), 215, Modulhelferlein.str2dec(Betrag) + " Euro ");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55 + Math.round(fontPlain.getStringWidth(SpracheVerrechnung1[Sprache] + " ") / 1000 * 12)
                                    + Math.round(fontBold.getStringWidth(Modulhelferlein.str2dec(Betrag)) / 1000 * 12)
                                    + Math.round(fontPlain.getStringWidth(" Euro ") / 1000 * 12), 215, SpracheVerrechnung2[Sprache]);
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 200, SpracheVerrechnung3[Sprache]);
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 215, SpracheSchluss1[Sprache]);
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 55 + Math.round(fontPlain.getStringWidth(SpracheSchluss1[Sprache] + " ") / 1000 * 12), 215, Modulhelferlein.str2dec(Betrag) + " Euro ");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55 + Math.round(fontPlain.getStringWidth(SpracheSchluss1[Sprache] + " ") / 1000 * 12)
                                    + Math.round(fontBold.getStringWidth(Modulhelferlein.str2dec(Betrag)) / 1000 * 12)
                                    + Math.round(fontPlain.getStringWidth(" Euro ") / 1000 * 12), 215, SpracheSchluss21[Sprache] + result.getString("BESTELLUNG_ZAHLUNGSZIEL") + SpracheSchluss22[Sprache]);
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 200, SpracheSchluss3[Sprache]);
                        }
                        System.out.println("Bezahlhinweis geschrieben");
// Schlusstext Hinweis
                        if (result.getBoolean("BESTELLUNG_TB")) {
                            String Beschreibung = "Hinweis: " + result.getString("BESTELLUNG_TEXT") + " ENDE ENDE";
                            String[] splitBeschreibung = Beschreibung.split(" ");
                            Integer woerter = splitBeschreibung.length;
                            //helferlein.Infomeldung("woerter - 1", splitBeschreibung[woerter-1]);
                            splitBeschreibung[woerter - 1] = ""; //ENDE
                            woerter = woerter - 2;
                            String zeile = "";
                            Integer i = 0;
                            Integer laenge = 0;
                            ZeilenNr = 1;
                            while (i < woerter - 1) {
                                zeile = splitBeschreibung[i];
                                laenge = Modulhelferlein.float2Int(fontPlain.getStringWidth(zeile + " " + splitBeschreibung[i + 1]) / 1000 * 12);
                                while ((laenge < 490) && (zeile.length() < 90) && (i < woerter - 1)) {
                                    zeile = zeile + " " + splitBeschreibung[i + 1];
                                    i = i + 1;
                                    laenge = Modulhelferlein.float2Int(fontPlain.getStringWidth(zeile + " " + splitBeschreibung[i + 1]) / 1000 * 12);
                                }
                                //helferlein.Infomeldung(Float.toString(laenge) + " => " + zeile);                                
                                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 180 - 15 * (ZeilenNr - 1), zeile);
                                i = i + 1;
                                ZeilenNr = ZeilenNr + 1;
                            }
                            System.out.println("Hinweis geschrieben");
                        }

// Schlussformel
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 150, SpracheGruss[Sprache]);
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 85, "Carola Hartmann");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 70, SpracheBeruf[Sprache]);
                        System.out.println("Schlussformel geschrieben");

//helferlein.Infomeldung("Fertig") ;                      
                        if (result.getBoolean("BESTELLUNG_STORNIERT")) {
                            Ausgabe(cos, fontBold, 24, Color.RED, 55, 550, SpracheStorniert[Sprache]);
                            Ausgabe(cos, fontBold, 24, Color.RED, 420, 715, SpracheStorniert[Sprache]);
                        }

                        outputFileName = outputFileName + ISBN + ".pdf";

// Make sure that the content stream is closed:
                        cos.close();
                    } else { // zweiseitige Bestellung die bestellungen werden auf der 2. Seite im Detail aufgef�hrt
                        System.out.println("Schreibe Hinweis auf Anlage");

// Schreibe Hinweis auf Anlage
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 400, SpracheAnlage1[Sprache]);

// Berechne Gesamtsumme
                        resultBD.beforeFirst();
                        while (resultBD.next()) {
                            // Buchdetails holen
                            if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                                ZBetrag = (double) resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS");
                                ZBetrag = Modulhelferlein.round2dec(ZBetrag);
                                Gesamt19 = Gesamt19 + ZBetrag;
                            } else {
                                Buch = resultBD.getInt("BESTELLUNG_DETAIL_BUCH");
                                Sql = "SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + Integer.toString(Buch) + "'";
                                //helferlein.Infomeldung("Brief Rechnung: hole Bestellungsdetails - jetzt Buch");
                                resultBuch = SQLAnfrageBuch.executeQuery(Sql);
                                resultBuch.next();

                                ZPreis = (double) resultBuch.getFloat("BUCH_PREIS");
                                ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");
                                ZPreis = Modulhelferlein.round2dec(ZPreis);
                                ZBetrag = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                                Gesamt7 = Gesamt7 + ZBetrag;
                                ZBetrag = Modulhelferlein.round2dec(ZBetrag);
                            }
                            Betrag = Betrag + ZBetrag;
                        } // while
                        switch (result.getInt("BESTELLUNG_LAND")) {
                            case 0: //Inland - Deutsch : Privatkunde = Gesch�ftskunde
                                // Umsatzsteuer ist auszuweisen
                                if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                    Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                }
                                break;
                            case 10: // EU-Ausland - Deutsch
                            case 11: // EU-Asuland - Englisch
                                if (result.getBoolean("BESTELLUNG_PRIVAT")) { // Privatkunde
                                    // Umsatzsteuer ist auszuweisen
                                    if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                        Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                    }
                                } else { //Gesch�ftskunde
                                    // Umsatzsteuer ist nicht auszuweisen
                                    Betrag = Gesamt19 / 119 * 100 + Gesamt7 / 107 * 100;
                                    if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                        Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                    }
                                }
                                break;
                            case 20: // Nicht-EU-Ausland - Deutsch
                            case 21: // Nicht-EU-Ausland - Englisch
                                // Privatkunde = Gesch�ftskunde
                                // keine Steuer
                                Betrag = Gesamt19 / 119 * 100 + Gesamt7 / 107 * 100;
                                if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                    Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                }
                                break;
                        } // switch

// Schreibe Bezahlung 
                        Betrag = Modulhelferlein.round2dec(Betrag);
                        System.out.println("Gesamtsumme/Steuer geschrieben");

// Schlusstext Bezahlung
                        if (result.getBoolean("BESTELLUNG_BEZAHLUNG")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 215, SpracheVerrechnung1[Sprache]);
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 55 + Math.round(fontPlain.getStringWidth(SpracheVerrechnung1[Sprache] + " ") / 1000 * 12), 215, Modulhelferlein.str2dec(Betrag) + " Euro ");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55 + Math.round(fontPlain.getStringWidth(SpracheVerrechnung1[Sprache] + " ") / 1000 * 12)
                                    + Math.round(fontBold.getStringWidth(Modulhelferlein.str2dec(Betrag)) / 1000 * 12)
                                    + Math.round(fontPlain.getStringWidth(" Euro ") / 1000 * 12), 215, SpracheVerrechnung2[Sprache]);
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 200, SpracheVerrechnung3[Sprache]);
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 215, SpracheSchluss1[Sprache]);
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 55 + Math.round(fontPlain.getStringWidth(SpracheSchluss1[Sprache] + " ") / 1000 * 12), 215, Modulhelferlein.str2dec(Betrag) + " Euro ");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55 + Math.round(fontPlain.getStringWidth(SpracheSchluss1[Sprache] + " ") / 1000 * 12)
                                    + Math.round(fontBold.getStringWidth(Modulhelferlein.str2dec(Betrag)) / 1000 * 12)
                                    + Math.round(fontPlain.getStringWidth(" Euro ") / 1000 * 12), 215, SpracheSchluss21[Sprache] + result.getString("BESTELLUNG_ZAHLUNGSZIEL") + SpracheSchluss22[Sprache]);
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 200, SpracheSchluss3[Sprache]);
                        }
                        System.out.println("Bezahlhinweis geschrieben");
// Schlusstext Hinweis
                        if (result.getBoolean("BESTELLUNG_TB")) {
                            String Beschreibung = result.getString("BESTELLUNG_TEXT") + " ENDE ENDE";
                            String[] splitBeschreibung = Beschreibung.split(" ");
                            Integer woerter = splitBeschreibung.length;
                            //helferlein.Infomeldung("woerter - 1", splitBeschreibung[woerter-1]);
                            splitBeschreibung[woerter - 1] = ""; //ENDE
                            woerter = woerter - 2;
                            String zeile = "";
                            Integer i = 0;
                            Integer laenge = 0;
                            ZeilenNr = 1;
                            while (i < woerter - 1) {
                                zeile = splitBeschreibung[i];
                                laenge = Modulhelferlein.float2Int(fontPlain.getStringWidth(zeile + " " + splitBeschreibung[i + 1]) / 1000 * 12);
                                while ((laenge < 490) && (zeile.length() < 90) && (i < woerter - 1)) {
                                    zeile = zeile + " " + splitBeschreibung[i + 1];
                                    i = i + 1;
                                    laenge = Modulhelferlein.float2Int(fontPlain.getStringWidth(zeile + " " + splitBeschreibung[i + 1]) / 1000 * 12);
                                }
                                //helferlein.Infomeldung(Float.toString(laenge) + " => " + zeile);                                
                                Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 180 - 15 * (ZeilenNr - 1), zeile);
                                i = i + 1;
                                ZeilenNr = ZeilenNr + 1;
                            }
                            System.out.println("Hinweis geschrieben");
                        }

// Schlussformel
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 150, SpracheGruss[Sprache]);
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 85, "Carola Hartmann");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 70, SpracheBeruf[Sprache]);
                        System.out.println("Schlussformel geschrieben");

//helferlein.Infomeldung("Fertig") ;                      
                        if (result.getBoolean("BESTELLUNG_STORNIERT")) {
                            Ausgabe(cos, fontBold, 24, Color.RED, 55, 550, SpracheStorniert[Sprache]);
                            Ausgabe(cos, fontBold, 24, Color.RED, 420, 715, SpracheStorniert[Sprache]);
                        }

                        outputFileName = outputFileName + ISBN + ".pdf";

// Make sure that the content stream is closed:
                        cos.close();

// neue Seite
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        cos = new PDPageContentStream(document, page);
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 730, SpracheAnlage2[Sprache]);
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
                            Modulhelferlein.Fehlermeldung("Rechnung erzeugen - Header", "FileNotFound-Exception", fnfex.getMessage());
                        }
// Fu?zeile
                        Ausgabe(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");
                        Ausgabe(cos, fontBold, 10, Color.GRAY, 230, 35, "George Caylay Stra�e 38");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 25, "Telefon: +49 (0)30 36 28 86 77");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 15, "e-Mail: miles-verlag@t-online.de");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 230, 5, "Internet: www.miles-verlag.jimdo.com");
                        Ausgabe(cos, fontBold, 10, Color.GRAY, 400, 35, "14089 Berlin");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 25, "Volksbank Berlin");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 15, "IBAN: DE61 1009 0000 2233 8320 17");
                        Ausgabe(cos, fontBold, 9, Color.GRAY, 400, 5, "BIC: BEV0DEBB");
                        System.out.println("Fu�zeile erzeugt");

// Faltmarke, Lochmarke, Faltmarke
                        Linie(cos, 1, 0, 595, 15, 595);
                        Linie(cos, 1, 0, 415, 25, 415);
                        Linie(cos, 1, 0, 285, 15, 285);

// Schreibe Details
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 55, 700, SpracheAnzahl[Sprache]);
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, 700, SpracheAutor[Sprache]);
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 420, 700, SpracheEinzelpreis[Sprache]);
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 485, 700, SpracheGesamtpreis[Sprache]);
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, 690, SpracheTitel[Sprache]);
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 435, 690, "in Euro");
                        Ausgabe(cos, fontBold, 9, Color.BLACK, 500, 690, "in Euro");
                        Linie(cos, 1, 55, 685, 540, 685);

                        Basiszeile = 688;
                        resultBD.beforeFirst();
                        while (resultBD.next()) {
                            // Buchdetails holen
                            if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                                ZBetrag = (double) resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS");
                                ZBetrag = Modulhelferlein.round2dec(ZBetrag);
                                Gesamt19 = Gesamt19 + ZBetrag;
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 70, Basiszeile - (zeilenNr + 1) * 11, "1");
                                Modulhelferlein.AusgabeLeistung(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"), 300);
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 450, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag)));
                                AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag)));
                                Produkte[itemcount] = "19";
                                Produkte[itemcount + 1] = resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT");
                                Produkte[itemcount + 2] = Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag));
                                Produkte[itemcount + 3] = Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag / 119 * 100));
                                Produkte[itemcount + 4] = "1";
                                Produkte[itemcount + 5] = "ohne";
                                zeilenNr = zeilenNr + 1;
                            } else {
                                Buch = resultBD.getInt("BESTELLUNG_DETAIL_BUCH");
                                Sql = "SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + Integer.toString(Buch) + "'";
                                //helferlein.Infomeldung("Brief Rechnung: hole Bestellungsdetails - jetzt Buch");
                                resultBuch = SQLAnfrageBuch.executeQuery(Sql);
                                resultBuch.next();

                                if (ISBN.equals("")) {
                                    ISBN = resultBuch.getString("BUCH_ISBN");
                                } else {
                                    if (ISBN.equals("Multi")) {

                                    } else {
                                        ISBN = "Multi";
                                    }
                                }
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 70, Basiszeile - (zeilenNr + 1) * 11, Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));

                                // Autor holen
                                String[] col_Autorliste = resultBuch.getString("BUCH_AUTOR").split(",");
                                String AutorEintrag = "";
                                for (String strAutor : col_Autorliste) {
                                    resultA = SQLAnfrageA.executeQuery(
                                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                                    resultA.next();
                                    AutorEintrag = AutorEintrag
                                            + resultA.getString("ADRESSEN_Name") + ", "
                                            + resultA.getString("ADRESSEN_Vorname") + "; ";
                                }
                                AutorEintrag = AutorEintrag.substring(0, AutorEintrag.length() - 2);
                                if (resultBuch.getBoolean("BUCH_HERAUSGEBER")) {
                                    AutorEintrag = AutorEintrag + " (Hrsg.)";
                                }

                                ZPreis = (double) resultBuch.getFloat("BUCH_PREIS");
                                ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");
                                ZPreis = Modulhelferlein.round2dec(ZPreis);
                                ZBetrag = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                                Gesamt7 = Gesamt7 + ZBetrag;
                                ZBetrag = Modulhelferlein.round2dec(ZBetrag);

                                Ausgabe(cos, fontItalic, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, resultBuch.getString("BUCH_ISBN") + ", " + AutorEintrag);
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 450, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(resultBuch.getFloat("BUCH_PREIS") * 1.0)));
                                AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag)));

                                zeilenNr = zeilenNr + 1;

                                String strTitel = resultBuch.getString("BUCH_TITEL");
                                if (strTitel.length() > 70) {
                                    Ausgabe(cos, fontItalic, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, strTitel.substring(0, 70) + "...");
                                } else {
                                    Ausgabe(cos, fontItalic, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, strTitel);
                                }

                                if (resultBD.getFloat("BESTELLUNG_DETAIL_RABATT") != 0) {
                                    Ausgabe(cos, fontPlain, 9, Color.BLACK, 420, Basiszeile - (zeilenNr + 1) * 11, "- " + Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")) + "% = " + Modulhelferlein.str2dec(ZPreis));
                                }
                                Produkte[itemcount] = "7";
                                Produkte[itemcount + 1] = resultBuch.getString("BUCH_TITEL");
                                Produkte[itemcount + 2] = Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag));
                                Produkte[itemcount + 3] = Modulhelferlein.df.format(Modulhelferlein.round2dec(ZBetrag / 107 * 100));
                                Produkte[itemcount + 4] = resultBD.getString("BESTELLUNG_DETAIL_ANZAHL");
                                Produkte[itemcount + 5] = resultBuch.getString("BUCH_ISBN");
                            }
                            System.out.println("Schreibe Position " + itemcount.toString() + ": " + Produkte[itemcount] + ", " + Produkte[itemcount + 1] + ", " + Produkte[itemcount + 2] + ", " + Produkte[itemcount + 3] + ", " + Produkte[itemcount + 4] + ", " + Produkte[itemcount + 5]);
                            Betrag = Betrag + ZBetrag;
                            zeilenNr = zeilenNr + 1;
                            Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                            itemcount = itemcount + 6;
                        } // while

// Schreibe Summen
                        switch (result.getInt("BESTELLUNG_LAND")) {
                            case 0: //Inland - Deutsch : Privatkunde = Gesch�ftskunde
                                // Umsatzsteuer ist auszuweisen
                                zeilenNr = zeilenNr + 1;
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Gesamtpreis");
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                zeilenNr = zeilenNr + 1;
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Nettogesamtbetrag");
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec((Gesamt19 / 119 * 100) + (Gesamt7 / 107 * 100))));

                                zeilenNr = zeilenNr + 1;
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, " 7% Mehrwertsteuer");
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt7 - Gesamt7 / 107 * 100)));

                                if (Gesamt19 > 0) {
                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "19% Mehrwertsteuer");
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt19 - Gesamt19 / 119 * 100)));
                                }

                                zeilenNr = zeilenNr + 1;
                                Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                    zeilenNr = zeilenNr + 1;
                                    Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                    Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Versandkosten");
                                    AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Gesamtpreis mit Versand");
                                    AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                }
                                break;
                            case 10: // EU-Ausland - Deutsch
                            case 11: // EU-Asuland - Englisch
                                if (result.getBoolean("BESTELLUNG_PRIVAT")) { // Privatkunde
                                    // Umsatzsteuer ist auszuweisen
                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtpreis[Sprache]);
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 1) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheNetto[Sprache]);
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec((Gesamt19 - Gesamt19 / 119 * 100) + (Gesamt7 - Gesamt7 / 107 * 100))));

                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, " 7% " + SpracheUmsatzsteuer[Sprache]);
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag - Betrag / 107 * 100)));

                                    if (Gesamt19 > 0) {
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "19% " + SpracheUmsatzsteuer[Sprache]);
                                        AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag - Betrag / 107 * 100)));
                                    }
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                    if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                        Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                        zeilenNr = zeilenNr + 1;
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheVersandkosten[Sprache]);
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtVersand[Sprache]);
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                    }
                                } else { //Gesch�ftskunde
                                    // Umsatzsteuer ist nicht auszuweisen
                                    Betrag = Gesamt19 / 119 * 100 + Gesamt7 / 107 * 100;
                                    ReverseCharge = true;
                                    setDuePayable = setTaxBasisTotal;
                                    setGrandTotal = setTaxBasisTotal;

                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtpreis[Sprache]);
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheNetto[Sprache]);
                                    Betrag = Betrag / 107 * 100;
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheEULieferung[Sprache]);
                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheReverseCharge[Sprache]);

                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                    if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                        Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                        zeilenNr = zeilenNr + 1;
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheVersandkosten[Sprache]);
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtVersand[Sprache]);
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                    }
                                }
                                break;
                            case 20: // Nicht-EU-Ausland - Deutsch
                            case 21: // Nicht-EU-Ausland - Englisch
                                // Privatkunde = Gesch�ftskunde
                                // keine Steuer
                                setDuePayable = setTaxBasisTotal;
                                setGrandTotal = setTaxBasisTotal;

                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, SpracheGesamtpreis[Sprache]);
                                AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 1) * 11, 540, Basiszeile - 2 - (zeilenNr + 1) * 11);

                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 2) * 11, SpracheSteuerfrei1[Sprache]);
                                Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 3) * 11, SpracheSteuerfrei2[Sprache]);

                                Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 3) * 11, 540, Basiszeile - 2 - (zeilenNr + 3) * 11);

                                if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                    Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 4) * 11, 540, Basiszeile - 2 - (zeilenNr + 4) * 11);
                                    Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 4) * 11, SpracheVersandkosten[Sprache]);
                                    AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 4) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                    Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 5) * 11, SpracheGesamtVersand[Sprache]);
                                    AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 5) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 5) * 11, 540, Basiszeile - 2 - (zeilenNr + 5) * 11);

                                }
                                break;
                        } // switch
                        cos.close();
                    } // if - Else-Zweig mehr als 8 Bestellungen
                    System.out.println("alle Bestelldetails geschrieben");
                    setLineTotal = Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt19 / 119 * 100 + Gesamt7 / 107 * 100 + result.getFloat("BESTELLUNG_VERSAND")));
                    setChargeTotal = "0";
                    setAllowanceTotal = "0";
                    setTaxBasisTotal = setLineTotal;
                    setTaxTotal = Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt19 - Gesamt19 / 119 * 100 + Gesamt7 - Gesamt7 / 107 * 100));
                    setGrandTotal = Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt19 + Gesamt7 + result.getFloat("BESTELLUNG_VERSAND")));
                    setTotalPrepaid = "0";
                    setDuePayable = setGrandTotal;

                    // bei Rechnungen ohne UStr gilt setDuePayable = setTaxBasisTotal
                    /*
                        switch (Buyerland) {
                            case "": // Inland : Privatkunde = Gesch�ftskunde
                            case "DEUTSCHLAND" : // Umsatzsteuer ist auszuweisen
                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Gesamtpreis");
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Nettogesamtbetrag");
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec((Gesamt19 / 119 * 100) + (Gesamt7 / 107 * 100))));

                                    zeilenNr = zeilenNr + 1;
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, " 7% Mehrwertsteuer");
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt7 - Gesamt7 / 107 * 100)));

                                    if (Gesamt19 > 0) {
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "19% Mehrwertsteuer");
                                        AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamt19 - Gesamt19 / 119 * 100)));
                                    }

                                    zeilenNr = zeilenNr + 1;
                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                    if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                        zeilenNr = zeilenNr + 1;
                                        Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Versandkosten");
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "Gesamtpreis mit Versand");
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                    }
                                break;
                            case "NIEDERLANDE: // EU - Unterscheidung Privatkunde - Gesch�ftskunde
                            case "�STERREICH":
                            case "ITALIEN":
                            case "PORTUGAL":
                            case "IRLAND"
                                    if (result.getBoolean("BESTELLUNG_PRIVAT")) { // Privatkunde, Umsatzsteuer ist auszuweisen
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtpreis[Sprache]);
                                        AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                        
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 1) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheNetto[Sprache]);
                                        AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec((Gesamt19 - Gesamt19 / 119 * 100) + (Gesamt7 - Gesamt7 / 107 * 100))));

                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, " 7% " + SpracheUmsatzsteuer[Sprache]);
                                        AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag - Betrag / 107 * 100)));

                                        if (Gesamt19 > 0) {
                                            zeilenNr = zeilenNr + 1;
                                            Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, "19% " + SpracheUmsatzsteuer[Sprache]);
                                            AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag - Betrag / 107 * 100)));
                                        }
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                        if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                            Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                            zeilenNr = zeilenNr + 1;
                                            Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                            Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheVersandkosten[Sprache]);
                                            AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                            zeilenNr = zeilenNr + 1;
                                            Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtVersand[Sprache]);
                                            AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                            Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                        }
                                    } else { //Gesch�ftskunde - keine Umsatzsteuer - Reverse Charge, innergeimnschaftliche Lieferung
                                        ReverseCharge = true;
                                        setDuePayable = setTaxBasisTotal;
                                        setGrandTotal = setTaxBasisTotal;

                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtpreis[Sprache]);
                                        AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheNetto[Sprache]);
                                        Betrag = Betrag / 107 * 100;
                                        AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheEULieferung[Sprache]);
                                        zeilenNr = zeilenNr + 1;
                                        Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheReverseCharge[Sprache]);

                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);

                                        if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                            Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                            zeilenNr = zeilenNr + 1;
                                            Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                            Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheVersandkosten[Sprache]);
                                            AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                            zeilenNr = zeilenNr + 1;
                                            Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr) * 11, SpracheGesamtVersand[Sprache]);
                                            AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                            Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr) * 11, 540, Basiszeile - 2 - (zeilenNr) * 11);
                                        }
                                    }
                                break;
                            case "SCHWEIZ": // Drittland Schweiz
                            default: // Drittland sonstiges: Privatkunde = Gesch�ftskunde -  keine Umsatzsteuer
                                    setDuePayable = setTaxBasisTotal;
                                    setGrandTotal = setTaxBasisTotal;

                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 1) * 11, SpracheGesamtpreis[Sprache]);
                                    AusgabeDB(cos, fontBold, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 1) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));

                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 1) * 11, 540, Basiszeile - 2 - (zeilenNr + 1) * 11);

                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 2) * 11, SpracheSteuerfrei1[Sprache]);
                                    Ausgabe(cos, fontBold, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 3) * 11, SpracheSteuerfrei2[Sprache]);

                                    Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 3) * 11, 540, Basiszeile - 2 - (zeilenNr + 3) * 11);

                                    if (result.getFloat("BESTELLUNG_VERSAND") > 0) {
                                        Betrag = Betrag + result.getFloat("BESTELLUNG_VERSAND");
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 4) * 11, 540, Basiszeile - 2 - (zeilenNr + 4) * 11);
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 4) * 11, SpracheVersandkosten[Sprache]);
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 4) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(result.getFloat("BESTELLUNG_VERSAND") * 1.0)));
                                        Ausgabe(cos, fontPlain, 9, Color.BLACK, 100, Basiszeile - (zeilenNr + 5) * 11, SpracheGesamtVersand[Sprache]);
                                        AusgabeDB(cos, fontPlain, 9, Color.BLACK, 520, Basiszeile - (zeilenNr + 5) * 11, Modulhelferlein.df.format(Modulhelferlein.round2dec(Betrag)));
                                        Linie(cos, 1, 55, Basiszeile - 2 - (zeilenNr + 5) * 11, 540, Basiszeile - 2 - (zeilenNr + 5) * 11);
                                    }
                                break;
                        
                     */
// add XMP metadata
                    XMPMetadata xmp = XMPMetadata.createXMPMetadata();

                    try {
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
                    } catch (BadFieldValueException e) {
                        // won't happen here, as the provided value is valid
                        throw new IllegalArgumentException(e);
                    } catch (TransformerException ex) {
                        Modulhelferlein.Fehlermeldung("PDF/A-Erstellung Honorabrechung", "TransformerException-Exception: " + ex.getMessage());
                    }
                    System.out.println("XMP Metadata geschrieben");
// sRGB output intent
                    //InputStream colorProfile = briefRechnungMahnung.class.getResourceAsStream("/org/apache/pdfbox/resources/pdfa/sRGB.icc");
                    InputStream colorProfile = briefRechnungMahnung.class.getResourceAsStream("sRGB.icc");
                    PDOutputIntent intent = new PDOutputIntent(document, colorProfile);
                    intent.setInfo("sRGB IEC61966-2.1");
                    intent.setOutputCondition("sRGB IEC61966-2.1");
                    intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
                    intent.setRegistryName("http://www.color.org");
                    document.getDocumentCatalog().addOutputIntent(intent);

                    System.out.println("Colorprofil geschrieben");
// Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();
                    //} try PDPageContentStream cos = new PDPageContentStream(document, page))
                }
                Modulhelferlein.Infomeldung("Rechnung Nr. " + RechNr, "ist als PDF gespeichert unter ", outputFileName);

                // E-Rechnung erstellen
                if (ERechnung != 0) {
                    ModulERechnung.createZUGFeRDInvoiceDocuments(ERechnung,
                            InvoiceNumber,
                            InvoiceLanguage,
                            InvoiceDate,
                            InvoiceName,
                            BuyerName,
                            BuyerPLZ,
                            BuyerStrasse,
                            BuyerOrt,
                            BuyerLand,
                            BuyerUID,
                            BuyerBestellzeichen,
                            Produkte,
                            ReverseCharge,
                            setLineTotal,
                            setChargeTotal,
                            setAllowanceTotal,
                            setTaxBasisTotal,
                            setTaxTotal,
                            setDuePayable,
                            setTotalPrepaid,
                            setGrandTotal,
                            outputFileName);
                    /**
                     * ERechnung.createXRechnungDocuments(InvoiceNumber,
                     * InvoiceLanguage, InvoiceDate, InvoiceName, BuyerName,
                     * BuyerPLZ, BuyerStrasse, BuyerOrt, BuyerLand, BuyerUID,
                     * BuyerBestellzeichen, Produkte, ReverseCharge,
                     * setLineTotal, setChargeTotal, setAllowanceTotal,
                     * setTaxBasisTotal, setTaxTotal, setDuePayable,
                     * setTotalPrepaid, setGrandTotal, outputFileName);
                     *
                     */
//helferlein.Infomeldung("Invoice erstellt ");
                }

                try {
                    switch (ERechnung) {
                        case 0 : Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                            break;
                        case 1 : Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + ".XRechnung.xml" + "\"");
                            break;
                        case 2 : Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + ".ZUGFeRD.pdf" + "\"");
                            break;
                    }
                } catch (IOException exept) {
                    Modulhelferlein.Fehlermeldung("Brief Rechnung", "Ausgabe Brief: IO-Exception: ", exept.getMessage());
                } // try Brief ausgeben

                // Adressetikett drucken
                if (result.getInt("BESTELLUNG_KUNDE") > 0) {
                    Zeile1 = resultK.getString("ADRESSEN_ZUSATZ_1");
                    Zeile2 = Modulhelferlein.makeAnrede(resultK.getString("ADRESSEN_NAMENSZUSATZ"), resultK.getString("ADRESSEN_VORNAME"), resultK.getString("ADRESSEN_NAME"));
                    Zeile3 = resultK.getString("ADRESSEN_ZUSATZ_2");
                    Zeile4 = resultK.getString("ADRESSEN_STRASSE");
                    Zeile5 = resultK.getString("ADRESSEN_PLZ") + " " + resultK.getString("ADRESSEN_ORT");
                    Zeile6 = resultK.getString("ADRESSEN_ZUSATZ_3");
                } else {
                    Zeile1 = result.getString("BESTELLUNG_ZEILE_1");
                    Zeile2 = result.getString("BESTELLUNG_ZEILE_2");
                    Zeile3 = result.getString("BESTELLUNG_ZEILE_3");
                    Zeile4 = result.getString("BESTELLUNG_ZEILE_4");
                    Zeile5 = result.getString("BESTELLUNG_ZEILE_5");
                    Zeile6 = result.getString("BESTELLUNG_ZEILE_6");
                } // if
                String[] argumente = {Zeile1, Zeile2, Zeile3, Zeile4, Zeile5, Zeile6};
                _DlgAdresseDrucken.main(argumente);

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Brief Rechnung", "SQL-Exception: SQL-Anfrage nicht moeglich: ",
                        exept.getMessage());
                // System.exit(1);
            } // try SQL-Anfragen an die Datenbank

        } // if conn != null

        try {
            if (resultK != null) {
                resultK.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }
        try {
            if (resultA != null) {
                resultA.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }
        try {
            if (resultBD != null) {
                resultBD.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }
        try {
            if (resultBuch != null) {
                resultBuch.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }
        try {
            if (SQLAnfrageK != null) {
                SQLAnfrageK.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }
        try {
            if (SQLAnfrageA != null) {
                SQLAnfrageA.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }
        try {
            if (SQLAnfrageBD != null) {
                SQLAnfrageBD.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }
        try {
            if (SQLAnfrageBuch != null) {
                SQLAnfrageBuch.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }
    } // void brief

} // clas
