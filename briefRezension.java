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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;

public class briefRezension {

    private static JFrame Dialog = new JFrame("Carola Hartmann Miles Verlag");
    private static JLabel dlgLabel = new JLabel("");
    private static JPanel dlgPanel;
    private static JScrollPane dlgScrollpane;
    private static JTextArea dlgTextarea;

    private static void Bildschirmausgabe(String zeile) {
        dlgTextarea.append(zeile + "\r\n");
        dlgTextarea.update(dlgTextarea.getGraphics());
    }

    public static void brief2DOC(Integer Auswahl, String BestNr) {

        ResultSet resultR;  // Rezension
        ResultSet resultRD;  // Rezension Details
        ResultSet resultB;  // Buch
        ResultSet resultAZ;  // Zeitschrift
        ResultSet resultAR; // Adresse Rezensent
        ResultSet resultAA; // Adresse Autor

        Statement SQLStatementR = null;
        Statement SQLStatementRD = null;
        Statement SQLStatementAZ = null;
        Statement SQLStatementB = null;
        Statement SQLStatementAR = null;
        Statement SQLStatementAA = null;
        Connection conn = null;

        Integer Startzeile = 0;
        Integer ZeilenNr = 0;

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
        dlgLabel.setText("Erzeuge Anschreiben");
        Dialog.setVisible(true);

        Integer twipsPerInch = 1440;

        try { // Datenbank-Treiber laden
            Class.forName(ModulHelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            ModulHelferlein.Fehlermeldung("Treiber nicht gefunden: " + exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verbindung nicht moeglich: " + exept.getMessage());
            System.exit(1);
        } // try Verbindung zur Datenbank über die JDBC-Brücke

        if (conn != null) {

            try {

                SQLStatementR = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLStatementRD = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLStatementAZ = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLStatementAR = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLStatementAA = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLStatementB = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                resultR = SQLStatementR.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS WHERE REZENSIONEN_AUS_NUMMER = '" + BestNr + "'");
                resultR.next();

                resultRD = SQLStatementRD.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER = '" + BestNr + "'");
                resultRD.next();

                resultAR = SQLStatementAR.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultR.getString("REZENSIONEN_AUS_REZENSENT") + "'");
                resultAR.next();

                resultAZ = SQLStatementAZ.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultR.getString("REZENSIONEN_AUS_ZEITSCHRIFT") + "'");
                resultAZ.next();

                resultB = SQLStatementB.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                resultB.next();

                resultAA = SQLStatementAA.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultB.getString("BUCH_AUTOR") + "'");
                resultAA.next();

                String ISBN = "";
                Integer Multi = 0;
                String outputFileName = "";

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
                    // Adresse
                    paragraph = cell.addParagraph();
                    run = paragraph.createRun();
                    run.setFontFamily("Arial");
                    run.setFontSize(12);

                    if (resultAR.getString("ADRESSEN_NAMENSZUSATZ").equals("")) {
                        run.setText(resultAR.getString("ADRESSEN_VORNAME") + " " + resultAR.getString("ADRESSEN_NAME"));
                    } else {
                        run.setText(resultAR.getString("ADRESSEN_NAMENSZUSATZ") + " " + resultAR.getString("ADRESSEN_VORNAME") + " " + resultAR.getString("ADRESSEN_NAME"));
                    }

                    run.addBreak();
                    run.setText(resultAR.getString("ADRESSEN_ZUSATZ_1"));
                    run.addBreak();
                    run.setText(resultAR.getString("ADRESSEN_ZUSATZ_2"));
                    run.addBreak();
                    run.setText(resultAR.getString("ADRESSEN_STRASSE"));
                    run.addBreak();
                    run.setText("");
                    run.setText(resultAR.getString("ADRESSEN_ORT"));

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
                    run.setText(" ");
                    run.addBreak();
                    run.setText(" ");
                    run.addBreak();
                    run.setText(" ");
                    run.addBreak();
                    run.setText("Datum: ");
                    run.addBreak();
                    run.addBreak();
                    run.setBold(false);
                    run.setText(" ");

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
                    run.setText(" ");
                    run.addBreak();
                    run.setText(ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    run.addBreak();
                    run.addBreak();
                    run.setText(" ");
                    run.addBreak();
                    run.setText(" ");

                    // Betreff
                    Bildschirmausgabe("... erzeuge Textblock Betreffzeile ...");
                    XWPFTable tableBetreff = document.createTable(1, 1);
                    type = tableBetreff.getCTTbl().getTblPr().addNewTblLayout();
                    type.setType(STTblLayoutType.FIXED);
                    tableBetreff.getCTTbl().getTblPr().unsetTblBorders();
                    tableBetreff.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(9000));

                    rows = tableBetreff.getRow(0);
                    cell = rows.getCell(0);
                    XWPFParagraph paragraphBetreff = cell.getParagraphs().get(0);
                    paragraphBetreff.setAlignment(ParagraphAlignment.LEFT);
                    XWPFRun runBetreff = paragraphBetreff.createRun();
                    runBetreff.setFontFamily("Arial");
                    runBetreff.setFontSize(12);
                    runBetreff.setBold(true);
                    runBetreff.setText("Rezensionsexemplare des Carola Hartmann Miles-Verlag");

                    // Anrede
                    Bildschirmausgabe("... erzeuge Textblock Anrede ...");
                    XWPFParagraph paragraphAnrede = document.createParagraph();
                    paragraphAnrede.setAlignment(ParagraphAlignment.LEFT);
                    XWPFRun runAnrede = paragraphAnrede.createRun();
                    runAnrede.addBreak();
                    runAnrede.addBreak();
                    runAnrede.setText(resultAR.getString("ADRESSEN_ANREDE") + " " + resultAR.getString("ADRESSEN_NAMENSZUSATZ") + " " + resultAR.getString("ADRESSEN_NAME") + ",");
                    runAnrede.addBreak();
                    switch (Auswahl) {
                        case 0: // Anfrage
                            runAnrede.setText("herzlichen Dank für Ihre Anfrage vom "
                                    + resultR.getString("REZENSIONEN_AUS_DATUM") + ".");
                            runAnrede.setText("Beiliegend übersende ich Ihnen ein Exemplar unserer Neuerscheinung.");
                            break;
                        case 1: // Wunsch des Autore
                            if (resultAA.getString("ADRESSEN_ANREDE").contains("Herr")) {
                                runAnrede.setText("unser Autor Herr " + resultAA.getString("ADRESSEN_VORNAME") + " " + resultAA.getString("ADRESSEN_NAME") + " bat mich, Ihnen sein neuestes Werk mit der Bitte um eine Rezension zuzusenden.");
                                runAnrede.addBreak();
                                runAnrede.setText("Gerne komme ich diesem Wunsch nach.");
                            } else {
                                runAnrede.setText("unsere Autorin Frau " + resultAA.getString("ADRESSEN_VORNAME") + " " + resultAA.getString("ADRESSEN_NAME") + " bat mich, Ihnen ihr neuestes Werk mit der"
                                        + "Bitte um eine Rezension zuzusenden.");
                                runAnrede.addBreak();
                                runAnrede.setText("Gerne komme ich diesem Wunsch nach.");
                            }
                            break;
                        case 2: // Initiative Verlag
                            runAnrede.setText("beiliegend übersende ich Ihnen ein Exemplar unserer (Neu)erscheinung.");
                            break;
                    }

                    runAnrede.addBreak();
                    runAnrede.addBreak();

                    do {
                        resultB = SQLStatementB.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                        resultB.next();

                        resultAA = SQLStatementAA.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultB.getString("BUCH_AUTOR") + "'");
                        resultAA.next();

                        Multi = Multi + 1;
                        ISBN = resultB.getString("BUCH_ISBN");

                        runAnrede.setText(resultAA.getString("ADRESSEN_VORNAME") + ", " + resultAA.getString("ADRESSEN_NAME") + ": " + resultB.getString("BUCH_TITEL"));
                        runAnrede.addBreak();
                        runAnrede.setText("ISBN " + resultB.getString("BUCH_ISBN") + ", Seiten: " + resultB.getString("BUCH_SEITEN") + ", Preis: " + resultB.getString("BUCH_PREIS") + " Euro.");
                        runAnrede.addBreak();
                        runAnrede.addBreak();
                    } while (resultRD.next());

                    if (resultR.getString("REZENSIONEN_AUS_ZEITSCHRIFT").equals("")) {
                        runAnrede.setText("Ich würde mich freuen, wenn Sie unsere Neuerscheinung besprechen würden.");
                    } else {
                        runAnrede.setText("Ich würde mich freuen, wenn Sie unsere (Neu)erscheinung in der Zeitschrift");
                        runAnrede.addBreak();
                        runAnrede.setText("'" + resultAZ.getString("ADRESSEN_ZEITSCHRIFT") + "' besprechen würden.");
                    }
                    runAnrede.addBreak();
                    runAnrede.setText("Für Fragen stehe ich gerne zur Verfügung, ebenso sende ich Ihnen bei Bedarf eine Bild-Datei des Buchcovers zu.");
                    runAnrede.addBreak();
                    runAnrede.addBreak();

                    // Schlussformel
                    Bildschirmausgabe("... erzeuge Schlussformel ...");
                    runAnrede.setText("Mit freundlichen Grüßen");
                    runAnrede.addBreak();
                    runAnrede.addBreak();
                    runAnrede.addBreak();
                    runAnrede.setText("Carola Hartmann");
                    runAnrede.addBreak();
                    runAnrede.setText("Diplom Kauffrau");

                    // Schlussformel
                    // create footer start
                    Bildschirmausgabe("... erzeuge Fußzeile ...");
                    XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

                    XWPFParagraph paragraphFooter = footer.createParagraph();
                    paragraphFooter.setAlignment(ParagraphAlignment.CENTER);
                    XmlCursor cursor = paragraphFooter.getCTP().newCursor();
                    XWPFTable tableFooter = footer.insertNewTbl(cursor);

                    String[] strFooter1 = {"Carola Hartmann Miles Verlag", "Alt Kladow 16d", "14089 Berlin"};
                    String[] strFooter2 = {"Dipl.Kff. Carola Hartmann", "Telefon +49 (0)30 36 28 86 77", "Volksbank Berlin"};
                    String[] strFooter3 = {"StrNr 19 332 6006 5", "E-Mail miles-verlag@t-online.de", "IBAN DE61 1009 0000 2233 8320 17"};
                    String[] strFooter4 = {"UStrID DE 269 369 5", "www.miles-verlag.jimdo.com", "BIC BEVODEBB"};

                    XWPFTableRow tableFooterrow1 = tableFooter.createRow();
                    for (int i = 0; i < 3; i++) {
                        cell = tableFooterrow1.createCell();
                        CTTblWidth tblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
                        tblWidth.setW(BigInteger.valueOf(3666));
                        tblWidth.setType(STTblWidth.DXA);
                        paragraphFooter = cell.getParagraphs().get(0);
                        paragraphFooter.setAlignment(ParagraphAlignment.CENTER);
                        XWPFRun runFooter = paragraphFooter.createRun();
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
                    if (Multi > 1) {
                        outputFileName = ModulHelferlein.pathRezensionen
                                + "\\"
                                + "Rezension-Multi-"
                                + resultAR.getString("ADRESSEN_NAME")
                                + "-"
                                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                                + ".docx";
                    } else {
                        outputFileName = ModulHelferlein.pathRezensionen
                                + "\\"
                                + "Rezension-" + ISBN + "-"
                                + resultAR.getString("ADRESSEN_NAME")
                                + "-"
                                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                                + ".docx";
                    }

                    try ( // speichern
                            FileOutputStream out = new FileOutputStream(new File(outputFileName))) {
                        document.write(out);
                    }

                    ModulHelferlein.Infomeldung("Brief an " + resultAR.getString("ADRESSEN_NAME") + " ist als DOC gespeichert!");
                    Dialog.setVisible(false);
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c start " + "'" + outputFileName + "'");
                    } catch (IOException exept) {
                        ModulHelferlein.Fehlermeldung("Exception: " + exept.getMessage());
                    }// try Brief ausgeben

                    // Adressetikett drucken
                    String Zeile1 = resultAR.getString("ADRESSEN_ZUSATZ_1");
                    String Zeile2 = "";
                    if (resultAR.getString("ADRESSEN_NAMENSZUSATZ").equals("")) {
                        Zeile2 = resultAR.getString("ADRESSEN_VORNAME") + " " + resultAR.getString("ADRESSEN_NAME");
                    } else {
                        Zeile2 = resultAR.getString("ADRESSEN_NAMENSZUSATZ") + " " + resultAR.getString("ADRESSEN_VORNAME") + " " + resultAR.getString("ADRESSEN_NAME");
                    }
                    String Zeile3 = resultAR.getString("ADRESSEN_ZUSATZ_2");
                    String Zeile4 = resultAR.getString("ADRESSEN_STRASSE");
                    String Zeile5 = "";
                    String Zeile6 = resultAR.getString("ADRESSEN_PLZ") + " " + resultAR.getString("ADRESSEN_ORT");
                    String[] args = {Zeile1, Zeile2, Zeile3, Zeile4, Zeile5, Zeile6};
                    _DlgAdresseDrucken.main(args);

                } catch (IOException exept) {
                    ModulHelferlein.Fehlermeldung("IO-Exception: " + exept.getMessage());
                } catch (InvalidFormatException ex) {
                    ModulHelferlein.Fehlermeldung("InvalidFormat-Exception: " + ex.getMessage());
                    //Logger.getLogger(briefRezension.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            }
        } //if conn != null
    }

    ;
    
    
    
    
    public static void brief2PDF(Integer Auswahl, String BestNr) {

        ResultSet resultR;  // Rezension
        ResultSet resultRD; // Rezension Details
        ResultSet resultB;  // Buch
        ResultSet resultAZ; // Zeitschrift
        ResultSet resultAR; // Adresse Rezensent
        ResultSet resultAA = null; // Adresse Autor

        Statement SQLStatementR = null;
        Statement SQLStatementRD = null;
        Statement SQLStatementAZ = null;
        Statement SQLStatementB = null;
        Statement SQLStatementAR = null;
        Statement SQLStatementAA = null;
        Connection conn = null;

        Integer Startzeile = 0;
        Integer ZeilenNr = 0;

        try { // Datenbank-Treiber laden
            Class.forName(ModulHelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            ModulHelferlein.Fehlermeldung("Treiber nicht gefunden: " + exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verbindung nicht moeglich: " + exept.getMessage());
            System.exit(1);
        } // try Verbindung zur Datenbank über die JDBC-Brücke

        if (conn != null) {

            try {

                SQLStatementR = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLStatementRD = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLStatementAZ = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLStatementAR = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLStatementAA = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLStatementB = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                resultR = SQLStatementR.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS WHERE REZENSIONEN_AUS_NUMMER = '" + BestNr + "'");
                resultR.next();

                resultRD = SQLStatementRD.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER = '" + BestNr + "'");
                resultRD.last();
                int Anzahl = resultRD.getRow();
                resultRD.first();

                resultAR = SQLStatementAR.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultR.getString("REZENSIONEN_AUS_REZENSENT") + "'");
                resultAR.next();

                resultAZ = SQLStatementAZ.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultR.getString("REZENSIONEN_AUS_ZEITSCHRIFT") + "'");
                resultAZ.next();

                resultB = SQLStatementB.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                resultB.next();

                // Autor holen
                String[] col_Autorliste = resultB.getString("BUCH_AUTOR").split(",");
                String AutorEintrag = "";
                Boolean Autoren = false;
                if (col_Autorliste.length > 1) {
                    Autoren = true;
                }
                for (String strAutor : col_Autorliste) {
                    resultAA = SQLStatementAA.executeQuery(
                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                    resultAA.next();
                    AutorEintrag = AutorEintrag
                            + resultAA.getString("ADRESSEN_Name") + ", "
                            + resultAA.getString("ADRESSEN_Vorname") + "; ";
                }
                AutorEintrag = AutorEintrag.substring(0, AutorEintrag.length() - 2);
                Boolean Herausgeber = false;
                if (resultB.getBoolean("BUCH_HERAUSGEBER")) {
                    Herausgeber = true;
                }

                if (!Autoren) {
                    resultAA = SQLStatementAA.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultB.getString("BUCH_AUTOR") + "'");
                    resultAA.next();
                }

                String ISBN = "";
                Integer Multi = 0;
                String outputFileName = "";

                // Create a document and add a page to it
                try {
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
                        ModulHelferlein.Fehlermeldung("Brief Rezension", "File not found-Exception", "Keine Bild-Datei gefunden " + fnfex.getMessage());
                        System.out.println("No image for you");
                    }
//helferlein.Infomeldung("schreibe fußzeile");
                    // Fu?zeile
                    AusgabeLB(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
                    AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
                    AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
                    AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");

                    AusgabeLB(cos, fontBold, 10, Color.GRAY, 230, 35, ModulHelferlein.CheckStr("Alt Kladow 16d"));
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
                    AusgabeLB(cos, fontPlain, 8, Color.BLACK, 50, 751, ModulHelferlein.CheckStr("C. Hartmann Miles-Verlag - Alt Kladow 16d - 14089 Berlin"));

                    // Datum
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 354, 655, "Datum: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
//helferlein.Infomeldung("schreibe adresse");

// Adresse
                    String[] AdressZeile = {"", "", "", "", "", "", ""};
                    String[] args = {"", "", "", "", "", "", ""};

                    args[0] = resultAR.getString("ADRESSEN_ZUSATZ_1");
                    args[1] = ModulHelferlein.makeAnrede(resultAR.getString("ADRESSEN_NAMENSZUSATZ"),
                            resultAR.getString("ADRESSEN_VORNAME"),
                            resultAR.getString("ADRESSEN_NAME"));
                    args[2] = resultAR.getString("ADRESSEN_ZUSATZ_2");
                    args[3] = resultAR.getString("ADRESSEN_STRASSE");
                    args[4] = resultAR.getString("ADRESSEN_PLZ") + " " + resultAR.getString("ADRESSEN_ORT");
                    args[5] = resultAR.getString("ADRESSEN_ZUSATZ_3");

                    Integer AdressZeilenNr = 1;
                    for (int i = 0; i < 6; i++) {
                        if (!args[i].equals("")) {
                            AdressZeile[AdressZeilenNr] = args[i];
                            AdressZeilenNr = AdressZeilenNr + 1;
                        }
                    }
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 730, AdressZeile[1]);
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 715, AdressZeile[2]);
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 700, AdressZeile[3]);
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 685, AdressZeile[4]);
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 670, AdressZeile[5]);
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 655, AdressZeile[6]);
                    System.out.println("... Adresse geschrieben");

                    // Betreff
                    if (resultR.getInt("REZENSIONEN_AUS_TYP") == 4) {
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 575, "Freiexemplar(e) des Carola Hartmann Miles-Verlag");
                    } else {
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 575, "Rezensionsexemplar(e) des Carola Hartmann Miles-Verlag");
                    }
                    System.out.println("... Betreffzeile geschrieben");

                    // Anrede
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 530, ModulHelferlein.makeAnrede(resultAR.getString("ADRESSEN_ANREDE"), resultAR.getString("ADRESSEN_NAMENSZUSATZ"), resultAR.getString("ADRESSEN_NAME")) + ",");
                    System.out.println("... Anrede geschrieben");

                    // Einleitung
                    switch (resultR.getInt("REZENSIONEN_AUS_TYP")) {
                        case 0: // Anfrage
                            System.out.println("... Typ 0 - Anfrage");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "herzlichen Dank für Ihre Anfrage vom " + ModulHelferlein.printDateFormat("dd.MM.yyyy", ModulHelferlein.SQLDateString2Date(resultR.getString("REZENSIONEN_AUS_DATUM"))) + ".");
                            if (Anzahl == 1) {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "Beiliegend übersende ich Ihnen das gewünschte Exemplar unserer (Neu)erscheinung.");
                            } else {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "Beiliegend übersende ich Ihnen die gewünschten Exemplare unserer (Neu)erscheinungen.");
                            }
                            Startzeile = 460;
                            break;
                        case 1: // Wunsch des Autors/Herausgeber
                            System.out.println("... Typ 1 - Wunsch Herausgeber/Autoren");
                            if (Herausgeber) {
                                if (Autoren) {
                                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "unsere Herausgeber " + AutorEintrag + " baten mich,");
                                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "Ihnen ihr neuestes Werk mit der Bitte um eine Rezension zuzusenden.");
                                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 470, "Gerne komme ich diesem Wunsch nach.");
                                } else {
                                    if (resultAA.getString("ADRESSEN_ANREDE").contains("Herr")) {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "unser Herausgeber Herr " + resultAA.getString("ADRESSEN_VORNAME") + " " + resultAA.getString("ADRESSEN_NAME") + " bat mich, Ihnen sein neuestes Werk mit der");
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "Bitte um eine Rezension zuzusenden.");
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 470, "Gerne komme ich diesem Wunsch nach.");
                                    } else {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "unsere Herausgeberein Frau " + resultAA.getString("ADRESSEN_VORNAME") + " " + resultAA.getString("ADRESSEN_NAME") + " bat mich, Ihnen ihr neuestes Werk mit der");
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "Bitte um eine Rezension zuzusenden.");
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 470, "Gerne komme ich diesem Wunsch nach.");
                                    }
                                }
                            } else {
                                if (Autoren) {
                                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "unsere Autoren " + AutorEintrag + " baten mich, Ihnen ihr neuestes Werk mit der");
                                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "Bitte um eine Rezension zuzusenden.");
                                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 470, "Gerne komme ich diesem Wunsch nach.");
                                } else {
                                    if (resultAA.getString("ADRESSEN_ANREDE").contains("Herr")) {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "unser Autor Herr " + resultAA.getString("ADRESSEN_VORNAME") + " " + resultAA.getString("ADRESSEN_NAME") + " bat mich, Ihnen sein neuestes Werk mit der");
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "Bitte um eine Rezension zuzusenden.");
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 470, "Gerne komme ich diesem Wunsch nach.");
                                    } else {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "unsere Autorin Frau " + resultAA.getString("ADRESSEN_VORNAME") + " " + resultAA.getString("ADRESSEN_NAME") + " bat mich, Ihnen ihr neuestes Werk mit der");
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "Bitte um eine Rezension zuzusenden. Gerne komme ich diesem Wunsch nach.");
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 470, "Gerne komme ich diesem Wunsch nach.");
                                    }
                                }
                            }
                            Startzeile = 445;
                            break;
                        case 3: // Initiative Verlag
                            System.out.println("... Typ 3 - Initiative Verlag");
                            if (Anzahl == 1) {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "beiliegend übersende ich Ihnen ein Exemplar unserer (Neu)erscheinung.");
                            } else {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "beiliegend übersende ich Ihnen je ein Exemplar unserer (Neu)erscheinungen.");
                            }
                            Startzeile = 475;
                            break;
                        case 4: // Freiexemplar
                            System.out.println("... Typ 4 - Freiexemplar");
                            if (Anzahl == 1) {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "beiliegend übersende ich Ihnen ein Freiexxemplar unserer (Neu)erscheinung.");
                            } else {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 500, "beiliegend übersende ich Ihnen je ein Freiexemplar unserer (Neu)erscheinungen.");
                            }
                            Startzeile = 475;
                            break;
                        case 5: // ANfrage Dritter
                            System.out.println("... Typ 5 - Anfrage Dritter");

                            String Anfrage = resultR.getString("REZENSIONEN_AUS_DRITTE") + " hat uns gebeten, Ihnen";
                            if (Anzahl == 1) {
                                Anfrage = Anfrage + " ein Exemplar unserer (Neu)erscheinung ";
                            } else {
                                Anfrage = Anfrage + " je ein Exemplar unserer (Neu)erscheinungen ";
                            }
                            Anfrage = Anfrage + "für eine Rezension in der Zeitschrift \""
                                    + resultAZ.getString("ADRESSEN_ZEITSCHRIFT") + "\" zuzusenden";

                            System.out.println(Anfrage);
                            Startzeile = 500;
                            ZeilenNr = 1;

                            Anfrage = ModulSilbentrennung.formatText(Anfrage, 483, cos, fontUniPlain, 12);
                            String[] splitAnfrage = Anfrage.split("~#!#~");
                            for (int i = 0; i < splitAnfrage.length; i++) {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 57, Startzeile - 15 * (ZeilenNr - 1), splitAnfrage[i]);
                                ZeilenNr = ZeilenNr + 1;
                            }

                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * ZeilenNr, "Gerne komme ich diesem Wunsch nach. Es handelt sich um ");
                            Startzeile = Startzeile - 15 * (ZeilenNr + 2);
                            break;
                    } // switch Typ
                    System.out.println("... Einleitung geschrieben");

                    System.out.println("... schreibe Rezensionsexemplare ab Startzeile " + Startzeile.toString());
                    if (Anzahl < 8) {
                        ZeilenNr = 0;
//helferlein.Infomeldung("laufe durch die rezensionsdetails");
                        do {
                            Multi = Multi + 1;

                            resultB = SQLStatementB.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                            resultB.next();
                            ISBN = resultB.getString("BUCH_ISBN");

                            //resultAA = SQLStatementAA.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultB.getString("BUCH_AUTOR") + "'");
                            //resultAA.next();
                            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 60, (Startzeile - ZeilenNr * 15), "-");

                            // Autor holen
                            col_Autorliste = resultB.getString("BUCH_AUTOR").split(",");
                            AutorEintrag = "";
                            Autoren = false;
                            if (col_Autorliste.length > 1) {
                                Autoren = true;
                            }
                            for (String strAutor : col_Autorliste) {
                                resultAA = SQLStatementAA.executeQuery(
                                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                                resultAA.next();
                                AutorEintrag = AutorEintrag
                                        + resultAA.getString("ADRESSEN_Name") + ", "
                                        + resultAA.getString("ADRESSEN_Vorname") + "; ";
                            }
                            AutorEintrag = AutorEintrag.substring(0, AutorEintrag.length() - 2);
                            Herausgeber = false;
                            if (resultB.getBoolean("BUCH_HERAUSGEBER")) {
                                Herausgeber = true;
                            }

                            if (!Autoren) {
                                resultAA = SQLStatementAA.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultB.getString("BUCH_AUTOR") + "'");
                                resultAA.next();
                            }
                            if (Herausgeber) {
                                Ausgabe(cos, fontItalic, 12, Color.BLACK, 75, (Startzeile - ZeilenNr * 15), AutorEintrag + " (Hrsg.): " + resultB.getString("BUCH_TITEL"), 465);
                            } else {
                                Ausgabe(cos, fontItalic, 12, Color.BLACK, 75, (Startzeile - ZeilenNr * 15), AutorEintrag + ": " + resultB.getString("BUCH_TITEL"), 465);
                            }
                            ZeilenNr = ZeilenNr + 1;

                            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 75, (Startzeile - ZeilenNr * 15),
                                    "ISBN " + ModulHelferlein.makeISBN13(resultB.getString("BUCH_ISBN")) + ", "
                                    + resultB.getString("BUCH_AUFLAGE") + ". Auflage, "
                                    + resultB.getString("BUCH_JAHR") + ", "
                                    + resultB.getString("BUCH_SEITEN") + " Seiten, "
                                    + "Preis: " + resultB.getString("BUCH_PREIS") + " Euro.");

                            ZeilenNr = ZeilenNr + 1;

                        } while (resultRD.next());
                        System.out.println("... Rezensionstitel geschrieben");

                        ZeilenNr = ZeilenNr + 2;

                        // Beschreibung ausgeben - wenn Anzahl == 1
                        if (Anzahl == 1) {

                            String Beschreibung = resultB.getString("BUCH_BESCHREIBUNG");
                            //Beschreibung.replace("\\u000A                            ", Trenner);
                            Beschreibung = Beschreibung.replace("\r\n", Trenner);
                            Beschreibung = Beschreibung.replace("\n\r", Trenner);
                            Beschreibung = Beschreibung.replace("\n", Trenner);
                            Beschreibung = Beschreibung.replace("\r", Trenner);
                            Beschreibung = ModulSilbentrennung.formatText(Beschreibung, 483, cos, fontUniPlain, 12);

                            String[] splitBeschreibung = Beschreibung.split(Trenner);
                            for (int i = 0; i < splitBeschreibung.length; i++) {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 57, Startzeile - 15 * (ZeilenNr - 1), splitBeschreibung[i]);
                                ZeilenNr = ZeilenNr + 1;
                                if (Startzeile - 15 * (ZeilenNr - 1) <= 130) { //neue Seite
                                    cos.close();
                                    page = new PDPage(A4);
                                    document.addPage(page);
                                    cos = new PDPageContentStream(document, page);

                                    // Fu?zeile
                                    AusgabeLB(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
                                    AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
                                    AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
                                    AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");

                                    AusgabeLB(cos, fontBold, 10, Color.GRAY, 230, 35, ModulHelferlein.CheckStr("Alt Kladow 16d"));
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

                                    Startzeile = 700;
                                    ZeilenNr = 0;
                                }
                            }
                        } // if (Anzahl == 1)

                        switch (resultR.getInt("REZENSIONEN_AUS_TYP")) {
                            case 4: // wenn Freiexemplar dann
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 0), "Ich würde mich freuen, wenn Sie Interesse an unserer (Neu)erscheinung finden würden und");
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 1), "diese Ihren Kolleginnen und Kollegen zur Lektüre weiterempfehlen könnten.");
                                break;
                            case 0: // 
                            case 1: // 
                            case 2: //
                            case 3: //
                                if (resultR.getString("REZENSIONEN_AUS_ZEITSCHRIFT").equals("0")) {
                                    if (Anzahl == 1) {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 0), "Ich würde mich freuen, wenn Sie unsere (Neu)erscheinung besprechen würden.");
                                    } else {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 0), "Ich würde mich freuen, wenn Sie unsere (Neu)erscheinungen besprechen würden.");
                                    }
                                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 1), "");
                                } else {
                                    if (Anzahl == 1) {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 0), "Ich würde mich freuen, wenn Sie unsere (Neu)erscheinung in der Zeitschrift");
                                    } else {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 0), "Ich würde mich freuen, wenn Sie unsere (Neu)erscheinungen in der Zeitschrift");
                                    }
                                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 1), "'" + resultAZ.getString("ADRESSEN_ZEITSCHRIFT") + "' besprechen würden.");
                                }
                            case 5:
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 2), "Für Fragen stehe ich gerne zur Verfügung, ebenso sende ich Ihnen bei Bedarf eine ");
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 3), "Bild-Datei des Buchcovers zu.");
                                break;
                        }
                        // Schlussformel
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 5), ModulHelferlein.CheckStr("Mit freundlichen Grüßen"));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 9), "Carola Hartmann");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 10), "Diplom Kauffrau");
                    } else {
                        ZeilenNr = ZeilenNr + 1;
                        switch (resultR.getInt("REZENSIONEN_AUS_TYP")) {
                            case 0: // 
                            case 1: // 
                            case 2: //
                            case 3: //
                                if (resultR.getString("REZENSIONEN_AUS_ZEITSCHRIFT").equals("0")) {
                                    if (Anzahl == 1) {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 0), "Ich würde mich freuen, wenn Sie unsere (Neu)erscheinung besprechen würden.");
                                    } else {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 0), "Ich würde mich freuen, wenn Sie unsere (Neu)erscheinungen besprechen würden.");
                                    }
                                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 1), "");
                                } else {
                                    if (Anzahl == 1) {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 0), "Ich würde mich freuen, wenn Sie unsere (Neu)erscheinung in der Zeitschrift");
                                    } else {
                                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 0), "Ich würde mich freuen, wenn Sie unsere (Neu)erscheinungen in der Zeitschrift");
                                    }
                                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 1), "'" + resultAZ.getString("ADRESSEN_ZEITSCHRIFT") + "' besprechen würden.");
                                }
                            case 5:
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 2), "Für Fragen stehe ich gerne zur Verfügung, ebenso sende ich Ihnen bei Bedarf eine ");
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 3), "Bild-Datei des Buchcovers zu.");
                                break;
                        }
                        // Schlussformel
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 5), ModulHelferlein.CheckStr("Mit freundlichen Grüßen"));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 9), "Carola Hartmann");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (ZeilenNr + 10), "Diplom Kauffrau");

                        // Neue Seite für die Anlage mit den Büchern
                        cos.close();
                        page = new PDPage(A4);
                        document.addPage(page);
                        cos = new PDPageContentStream(document, page);
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 750, "Anlage");

                        // Fu?zeile
                        AusgabeLB(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
                        AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
                        AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
                        AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");

                        AusgabeLB(cos, fontBold, 10, Color.GRAY, 230, 35, ModulHelferlein.CheckStr("Alt Kladow 16d"));
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

                        Startzeile = 700;
                        ZeilenNr = 0;
//helferlein.Infomeldung("laufe durch die rezensionsdetails");
                        do {
                            Multi = Multi + 1;

                            resultB = SQLStatementB.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                            resultB.next();
                            ISBN = resultB.getString("BUCH_ISBN");

                            resultAA = SQLStatementAA.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultB.getString("BUCH_AUTOR") + "'");
                            resultAA.next();

                            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 60, (Startzeile - ZeilenNr * 15), "-");
                            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 75, (Startzeile - ZeilenNr * 15), resultAA.getString("ADRESSEN_VORNAME") + ", " + resultAA.getString("ADRESSEN_NAME") + ": " + resultB.getString("BUCH_TITEL"));
                            ZeilenNr = ZeilenNr + 1;

                            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 75, (Startzeile - ZeilenNr * 15), "ISBN " + ModulHelferlein.makeISBN13(resultB.getString("BUCH_ISBN")) + ", Seiten: " + resultB.getString("BUCH_SEITEN") + ", Preis: " + resultB.getString("BUCH_PREIS") + " Euro.");
                            ZeilenNr = ZeilenNr + 1;

                        } while (resultRD.next());
                    } // if 

                    // Save the results and ensure that the document is properly closed:
                    if (Multi > 1) {
                        outputFileName = ModulHelferlein.pathRezensionen
                                + "\\"
                                + "Rezension-Multi-"
                                + resultAR.getString("ADRESSEN_NAME")
                                + "-"
                                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                                + ".pdf";
                    } else {
                        outputFileName = ModulHelferlein.pathRezensionen
                                + "\\"
                                + "Rezension-" + ISBN + "-"
                                + resultAR.getString("ADRESSEN_NAME")
                                + "-"
                                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                                + ".pdf";
                    }
                    // Make sure that the content stream is closed:
