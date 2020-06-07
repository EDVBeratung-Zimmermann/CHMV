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

import static milesVerlagMain.ModulHelferlein.AusgabeDB;

import jxl.Workbook;
import static jxl.format.Alignment.LEFT;
import static jxl.format.Alignment.RIGHT;
import jxl.write.*;
import static milesVerlagMain.ModulHelferlein.Linie;
import static milesVerlagMain.ModulHelferlein.SQLDate2Normal;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;

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

        Double Gesamtsumme1 = 0D;
        Double Gesamtsumme2 = 0D;
        Double Gesamtzeile = 0D;
        Double Gesamt7 = 0D;
        Double Gesamt19 = 0D;
        Double Buchpreis = 0D;
        Double Gesamteinnahmen = 0D;

        Double einBuch = 0D;
        Double einDruck = 0D;
        Double einKunde = 0D;
        Double einVGW = 0D;
        Double einMarge = 0D;
        Double einSonst = 0D;

        int Anzahl = 0;
        int Rabatt = 0;

        String KundenName = "";

        ResultSet resultKunde = null;
        ResultSet resultBuch = null;
        ResultSet resultBestellungDetails = null;
        ResultSet resultBestellung = null;

        Statement SQLBuch = null;
        Statement SQLKunde = null;
        Statement SQLBestellung = null;
        Statement SQLBestellungDetails = null;

        outputFileName = ModulHelferlein.pathBerichte + "\\Mahnungen\\"
                + "Liste-Mahnungen-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                + ".xls";

        try { // Erstelle Workbook
            WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName));
            WritableSheet sheet_Mahnungen = workbook.createSheet("Offene Einnahmen - Mahnungen", 0);

            try {
                // Create a cell format for Arial 10 point font
                WritableFont arial14fontBold = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
                WritableCellFormat arial14formatBold = new WritableCellFormat(arial14fontBold);

                WritableFont arial10fontBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
                WritableCellFormat arial10formatBold = new WritableCellFormat(arial10fontBold);

                WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 10);
                WritableCellFormat arial10formatR = new WritableCellFormat(arial10font);
                arial10formatR.setAlignment(RIGHT);
                WritableCellFormat arial10formatL = new WritableCellFormat(arial10font);
                arial10formatL.setAlignment(LEFT);

                // Aufbau des Tabellenblattes sheet_Adressen
                Label label = new Label(0, 0, "Übersicht der offenen Rechnungen/Einnahmen im Zeitraum " + strVon + " - " + strBis, arial10formatBold);
                label = new Label(0, 1, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"), arial10formatBold);

                label = new Label(0, 3, "RechNr", arial10formatBold);
                sheet_Mahnungen.addCell(label);
                label = new Label(1, 3, "Kunde", arial10formatBold);
                sheet_Mahnungen.addCell(label);
                label = new Label(2, 3, "Betrag", arial10formatBold);
                sheet_Mahnungen.addCell(label);

                Connection conn = null;

                try { // Datenbank-Treiber laden
                    Class.forName(ModulHelferlein.dbDriver);
                } catch (ClassNotFoundException exept) {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Datenbankanbindung: ClassNotFoundException: Treiber nicht gefunden: " + exept.getMessage());
                } // try Datenbank-Treiber laden

                try { // Verbindung zur Datenbank über die JDBC-Brücke
                    conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Datenbankanbindung: SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
                } // try Verbindung zur Datenbank über die JDBC-Brücke

                final Connection conn2 = conn;

                if (conn2 != null) { // Datenbankverbindung steht

                    try { // Datenbankabfrage
//helferlein.Infomeldung("Datenbankabfrage");
                        // Aufbau des Tabellenblattes sheet_Mahnungen aus Buchbestellungen
                        // RechNr - BestDat  Kunde  Einnahmen  Bezahlt  Bemerkung
                        //  0        1       2      3          4        5
                        label = new Label(0, 0, "Carola Hartmann Miles-Verlag", arial14formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(0, 1, "Offene Einnahmen aus Buchbestellungen von " + strVon + " - " + strBis, arial14formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(0, 3, "RechnungsNr", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(1, 3, "Bestelldatum", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(2, 3, "Kunde", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(3, 3, "Preis in €", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(4, 3, "Bezahlt", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(5, 3, "Bemerkung", arial10formatBold);
                        sheet_Mahnungen.addCell(label);

                        label = new Label(3, 4, "Brutto", arial10formatBold);
                        sheet_Mahnungen.addCell(label);

                        Integer zeile = 6;
                        Integer seite = 1;

                        String strKunde = "";
                        String strLand = "";
                        String Bemerkung = "";

                        SQLBuch = conn2.createStatement();
                        SQLKunde = conn2.createStatement();
                        SQLBestellung = conn2.createStatement();
                        SQLBestellungDetails = conn2.createStatement();

                        String Sql = "SELECT * FROM TBL_BESTELLUNG";
                            Sql = Sql + " WHERE (BESTELLUNG_RECHDAT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + " AND (BESTELLUNG_BEZAHLT = '1970-01-01')"
                                    + " ORDER BY BESTELLUNG_RECHNR, BESTELLUNG_DATUM";
//helferlein.Infomeldung(Sql);                        
                        resultBestellung = SQLBestellung.executeQuery(Sql);
                        Gesamtsumme1 = 0D;
                        while (resultBestellung.next()) { // geht durch alle zeilen
                            // Kundendaten holen
                            if (resultBestellung.getInt("BESTELLUNG_KUNDE") > 0) {
                                resultKunde = SQLKunde.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultBestellung.getString("BESTELLUNG_KUNDE") + "'");
                                resultKunde.next();
                                strKunde = resultKunde.getString("ADRESSEN_NAME");
                                strLand = resultKunde.getString("ADRESSEN_ZUSATZ_3");
                            } else {
                                strKunde = resultBestellung.getString("BESTELLUNG_ZEILE_2");
                                strLand = resultBestellung.getString("BESTELLUNG_ZEILE_6");
                            }

                            // Bemerkungsfeld bestimmen
                            switch (resultBestellung.getInt("BESTELLUNG_TYP")) {
                                case 4:
                                    Bemerkung = "Belegexemplar";
                                    break;
                                case 3:
                                    Bemerkung = "Werbeexemplar";
                                    break;
                                case 2:
                                    Bemerkung = "Pflichtexemplar";
                                    break;
                                case 1:
                                    Bemerkung = "Rezensionsexemplar";
                                    break;
                                case 0:
                                    Bemerkung = "";
                                    switch (resultBestellung.getInt("BESTELLUNG_LAND")) {
                                        case 10:
                                            Bemerkung = Bemerkung + " EU " + strLand;
                                            break;
                                        case 11:
                                            Bemerkung = Bemerkung + " EU " + strLand;
                                            break;
                                        case 20:
                                            Bemerkung = Bemerkung + " Drittland " + strLand;
                                            break;
                                        case 21:
                                            Bemerkung = Bemerkung + " Drittland " + strLand;
                                            break;
                                    }
                                    break;
                            }
                            if (resultBestellung.getBoolean("BESTELLUNG_STORNIERT")) {
                                Bemerkung = "Bestellung storniert";
                            }

                            Gesamtzeile = 0D;
                            Gesamt7 = 0D;
                            Gesamt19 = 0D;

                            // Buchdaten holen aus BESTELLUNG_DETAILS
                            resultBestellungDetails = SQLBestellungDetails.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultBestellung.getString("BESTELLUNG_RECHNR") + "'");

                            Gesamtzeile = 0D;

                            while (resultBestellungDetails.next()) {
                                resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBestellungDetails.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                                resultBuch.next();
                                if (resultBestellungDetails.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                                    Anzahl = 1;
                                    Rabatt = 0;
                                    Buchpreis = (double) resultBestellungDetails.getFloat("BESTELLUNG_DETAIL_SONST_PREIS");
                                    Gesamt19 = Gesamt19 + Anzahl * Buchpreis / 100 * (100 - Rabatt);
                                } else {
                                    Anzahl = resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL");
                                    Rabatt = resultBestellungDetails.getInt("BESTELLUNG_DETAIL_RABATT");
                                    Buchpreis = (double) resultBuch.getFloat("BUCH_PREIS");
                                    Gesamt7 = Gesamt7 + Anzahl * Buchpreis / 100 * (100 - Rabatt);
                                }

                                // Gesamtzeile berechnen
                                Gesamtzeile = Gesamtzeile + Anzahl * Buchpreis / 100 * (100 - Rabatt);
                            } // while bestellung details

                            // Abzug der Umsatzsteuer bei Drittland etc.
                            switch (resultBestellung.getInt("BESTELLUNG_LAND")) {
                                case 20:
                                    Gesamtzeile = Gesamtzeile / 107 * 100;
                                    break;
                                case 21:
                                    Gesamtzeile = Gesamtzeile / 107 * 100;
                                    break;
                                case 10:
                                    if (!resultBestellung.getBoolean("BESTELLUNG_PRIVAT")) {
                                        Gesamtzeile = Gesamtzeile / 107 * 100;
                                    }
                                    break;
                                case 11:
                                    if (!resultBestellung.getBoolean("BESTELLUNG_PRIVAT")) {
                                        Gesamtzeile = Gesamtzeile / 107 * 100;
                                    }
                                    break;
                            }
                            // Versandkosten addieren
                            Gesamtzeile = Gesamtzeile + resultBestellung.getFloat("BESTELLUNG_VERSAND") * 1D;

                            // Gesamtsumme berechnen
                            Gesamtsumme1 = Gesamtsumme1 + Gesamtzeile;

                            label = new Label(0, zeile, resultBestellung.getString("BESTELLUNG_RECHNR"), arial10formatL);
                            sheet_Mahnungen.addCell(label);
                            label = new Label(1, zeile, SQLDate2Normal(resultBestellung.getString("BESTELLUNG_DATUM")), arial10formatL);
                            sheet_Mahnungen.addCell(label);
                            label = new Label(2, zeile, strKunde);
                            sheet_Mahnungen.addCell(label);
                            //label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
                            //sheet_Mahnungen.addCell(label);
                            // Number number = new Number(3, 4, 3.1459);
                            jxl.write.Number number = new jxl.write.Number(3, zeile, ModulHelferlein.round2dec(Gesamtzeile), arial10formatR);
                            sheet_Mahnungen.addCell(number);

                            if (resultBestellung.getString("BESTELLUNG_BEZAHLT").equals("1970-01-01")) {
                                label = new Label(4, zeile, "", arial10formatL);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("BESTELLUNG_BEZAHLT")), arial10formatL);
                            }
                            sheet_Mahnungen.addCell(label);

                            label = new Label(5, zeile, Bemerkung, arial10formatL);
                            sheet_Mahnungen.addCell(label);

                            zeile = zeile + 1;
                        } // while bestellungen geht durch alle zeilen
                        // Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;

                        zeile = zeile + 1;

                        label = new Label(0, zeile, "Gesamtsumme der offenen Einnahmen aus Buchbestellungen:", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        //label = new Label(6, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)), arial10formatBold);
                        //sheet_Mahnungen.addCell(label);
                        jxl.write.Number number = new jxl.write.Number(6, zeile, ModulHelferlein.round2dec(Gesamtsumme1), arial10formatBold);
                        sheet_Mahnungen.addCell(number);

                        zeile = zeile + 3;
                        // RechNr - BestDat  Kunde  Einnahmen  Bezahlt  Bemerkung
                        //  0        1       2      3          4        5
                        label = new Label(0, zeile, "weitere offene Einnahmen im Zeitraum von " + strVon + " - " + strBis, arial14formatBold);
                        sheet_Mahnungen.addCell(label);
                        zeile = zeile + 2;
                        label = new Label(0, zeile, "RechnungsNr", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(1, zeile, "Bestelldatum", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(2, zeile, "Kunde", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(3, zeile, "Preis in €", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(4, zeile, "Bezahlt", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        label = new Label(5, zeile, "Bemerkung", arial10formatBold);
                        sheet_Mahnungen.addCell(label);

                        label = new Label(3, zeile + 1, "Brutto", arial10formatBold);
                        sheet_Mahnungen.addCell(label);

                        zeile = zeile + 2;
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + " AND EINNAHMEN_BEZAHLT = '1970-01-01'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
//helferlein.Infomeldung(Sql);  

                        resultBestellung = SQLBestellung.executeQuery(Sql);

                        Gesamtsumme2 = 0D;
                        while (resultBestellung.next()) { // geht durch alle zeilen
                            Gesamtsumme2 = Gesamtsumme2 + resultBestellung.getFloat("EINNAHMEN_KOSTEN");
                            label = new Label(0, zeile, resultBestellung.getString("EINNAHMEN_RECHNNR"), arial10formatL);
                            sheet_Mahnungen.addCell(label);
                            label = new Label(1, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")), arial10formatL);
                            sheet_Mahnungen.addCell(label);
                            label = new Label(2, zeile, resultBestellung.getString("EINNAHMEN_LIEFERANT"));
                            sheet_Mahnungen.addCell(label);
                            //label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(resultBestellung.getFloat("EINNAHMEN_KOSTEN"))), arial10formatR);
                            //sheet_Mahnungen.addCell(label);
                            number = new jxl.write.Number(3, zeile, ModulHelferlein.round2dec(resultBestellung.getFloat("EINNAHMEN_KOSTEN")), arial10formatR);
                            sheet_Mahnungen.addCell(number);

                            if (resultBestellung.getString("EINNAHMEN_BEZAHLT").equals("1970-01-01")) {
                                label = new Label(4, zeile, "", arial10formatL);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")), arial10formatL);
                            }
                            sheet_Mahnungen.addCell(label);
                            switch (resultBestellung.getInt("EINNAHMEN_TYP")) {
                                //{"Kundenbestellungen", "Margenabrechnungen", "Druckkostenzuschüsse",
                                //"Auszahlung VG Wort", "Sonstige"}
                                case 4:
                                    Bemerkung = "Sonstige: " + resultBestellung.getString("EINNAHMEN_BESCHREIBUNG");
                                    break;
                                case 3:
                                    Bemerkung = "Auszahlung VG Wort";
                                    break;
                                case 2:
                                    Bemerkung = "Druckkostenzuschüsse: " + resultBestellung.getString("EINNAHMEN_BESCHREIBUNG");
                                    break;
                                case 1:
                                    Bemerkung = "Margenabrechnungen";
                                    break;
                                case 0:
                                    Bemerkung = "Kundenbestellungen: " + resultBestellung.getString("EINNAHMEN_BESCHREIBUNG");
                            }

                            label = new Label(5, zeile, Bemerkung, arial10formatL);
                            sheet_Mahnungen.addCell(label);

                            zeile = zeile + 1;
                        }
                        zeile = zeile + 1;

                        label = new Label(0, zeile, "Gesamtsumme der sonstigen offenen Einnahmen:", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        //label = new Label(6, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)), arial10formatBold);
                        //sheet_Mahnungen.addCell(label);
                        number = new jxl.write.Number(6, zeile, ModulHelferlein.round2dec(Gesamtsumme2), arial10formatBold);
                        sheet_Mahnungen.addCell(number);

                        zeile = zeile + 3;

                        label = new Label(0, zeile, "Gesamtsumme aller offenen Einnahmen:", arial10formatBold);
                        sheet_Mahnungen.addCell(label);
                        //label = new Label(6, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)), arial10formatBold);
                        //sheet_Mahnungen.addCell(label);
                        number = new jxl.write.Number(6, zeile, ModulHelferlein.round2dec(Gesamtsumme1 + Gesamtsumme2), arial10formatBold);
                        sheet_Mahnungen.addCell(number);

                        // Fertig - alles schließen
                        try {// workbook write
                            workbook.write();
                        } catch (IOException e) {
                            ModulHelferlein.Fehlermeldung("XLS-Bericht Umsatz", "IO-Exception: ", e.getMessage());
                        } // workbook write

                        try { // try workbook close
                            workbook.close();
                        } catch (IOException e) {
                            ModulHelferlein.Fehlermeldung("XLS-Bericht Umsatz", "IO-Exception: ", e.getMessage());
                        } // try workbook close

                        try { // try XLS anzeigen
                            Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                        } catch (IOException exept) {
                            ModulHelferlein.Fehlermeldung("Bericht Umsatz", "Anzeige XLS-Export: Exception: ", exept.getMessage());
                        } // try XLS anzeigen

                        if (SQLBuch != null) {
                            SQLBuch.close();
                        }
                        if (SQLKunde != null) {
                            SQLKunde.close();
                        }
                        if (SQLBestellungDetails != null) {
                            SQLBestellungDetails.close();
                        }
                        if (resultKunde != null) {
                            resultKunde.close();
                        }
                        if (resultBestellungDetails != null) {
                            resultBestellungDetails.close();
                        }
                        if (resultBuch != null) {
                            resultBuch.close();
                        }
                        if (conn != null) {
                            conn.close();
                        }

                    } catch (SQLException exept) {
                        ModulHelferlein.Fehlermeldung("Bericht Umsätze: ", "XLS-Export: SQL-Exception: SQL-Anfrage nicht moeglich: ", exept.getMessage());

                    } // try Datenbankabfrage
                } // Datenbankverbindung steht
                else {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Datenbankanbindung besteht nicht");
                } // keine Datenbankverbindung

            } catch (WriteException e) {
                ModulHelferlein.Fehlermeldung("XLS-Bericht Mahnungen: Write-Exception: " + e.getMessage());
            } // try Tabellenblätter schreiben

        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("XLS-Bericht Mahnungen: IO-Exception: " + e.getMessage());
        } // try Workbook create

        ModulHelferlein.Infomeldung("Bericht Mahnungen ist als XLS gespeichert!");

    }

    /**
     * Erzeugt eine Übersicht der Einnahmen im Zeitraum strVon bis strBis im
     * Format MS Word
     *
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berichtDOC(String strVon, String strBis) {
        ModulHelferlein.Infomeldung("Diese Funktion ist noch nicht implementiert!");
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
        outputFileName = ModulHelferlein.pathBerichte + "\\Mahnungen\\"
                + "Liste-Mahnungen-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
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

            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "Übersicht der offenen Rechnungen/Einnahmen im Zeitraum " + strVon + " - " + strBis);
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 755, "Seite " + Integer.toString(seite));
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 735, "RechNr");
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 200, 735, "Kunde");
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 735, "Betrag");

            Linie(cos, 1, 56, 730, 539, 730);

            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("Bericht Mahnungen: ClassNotFoundException: Treiber nicht gefunden: " + exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("Bericht Mahnungen: SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
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

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "Übersicht der offenen Rechnungen/Einnahmen im Zeitraum " + strVon + " - " + strBis);
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 755, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 735, "RechNr");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 200, 735, "Kunde");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 735, "Betrag");

                            Linie(cos, 1, 56, 730, 539, 730);
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

                        // AusgabeLB RechNr, Kunde, Betrag
//helferlein.Infomeldung("erzeuge ausgabe ");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 715 - 15 * (zeile - 1), resultBestellung.getString("BESTELLUNG_RECHNR"));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 715 - 15 * (zeile - 1), KundenName);
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 520, 715 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));

                        zeile = zeile + 1;
                    } // while bestellungen

                    Linie(cos, 1, 56, 715 - 15 * (zeile - 1), 539, 715 - 15 * (zeile - 1));

                    zeile = zeile + 1;

                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 715 - 15 * (zeile - 1), "Gesamtsumme der Mahnungen : ");
                    AusgabeDB(cos, fontPlain, 12, Color.BLACK, 520, 715 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)));

                    // close the content stream for page 
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);

                    document.close();

                    ModulHelferlein.Infomeldung(
                            "Liste der offenen Rechnungen/Einnahmen/Mahnungen ist als PDF gespeichert!");
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        ModulHelferlein.Fehlermeldung("Bericht Mahnungen: Exception: ", exept.getMessage());
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
                    ModulHelferlein.Fehlermeldung("Bericht Mahnungen: SQL-Exception: SQL-Anfrage nicht moeglich: " + exept.getMessage());
                } // try 

            } // if 
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        } // try

    } // void
} // class

