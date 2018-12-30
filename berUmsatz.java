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

import java.io.IOException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.SQLDate2Normal;

import jxl.*;
import static jxl.format.Alignment.LEFT;
import static jxl.format.Alignment.RIGHT;
import jxl.write.*;
import static milesVerlagMain.Modulhelferlein.AusgabeDB;
import static milesVerlagMain.Modulhelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

/**
 * Klasse zur Erzeugung einer Umsatzübersicht
 *
 * @author Thomas Zimmermann
 *
 */
public class berUmsatz {

    /**
     * Erzeugt eine Übersicht der Einnahmen im Zeitraum strVon bis strBis im
     * Format MS EXCEL
     *
     * @param Umfang
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berUmsatzXLS(boolean Umfang, String strVon, String strBis) {

        Double Gesamtsumme = 0D;
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

        Statement SQLBuch = null;
        Statement SQLKunde = null;
        Statement SQLBestellung = null;
        Statement SQLBestellungDetails = null;

        ResultSet resultKunde = null;
        ResultSet resultBestellung = null;
        ResultSet resultBuch = null;
        ResultSet resultBestellungDetails = null;

        String outputFileName;
        outputFileName = Modulhelferlein.pathBerichte + "\\Umsätze"
                + "\\Umsatz-"
                + strVon
                + "-"
                + strBis
                + "-"
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                + ".xls";
//helferlein.Infomeldung(outputFileName);
        try {
//helferlein.Infomeldung("try create workbook sheet");            
            WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName));
            WritableSheet sheet_Einnahmen = workbook.createSheet("Einnahmen", 0);
            WritableSheet sheet_Ausgaben = workbook.createSheet("Ausgaben", 1);

            // A3 = 0, 2
            // D5 = 3, 4
            // Number number = new Number(3, 4, 3.1459);
            // sheet.addCell(number);
            // Aufbau der Tabellenblätter
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

                Label label = new Label(0, 0, "Übersicht der Einnahmen", arial14formatBold);
                // Aufbau des Tabellenblattes sheet_Einnahmen

                Connection conn = null;

                Class.forName(Modulhelferlein.dbDriver);
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);

                final Connection conn2 = conn;

                if (conn2 != null) { // Datenbankverbindung steht

                    try { // Datenbankabfrage
//helferlein.Infomeldung("Datenbankabfrage");
                        // Aufbau des Tabellenblattes sheet_Einnahmen aus Buchbestellungen
                        // RechNr - BestDat  Kunde  Einnahmen  Bezahlt  Bemerkung
                        //  0        1       2      3          4        5
                        label = new Label(0, 0, "Carola Hartmann Miles-Verlag", arial14formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(0, 1, "Ausgestellte Rechnungen aus Buchbestellungen von " + strVon + " - " + strBis, arial14formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(0, 3, "RechnungsNr", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(1, 3, "Bestelldatum", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(2, 3, "Kunde", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(3, 3, "Preis in €", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(4, 3, "Bezahlt", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(5, 3, "Bemerkung", arial10formatBold);
                        sheet_Einnahmen.addCell(label);

                        label = new Label(3, 4, "Brutto", arial10formatBold);
                        sheet_Einnahmen.addCell(label);

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
                        if (Umfang) { // Alle
                            Sql = Sql + " WHERE (BESTELLUNG_RECHDAT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + " OR (BESTELLUNG_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + " ORDER BY BESTELLUNG_RECHNR, BESTELLUNG_DATUM";
                        } else { // bezahlte
                            Sql = Sql + " WHERE (BESTELLUNG_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + " ORDER BY BESTELLUNG_RECHNR, BESTELLUNG_DATUM";
                        }
//helferlein.Infomeldung(Sql);                        
                        resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert
                        Gesamtsumme = 0D;
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
                                    Bemerkung = "Geschenk";
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

                            label = new Label(0, zeile, resultBestellung.getString("BESTELLUNG_RECHNR"), arial10formatL);
                            sheet_Einnahmen.addCell(label);
                            label = new Label(1, zeile, SQLDate2Normal(resultBestellung.getString("BESTELLUNG_DATUM")), arial10formatL);
                            sheet_Einnahmen.addCell(label);
                            label = new Label(2, zeile, strKunde);
                            sheet_Einnahmen.addCell(label);
                            //label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                            //sheet_Einnahmen.addCell(label);
                            // Number number = new Number(3, 4, 3.1459);
                            jxl.write.Number number = new jxl.write.Number(3, zeile, Modulhelferlein.round2dec(Gesamtzeile), arial10formatR);
                            sheet_Einnahmen.addCell(number);

                            if (resultBestellung.getString("BESTELLUNG_BEZAHLT").equals("1970-01-01")) {
                                label = new Label(4, zeile, "", arial10formatL);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("BESTELLUNG_BEZAHLT")), arial10formatL);
                            }
                            sheet_Einnahmen.addCell(label);

                            label = new Label(5, zeile, Bemerkung, arial10formatL);
                            sheet_Einnahmen.addCell(label);

                            zeile = zeile + 1;
                        } // while bestellungen geht durch alle zeilen
                        // Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;

                        zeile = zeile + 1;

                        label = new Label(0, zeile, "Gesamtsumme der Einnahmen aus Buchbestellungen:", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        //label = new Label(6, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)), arial10formatBold);
                        //sheet_Einnahmen.addCell(label);
                        jxl.write.Number number = new jxl.write.Number(6, zeile, Modulhelferlein.round2dec(Gesamtsumme), arial10formatBold);
                        sheet_Einnahmen.addCell(number);

                        zeile = zeile + 3;
                        // RechNr - BestDat  Kunde  Einnahmen  Bezahlt  Bemerkung
                        //  0        1       2      3          4        5
                        label = new Label(0, zeile, "weitere Einnahmen im Zeitraum von " + strVon + " - " + strBis, arial14formatBold);
                        sheet_Einnahmen.addCell(label);
                        zeile = zeile + 2;
                        label = new Label(0, zeile, "RechnungsNr", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(1, zeile, "Bestelldatum", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(2, zeile, "Kunde", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(3, zeile, "Preis in €", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(4, zeile, "Bezahlt", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        label = new Label(5, zeile, "Bemerkung", arial10formatBold);
                        sheet_Einnahmen.addCell(label);

                        label = new Label(3, zeile + 1, "Brutto", arial10formatBold);
                        sheet_Einnahmen.addCell(label);

                        zeile = zeile + 2;
                        if (Umfang) {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE EINNAHMEN_RECHDATUM BETWEEN \"" + strVon + "\"  AND \"" + strBis + "\""
                                    + " OR EINNAHMEN_BEZAHLT BETWEEN \"" + strVon + "\"  AND \"" + strBis + "\""
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        } else {
                            Sql = "SELECT * FROM TBL_EINNAHMEN"
                                    + " WHERE EINNAHMEN_BEZAHLT BETWEEN \"" + strVon + "\"  AND \"" + strBis + "\""
                                    + " ORDER BY EINNAHMEN_RECHDATUM";
                        }
//helferlein.Infomeldung(Sql);  

                        resultBestellung = SQLBestellung.executeQuery(Sql);

                        Gesamtsumme = 0D;
                        while (resultBestellung.next()) { // geht durch alle zeilen
                            Gesamtsumme = Gesamtsumme + resultBestellung.getFloat("EINNAHMEN_KOSTEN");
                            label = new Label(0, zeile, resultBestellung.getString("EINNAHMEN_RECHNNR"), arial10formatL);
                            sheet_Einnahmen.addCell(label);
                            label = new Label(1, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")), arial10formatL);
                            sheet_Einnahmen.addCell(label);
                            label = new Label(2, zeile, resultBestellung.getString("EINNAHMEN_LIEFERANT"));
                            sheet_Einnahmen.addCell(label);
                            //label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(resultBestellung.getFloat("EINNAHMEN_KOSTEN"))), arial10formatR);
                            //sheet_Einnahmen.addCell(label);
                            number = new jxl.write.Number(3, zeile, Modulhelferlein.round2dec(resultBestellung.getFloat("EINNAHMEN_KOSTEN")), arial10formatR);
                            sheet_Einnahmen.addCell(number);

                            if (resultBestellung.getString("EINNAHMEN_BEZAHLT").equals("1970-01-01")) {
                                label = new Label(4, zeile, "", arial10formatL);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")), arial10formatL);
                            }
                            sheet_Einnahmen.addCell(label);
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
                            sheet_Einnahmen.addCell(label);

                            zeile = zeile + 1;
                        }
                        zeile = zeile + 1;

                        label = new Label(0, zeile, "Gesamtsumme der sonstigen Einnahmen:", arial10formatBold);
                        sheet_Einnahmen.addCell(label);
                        //label = new Label(6, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)), arial10formatBold);
                        //sheet_Einnahmen.addCell(label);
                        number = new jxl.write.Number(6, zeile, Modulhelferlein.round2dec(Gesamtsumme), arial10formatBold);
                        sheet_Einnahmen.addCell(number);

                        // Aufbau des Tabellenblattes sheet_Ausgaben
                        label = new Label(0, 0, "Carola Hartmann Miles-Verlag", arial14formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(0, 1, "Eingehende (bezahlte) Rechnungen", arial14formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(0, 3, "abziehbare Vorsteuerbeträge aus Rechnungen von anderen Unternehmen " + strVon + " - " + strBis, arial14formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(0, 5, "Rechnungen mit UStr-Satz 19%", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(0, 7, "RechnungsNr", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(1, 7, "Datum", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(2, 7, "Lieferant", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(3, 7, "Preis in €", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(4, 7, "Bezahlt", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(2, 8, "Bezug", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(3, 8, "Brutto", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        zeile = 10;

                        Gesamtsumme = 0D;
                        if (Umfang) { // Alle
                            Sql = "SELECT * FROM TBL_AUSGABEN "
                                    + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + " OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                    + " AND (AUSGABEN_USTR = '19')"
                                    + " ORDER BY AUSGABEN_RECHDATUM";
                        } else { // bezahlte
                            Sql = "SELECT * FROM TBL_AUSGABEN "
                                    + " WHERE ((AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + " AND (AUSGABEN_USTR = '19'))"
                                    + " ORDER BY AUSGABEN_RECHDATUM";
                        }
                        resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                        while (resultBestellung.next()) { // geht durch alle zeilen
                            Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                            // Ausgabe RechNr, Kunde, Betrag
                            label = new Label(0, zeile, resultBestellung.getString("AUSGABEN_RECHNNR"), arial10formatL);
                            sheet_Ausgaben.addCell(label);
                            label = new Label(1, zeile, SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                            sheet_Ausgaben.addCell(label);
                            label = new Label(2, zeile, resultBestellung.getString("AUSGABEN_LIEFERANT"), arial10formatL);
                            sheet_Ausgaben.addCell(label);
                            number = new jxl.write.Number(3, zeile, Modulhelferlein.round2dec(Gesamtzeile), arial10formatR);
                            sheet_Ausgaben.addCell(number);

                            if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                                label = new Label(4, zeile, "----------", arial10formatL);
                                sheet_Ausgaben.addCell(label);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                                sheet_Ausgaben.addCell(label);
                            }
                            zeile = zeile + 1;

                            Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        } // geht durch alle zeilen
                        //Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                        label = new Label(0, zeile + 1, "Gesamtsumme der Ausgaben", arial10formatL);
                        sheet_Ausgaben.addCell(label);
                        number = new jxl.write.Number(3, zeile + 1, Modulhelferlein.round2dec(Gesamtsumme), arial10formatR);
                        sheet_Ausgaben.addCell(number);
                        label = new Label(0, zeile + 2, "UStr 19%", arial10formatL);
                        sheet_Ausgaben.addCell(label);
                        number = new jxl.write.Number(3, zeile + 2, Modulhelferlein.round2dec(Gesamtsumme - Gesamtsumme / 119 * 100), arial10formatR);
                        sheet_Ausgaben.addCell(number);

                        zeile = zeile + 5;

                        label = new Label(0, zeile, "Rechnungen mit UStr-Satz 7%", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(0, zeile + 2, "RechnungsNr", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(1, zeile + 2, "Datum", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(2, zeile + 2, "Lieferant", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(3, zeile + 2, "Preis in €", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(4, zeile + 2, "Bezahlt", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(2, zeile + 3, "Bezug", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(3, zeile + 3, "Brutto", arial10formatBold);
                        sheet_Ausgaben.addCell(label);

                        zeile = zeile + 5;
                        Gesamtsumme = 0D;
                        if (Umfang) { // Alle
                            Sql = "SELECT * FROM TBL_AUSGABEN "
                                    + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + " OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                    + " AND (AUSGABEN_USTR = '7')"
                                    + " ORDER BY AUSGABEN_RECHDATUM";
                        } else { // bezahlte
                            Sql = "SELECT * FROM TBL_AUSGABEN "
                                    + " WHERE ((AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + " AND (AUSGABEN_USTR = '7'))"
                                    + " ORDER BY AUSGABEN_RECHDATUM";
                        }
                        resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                        while (resultBestellung.next()) { // geht durch alle zeilen
                            Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                            // Ausgabe RechNr, Kunde, Betrag
                            label = new Label(0, zeile, resultBestellung.getString("AUSGABEN_RECHNNR"), arial10formatL);
                            sheet_Ausgaben.addCell(label);
                            label = new Label(1, zeile, SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                            sheet_Ausgaben.addCell(label);
                            label = new Label(2, zeile, resultBestellung.getString("AUSGABEN_LIEFERANT"), arial10formatL);
                            sheet_Ausgaben.addCell(label);
                            number = new jxl.write.Number(3, zeile, Modulhelferlein.round2dec(Gesamtzeile), arial10formatR);
                            sheet_Ausgaben.addCell(number);

                            if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                                label = new Label(4, zeile, "----------", arial10formatL);
                                sheet_Ausgaben.addCell(label);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                                sheet_Ausgaben.addCell(label);
                            }
                            zeile = zeile + 1;

                            Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        } // geht durch alle zeilen
                        //Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                        label = new Label(0, zeile + 1, "Gesamtsumme der Ausgaben", arial10formatL);
                        sheet_Ausgaben.addCell(label);
                        number = new jxl.write.Number(3, zeile + 1, Modulhelferlein.round2dec(Gesamtsumme), arial10formatR);
                        sheet_Ausgaben.addCell(number);
                        label = new Label(0, zeile + 2, "UStr 7%", arial10formatL);
                        sheet_Ausgaben.addCell(label);
                        number = new jxl.write.Number(3, zeile + 2, Modulhelferlein.round2dec(Gesamtsumme - Gesamtsumme / 107 * 100), arial10formatR);
                        sheet_Ausgaben.addCell(number);

                        zeile = zeile + 5;

                        label = new Label(0, zeile, "Rechnungen mit UStr-Satz 0%", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(0, zeile + 2, "RechnungsNr", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(1, zeile + 2, "Datum", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(2, zeile + 2, "Lieferant", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(3, zeile + 2, "Preis in €", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(4, zeile + 2, "Bezahlt", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(2, zeile + 3, "Bezug", arial10formatBold);
                        sheet_Ausgaben.addCell(label);
                        label = new Label(3, zeile + 3, "Brutto", arial10formatBold);
                        sheet_Ausgaben.addCell(label);

                        zeile = zeile + 5;
                        Gesamtsumme = 0D;
                        if (Umfang) { // Alle
                            Sql = "SELECT * FROM TBL_AUSGABEN "
                                    + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + " OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                    + " AND (AUSGABEN_USTR = '0')"
                                    + " ORDER BY AUSGABEN_RECHDATUM";
                        } else { // bezahlte
                            Sql = "SELECT * FROM TBL_AUSGABEN "
                                    + " WHERE ((AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                    + " AND (AUSGABEN_USTR = '0'))"
                                    + " ORDER BY AUSGABEN_RECHDATUM";
                        }
                        resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                        while (resultBestellung.next()) { // geht durch alle zeilen
                            Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                            // Ausgabe RechNr, Kunde, Betrag
                            label = new Label(0, zeile, resultBestellung.getString("AUSGABEN_RECHNNR"), arial10formatL);
                            sheet_Ausgaben.addCell(label);
                            label = new Label(1, zeile, SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                            sheet_Ausgaben.addCell(label);
                            label = new Label(2, zeile, resultBestellung.getString("AUSGABEN_LIEFERANT"), arial10formatL);
                            sheet_Ausgaben.addCell(label);
                            number = new jxl.write.Number(3, zeile, Modulhelferlein.round2dec(Gesamtzeile), arial10formatR);
                            sheet_Ausgaben.addCell(number);

                            if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                                label = new Label(4, zeile, "----------", arial10formatL);
                                sheet_Ausgaben.addCell(label);
                            } else {
                                label = new Label(4, zeile, SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                                sheet_Ausgaben.addCell(label);
                            }
                            zeile = zeile + 1;

                            Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        } // geht durch alle zeilen
                        //Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                        label = new Label(0, zeile + 1, "Gesamtsumme der Ausgaben", arial10formatL);
                        sheet_Ausgaben.addCell(label);
                        number = new jxl.write.Number(3, zeile + 1, Modulhelferlein.round2dec(Gesamtsumme), arial10formatR);
                        sheet_Ausgaben.addCell(number);
                        label = new Label(0, zeile + 2, "UStr 0%", arial10formatL);
                        sheet_Ausgaben.addCell(label);
                        number = new jxl.write.Number(3, zeile + 2, Modulhelferlein.round2dec(Gesamtsumme - Gesamtsumme / 100 * 100), arial10formatR);
                        sheet_Ausgaben.addCell(number);

                        // Fertig - alles schließen
                        try {// workbook write
                            workbook.write();
                        } catch (IOException e) {
                            Modulhelferlein.Fehlermeldung("XLS-Bericht Umsatz", "IO-Exception: ", e.getMessage());
                        } // workbook write

                        try { // try workbook close
                            workbook.close();
                        } catch (IOException e) {
                            Modulhelferlein.Fehlermeldung("XLS-Bericht Umsatz", "IO-Exception: ", e.getMessage());
                        } // try workbook close

                        try { // try XLS anzeigen
                            Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                        } catch (IOException exept) {
                            Modulhelferlein.Fehlermeldung("Bericht Umsatz", "Anzeige XLS-Export: Exception: ", exept.getMessage());
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
                        Modulhelferlein.Fehlermeldung("Bericht Umsätze: ", "XLS-Export: SQL-Exception: SQL-Anfrage nicht moeglich: ", exept.getMessage());

                    } // try Datenbankabfrage
                } // Datenbankverbindung steht

            } catch (WriteException e) {
                Modulhelferlein.Fehlermeldung("Bericht Umsätze: ", "XWrite-Exception: ", e.getMessage());
            } catch (ClassNotFoundException ex) {
                Modulhelferlein.Fehlermeldung("Bericht Umsätze: ", "ClassNotFound-Exception: ", ex.getMessage());
            } catch (SQLException ex) {
                Modulhelferlein.Fehlermeldung("Bericht Umsätze: ", "SQL-Exception: ", ex.getMessage());
            } // try Tabellenblätter schreiben

        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("Bericht Umsätze: ", "XLS-Bericht Umsätze: IO-Exception: ", e.getMessage());
        } // try Workbook create

        Modulhelferlein.Infomeldung("Bericht Umsätze: ", "Liste der Umsätze ist als XLS gespeichert!");
    } // XLS-Bericht erzeugen

    /**
     * Erzeugt eine Übersicht der Einnahmen im Zeitraum strVon bis strBis im
     * Format MS Word
     *
     * @param Umfang
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berUmsatzDOC(boolean Umfang, String strVon, String strBis) {
        Modulhelferlein.Infomeldung("noch nicht implementiert");
    }

    /**
     * Erzeugt eine Übersicht der Einnahmen im Zeitraum strVon bis strBis im
     * Format PDF
     *
     * @param Umfang
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berUmsatzPDF(boolean Umfang, String strVon, String strBis) {
        Double Gesamtsumme = 0D;
        Double Gesamtzeile = 0D;
        Double Gesamt19 = 0D;
        Double Gesamt7 = 0D;
        Double Buchpreis = 0D;

        Double einBuch = 0D;
        Double einKunde = 0D;
        Double einDruck = 0D;
        Double einVGW = 0D;
        Double einMarge = 0D;
        Double einSonst = 0D;

        Double ausPorto = 0D;
        Double ausMat = 0D;
        Double ausBest = 0D;
        Double ausPflicht = 0D;
        Double ausRez = 0D;
        Double ausHon = 0D;
        Double ausKonto = 0D;
        Double ausTel = 0D;
        Double ausBuero = 0D;
        Double ausErst = 0D;
        Double ausFahrt = 0D;
        Double ausSonst = 0D;

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

        String outputFileName;
        outputFileName = Modulhelferlein.pathBerichte + "\\Umsätze"
                + "\\Umsatz-"
                + strVon
                + "-"
                + strBis
                + "-"
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                + ".pdf";

        String Bemerkung = "";
        String strLand = "";

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
            Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 750, "Umsatz im Zeitraum " + strVon + " - " + strBis);
            Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 600, "Teil 1 - Einnahmen aus Buchbestellungen");
            Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 485, "Teil 2 - Weitere Einnahmen");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 460, "- Kundenbestellungen");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 440, "- Druckkostenzuschüsse");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 420, "- Vergütungen VG Wort");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 400, "- Margenabrechnungen BoD");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 380, "- Sonstige Einnahmen");
            Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 350, "Teil 3- Ausgaben");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 325, "- Portokosten");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 305, "- Büromaterial");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 285, "- Buchbestellungen");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 265, "- Honorarabrechnung");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 245, "- Kontoführung");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 225, "- Telefonkosten");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 205, "- Mieten für Büro- und Lagerräume");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 185, "- Bucherstellung");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 165, "- Fahrtkosten");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 145, "- Sonstiges");

            cos.close();
            page = new PDPage(A4);
            document.addPage(page);

            cos = new PDPageContentStream(document, page);

            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Bericht Umsatz", "ClassNotFoundException: Treiber nicht gefunden: ", exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Bericht Umsatz", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
            } // try Verbindung zur Datenbank über die JDBC-Brücke

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

                    Linie(cos,1,56, 735, 539, 735);
                    
                    SQLBuch = conn2.createStatement();
                    SQLKunde = conn2.createStatement();
                    SQLBestellung = conn2.createStatement();
                    SQLBestellungDetails = conn2.createStatement();

                    String Sql = "";
                    if (Umfang) { // Alle auch bezahlte 
                        Sql = "SELECT * FROM TBL_BESTELLUNG"
                                + " WHERE ((BESTELLUNG_RECHDAT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + " OR (BESTELLUNG_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + " ORDER BY BESTELLUNG_RECHNR";
                    } else { // bezahlte
                        Sql = "SELECT * FROM TBL_BESTELLUNG"
                                + " WHERE (BESTELLUNG_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + " ORDER BY BESTELLUNG_RECHNR";
                    }
                    resultBestellung = SQLBestellung.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultBestellung.next()) { // geht durch alle zeilen
                        // Kundendaten holen
                        String strKunde = "";
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
                                Bemerkung = "Geschenk";
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

                            Linie(cos,1,56, 735, 539, 735);
                            
                        } // if

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
//                        switch (resultBestellung.getInt("BESTELLUNG_LAND")) {
//                            case 20:
//                                Gesamtzeile = Gesamtzeile / 107 * 100;
//                                break;
//                            case 21:
//                                Gesamtzeile = Gesamtzeile / 107 * 100;
//                                break;
//                            case 10:
//                                if (!resultBestellung.getBoolean("BESTELLUNG_PRIVAT")) {
//                                    Gesamtzeile = Gesamtzeile / 107 * 100;
//                                }
//                                break;
//                            case 11:
//                                if (!resultBestellung.getBoolean("BESTELLUNG_PRIVAT")) {
//                                    Gesamtzeile = Gesamtzeile / 107 * 100;
//                                }
//                                break;
//                        }
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

                    Linie(cos,1,56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));
                    
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
                    einKunde = 0D;

                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kundenbestellungen BoD");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                    Linie(cos,1,56, 735, 539, 735);
                    
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

                            Linie(cos,1,56, 735, 539, 735);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;
                        einKunde = Gesamtzeile;

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

                    Linie(cos,1,56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));
                    
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

                    Linie(cos,1,56, 735, 539, 735);
                    
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

                            Linie(cos,1,56, 735, 539, 735);
                            
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

                    Linie(cos,1,56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), "Gesamtsumme der Einnahmen : ");
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    einMarge = Gesamtsumme;

                    cos.close();

                    // jetzt EinnahmenTyp Druckkostenzuschüsse -2- ausgeben
                    page = new PDPage(A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Druckkostenzuschüsse");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 220, 740, "Autor");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                    Linie(cos,1,56, 735, 539, 735);
                    
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
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Druckkostenzuschüsse");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 740, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 740, "RechnungsNr");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 220, 740, "Autor");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 400, 740, "Einnahmen");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 480, 740, "Bezahlt");

                            Linie(cos,1,56, 735, 539, 735);
                            
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
                        zeile = zeile + 1;
                    } // while

                    Linie(cos,1,56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));
                    
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

                    Linie(cos,1,56, 735, 539, 735);
                    
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

                            Linie(cos,1,56, 735, 539, 735);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("EINNAHMEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("EINNAHMEN_RECHDATUM")));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 440, 720 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 480, 720 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("EINNAHMEN_BEZAHLT")));

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while

                    Linie(cos,1,56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));
                    
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

                    Linie(cos,1,56, 735, 539, 735);
                    
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

                            Linie(cos,1,56, 735, 539, 735);
                            
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
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 720 - 15 * (zeile - 1), 539, 720 - 15 * (zeile - 1));
                    
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
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 485, Modulhelferlein.df.format(Modulhelferlein.round2dec(einKunde + einDruck + einVGW + einMarge + einSonst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 485, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 460, "- Kundenbestellungen");
                    AusgabeDB(cos, fontBold, 14, Color.BLACK, 440, 460, Modulhelferlein.df.format(Modulhelferlein.round2dec(einKunde)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 460, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 440, "- Druckkostenzuschüsse");
                    AusgabeDB(cos, fontBold, 14, Color.BLACK, 440, 440, Modulhelferlein.df.format(Modulhelferlein.round2dec(einDruck)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 440, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 420, "- Vergütungen VG Wort");
                    AusgabeDB(cos, fontBold, 14, Color.BLACK, 440, 420, Modulhelferlein.df.format(Modulhelferlein.round2dec(einVGW)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 420, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 400, "- Margenabrechnungen BoD");
                    AusgabeDB(cos, fontBold, 14, Color.BLACK, 440, 400, Modulhelferlein.df.format(Modulhelferlein.round2dec(einMarge)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 400, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 120, 380, "- Sonstige Einnahmen");
                    AusgabeDB(cos, fontBold, 14, Color.BLACK, 440, 380, Modulhelferlein.df.format(Modulhelferlein.round2dec(einSonst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 380, "Euro");

                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 300, "Gesamtsumme der Einnahmen : ");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 300, Modulhelferlein.df.format(Modulhelferlein.round2dec(einKunde + einBuch + einDruck + einVGW + einMarge + einSonst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 300, "Euro");

                    // close the content stream for page 
                    cos.close();

                    // jetzt die Ausgaben anfügen
                    page = new PDPage(A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 750, "Ausgaben im Zeitraum " + strVon + " - " + strBis);
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 600, "- Porto-Kosten");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 580, "- Büromaterial");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 560, "- Buchbestellungen bei BoD");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 540, "- Honorarabrechnung");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 520, "- Kontoführung");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 500, "- Telefonkosten");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 480, "- Büro- und Lagerräume inkl. Nebenkosten");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 460, "- Bucherstellung");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 440, "- Kfz-/Fahrtkosten");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 420, "- Sonstige");

                    // close the content stream for page 
                    cos.close();

                    // jetzt die Ausgaben anfügen
                    page = new PDPage(A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    // Neuer Ausgabenabschnitt Portokosten - Typ 0  
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Portokosten");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '0'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '0'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
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
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Portokosten");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Porto: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausPorto = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Büromaterial - Typ 1                    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Büromaterial");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Lieferant");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLBestellung = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '1'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '1'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
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
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Büromaterial");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Lieferant");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Büromaterial: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausMat = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Buchbestellungen - Typ 2  
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Buchbestellungen bei BoD");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLBestellung = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '2'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '2'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
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
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Buchbestellungen bei BoD");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Buchbestellungen bei BoD: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausBest = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Honorarabrechnung - Typ 3 
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Honorarabrechnung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Autor");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLBestellung = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '3'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '3'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
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
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Honorarabrechnung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Autor");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Honorarabrechnung: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausHon = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Kontoführung - Typ 4     
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kontoführung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Kontoauszug Nr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLBestellung = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '4'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '4'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
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
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kontoführung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Kontoauszug Nr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_RECHNNR"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Kontoführung: ");
                    ausKonto = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Telefonkosten - Typ 5       
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Telefonkosten");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLBestellung = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '5'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '5'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
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
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Telefonkosten");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Telefonkosten: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausTel = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Büro- und Lagerräume inkl. Nebenkosten - Typ 6    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Büro- und Lagerräume inkl. Nebenkosten");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                   
                    SQLBestellung = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '6'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '6'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
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
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Büro und Lagerräume inkl. Nebenkosten");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Büro- und Lagerräume inkl. Nebenkosten: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausBuero = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Bucherstellung - Typ 7                    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kosten für Bucherstellung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLBestellung = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '7'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '7'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
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
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kosten für Bucherstellung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Bucherstellung: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausErst = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Kfz/Fahrtkosten - Typ 8                    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLBestellung = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '8'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '8'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
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
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kfz-/Fahrtkosten");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Kfz-/Fahrtkosten: ");
                    ausFahrt = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Sonstige - Typ 9                    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLBestellung = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '9'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '9'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
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
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultBestellung.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultBestellung.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultBestellung.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultBestellung.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für sonstige Ausgaben: ");
                    ausSonst = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // jetzt Gesamtbilanz ausgeben
                    page = new PDPage(A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 750, "Ausgaben im Zeitraum " + strVon + " - " + strBis);
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 580, "- Porto-Kosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 580, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausPorto)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 580, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 560, "- Büromaterial");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 560, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausMat)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 560, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 540, "- Buchbestellungen bei BoD");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 540, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausBest)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 540, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 520, "- Honorarabrechnung");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 520, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausHon)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 520, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 500, "- Kontoführung");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 500, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausKonto)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 500, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 480, "- Telefonkosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 480, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausTel)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 480, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 460, "- Büro- und Lagerräume inkl. Nebenkosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 460, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausBuero)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 460, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 440, "- Bucherstellung");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 440, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausErst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 440, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 420, "- Kfz-/Fahrtkosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 420, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausFahrt)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 420, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 400, "- Sonstige");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 400, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausSonst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 400, "Euro");

                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 300, "Gesamtsumme der Ausgaben : ");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 300, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausPorto + ausMat + ausBest + ausPflicht + ausRez + ausHon + ausKonto + ausTel + ausBuero + ausErst + ausFahrt + ausSonst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 300, "Euro");

                    // close the content stream for page 
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);

                    document.close();

                    Modulhelferlein.Infomeldung(
                            "Liste der Umsätze ist als PDF gespeichert!");
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        Modulhelferlein.Fehlermeldung("Exception: ", exept.getMessage());
                    } // try PDF anzeigen

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
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("Bericht Umsätze: SQL-Exception: SQL-Anfrage nicht moeglich: ", exept.getMessage());
                } // try 
            }
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("Bericht Umsätze: IO-Exception: ", e.getMessage());
        } // try

    } // void
} // class

