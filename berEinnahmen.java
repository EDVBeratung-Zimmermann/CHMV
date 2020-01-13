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

import java.io.IOException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.AusgabeDB;
import static milesVerlagMain.Modulhelferlein.SQLDate2Normal;

import jxl.*;
import static jxl.format.Alignment.LEFT;
import static jxl.format.Alignment.RIGHT;
import jxl.write.*;
import static milesVerlagMain.Modulhelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

/**
 * Klasse zur Erzeugung einer Einnahmen�bersicht
 *
 * @author Thomas Zimmermann
 *
 */
public class berEinnahmen {

    /**
     * Erzeugt eine �bersicht der Einnahmen im Zeitraum strVon bis strBis im
     * Format MS EXCEL
     *
     * @param Umfang
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    @SuppressWarnings("null")
    public static void berichtXLS(boolean Umfang, String strVon, String strBis) {
        //helferlein.Infomeldung("noch nicht implementiert");

        Double Gesamtsumme = 0D;
        Double Gesamtzeile = 0D;
        Double Gesamt7 = 0D;
        Double Gesamt19 = 0D;
        Double Buchpreis = 0D;
        Double Gesamteinnahmen = 0D;

        Double einBuch = 0D;
        Double einKunde = 0D;
        Double einDruck = 0D;
        Double einVGW = 0D;
        Double einMarge = 0D;
        Double einSonst = 0D;

        int Anzahl = 0;
        int Rabatt = 0;

        Statement SQLBuch = null;
        Statement SQLKunde = null;
        Statement SQLBestellung = null;
        Statement SQLBestellungDetails = null;

        ResultSet resultKunde = null;
        ResultSet resultBestellung = null;
        ResultSet resultBuch = null;
        ResultSet resultBestellungDetails = null;
        
        Label label = null;

        String Bemerkung = "";
        String outputFileName;
        
        outputFileName = Modulhelferlein.pathBerichte + "\\Einnahmen";
        if (Umfang) {
            outputFileName = outputFileName + "\\Liste-Einnahmen-Ges-";
        } else {
            outputFileName = outputFileName + "\\Liste-Einnahmen-Bez-";
        };
        outputFileName = outputFileName + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                                        + ".xls";
        
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName));
            WritableSheet sheet_Uebersicht = workbook.createSheet("�bersicht", 0);
            WritableSheet sheet_Buecher = workbook.createSheet("Buchbestellungen", 1);
            WritableSheet sheet_Kunde = workbook.createSheet("Kundenbestellungen", 2);
            WritableSheet sheet_Druckkosten = workbook.createSheet("Druckkostenzusch�sse", 3);
            WritableSheet sheet_VGWort = workbook.createSheet("VG Wort", 4);
            WritableSheet sheet_MargeBOD = workbook.createSheet("Margenabrechnung BOD", 5);
            WritableSheet sheet_Sonstig = workbook.createSheet("Sonstiges", 6);

            // A3 = 0, 2
            // D5 = 3, 4
            // Number number = new Number(3, 4, 3.1459);
            // sheet.addCell(number);
            // Aufbau der Tabellenbl�tter
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

                // Aufbau des Tabellenblattes sheet_Uebersicht
                if (Umfang) {
                    label = new Label(0, 0, "�bersicht der Einnahmen aus allen Rechnungen", arial14formatBold); 
                } else {
                    label = new Label(0, 0, "�bersicht der Einnahmen der bezahlten Rechnungen", arial14formatBold);
                };
                sheet_Uebersicht.addCell(label);

                label = new Label(0, 2, "Einnahmen aus Buchbestellungen", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(0, 3, "Einnahmen aus Druckkostenzusch�ssen", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(0, 4, "Einnahmen aus Abrechnungen VG Wort", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(0, 5, "Einnahmen aus Margenabrechnungen BoD", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(0, 6, "sonstige Einnahmen", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(0, 7, "Einnahmen aus Kundenbestellungen", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(0, 9, "Gesamtsumme der Einnahmen", arial10formatBold);
                sheet_Uebersicht.addCell(label);

                Connection conn = null;

                try { // Datenbank-Treiber laden
                    Class.forName(Modulhelferlein.dbDriver);
                } catch (ClassNotFoundException exept) {
                    Modulhelferlein.Fehlermeldung("Bericht Einnahmen", "ClassNotFoundException: Treiber nicht gefunden: ", exept.getMessage());
                } // try Datenbank-Treiber laden

                try { // Verbindung zur Datenbank �ber die JDBC-Br�cke
                    conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("Bericht Einnahmen", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
                } // try Verbindung zur Datenbank �ber die JDBC-Br�cke

                final Connection conn2 = conn;

                if (conn2 != null) { // Datenbankverbindung steht

                    try { // Datenbankabfrage
                        // Aufbau des Tabellenblattes sheet_Buecher
                        // Datum RechNr Kunde Einnahmen Bezahlt Bemerkung
                        //  0     1      2     3         4       5
                        label = new Label(0, 0, "Einnahmen aus Buchbestellungen", arial14formatBold);
                        sheet_Buecher.addCell(label);
                        label = new Label(0, 2, "Datum", arial10formatBold);
                        sheet_Buecher.addCell(label);
                        label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                        sheet_Buecher.addCell(label);
                        label = new Label(2, 2, "Kunde", arial10formatBold);
                        sheet_Buecher.addCell(label);
                        label = new Label(3, 2, "Einnahmen", arial10formatBold);
                        sheet_Buecher.addCell(label);
                        label = new Label(4, 2, "Bezahlt", arial10formatBold);
                        sheet_Buecher.addCell(label);
                        label = new Label(5, 2, "Bemerkung", arial10formatBold);
                        sheet_Buecher.addCell(label);

                        Integer zeile = 3;
                        Integer seite = 1;

                        String strKunde = "";
                        String strLand = "";

                        SQLBuch = conn2.createStatement();
                        SQLKunde = conn2.createStatement();
                        SQLBestellung = conn2.createStatement();
                        SQLBestellungDetails = conn.createStatement();

                        String Sql = "SELECT * FROM TBL_BESTELLUNG";
                        if (Umfang) {
                            Sql = Sql + " WHERE BESTELLUNG_RECHDAT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + " OR BESTELLUNG_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'";
                        } else {
                            Sql = Sql + " WHERE BESTELLUNG_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'";
                        }
                        Sql = Sql + " ORDER BY BESTELLUNG_RECHNR";
                        resultBestellung = SQLBestellung.executeQuery(Sql);

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
                                case 5:
                                    Bemerkung = "Ersatzexemplar/Remittende";
                                    break;
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
                                Bemerkung = "storniert";
                            }
                            // Buchdaten holen aus BESTELLUNG_DETAILS
                            resultBestellungDetails = SQLBestellungDetails.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultBestellung.getString("BESTELLUNG_RECHNR") + "'");

                            Gesamtzeile = 0D;
                            Gesamt7 = 0D;
                            Gesamt19 = 0D;

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
//                            switch (resultBestellung.getInt("BESTELLUNG_LAND")) {
//                                case 20 : Gesamtzeile = Gesamtzeile / 107 * 100;
//                                    break;
//                                case 21 : Gesamtzeile = Gesamtzeile / 107 * 100;
//                                    break;
//                                case 10 : if (!resultBestellung.getBoolean("BESTELLUNG_PRIVAT")) {
//                                                Gesamtzeile = Gesamtzeile / 107 * 100;
//                                          }
//                                    break;
//                                case 11 : if (!resultBestellung.getBoolean("BESTELLUNG_PRIVAT")) {
//                                                Gesamtzeile = Gesamtzeile / 107 * 100;
//                                          }
//                                    break;
//                            }
                            // Versandkosten addieren
                            Gesamtzeile = Gesamtzeile + resultBestellung.getFloat("BESTELLUNG_VERSAND") * 1D;

                            // Gesamtsumme berechnen
                            Gesamtsumme = Gesamtsumme + Gesamtzeile;

                            // Ausgabe RechNr, Kunde, Betrag
                            // Datum - RechNr  Kunde  Einnahmen  Bezahlt
                            //  0        1       2      3          4
                            label = new Label(0, zeile, SQLDate2Normal(resultBestellung.getString("BESTELLUNG_DATUM")), arial10formatL);
                            sheet_Buecher.addCell(label);
                            label = new Label(1, zeile, resultBestellung.getString("BESTELLUNG_RECHNR"), arial10formatL);
                            sheet_Buecher.addCell(label);
                            label = new Label(2, zeile, strKunde);
                            sheet_Buecher.addCell(label);
                            label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                            sheet_Buecher.addCell(label);

                            if (resultBestellung.getString("BESTELLUNG_BEZAHLT").equals("1970-01-01")) {
                                label = new Label(4, zeile, "noch nicht", arial10formatL);
                                sheet_Buecher.addCell(label);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("BESTELLUNG_BEZAHLT")), arial10formatL);
                                sheet_Buecher.addCell(label);
                            }

                            label = new Label(5, zeile, Bemerkung, arial10formatL);
                            sheet_Buecher.addCell(label);

                            zeile = zeile + 1;
                        } // while bestellungen geht durch alle zeilen
                        zeile = zeile + 1;
                        label = new Label(0, zeile, "Gesamtsumme:", arial10formatBold);
                        sheet_Buecher.addCell(label);
                        label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatBold);
                        sheet_Buecher.addCell(label);

                        Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                        label = new Label(3, 2, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                        sheet_Uebersicht.addCell(label);

                        // Aufbau des Tabellenblattes sheet_Druckkosten
//helferlein.Infomeldung("sheet druckkosten");
                        label = new Label(0, 0, "Einnahmen aus Druckkostenzusch�ssen", arial14formatBold);
                        sheet_Druckkosten.addCell(label);
                        label = new Label(0, 2, "Datum", arial10formatBold);
                        sheet_Druckkosten.addCell(label);
                        label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                        sheet_Druckkosten.addCell(label);
                        label = new Label(2, 2, "Kunde", arial10formatBold);
                        sheet_Druckkosten.addCell(label);
                        label = new Label(3, 2, "Einnahmen", arial10formatBold);
                        sheet_Druckkosten.addCell(label);
                        label = new Label(4, 2, "Bezahlt", arial10formatBold);
                        sheet_Druckkosten.addCell(label);
                        zeile = 3;
                        Gesamtsumme = 0D;
                        if (Umfang) {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE (EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + "       OR EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + "       AND EINNAHMEN_TYP = '2'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        } else {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + "       AND EINNAHMEN_TYP = '2'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        }
                        resultBestellung = SQLBestellung.executeQuery(Sql);

                        Gesamtzeile = 0D;
                        while (resultBestellung.next()) { // geht durch alle zeilen
                            Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;

                            // Ausgabe RechNr, Kunde, Betrag
                            label = new Label(0, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")), arial10formatL);
                            sheet_Druckkosten.addCell(label);
                            label = new Label(1, zeile, resultBestellung.getString("EINNAHMEN_RECHNNR"), arial10formatL);
                            sheet_Druckkosten.addCell(label);
                            label = new Label(2, zeile, resultBestellung.getString("EINNAHMEN_LIEFERANT"), arial10formatL);
                            sheet_Druckkosten.addCell(label);
                            label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                            sheet_Druckkosten.addCell(label);

                            if (resultBestellung.getString("EINNAHMEN_RECHDATUM").equals("1970-01-01")) {
                                label = new Label(4, zeile, "noch nicht", arial10formatL);
                                sheet_Druckkosten.addCell(label);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")), arial10formatL);
                                sheet_Druckkosten.addCell(label);
                            }

                            zeile = zeile + 1;

                            Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        } // geht durch alle zeilen
                        zeile = zeile + 1;
                        label = new Label(0, zeile, "Gesamtsumme:", arial10formatBold);
                        sheet_Druckkosten.addCell(label);
                        label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatBold);
                        sheet_Druckkosten.addCell(label);

                        Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                        label = new Label(3, 3, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                        sheet_Uebersicht.addCell(label);

                        // Aufbau des Tabellenblattes sheet_VGWort
//helferlein.Infomeldung("sheet vg wort");
                        label = new Label(0, 0, "Einnahmen aus Abrechnungen VG Wort", arial14formatBold);
                        sheet_VGWort.addCell(label);
                        label = new Label(0, 2, "Datum", arial10formatBold);
                        sheet_VGWort.addCell(label);
                        label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                        sheet_VGWort.addCell(label);
                        label = new Label(2, 2, "VG Wort", arial10formatBold);
                        sheet_VGWort.addCell(label);
                        label = new Label(3, 2, "Einnahmen", arial10formatBold);
                        sheet_VGWort.addCell(label);
                        label = new Label(4, 2, "Bezahlt", arial10formatBold);
                        sheet_VGWort.addCell(label);
                        zeile = 3;
                        Gesamtsumme = 0D;
                        Gesamtzeile = 0D;
                        if (Umfang) {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE (EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + "       OR EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + "       AND EINNAHMEN_TYP = '3'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        } else {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + "       AND EINNAHMEN_TYP = '3'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        }
                        resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                        while (resultBestellung.next()) { // geht durch alle zeilen
                            Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;

                            // Ausgabe RechNr, Kunde, Betrag
                            label = new Label(0, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")), arial10formatL);
                            sheet_VGWort.addCell(label);
                            label = new Label(1, zeile, resultBestellung.getString("EINNAHMEN_RECHNNR"), arial10formatL);
                            sheet_VGWort.addCell(label);
                            label = new Label(2, zeile, resultBestellung.getString("EINNAHMEN_LIEFERANT"), arial10formatL);
                            sheet_VGWort.addCell(label);
                            label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                            sheet_VGWort.addCell(label);

                            if (resultBestellung.getString("EINNAHMEN_RECHDATUM").equals("1970-01-01")) {
                                label = new Label(4, zeile, "noch nicht", arial10formatL);
                                sheet_VGWort.addCell(label);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")), arial10formatL);
                                sheet_VGWort.addCell(label);
                            }

                            zeile = zeile + 1;

                            Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        } // geht durch alle zeilen
                        zeile = zeile + 1;
                        label = new Label(0, zeile, "Gesamtsumme:", arial10formatBold);
                        sheet_VGWort.addCell(label);
                        label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatBold);
                        sheet_VGWort.addCell(label);

                        Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                        label = new Label(3, 4, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                        sheet_Uebersicht.addCell(label);

                        // Aufbau des Tabellenblattes sheet_MargeBOD
//helferlein.Infomeldung("sheet marge bod");
                        label = new Label(0, 0, "Einnahmen aus Margenabrechnungen BOD", arial14formatBold);
                        sheet_MargeBOD.addCell(label);
                        label = new Label(0, 2, "Datum", arial10formatBold);
                        sheet_MargeBOD.addCell(label);
                        label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                        sheet_MargeBOD.addCell(label);
                        label = new Label(2, 2, "Marge BoD", arial10formatBold);
                        sheet_MargeBOD.addCell(label);
                        label = new Label(3, 2, "Einnahmen", arial10formatBold);
                        sheet_MargeBOD.addCell(label);
                        label = new Label(4, 2, "Bezahlt", arial10formatBold);
                        sheet_MargeBOD.addCell(label);

                        zeile = 3;
                        Gesamtsumme = 0D;
                        Gesamtzeile = 0D;
                        if (Umfang) {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE (EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + "       OR EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + "       AND EINNAHMEN_TYP = '1'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        } else {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + "       AND EINNAHMEN_TYP = '1'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        }
                        resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                        while (resultBestellung.next()) { // geht durch alle zeilen
                            Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;

                            // Ausgabe RechNr, Kunde, Betrag
                            label = new Label(0, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")), arial10formatL);
                            sheet_MargeBOD.addCell(label);
                            label = new Label(1, zeile, resultBestellung.getString("EINNAHMEN_RECHNNR"), arial10formatL);
                            sheet_MargeBOD.addCell(label);
                            label = new Label(2, zeile, resultBestellung.getString("EINNAHMEN_LIEFERANT"), arial10formatL);
                            sheet_MargeBOD.addCell(label);
                            label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                            sheet_MargeBOD.addCell(label);

                            if (resultBestellung.getString("EINNAHMEN_RECHDATUM").equals("1970-01-01")) {
                                label = new Label(4, zeile, "noch nicht", arial10formatL);
                                sheet_MargeBOD.addCell(label);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")), arial10formatL);
                                sheet_MargeBOD.addCell(label);
                            }

                            zeile = zeile + 1;

                            Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        } // geht durch alle zeilen
                        zeile = zeile + 1;
                        label = new Label(0, zeile, "Gesamtsumme:", arial10formatBold);
                        sheet_MargeBOD.addCell(label);
                        label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatBold);
                        sheet_MargeBOD.addCell(label);

                        Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                        label = new Label(3, 5, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                        sheet_Uebersicht.addCell(label);

                        // Aufbau des Tabellenblattes sheet_Sonstig
//helferlein.Infomeldung("sheet sonstiges");
                        label = new Label(0, 0, "Sonstige Einnahmen", arial14formatBold);
                        sheet_Sonstig.addCell(label);
                        zeile = 3;
                        Gesamtsumme = 0D;
                        Gesamtzeile = 0D;
                        if (Umfang) {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE (EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + "       OR EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + "       AND EINNAHMEN_TYP = '4'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        } else {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + "       AND EINNAHMEN_TYP = '4'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        }
                        resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                        while (resultBestellung.next()) { // geht durch alle zeilen
                            Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;

                            // Ausgabe RechNr, Kunde, Betrag
                            label = new Label(0, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")), arial10formatL);
                            sheet_Sonstig.addCell(label);
                            label = new Label(1, zeile, resultBestellung.getString("EINNAHMEN_RECHNNR"), arial10formatL);
                            sheet_Sonstig.addCell(label);
                            label = new Label(2, zeile, resultBestellung.getString("EINNAHMEN_LIEFERANT"), arial10formatL);
                            sheet_Sonstig.addCell(label);
                            label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                            sheet_Sonstig.addCell(label);

                            if (resultBestellung.getString("EINNAHMEN_RECHDATUM").equals("1970-01-01")) {
                                label = new Label(4, zeile, "noch nicht", arial10formatL);
                                sheet_Sonstig.addCell(label);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")), arial10formatL);
                                sheet_Sonstig.addCell(label);
                            }
                            label = new Label(5, zeile, resultBestellung.getString("EINNAHMEN_BESCHREIBUNG"), arial10formatR);
                            sheet_Sonstig.addCell(label);

                            zeile = zeile + 1;

                            Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        } // geht durch alle zeilen
                        zeile = zeile + 1;
                        label = new Label(0, zeile, "Gesamtsumme:", arial10formatBold);
                        sheet_Sonstig.addCell(label);
                        label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatBold);
                        sheet_Sonstig.addCell(label);

                        Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                        label = new Label(3, 6, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                        sheet_Uebersicht.addCell(label);

                        // Aufbau des Tabellenblattes sheet_Kunde
//helferlein.Infomeldung("sheet sonstiges");
                        label = new Label(0, 0, "Einnahmen aus Kundenbestellungen", arial14formatBold);
                        sheet_Kunde.addCell(label);
                        zeile = 3;
                        Gesamtsumme = 0D;
                        Gesamtzeile = 0D;
                        einKunde = 0D;
                        if (Umfang) {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE (EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + "       OR EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + "       AND EINNAHMEN_TYP = '0'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        } else {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                    + "       AND EINNAHMEN_TYP = '0'"
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        }
                        resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                        while (resultBestellung.next()) { // geht durch alle zeilen
                            Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;
                            einKunde = einKunde + Gesamtzeile;

                            // Ausgabe RechNr, Kunde, Betrag
                            label = new Label(0, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")), arial10formatL);
                            sheet_Kunde.addCell(label);
                            label = new Label(1, zeile, resultBestellung.getString("EINNAHMEN_RECHNNR"), arial10formatL);
                            sheet_Kunde.addCell(label);
                            label = new Label(2, zeile, resultBestellung.getString("EINNAHMEN_LIEFERANT"), arial10formatL);
                            sheet_Kunde.addCell(label);
                            label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                            sheet_Kunde.addCell(label);

                            if (resultBestellung.getString("EINNAHMEN_RECHDATUM").equals("1970-01-01")) {
                                label = new Label(4, zeile, "noch nicht", arial10formatL);
                                sheet_Sonstig.addCell(label);
                            } else {
                                label = new Label(4, zeile, resultBestellung.getString("EINNAHMEN_BEZAHLT"), arial10formatL);
                                sheet_Kunde.addCell(label);
                            }
                            //label = new Label(5, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BESCHREIBUNG")), arial10formatR);
                            label = new Label(5, zeile, resultBestellung.getString("EINNAHMEN_BESCHREIBUNG"), arial10formatR);
                            sheet_Kunde.addCell(label);

                            zeile = zeile + 1;

                            Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        } // geht durch alle zeilen
                        zeile = zeile + 1;
                        label = new Label(0, zeile, "Gesamtsumme:", arial10formatBold);
                        sheet_Kunde.addCell(label);
                        label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatBold);
                        sheet_Kunde.addCell(label);

                        Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                        label = new Label(3, 7, Modulhelferlein.str2dec(einKunde), arial10formatR);
                        sheet_Uebersicht.addCell(label);

                        // Gesamteinnahmn schreiben
                        label = new Label(3, 9, Modulhelferlein.str2dec(Gesamteinnahmen), arial10formatR);
                        sheet_Uebersicht.addCell(label);

                        // Fertig - alles schlie�en
                        try {// workbook write
                            workbook.write();
                        } catch (IOException e) {
                            Modulhelferlein.Fehlermeldung("XLS-Bericht Einnahmen: IO-Exception: ", e.getMessage());
                        } // workbook write

                        try { // try workbook close
                            workbook.close();
                        } catch (IOException e) {
                            Modulhelferlein.Fehlermeldung("XLS-Bericht: IO-Exception: ", e.getMessage());
                        } // try workbook close

                        try { // try XLS anzeigen
                            Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                        } catch (IOException exept) {
                            Modulhelferlein.Fehlermeldung("Bericht Einnahmen", "Anzeige XLS-Export: Exception: ", exept.getMessage());
                        } // try XLS anzeigen

                        if (SQLBuch != null) {
                            SQLBuch.close();
                        }
                        if (SQLKunde != null) {
                            SQLKunde.close();
                        }
                        if (SQLBestellung != null) {
                            SQLBestellung.close();
                        }
                        if (SQLBestellungDetails != null) {
                            SQLBestellungDetails.close();
                        }
                        if (resultKunde != null) {
                            resultKunde.close();
                        }
                        if (resultBestellung != null) {
                            resultBestellung.close();
                        }
                        if (resultBestellungDetails != null) {
                            resultBestellungDetails.close();
                        }
                        if (resultBuch != null) {
                            resultBuch.close();
                        }
                        if (conn2 != null) {
                            conn2.close();
                        }
                        if (conn != null) {
                            conn.close();
                        }

                    } catch (SQLException exept) {
                        Modulhelferlein.Fehlermeldung("Bericht Einnahmen", "XLS-Export: SQL-Exception: SQL-Anfrage nicht moeglich: ", exept.getMessage());

                    } // try Datenbankabfrage
                } // Datenbankverbindung steht

            } catch (WriteException e) {
                Modulhelferlein.Fehlermeldung("Bericht Einnahmen", "XLS-Bericht: Write-Exception: ", e.getMessage());
            } // try Tabellenbl�tter schreiben

        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("Bericht Einnahmen", "XLS-Bericht: IO-Exception: ", e.getMessage());
        } // try Workbook create

        Modulhelferlein.Infomeldung("Liste der Einnahmen ist als XLS gespeichert!");
    } // XLS-Bericht erzeugen

    /**
     * Erzeugt eine �bersicht der Einnahmen im Zeitraum strVon bis strBis im
     * Format MS Word
     *
     * @param Umfang
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berichtDOC(boolean Umfang, String strVon, String strBis) {
        Modulhelferlein.Infomeldung("noch nicht implementiert");
    }

    /**
     * Erzeugt eine �bersicht der Einnahmen im Zeitraum strVon bis strBis im
     * Format PDF
     *
     * @param Umfang
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    @SuppressWarnings("null")
    public static void berichtPDF(boolean Umfang, String strVon, String strBis) {
        Double Gesamtsumme = 0D;
        Double Gesamtzeile = 0D;
        Double Gesamt7 = 0D;
        Double Gesamt19 = 0D;
        Double Buchpreis = 0D;

        Double einBuch = 0D;
        Double einKunde = 0D;
        Double einDruck = 0D;
        Double einVGW = 0D;
        Double einMarge = 0D;
        Double einSonst = 0D;

        int Anzahl = 0;
        int Rabatt = 0;

        Statement SQLBuch = null;
        Statement SQLKunde = null;
        Statement SQLBestellung = null;
        Statement SQLBestellungDetails = null;

        ResultSet resultKunde = null;
        ResultSet resultBestellung = null;
        ResultSet resultBuch = null;
        ResultSet resultBestellungDetails = null;

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(A4);
        document.addPage(page);

        PDPageContentStream cos;

        String Bemerkung = "";
        String outputFileName;
        outputFileName = Modulhelferlein.pathBerichte + "\\Einnahmen"
                + "\\Liste-Einnahmen-"
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                + ".pdf";

        PDDocumentInformation docInfo = document.getDocumentInformation();

        docInfo.setSubject("Einnahmen");
        docInfo.setTitle("miles-Verlag Umsatz");
        docInfo.setAuthor("miles-Verlag");
        docInfo.setCreationDate(Calendar.getInstance());
        docInfo.setCreator("miles-Verlag");
        docInfo.setProducer("miles-Verlag");

        PDFont fontPlain = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;

        try { // Start a new content stream which will "hold" the to be created content
            cos = new PDPageContentStream(document, page);

            Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
            Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 750, "Einnahmen im Zeitraum " + strVon + " - " + strBis);
            Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 600, "Teil 1 - Einnahmen aus Buchbestellungen");
            Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 485, "Teil 2 - Weitere Einnahmen");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 460, "- Kundenbestellungen");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 440, "- Druckkostenzusch�sse");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 420, "- Verg�tungen VG Wort");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 400, "- Margenabrechnungen BoD");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 380, "- Sonstige Einnahmen");

            cos.close();
            page = new PDPage(A4);
            document.addPage(page);

            cos = new PDPageContentStream(document, page);

            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Bericht Einnahmen", "ClassNotFoundException: Treiber nicht gefunden: ", exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank �ber die JDBC-Br�cke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Bericht Einnahmen", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
            } // try Verbindung zur Datenbank �ber die JDBC-Br�cke

            final Connection conn2 = conn;

            if (conn2 != null) {

                try {
                    Integer zeile = 1;
                    Integer seite = 1;

                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Einnahmen aus Buchbestellungen");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 220, 740, "Kunde");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 550, 740, "Bemerkung");

                    Linie(cos, 1, 56, 735, 539, 735);

                    SQLBuch = conn2.createStatement();
                    SQLKunde = conn2.createStatement();
                    SQLBestellung = conn2.createStatement();
                    SQLBestellungDetails = conn.createStatement();

                    String Sql = "SELECT * FROM TBL_BESTELLUNG";
                    if (Umfang) {
                        Sql = Sql + " WHERE BESTELLUNG_RECHDAT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + " OR BESTELLUNG_BEZAHLT BETWEEN '" + strVon + "' AND '" + strBis + "'";
                    } else {
                        Sql = Sql + " WHERE BESTELLUNG_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'";
                    }
                    Sql = Sql + " ORDER BY BESTELLUNG_RECHNR";
                    resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultBestellung.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Einnahmen aus Buchbestellungen");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));

                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 220, 740, "Kunde");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 550, 740, "Bemerkung");

                            Linie(cos, 1, 56, 735, 539, 735);

                        } // if

                        // Kundendaten holen
                        String strKunde = "";
                        String strLand = "";

                        if (resultBestellung.getInt("BESTELLUNG_KUNDE") > 0) {
                            resultKunde = SQLKunde.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + resultBestellung.getString("BESTELLUNG_KUNDE") + "'");
                            resultKunde.next();
                            strKunde = resultKunde.getString("ADRESSEN_NAME");
                            strLand = resultKunde.getString("ADRESSEN_ZUSATZ_3");
                        } else {
                            strKunde = resultBestellung.getString("BESTELLUNG_ZEILE_2");
                            strLand = resultBestellung.getString("BESTELLUNG_ZEILE_6");
                        }

                        // Buchdaten holen aus BESTELLUNG_DETAILS
                        resultBestellungDetails = SQLBestellungDetails.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultBestellung.getString("BESTELLUNG_RECHNR") + "'");

                        Gesamtzeile = 0D;
                        Gesamt19 = 0D;
                        Gesamt7 = 0D;

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
//                            switch (resultBestellung.getInt("BESTELLUNG_LAND")) {
//                                case 20 : Gesamtzeile = Gesamtzeile / 107 * 100;
//                                    break;
//                                case 21 : Gesamtzeile = Gesamtzeile / 107 * 100;
//                                    break;
//                                case 10 : if (!resultBestellung.getBoolean("BESTELLUNG_PRIVAT")) {
//                                                Gesamtzeile = Gesamtzeile / 107 * 100;
//                                          }
//                                    break;
//                                case 11 : if (!resultBestellung.getBoolean("BESTELLUNG_PRIVAT")) {
//                                                Gesamtzeile = Gesamtzeile / 107 * 100;
//                                          }
//                                    break;
//                            }
                        // Versandkosten addieren
                        Gesamtzeile = Gesamtzeile + resultBestellung.getFloat("BESTELLUNG_VERSAND") * 1D;

                        // Gesamtsumme berechnen
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("BESTELLUNG_DATUM")));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 130, 720 - 15 * (zeile - 1), resultBestellung.getString("BESTELLUNG_RECHNR"));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 220, 720 - 15 * (zeile - 1), strKunde);
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("BESTELLUNG_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("BESTELLUNG_BEZAHLT")));
                        }

                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 550, 720 - 15 * (zeile - 1), Bemerkung);

                        zeile = zeile + 1;
                    } // while bestellungen

                    Linie(cos, 1, 56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), "Gesamtsumme der Einnahmen : ");
                    AusgabeDB(cos, fontPlain, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    einBuch = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // jetzt sonstige Einnahmen Kundenbestellungen -0- ausgeben
                    page = new PDPage(A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kundenbestellungen BoD");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                    Linie(cos, 1, 56, 735, 539, 735);

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_EINNAHMEN"
                                + " WHERE (EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       OR EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       AND EINNAHMEN_TYP = '0'"
                                + " ORDER BY EINNAHMEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_EINNAHMEN"
                                + " WHERE EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND EINNAHMEN_TYP = '0'"
                                + " ORDER BY EINNAHMEN_RECHDATUM";
                    }
                    resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultBestellung.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Margenabrechungen BoD");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                            Linie(cos, 1, 56, 735, 539, 735);

                        } // if

                        Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 130, 720 - 15 * (zeile - 1), resultBestellung.getString("EINNAHMEN_RECHNNR"));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("EINNAHMEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while

                    Linie(cos, 1, 56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), "Gesamtsumme der Einnahmen : ");
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    einKunde = Gesamtsumme;

                    cos.close();

                    // jetzt sonstige Einnahmen Margenabrechnungen -1- ausgeben
                    page = new PDPage(A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Margenabrechungen BoD");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                    Linie(cos, 1, 56, 735, 539, 735);

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_EINNAHMEN"
                                + " WHERE (EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       OR EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       AND EINNAHMEN_TYP = '1'"
                                + " ORDER BY EINNAHMEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_EINNAHMEN"
                                + " WHERE EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND EINNAHMEN_TYP = '1'"
                                + " ORDER BY EINNAHMEN_RECHDATUM";
                    }
                    resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultBestellung.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Margenabrechungen BoD");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                            Linie(cos, 1, 56, 735, 539, 735);

                        } // if

                        Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 130, 720 - 15 * (zeile - 1), resultBestellung.getString("EINNAHMEN_RECHNNR"));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("EINNAHMEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while

                    Linie(cos, 1, 56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));

                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), "Gesamtsumme der Einnahmen : ");
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    einMarge = Gesamtsumme;

                    cos.close();

                    // jetzt EinnahmenTyp Druckkostenzusch�sse -2- ausgeben
                    page = new PDPage(A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Druckkostenzusch�sse");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 220, 740, "Autor");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                    Linie(cos, 1, 56, 735, 539, 735);

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_EINNAHMEN"
                                + " WHERE (EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       OR EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       AND EINNAHMEN_TYP = '2'"
                                + " ORDER BY EINNAHMEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_EINNAHMEN"
                                + " WHERE EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND EINNAHMEN_TYP = '2'"
                                + " ORDER BY EINNAHMEN_RECHDATUM";
                    }
                    resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultBestellung.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Druckkostenzusch�sse");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 220, 740, "Autor");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                            Linie(cos, 1, 56, 735, 539, 735);

                        } // if

                        Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 130, 720 - 15 * (zeile - 1), resultBestellung.getString("EINNAHMEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 220, 720 - 15 * (zeile - 1), resultBestellung.getString("EINNAHMEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("EINNAHMEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while

                    Linie(cos, 1, 56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));

                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), "Gesamtsumme der Einnahmen : ");
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    einDruck = Gesamtsumme;

                    cos.close();

                    // jetzt EinnahmenTyp VG-Wort -3- ausgeben
                    page = new PDPage(A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Auszahlungen VG-Wort");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                    Linie(cos, 1, 56, 735, 539, 735);

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_EINNAHMEN"
                                + " WHERE (EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       OR EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       AND EINNAHMEN_TYP = '3'"
                                + " ORDER BY EINNAHMEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_EINNAHMEN"
                                + " WHERE EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND EINNAHMEN_TYP = '3'"
                                + " ORDER BY EINNAHMEN_RECHDATUM";
                    }
                    resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultBestellung.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Auszahlungen VG-Wort");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                            Linie(cos, 1, 56, 735, 539, 735);

                        } // if

                        Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")));

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while

                    Linie(cos, 1, 56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));

                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), "Gesamtsumme der Einnahmen : ");
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    einVGW = Gesamtsumme;

                    cos.close();

                    // jetzt EinnahmenTyp sonstige Einnahmen -4- ausgeben
                    page = new PDPage(A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Einnahmen");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 220, 740, "Lieferant");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                    Linie(cos, 1, 56, 735, 539, 735);

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_EINNAHMEN"
                                + " WHERE (EINNAHMEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       OR EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       AND EINNAHMEN_TYP = '4'"
                                + " ORDER BY EINNAHMEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_EINNAHMEN"
                                + " WHERE EINNAHMEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND EINNAHMEN_TYP = '4'"
                                + " ORDER BY EINNAHMEN_RECHDATUM";
                    }
                    resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultBestellung.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Einnahmen");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 220, 740, "Lieferant");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                            Linie(cos, 1, 56, 735, 539, 735);

                        } // if

                        Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), resultBestellung.getString("EINNAHMEN_RECHDATUM"));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 130, 720 - 15 * (zeile - 1), resultBestellung.getString("EINNAHMEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 220, 720 - 15 * (zeile - 1), resultBestellung.getString("EINNAHMEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("EINNAHMEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), resultBestellung.getString("EINNAHMEN_BEZAHLT"));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while
                    Linie(cos, 1, 56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));

                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), "Gesamtsumme der Einnahmen : ");
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    einSonst = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // jetzt Gesamtbilanz ausgeben
                    page = new PDPage(A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 750, "Einnahmen im Zeitraum " + strVon + " - " + strBis);
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 600, "Einnahmen aus Buchbestellungen");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 600, Modulhelferlein.df.format(Modulhelferlein.round2dec(einBuch)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 600, "Euro");
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 485, "Weitere Einnahmen");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 485, Modulhelferlein.df.format(Modulhelferlein.round2dec(einDruck + einVGW + einMarge + einSonst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 485, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 460, "- Kundenbestellungen");
                    AusgabeDB(cos, fontBold, 14, Color.BLACK, 440, 460, Modulhelferlein.df.format(Modulhelferlein.round2dec(einKunde)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 460, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 440, "- Druckkostenzusch�sse");
                    AusgabeDB(cos, fontBold, 14, Color.BLACK, 440, 440, Modulhelferlein.df.format(Modulhelferlein.round2dec(einDruck)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 440, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 420, "- Verg�tungen VG Wort");
                    AusgabeDB(cos, fontBold, 14, Color.BLACK, 440, 420, Modulhelferlein.df.format(Modulhelferlein.round2dec(einVGW)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 420, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 400, "- Margenabrechnungen BoD");
                    AusgabeDB(cos, fontBold, 14, Color.BLACK, 440, 400, Modulhelferlein.df.format(Modulhelferlein.round2dec(einMarge)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 400, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 380, "- Sonstige Einnahmen");
                    AusgabeDB(cos, fontBold, 14, Color.BLACK, 440, 380, Modulhelferlein.df.format(Modulhelferlein.round2dec(einSonst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 380, "Euro");

                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 300, "Gesamtsumme der Einnahmen : ");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 300, Modulhelferlein.df.format(Modulhelferlein.round2dec(einBuch + einKunde + einDruck + einVGW + einMarge + einSonst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 300, "Euro");

                    // close the content stream for page 
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    Modulhelferlein.Infomeldung(
                            "Liste der Einnahmen ist als PDF gespeichert!");
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        Modulhelferlein.Fehlermeldung("Bericht Einnamen", "Anzeige PDF: IO-Exception: ", exept.getMessage());
                    } // try PDF anzeigen

                    if (SQLBuch != null) {
                        SQLBuch.close();
                    }
                    if (SQLKunde != null) {
                        SQLKunde.close();
                    }
                    if (SQLBestellung != null) {
                        SQLBestellung.close();
                    }
                    if (SQLBestellungDetails != null) {
                        SQLBestellungDetails.close();
                    }
                    if (resultKunde != null) {
                        resultKunde.close();
                    }
                    if (resultBestellung != null) {
                        resultBestellung.close();
                    }
                    if (resultBestellungDetails != null) {
                        resultBestellungDetails.close();
                    }
                    if (resultBuch != null) {
                        resultBuch.close();
                    }
                    if (conn2 != null) {
                        conn2.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }

                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("Bericht Einnahmen", "SQL-Exception: SQL-Anfrage nicht moeglich: ", exept.getMessage());
                } // try 
            }
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("Bericht Einnahmen", "IO-Exception: ", e.getMessage());
        } // try

    } // void
} // class