//helferlein.Infomeldung(outputFileName);                    
                    cos.close();
                    document.save(outputFileName);
                    document.close();

                    ModulHelferlein.Infomeldung("Brief an " + resultAR.getString("ADRESSEN_NAME") + " ist als PDF gespeichert!");
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        ModulHelferlein.Fehlermeldung(
                                "Exception: " + exept.getMessage());
                    }// try Brief ausgeben

                    // Adressetikett drucken
                    String Zeile1 = resultAR.getString("ADRESSEN_ZUSATZ_1");
                    String Zeile2 = ModulHelferlein.makeAnrede(resultAR.getString("ADRESSEN_NAMENSZUSATZ"), resultAR.getString("ADRESSEN_VORNAME"), resultAR.getString("ADRESSEN_NAME"));
                    String Zeile3 = resultAR.getString("ADRESSEN_ZUSATZ_2");
                    String Zeile4 = resultAR.getString("ADRESSEN_STRASSE");
                    String Zeile5 = resultAR.getString("ADRESSEN_PLZ") + " " + resultAR.getString("ADRESSEN_ORT");
                    String Zeile6 = resultAR.getString("ADRESSEN_ZUSATZ_3");
                    String[] argumente = {Zeile1, Zeile2, Zeile3, Zeile4, Zeile5, Zeile6};
                    _DlgAdresseDrucken.main(argumente);

                } catch (IOException exept) {
                    ModulHelferlein.Fehlermeldung("IO-Exception: " + exept.getMessage());
                }

            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung(
                        "Exception: " + exept.getMessage());
            }
        } //if conn != null
    }

    ;
    
    /**
     *
     * @param Auswahl
     * @param AnfrageDatum
     * @param Anrede
     * @param Autor
     * @param Titel
     * @param Beschreibung
     * @param ISBN
     * @param Rezensent
     * @param Zeitschrift
     * @param adrAnrede_1
     * @param adrAnrede_2
     * @param adrTitel
     * @param adrVorname
     * @param adrName
     * @param adrStrasse
     * @param adrOrt
     * @param briefAnrede
     * @param Preis
     * @param Seiten
     * @throws Exception
     */
    public static void briefPDF(Integer Auswahl,
            String AnfrageDatum,
            String Anrede,
            String Autor,
            String Titel,
            String Beschreibung,
            String ISBN,
            String Rezensent,
            String Zeitschrift,
            String adrAnrede_1,
            String adrAnrede_2,
            String adrTitel,
            String adrVorname,
            String adrName,
            String adrStrasse,
            String adrOrt,
            String briefAnrede,
            String Preis,
            String Seiten) throws Exception {
        /**
         * helferlein.Infomeldung(Autor); helferlein.Infomeldung(Titel);
         * helferlein.Infomeldung(Beschreibung);
         * helferlein.Infomeldung(Rezensent);
         * helferlein.Infomeldung(Zeitschrift);
         * helferlein.Infomeldung(adrAnrede_1);
         * helferlein.Infomeldung(adrAnrede_2); helferlein.Infomeldung(adrName);
         * helferlein.Infomeldung(adrStrasse); helferlein.Infomeldung(adrOrt);
         * helferlein.Infomeldung(briefAnrede);
         */
        String[] splitBeschreibung = Beschreibung.split(" ");
        Integer woerter = splitBeschreibung.length;

        //helferlein.Infomeldung(Integer.toString(woerter));
        String[] splitAutor = Autor.split(",");
        String zeile = "";

        String outputFileName = ModulHelferlein.pathBuchprojekte + "/" + ISBN + "/Rezensionen/Rezension-"
                + ISBN
                + "-"
                + Rezensent
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
        PDFont fontUni = PDType0Font.load(document, new File("LucidaSansUnicode.ttf"));

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
        AusgabeLB(cos, fontPlain, 8, Color.BLACK, 50, 751, ModulHelferlein.CheckStr("C. Hartmann Miles-Verlag - Alt Kladow 16d - 14089 Berlin"));

        // Datum
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 354, 655, "Datum: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));

        // Adresse
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 730, adrAnrede_1);
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 715, adrTitel + " " + adrVorname + " " + adrName);
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 700, adrAnrede_2);
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 685, adrStrasse);
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 670, "");
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 655, adrOrt);

        // Betreff
        if (Titel.length() > 50) {
            Titel = Titel.substring(0, 49) + "...";
        }

        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 575, "Rezension unserer (Neu)erscheinung des Carola Hartmann Miles-Verlag");
        AusgabeLB(cos, fontItalic, 12, Color.BLACK, 55, 560, splitAutor[1] + ", " + splitAutor[2] + ": " + Titel);
        AusgabeLB(cos, fontItalic, 12, Color.BLACK, 55, 545, " ISBN " + ModulHelferlein.makeISBN13(ISBN) + ", Seiten: " + Seiten + ", Preis: " + Preis + " Euro.");

        // Anrede
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 515, briefAnrede + " " + adrTitel + " " + adrName + ",");

        // Text
        Integer zeilenNr = 1;
        Integer Startzeile = 0;

        switch (Auswahl) {
            case 0: // Anfrage
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "herzlichen Dank für Ihre Anfrage vom " + AnfrageDatum + ".");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 470, "Beiliegend übersende ich Ihnen ein Exemplar unserer Neuerscheinung.");
                Startzeile = 445;
                break;
            case 1: // Wunsch des Autore
                if (Anrede.contains("Herr")) {
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "unser Autor Herr " + splitAutor[2] + " " + splitAutor[1] + " bat mich, Ihnen sein neuestes Werk mit der");
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 470, "Bitte um eine Rezension zuzusenden. Gerne komme ich diesem Wunsch nach.");
                } else {
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "unsere Autorin Frau " + splitAutor[2] + " " + splitAutor[1] + " bat mich, Ihnen ihr neuestes Werk mit der");
                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 470, "Bitte um eine Rezension zuzusenden. Gerne komme ich diesem Wunsch nach.");
                }
                Startzeile = 445;
                break;
            case 2: // Initiative Verlag
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 485, "beiliegend übersende ich Ihnen ein Exemplar unserer (Neu)erscheinung.");
                Startzeile = 460;
                break;
        }
        // Beschreibung
        Integer i = 0;
        while (i < woerter) {
            zeile = "";
            while ((zeile.length() < 60) && (i < woerter)) {
                zeile = zeile + " " + splitBeschreibung[i];
                i = i + 1;
            }
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr - 1), zeile);
            zeilenNr = zeilenNr + 1;
        }
        if (Zeitschrift.equals("")) {
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr + 0), "Ich würde mich freuen, wenn Sie unsere Neuerscheinung besprechen würden.");
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr + 1), "");
        } else {
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr + 0), "Ich würde mich freuen, wenn Sie unsere Neuerscheinung in der Zeitschrift");

            zeilenNr = zeilenNr + 1;

            String ZeileZeitschrift = "'" + Zeitschrift + "' besprechen würden.";
            splitBeschreibung = Beschreibung.split(" ");
            woerter = splitBeschreibung.length;
            i = 0;
            while (i < woerter) {
                zeile = "";
                while ((zeile.length() < 60) && (i < woerter)) {
                    zeile = zeile + " " + splitBeschreibung[i];
                    i = i + 1;
                }
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr), zeile);
                zeilenNr = zeilenNr + 1;
            }

            // AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr + 1), ZeileZeitschrift );
        }
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr + 0), "Für Fragen stehe ich gerne zur Verfügung, ebenso sende ich Ihnen bei Bedarf");
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr + 1), "eine Bild-Datei des Buchcovers zu.");

        // Schlussformel
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr + 5), ModulHelferlein.CheckStr("Mit freundlichen Grüßen"));
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr + 8), "Carola Hartmann");
        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, Startzeile - 15 * (zeilenNr + 9), "Diplom Kauffrau");

        // Make sure that the content stream is closed:
        cos.close();

        // Save the results and ensure that the document is properly closed:
        document.save(outputFileName);
        document.close();

        ModulHelferlein.Infomeldung("Brief an " + adrName + " ist als PDF gespeichert!");
        try {
            Runtime.getRuntime().exec("cmd.exe /c " + outputFileName);
        } catch (IOException exept) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " + exept.getMessage());
        }// try Brief ausgeben

        // Adressetikett drucken
        String Zeile1 = adrAnrede_1;
        String Zeile2 = ModulHelferlein.makeAnrede(adrTitel, adrVorname, adrName);
        String Zeile3 = adrAnrede_2;
        String Zeile4 = adrStrasse;
        String Zeile5 = "";
        String Zeile6 = adrOrt;
        String[] args = {Zeile1, Zeile2, Zeile3, Zeile4, Zeile5, Zeile6};
        _DlgAdresseDrucken.main(args);

    } // void brief PDF

    /**
     *
     * @param Auswahl
     * @param AnfrageDatum
     * @param Anrede
     * @param Autor
     * @param Titel
     * @param Beschreibung
     * @param ISBN
     * @param Rezensent
     * @param Zeitschrift
     * @param adrAnrede_1
     * @param adrAnrede_2
     * @param adrTitel
     * @param adrVorname
     * @param adrName
     * @param adrStrasse
     * @param adrOrt
     * @param briefAnrede
     * @param Preis
     * @param Seiten
     * @throws Exception
     */
    public static void briefDOC(Integer Auswahl,
            String AnfrageDatum,
            String Anrede,
            String Autor,
            String Titel,
            String Beschreibung,
            String ISBN,
            String Rezensent,
            String Zeitschrift,
            String adrAnrede_1,
            String adrAnrede_2,
            String adrTitel,
            String adrVorname,
            String adrName,
            String adrStrasse,
            String adrOrt,
            String briefAnrede,
            String Preis,
            String Seiten) throws Exception {

        String[] splitAutor = Autor.split(",");

        String outputFileName = ModulHelferlein.pathBuchprojekte + "/" + ISBN + "/Rezensionen/Rezension-"
                + ISBN
                + "-"
                + Rezensent
                + "-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                + ".doc";

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

            // Adresse
            paragraph = cell.addParagraph();
            run = paragraph.createRun();
            run.setFontFamily("Arial");
            run.setFontSize(12);

            run.setText(adrAnrede_1);
            run.addBreak();
            run.setText(adrTitel + " " + adrVorname + " " + adrName);
            run.addBreak();
            run.setText(adrAnrede_2);
            run.addBreak();
            run.setText(adrStrasse);
            run.addBreak();
            run.setText("");
            run.setText(adrOrt);

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
            run.setText(" ");
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.setText("Datum: ");
            run.addBreak();
            run.addBreak();
            run.setBold(false);
            run.setText(" ");

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
            run.setText(" ");
            run.addBreak();
            run.setText(ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
            run.addBreak();
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.setText(" ");

            // Betreff
            if (Titel.length() > 50) {
                Titel = Titel.substring(0, 49) + "...";
            }
            Bildschirmausgabe("... erzeuge Textblock Betreffzeile ...");
            XWPFTable tableBetreff = document.createTable(1, 1);
            type = tableBetreff.getCTTbl().getTblPr().addNewTblLayout();
            type.setType(STTblLayoutType.FIXED);
            tableBetreff.getCTTbl().getTblPr().unsetTblBorders();
            tableBetreff.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(9000));

            rows = tableBetreff.getRow(0);
            cell = rows.getCell(0);
            XWPFParagraph paragraphBetreff = cell.getParagraphs().get(0);
            paragraphBetreff.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun runBetreff = paragraphBetreff.createRun();
            runBetreff.setFontFamily("Arial");
            runBetreff.setFontSize(12);
            runBetreff.setBold(true);
            runBetreff.setText("Rezension unserer (Neu)erscheinung des Carola Hartmann Miles-Verlag");
            runBetreff.addBreak();
            runBetreff.setText(splitAutor[1] + ", " + splitAutor[2] + ": " + Titel);
            runBetreff.addBreak();
            runBetreff.setText("ISBN " + ISBN + ", Seiten: " + Seiten + ", Preis: " + Preis + " Euro.");

            // Anrede
            Bildschirmausgabe("... erzeuge Textblock Anrede ...");
            XWPFParagraph paragraphAnrede = document.createParagraph();
            paragraphAnrede.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun runAnrede = paragraphAnrede.createRun();
            runAnrede.addBreak();
            runAnrede.addBreak();
            runAnrede.setText(briefAnrede + " " + adrTitel + " " + adrName + ",");
            runAnrede.addBreak();

            // Text
            switch (Auswahl) {
                case 0: // Anfrage
                    runAnrede.setText("herzlichen Dank für Ihre Anfrage vom "
                            + AnfrageDatum + ".");
                    runAnrede.setText("Beiliegend übersende ich Ihnen ein Exemplar unserer Neuerscheinung.");
                    break;
                case 1: // Wunsch des Autore
                    if (Anrede.contains("Herr")) {
                        runAnrede.setText("unser Autor Herr " + splitAutor[2] + " " + splitAutor[1] + " bat mich, Ihnen sein neuestes Werk mit der Bitte um eine Rezension zuzusenden.");
                        runAnrede.addBreak();
                        runAnrede.setText("Gerne komme ich diesem Wunsch nach.");
                    } else {
                        runAnrede.setText("unsere Autorin Frau " + splitAutor[2] + " " + splitAutor[1] + " bat mich, Ihnen ihr neuestes Werk mit der Bitte um eine Rezension zuzusenden.");
                        runAnrede.addBreak();
                        runAnrede.setText("Gerne komme ich diesem Wunsch nach.");
                    }
                    break;
                case 2: // Initiative Verlag
                    runAnrede.setText("beiliegend übersende ich Ihnen ein Exemplar unserer (Neu)erscheinung.");
                    break;
            }

            runAnrede.addBreak();
            runAnrede.addBreak();

            // Beschreibung
            runAnrede.setText(Beschreibung);
            runAnrede.addBreak();
            runAnrede.addBreak();
            if (Zeitschrift.equals("")) {
                runAnrede.setText("Ich würde mich freuen, wenn Sie unsere Neuerscheinung besprechen würden.");
            } else {
                runAnrede.setText("Ich würde mich freuen, wenn Sie unsere (Neu)erscheinung in der Zeitschrift");
                runAnrede.addBreak();
                runAnrede.setText("'" + Zeitschrift + "' besprechen würden.");
            }
            runAnrede.addBreak();
            runAnrede.setText("Für Fragen stehe ich gerne zur Verfügung, ebenso sende ich Ihnen bei Bedarf eine Bild-Datei des Buchcovers zu.");
            runAnrede.addBreak();
            runAnrede.addBreak();

            // Schlussformel
            Bildschirmausgabe("... erzeuge Schlussformel ...");
            runAnrede.setText("Mit freundlichen Grüßen");
            runAnrede.addBreak();
            runAnrede.addBreak();
            runAnrede.addBreak();
            runAnrede.setText("Carola Hartmann");
            runAnrede.addBreak();
            runAnrede.setText("Diplom Kauffrau");

            // Schlussformel
            // create footer start
            Bildschirmausgabe("... erzeuge Fußzeile ...");
            XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

            XWPFParagraph paragraphFooter = footer.createParagraph();
            paragraphFooter.setAlignment(ParagraphAlignment.CENTER);
            XmlCursor cursor = paragraphFooter.getCTP().newCursor();
            XWPFTable tableFooter = footer.insertNewTbl(cursor);

            String[] strFooter1 = {"Carola Hartmann Miles Verlag", "Alt Kladow 16d", "14089 Berlin"};
            String[] strFooter2 = {"Dipl.Kff. Carola Hartmann", "Telefon +49 (0)30 36 28 86 77", "Volksbank Berlin"};
            String[] strFooter3 = {"StrNr 19 332 6006 5", "E-Mail miles-verlag@t-online.de", "IBAN DE61 1009 0000 2233 8320 17"};
            String[] strFooter4 = {"UStrID DE 269 369 5", "www.miles-verlag.jimdo.com", "BIC BEVODEBB"};

            XWPFTableRow tableFooterrow1 = tableFooter.createRow();
            for (int i = 0; i < 3; i++) {
                cell = tableFooterrow1.createCell();
                CTTblWidth tblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
                tblWidth.setW(BigInteger.valueOf(3666));
                tblWidth.setType(STTblWidth.DXA);
                paragraphFooter = cell.getParagraphs().get(0);
                paragraphFooter.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun runFooter = paragraphFooter.createRun();
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

            } // for
            // Make sure that the content stream is closed:
            Bildschirmausgabe("... Fertig! Dokument ist erzeugt");
            Bildschirmausgabe("... Dokument wird gespeichert");
            try ( // speichern
                    FileOutputStream out = new FileOutputStream(new File(outputFileName))) {
                document.write(out);
            }

            ModulHelferlein.Infomeldung(outputFileName + " ist als DOC gespeichert!");
            Dialog.setVisible(false);
            try {
                Runtime.getRuntime().exec("cmd.exe /c " + outputFileName);
            } catch (IOException exept) {
                ModulHelferlein.Fehlermeldung(
                        "Exception: " + exept.getMessage());
            }// try Brief ausgeben

        }
    } // void brief DOC

} // class
