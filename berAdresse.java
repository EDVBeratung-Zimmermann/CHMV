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
import java.awt.Color;
import java.awt.Dimension;
import jxl.Workbook;

import jxl.write.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import static jxl.format.Alignment.LEFT;
import static jxl.format.Alignment.RIGHT;

//~--- JDK imports ------------------------------------------------------------
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import static milesVerlagMain.ModulHelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;

/**
 * Klasse zur Erzeugung einer Übersicht der Adressen
 *
 * @author Thomas Zimmermann
 *
 */
public class berAdresse {

    @SuppressWarnings("FieldMayBeFinal")
    private static JFrame Dialog = new JFrame("Carola Hartmann Miles Verlag");
    private static JLabel dlgLabel = new JLabel("");
    private static JPanel dlgPanel;
    private static JScrollPane dlgScrollpane;
    private static JTextArea dlgTextarea;

    private static void Bildschirmausgabe(String zeile) {
        dlgTextarea.append(zeile + "\r\n");
        dlgTextarea.update(dlgTextarea.getGraphics());
    }

    /**
     * Erzeugt die Übersicht der Adressen als XLSX-Datei
     *
     * @param Parameter
     */
    public void berichtXLSX(String Parameter) {
        String outputFileName;

        outputFileName = ModulHelferlein.pathBerichte + "\\Adressen\\"
                + "Adressen-"
                + Parameter
                + "-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                + ".xls";

        try {    // Erstelle Workbook
            WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName));
            WritableSheet sheet_Adressen = workbook.createSheet("Adressen", 0);

            try {

                // Create a cell format for Arial 10 point font
                WritableFont arial10fontBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
                WritableCellFormat arial10formatBold = new WritableCellFormat(arial10fontBold);
                WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 10);
                WritableCellFormat arial10formatR = new WritableCellFormat(arial10font);

                arial10formatR.setAlignment(RIGHT);

                WritableCellFormat arial10formatL = new WritableCellFormat(arial10font);

                arial10formatL.setAlignment(LEFT);

                // Aufbau des Tabellenblattes sheet_Adressen
                Label label = new Label(0, 0, "Übersicht der Adressen", arial10formatBold);

