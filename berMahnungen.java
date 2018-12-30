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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.awt.Color;

import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.AusgabeDB;

import jxl.Workbook;
import static jxl.format.Alignment.LEFT;
import static jxl.format.Alignment.RIGHT;
import jxl.write.*;
import static milesVerlagMain.Modulhelferlein.Linie;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Klasse zur Erzeugung einer Liste von offenen Einnahmen
 *
 * @author Thomas Zimmermann
 *
 */
public class berMahnungen {

    /**
     * Erzeugt eine Übersicht der Einnahmen im Zeitraum strVon bis strBis im
     * Format MS EXCEL
     *
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berichtXLS(String strVon, String strBis) {
        String outputFileName;

        Double Gesamtsumme = 0D;
        Double Gesamtzeile = 0D;
        Double Buchpreis = 0D;

        int Anzahl = 0;
        int Rabatt = 0;

        String KundenName = "";

        ResultSet resultKunde = null;
        ResultSet resultBuch = null;
        ResultSet resultBestellungDetails = null;
        ResultSet resultBestellung = null;

        outputFileName = Modulhelferlein.pathBerichte + "\\Mahnungen\\"
                + "Liste-Mahnungen-" 
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd") 
                + ".xls";

        try { // Erstelle Workbook
            WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName));
            WritableSheet sheet_Mahnungen = workbook.createSheet("Offene Einnahmen", 0);

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
                Label label = new Label(0, 0, "Übersicht der offenen Rechnungen/Einnahmen im Zeitraum " + strVon + " - " + strBis, arial10formatBold);
                label = new Label(0, 1, "Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"), arial10formatBold);

                label = new Label(0, 3, "RechNr", arial10formatBold);
                sheet_Mahnungen.addCell(label);
                label = new Label(1, 3, "Kunde", arial10formatBold);
                sheet_Mahnungen.addCell(label);
                label = new Label(2, 3, "Betrag", arial10formatBold);
                sheet_Mahnungen.addCell(label);

                Connection conn = null;

                try { // Datenbank-Treiber laden
                    Class.forName(Modulhelferlein.dbDriver);
                } catch (ClassNotFoundException exept) {
                    Modulhelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Datenbankanbindung: ClassNotFoundException: Treiber nicht gefunden: " + exept.getMessage());
                } // try Datenbank-Treiber laden

                try { // Verbindung zur Datenbank über die JDBC-Brücke
                    conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Datenbankanbindung: SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
                } // try Verbindung zur Datenbank über die JDBC-Brücke

                final Connection conn2 = conn;

                if (conn2 != null) { // Datenbankverbindung steht
                    Statement SQLAnfrage = null; // Anfrage erzeugen

                    try {//Anfrage
                        SQLAnfrage = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                        String Abfrage = "SELECT * FROM TBL_ADRESSE";
                        ResultSet result = SQLAnfrage.executeQuery(Abfrage); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                        int rownum = 4;

                        while (result.next()) { // geht durch alle zeilen
                            label = new Label(0, rownum, result.getString("ADRESSEN_ID"), arial10formatL);
                            sheet_Mahnungen.addCell(label);
                            label = new Label(1, rownum, result.getString("ADRESSEN_TYP"), arial10formatL);
                            sheet_Mahnungen.addCell(label);
                            label = new Label(2, rownum, result.getString("ADRESSEN_NAME"), arial10formatL);
                            sheet_Mahnungen.addCell(label);

                            rownum = rownum + 1;
                        }	// geht durch alle zeilen

                        // Fertig - alles schließen
                        try {// workbook write
                            workbook.write();
                        } catch (IOException e) {
                            Modulhelferlein.Fehlermeldung("XLS-Bericht Mahnungen: IO-Exception: " + e.getMessage());
                        } // workbook write

                        try { // try workbook close
                            workbook.close();
                        } catch (IOException e) {
                            Modulhelferlein.Fehlermeldung("XLS-Bericht Mahnungen: IO-Exception: " + e.getMessage());
                        } // try workbook close

                        try { // try XLS anzeigen
                            Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                        } catch (IOException exept) {
                            Modulhelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Anzeige XLS-Export: Exception: " + exept.getMessage());
                        } // try XLS anzeigen

                    } catch (SQLException exept) {
                        Modulhelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Datenbankanbindung: SQL-Exception: SQL-Anfrage nicht moeglich: " + exept.getMessage());
                    } catch (WriteException e) {
                        Modulhelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Datenbankanbindung: Exception: " + e.getMessage());
                    } //try Anfrage

                } else {
                    Modulhelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Datenbankanbindung besteht nicht");
                } // keine Datenbankverbindung

            } catch (WriteException e) {
                Modulhelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Write-Exception: " + e.getMessage());
            } // try Tabellenblätter schreiben

        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("XLS-Bericht Mahnungen: IO-Exception: " + e.getMessage());
        } // try Workbook create

        Modulhelferlein.Infomeldung("Bericht Mahnungen ist als XLS gespeichert!");

    }

    /**
     * Erzeugt eine Übersicht der Einnahmen im Zeitraum strVon bis strBis im
     * Format MS Word
     *
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berichtDOC(String strVon, String strBis) {
        Modulhelferlein.Infomeldung("Diese Funktion ist noch nicht implementiert!");
    }

    /**
     * Erzeugt eine Übersicht der offenen Rechnungen/Einnahmen/Mahnungen im
     * Zeitraum strVon bis strBis im Format PDF
     *
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berichtPDF(String strVon, String strBis) {
        Double Gesamtsumme = 0D;
        Double Gesamtzeile = 0D;
        Double Buchpreis = 0D;

        int Anzahl = 0;
        int Rabatt = 0;

        ResultSet resultKunde = null;
        ResultSet resultBuch = null;
        ResultSet resultBestellungDetails = null;
        ResultSet resultBestellung = null;

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(A4);
        document.addPage(page);

        PDPageContentStream cos;

        String outputFileName;
        outputFileName = Modulhelferlein.pathBerichte + "\\Mahnungen\\"
                + "Liste-Mahnungen-" 
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd") 
                + ".pdf";

        PDDocumentInformation docInfo = document.getDocumentInformation();

        docInfo.setSubject("Mahnungen");
        docInfo.setTitle("miles-Verlag");
        docInfo.setAuthor("miles-Verlag");
        docInfo.setCreationDate(Calendar.getInstance());
        docInfo.setCreator("miles-Verlag");
        docInfo.setProducer("miles-Verlag");

        PDFont fontPlain = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;

        try { // Start a new content stream which will "hold" the to be created content
            cos = new PDPageContentStream(document, page);

            Integer zeile = 1;
            Integer seite = 1;

            String KundenName = "";

            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "Übersicht der offenen Rechnungen/Einnahmen im Zeitraum " + strVon + " - " + strBis);
            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 755, "Seite " + Integer.toString(seite));
            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 735, "RechNr");
            Ausgabe(cos, fontBold, 12, Color.BLACK, 200, 735, "Kunde");
            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 735, "Betrag");

            Linie(cos,1,56, 730, 539, 730);
            
            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Bericht Mahnungen: ClassNotFoundException: Treiber nicht gefunden: " + exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Bericht Mahnungen: SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
            } // try Verbindung zur Datenbank über die JDBC-Brücke

            if (conn != null) {

                Statement SQLBuch = null;
                Statement SQLKunde = null;
                Statement SQLBestellung = null;
                Statement SQLBestellungDetails = null;

                try {
                    SQLBuch = conn.createStatement();
                    SQLKunde = conn.createStatement();
                    SQLBestellung = conn.createStatement();
                    SQLBestellungDetails = conn.createStatement();

                    String Sql = "SELECT  *  FROM  tbl_bestellung "
                            + " WHERE "
                            + "(BESTELLUNG_DATUM BETWEEN '" + strVon
                            + "' AND '" + strBis + "')"
                            + " AND "
                            + " BESTELLUNG_BEZAHLT  =  '1970-01-01' "
                            + " ORDER  BY  BESTELLUNG_DATUM";
//helferlein.Infomeldung("Sql " + Sql);
                    resultBestellung = SQLBestellung.executeQuery(Sql);

                    Gesamtsumme = 0D;

                    while (resultBestellung.next()) { // geht durch alle zeilen
//helferlein.Infomeldung("Zeile Nr " + zeile.toString() + " => " + resultBestellung.getString("BESTELLUNG_RECHNR"));
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;

                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "Übersicht der offenen Rechnungen/Einnahmen im Zeitraum " + strVon + " - " + strBis);
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 755, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 735, "RechNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 200, 735, "Kunde");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 735, "Betrag");

                            Linie(cos,1,56, 730, 539, 730);
                        } // if

                        // Kundendaten holen
                        if (resultBestellung.getInt("BESTELLUNG_KUNDE") > 0) {
                            resultKunde = SQLKunde.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultBestellung.getString("BESTELLUNG_KUNDE") + "'");
                            resultKunde.next();
                            KundenName = resultKunde.getString("ADRESSEN_NAME");
                        } else {
                            KundenName = resultBestellung.getString("BESTELLUNG_ZEILE_1");
                        }

                        // Buchdaten holen aus BESTELLUNG_DETAILS
                        resultBestellungDetails = SQLBestellungDetails.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultBestellung.getString("BESTELLUNG_RECHNR") + "'");

                        Gesamtzeile = resultBestellung.getFloat("BESTELLUNG_VERSAND") * 1D;
//helferlein.Infomeldung("hole bestellungsdetails für " + resultBestellung.getString("BESTELLUNG_RECHNR"));
                        while (resultBestellungDetails.next()) {
//helferlein.Infomeldung("Buch Id " + resultBestellungDetails.getString("BESTELLUNG_DETAIL_BUCH"));
                            Anzahl = resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL");
                            Rabatt = resultBestellungDetails.getInt("BESTELLUNG_DETAIL_RABATT");

                            resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBestellungDetails.getString("BESTELLUNG_DETAIL_BUCH") + "'");
//helferlein.Infomeldung("SQL " + "SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBestellungDetails.getString("BESTELLUNG_DETAIL_BUCH")+"'");
                            Buchpreis = 0D;
                            if (resultBuch.first()) {
//helferlein.Infomeldung("Buchpreis möglich ");
                                Buchpreis = (double) resultBuch.getFloat("BUCH_PREIS");
                            }
//helferlein.Infomeldung("Buchpreis gelesen ");

                            // Gesamtzeile berechnen
                            Gesamtzeile = Gesamtzeile + Anzahl * Buchpreis / 100 * (100 - Rabatt);

                        } // while bestellung details

                        // Gesamtsumme berechnen
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;

                        // Ausgabe RechNr, Kunde, Betrag
//helferlein.Infomeldung("erzeuge ausgabe ");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 715 - 15 * (zeile - 1), resultBestellung.getString("BESTELLUNG_RECHNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 715 - 15 * (zeile - 1), KundenName);
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 520, 715 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));

                        zeile = zeile + 1;
                    } // while bestellungen

                    Linie(cos,1,56, 715 - 15 * (zeile - 1), 539, 715 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;

                    Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 715 - 15 * (zeile - 1), "Gesamtsumme der Mahnungen : ");
                    AusgabeDB(cos, fontPlain, 12, Color.BLACK, 520, 715 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));

                    // close the content stream for page 
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);

                    document.close();

                    Modulhelferlein.Infomeldung(
                            "Liste der offenen Rechnungen/Einnahmen/Mahnungen ist als PDF gespeichert!");
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        Modulhelferlein.Fehlermeldung("Bericht Mahnungen: Exception: " , exept.getMessage());
                    } // try PDF anzeigen

                    if (resultKunde != null) {
                        resultKunde.close();
                    }
                    if (resultBestellungDetails != null) {
                        resultBestellungDetails.close();
                    }
                    if (resultBuch != null) {
                        resultBuch.close();
                    }

                    if (SQLBuch != null) {
                        SQLBuch.close();
                    }
                    if (SQLKunde != null) {
                        SQLKunde.close();
                    }
                    if (SQLBestellungDetails != null) {
                        SQLBestellungDetails.close();
                    }

                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("Bericht Mahnungen: SQL-Exception: SQL-Anfrage nicht moeglich: " + exept.getMessage());
                } // try 

            } // if 
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        } // try

    } // void
} // class