                label = new Label(0, 0, "ADRESSEN_ID", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(1, 0, "ADRESSEN_TYP", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(2, 0, "ADRESSEN_NAME", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(3, 0, "ADRESSEN_VORNAME", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(4, 0, "ADRESSEN_ANREDE", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(5, 0, "ADRESSEN_STRASSE", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(6, 0, "ADRESSEN_PLZ", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(7, 0, "ADRESSEN_ORT", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(8, 0, "ADRESSEN_TELEFON", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(9, 0, "ADRESSEN_MOBIL", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(10, 0, "ADRESSEN_TELEFAX", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(11, 0, "ADRESSEN_EMAIL", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(12, 0, "ADRESSEN_WEB", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(13, 0, "ADRESSEN_POC", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(14, 0, "ADRESSEN_KUNDENNUMMER", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(15, 0, "ADRESSEN_ANMELDUNG", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(16, 0, "ADRESSEN_KENNWORT", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(17, 0, "ADRESSEN_ZUSATZ_1", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(18, 0, "ADRESSEN_ZUSATZ_2", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(19, 0, "ADRESSEN_ZEITSCHRIFT", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(20, 0, "ADRESSEN_NAMENSZUSATZ", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(21, 0, "ADRESSEN_USTR_ID", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(22, 0, "ADRESSEN_IBAN", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(23, 0, "ADRESSEN_BIC", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(24, 0, "ADRESSEN_SONDERKONDITION", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(25, 0, "ADRESSEN_RABATT", arial10formatBold);
                sheet_Adressen.addCell(label);
                label = new Label(26, 0, "ADRESSEN_USTR", arial10formatBold);
                sheet_Adressen.addCell(label);

                Connection conn = null;

                try {    // Datenbank-Treiber laden
                    Class.forName(ModulHelferlein.dbDriver);
                } catch (ClassNotFoundException exept) {
                    ModulHelferlein.Fehlermeldung(
                            "Bericht Adressen: Datenbankanbindung: ClassNotFoundException: Treiber nicht gefunden: "
                            + exept.getMessage());
                }        // try Datenbank-Treiber laden

                try {    // Verbindung zur Datenbank über die JDBC-Brücke
                    conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung(
                            "Bericht Adressen: Datenbankanbindung: SQL-Exception: Verbindung nicht moeglich: "
                            + exept.getMessage());
                }    // try Verbindung zur Datenbank über die JDBC-Brücke

                final Connection conn2 = conn;

                if (conn2 != null) {                             // Datenbankverbindung steht
                    Statement SQLAnfrage = null;                 // Anfrage erzeugen

                    try {                                        // Anfrage
                        SQLAnfrage = conn2.createStatement();    // Anfrage der DB conn2 zuordnen

                        String Abfrage = "SELECT * FROM TBL_ADRESSE";

                        if ("ALLE".equals(Parameter)) {
                            Abfrage = "SELECT * FROM TBL_ADRESSE";
                        } else {
                            Abfrage = "SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_TYP = '" + Parameter + "'";
                        }

                        // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert
                        ResultSet result = SQLAnfrage.executeQuery(Abfrage);
                        int rownum = 1;

                        while (result.next()) {    // geht durch alle zeilen
                            label = new Label(0, rownum, result.getString("ADRESSEN_ID"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(1, rownum, result.getString("ADRESSEN_TYP"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(2, rownum, result.getString("ADRESSEN_NAME"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(3, rownum, result.getString("ADRESSEN_VORNAME"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(4, rownum, result.getString("ADRESSEN_ANREDE"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(5, rownum, result.getString("ADRESSEN_STRASSE"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(6, rownum, result.getString("ADRESSEN_PLZ"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(7, rownum, result.getString("ADRESSEN_ORT"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(8, rownum, result.getString("ADRESSEN_TELEFON"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(9, rownum, result.getString("ADRESSEN_MOBIL"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(10, rownum, result.getString("ADRESSEN_TELEFAX"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(11, rownum, result.getString("ADRESSEN_EMAIL"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(12, rownum, result.getString("ADRESSEN_WEB"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(13, rownum, result.getString("ADRESSEN_POC"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(14, rownum, result.getString("ADRESSEN_KUNDENNUMMER"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(15, rownum, result.getString("ADRESSEN_ANMELDUNG"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(16, rownum, result.getString("ADRESSEN_KENNWORT"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(17, rownum, result.getString("ADRESSEN_ZUSATZ_1"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(18, rownum, result.getString("ADRESSEN_ZUSATZ_2"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(19, rownum, result.getString("ADRESSEN_ZEITSCHRIFT"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(20, rownum, result.getString("ADRESSEN_NAMENSZUSATZ"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(21, rownum, result.getString("ADRESSEN_USTR_ID"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(22, rownum, result.getString("ADRESSEN_IBAN"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(23, rownum, result.getString("ADRESSEN_BIC"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(24, rownum, result.getString("ADRESSEN_SONDERKONDITION"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(25, rownum, result.getString("ADRESSEN_RABATT"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            label = new Label(26, rownum, result.getString("ADRESSEN_USTR"), arial10formatL);
                            sheet_Adressen.addCell(label);
                            rownum = rownum + 1;
                        }                          // geht durch alle zeilen

                        // Fertig - alles schließen
                        try {                      // workbook write
                            workbook.write();
                        } catch (IOException e) {
                            ModulHelferlein.Fehlermeldung("XLS-Bericht Einnahmen: IO-Exception: " + e.getMessage());
                        }                          // workbook write

                        try {                      // try workbook close
                            workbook.close();
                        } catch (IOException e) {
                            ModulHelferlein.Fehlermeldung("XLS-Bericht: IO-Exception: " + e.getMessage());
                        }                          // try workbook close

                        try {                      // try XLS anzeigen
                            Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                        } catch (IOException exept) {
                            ModulHelferlein.Fehlermeldung("Bericht Einnahmen: Anzeige XLS-Export: Exception: "
                                    + exept.getMessage());
                        }    // try XLS anzeigen
                    } catch (SQLException exept) {
                        ModulHelferlein.Fehlermeldung(
                                "Bericht Adressen: Datenbankanbindung: SQL-Exception: SQL-Anfrage nicht moeglich: "
                                + exept.getMessage());
                    } catch (WriteException e) {
                        ModulHelferlein.Fehlermeldung("Bericht Adressen: Datenbankanbindung: Exception: " + e.getMessage());
                    }    // try Anfrage
                } else {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Adressen: Datenbankanbindung besteht nicht");
                }        // keine Datenbankverbindung
            } catch (WriteException e) {
                ModulHelferlein.Fehlermeldung("XLS-Bericht Adressen: Write-Exception: " + e.getMessage());
            }            // try Tabellenblätter schreiben
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("XLS-Bericht Adressen: IO-Exception: " + e.getMessage());
        }                // try Workbook create

        ModulHelferlein.Infomeldung("Adressübersicht ist als XLS gespeichert!");
    }    // public void berichtXLSX

    /**
     * Erzeugt die Übersicht der Adressen als DOCX-Datei
     *
     * @param Parameter
     */
    public void berichtDOCX(String Parameter) {
        String outputFileName;
        outputFileName = ModulHelferlein.pathBerichte + "\\Adressen\\"
                + "Adressen-"
                + Parameter
                + "-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                + ".docx";
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
        dlgTextarea.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 12));
        dlgTextarea.setForeground(Color.WHITE);
        dlgTextarea.setBackground(Color.BLACK);

        dlgScrollpane = new JScrollPane(dlgTextarea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
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

            CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(1152));
            pageMar.setTop(BigInteger.valueOf(1152));
            pageMar.setRight(BigInteger.valueOf(1152));
            pageMar.setBottom(BigInteger.valueOf(1152));

            Connection conn = null;

            try {    // Datenbank-Treiber laden
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung(
                        "Bericht Adressen: Datenbankanbindung: ClassNotFoundException: Treiber nicht gefunden: "
                        + exept.getMessage());
            }        // try Datenbank-Treiber laden

            try {    // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung(
                        "Bericht Adressen: Datenbankanbindung: SQL-Exception: Verbindung nicht moeglich: "
                        + exept.getMessage());
            }    // try Verbindung zur Datenbank über die JDBC-Brücke

            final Connection conn2 = conn;

            if (conn2 != null) {                             // Datenbankverbindung steht
                Statement SQLAnfrage = null;                 // Anfrage erzeugen

                try {                                        // Anfrage
                    SQLAnfrage = conn2.createStatement();    // Anfrage der DB conn2 zuordnen

                    String Abfrage;
                    if ("ALLE".equals(Parameter)) {
                        Abfrage = "SELECT * FROM TBL_ADRESSE ORDER BY ADRESSEN_NAME";
                    } else {
                        Abfrage = "SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_TYP = '" + Parameter + "'  ORDER BY ADRESSEN_NAME";
                    }

                    // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert
                    ResultSet result = SQLAnfrage.executeQuery(Abfrage);

                    String Bereich = "A";
                    int ascii = 65;
                    char ch = Bereich.charAt(0);
                    
                    XWPFParagraph paragraphAnrede = document.createParagraph();
                    paragraphAnrede.setAlignment(ParagraphAlignment.LEFT);
                    XWPFRun runAdresse = paragraphAnrede.createRun();
                    runAdresse.setText("Carola Hartmann Miles Verlag - Verlagsverwaltung - Adressen");
                    runAdresse.addBreak();
                    runAdresse.setText("Übersicht der Adressen, Stand: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    runAdresse.addBreak();
                    paragraphAnrede = document.createParagraph();
                    paragraphAnrede.setAlignment(ParagraphAlignment.LEFT);
                    runAdresse = paragraphAnrede.createRun();
                    runAdresse.setText("---" + Bereich + "-------------------");
                    runAdresse.addBreak();
                    while (result.next()) {    // geht durch alle zeilen
                        Bildschirmausgabe("... schreibe Adresse " + result.getString("ADRESSEN_NAME") + " ...");
                        if (result.getString("ADRESSEN_NAME").substring(0, 1).equals(Bereich)) {
                            // do nothing
                        } else {
                            while ((!result.getString("ADRESSEN_NAME").substring(0, 1).toUpperCase().equals(Bereich)) && (ascii < 90)) {
                                ascii = ascii + 1;
                                ch = (char) ascii;
                                Bereich = String.valueOf(ch);
                                ascii = (int) Bereich.charAt(0);
                                paragraphAnrede = document.createParagraph();
                                paragraphAnrede.setAlignment(ParagraphAlignment.LEFT);
                                runAdresse = paragraphAnrede.createRun();
                                runAdresse.addBreak();
                                runAdresse.addBreak();
                                runAdresse.setText("--- " + Bereich + " -------------------");
                                runAdresse.addBreak();
                            }
                        }
                        paragraphAnrede = document.createParagraph();
                        paragraphAnrede.setAlignment(ParagraphAlignment.LEFT);
                        runAdresse = paragraphAnrede.createRun();
                        runAdresse.setText(result.getString("ADRESSEN_ANREDE"));
                        runAdresse.addBreak();
                        runAdresse.setText(result.getString("ADRESSEN_NAMENSZUSATZ") + " "
                                + result.getString("ADRESSEN_VORNAME") + " "
                                + result.getString("ADRESSEN_NAME"));
                        runAdresse.addBreak();
                        runAdresse.setText(result.getString("ADRESSEN_ZUSATZ_2"));
                        runAdresse.addBreak();
                        runAdresse.setText(result.getString("ADRESSEN_STRASSE"));
                        runAdresse.addBreak();
                        runAdresse.setText(result.getString("ADRESSEN_PLZ") + " " + result.getString("ADRESSEN_ORT"));
                        runAdresse.addBreak();
                        runAdresse.addBreak();
                        runAdresse.setText("Telefon:"); runAdresse.addTab(); runAdresse.setText(result.getString("ADRESSEN_TELEFON"));
                        runAdresse.addBreak();
                        runAdresse.setText("Telefax:"); runAdresse.addTab(); runAdresse.addTab(); runAdresse.setText(result.getString("ADRESSEN_TELEFAX"));
                        runAdresse.addBreak();
                        runAdresse.setText("Mobilfunk:"); runAdresse.addTab(); runAdresse.setText(result.getString("ADRESSEN_MOBIL"));
                        runAdresse.addBreak();
                        runAdresse.setText("E-Mail:"); runAdresse.addTab(); runAdresse.addTab(); runAdresse.setText(result.getString("ADRESSEN_EMAIL"));
                        runAdresse.addBreak();
                        runAdresse.setText("Webseite:"); runAdresse.addTab(); runAdresse.setText(result.getString("ADRESSEN_WEB"));
                        runAdresse.addBreak();
                        runAdresse.setText("UStrID:"); runAdresse.addTab(); runAdresse.addTab(); runAdresse.setText(result.getString("ADRESSEN_USTR_ID"));
                        runAdresse.addBreak();
                        runAdresse.setText("IBAN:"); runAdresse.addTab(); runAdresse.addTab(); runAdresse.setText(result.getString("ADRESSEN_IBAN"));
                        runAdresse.addBreak();
                        runAdresse.setText("BIC:"); runAdresse.addTab(); runAdresse.addTab(); runAdresse.setText(result.getString("ADRESSEN_BIC"));
                        runAdresse.addBreak();

                        /**
                         * result.getString("ADRESSEN_ID")
                         * result.getString("ADRESSEN_TYP")
                         * result.getString("ADRESSEN_POC")
                         * result.getString("ADRESSEN_KUNDENNUMMER")
                         * result.getString("ADRESSEN_ANMELDUNG")
                         * result.getString("ADRESSEN_KENNWORT")
                         * result.getString("ADRESSEN_ZUSATZ_1")
                         * result.getString("ADRESSEN_ZEITSCHRIFT")
                         * result.getString("ADRESSEN_SONDERKONDITION")
                         * result.getString("ADRESSEN_RABATT")
                         * result.getString("ADRESSEN_USTR")
                         */
                    } // geht durch alle zeilen
                    while (ascii < 90) {
                        ascii = ascii + 1;
                        ch = (char) ascii;
                        Bereich = String.valueOf(ch);
                        ascii = (int) Bereich.charAt(0);
                        paragraphAnrede = document.createParagraph();
                        paragraphAnrede.setAlignment(ParagraphAlignment.LEFT);
                        runAdresse = paragraphAnrede.createRun();
                        runAdresse.addBreak();
                        runAdresse.addBreak();
                        runAdresse.setText("--- " + Bereich + " -------------------");
                        runAdresse.addBreak();
                    }

                    // Make sure that the content stream is closed:
                    Bildschirmausgabe("... Fertig! Dokument ist erzeugt");
                    Bildschirmausgabe("... Dokument wird gespeichert");
                    try ( // speichern
                            FileOutputStream out = new FileOutputStream(new File(outputFileName))) {
                        document.write(out);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(berAdresse.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(berAdresse.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    ModulHelferlein.Infomeldung(outputFileName + " ist als DOC gespeichert!");
                    Dialog.setVisible(false);
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + outputFileName);
                    } catch (IOException exept) {
                        ModulHelferlein.Fehlermeldung(
                                "Exception: " + exept.getMessage());
                    }// try Brief ausgeben

                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung(
                            "Bericht Adressen: Datenbankanbindung: SQL-Exception: SQL-Anfrage nicht moeglich: "
                            + exept.getMessage());
                }
                // try Anfrage
                // try Anfrage
            } else {
                ModulHelferlein.Fehlermeldung("XLS-Bericht Adressen: Datenbankanbindung besteht nicht");
            }        // keine Datenbankverbindung
        } catch (IOException ex) {
            Logger.getLogger(berAdresse.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // void berichtDOCX

    /**
     * Erzeugt die Übersicht der Adressen als PDF-Datei
     *
     * @param Parameter
     */
    public void berichtPDF(String Parameter) {
        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(PDRectangle.A4);

        document.addPage(page1);

        // Start a new content stream which will "hold" the to be created content
        PDPageContentStream cos;

        try {
            cos = new PDPageContentStream(document, page1);

            String outputFileName;

            outputFileName = ModulHelferlein.pathBerichte + "\\Adressen\\"
                    + "Adressen-"
                    + Parameter
                    + "-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                    + ".pdf";

            PDDocumentInformation docInfo = document.getDocumentInformation();

            docInfo.setSubject("Adressen");
            docInfo.setTitle("miles-Verlag Stammdaten");
            docInfo.setAuthor("miles-Verlag");
            docInfo.setCreationDate(Calendar.getInstance());
            docInfo.setCreator("miles-Verlag");
            docInfo.setProducer("miles-Verlag");

            // Einfügen der Texte und Bilder
            Connection conn = null;

            // Datenbank-Treiber laden
            try {
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("ClassNotFoundException: Treiber nicht gefunden: " + exept.getMessage());
            }

            // Verbindung zur Datenbank über die JDBC-Brücke
            try {
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
            }

            final Connection conn2 = conn;

            if (conn2 != null) {
                Statement SQLAnfrage = null;                 // Anfrage erzeugen

                try {
                    SQLAnfrage = conn2.createStatement();    // Anfrage der DB conn2 zuordnen

                    String Abfrage = "SELECT * FROM TBL_ADRESSE";

                    if ("ALLE".equals(Parameter)) {
                        Abfrage = "SELECT * FROM TBL_ADRESSE ORDER BY ADRESSEN_NAME";
                    } else {
                        Abfrage = "SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_TYP = '" + Parameter
                                + "' ORDER BY ADRESSEN_NAME";
                    }

                    // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert
                    ResultSet result = SQLAnfrage.executeQuery(Abfrage);
                    PDFont fontPlain = PDType1Font.HELVETICA;
                    PDFont fontBold = PDType1Font.HELVETICA_BOLD;
                    Integer zeile = 1;
                    Integer seite = 1;

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Adressen");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755,
                            "Übersicht der Adressen, Stand: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 720, "ID");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 720, "Adresstyp");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 200, 720, "Zeitschrift");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 720, "Ansprechpartner");

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 705, "Zusatz 1");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 705, "Anrede");

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 690, "Namenszusatz");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 690, "Vorname");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 690, "Firmenname/Name");

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 675, "Zusatz 2");

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 660, "Straße und Hausnummer");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 660, "PLZ");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 430, 660, "Ort");

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 645, "Telefon");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 645, "Telefax");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 645, "Mobilfunk");

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 630, "e-Mail");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 630, "Internet");

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 615, "UStr-ID");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 615, "IBAN");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 430, 615, "BIC");

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 600, "Kundennummer");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 600, "Benutzerkennung");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 430, 600, "Kennwort");

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 585, "Sonderkondition");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 585, "Rabatt");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 585, "Ustr on Top");

                    Linie(cos,2,56, 580, 539, 580);
                    
                    while (result.next()) {    // geht durch alle zeilen
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 715 - zeile * 175, result.getString("ADRESSEN_ID"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 715 - zeile * 175, result.getString("ADRESSEN_TYP"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 200, 715 - zeile * 175, result.getString("ADRESSEN_ZEITSCHRIFT"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 715 - zeile * 175, result.getString("ADRESSEN_POC"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 700 - zeile * 175, result.getString("ADRESSEN_ZUSATZ_1"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 200, 700 - zeile * 175, result.getString("ADRESSEN_ANREDE"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 685 - zeile * 175, result.getString("ADRESSEN_NAMENSZUSATZ"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 685 - zeile * 175, result.getString("ADRESSEN_VORNAME"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 685 - zeile * 175, result.getString("ADRESSEN_NAME"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 660 - zeile * 175, result.getString("ADRESSEN_ZUSATZ_2"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 645 - zeile * 175, result.getString("ADRESSEN_STRASSE"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 645 - zeile * 175, result.getString("ADRESSEN_PLZ"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 430, 645 - zeile * 175, result.getString("ADRESSEN_ORT"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 630 - zeile * 175, result.getString("ADRESSEN_TELEFON"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 630 - zeile * 175, result.getString("ADRESSEN_TELEFAX"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 630 - zeile * 175, result.getString("ADRESSEN_MOBIL"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 615 - zeile * 175, result.getString("ADRESSEN_EMAIL"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 615 - zeile * 175, result.getString("ADRESSEN_WEB"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 600 - zeile * 175, result.getString("ADRESSEN_USTR_ID"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 600 - zeile * 175, result.getString("ADRESSEN_IBAN"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 430, 600 - zeile * 175, result.getString("ADRESSEN_BIC"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 585 - zeile * 175, result.getString("ADRESSEN_Kundennummer"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 585 - zeile * 175, result.getString("ADRESSEN_ANMELDUNG"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 430, 585 - zeile * 175, result.getString("ADRESSEN_KENNWORT"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 560 - zeile * 175, result.getString("ADRESSEN_Sonderkondition"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 560 - zeile * 175, result.getString("ADRESSEN_Rabatt"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 560 - zeile * 175, result.getString("ADRESSEN_Ustr"));

                        Linie(cos,1,56, 555 - zeile * 175, 539, 555 - zeile * 175);
                        zeile = zeile + 1;

                        if (zeile == 4) {
                            zeile = 1;
                            seite = seite + 1;
                            cos.close();

                            PDPage page = new PDPage(PDRectangle.A4);

                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Adressen");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755,
                                    "Übersicht der Adressen, Stand: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 720, "ID");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 720, "Adresstyp");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 200, 720, "Zeitschrift");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 720, "Ansprechpartner");

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 705, "Zusatz 1");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 705, "Anrede");

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 690, "Namenszusatz");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 690, "Vorname");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 690, "Firmenname/Name");

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 675, "Zusatz 2");

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 660, "Straße und Hausnummer");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 660, "PLZ");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 430, 660, "Ort");

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 645, "Telefon");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 645, "Telefax");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 645, "Mobilfunk");

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 630, "e-Mail");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 630, "Internet");

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 615, "UStr-ID");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 615, "IBAN");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 430, 615, "BIC");

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 600, "Kundennummer");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 600, "Benutzerkennung");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 430, 600, "Kennwort");

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 80, 585, "Sonderkondition");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 585, "Rabatt");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 390, 585, "Ustr on Top");

                            Linie(cos,2,56, 580, 539, 580);
                        }    // if
                    }        // while

                    // close the content stream for page 1
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();
                    ModulHelferlein.Infomeldung("Liste der Adressen ist als PDF gespeichert!");

                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        ModulHelferlein.Fehlermeldung("PDF-Bericht Adressen: Exception: " + exept.getMessage());
                    }
                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung("PDF-Bericht Adressen: SQL-Exception: SQL-Anfrage nicht moeglich: "
                            + exept.getMessage());
                } catch (IOException e) {
                    ModulHelferlein.Fehlermeldung("PDF-Bericht Adressen: Exception: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            ModulHelferlein.Fehlermeldung("PDF-Bericht Adressen: FileNotFoundException: " + e.getMessage());
        } catch (IOException e1) {
            ModulHelferlein.Fehlermeldung("PDF-Bericht Adressen: IO-Exception: " + e1.getMessage());
        }
    }    // void
}    // class


//~ Formatted by Jindent --- http://www.jindent.com
